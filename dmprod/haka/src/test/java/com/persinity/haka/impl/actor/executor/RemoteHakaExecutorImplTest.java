/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor.executor;

import static com.persinity.haka.impl.actor.TestWorkerUtil.shutdownAndWaitTermination;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.persinity.haka.HakaExecutor;
import com.persinity.haka.HakaExecutorFactory;
import com.persinity.haka.HakaExecutorFactoryProvider;
import com.persinity.haka.impl.actor.TestJob;
import com.persinity.haka.impl.actor.TestJobProducerNoChildren;
import com.persinity.haka.impl.actor.execjob.ExecJobWorker;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Ivan Dachev
 */
public class RemoteHakaExecutorImplTest {
    static class TestExecJobWorker extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            lastMsg = (NewMsg) message;
            TestJob job = (TestJob) lastMsg.getJob();
            job.state = TestJob.STATE_DONE;
            getSender().tell(new ProcessedMsg(job, lastMsg.getMsgId(), false), getSelf());
        }

        static NewMsg lastMsg;
    }

    @Before
    public void setUp() {
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.hostname=127.0.0.1\nakka.remote.netty.tcp.port=1234")
                .withFallback(ConfigFactory.load("test-application.conf"));
        system = ActorSystem.create("haka", config);

        system.actorOf(Props.create(TestExecJobWorker.class), ExecJobWorker.class.getSimpleName() + "-1");
    }

    @After
    public void tearDown() throws InterruptedException {
        shutdownAndWaitTermination(system);
        system = null;
    }

    @Test
    public void test() throws Exception {
        HakaExecutorFactory factory = HakaExecutorFactoryProvider.getFactory();

        ((HakaExecutorFactoryImpl) factory).setRemoteHakaConfig("haka-client.conf");

        HakaExecutor executor = factory.newRemoteInstance("127.0.0.1", 1234);

        TestJob job = new TestJob(TestJobProducerNoChildren.class);
        Future<TestJob> future = executor.executeJob(job, 1000);

        job = future.get();

        assertThat(job.state, is(TestJob.STATE_DONE));

        executor.shutdown();
    }

    private ActorSystem system;
}