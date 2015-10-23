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

package com.persinity.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * {@link SoftReference} which equals and hashCode depend on the referent object.<BR>
 * Useful when caching soft references to limit the amount of memory to the numbered of actually referent objects.
 *
 * @author dyordanov
 */
public final class ReferentComparableSoftReference<T> extends SoftReference<T> {

    public ReferentComparableSoftReference(final T referent) {
        super(referent);
    }

    private ReferentComparableSoftReference(final T referent, final ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    @Override
    public int hashCode() {
        T referent = get();
        return referent != null ? referent.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SoftReference))
            return false;
        final SoftReference<T> thatReference = (SoftReference) obj;
        final T thisObject = get();
        final T thatObject = thatReference.get();
        if (thisObject == null && thatObject == null)
            return true;
        else if (thisObject == null || thatObject == null)
            return false;
        return thisObject.equals(thatObject);
    }

    @Override
    public String toString() {
        return format("{}({})", formatObj(this), get());
    }
}
