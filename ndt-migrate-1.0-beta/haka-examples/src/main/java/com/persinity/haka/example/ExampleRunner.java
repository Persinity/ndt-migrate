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

package com.persinity.haka.example;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.invariant.Invariant.assertArg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author Ivo Yanakiev
 */
public class ExampleRunner {

    private class TestDataStatistics {

        private TestDataStatistics(int files, int dirs, long sizeInMB) {
            this.files = files;
            this.dirs = dirs;
            this.sizeInMB = sizeInMB;
        }

        public final int files;
        public final int dirs;
        public final long sizeInMB;

    }

    public static void main(String[] args) throws Exception {
        ExampleRunner runner = new ExampleRunner(args);
        runner.run();
    }

    public ExampleRunner(String[] args) {
        loadArguments(args);
    }

    public void run() throws Exception {

        Callable<Map<String, Integer>> example = buildExampleRunner();

        TestDataStatistics testDataStats = calculateTestDataStats(rootDirFile);

        String testParameters = formatTestParameters(rootDirFile, example.toString(), testDataStats);

        log.info(testParameters);
        log.info("\nCalculating...\n");

        long start = System.currentTimeMillis();
        Map<String, Integer> wordCount = example.call();
        long stop = System.currentTimeMillis();

        String runStatistics = formatResultStatistics(stop - start, wordCount);

        log.info(runStatistics);
    }

    private void loadArguments(String[] args) {

        Namespace ns = parseArguments(args);

        type = ns.getString("example");

        String rootDirFileString = ns.get("root_dir");
        rootDirFile = new File(rootDirFileString);

        assertArg(rootDirFile.isDirectory(), "Unable to find root dir: {}", rootDirFile.getAbsolutePath());

        maxParallelFiles = ns.getInt("max_parallel_files");

        timeoutInSeconds = ns.getInt("timeout");

        hakaHost = ns.getString("haka_ip");
        hakaPort = ns.getInt("haka_port");
    }

    private Namespace parseArguments(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("HakaTest").defaultHelp(true)
                .description("Haka test.");

        parser.addArgument("-e", "--example").required(true).type(String.class).choices("s", "h", "r")
                .help("Example type: S for single thread, H for HAKA local, R for HAKA remote");

        parser.addArgument("-r", "--root-dir").required(true).type(String.class)
                .help("Root directory that contains test files.");

        parser.addArgument("-m", "--max-parallel-files").required(false).setDefault(10).type(Integer.class)
                .help("Number of files that are processed in one job execution.");

        parser.addArgument("-t", "--timeout").required(false).setDefault(600).type(Integer.class)
                .help("Timeout for HAKA example in seconds.");

        parser.addArgument("-i", "--haka-ip").required(false).setDefault("127.0.0.1").type(String.class)
                .help("Haka ip.");

        parser.addArgument("-p", "--haka-port").required(false).setDefault(5242).type(Integer.class).help("Haka port.");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        return ns;
    }

    private TestDataStatistics calculateTestDataStats(File rootDir) {

        Stack<File> nodes = new Stack<>();
        nodes.push(rootDir);

        int files = 0;
        int dirs = 0;
        long size = 0L;

        while (!nodes.isEmpty()) {
            File node = nodes.pop();
            if (node.isDirectory()) {
                dirs++;
                for (File childNodes : node.listFiles()) {
                    nodes.push(childNodes);
                }
            }
            if (node.isFile()) {
                files++;
                size += node.length();
            }
        }

        return new TestDataStatistics(files, dirs, size / (1024L * 1024L));
    }

    private String formatTestParameters(File rootDirFile, String exampleType, TestDataStatistics testDataStats) {

        StringBuilder result = new StringBuilder();

        result.append('\n');
        result.append(format("Type: {} \n", exampleType));
        result.append(format("Root dir: {} \n", rootDirFile.getAbsolutePath()));
        result.append(format("Total files: {} \n", testDataStats.files));
        result.append(format("Total dirs: {} \n", testDataStats.dirs));
        result.append(format("Total size: {} MB ({} GB)\n", testDataStats.sizeInMB,
                String.format("%.3f", testDataStats.sizeInMB / 1024F)));

        return result.toString();

    }

    private String formatResultStatistics(long interval, Map<String, Integer> wordCount) {

        StringBuilder result = new StringBuilder();

        result.append('\n');
        result.append(format("Word count: {} \n", formatWordCount(wordCount)));

        long seconds = (interval / 1000L) % 60L;
        long minutes = (interval / 60000L) % 60L;
        long hours = interval / 3600000L;

        result.append('\n');
        result.append(format("Total time: {} hours, {} minutes, {} seconds \n", hours, minutes, seconds));

        return result.toString();
    }

    public String formatWordCount(Map<String, Integer> wordCount) {

        StringBuilder sb = new StringBuilder();
        ArrayList<String> keys = new ArrayList<>(wordCount.keySet());
        Collections.sort(keys);

        int index = 0;

        for (String key : keys) {

            if (index % WORDS_PER_LINE == 0) {
                sb.append('\n');
            } else {
                sb.append(' ');
            }

            index++;
            sb.append(key).append(": ").append(wordCount.get(key)).append(';');
        }
        return sb.toString();
    }

    private Callable<Map<String, Integer>> buildExampleRunner() {
        switch (type.toLowerCase()) {
        case "s":
            return buildWordCountSerial();
        case "h":
            return buildWordCountEmbeddedMain();
        case "r":
            return buildWordCountRemoteMain();
        default:
            throw new IllegalArgumentException(format("Illegal argument {} for example type.", type));
        }
    }

    private Callable<Map<String, Integer>> buildWordCountSerial() {
        return new WordCountSerial(rootDirFile, maxParallelFiles);
    }

    private Callable<Map<String, Integer>> buildWordCountEmbeddedMain() {
        return new WordCountEmbeddedMain(rootDirFile, maxParallelFiles, timeoutInSeconds);
    }

    private Callable<Map<String, Integer>> buildWordCountRemoteMain() {
        return new WordCountRemoteMain(rootDirFile, maxParallelFiles, timeoutInSeconds, hakaHost, hakaPort);
    }

    private String type;
    private File rootDirFile;
    private Integer maxParallelFiles;
    private int timeoutInSeconds;
    private String hakaHost;
    private Integer hakaPort;

    private static int WORDS_PER_LINE = 4;

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(ExampleRunner.class));
}
