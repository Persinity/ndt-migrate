/*
 *
 *  * Copyright (c) 2015 Persinity Inc.
 *  *
 *
 */

package com.persinity.ndt.datamutator;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.assertArg;

import java.util.Properties;

import com.persinity.common.Config;

/**
 * @author Ivo Yanakiev
 */
public class DataMutatorConfig {

    public DataMutatorConfig(final Properties props, final String propsSource) {
        config = new Config(props, propsSource);
    }

    public int getParallelConnections() {
        return config.getPositiveInt(PARALLEL_CONNECTIONS_KEY);
    }

    public int getDmlsPerTransaction() {
        return config.getPositiveInt(DMLS_PER_TRANSACTION_KEY);
    }

    public long getTransactionDelayInMs() {
        return config.getLong(TRANSACTION_DELAY_MS_KEY);
    }

    public int getRatio() {
        if (ratio == null) {
            String ratioStr = config.getString(RATIO_INSERT_DELETE_KEY);
            String[] ratioData = ratioStr.split(":");
            assertArg((ratioData.length == 2), "Unable to parse ratio: " + ratio);
            double addCount = Double.parseDouble(ratioData[0]);
            double deleteCount = Double.parseDouble(ratioData[1]);
            assertArg((addCount > deleteCount), "Add count must be bigger than delete count.");
            ratio = (int) ((addCount / (addCount + deleteCount)) * 100);
            assertArg((1 <= ratio && ratio <= 99), "Ratio must be between 1 and 99");
        }
        return ratio;
    }

    public LoadType getLoadType() {
        return LoadType.getByString(config.getString(LOAD_TYPE_KEY));
    }

    public int getLoadQuantity() {
        return config.getInt(LOAD_QUANTITY_KEY);
    }

    public long getIdRangeMin() {
        return config.getLong(ID_RANGE_MIN);
    }

    public long getIdRangeMax() {
        return config.getLong(ID_RANGE_MAX);
    }

    public long getIdRangeIncrement() {
        return config.getLong(ID_RANGE_INCREMENT);
    }

    public Class getEntityFactoryClass() {
        try {
            return Class.forName(config.getString(ENTITY_FACTORY_CLASS_KEY));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getEntityStoreCapacity() {
        return config.getPositiveInt(ENTITY_STORE_CAPACITY);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", formatObj(this), config.dumpProperties());
        }
        return toString;
    }

    static final String PARALLEL_CONNECTIONS_KEY = "parallel.connections";
    static final String DMLS_PER_TRANSACTION_KEY = "dmls.per.transaction";
    static final String TRANSACTION_DELAY_MS_KEY = "transaction.delay.ms";
    static final String RATIO_INSERT_DELETE_KEY = "ratio.insert.delete";
    static final String LOAD_TYPE_KEY = "load.type";
    static final String LOAD_QUANTITY_KEY = "load.quantity";
    static final String ID_RANGE_MIN = "range.id.min";
    static final String ID_RANGE_MAX = "range.id.max";
    static final String ID_RANGE_INCREMENT = "range.id.increment";
    static final String ENTITY_FACTORY_CLASS_KEY = "entity.factory.class";
    static final String ENTITY_STORE_CAPACITY = "entity.store.capacity";

    private Integer ratio;
    private String toString;

    private final Config config;
}
