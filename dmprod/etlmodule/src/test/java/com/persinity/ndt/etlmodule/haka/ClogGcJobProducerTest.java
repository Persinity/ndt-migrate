/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.ndt.etlmodule.haka;

import static com.persinity.test.TestUtil.assertEquals;
import static org.easymock.EasyMock.createNiceMock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Function;
import com.persinity.common.collection.CollectionContentComparator;
import com.persinity.common.collection.CollectionUtils;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.haka.JobIdentity;

/**
 * @author dyordanov
 */
public class ClogGcJobProducerTest {
    @Test
    public void testProcess() {
        final ClogGcJobProducer testee = new ClogGcJobProducer();
        final CollectionContentComparator<ClogGcJob> comparator = new CollectionContentComparator<>(
                new IdIgnorantJobComparator());

        final JobIdentity gcJobId = new JobIdentity();
        final Function<RelDb, RelDb> f1 = stubF(1);
        final Function<RelDb, RelDb> f2 = stubF(2);
        final Function<RelDb, RelDb> f3 = stubF(3);
        final Pool<RelDb> db = createNiceMock(Pool.class);
        final GcJob job = new GcJob(gcJobId, CollectionUtils.newTree(Arrays.asList(f1, f2, f3)), db, 2);

        Set<ClogGcJob> children = testee.process(job);
        Set<ClogGcJob> expectedChildren = new HashSet<>(Arrays.asList(new ClogGcJob(new JobIdentity(gcJobId), f1, db),
                new ClogGcJob(new JobIdentity(gcJobId), f2, db)));
        assertEquals(expectedChildren, children, comparator);

        children = testee.process(job);
        expectedChildren = new HashSet<>(Arrays.asList(new ClogGcJob(new JobIdentity(gcJobId), f3, db)));
        assertEquals(expectedChildren, children, comparator);

        children = testee.process(job);
        expectedChildren = Collections.emptySet();
        assertEquals(expectedChildren, children, comparator);
    }

    private Function<RelDb, RelDb> stubF(final int i) {
        return new ComparableF(i);
    }

    private class ComparableF implements Function<RelDb, RelDb> {

        public ComparableF(int weight) {
            this.weight = weight;
        }

        @Override
        public RelDb apply(final RelDb input) {
            return input;
        }

        public final int weight;
    }

    private class IdIgnorantJobComparator implements Comparator<ClogGcJob> {
        @Override
        public int compare(final ClogGcJob o1, final ClogGcJob o2) {
            final ComparableF f1 = (ComparableF) o1.getGcF();
            final ComparableF f2 = (ComparableF) o2.getGcF();
            return f1.weight - f2.weight;
        }
    }
}