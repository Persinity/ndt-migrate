/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common.logging;

import org.apache.log4j.pattern.NameAbbreviator;

/**
 * Shorten the package name in Object.toSting()
 *
 * @author Ivan Dachev
 */
public class LogUtil {

    /**
     * Check the Log4J
     * http://svn.apache.org/viewvc/logging/log4j/trunk/src/main/java/org/apache/log4j/pattern/NameAbbreviator
     * .java?view=markup
     */
    public final static String FORMATTING_PATTERN = "1.";

    public final static NameAbbreviator abbreviator = NameAbbreviator.getAbbreviator(FORMATTING_PATTERN);

    /**
     * Format com.persinity.common.LogUtil@3b39d369 to c.p.c.LogUtil@3b39d369
     *
     * @param objToStr
     * @return
     */
    public static String formatPackageName(final String objToStr) {
        if (objToStr == null) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer(objToStr);
        abbreviator.abbreviate(0, sb);
        return sb.toString();
    }
}
