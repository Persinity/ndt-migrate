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

package com.persinity.ndt.datamutator;

import com.persinity.common.invariant.Invariant;

/**
 * @author Ivo Yanakiev
 */
public enum LoadType {
    TIME("i"), TRANSACTIONS("r");

    LoadType(String value) {
        Invariant.notNull(value);
        this.value = value;
    }

    public String getUpperCaseValue() {
        return value.toUpperCase();
    }

    public String getLowerCaseValue() {
        return value.toLowerCase();
    }

    public static LoadType getByString(String value) {

        for (LoadType loadType : values()) {
            if (loadType.getLowerCaseValue().equalsIgnoreCase(value)) {
                return loadType;
            }
        }
        throw new RuntimeException("Invalid load type: " + value);
    }

    private final String value;
}
