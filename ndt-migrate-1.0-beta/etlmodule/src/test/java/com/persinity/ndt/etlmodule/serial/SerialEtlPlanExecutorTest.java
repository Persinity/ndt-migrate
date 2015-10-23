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

package com.persinity.ndt.etlmodule.serial;

import static com.persinity.common.StringUtils.format;
import static java.lang.Math.min;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.collection.Pool;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.ndt.dbagent.relational.StringTid;
import com.persinity.ndt.etlmodule.EtlPlanDag;
import com.persinity.ndt.etlmodule.EtlPlanGenerator;
import com.persinity.ndt.etlmodule.TransferFunctor;
import com.persinity.ndt.etlmodule.WindowGenerator;
import com.persinity.ndt.etlmodule.relational.RelTransferWindow;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.TransferFunc;
import com.persinity.ndt.transform.TransferWindow;

/**
 * @author Ivo Yanakiev
 */
public class SerialEtlPlanExecutorTest extends EasyMockSupport {

    @Test
    public void testExecute() throws Exception {

        final WindowGenerator<RelDb, RelDb> winGen = createMock(WindowGenerator.class);

        expect(winGen.iterator()).andReturn(winGeneratorResult.iterator());

        expect(relDbPool.get()).andReturn(relDb).times(DB_OPEN_OPS);

        final EtlPlanGenerator<RelDb, RelDb> etlPlanner = createMock(EtlPlanGenerator.class);

        final Capture<TransferWindow<RelDb, RelDb>> argument = newCapture();
        expect(etlPlanner.newEtlPlan(capture(argument)));

        expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
                TransferWindow<RelDb, RelDb> win = argument.getValue();
                return planGeneratorResult.get(win);
            }

        }).anyTimes();
        relDb.close();
        expectLastCall().times(DB_OPEN_OPS);

        replayAll();

        final SerialEtlPlanExecutor executor = new SerialEtlPlanExecutor(0);
        executor.execute(winGen, etlPlanner, "test");

        verifyAll();

        for (int i = 0; i < min(expectedCallResult.size(), actualCallResult.size()); i++) {
            DirectedEdge<String, TransferWindow<RelDb, RelDb>> expected = expectedCallResult.get(i);
            DirectedEdge<String, TransferWindow<RelDb, RelDb>> actual = actualCallResult.get(i);

            if (!expected.equals(actual)) {
                throw new AssertionError(format("\nexpected element: {}\nactual element:   {}\n\n" + "expected: {}\n"
                        + "actual:    {}\n", expected, actual, expectedCallResult, actualCallResult));
            }

        }

        Assert.assertEquals(format("Missing or extra elements:\nexpected: {}\nactual:   {}\n", expectedCallResult,
                actualCallResult), expectedCallResult.size(), actualCallResult.size());

    }

    /**
     * <pre>
     *    (table1)
     *     /    \
     *    /      \
     * (table2)  (table3)
     *    |
     * (table4)
     * </pre>
     */
    @Before
    public void setUp() throws Exception {

        relDbPool = createMock(Pool.class);
        relDb = createNiceMock(RelDb.class);

        final DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge = new DirectedEdge<>(relDbPool, relDbPool);

        final EntitiesDag entitiesDag = new EntitiesDag();
        entitiesDag.addVertex(TABLE1);
        entitiesDag.addVertex(TABLE2);
        entitiesDag.addVertex(TABLE3);
        entitiesDag.addVertex(TABLE4);
        entitiesDag.addDagEdge(TABLE1, TABLE2, newFkEdge());
        entitiesDag.addDagEdge(TABLE1, TABLE3, newFkEdge());
        entitiesDag.addDagEdge(TABLE3, TABLE4, newFkEdge());

        generateTestWin1(entitiesDag, dataPoolBridge);
        generateTestWin2(entitiesDag, dataPoolBridge);
        generateTestWin3(entitiesDag, dataPoolBridge);

    }

    private void generateTestWin1(EntitiesDag entitiesDag, DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge) {
        final List<StringTid> tidList = new ArrayList<StringTid>() {{
            add(new StringTid("1"));
            add(new StringTid("2"));
            add(new StringTid("3"));
            add(new StringTid("4"));
        }};
        final Set<String> affectedSrcEntities = new HashSet<String>() {{
            add(TABLE1);
            add(TABLE2);
            add(TABLE3);
            add(TABLE4);
        }};

        final RelTransferWindow relTransferWindow = new RelTransferWindow(dataPoolBridge, tidList, affectedSrcEntities,
                entitiesDag);

        final EtlPlanDag<RelDb, RelDb> plan = new EtlPlanDag<>();
        plan.addVertex(newFunctor(newFunc(TABLE1, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE2, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE3, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE4, relTransferWindow)));
        winGeneratorResult.add(relTransferWindow);
        planGeneratorResult.put(relTransferWindow, plan);
    }

    private void generateTestWin2(EntitiesDag entitiesDag, DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge) {
        final List<StringTid> tidList = new ArrayList<StringTid>() {{
            add(new StringTid("5"));
            add(new StringTid("6"));
            add(new StringTid("7"));
        }};
        final Set<String> affectedSrcEntities = new HashSet<String>() {{
            add(TABLE1);
            add(TABLE2);
            add(TABLE3);
        }};

        final RelTransferWindow relTransferWindow = new RelTransferWindow(dataPoolBridge, tidList, affectedSrcEntities,
                entitiesDag);

        final EtlPlanDag<RelDb, RelDb> plan = new EtlPlanDag<>();
        plan.addVertex(newFunctor(newFunc(TABLE1, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE2, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE3, relTransferWindow)));
        winGeneratorResult.add(relTransferWindow);
        planGeneratorResult.put(relTransferWindow, plan);
    }

    private void generateTestWin3(EntitiesDag entitiesDag, DirectedEdge<Pool<RelDb>, Pool<RelDb>> dataPoolBridge) {
        final List<StringTid> tidList = new ArrayList<StringTid>() {{
            add(new StringTid("8"));
            add(new StringTid("9"));
            add(new StringTid("10"));
        }};
        final Set<String> affectedSrcEntities = new HashSet<String>() {{
            add(TABLE2);
            add(TABLE3);
            add(TABLE4);
        }};

        final RelTransferWindow relTransferWindow = new RelTransferWindow(dataPoolBridge, tidList, affectedSrcEntities,
                entitiesDag);

        final EtlPlanDag<RelDb, RelDb> plan = new EtlPlanDag<>();
        plan.addVertex(newFunctor(newFunc(TABLE2, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE3, relTransferWindow)));
        plan.addVertex(newFunctor(newFunc(TABLE4, relTransferWindow)));

        winGeneratorResult.add(relTransferWindow);
        planGeneratorResult.put(relTransferWindow, plan);
    }

    private FKEdge newFkEdge() {
        return createMock(FKEdge.class);
    }

    private TransferFunctor<RelDb, RelDb> newFunctor(final TransferFunc<RelDb, RelDb>... funcs) {
        TransferFunctor<RelDb, RelDb> result = new TransferFunctor<RelDb, RelDb>() {
            @Override
            public Set<TransferFunc<RelDb, RelDb>> apply(final Void input) {
                Set<TransferFunc<RelDb, RelDb>> result = new HashSet<>();
                for (TransferFunc<RelDb, RelDb> func : funcs) {
                    result.add(func);
                }
                return result;
            }
        };

        return result;
    }

    private TransferFunc<RelDb, RelDb> newFunc(final String table, final RelTransferWindow window) {

        expectedCallResult.add(new DirectedEdge<String, TransferWindow<RelDb, RelDb>>(table, window));

        TransferFunc<RelDb, RelDb> result = new TransferFunc<RelDb, RelDb>() {
            @Override
            public Integer apply(final DirectedEdge<RelDb, RelDb> input) {

                actualCallResult.add(new DirectedEdge<String, TransferWindow<RelDb, RelDb>>(table, window));

                return 0;
            }

            @Override
            public String toString() {
                return format("Func({}:{})", window, table);
            }
        };
        return result;
    }

    public static final int DB_OPEN_OPS = 20;

    private final String TABLE1 = "table1";
    private final String TABLE2 = "table2";
    private final String TABLE3 = "table3";
    private final String TABLE4 = "table4";

    private Pool<RelDb> relDbPool;
    private RelDb relDb;

    private Map<TransferWindow<RelDb, RelDb>, EtlPlanDag<RelDb, RelDb>> planGeneratorResult = new HashMap<>();
    private List<TransferWindow<RelDb, RelDb>> winGeneratorResult = new ArrayList<>();

    private List<DirectedEdge<String, TransferWindow<RelDb, RelDb>>> actualCallResult = new ArrayList<>();
    private List<DirectedEdge<String, TransferWindow<RelDb, RelDb>>> expectedCallResult = new ArrayList<>();
}
