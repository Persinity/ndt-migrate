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