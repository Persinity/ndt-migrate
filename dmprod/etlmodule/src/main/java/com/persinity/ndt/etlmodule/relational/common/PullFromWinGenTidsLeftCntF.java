/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.relational.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;

import com.persinity.common.StringUtils;

/**
 * {@link TidsLeftCntF} that pulls data from {@link BaseWindowGenerator}
 *
 * @author dyordanov
 */
public class PullFromWinGenTidsLeftCntF implements TidsLeftCntF {

    public PullFromWinGenTidsLeftCntF(final long gidSentinel) {
        assertArg(gidSentinel >= 0, format("Expected >=0, got {}", gidSentinel));
        this.gidSentinel = gidSentinel;
    }

    @Override
    public Long apply(final BaseWindowGenerator input) {
        return gidSentinel - input.getGidHead();
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = StringUtils.format("{}({})", this.getClass().getSimpleName(), gidSentinel);
        }
        return toString;
    }

    private final long gidSentinel;
    private String toString;
}
