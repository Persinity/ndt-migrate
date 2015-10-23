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

import static com.persinity.ndt.controller.step.StopWindowGenerator.FEED_EXAUSTED_STOP;
import static com.persinity.ndt.controller.step.StopWindowGenerator.FORCE_STOP;
import static org.easymock.EasyMock.expectLastCall;

import org.junit.Test;

import com.persinity.common.db.RelDb;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.etlmodule.WindowGenerator;

/**
 * @author Ivan Dachev
 */
public class StopWindowGeneratorTest extends NdtStepBaseTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testFeedExhaustedStop() throws Exception {
        final WindowGenerator<RelDb, RelDb> windowGenerator = createMock(WindowGenerator.class);
        ContextUtil.addWindowGenerator("key", windowGenerator, ctx);

        windowGenerator.stopWhenFeedExhausted();
        expectLastCall();

        replayAll();

        final StopWindowGenerator testee = new StopWindowGenerator(null, Step.NO_DELAY, ctx, "key", FEED_EXAUSTED_STOP,
                null);

        testee.work();

        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForceStop() throws Exception {
        final WindowGenerator<RelDb, RelDb> windowGenerator = createMock(WindowGenerator.class);
        ContextUtil.addWindowGenerator("key", windowGenerator, ctx);

        windowGenerator.forceStop();
        expectLastCall();

        replayAll();

        final StopWindowGenerator testee = new StopWindowGenerator(null, Step.NO_DELAY, ctx, "key", FORCE_STOP, null);

        testee.work();

        verifyAll();
    }
}