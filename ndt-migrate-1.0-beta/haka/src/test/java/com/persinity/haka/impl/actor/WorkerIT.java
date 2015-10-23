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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import akka.testkit.JavaTestKit;
import com.persinity.common.Id;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.persinity.haka.impl.actor.message.ProgressIgnoredMsg;
import com.persinity.haka.impl.actor.message.ProgressMsg;

/**
 * Testing of Worker sequences.
 * TODO This tests timeouts regularly during batch ITs. Enable when the problem is solved by renaming to WorkerIT.
 *
 * @author Ivan Dachev
 */
public class WorkerIT extends WorkerBaseIT {

    @Test
    public void testSingleJobNoChildren() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerNoChildren.class);
            elMediator.tell(new NewMsg(job), getRef());

            ProcessedMsg processedMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(job.getId()));
            assertThat(((TestJob) processedMsg.getJob()).state, is(TestJob.STATE_DONE));

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
            testPool.testKit.expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

    @Test
    public void testJobOneChild() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerOneChild.class);
            NewMsg newMsgOrigin = new NewMsg(job);
            elMediator.tell(newMsgOrigin, getRef());

            NewMsg newMsgChild = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild.getJobId().parentId, is(job.getId().parentId));

            // resend new child msg to same mediator for handling
            elMediator.tell(newMsgChild, testPool.testKit.getRef());

            // intermediate send processed ack on parent should be ignored we are still in processing
            elMediator.tell(new ProcessedAckMsg(job.getId(), newMsgOrigin.getMsgId()), getRef());

            ProcessedMsg processedMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(newMsgChild.getJobId()));
            assertThat(((TestChildJob) processedMsg.getJob()).state, is(TestChildJobProducer.STATE_CHILD_DONE));

            // send processed ack for child should be ignored by mediator
            elMediator.tell(new ProcessedAckMsg(newMsgChild.getJobId(), newMsgChild.getMsgId()),
                    testPool.testKit.getRef());

            // send the processed msg to same mediator to handle parent job done
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

    @Test
    public void testJobTwoChildrenAtOnce() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerTwoChildrenAtOnce.class);
            NewMsg newMsgOrigin = new NewMsg(job);
            elMediator.tell(newMsgOrigin, getRef());

            NewMsg newMsgChild1 = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild1.getJobId().parentId, is(job.getId().parentId));

            NewMsg newMsgChild2 = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild2.getJobId().parentId, is(job.getId().parentId));

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

    @Test
    public void testJobTwoChildrenOneByOne() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerTwoChildrenOneByOne.class);
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

    @Test
    public void testJobOneChildInvariants() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerOneChild.class);
            NewMsg newMsgOrigin = new NewMsg(job);
            elMediator.tell(newMsgOrigin, getRef());

            NewMsg newMsgChild = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), NewMsg.class);
            assertThat(newMsgChild.getJobId().parentId, is(job.getId().parentId));

            // resend new child msg to same mediator for handling
            elMediator.tell(newMsgChild, testPool.testKit.getRef());

            // expect processed for child
            ProcessedMsg processedMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(newMsgChild.getJobId()));
            assertThat(((TestChildJob) processedMsg.getJob()).state, is(TestChildJobProducer.STATE_CHILD_DONE));

            // send processed ack for child should be ignored by mediator
            elMediator.tell(new ProcessedAckMsg(newMsgChild.getJobId(), newMsgOrigin.getMsgId()),
                    testPool.testKit.getRef());

            // test invariant one send the progress msg for wrong children
            ProgressMsg progressMsgWrongChild = new ProgressMsg(new JobIdentity(job.getId()), newMsgOrigin.getMsgId());
            elMediator.tell(progressMsgWrongChild, testPool.testKit.getRef());

            ProgressIgnoredMsg progressIgnoredMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProgressIgnoredMsg.class);
            assertThat(progressIgnoredMsg.getJobId(), is(progressMsgWrongChild.getJobId()));

            // test invariant send progress msg for unknown parent job
            ProgressMsg progressMsgWrongParent = new ProgressMsg(new JobIdentity(), newMsgOrigin.getMsgId());
            elMediator.tell(progressMsgWrongParent, testPool.testKit.getRef());

            progressIgnoredMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProgressIgnoredMsg.class);
            assertThat(progressIgnoredMsg.getJobId(), is(progressMsgWrongParent.getJobId()));

            // send the processed msg to same mediator to handle parent job done
            elMediator.tell(processedMsg, testPool.testKit.getRef());

            // send again same processed msg should be ignored
            ProcessedMsg processedMsgWithAck = new ProcessedMsg(processedMsg.getJob(), processedMsg.getSessionId(),
                    true);
            elMediator.tell(processedMsgWithAck, testPool.testKit.getRef());

            ProcessedAckMsg processedAckMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedAckMsg.class);
            assertThat(processedAckMsg.getJobId(), is(newMsgChild.getJobId()));

            // send the processed msg for unknown parent job
            ProcessedMsg processedMsgUnknownParent = new ProcessedMsg(new TestJob(TestJobProducerOneChild.class),
                    Id.nextValue(), true);
            elMediator.tell(processedMsgUnknownParent, testPool.testKit.getRef());

            processedAckMsg = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedAckMsg.class);
            assertThat(processedAckMsg.getJobId(), is(processedMsgUnknownParent.getJobId()));

            // send the processed msg for unknown child job
            ProcessedMsg processedMsgUnknownChild = new ProcessedMsg(new TestChildJob(job, TestChildJobProducer.class),
                    Id.nextValue(), true);
            elMediator.tell(processedMsgUnknownChild, testPool.testKit.getRef());

            processedAckMsg = testPool.testKit.expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedAckMsg.class);
            assertThat(processedAckMsg.getJobId(), is(processedMsgUnknownChild.getJobId()));

            // resend the child progress msg to same mediator should be ignored as child is already done
            elMediator.tell(new ProgressMsg(newMsgChild.getJob().getId(), newMsgChild.getMsgId()),
                    testPool.testKit.getRef());

            progressIgnoredMsg = testPool.testKit
                    .expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProgressIgnoredMsg.class);
            assertThat(progressIgnoredMsg.getJobId(), is(newMsgChild.getJobId()));

            processedMsg = expectMsgClass(duration(TIMEOUT_WAIT_EXPECT_MSG), ProcessedMsg.class);
            assertThat(processedMsg.getJobId(), is(job.getId()));
            assertThat(((TestJob) processedMsg.getJob()).state, is(TestJob.STATE_DONE));

            // send progress ignored should cleanup its internal state
            elMediator.tell(new ProgressIgnoredMsg(job.getId(), newMsgOrigin.getMsgId()), getRef());

            // send progress ignored for unknown job should be ignored
            elMediator.tell(new ProgressIgnoredMsg(new JobIdentity(), Id.nextValue()), getRef());

            // send processed ack for invalid job should be ignored
            elMediator.tell(new ProcessedAckMsg(new JobIdentity(), Id.nextValue()), getRef());

            // send processed ack for parent job should be ignored as it already did the cleanup
            elMediator.tell(new ProcessedAckMsg(new JobIdentity(), Id.nextValue()), getRef());

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
            testPool.testKit.expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

    @Test
    public void testJobInvalidNullChildren() {
        new JavaTestKit(TestSharedSystem.system) {{
            TestJob job = new TestJob(TestJobProducerNullChildrenInvalid.class);
            elMediator.tell(new NewMsg(job), getRef());

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
            testPool.testKit.expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }

    @Test
    public void testJobUnhandledMsg() {
        new JavaTestKit(TestSharedSystem.system) {{
            elMediator.tell("unknown", getRef());

            expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
            testPool.testKit.expectNoMsg(duration(TIMEOUT_WAIT_EXPECT_NO_MSG));
        }};
    }
}