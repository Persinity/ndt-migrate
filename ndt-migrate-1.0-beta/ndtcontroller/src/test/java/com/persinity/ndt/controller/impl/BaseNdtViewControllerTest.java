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
package com.persinity.ndt.controller.impl;

import static com.persinity.common.ThreadUtil.sleep;
import static com.persinity.common.ThreadUtil.waitForCondition;
import static com.persinity.ndt.controller.NdtEvent.NdtEventType.initialTransferStarted;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.ThreadUtil;
import com.persinity.ndt.controller.NdtEvent;

/**
 * @author Ivan Dachev
 */
public class BaseNdtViewControllerTest {

    /**
     * Test method for {@link BaseNdtViewController#sendEvent(NdtEvent)}
     */
    @Test
    public void testSendEvent() {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        final NdtEvent event = new NdtEvent(initialTransferStarted);

        lastFiredEvent = null;
        view.sendEvent(event);
        waitForFiredEvent();

        assertThat(lastFiredEvent, is(event));
    }

    /**
     * Test method for {@link BaseNdtViewController#sendEvent(NdtEvent)}
     */
    @Test(expected = NullPointerException.class)
    public void testSendEvent_Null() throws Exception {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        view.sendEvent(null);
    }

    /**
     * Test method for {@link BaseNdtViewController#sendBlockingEvent(NdtEvent)}
     */
    @Test
    public void testSendBlockingEvent() {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        final NdtEvent event = new NdtEvent(initialTransferStarted);

        final Thread th = new Thread() {
            @Override
            public void run() {
                waitForFiredEvent();
                view.receiveAck();
            }
        };

        lastFiredEvent = null;
        th.start();
        view.sendBlockingEvent(event);

        assertThat(lastFiredEvent, is(event));
    }

    /**
     * Test method for {@link BaseNdtViewController#sendBlockingEvent(NdtEvent)}
     */
    @Test(expected = NullPointerException.class)
    public void testSendBlockingEvent_Null() throws Exception {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        view.sendBlockingEvent(null);
    }

    /**
     * Test method for {@link BaseNdtViewController#sendBlockingEvent(NdtEvent)}
     */
    @Test(expected = IllegalStateException.class)
    public void testSendBlockingEvent_IllegalState() {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        final NdtEvent event = new NdtEvent(initialTransferStarted);

        final boolean[] started = new boolean[1];
        started[0] = false;
        Thread th = new Thread() {
            @Override
            public void run() {
                started[0] = true;
                view.sendBlockingEvent(event);
            }
        };
        th.start();
        while (!started[0]) {
            sleep(250);
        }
        try {
            view.sendBlockingEvent(event);
        } finally {
            // to stop the thread for graceful test exit
            view.receiveAck();
        }
    }

    /**
     * Test method for {@link BaseNdtViewController#receiveAck()}
     */
    @Test(expected = IllegalStateException.class)
    public void testReceiveAck_Invalid() {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        view.receiveAck();
    }

    /**
     * Test method for {@link BaseNdtViewController#sendBlockingEvent(NdtEvent)}
     */
    @Test(expected = RuntimeException.class)
    public void testSendBlockingEvent_Interrupted() {
        final StubBaseNdtViewController view = new StubBaseNdtViewController();
        final NdtEvent event = new NdtEvent(initialTransferStarted);

        final Thread mainTh = Thread.currentThread();
        final Thread th = new Thread() {
            @Override
            public void run() {
                ThreadUtil.sleep(500);
                mainTh.interrupt();
            }
        };

        lastFiredEvent = null;
        th.start();
        view.sendBlockingEvent(event);
    }

    class StubBaseNdtViewController extends BaseNdtViewController {
        @Override
        protected void fire(final NdtEvent event) {
            lastFiredEvent = event;
        }

        @Override
        public void logNdtMessage(final String msg) {
        }

        @Override
        public void setNdtStatusMessage(final String msg) {
        }

        @Override
        public void setProgress(final boolean state) {
        }

        @Override
        public void run() {

        }
    }

    private void waitForFiredEvent() {
        final Function<Void, Boolean> condition = new Function<Void, Boolean>() {
            @Override
            public Boolean apply(final Void aVoid) {
                return lastFiredEvent != null;
            }
        };
        if (!waitForCondition(condition, TIMEOUT_WAIT_FOR_EVENT_MS)) {
            fail("Timeout on waiting for event");
        }
    }

    private static final long TIMEOUT_WAIT_FOR_EVENT_MS = 5000;
    private NdtEvent lastFiredEvent;
}