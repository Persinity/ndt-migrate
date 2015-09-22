/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common;

import static com.persinity.common.ThreadUtil.sleepSeconds;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.Test;

/**
 * @author Ivan Dachev
 */
public class ConsoleProgressBarTest {
    @Test
    public void testProgress() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ConsoleProgressBar testee = new ConsoleProgressBar(new PrintStream(baos), 1, 3);

        testee.start();

        sleepSeconds(4);

        testee.stop();

        final BufferedReader br = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
        assertThat(br.readLine(), is(".   "));
        assertThat(br.readLine(), is("..  "));
        assertThat(br.readLine(), is("... "));
    }

    @Test(expected = NullPointerException.class)
    public void testNullPrintStream() throws Exception {
        new ConsoleProgressBar(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroTickSeconds() throws Exception {
        new ConsoleProgressBar(new PrintStream(new ByteArrayOutputStream()), 0, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTickSeconds() throws Exception {
        new ConsoleProgressBar(new PrintStream(new ByteArrayOutputStream()), -1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroProgressLength() throws Exception {
        new ConsoleProgressBar(new PrintStream(new ByteArrayOutputStream()), 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeProgressLength() throws Exception {
        new ConsoleProgressBar(new PrintStream(new ByteArrayOutputStream()), 1, -1);
    }
}