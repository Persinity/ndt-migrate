/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.db.metainfo;

import com.persinity.common.collection.WeightedDirectedEdge;
import com.persinity.common.db.metainfo.constraint.FK;

/**
 * @author Doichin Yordanov
 */
public class FKEdge extends WeightedDirectedEdge<String, FK, String> {

    public FKEdge(final String src, final FK fk, final String dst) {
        super(src, fk, dst);
    }

}
