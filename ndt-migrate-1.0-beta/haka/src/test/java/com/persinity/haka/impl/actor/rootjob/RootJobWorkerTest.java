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
package com.persinity.haka.impl.actor.rootjob;

import static com.persinity.haka.impl.actor.TestWorkerUtil.shutdownAndWaitTermination;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;
import com.persinity.haka.impl.actor.WorkerState;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.typesafe.config.ConfigFactory;

/**
 * @author Ivan Dachev
 */
public class RootJobWorkerTest {

    static class TestSupervisor extends UntypedActor {
        Object lastMsg;

        @Override
        public void onReceive(Object message) throws Exception {
            lastMsg = message;
        }
    }

    @Before
    public void setUp() {
        system = ActorSystem.create("haka", ConfigFactory.load("test-root-job-worker.conf"));
    }

    @After
    public void tearDown() {
        shutdownAndWaitTermination(system);
        system = null;
    }

    @Test
    public void test() throws Exception {
        final String supervisorName = "supervisorName";
        final Props supervisorProps = Props.create(TestSupervisor.class);
        TestActorRef<TestSupervisor> supervisorRef = TestActorRef.create(system, supervisorProps, supervisorName);

        final Props props = RootJobWorker.props(supervisorName);
        TestActorRef<RootJobWorker> ref = TestActorRef.create(system, props, "testA");

        TestSupervisor supervisor = supervisorRef.underlyingActor();
        assertThat(supervisor.lastMsg, nullValue());

        RootJobWorker worker = ref.underlyingActor();
        worker.onReceiveCommand(RootJobClusterFire.Message.FIRE);

        assertEquals(((NewMsg) supervisor.lastMsg).getJob().getJobProducerClass(), TestRootJobProducer.class);

        supervisor.lastMsg = null;
        WorkerState state = worker.getState();
        state.getJobStates().clear();

        worker.resendRootJob();
        assertEquals(((NewMsg) supervisor.lastMsg).getJob().getJobProducerClass(), TestRootJobProducer.class);
    }

    private ActorSystem system;
}