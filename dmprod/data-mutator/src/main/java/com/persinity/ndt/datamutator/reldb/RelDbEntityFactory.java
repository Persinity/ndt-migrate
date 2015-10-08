/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.reldb;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.collection.CollectionUtils.implode;
import static com.persinity.common.db.RelDbUtil.warmUpCache;
import static com.persinity.common.db.metainfo.BufferedSchema.WARMUP_FKS;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.assertState;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.persinity.common.db.ProxyRelDb;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SqlStrategy;
import com.persinity.common.db.SqlUtil;
import com.persinity.common.db.Trimmer;
import com.persinity.common.db.metainfo.And;
import com.persinity.common.db.metainfo.Col;
import com.persinity.common.db.metainfo.FKEdge;
import com.persinity.common.db.metainfo.ProxySchema;
import com.persinity.common.db.metainfo.Schema;
import com.persinity.common.db.metainfo.constraint.FK;
import com.persinity.common.db.metainfo.constraint.PK;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.load.EntityBase;
import com.persinity.ndt.datamutator.load.EntityFactory;
import com.persinity.ndt.datamutator.load.EntityPoolUtil;
import com.persinity.ndt.datamutator.load.EntitySession;
import com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo;
import com.persinity.ndt.dbagent.topology.NormalizedSchemaGraphBuilder;
import com.persinity.ndt.dbagent.topology.SchemaGraphBuilder;
import com.persinity.ndt.transform.EntitiesDag;

/**
 * Implementation over RelDb, Schema, SqlStrategy
 *
 * @author Ivan Dachev
 */
public class RelDbEntityFactory implements EntityFactory {
    @Override
    public void init(final Properties dbConfigProps, final String dbConfigSource, final EntityPoolUtil entityPoolUtil) {
        notNull(dbConfigProps);
        notEmpty(dbConfigSource);
        notNull(entityPoolUtil);

        config = new RelDbConfig(dbConfigProps, dbConfigSource);
        db = new ProxyRelDb(config.getDbConfig());
        this.entityPoolUtil = entityPoolUtil;
        trimmer = new Trimmer();
        sqlStrategy = db.getSqlStrategy();
        banTables = new HashSet<>();
    }

    @Override
    public EntitySession createSession() {
        assertState(db != null, "You should call init() before use");
        return new RelDbEntitySession(db, sqlStrategy, this);
    }

    @Override
    public void initSchema() {
        assertState(db != null, "You should call init() before use");

        try {
            db.executeScript(config.getSchemaDropSql());
        } catch (RuntimeException e) {
            log.warn(e, "Exception during schema drop before init.");
        }
        db.executeScript(config.getSchemaInitSql());
        db.commit();
        recreateSchemaMeta();
    }

    @Override
    public void readSchema(final int maxEntitiesToLoad) {
        assertState(db != null, "You should call init() before use");

        if (tables == null) {
            recreateSchemaMeta();
        }

        final List<EntityBase> entities = new ArrayList<>();
        for (final String table : tables) {
            final PK pk = schema.getTablePk(table);
            if (pk == null) {
                log.warning("Skip {} without primary key", table);
                banTable(table);
                continue;
            }

            final String pkColsList = implode(pk.getColumns(), ",", new Function<Col, String>() {
                @Override
                public String apply(final Col col) {
                    return col.getName();
                }
            });

            // TODO add this to SqlStrategy
            final String sql = format("SELECT {} FROM {} WHERE ROWNUM <= {} ORDER BY {}", pkColsList, table,
                    maxEntitiesToLoad, pkColsList);
            try {
                int counter = 0;
                final Iterator<Map<String, Object>> iter = db.executeQuery(sql);
                while (iter.hasNext()) {
                    final Map<String, Object> row = iter.next();
                    final EntityBase entity = constructEntityBase(table, pk, row);
                    entities.add(entity);
                    counter++;
                }
                log.info("Loaded {} entities from {}", counter, table);
            } catch (RuntimeException e) {
                log.warn(e, "Unable to load table entities: {}", table);
            }
        }
        entityPoolUtil.getEntityPool().returnUpdatedEntities(entities);
    }

