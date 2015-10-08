/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.dbdiff;

import static com.persinity.common.db.DbConfig.NO_KEY_PREFIX;

import java.util.Collection;

import com.persinity.common.Config;
import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.db.DbConfig;
import com.persinity.common.db.RelDb;
import com.persinity.common.db.SimpleRelDb;
import com.persinity.common.db.Trimmer;
import com.persinity.ndt.dbagent.relational.AgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.SchemaInfo;
import com.persinity.ndt.dbagent.relational.oracle.OracleAgentSqlStrategy;
import com.persinity.ndt.dbagent.relational.oracle.OracleSchemaInfo;
import com.persinity.ndt.dbdiff.rel.JsonTransformEntityStore;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Generates initial DB transformation files that should be validated before final usage.
 *
 * @author Ivan Dachev
 */
public class DbDiff {
    public static final String GENERATOR_MIGRATE_TYPE = "migrate";
    public static final String GENERATOR_DEFAULT_TYPE = GENERATOR_MIGRATE_TYPE;

    public static void main(final String... args) throws Exception {
        final ArgumentParser parser = ArgumentParsers.newArgumentParser("DbDiff").defaultHelp(true)
                .description("Db diff tool.");
        parser.addArgument("output-file").required(true)
                .help("Output file to store DB diff represented as transformation info");
        parser.addArgument("--generator-type").setDefault(GENERATOR_DEFAULT_TYPE).choices(GENERATOR_MIGRATE_TYPE)
                .help("Transformation type to generate info - default: " + GENERATOR_DEFAULT_TYPE);

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final String outputFile = ns.get("output_file");
        final String genType = ns.get("generator_type");

        SchemaDiffGenerator schemaDiffGenerator = null;
        if (GENERATOR_MIGRATE_TYPE.equals(genType.toLowerCase())) {
            schemaDiffGenerator = new MigrateSchemaDiffGenerator();
        } else {
            assert false;
        }

        final AgentSqlStrategy sqlStrategy = new OracleAgentSqlStrategy();
        final DirectedEdge<SchemaInfo, SchemaInfo> dbSources = createDbSources(sqlStrategy);

        final Collection<TransformEntity> transformEntities = schemaDiffGenerator.generateDiff(dbSources, sqlStrategy);

        final JsonTransformEntityStore jsonTransformEntityStore = new JsonTransformEntityStore(outputFile);
        jsonTransformEntityStore.saveTransformEntities(transformEntities);
    }

    private static DirectedEdge<SchemaInfo, SchemaInfo> createDbSources(final AgentSqlStrategy sqlStrategy) {
        final DbConfig srcConfig = new DbConfig(Config.loadPropsFrom(NDT_SRC_PROPS_FILE), NDT_SRC_PROPS_FILE,
                NO_KEY_PREFIX);
        final DbConfig dstConfig = new DbConfig(Config.loadPropsFrom(NDT_DST_PROPS_FILE), NDT_DST_PROPS_FILE,
                NO_KEY_PREFIX);

        final RelDb srcDb = new SimpleRelDb(srcConfig);
        final RelDb dstDb = new SimpleRelDb(dstConfig);

        final SchemaInfo srcSchemaInfo = new OracleSchemaInfo(srcDb.metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());
        final SchemaInfo dstSchemaInfo = new OracleSchemaInfo(dstDb.metaInfo(), new Trimmer(),
                sqlStrategy.getMaxNameLength());

        return new DirectedEdge<>(srcSchemaInfo, dstSchemaInfo);
    }

    public static final String NDT_SRC_PROPS_FILE = "ndtsrc.properties";
    public static final String NDT_DST_PROPS_FILE = "ndtdst.properties";
}
