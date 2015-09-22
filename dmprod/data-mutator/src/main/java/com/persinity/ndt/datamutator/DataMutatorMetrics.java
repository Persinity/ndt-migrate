/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator;

import com.persinity.common.metrics.Metrics;

/**
 * @author Ivan Dachev
 */
public class DataMutatorMetrics {
    public static String METER_DMLS_PER_SECOND = "com.persinity.ndt.datamutator.dmlspersecond";
    public static String METER_TRANSACTIONS_PER_SECOND = "com.persinity.ndt.datamutator.transactionspersecond";
    public static String COUNT_TRANSACTION_ROLLBACKS = "com.persinity.ndt.datamutator.transaction.rollbakcs";

    /**
     * @return DMLs
     */
    public static long getDmls() {
        return Metrics.getMetrics().getMeterCount(METER_DMLS_PER_SECOND);
    }

    /**
     * @return DMLs per second
     */
    public static double getDps() {
        return Metrics.getMetrics().getOneMinuteRate(METER_DMLS_PER_SECOND);
    }

    /**
     * @return transaction commits
     */
    public static long getTransactionCommits() {
        return Metrics.getMetrics().getMeterCount(METER_TRANSACTIONS_PER_SECOND);
    }

    /**
     * @return transactions per second
     */
    public static double getTps() {
        return Metrics.getMetrics().getOneMinuteRate(METER_TRANSACTIONS_PER_SECOND);
    }

    /**
     * @return transaction rollbacks
     */
    public static long getTransactionRollbacks() {
        return Metrics.getMetrics().getCount(COUNT_TRANSACTION_ROLLBACKS);
    }
}
