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
package com.persinity.ndt.etlmodule.serial;

import static com.persinity.common.IoUtils.silentClose;
import static com.persinity.common.ThreadUtil.sleepSeconds;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanExecutor;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivan Dachev
 */
public class SerialEtlPlanExecutor implements EtlPlanExecutor {

    public SerialEtlPlanExecutor(final int windowCheckIntervalSeconds) {
        this.windowCheckIntervalSeconds = windowCheckIntervalSeconds;
    }

    @Override
    public void execute(final WindowGenerator<RelDb, RelDb> winGen, final EtlPlanGenerator<RelDb, RelDb> etlPlanner,
            final String name) {

        log.debug("[{}] Getting next window from {}", name, winGen);
        final Iterator<TransferWindow<RelDb, RelDb>> winIt = winGen.iterator();
        while (winIt.hasNext()) {

            final TransferWindow<RelDb, RelDb> win = winIt.next();
            if (win.isEmpty()) {
                sleepSeconds(windowCheckIntervalSeconds);
                continue;
            }

            log.debug("[{}] Transfer of {}", name, win);
            final EtlPlanDag<RelDb, RelDb> eltEntityPlan = etlPlanner.newEtlPlan(win);

            log.debug("[{}] Transform plan: {}", name, eltEntityPlan);
            final Iterator<TransferFunctor<RelDb, RelDb>> eltEntityIt = new TopologicalOrderIterator<>(eltEntityPlan);
            while (eltEntityIt.hasNext()) {

                final TransferFunctor<RelDb, RelDb> eltEntityF = eltEntityIt.next();

                log.debug("[{}] Processing {}", name, eltEntityF);
                final Set<TransferFunc<RelDb, RelDb>> eltInstructions = eltEntityF.apply(null);
                RelDb src = null;
                RelDb dst = null;
                try {
                    src = win.getDataPoolBridge().src().get();
                    dst = win.getDataPoolBridge().dst().get();
                    for (final TransferFunc<RelDb, RelDb> eltInstruction : eltInstructions) {
                        log.debug("[{}] Processing {} over {}", name, eltInstruction, win.getDataPoolBridge());
                        eltInstruction.apply(new DirectedEdge<>(src, dst));
                    }
                } finally {
                    silentClose(src, dst);
                }
            }
        }
    }

    @Override
    public void close() {
    }

    private final int windowCheckIntervalSeconds;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(SerialEtlPlanExecutor.class));
}
