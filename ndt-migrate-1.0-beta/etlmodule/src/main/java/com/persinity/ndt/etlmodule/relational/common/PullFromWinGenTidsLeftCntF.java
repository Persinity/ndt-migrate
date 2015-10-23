/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
