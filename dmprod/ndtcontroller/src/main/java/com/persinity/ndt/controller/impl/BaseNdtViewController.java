/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.impl;

import static com.persinity.common.invariant.Invariant.assertState;
import static com.persinity.common.invariant.Invariant.notNull;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.NdtEvent;
import com.persinity.ndt.controller.NdtViewController;

/**
 * Base {@link NdtViewController} controller that blocks on {@code NdtView#BLOCKING_EVENTS} through thread wait/notify
 *
 * @author Doichin Yordanov
 */
public abstract class BaseNdtViewController implements NdtViewController {

    static final class AsyncFirer implements Runnable {

        private final BaseNdtViewController view;
        private final NdtEvent event;

        public AsyncFirer(BaseNdtViewController view, NdtEvent event) {
            this.view = view;
            this.event = event;
        }

        @Override
        public void run() {
            view.fire(event);
        }

    }

    @Override
    public void receiveAck() {
        synchronized (lock) {
            assertState(lastBlockingEvent != null, "No blocking event to wait on");
            log.debug("Received ack for {}", lastBlockingEvent);
            recvAckBlockingEvent = lastBlockingEvent;
            lock.notifyAll();
        }
    }

    @Override
    public void sendEvent(NdtEvent event) {
        notNull(event);
        log.debug("Sending {}", event);
        new Thread(new AsyncFirer(this, event)).start();
    }

    @Override
    public void sendBlockingEvent(NdtEvent event) {
        notNull(event);
        synchronized (lock) {
            assertState(lastBlockingEvent == null, "Already waiting on {}", lastBlockingEvent);
            assertState(recvAckBlockingEvent == null, "Already received ack for {}", recvAckBlockingEvent);
            lastBlockingEvent = event;
        }
        sendEvent(event);
        synchronized (lock) {
            log.debug("Waiting on {}", lastBlockingEvent);
            while (recvAckBlockingEvent == null) {
                try {
                    lock.wait(500);
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            assertState(recvAckBlockingEvent == lastBlockingEvent, "Expected {} but received ack for {}",
                    lastBlockingEvent, recvAckBlockingEvent);
            lastBlockingEvent = null;
            recvAckBlockingEvent = null;
            log.debug("End of {}", event);
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    protected boolean isClosed() {
        return closed;
    }

    /**
     * @param event
     */
    protected abstract void fire(NdtEvent event);

    private NdtEvent lastBlockingEvent;
    private NdtEvent recvAckBlockingEvent;
    private boolean closed;

    private final Object lock = new Object();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(BaseNdtViewController.class));
}
