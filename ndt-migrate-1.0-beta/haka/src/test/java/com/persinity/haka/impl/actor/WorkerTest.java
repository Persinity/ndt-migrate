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

import static com.persinity.common.ThreadUtil.sleepSeconds;
import static com.persinity.common.ThreadUtil.waitForCondition;
import static com.persinity.haka.impl.actor.TestWorkerUtil.shutdownAndWaitTermination;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.actor.RootActorPath;
import akka.dispatch.Envelope;
import akka.dispatch.Mailbox;
import akka.dispatch.MessageQueue;
import akka.testkit.TestActorRef;
import com.google.common.base.Function;
import com.persinity.common.Id;
import com.persinity.haka.Job;
import com.persinity.haka.impl.actor.handler.ProcessJobStateHandler;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.pool.WorkerPool;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.FiniteDuration;

/**
 * @author Ivan Dachev
 */
public class WorkerTest extends EasyMockSupport {

    @Before
    public void setUp() {
        system = ActorSystem.create("haka", ConfigFactory.load("test-application-random-port.conf"));

        final Address address = new Address("akka", "haka");
        path = new RootActorPath(address, "path");
    }

    @After
    public void tearDown() {
        shutdownAndWaitTermination(system);
        system = null;
    }

    @Test
    public void testInitialization() throws Exception {
        final WorkerPool pool = createMock(WorkerPool.class);

        final Capture<ActorRef> capturePoolAddActorRef = newCapture();
        pool.add(capture(capturePoolAddActorRef));
        expectLastCall();

        replayAll();

        final Props props = Worker.props(pool);
        final TestActorRef<Worker> ref = TestActorRef.create(system, props, "testInitialization");

        // waiting for ref/worker.receivedRecoveryCompleted()
        assertTrue(waitForCondition(new Function<Void, Boolean>() {
            @Override
            public Boolean apply(final Void aVoid) {
                return capturePoolAddActorRef.hasCaptured();
            }
        }, 5000));

        verifyAll();
    }

    @Test
    public void testSendNewChildJobMsg() throws Exception {
        final WorkerPool pool = createMock(WorkerPool.class);

        final Capture<ActorRef> capturePoolAddActorRef = newCapture();
        pool.add(capture(capturePoolAddActorRef));
        expectLastCall().anyTimes();

        final TestJob job = new TestJob(TestJobProducerNoChildren.class);
        final JobState childJobState = new JobState(job, Id.nextValue(), path);

        final Capture<Object> captureMsg = newCapture();
        final Capture<ActorRef> captureActorRef2 = newCapture();
        pool.tell(capture(captureMsg), capture(captureActorRef2));
        expectLastCall();

        replayAll();

        final Props props = Worker.props(pool);
        final TestActorRef<Worker> ref = TestActorRef.create(system, props, "testSendNewChildJobMsg");

        final Worker worker = ref.underlyingActor();

        worker.sendNewChildJobMsg(childJobState);

        verifyAll();

        final NewMsg msg = (NewMsg) captureMsg.getValue();
        assertThat(msg.getJob().getId(), is(job.getId()));

        assertThat(childJobState.getStatus(), is(JobState.Status.PROCESSING));
    }

