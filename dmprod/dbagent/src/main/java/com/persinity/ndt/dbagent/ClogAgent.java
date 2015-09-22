/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent;

import com.google.common.base.Function;
import com.persinity.common.collection.Tree;

/**
 * Responsible for un/mounting the logic and DB objects for Change log.
 *
 * @author Doichin Yordanov
 */
public interface ClogAgent<T extends Function<?, ?>> {

    /**
     * Generates the plan for Change log structures creation.
     */
    Tree<T> clogMount();

    /**
     * Generates the plan for Change log structures removal.
     */
    Tree<T> clogUmount();

    /**
     * Generates the plan for garbage collection of clog entries.
     */
    Tree<T> clogGc();

    /**
     * Deletes the TRLOG records.
     */
    Tree<T> trlogCleanup();

    /**
     * @return The maximal GID (change ID) out of all CLOG records
     */
    Long getLastGid();
}
