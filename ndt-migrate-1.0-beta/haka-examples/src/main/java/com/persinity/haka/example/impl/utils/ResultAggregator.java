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
package com.persinity.haka.example.impl.utils;

import static com.persinity.common.invariant.Invariant.assertArg;

import java.io.Serializable;
import java.util.Map;

/**
 * Utility that aggregates word count results.
 * <p/>
 * Created by Ivo Yanakiev on 5/20/15.
 */
public class ResultAggregator implements Serializable {

    public void aggregate(Map<String, Integer> result, Map<String, Integer> slice) {

        assertArg(result != null, "Parameter 'result' must not be null.");
        assertArg(slice != null, "Parameter 'slice' must not be null.");

        for (String sliceKey : slice.keySet()) {

            int sliceValue = slice.get(sliceKey);

            if (result.containsKey(sliceKey)) {

                int count = result.get(sliceKey) + sliceValue;
                result.put(sliceKey, count);

            } else {
                result.put(sliceKey, sliceValue);
            }
        }
    }

    public void aggregate(Map<String, Integer> result, String word) {

        assert result != null : "Parameter 'result' must not be null.";
        assert word != null : "Parameter 'word' must not be null.";

        if (result.containsKey(word)) {
            result.put(word, result.get(word) + 1);
        } else {
            result.put(word, 1);
        }
    }
}
