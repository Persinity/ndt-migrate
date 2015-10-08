/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class NdtControllerConfigTest {

    @Before
    public void setUp() throws Exception {
        props = new Properties();
        testee = new NdtControllerConfig(props, props.toString());
    }

    @Test
    public void testGetDbConfigName() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.DB_CONFIG_NAME_KEY, "dbconfig");
        assertThat(testee.getDbConfigName(), is("dbconfig"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetDbConfigName_Null() throws Exception {
        props.clear();
        testee.getDbConfigName();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDbConfigName_Empty() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.DB_CONFIG_NAME_KEY, " ");
        testee.getDbConfigName();
    }

    @Test
    public void testGetViewClassname() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.VIEW_CLASSNAME_KEY, "viewclass");
        assertThat(testee.getViewClassname(), is("viewclass"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetViewClassname_Null() throws Exception {
        props.clear();
        testee.getViewClassname();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetViewClassname_Empty() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.VIEW_CLASSNAME_KEY, " ");
        testee.getViewClassname();
    }

    @Test
    public void testGetMigrateWindowSize() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_WINDOWS_SIZE_KEY, "1");
        assertThat(testee.getMigrateWindowSize(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetMigrateWindowSize_Null() throws Exception {
        props.clear();
        testee.getMigrateWindowSize();
    }

    @Test
    public void testGetTransformWindowSize() {
        props.put(NdtControllerConfig.ETL_WINDOWS_SIZE_KEY, "1");
        assertThat(testee.getTransformWindowSize(), is(NdtControllerConfig.MIGRATE_TO_TRANSF_WIN_SIZE_COEF));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMigrateWindowSize_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_WINDOWS_SIZE_KEY, "0");
        testee.getMigrateWindowSize();
    }

    @Test
    public void testGetEtlInstructionSize() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_INSTRUCTION_SIZE_KEY, "1");
        assertThat(testee.getEtlInstructionSize(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetEtlInstructionSize_Null() throws Exception {
        props.clear();
        testee.getEtlInstructionSize();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEtlInstructionSize_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_INSTRUCTION_SIZE_KEY, "0");
        testee.getEtlInstructionSize();
    }

    @Test
    public void testGetEtlWindowCheckIntervalSeconds() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_WINDOW_CHECK_INTERVAL_SECONDS_KEY, "1");
        assertThat(testee.getEtlWindowCheckIntervalSeconds(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetEtlWindowCheckIntervalSeconds_Null() throws Exception {
        props.clear();
        testee.getEtlWindowCheckIntervalSeconds();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEtlWindowCheckIntervalSeconds_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_WINDOW_CHECK_INTERVAL_SECONDS_KEY, "0");
        testee.getEtlWindowCheckIntervalSeconds();
    }

    @Test
    public void testGetEtlMetricsReportingIntervalSeconds() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_METRICS_REPORTING_INTERVAL_SECONDS_KEY, "1");
        assertThat(testee.getEtlMetricsReportingIntervalSeconds(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetEtlMetricsReportingIntervalSeconds_Null() throws Exception {
        props.clear();
        testee.getEtlMetricsReportingIntervalSeconds();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEtlMetricsReportingIntervalSeconds_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.ETL_METRICS_REPORTING_INTERVAL_SECONDS_KEY, "0");
        testee.getEtlMetricsReportingIntervalSeconds();
    }

    @Test
    public void testGetHakaEnable() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.HAKA_ENABLE_KEY, "true");
        assertTrue(testee.getHakaEnable());
    }

    @Test
    public void testGetHakaEnable_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.HAKA_ENABLE_KEY, "0");
        assertFalse(testee.getHakaEnable());
    }

    @Test
    public void testGetHakaTimeoutSeconds() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.HAKA_TIMEOUT_SECONDS_KEY, "1");
        assertThat(testee.getHakaTimeoutSeconds(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetHakaTimeoutSeconds_Null() throws Exception {
        props.clear();
        testee.getHakaTimeoutSeconds();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHakaTimeoutSeconds_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.HAKA_TIMEOUT_SECONDS_KEY, "0");
        testee.getHakaTimeoutSeconds();
    }

    @Test
    public void testGetDbAgentClogGcIntervalSeconds() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.DBAGENT_CLOG_GC_INTERVAL_SECONDS_KEY, "1");
        assertThat(testee.getDbAgentClogGcIntervalSeconds(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void testGetDbAgentClogGcIntervalSeconds_Null() throws Exception {
        props.clear();
        testee.getDbAgentClogGcIntervalSeconds();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDbAgentClogGcIntervalSeconds_Invalid() throws Exception {
        props.clear();
        props.put(NdtControllerConfig.DBAGENT_CLOG_GC_INTERVAL_SECONDS_KEY, "0");
        testee.getDbAgentClogGcIntervalSeconds();
    }

    private NdtControllerConfig testee;
    private Properties props;
}