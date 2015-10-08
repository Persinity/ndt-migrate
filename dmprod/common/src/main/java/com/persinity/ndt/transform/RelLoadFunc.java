/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.RelDb;

/**
 * Represents load function.
 * 
 * @author Ivan Dachev
 */
public interface RelLoadFunc extends Function<DirectedEdge<RelDb, Iterator<Map<String, Object>>>, Integer> {
}
