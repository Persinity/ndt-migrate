/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.transform;

import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;

/**
 * Function that transforms set of tuples to a set of tuples.<BR>
 * A tuple is associative array (map) of objects. It can represent a table row or entity instance data.
 * 
 * @author Doichin Yordanov
 */
public interface TupleFunc extends Function<Iterator<Map<String, Object>>, Iterator<Map<String, Object>>> {

}
