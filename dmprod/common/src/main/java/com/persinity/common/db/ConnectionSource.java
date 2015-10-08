/**
 * Copyright (c) 2015 Persinity Inc.
 */

package com.persinity.common.db;

import java.sql.Connection;

/**
 * Provides connections.
 *
 * @author dyordanov
 */
public interface ConnectionSource {
    Connection getConnection();

    void close();
}