    @Test
    public void testSendNewChildrenJobMsg() throws Exception {
        final WorkerPool pool = createMock(WorkerPool.class);

        final Capture<ActorRef> capturePoolAddActorRef = newCapture();
        pool.add(capture(capturePoolAddActorRef));
        expectLastCall().anyTimes();

        final TestJob job = new TestJob(TestJobProducerNoChildren.class);
        final JobState jobState = new JobState(job, Id.nextValue(), path);

        final TestJob childJob1 = new TestJob(job, TestJobProducerNoChildren.class);
        final TestJob childJob2 = new TestJob(job, TestJobProducerNoChildren.class);
        jobState.appendChildren(new HashSet<Job>(Arrays.asList(childJob1, childJob2)));

        final Capture<Object> captureMsg1 = newCapture();
        final Capture<ActorRef> captureActorRef1 = newCapture();
        pool.tell(capture(captureMsg1), capture(captureActorRef1));
        expectLastCall();

        final Capture<Object> captureMsg2 = newCapture();
        final Capture<ActorRef> captureActorRef2 = newCapture();
        pool.tell(capture(captureMsg2), capture(captureActorRef2));
        expectLastCall();

        replayAll();

        final Props props = Worker.props(pool);
        final TestActorRef<Worker> ref = TestActorRef.create(system, props, "testSendNewChildrenJobMsg");

        final Worker worker = ref.underlyingActor();

        worker.sendNewChildrenJobMsg(jobState);

        verifyAll();

        final NewMsg msg1 = (NewMsg) captureMsg1.getValue();
        assertThat(msg1.getJob().getId(), anyOf(is(childJob1.getId()), is(childJob2.getId())));
        final NewMsg msg2 = (NewMsg) captureMsg2.getValue();
        assertThat(msg2.getJob().getId(), anyOf(is(childJob1.getId()), is(childJob2.getId())));
    }

    @Test
    public void testResendMsgNew() throws Exception {
        final WorkerPool pool = createMock(WorkerPool.class);

        final Capture<ActorRef> capturePoolAddActorRef = newCapture();
        pool.add(capture(capturePoolAddActorRef));
        expectLastCall().anyTimes();

        final TestJob job = new TestJob(TestJobProducerNoChildren.class);
        final NewMsg msg = new NewMsg(job);

        final TestActorRefMock sender = new TestActorRefMock(path);

        final Capture<NewMsg> captureMsg = newCapture();
        final Capture<ActorRef> captureSender = newCapture();
        final Capture<FiniteDuration> captureDelay = newCapture();
        pool.schedule(capture(captureMsg), capture(captureSender), capture(captureDelay));
        expectLastCall();

        replayAll();

        final Props props = Worker.props(pool);
        final TestActorRef<Worker> ref = TestActorRef.create(system, props, "testResendMsgNew");

        final Worker worker = ref.underlyingActor();

        worker.resendMsgNew(msg, sender);

        verifyAll();

        final NewMsg msg1 = captureMsg.getValue();
        assertThat(msg1, is(msg));

        final ActorRef sender1 = captureSender.getValue();
        assertThat(sender1, is((ActorRef) sender));

        final FiniteDuration delay1 = captureDelay.getValue();
        assertTrue(delay1.toSeconds() > 0);
    }

    @Test
    public void testSendProcessJobStateMsg() throws Exception {
        final WorkerPool pool = createMock(WorkerPool.class);

        final Capture<ActorRef> capturePoolAddActorRef = newCapture();
        pool.add(capture(capturePoolAddActorRef));
        expectLastCall().anyTimes();

        final TestJob job = new TestJob(TestJobProducerNoChildren.class);
        final JobState jobState = new JobState(job, Id.nextValue(), path);

        replayAll();

        final Props props = Worker.props(pool);
        final TestActorRef<Worker> ref = TestActorRef.create(system, props, "testSendProcessJobStateMsg");

        final Worker worker = ref.underlyingActor();

        worker.getState().addJobState(jobState);

        worker.sendProcessJobStateMsg(jobState.getJob().getId());

        sleepSeconds(3);
        assertThat(ref.underlying().hasMessages(), is(true));

        final Mailbox mailbox = ref.underlying().mailbox();
        final MessageQueue queue = mailbox.messageQueue();
        final Envelope msgEnvelop = queue.dequeue();

        final ProcessJobStateHandler.ProcessJobStateMsg msg = (ProcessJobStateHandler.ProcessJobStateMsg) msgEnvelop
                .message();
        assertThat(msg.getJobStateId(), is(job.getId()));

        verifyAll();
    }

    private ActorSystem system;
    private ActorPath path;
}