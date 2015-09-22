/**
 * Copyright (c) 2015 Persinity Inc.
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