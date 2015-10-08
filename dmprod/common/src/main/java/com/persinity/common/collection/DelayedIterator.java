/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.collection;

import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Iterator;

import com.google.common.base.Function;

/**
 * A wrapper iterator that delays the calling of next and
 * returns a marker value until specified time has passed.
 *
 * @author Ivan Dachev
 */
public class DelayedIterator<E> implements Iterator<E> {

    /**
     * @param iter
     *         original {@link Iterator} to use
     * @param isMarkerValueF
     *         function to check is returned value a marker
     * @param markerDelayMs
     *         before calling next on original iterator if the last value was a marker,
     *         note that the actual delay can be greater if hasNext is not called regularly
     */
    public DelayedIterator(final Iterator<E> iter, final Function<E, Boolean> isMarkerValueF,
            final long markerDelayMs) {
        notNull(iter);
        notNull(isMarkerValueF);
        assertArg(markerDelayMs > 0);
        this.iter = iter;
        this.isMarkerValueF = isMarkerValueF;
        this.timeoutMs = markerDelayMs;
    }

    @Override
    public boolean hasNext() {
        recheckElapsed();
        if (markedValue != null) {
            return true;
        }
        return iter.hasNext();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public E next() {
        if (markedValue != null) {
            return markedValue;
        }

        final E value = iter.next();
        if (isMarkerValueF.apply(value)) {
            markedValue = value;
            lastMarkedValueTime = System.currentTimeMillis();
        }

        return value;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void recheckElapsed() {
        if (markedValue != null) {
            final long timeNow = System.currentTimeMillis();
            if (timeNow < lastMarkedValueTime) {
                // detected back clock move, reset marked time
                lastMarkedValueTime = timeNow;
            }
            if ((timeNow - lastMarkedValueTime) >= timeoutMs) {
                markedValue = null;
            }
        }
    }

    private final Iterator<E> iter;
    private final Function<E, Boolean> isMarkerValueF;
    private final long timeoutMs;

    private E markedValue;
    private long lastMarkedValueTime;
}
