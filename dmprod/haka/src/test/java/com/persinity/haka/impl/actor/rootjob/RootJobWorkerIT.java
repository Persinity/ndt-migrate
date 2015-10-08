/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import static com.persinity.haka.impl.actor.WorkerBaseIT.TIMEOUT_WAIT_EXPECT_2X_MSG;
import static com.persinity.haka.impl.actor.WorkerBaseIT.TIMEOUT_WAIT_EXPECT_MSG;
import static com.persinity.haka.impl.actor.WorkerBaseIT.TIMEOUT_WAIT_EXPECT_NO_MSG;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.testkit.JavaTestKit;
import com.persinity.common.Id;
import com.persinity.common.WaitForCondition;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestSharedSystem;
import com.persinity.haka.impl.actor.WorkersSupervisor;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.persinity.haka.impl.actor.message.ProgressIgnoredMsg;
import com.persinity.haka.impl.actor.message.ProgressMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class RootJobWorkerIT {

    @BeforeClass
    public static void setUpClass() {
        Config config = ConfigFactory.parseString("haka.root-job-worker.min-cluster-up-members-before-fire = 0\n" +
                "haka.root-job-worker.cluster-up-members-check-period = 1 s\n" +
                "haka.workers = 1").withFallback(ConfigFactory.load("test-root-job-worker.conf"));

        TestSharedSystem.system = ActorSystem.create("TestSystem", config);
    }

    @AfterClass
    public static void tearDownClass() {
        if (TestSharedSystem.system != null) {
            JavaTestKit.shutdownActorSystem(TestSharedSystem.system);
            TestSharedSystem.system = null;
        }
    }

    @Before
    public void setUp() {
        final String nodeId = Id.nextValue().toStringShort();

        String poolSupervisorName = "WorkersSupervisor-" + nodeId;
        TestSharedSystem.system.actorOf(WorkersSupervisor.props(nodeId), poolSupervisorName);

        rootJobWorker = TestSharedSystem.system
                .actorOf(RootJobWorker.props(poolSupervisorName), "RootJobWorker-" + nodeId);

        log.info("Created: {}", rootJobWorker.path());
    }

    @After
    public void tearDown() {
        rootJobWorker.tell(PoisonPill.getInstance(), ActorRef.noSender());
        rootJobWorker = null;
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
        TestRootJobProducer.doDelayMs = 0;
    }

    @Test
    public void test() throws Exception {
        new JavaTestKit(TestSharedSystem.system) {{
            TestRootJobProducer.doDelayMs = 3000;

            ProgressMsg progressMsg = new ProgressMsg(new JobIdentity(), Id.nextValue());
            rootJobWorker.tell(progressMsg, getRef());

            ProgressIgnoredMsg progressIgnoredMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG),
                    ProgressIgnoredMsg.class);
            assertThat(progressIgnoredMsg.getJobId(), is(progressMsg.getJobId()));

            ProcessedMsg processedMsg = new ProcessedMsg(new TestRootJob(), Id.nextValue(), true);
            rootJobWorker.tell(processedMsg, getRef());

            ProcessedAckMsg processedAckMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedAckMsg.class);
            assertThat(processedAckMsg.getJobId(), is(processedMsg.getJobId()));

            waitTestRootJobProducerSavedJobState(TestRootJobProducer.STATE_JOB_CREATED,
                    duration(TIMEOUT_WAIT_EXPECT_2X_MSG));

            processedMsg = new ProcessedMsg(new TestRootJobWatchdog(), Id.nextValue(), true);
            rootJobWorker.tell(processedMsg, getRef());

            processedAckMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedAckMsg.class);
            assertThat(processedAckMsg.getJobId(), is(processedMsg.getJobId()));

            TestRootJobProducer.doDelayMs = 0;

            waitTestRootJobProducerSavedJobState(TestRootJob.STATE_DONE, duration(TIMEOUT_WAIT_EXPECT_2X_MSG));

            assertThat(TestRootJobProducer.saveChildJob, notNullValue());
            assertThat(TestRootJobProducer.saveChildJob.state, is(TestJob.STATE_DONE));

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

    private void waitTestRootJobProducerSavedJobState(final String expectedState, final FiniteDuration duration) {
        new WaitForCondition(duration.toMillis(), "Timeout to wait for state: " + expectedState) {
            @Override
            public boolean condition() {
                String state = TestRootJobProducer.saveRootJob != null ? TestRootJobProducer.saveRootJob.state : "";
                return state.equals(expectedState);
            }
        }.waitOrTimeout();
    }

    final LoggingAdapter log = Logging.getLogger(TestSharedSystem.system, this);

    Cancellable ticker = null;
    ActorRef rootJobWorker;
}