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

import com.google.common.base.Function;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.CdcAgent;
import com.persinity.ndt.dbagent.ClogAgent;
import com.persinity.ndt.dbagent.SchemaAgent;

/**
 * @author Ivan Dachev
 */
public class AgentContext {

    public AgentContext(final ClogAgent<Function<RelDb, RelDb>> srcClogAgent,
            final ClogAgent<Function<RelDb, RelDb>> dstClogAgent, final CdcAgent<Function<RelDb, RelDb>> cdcAgent,
            final SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent) {
        notNull(srcClogAgent);
        notNull(dstClogAgent);
        notNull(cdcAgent);
        notNull(dstSchemaAgent);

        this.srcClogAgent = srcClogAgent;
        this.dstClogAgent = dstClogAgent;
        this.cdcAgent = cdcAgent;
        this.dstSchemaAgent = dstSchemaAgent;
    }

    /**
     * @return source {@link ClogAgent}
     */
    public ClogAgent<Function<RelDb, RelDb>> getSrcClogAgent() {
        return srcClogAgent;
    }

    /**
     * @return destinaiton {@link ClogAgent}
     */
    public ClogAgent<Function<RelDb, RelDb>> getDstClogAgent() {
        return dstClogAgent;
    }

    /**
     * @return {@link CdcAgent}
     */
    public CdcAgent<Function<RelDb, RelDb>> getCdcAgent() {
        return cdcAgent;
    }

    /**
     * @return source {@link SchemaAgent}
     */
    public SchemaAgent<Function<RelDb, RelDb>> getDstSchemaAgent() {
        return dstSchemaAgent;
    }

    private final ClogAgent<Function<RelDb, RelDb>> srcClogAgent;
    private final ClogAgent<Function<RelDb, RelDb>> dstClogAgent;
    private final CdcAgent<Function<RelDb, RelDb>> cdcAgent;
    private final SchemaAgent<Function<RelDb, RelDb>> dstSchemaAgent;
}
