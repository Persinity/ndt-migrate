/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import java.util.HashMap;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.japi.Procedure;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotFailure;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;
import com.persinity.common.Id;
import com.persinity.haka.impl.actor.handler.IdleJobHandler;
import com.persinity.haka.impl.actor.handler.MsgHandler;
import com.persinity.haka.impl.actor.handler.WatchdogHandler;
import com.persinity.haka.impl.actor.message.Msg;
import com.persinity.haka.impl.actor.message.ProcessedAckMsg;
import com.persinity.haka.impl.actor.message.ProcessedMsg;
import com.persinity.haka.impl.actor.message.ProgressIgnoredMsg;
import com.persinity.haka.impl.actor.message.ProgressMsg;

/**
 * @author Ivan Dachev
 */
public abstract class WorkerBase extends UntypedPersistentActor implements ContextLoggingProvider {
    protected WorkerBase() {
        msgHandlers = new HashMap<>();
        state = new WorkerState();
    }

    @Override
    public void postStop() {
        super.postStop();

        stopWatchdogTask();
        stopIdleJobTask();
    }

    @Override
    public String persistenceId() {
        return getSelf().path().name();
    }

    @Override
    public void onReceiveRecover(Object message) throws Exception {
        if (message instanceof SnapshotOffer) {
            receivedSnapshotOffer(((SnapshotOffer) message).snapshot());
        } else if (message instanceof RecoveryCompleted) {
            receivedRecoveryCompleted();
        } else {
            unhandled(message);
        }
    }

    protected void receivedSnapshotOffer(Object snapshot) {
        if (settings.enablePersistence()) {
            state = (WorkerState) snapshot;

            resetStatusUpdateTimes(state);

            log.debug("Recover state: %s", state);
        }
    }

    protected void receivedRecoveryCompleted() {
        log.debug("Recovery completed, starting: %s", getSelf());

        if (watchdogHandler != null) {
            startWatchdogTask();
        }

        if (idleJobHandler != null) {
            startIdleJobTask();
        }
    }

    @Override
    public void onReceiveCommand(Object msg) throws Exception {
        log.debug("Recv %s", msg);

        if (msg == InternalMessage.WATCHDOG) {
            if (watchdogHandler != null) {
                watchdogHandler.handleWatchdog();
            }
        } else if (msg == InternalMessage.IDLEJOB) {
            if (idleJobHandler != null) {
                idleJobHandler.handleIdleJob();
            }
        } else if (msg == PingPong.PING) {
            log.debug("Send %s to %s", PingPong.PONG, getSender());
            getSender().tell(PingPong.PONG, getSelf());
        } else if (msg instanceof SaveSnapshotSuccess) {
            handleSaveSnapshotSuccess((SaveSnapshotSuccess) msg);
        } else if (msg instanceof SaveSnapshotFailure) {
            handleSaveSnapshotFailure((SaveSnapshotFailure) msg);
        } else if (msg instanceof Msg) {
            MsgHandler<Msg> handler = getHandler((Msg) msg);
            if (handler != null) {
                handler.handleMsg((Msg) msg, getSender());
            } else {
                log.warning("No handler for msg: %s", msg);
                unhandled(msg);
            }
        } else {
            handleCustomMsg(msg);
        }

        log.debug("Done %s", msg);
    }

    protected void handleCustomMsg(Object msg) {
        unhandled(msg);
    }

    public void doSnapshot() {
        if (settings.enablePersistence()) {
            // need to do at least one persist call in order to save correct snapshot order
            // as we are using only the snapshot capabilities of UntypedPersistentActor
            persist("", new Procedure<String>() {
                @Override
                public void apply(String param) throws Exception {
                }
            });

            WorkerState clone = state.clone();

            log.debug("Snapshot state: %s", clone);

            saveSnapshot(clone);
        }
    }

    public void sendMsgProgress(JobState jobState) {
        ActorPath senderPath = jobState.getSenderPath();
        assert senderPath != null;

        ActorSelection sender = getContext().system().actorSelection(senderPath);

        sendMsg(sender, new ProgressMsg(jobState.getJob().getId(), jobState.getSessionId()), getSelf());
    }

    public void sendMsgProcessed(JobState jobState, boolean needAck) {
        ActorPath senderPath = jobState.getSenderPath();
        assert senderPath != null;

        ActorSelection sender = getContext().system().actorSelection(senderPath);

        sendMsg(sender, new ProcessedMsg(jobState.getJob(), jobState.getSessionId(), needAck), getSelf());
    }

    public void sendMsgProgressIgnored(ProgressMsg msg, ActorRef sender) {
        sendMsg(sender, new ProgressIgnoredMsg(msg.getJobId(), msg.getSessionId()), getSelf());
    }

