/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import static akka.testkit.JavaTestKit.duration;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.persinity.haka.impl.actor.message.ProgressMsg;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class TestWorkerUtil {

    /**
     * Waits for ProgressMsg until a ProcessedMsg is received and then return it.
     *
     * @param thiz
     *         instance of JavaTestKit to wait on for message
     * @param expectedJobId
     *         Job identity to expect on each ProgressMsg/ProcessedMsg
     * @return ProcessedMsg
     */
    public static ProcessedMsg waitForProcessedMsg(JavaTestKit thiz, JobIdentity expectedJobId) {
        return waitForProcessedMsg(thiz, expectedJobId, duration(TIMEOUT_WAIT_PROCESSED_MSG));
    }

    /*
     * Same as {@link #waitForProcessedMsg} allowing to set timeout for waiting.
     */
    public static ProcessedMsg waitForProcessedMsg(JavaTestKit thiz, JobIdentity expectedJobId,
            FiniteDuration timeout) {
        ProcessedMsg[] processedMsg = new ProcessedMsg[1];
        Deadline deadline = timeout.fromNow();
        while (deadline.hasTimeLeft()) {
            Object msg = thiz.expectMsgAnyClassOf(duration(TIMEOUT_WAIT_EXPECT_PROCESSED_MSG), ProcessedMsg.class,
                    ProgressMsg.class);
            if (msg instanceof ProgressMsg) {
                ProgressMsg progressMsg = (ProgressMsg) msg;
                assertThat(progressMsg.getJobId(), is(expectedJobId));
            } else if (msg instanceof ProcessedMsg) {
                processedMsg[0] = (ProcessedMsg) msg;
                assertThat(processedMsg[0].getJobId(), is(expectedJobId));
                break;
            } else {
                fail("Unexpected msg: " + msg);
                break;
            }
        }

        if (processedMsg[0] == null) {
            fail(String.format("Timeout waiting for ProcessedMsg on Job: %s", expectedJobId));
        }

        return processedMsg[0];
    }

    /**
     * @param system
     *         to shutdown and wait for termination
     */
    public static void shutdownAndWaitTermination(final ActorSystem system) {
        if (system != null && !system.isTerminated()) {
            system.shutdown();
            system.awaitTermination(Duration.create(5, TimeUnit.SECONDS));
        }
    }

    static final String TIMEOUT_WAIT_PROCESSED_MSG = "18 seconds";
    static final String TIMEOUT_WAIT_EXPECT_PROCESSED_MSG = "6 seconds";
}
