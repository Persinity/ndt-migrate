/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;

/**
 * Represents data transfer (migration/upgrade/etc) function between Source and Destination state nodes.
 * 
 * @author Doichin Yordanov
 */
public interface TransferFunc<S, D> extends Function<DirectedEdge<S, D>, Integer> {
}
