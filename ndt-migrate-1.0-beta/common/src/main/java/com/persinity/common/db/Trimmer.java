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
package com.persinity.common.db;

import org.apache.log4j.Logger;

import com.persinity.common.StringUtils;
import com.persinity.common.invariant.Invariant;

/**
 * DB object name trimmer. Thread-safe.
 *
 * @author Doichin Yordanov
 */
public class Trimmer {

    /**
     * Trims candidate object name to fit in given size. If the candidate does not fit, cuts it and gives it a hash suffix.
     * The suffix follows the {@link String} hashCode contract and is the same across JVMs and restarts.
     *
     * @param candidateName
     * @param size
     * @return
     */
    public String trim(final String candidateName, final int size) {
        Invariant.assertArg(size > HASHCODE_LENGTH + 1);
        String result = candidateName;
        if (candidateName.length() > size) {
            final String uniqueSuffix = StringUtils.hashString(candidateName);
            final int trimLength = size - uniqueSuffix.length();
            if (trimLength < 1) {
                log.error("Unable to name object after " + candidateName + " using unique suffix " + uniqueSuffix);
                throw new IllegalArgumentException("Unsupported schema!");
            }

            result = candidateName.substring(0, trimLength) + uniqueSuffix;
            if (log.isDebugEnabled()) {
                log.debug("Renamed " + candidateName + " to " + result + " to fit in " + size);
            }
            assert result.length() <= size;
        }
        return result;

    }

    private static final Logger log = Logger.getLogger(Trimmer.class);
    public static final int HASHCODE_LENGTH = 9;
}