    @Override
    public void cleanupSchema() {
        assertState(db != null, "You should call init() before use");

        if (tables == null) {
            recreateSchemaMeta();
        }

        // brute force here to cleanup when there is PK/FK relation
        for (int i = 0; i < tables.size(); i++) {
            boolean retry = false;
            for (final String table : tables) {
                final String sql = sqlStrategy.deleteAllStatement(table);
                try {
                    final int deleted = db.executeDmdl(sql);
                    if (deleted > 0) {
                        retry = true;
                    }
                } catch (RuntimeException e) {
                    // silent
                    retry = true;
                }
            }
            if (!retry) {
                break;
            }
        }

        db.commit();
    }

    @Override
    public void dropSchema() {
        assertState(db != null && config != null, "You should call init() before use");

        db.executeScript(config.getSchemaDropSql());
        db.commit();
        recreateSchemaMeta();
    }

    @Override
    public EntityBase createRandomEntity(final long id) {
        assertState(db != null, "You should call init() before use");

        if (tables == null) {
            recreateSchemaMeta();
        }

        EntityBase result = null;
        for (int i = 0; i < tables.size(); i++) {
            final String table = tables.get(random.nextInt(tables.size()));

            if (banTables.contains(table)) {
                continue;
            }

            final Set<FK> fks = schema.getTableFks(table);
            if (fks.size() > 0 && !entityPoolUtil.haveFksEntities(fks)) {
                continue;
            }

            final PK pk = schema.getTablePk(table);
            if (pk == null) {
                log.warning("Do not support table without PK: \"{}\"", table);
                banTable(table);
                continue;
            }

            final Set<Col> pkCols = pk.getColumns();
            final List<Object> values = entityPoolUtil.generatePkValues(table, pkCols, fks, id);

            if (values.size() != 0) {
                result = new RelDbEntityBase(values, table, schema.getTableCols(table), pk, schema.getTableFks(table),
                        entityPoolUtil);
                result.mutate(id);
                break;
            }
        }

        if (result == null) {
            throw new RuntimeException("Unable to generate new entity");
        }

        return result;
    }

    @Override
    public String getConnectionInfo() {
        assertState(db != null, "You should call init() before use");

        return format("DB Connection\n\tURL: {}\n\tUser: {}", config.getDbConfig().getDbUrl(),
                config.getDbConfig().getDbUser());
    }

    @Override
    public String getSchemaInfo() {
        assertState(db != null, "You should call init() before use");

        if (tables == null) {
            recreateSchemaMeta();
        }

        return format("DB Schema\n\tTables: {}", tables.size());
    }

    /**
     * Store to ban table.
     * TODO replace with skip.list config
     * @param table
     */
    public synchronized void banTable(final String table) {
        log.warning("Ban table {}", table);
        banTables.add(table);
    }

    public void findCascadeDeleteEntities(final EntityBase entity, final List<EntityBase> deletePlan) {
        assertArg(entity instanceof RelDbEntityBase);
        final RelDbEntityBase relDbEntity = (RelDbEntityBase) entity;

        final Set<Col> pkCols = relDbEntity.getPk().getColumns();
        final List<Object> pkValues = relDbEntity.getId();
        assertState(pkCols.size() == pkValues.size(), "Expected PK columns/values size match {} == {} for {}", pkCols,
                pkValues, relDbEntity.getTable());

        final Set<FKEdge> inEdges = entitiesDag.incomingEdgesOf(relDbEntity.getTable());
        for (FKEdge fkEdge : inEdges) {
            findFkCascadeDeleteEntities(fkEdge.weight(), relDbEntity, deletePlan);
        }

        deletePlan.add(relDbEntity);
    }

