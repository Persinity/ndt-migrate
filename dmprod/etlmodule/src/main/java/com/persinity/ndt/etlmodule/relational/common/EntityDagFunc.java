/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.common;

import java.util.Set;

import com.google.common.base.Function;
import com.persinity.common.collection.Dag;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * Generates {@link Dag} of entity relations, by given set of entities
 *
 * @author Doichin Yordanov
 */
public interface EntityDagFunc extends Function<Set<String>, EntitiesDag> {

}