    public void sendMsgProcessedAck(ProcessedMsg msg, ActorRef sender) {
        if (msg.needAck()) {
            sendMsg(sender, new ProcessedAckMsg(msg.getJobId(), msg.getSessionId()), getSelf());
        }
    }

    public void sendMsg(ActorRef dst, Msg msg, ActorRef src) {
        log.debug("Send %s to %s", msg, dst.path());
        dst.tell(msg, src);
    }

    public void sendMsg(String path, Msg msg, ActorRef src) {
        ActorSelection actorSelection = getContext().system().actorSelection(path);
        sendMsg(actorSelection, msg, src);
    }

    public void sendMsg(ActorSelection dst, Msg msg, ActorRef src) {
        log.debug("Send %s to %s", msg, dst.path());
        dst.tell(msg, src);
    }

    public void cleanupJobState(JobState jobState) {
        getState().removeJobState(jobState.getJob().getId().id);

        doSnapshot();
    }

    public WorkerState getState() {
        return state;
    }

    public HakaSettings getSettings() {
        return settings;
    }

    public ContextLoggingAdapter getLog() {
        return log;
    }

    @Override
    public void appendContext(StringBuilder sb) {
        sb.append('[').append(getSelf().path().name()).append(']');
        sb.append('[').append(state.dumpJobStatesIds()).append(']');
        sb.append(' ');
    }

    protected void handleSaveSnapshotSuccess(SaveSnapshotSuccess message) {
        log.debug("Success to save snapshot: %s", message);
    }

    protected void handleSaveSnapshotFailure(SaveSnapshotFailure message) {
        log.error("Failed to save snapshot: %s", message);

        // TODO what actions to take here
    }

    protected void addMsgHandler(MsgHandler<? extends Msg> handler) {
        msgHandlers.put(handler.getMsgClass(), handler);
    }

    @SuppressWarnings("unchecked")
    private MsgHandler<Msg> getHandler(final Msg msg) {
        return (MsgHandler<Msg>) msgHandlers.get(msg.getClass());
    }

    protected void setWatchdogHandler(WatchdogHandler watchdogHandler) {
        this.watchdogHandler = watchdogHandler;
    }

    protected void setIdleJobHandler(IdleJobHandler idleJobHandler) {
        this.idleJobHandler = idleJobHandler;
    }

    protected void startWatchdogTask() {
        stopWatchdogTask();

        assert watchdogCancellable == null || watchdogCancellable.isCancelled();

        log.debug("Starting watchdog task every: %d s", settings.getWatchdogPeriod().toSeconds());

        ActorSystem system = getContext().system();
        watchdogCancellable = system.scheduler()
                .schedule(settings.getWatchdogPeriod(), settings.getWatchdogPeriod(), getSelf(),
                        InternalMessage.WATCHDOG, system.dispatcher(), null);
    }

    protected void stopWatchdogTask() {
        if (watchdogCancellable != null && !watchdogCancellable.isCancelled()) {
            log.debug("Stop watchdog task");
            watchdogCancellable.cancel();
        }
    }

    protected void startIdleJobTask() {
        stopIdleJobTask();

        assert idleJobCancellable == null || idleJobCancellable.isCancelled();

        log.debug("Starting idle jobs task every: %d s", settings.getIdleJobPeriod().toSeconds());

        ActorSystem system = getContext().system();
        idleJobCancellable = system.scheduler()
                .schedule(settings.getIdleJobPeriod(), settings.getIdleJobPeriod(), getSelf(), InternalMessage.IDLEJOB,
                        system.dispatcher(), null);
    }

    protected void stopIdleJobTask() {
        if (idleJobCancellable != null && !idleJobCancellable.isCancelled()) {
            log.debug("Stop idle jobs task");
            idleJobCancellable.cancel();
        }
    }

    private void resetStatusUpdateTimes(WorkerState state) {
        HashMap<Id, JobState> jobStates = state.getJobStates();

        for (JobState jobState : jobStates.values()) {
            jobState.resetStatusUpdateTime(true);
        }
    }

    public enum PingPong {
        PING, PONG
    }

    private enum InternalMessage {
        WATCHDOG, IDLEJOB
    }

    private final HakaSettings settings = HakaSettings.Provider.SettingsProvider.get(getContext().system());
    private final ContextLoggingAdapter log = new ContextLoggingAdapter(getContext().system(), this);
    private final HashMap<Class<? extends Msg>, MsgHandler<? extends Msg>> msgHandlers;

    private WatchdogHandler watchdogHandler;
    private IdleJobHandler idleJobHandler;
    private Cancellable watchdogCancellable;
    private Cancellable idleJobCancellable;
    private WorkerState state;
}
