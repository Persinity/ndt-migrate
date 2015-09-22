/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule;

import java.util.Set;

import com.google.common.base.Function;
import com.persinity.ndt.transform.TransferFunc;

/**
 * Functor that returns a set of {@link TransferFunc}s, which ETL window related data to a destination entity.
 *
 * @author Ivan Dachev
 */
public interface TransferFunctor<S, D> extends Function<Void, Set<TransferFunc<S, D>>> {
}
