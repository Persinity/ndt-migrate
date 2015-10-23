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
package com.persinity.ndt.etlmodule.haka;

import com.persinity.common.db.Closeable;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;

/**
 * @author Ivan Dachev
 */
public class EtlContext<S extends Closeable, D extends Closeable> {

    public EtlContext(EtlPlanGenerator<S, D> etlPlanGenerator) {
        this.etlPlanGenerator = etlPlanGenerator;
    }

    /**
     * @return EtlPlanGenerator
     */
    public EtlPlanGenerator<S, D> getEtlPlanGenerator() {
        return etlPlanGenerator;
    }

    private final EtlPlanGenerator<S, D> etlPlanGenerator;
}
