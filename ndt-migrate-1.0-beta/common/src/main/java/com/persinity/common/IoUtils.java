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

import java.io.Closeable;

import org.apache.log4j.Logger;

import com.persinity.common.collection.DirectedEdge;
import com.persinity.common.logging.Log4jLogger;

/**
 * @author Ivo Yanakiev
 */
public class IoUtils {

    public static void silentClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            log.error("Error during silent close: {}", e);
        }
    }

    public static void silentClose(com.persinity.common.db.Closeable... closeables) {
        for (com.persinity.common.db.Closeable closeable : closeables) {
            try {
                if (closeable != null)
                    closeable.close();
            } catch (Exception e) {
                log.error("Error during silent close: {}", e);
            }
        }
    }

    public static void silentClose(AutoCloseable autoCloseable) {
        try {
            autoCloseable.close();
        } catch (Exception e) {
            log.error("Error during silent close: {}", e);
        }
    }

    public static void silentClose(
            DirectedEdge<? extends com.persinity.common.db.Closeable, ? extends com.persinity.common.db.Closeable> dataBridge) {

        if (dataBridge != null) {
            silentClose(dataBridge.src());
            silentClose(dataBridge.dst());
        }
    }

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(IoUtils.class));
}
