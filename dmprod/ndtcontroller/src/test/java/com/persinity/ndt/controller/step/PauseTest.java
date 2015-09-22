/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller.step;

import static org.easymock.EasyMock.expectLastCall;

import org.junit.Test;

import com.persinity.ndt.controller.NdtEvent;
import com.persinity.ndt.controller.script.Step;

/**
 * @author Ivan Dachev
 */
public class PauseTest extends NdtStepBaseTest {

    @Test
    public void testWork() throws Exception {
        ndtControllerView.setProgress(false);
        expectLastCall();

        final NdtEvent event = new NdtEvent(NdtEvent.NdtEventType.setupCompleted, "test");
        ndtControllerView.sendBlockingEvent(event);
        expectLastCall();

        final Pause testee = new Pause(null, Step.NO_DELAY, event, ctx);

        replayAll();

        testee.work();

        verifyAll();
    }
}