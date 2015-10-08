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