    private void findFkCascadeDeleteEntities(final FK fk, final RelDbEntityBase relDbEntity,
            final List<EntityBase> deletePlan) {
        final Set<Col> dstCols = fk.getDstConstraint().getColumns();

        final String dstColsList = implode(dstCols, ",", new Function<Col, String>() {
            @Override
            public String apply(final Col col) {
                return col.getName();
            }
        });

        final PK fkRelPk = schema.getTablePk(fk.getTable());
        if (fkRelPk == null) {
            // TODO should be implemented when support table without PK
            return;
        }

        final String fkRelPksCosList = implode(fkRelPk.getColumns(), ",", new Function<Col, String>() {
            @Override
            public String apply(final Col col) {
                return col.getName();
            }
        });

        final String sql = format("SELECT {} FROM {} WHERE {}", dstColsList, relDbEntity.getTable(),
                new And(SqlUtil.toEqualParams(new ArrayList<>(relDbEntity.getPk().getColumns()))));
        final String sqlToDel = format("SELECT {} FROM {} WHERE {}", fkRelPksCosList, fk.getTable(),
                new And(SqlUtil.toEqualParams(new ArrayList<>(fk.getColumns()))));
        final Iterator<Map<String, Object>> iter = db.executePreparedQuery(sql, relDbEntity.getId());
        while (iter.hasNext()) {
            final Map<String, Object> dstValues = iter.next();
            final List<Object> listValues = new ArrayList<>();
            for (Col col : dstCols) {
                listValues.add(dstValues.get(col.getName()));
            }

            final Iterator<Map<String, Object>> iterToDel = db.executePreparedQuery(sqlToDel, listValues);
            while (iterToDel.hasNext()) {
                final Map<String, Object> row = iterToDel.next();
                final RelDbEntityBase toDelEntity = constructEntityBase(fk.getTable(), fkRelPk, row);
                findCascadeDeleteEntities(toDelEntity, deletePlan);
            }
        }
    }

    public EntitiesDag getEntitiesDag() {
        return entitiesDag;
    }

    public Schema getSchema() {
        return schema;
    }

    public EntityPoolUtil getEntityPoolUtil() {
        return entityPoolUtil;
    }

    private synchronized void recreateSchemaMeta() {
        schema = ProxySchema.newSchema(db, sqlStrategy);
        warmUpCache(schema, WARMUP_FKS);
        tables = new ArrayList<>(schema.getTableNames());
        banTables.clear();
        final SchemaGraphBuilder schemaGraphBuilder = new SchemaGraphBuilder(
                new OracleSchemaInfo(schema, trimmer, sqlStrategy.getMaxNameLength()));
        final NormalizedSchemaGraphBuilder builder = new NormalizedSchemaGraphBuilder(schemaGraphBuilder);
        entitiesDag = builder.buildNormalizedTopology();
    }

    public RelDbEntityBase constructEntityBase(final String table, final PK pk, final Map<String, Object> row) {
        notEmpty(table);
        notNull(pk);
        notEmpty(row);

        final List<Object> pkValues = new ArrayList<>();
        for (final Col col : pk.getColumns()) {
            Object value = row.get(col.getName());
            if (value instanceof Number) {
                value = ((Number) value).longValue();
            } else {
                value = value.toString();
            }
            pkValues.add(value);
        }

        return new RelDbEntityBase(pkValues, table, schema.getTableCols(table), pk, schema.getTableFks(table),
                entityPoolUtil);
    }

    @Override
    public void close() {
        db.close();
    }

    private static final Random random = new Random();

    private RelDbConfig config;
    private RelDb db;
    private Schema schema;
    private Trimmer trimmer;
    private EntitiesDag entitiesDag;
    private SqlStrategy sqlStrategy;
    private ArrayList<String> tables;
    private Set<String> banTables;
    private EntityPoolUtil entityPoolUtil;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelDbEntityFactory.class));
}
