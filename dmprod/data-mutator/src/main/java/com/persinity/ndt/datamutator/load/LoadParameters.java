/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.load;

/**
 * Used from loaders to tune the speed and volume.
 *
 * @author Ivan Dachev
 */
public interface LoadParameters {

    long getTransactionDelayInMs();

    int getDmlsPerTransaction();
}
