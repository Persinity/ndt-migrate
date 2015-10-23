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
package com.persinity.ndt.datamutator.reldb;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.persinity.common.logging.Log4jLogger;

/**
 * Constructs Objects from RelDb type.
 *
 * @author Ivan Dachev
 */
public class RelDbTypeFactory {
    public Object formatValue(final String colType, final long valueLong, String valueString) {
        valueString = valueString == null || valueString.length() == 0 ? "" + valueLong : valueString;
        final String colTypeLowerCase = colType.toLowerCase();
        Object value = null;
        if (colTypeLowerCase.startsWith("varchar") || colTypeLowerCase.startsWith("char")) {
            final int len = getFirstNumber(colType);
            if (len > 0 && valueString.length() > len) {
                value = valueString.substring(0, len);
            } else {
                value = valueString;
            }
        } else if (colTypeLowerCase.startsWith("clob")) {
            value = valueString;
        } else if (colTypeLowerCase.startsWith("number")) {
            final int len = getFirstNumber(colType);
            if (len == 1) {
                value = valueLong % 2 == 0;
            } else if (len > 1 && len <= 10) {
                value = (int) valueLong;
            } else if (len > 10) {
                value = valueLong;
            } else {
                value = 0;
            }
        } else if (colTypeLowerCase.startsWith("float")) {
            value = (double) valueLong;
        } else if (colTypeLowerCase.startsWith("timestamp")) {
            value = new Timestamp(System.currentTimeMillis() - valueLong);
        } else {
            log.warning("No Java type for RelDB type: {}", colType);
        }

        // TODO implement more types

        return value;
    }

    static int getFirstNumber(final String colType) {
        final int indx = colType.indexOf('(');
        if (indx == -1) {
            return 0;
        }
        final String str = colType.substring(indx + 1);
        Matcher m = NUMBER_PATTERN.matcher(str);
        if (m.find()) {
            return Integer.parseInt(m.group());
        }
        return 0;
    }

    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+");

    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(RelDbTypeFactory.class));
}
