/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.haka.impl.actor;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import scala.concurrent.duration.Duration;

/**
 * Haka node to be created as embedded or from main method for single/cluster node.
 *
 * @author Ivan Dachev
 */
public class HakaNode {
    public static final String ACTOR_SYSTEM_NAME = "haka";
    public static final String DEFAULT_NODE_ID = "embedded";
    public static final String DEFAULT_CONFIG = "haka-embedded-node.conf";

    public static void main(String... args) throws Exception {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Haka").defaultHelp(true).description("Haka node.");
        parser.addArgument("-i", "--node-id").required(true).help("Haka node ID");
        parser.addArgument("--config").setDefault(DEFAULT_CONFIG).help("Haka config file to use");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final String nodeId = ns.get("node_id");
        assert nodeId != null && nodeId.length() > 0;

        final String config = ns.get("config");

        haka = new HakaNode(nodeId, config);
    }

    public HakaNode() {
        this(DEFAULT_NODE_ID, DEFAULT_CONFIG);
    }

    public HakaNode(String nodeId, String config) {
        system = ActorSystem.create(ACTOR_SYSTEM_NAME, ConfigFactory.load(config));

        try {
            HakaSettings settings = HakaSettings.Provider.SettingsProvider.get(system);

            if (settings.isPoolImplCluster()) {
                system.actorOf(Props.create(ClusterListener.class),
                        ClusterListener.class.getSimpleName() + "-" + nodeId);
            }

            String poolSupervisorName = WorkersSupervisor.class.getSimpleName() + "-" + nodeId;
            ActorRef workersSupervisorRef = system.actorOf(WorkersSupervisor.props(nodeId), poolSupervisorName);

            WorkersSupervisor.waitReady(workersSupervisorRef, settings.getWatchdogPeriod());

            for (String mainJobWorkerClass : settings.getMainJobsWorkers()) {
                try {
                    Class<?> mainJobWorker = Class.forName(mainJobWorkerClass);
                    Method propsMethod = mainJobWorker.getMethod("props", String.class);
                    system.actorOf((Props) propsMethod.invoke(mainJobWorker, poolSupervisorName),
                            mainJobWorker.getSimpleName() + "-" + nodeId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (RuntimeException e) {
            try {
                shutdown();
            } catch (RuntimeException ignored) {
            }
            throw e;
        }
    }

    public static HakaNode getHaka() {
        return haka;
    }

    public ActorSystem getActorSystem() {
        return system;
    }

    public void shutdown() {
        system.shutdown();
        system.awaitTermination(SYSTEM_AWAIT_TERMINATION_TIMEOUT_SECONDS);
    }

    private static HakaNode haka;
    private static final Duration SYSTEM_AWAIT_TERMINATION_TIMEOUT_SECONDS = Duration.create(60, TimeUnit.SECONDS);

    private final ActorSystem system;
}

