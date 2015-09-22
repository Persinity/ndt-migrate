/*
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.datamutator.load;

import static com.persinity.common.StringUtils.format;

import java.io.Serializable;
import java.util.List;

import com.persinity.common.invariant.Invariant;

/**
 * @author Ivo Yanakiev
 */
public abstract class EntityBase implements Serializable, Comparable<EntityBase> {

    public abstract List<Object> getId();

    public abstract void mutate(final long mutateId);

    public abstract String getType();

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final EntityBase other) {
        Invariant.notNull(other);

        final List<Object> thisId = getId();
        Invariant.notNull(thisId);

        final List<Object> otherId = other.getId();
        Invariant.notNull(otherId);

        int result = getType().compareTo(other.getType());
        if (result != 0) {
            return result;
        }

        Invariant.assertArg(thisId.size() == otherId.size());

        for (int i = 0; i < thisId.size(); i++) {
            final Object thisIdComp = thisId.get(i);
            final Object otherIdComp = otherId.get(i);
            if (! (thisIdComp instanceof Comparable) ) {
                throw new RuntimeException(format("Expected comparable {} {}", this, thisIdComp));
            }
            if (! (otherIdComp instanceof Comparable) ) {
                throw new RuntimeException(format("Expected comparable {} {}", other, otherIdComp));
            }

            result = ((Comparable)thisIdComp).compareTo(otherIdComp);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

}
