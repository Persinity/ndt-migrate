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

package com.persinity.ndt.datamutator.hibernate;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.fp.FunctionUtil.executeAndCaptureSysOut;
import static com.persinity.common.invariant.Invariant.assertState;

import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.google.common.base.Function;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.datamutator.load.EntityBase;
import com.persinity.ndt.datamutator.load.EntityFactory;
import com.persinity.ndt.datamutator.load.EntityPoolUtil;
import com.persinity.ndt.datamutator.load.EntitySession;

/**
 * @author Ivo Yanakiev
 */
public class HibernateEntityFactory implements EntityFactory {

    @Override
    public void init(final Properties dbConfigProps, final String dbConfigSource, final EntityPoolUtil entityPoolUtil) {
        config = new HibernateConfig(dbConfigProps, dbConfigSource);
        configuration = config.getHibernateConfiguration();
        sessionFactory = configuration.buildSessionFactory();
        this.entityPoolUtil = entityPoolUtil;
    }

    @Override
    public void readSchema(final int maxEntitiesToLoad) {
        // TODO implement
    }

    @Override
    public EntitySession createSession() {
        assertState(sessionFactory != null, "You should call init() before use");

        return new HibernateEntitySession(sessionFactory.openSession());
    }

    @Override
    public EntityBase createRandomEntity(final long id) {
        switch (random.nextInt(5)) {
        case 0:
            return newPosition(id);
        case 1:
            return newEmployee(id);
        case 2:
            return newDepartment(id);
        case 3:
            return newCostCenter(id);
        case 4:
            return newFunding(id);

        default:
            throw new RuntimeException("Ups!");
        }
    }

    @Override
    public String getConnectionInfo() {
        return format("Hibernate Connection\n\tURL: {}\n\tUser: {}", config.getDbConfig().getDbUrl(),
                config.getDbConfig().getDbUser());
    }

    @Override
    public String getSchemaInfo() {
        return format("Hibernate Schema\n\tTables: {}", getTablesCount());
    }

    @Override
    public void initSchema() {
        assertState(configuration != null, "You should call init() before use");

        // hack to not dump the schema init at System.out
        final String res = executeAndCaptureSysOut(new Function<Void, Void>() {
            @Override
            public Void apply(final Void aVoid) {
                SchemaExport schemaExport = new SchemaExport(configuration);
                schemaExport.create(true, true);
                return null;
            }
        });
        log.info("{}", res);
    }

    @Override
    public void cleanupSchema() {
        // TODO find way in hibernate to cleanup all entities
    }

    @Override
    public void dropSchema() {
        assertState(configuration != null, "You should call init() before use");

        // hack to not dump the schema init at System.out
        final String res = executeAndCaptureSysOut(new Function<Void, Void>() {
            @Override
            public Void apply(final Void aVoid) {
                SchemaExport schemaExport = new SchemaExport(configuration);
                schemaExport.drop(true, true);
                return null;
            }
        });
        log.info("{}", res);
    }

    private int getTablesCount() {
        // TODO find way to get tables count for hibernate
        return 5;
    }

    private Position newPosition(final long id) {
        Position result = new Position();
        result.setId(id);
        result.mutate(id);
        return result;
    }

    private Employee newEmployee(final long id) {
        Employee result = new Employee();
        result.setId(id);
        result.mutate(id);
        return result;
    }

    private Department newDepartment(final long id) {
        Department result = new Department();
        result.setId(id);
        result.mutate(id);
        return result;
    }

    private CostCenter newCostCenter(final long id) {
        CostCenter result = new CostCenter();
        result.setId(id);
        result.mutate(id);
        return result;
    }

    private Funding newFunding(final long id) {
        Funding result = new Funding();
        result.setId(id, id + 1000);
        result.mutate(id);
        return result;
    }

    @Override
    public void close() throws RuntimeException {

    }

    private HibernateConfig config;
    private Configuration configuration;
    private SessionFactory sessionFactory;
    private EntityPoolUtil entityPoolUtil;

    private static final Random random = new Random();

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(HibernateEntityFactory.class));
}
