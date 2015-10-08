/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.execjob;

import static com.persinity.haka.impl.actor.TestWorkerUtil.shutdownAndWaitTermination;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.typesafe.config.ConfigFactory;

/**
 * @author Ivan Dachev
 */
public class ExecJobWorkerTest {

    static class TestSupervisor extends UntypedActor {
        Object lastMsg;

        @Override
        public void onReceive(Object message) throws Exception {
            lastMsg = message;
        }
    }

    @Before
    public void setUp() {
        system = ActorSystem.create("haka", ConfigFactory.load("test-application-random-port.conf"));
    }

    @After
    public void tearDown() {
        shutdownAndWaitTermination(system);
        system = null;
    }

    @Test
    public void testSendMsgToPool() throws Exception {
        final String supervisorName = "supervisorName";
        final Props supervisorProps = Props.create(TestSupervisor.class);
        TestActorRef<TestSupervisor> supervisorRef = TestActorRef.create(system, supervisorProps, supervisorName);

        final Props props = ExecJobWorker.props(supervisorName);
        TestActorRef<ExecJobWorker> ref = TestActorRef.create(system, props, "testA");

        TestSupervisor supervisor = supervisorRef.underlyingActor();
        assertThat(supervisor.lastMsg, nullValue());

        ExecJobWorker worker = ref.underlyingActor();
        TestJob job = new TestJob(TestJobProducerNoChildren.class);
        NewMsg msg = new NewMsg(job);
        worker.sendMsgToPool(msg);

        assertThat((NewMsg) supervisor.lastMsg, is(msg));
    }

    private ActorSystem system;
}