/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbagent.relational;

import static com.persinity.common.collection.CollectionUtils.newTree;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Arrays;

import com.google.common.base.Function;
import com.persinity.common.collection.Tree;
import com.persinity.common.db.RelDb;
import com.persinity.ndt.dbagent.SchemaAgent;
import com.persinity.ndt.dbagent.relational.impl.FkDisableFunctor;
import com.persinity.ndt.dbagent.relational.impl.FkEnableFunctor;
import com.persinity.ndt.dbagent.relational.impl.IntegrityDisableF;
import com.persinity.ndt.dbagent.relational.impl.IntegrityEnableF;
import com.persinity.ndt.dbagent.relational.impl.PlanBuilder;
import com.persinity.ndt.dbagent.topology.NormalizedSchemaGraphBuilder;
import com.persinity.ndt.dbagent.topology.SchemaGraphBuilder;
import com.persinity.ndt.transform.EntitiesDag;
import com.persinity.ndt.transform.RelFunc;

/**
 * @author Doichin Yordanov
 */
public class RelSchemaAgent implements SchemaAgent<Function<RelDb, RelDb>> {

    public RelSchemaAgent(final String ndtUserName, final SchemaInfo schemaInfo, final AgentSqlStrategy sqlStrategy) {
        notEmpty(ndtUserName);
        notNull(schemaInfo);
        notNull(sqlStrategy);

        this.ndtUserName = ndtUserName;
        this.sqlStrategy = sqlStrategy;

        nsgb = new NormalizedSchemaGraphBuilder(new SchemaGraphBuilder(schemaInfo));
        pb = new PlanBuilder();
    }

    @Override
    public Tree<Function<RelDb, RelDb>> breakRefIntegrityCycles() {
        return pb.build(nsgb.getDecycleFks(), new FkDisableFunctor(sqlStrategy));
    }

    @Override
    public EntitiesDag getSchema() {
        final EntitiesDag entitiesDag = nsgb.buildNormalizedTopology();
        return entitiesDag;
    }

    @Override
    public Tree<Function<RelDb, RelDb>> renewRefIntegrityCycles() {
        return pb.build(nsgb.getDecycleFks(), new FkEnableFunctor(sqlStrategy));
    }

    @Override
    public Tree<Function<RelDb, RelDb>> enableIntegrity() {
        // TODO parallelize in mid-tier
        return newTree(Arrays.<Function<RelDb, RelDb>>asList(new IntegrityEnableF(ndtUserName)));
    }

    @Override
    public Tree<Function<RelDb, RelDb>> disableIntegrity() {
        // TODO parallelize in mid-tier
        return newTree(Arrays.<Function<RelDb, RelDb>>asList(new IntegrityDisableF(ndtUserName)));
    }

    @Override
    public Tree<Function<RelDb, RelDb>> umount() {
        return newTree(Arrays.<Function<RelDb, RelDb>>asList(new Function<RelDb, RelDb>() {
            @Override
            public RelDb apply(final RelDb input) {
                return new RelFunc(sqlStrategy.dropPackage(SchemaInfo.SP_NDT_COMMON)).apply(input);
            }
        }));
    }

    private final NormalizedSchemaGraphBuilder nsgb;

    private final PlanBuilder pb;
    private final AgentSqlStrategy sqlStrategy;
    private final String ndtUserName;
}
