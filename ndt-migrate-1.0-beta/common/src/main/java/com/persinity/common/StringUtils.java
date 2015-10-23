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

import static com.persinity.common.invariant.Invariant.notNull;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * TODO rename it to StringUtil as all other XyzUtil classes are with this notion.
 *
 * @author Ivo Yanakiev
 */
public class StringUtils {

    /**
     * Fills given template with values.
     *
     * @param template
     *         non-null {@link String} template containing placeholders: '{@value #PLACEHOLDER}'
     * @param args
     *         non-null list of arguments to be populated in the template
     * @return
     */
    public static String format(String template, Object... args) {

        assert template != null : "Argument template is null.";
        assert args != null : "Argument args is null.";

        StringBuilder result = new StringBuilder();

        int currentPlaceholderBeginIndex;
        int previousPlaceholderEndIndex = 0;

        for (Object arg : args) {

            currentPlaceholderBeginIndex = template.indexOf(PLACEHOLDER, previousPlaceholderEndIndex);

            if (currentPlaceholderBeginIndex >= 0) {

                result.append(template.subSequence(previousPlaceholderEndIndex, currentPlaceholderBeginIndex));
                result.append((arg != null) ? arg.toString() : NULL_VALUE);
                previousPlaceholderEndIndex = currentPlaceholderBeginIndex + PLACEHOLDER.length();

            } else {
                throw new IllegalArgumentException(format("There are no placeholders left for the remaining arguments."
                        + "Template: {} args[{}]: {}", template, args.length, Arrays.toString(args)));
            }
        }

        CharSequence remainingTemplateSymbols = template.subSequence(previousPlaceholderEndIndex, template.length());
        result.append(remainingTemplateSymbols);

        String resultString = result.toString();

        if (template.indexOf(PLACEHOLDER, previousPlaceholderEndIndex) >= 0) {
            throw new IllegalArgumentException(
                    format("There are placeholders left after filling the arguments, result: {}", resultString));
        }

        return resultString;
    }

    /**
     * Extract last segments from a string separated by given char.
     *
     * @param str
     *         to extract last segments
     * @param sep
     *         of the segments
     * @param segments
     *         to extract
     * @return
     */
    public static String extractLastSegments(final String str, final char sep, final int segments) {
        if (segments <= 0) {
            return str;
        }
        int index = str.length();
        for (int i = 0; index != -1 && i < segments; i++) {
            index = str.lastIndexOf(sep, index - 1);
        }
        if (index == -1) {
            index = 0;
        } else {
            index += 1;
        }
        return str.substring(index, str.length());
    }

    /**
     * @param obj
     *         to format
     * @return ObjectSimpleClass@hashcode_in_hex
     */
    public static String formatObj(final Object obj) {
        return format("{}@{}", obj.getClass().getSimpleName(), Integer.toHexString(obj.hashCode()));
    }

    public static String hashString(Object o) {
        notNull(o);
        int hash = o.hashCode();
        final String hashString = Integer.valueOf(hash).toString();
        return hash < 0 ? hashString.replace("-", "_") : hashString;
    }

    private final static String PLACEHOLDER = "{}";
    private final static String NULL_VALUE = "null";

    private static final Logger log = Logger.getLogger(StringUtils.class);
}
