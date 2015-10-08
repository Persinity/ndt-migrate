/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

/**
 * Utils to get build-info.properties.
 *
 * @author Ivan Dachev
 */
public class BuildInfo {
    /**
     * @return BuildInfo instance
     */
    public synchronized static BuildInfo getInstance() {
        if (singleton == null) {
            singleton = new BuildInfo();
        }
        return singleton;
    }

    /**
     * Constructs a build config and on first instance load the properties.
     */
    private BuildInfo() {
        config = new Config(Config.loadPropsFrom(DEFAULT_BUILD_CONFIG_NAME), DEFAULT_BUILD_CONFIG_NAME);
    }

    /**
     * @return product version
     */
    public String getProductVersion() {
        return config.getString(PRODUCT_VERSION_KEY);
    }

    /**
     * @return pom version
     */
    public String getPomVersion() {
        return config.getString(POM_VERSION_KEY);
    }

    /**
     * @return build timestamp
     */
    public String getBuildTimestamp() {
        return config.getString(BUILD_TIMESTAMP_KEY);
    }

    /**
     * @return git commit id
     */
    public String getGitCommitId() {
        return config.getString(GIT_COMMIT_ID_KEY);
    }

    /**
     * @return git commit id abbreviation
     */
    public String getGitCommitIdAbbrev() {
        return config.getString(GIT_COMMIT_ID_ABBREV_KEY);
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({})", formatObj(this), config.dumpProperties());
        }
        return toString;
    }

    private static final String DEFAULT_BUILD_CONFIG_NAME = "build-info.properties";
    private static final String PRODUCT_VERSION_KEY = "product.version";
    private static final String POM_VERSION_KEY = "pom.version";
    private static final String BUILD_TIMESTAMP_KEY = "build.timestamp";
    private static final String GIT_COMMIT_ID_KEY = "git.commit.id";
    private static final String GIT_COMMIT_ID_ABBREV_KEY = "git.commit.id.abbrev";

    private final Config config;

    private String toString;

    private static BuildInfo singleton;
}
