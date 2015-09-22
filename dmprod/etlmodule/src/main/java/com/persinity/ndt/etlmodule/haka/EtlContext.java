/**
 * Copyright (c) 2015 Persinity Inc.
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
