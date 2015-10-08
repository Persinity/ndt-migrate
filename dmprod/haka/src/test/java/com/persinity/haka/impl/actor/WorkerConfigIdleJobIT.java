/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Testing of Worker idle job task set with smaller intervals in config.
 *
 * @author Ivan Dachev
 */
public class WorkerConfigIdleJobIT extends WorkerBaseIT {
    @BeforeClass
    public static void setUpClass() {
        Config config = ConfigFactory.parseString("haka.idlejob-period=500 ms")
                .withFallback(ConfigFactory.load("test-application.conf"));

        TestSharedSystem.system = ActorSystem.create("TestSystem", config);
    }

    @Test
    public void testIdleJob() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestIdleJobProducer.class);
            NewMsg newMsgOrigin = new NewMsg(job);
            elMediator.tell(newMsgOrigin, getRef());

            NewMsg newMsgChild1 = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild1.getJobId().parentId, is(job.getId().parentId));

            // resend new child msg to same mediator for handling
            elMediator.tell(newMsgChild1, testPool.testKit.getRef());

            ProcessedMsg processedMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(newMsgChild1.getJobId()));
            assertThat(((TestChildJob) processedMsg.getJob()).state, is(TestChildJobProducer.STATE_CHILD_DONE));

            // send processed ack for child should be ignored by mediator
            elMediator.tell(new ProcessedAckMsg(newMsgChild1.getJobId(), newMsgChild1.getMsgId()),
                    testPool.testKit.getRef());

            // send the processed msg to same mediator to handle parent progress
            elMediator.tell(processedMsg, testPool.testKit.getRef());

            // next expect to have new child 2 as the first one is done
            NewMsg newMsgChild2 = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild2.getJobId().parentId, is(job.getId().parentId));

            // resend next new child msg to same mediator for handling
            elMediator.tell(newMsgChild2, testPool.testKit.getRef());

            processedMsg = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(newMsgChild2.getJobId()));
            assertThat(((TestChildJob) processedMsg.getJob()).state, is(TestChildJobProducer.STATE_CHILD_DONE));

            // send processed ack for child should be ignored by mediator
            elMediator.tell(new ProcessedAckMsg(newMsgChild2.getJobId(), newMsgChild2.getMsgId()),
                    testPool.testKit.getRef());

            // send the processed msg to same mediator to handle parent progress
            elMediator.tell(processedMsg, testPool.testKit.getRef());

            processedMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(job.getId()));
            assertThat(((TestJob) processedMsg.getJob()).state, is(TestJob.STATE_DONE));

            // send processed ack
            elMediator.tell(new ProcessedAckMsg(job.getId(), newMsgOrigin.getMsgId()), getRef());

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
            testPool.testKit.expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

}