/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.common.fp.Functor;

/**
 * Plan builder utils.
 * 
 * @author Doichin Yordanov
 */
public class PlanBuilder {

    /**
     * Builds a tree plan of functions using the supplied functor.
     * 
     * @param args
     * @param functor
     * @return
     */
    public <T> Tree<Function<RelDb, RelDb>> build(final Set<T> args,
            final Functor<RelDb, RelDb, T, Function<RelDb, RelDb>> functor) {
        final List<Function<RelDb, RelDb>> funcs = new LinkedList<>();
        for (final T arg : args) {
            final Function<RelDb, RelDb> func = functor.apply(arg);
            funcs.add(func);
        }
        final Tree<Function<RelDb, RelDb>> plan = CollectionUtils.newTree(funcs);
        return plan;
    }

}
