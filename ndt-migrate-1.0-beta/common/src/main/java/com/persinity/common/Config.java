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
package com.persinity.common;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;
import static com.persinity.common.invariant.Invariant.assertArg;
import static com.persinity.common.invariant.Invariant.notEmpty;
import static com.persinity.common.invariant.Invariant.notNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.persinity.common.Resource.Accessor;
import com.persinity.common.logging.Log4jLogger;

/**
 * Provides read access to configuration properties
 *
 * @author Doichin Yordanov
 */
public class Config {

    /**
     * @param props
     * @param propsSource
     *         The name of the source that holds the properties
     */
    public Config(final Properties props, final String propsSource) {
        notNull(props);
        notEmpty(propsSource);

        this.props = props;
        this.propsSource = propsSource;
    }

    /**
     * @return the properties source
     */
    public String getPropsSource() {
        return propsSource;
    }

    /**
     * @param key
     * @return String representation of the value corresponding to that key.
     * @throws IllegalArgumentException
     *         if empty key is supplied or empty value is found.
     * @thorws NullPointerException if no key is supplied or no value is found.
     */
    public String getString(final String key) {
        notEmpty(key);
        String val = props.getProperty(key);
        notNull(val, format("Unable to get value for property \"{}\" in \"{}\"", key, propsSource));
        val = val.trim();
        notEmpty(val, format("Unable to get non empty value for property \"{}\" in \"{}\"", key, propsSource));
        return val;
    }

    /**
     * @param key
     * @param defaultVal
     * @return The value corresponding to the given key, or the supplied default value
     * @throws IllegalArgumentException
     *         if empty value is found.
     * @thorws NullPointerException if no value is found, but key is present
     */
    public String getStringDefault(final String key, final String defaultVal) {
        return props.containsKey(key) ? getString(key) : defaultVal;
    }

    /**
     * @param key
     * @return Integer representation of the value corresponding to that key.
     * @throws IllegalArgumentException
     *         if empty key is supplied or the found value is not an integer.
     * @thorws NullPointerException if no key is supplied or no value is found.
     */
    public int getInt(final String key) {
        final String strVal = getString(key);
        try {
            return Integer.valueOf(strVal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    format("Expected an integer for property \"{}\" in \"{}\", found \"{}\"", key, propsSource,
                            strVal));
        }
    }

    /**
     * @param key
     * @return Integer representation of the value corresponding to that key.
     * @throws IllegalArgumentException
     *         if empty key is supplied or the found value is not positive integer.
     * @thorws NullPointerException if no key is supplied or no value is found.
     */
    public int getPositiveInt(final String key) {
        final int result = getInt(key);
        assertArg(result > 0, "Expected positive integer for property \"{}\" in \"{}\", found \"{}\"", key, propsSource,
                result);
        return result;
    }

    /**
     * @param key
     * @return Long integer representation of the value corresponding to that key.
     * @throws IllegalArgumentException
     *         if empty key is supplied or the found value is not an long integer.
     * @thorws NullPointerException if no key is supplied or no value is found.
     */
    public long getLong(final String key) {
        final String strVal = getString(key);
        try {
            return Long.valueOf(strVal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    format("Expected a long integer for property \"{}\" in \"{}\", found \"{}\"", key, propsSource,
                            strVal));
        }
    }

    /**
     * @param key
     * @return Long integer representation of the value corresponding to that key.
     * @throws IllegalArgumentException
     *         if empty key is supplied or the found value is not positive long integer.
     * @thorws NullPointerException if no key is supplied or no value is found.
     */
    public long getPositiveLong(final String key) {
        final long result = getLong(key);
        assertArg(result > 0, "Expected positive long integer for property \"{}\" in \"{}\", found \"{}\"", key,
                propsSource, result);
        return result;
    }

    /**
     * Returns Boolean representation of the value corresponding to that key.
     *
     * @param key
     * @return {@code true} if the value is "true", otherwise returns {@code false}
     * @throws IllegalArgumentException
     *         if empty key is supplied or empty value is found.
     * @thorws NullPointerException if no key is supplied or no value is found.
     */
    public boolean getBoolean(final String key) {
        return Boolean.parseBoolean(getString(key));
    }

    /**
     * @param key
     * @param defaultVal
     * @return The value corresponding to the given key, or the supplied default value
     * @throws IllegalArgumentException
     *         if empty value is found.
     * @thorws NullPointerException if no value is found, but key is present
     */
    public boolean getBooleanDefault(final String key, final boolean defaultVal) {
        return props.containsKey(key) ? getBoolean(key) : defaultVal;
    }

    /**
     * Loads properties from the given file name using {@code ClassLoader#getSystemResourceAsStream(String)}
     *
     * @param propertyFileName
     * @return
     */
    public static Properties loadPropsFrom(final String propertyFileName) {
        notEmpty(propertyFileName);
        final InputStream configStream = ClassLoader.getSystemResourceAsStream(propertyFileName);
        if (configStream == null) {
            throw new RuntimeException(format("Property file not found: {}", propertyFileName));
        }
        final Properties props = new Properties();
        resource.accessAndAutoClose(new Accessor<InputStream, Void>(configStream, propertyFileName) {
            @Override
            public Void access(final InputStream resource) throws Exception {
                props.load(configStream);
                log.debug("Loaded properties from classpath, file: {}", propertyFileName);
                return null;
            }
        });

        return props;
    }

    @Override
    public String toString() {
        if (toString == null) {
            toString = format("{}({}, {})", formatObj(this), propsSource, dumpProperties());
        }
        return toString;
    }

    /**
     * @return dump of properties in format key=value, key=value, ...
     */
    public String dumpProperties() {
        final List<String> keys = new ArrayList<>();
        for (Object key : props.keySet()) {
            keys.add(key.toString());
        }
        Collections.sort(keys);
        return dumpProperties(keys);
    }

    /**
     * @param keys
     *         to dump
     * @return dump of properties in format key=value, key=value, ...
     */
    public String dumpProperties(final Collection<String> keys) {
        notNull(keys);

        StringBuilder sb = new StringBuilder();
        for (final String key : keys) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(key).append(" = ");
            if (props.containsKey(key)) {
                sb.append('"').append(props.getProperty(key)).append('"');
            } else {
                sb.append("null");
            }
        }
        return sb.toString();
    }

    private final Properties props;
    private final String propsSource;
    private String toString;

    private static final Resource resource = new Resource();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(Config.class));
}
