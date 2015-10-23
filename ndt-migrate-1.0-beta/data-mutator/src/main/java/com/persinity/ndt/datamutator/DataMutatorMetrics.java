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
