/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persinity.haka.impl.actor;

import static com.persinity.haka.impl.actor.TestWorkerUtil.waitForProcessedMsg;
import static com.persinity.haka.impl.actor.WorkerBaseIT.TIMEOUT_WAIT_EXPECT_NO_MSG;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class WorkerRecoverFailureIT {
    @Before
    public void setUp() {
        int persistenceId = (int) (System.currentTimeMillis() % 5);
        Config config = ConfigFactory.parseString(String.format("haka.watchdog-period=1 seconds\n" +
                "haka.status-update-timeout = 3 seconds\n" +
                "haka.workers = 3\n" +
                "haka.enable-persistence = true\n" +
                "akka.persistence.journal.leveldb.dir=./target/journal-%d\n" +
                "akka.persistence.snapshot-store.local.dir=./target/snapshots-%d", persistenceId, persistenceId))
                .withFallback(ConfigFactory.load("test-application.conf"));
        TestSharedSystem.system = ActorSystem.create("TestSystem", config);

        final String nodeId = Id.nextValue().toStringShort();

        String poolSupervisorName = "WorkersSupervisor-" + nodeId;
        supervisor = TestSharedSystem.system.actorOf(WorkersSupervisor.props(nodeId), poolSupervisorName);

        WorkersSupervisor.waitReady(supervisor, FiniteDuration.create(10, TimeUnit.SECONDS));
    }

    @After
    public void tearDown() {
        TestJobProducerOneChild.setDefaultChildClasses();

        if (TestSharedSystem.system != null) {
            JavaTestKit.shutdownActorSystem(TestSharedSystem.system);
            TestSharedSystem.system = null;
        }
    }

    @Test
    public void test_RecoverChildJobProducerFailure() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJobProducerOneChild.CHILD_JOB_PRODUCER_CLASS = TestJobProducerRecoverFailure.class;

            final TestJob job = new TestJob(TestJobProducerOneChild.class);
            supervisor.tell(new NewMsg(job), getRef());

            ProcessedMsg processedMsg = waitForProcessedMsg(this, job.getId());

            assertThat(((TestJob) processedMsg.getJob()).state, is(TestJob.STATE_DONE));

            assertThat(((TestChildJob) TestJobProducerOneChild.lastChildJob).state, is(TestJob.STATE_DONE));

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

    @Test
    public void test_RecoverParentJobProducerFailure() {
        new JavaTestKit(TestSharedSystem.system) {{
            final TestJob job = new TestJob(TestJobProducerRecoverFailureOnSecondChild.class);
            supervisor.tell(new NewMsg(job), getRef());

            ProcessedMsg processedMsg = waitForProcessedMsg(this, job.getId());

            TestJob testJob = (TestJob) processedMsg.getJob();
            assertThat(testJob.state, is(TestJob.STATE_DONE));

            assertThat(testJob.children.size(), is(2));
            for (TestChildJob childJob : testJob.children.values()) {
                assertThat(childJob.state, is(TestChildJobProducer.STATE_CHILD_DONE));
            }

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

    private ActorRef supervisor;
}
