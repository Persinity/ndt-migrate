/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.etlmodule.relational.migrate;

import static com.persinity.common.invariant.Invariant.assertArg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.persinity.common.collection.PreviewIterator;
import com.persinity.common.db.metainfo.Col;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.SchemaInfo.ChangeType;
import com.persinity.ndt.transform.TupleFunc;

/**
 * {@link TupleFunc} that implements NDT coalesce
 *
 * @author Doichin Yordanov
 */
public class ClogCoalesceTupleFunc implements TupleFunc {

    public ClogCoalesceTupleFunc(final Set<Col> pidCols) {
        assert pidCols != null;
        this.pidCols = pidCols;
    }

    @Override
    public Iterator<Map<String, Object>> apply(final Iterator<Map<String, Object>> input) {
        return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Map<String, Object> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final Map<String, Object> res = next;
                next = resolveNext(previewInput);
                return res;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private final PreviewIterator<Map<String, Object>> previewInput = new PreviewIterator<>(input);
            private Map<String, Object> next = resolveNext(previewInput);
        };
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    private Map<String, Object> resolveNext(final PreviewIterator<Map<String, Object>> previewInput) {
        Map<String, Object> res = null;
        while (true) {
            final ArrayList<Map<String, Object>> group = getNextGroup(previewInput);
            if (group.isEmpty()) {
                break;
            }
            res = coalesce(group);
            if (res != null) {
                break;
            }
        }
        return res;
    }

    private ArrayList<Map<String, Object>> getNextGroup(final PreviewIterator<Map<String, Object>> input) {
        final ArrayList<Map<String, Object>> group = new ArrayList<>();
        while (input.hasNext()) {
            final Map<String, Object> data = input.preview();
            final Set<Object> pkValues = getPkValues(data);
            if (group.isEmpty() || pkValues.equals(getPkValues(group.get(0)))) {
                group.add(data);
                input.next();
            } else {
                break;
            }
        }
        return group;
    }

    private static Map<String, Object> coalesce(final ArrayList<Map<String, Object>> group) {
        assert !group.isEmpty();

        Map<String, Object> res;
        final Map<String, Object> first = group.get(0);

        if (group.size() == 1) {
            res = first;
        } else {
            // here aways get the last one expect in a group like this: I,...,D
            // if we have U,U,D,I,U,D the D will be returned to cleanup the existing
            // record as the first U indicates the PK is alive
            // if we have D,I,U,U,D again D must be returned for the cleanup
            final Map<String, Object> last = group.get(group.size() - 1);

            if (isInsert(first) && isDelete(last)) {
                res = null;
            } else {
                res = last;
            }
        }

        group.clear();
        return res;
    }

    private Set<Object> getPkValues(final Map<String, Object> data) {
        final Set<Object> pkValues = new LinkedHashSet<>();
        for (Col pidCol : pidCols) {
            final Object pkValue = data.get(pidCol.getName());
            assertArg(pkValue != null, "Failed to find PkCol: {} value", pidCol.getName());
            pkValues.add(pkValue);
        }
        return pkValues;
    }

    private static boolean isInsert(final Map<String, Object> data) {
        final String ctype = getCtype(data);
        return ctype.equals(ChangeType.I.toString());
    }

    private static boolean isDelete(final Map<String, Object> data) {
        final String ctype = getCtype(data);
        return ctype.equals(ChangeType.D.toString());
    }

    private static String getCtype(final Map<String, Object> data) {
        final String ctype = (String) data.get(SchemaInfo.COL_CTYPE);
        assertArg(ctype != null, "Failed to find {} value", SchemaInfo.COL_CTYPE);
        assertArg(ctype.equals(ChangeType.I.toString()) || ctype.equals(ChangeType.U.toString()) || ctype
                        .equals(ChangeType.D.toString()), "Invalid {} value {}", SchemaInfo.COL_CTYPE, ctype);
        return ctype;
    }

    private final Set<Col> pidCols;
}
