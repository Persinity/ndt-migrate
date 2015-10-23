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

package com.persinity.haka.example;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivo Yanakiev
 */
public class WordCountBase {

    protected File getRootDir() {

        URL url = WordCountBase.class.getResource("/test-data/");
        URI uri;

        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new File(uri);
    }

    protected final int TIMEOUT = 60 * 1000;
    protected final int MAX_PARALLEL_FILES = 10;

    @SuppressWarnings({ "unchecked", "SerializableInnerClassWithNonSerializableOuterClass", "serial" })
    protected final Map<String, Integer> EXPECTED = new HashMap<String, Integer>() {
        {
            put("one", 55);
            put("two", 150);
            put("three", 250);
        }
    };
}
