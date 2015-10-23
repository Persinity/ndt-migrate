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
