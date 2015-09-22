/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;
import com.persinity.haka.JobIdentity;
import com.persinity.haka.impl.actor.handler.NewMsgHandler;
import com.persinity.haka.impl.actor.handler.ProcessJobStateHandler;
import com.persinity.haka.impl.actor.handler.ProcessedAckMsgHandler;
import com.persinity.haka.impl.actor.handler.ProcessedMsgHandler;
import com.persinity.haka.impl.actor.handler.ProgressIgnoredMsgHandler;
import com.persinity.haka.impl.actor.handler.ProgressMsgHandler;
import com.persinity.haka.impl.actor.handler.WorkerIdleJobHandler;
import com.persinity.haka.impl.actor.handler.WorkerWatchdogHandler;
import com.persinity.haka.impl.actor.message.NewMsg;
import com.persinity.haka.impl.actor.pool.WorkerPool;
import scala.concurrent.duration.FiniteDuration;

/**
 * Worker implementation.
 *
 * @author Ivan Dachev
 */
public class Worker extends WorkerBase {
    protected Worker(WorkerPool pool) {
        this.pool = pool;

        addMsgHandler(new NewMsgHandler(this));
        addMsgHandler(new ProgressMsgHandler(this));
        addMsgHandler(new ProgressIgnoredMsgHandler(this));
        addMsgHandler(new ProcessedMsgHandler(this));
        addMsgHandler(new ProcessedAckMsgHandler(this));

        setWatchdogHandler(new WorkerWatchdogHandler(this));
        setIdleJobHandler(new WorkerIdleJobHandler(this));

        processJobStateHandler = new ProcessJobStateHandler(this);
    }

    public static Props props(final WorkerPool pool) {
        assert pool != null;

        return Props.create(new Creator<Worker>() {
            private static final long serialVersionUID = 8512123129521502262L;

            @Override
            public Worker create() throws Exception {
                return new Worker(pool);
            }
        });
    }

    @Override
    protected void handleCustomMsg(Object msg) {
        if (msg instanceof ProcessJobStateHandler.ProcessJobStateMsg) {
            processJobStateHandler.processJobState(((ProcessJobStateHandler.ProcessJobStateMsg) msg).getJobStateId());
        } else {
            super.handleCustomMsg(msg);
        }
    }

    @Override
    protected void receivedRecoveryCompleted() {
        super.receivedRecoveryCompleted();

        pool.add(getSelf());
    }

    public void sendNewChildJobMsg(JobState childJobState) {
        assert childJobState.getStatus() == JobState.Status.NEW;

        NewMsg msg = new NewMsg(childJobState.getJob());
        childJobState.setSessionId(msg.getMsgId());

        getLog().debug("Send new child %s to %s", msg, pool);

        pool.tell(msg, getSelf());

        childJobState.setStatus(JobState.Status.PROCESSING);
    }

    public void sendNewChildrenJobMsg(JobState jobState) {
        Collection<JobState> children = jobState.getChildren().values();
        for (JobState childJobState : children) {
            if (childJobState.getStatus() == JobState.Status.NEW) {
                sendNewChildJobMsg(childJobState);
            }
        }
    }

    public void resendMsgNew(NewMsg msg, ActorRef sender) {
        long seconds = getSettings().getMsgResendDelay().toSeconds();
        seconds += (long) (Math.random() * seconds);

        getLog().debug("Schedule for %d seconds to resend %s to %s", seconds, msg, pool);

        pool.schedule(msg, sender, FiniteDuration.create(seconds, TimeUnit.SECONDS));
    }

    public void sendProcessJobStateMsg(JobIdentity jobStateId) {
        assert jobStateId != null;
        ProcessJobStateHandler.ProcessJobStateMsg msg = new ProcessJobStateHandler.ProcessJobStateMsg(jobStateId);
        getLog().debug("Send ProcessJobStateMsg for jobStateId: %s", jobStateId.toShortString());
        getSelf().tell(msg, getSelf());
    }

    private final WorkerPool pool;

    private final ProcessJobStateHandler processJobStateHandler;
}
