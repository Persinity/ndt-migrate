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

import static com.persinity.common.invariant.Invariant.notNull;
import static com.persinity.ndt.controller.step.ContextUtil.getWindowGenerator;

import java.util.Map;

import org.apache.log4j.Logger;

import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.controller.script.BaseStep;
import com.persinity.ndt.controller.script.Step;
import com.persinity.ndt.etlmodule.WindowGenerator;

/**
 * @author Ivan Dachev
 */
public class StopWindowGenerator extends BaseStep {
    public static final boolean FORCE_STOP = true;
    public static final boolean FEED_EXAUSTED_STOP = false;

    /**
     * @param prev
     *         Step or {@code null} if the first step
     * @param delaySecs
     *         from previous step
     * @param ctx
     */
    public StopWindowGenerator(final Step prev, final int delaySecs, final Map<Object, Object> ctx, final String key,
            final boolean forceStop, final String userMsg) {
        super(prev, delaySecs, ctx);
        this.key = key;
        this.forceStop = forceStop;
        this.userMsg = userMsg;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void work() {
        if (userMsg != null) {
            getController().getView().logNdtMessage(userMsg);
        }

        final WindowGenerator<RelDb, RelDb> windowGenerator = getWindowGenerator(key, getCtx());
        notNull(windowGenerator);
        if (forceStop) {
            log.debug("Force stop window generator: {}", windowGenerator);
            windowGenerator.forceStop();
        } else {
            log.debug("Stop window generator: {}", windowGenerator);
            windowGenerator.stopWhenFeedExhausted();
        }
    }

    private final String key;
    private final boolean forceStop;
    private final String userMsg;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(StopWindowGenerator.class));
}
