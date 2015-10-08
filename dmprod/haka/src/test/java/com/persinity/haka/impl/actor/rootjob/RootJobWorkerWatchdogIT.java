/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.rootjob;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.persinity.common.WaitForCondition;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestSharedSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class RootJobWorkerWatchdogIT extends RootJobWorkerIT {

    @BeforeClass
    public static void setUpClass() {
        int persistenceId = (int) (System.currentTimeMillis() % 5);
        Config config = ConfigFactory.parseString(String.format("haka.watchdog-period=2 seconds\n" +
                "haka.status-update-timeout = 5 seconds\n" +
                "haka.root-job-worker.min-cluster-up-members-before-fire = 0\n" +
                "haka.root-job-worker.cluster-up-members-check-period = 1 s\n" +
                "haka.workers = 3\n" +
                "akka.persistence.journal.leveldb.dir=./target/journal-%d\n" +
                "akka.persistence.snapshot-store.local.dir=./target/snapshots-%d", persistenceId, persistenceId))
                .withFallback(ConfigFactory.load("test-root-job-worker-watchdog.conf"));
        TestSharedSystem.system = ActorSystem.create("TestSystem", config);
    }

    @Test
    public void test() {
        new JavaTestKit(TestSharedSystem.system) {{
            // here the RootJobWorker will automatically create the TestRootJobWatchdog
            // and will start processing it we verify that the watchdog was triggered
            // as we blocked the first Job execution and complete it on the second call
            // check TestRootJobProducerWatchdog.process(...) for more details

            waitTestRootJobProducerWatchdogSavedJobState(TestRootJob.STATE_DONE, duration(TIMEOUT_WAIT_STATE));

            assertThat(TestRootJobProducerWatchdog.saveChildJob, notNullValue());
            assertThat(TestRootJobProducerWatchdog.saveChildJob.state, is(TestJob.STATE_DONE));
            assertThat(TestRootJobWatchdog.resendCount, is(2));
        }};
    }

    private void waitTestRootJobProducerWatchdogSavedJobState(final String expectedState,
            final FiniteDuration duration) {
        new WaitForCondition(duration.toMillis(), "Timeout to wait for state: " + expectedState) {
            @Override
            public boolean condition() {
                String state = TestRootJobProducerWatchdog.saveRootJob != null ?
                        TestRootJobProducerWatchdog.saveRootJob.state :
                        "";
                return state.equals(expectedState);
            }
        }.waitOrTimeout();
    }

    static final String TIMEOUT_WAIT_STATE = "15 seconds";
}