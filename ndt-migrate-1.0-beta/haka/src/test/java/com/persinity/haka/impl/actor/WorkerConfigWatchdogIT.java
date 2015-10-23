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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.persinity.haka.impl.actor.message.ProgressMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Testing of Worker watchdog set with smaller intervals in config.
 *
 * @author Ivan Dachev
 */
public class WorkerConfigWatchdogIT extends WorkerBaseIT {
    static final int STATUS_UPDATE_TIMEOUT = 4;

    @BeforeClass
    public static void setUpClass() {
        Config config = ConfigFactory.parseString(
                String.format("haka.watchdog-period=2 seconds\nhaka.status-update-timeout = %d seconds",
                        STATUS_UPDATE_TIMEOUT)).withFallback(ConfigFactory.load("test-application.conf"));

        TestSharedSystem.system = ActorSystem.create("TestSystem", config);
    }

    @Test
    public void testWatchdogResendChildJob() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerOneChild.class);
            NewMsg newMsgOrigin = new NewMsg(job);
            elMediator.tell(newMsgOrigin, getRef());

            NewMsg newMsgChild = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild.getJobId().parentId, is(job.getId().parentId));

            // progress message expected from each watchdog run

            ProgressMsg progressMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProgressMsg.class);
            assertThat(progressMsg.getJobId(), is(job.getId()));

            newMsgChild = testPool.testKit
                    .expectMsgClass(duration(String.format("%d seconds", STATUS_UPDATE_TIMEOUT * 2)), NewMsg.class);
            assertThat(newMsgChild.getJobId().parentId, is(job.getId().parentId));

            // resend new child msg to same mediator for handling
            elMediator.tell(newMsgChild, testPool.testKit.getRef());

            ProcessedMsg processedMsg = waitForProcessedMsg(testPool.testKit, newMsgChild.getJobId());
            assertThat(((TestChildJob) processedMsg.getJob()).state, is(TestChildJobProducer.STATE_CHILD_DONE));

            elMediator.tell(processedMsg, testPool.testKit.getRef());

            processedMsg = waitForProcessedMsg(this, job.getId());

            assertThat(((TestJob) processedMsg.getJob()).state, is(TestJob.STATE_DONE));

            // wait watchdog to trigger resend msg processed

            processedMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_2X_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(job.getId()));
            assertThat(((TestJob) processedMsg.getJob()).state, is(TestJob.STATE_DONE));

            // send processed ack
            elMediator.tell(new ProcessedAckMsg(job.getId(), newMsgOrigin.getMsgId()), getRef());

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
            testPool.testKit.expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

}