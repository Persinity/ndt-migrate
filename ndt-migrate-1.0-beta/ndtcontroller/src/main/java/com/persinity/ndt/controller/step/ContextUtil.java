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

import java.util.HashMap;
import java.util.Map;

import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbdiff.BufferedSchemaDiffGenerator;
import com.persinity.ndt.dbdiff.MigrateSchemaDiffGenerator;
import com.persinity.ndt.dbdiff.SchemaDiffGenerator;
import com.persinity.ndt.etlmodule.WindowGenerator;

/**
 * TODO Think about creating a Context class
 *
 * @author Ivan Dachev
 */
public class ContextUtil {

    /**
     * @param key
     *         WindowGenerator key
     * @param windowGenerator
     *         to add
     * @param ctx
     *         context to add to
     */
    @SuppressWarnings("unchecked")
    public static void addWindowGenerator(final String key, final WindowGenerator<RelDb, RelDb> windowGenerator,
            final Map<Object, Object> ctx) {
        synchronized (ctx) {
            Map<String, WindowGenerator<RelDb, RelDb>> windowGenerators = (Map<String, WindowGenerator<RelDb, RelDb>>) ctx
                    .get(WindowGenerator.class);
            if (windowGenerators == null) {
                windowGenerators = new HashMap<>();
                ctx.put(WindowGenerator.class, windowGenerators);
            }
            windowGenerators.put(key, windowGenerator);
        }
    }

    /**
     * @param ctx
     *         context to get from
     * @return WindowGenerators
     */
    @SuppressWarnings("unchecked")
    public static WindowGenerator<RelDb, RelDb> getWindowGenerator(final String key, final Map<Object, Object> ctx) {
        synchronized (ctx) {
            return ((Map<String, WindowGenerator<RelDb, RelDb>>) ctx.get(WindowGenerator.class)).get(key);
        }
    }

    /**
     * Gets {@link SchemaDiffGenerator} instance from the context. If such is not found, new one is created and is put in the context.
     *
     * @param ctx
     * @return
     */
    public static SchemaDiffGenerator getSchemaDiffGenerator(final Map<Object, Object> ctx) {
        notNull(ctx);
        synchronized (ctx) {
            SchemaDiffGenerator sdg = (SchemaDiffGenerator) ctx.get(SchemaDiffGenerator.class);
            if (sdg == null) {
                sdg = new BufferedSchemaDiffGenerator(new MigrateSchemaDiffGenerator());
                ctx.put(SchemaDiffGenerator.class, sdg);
            }
            return sdg;
        }
    }

}
