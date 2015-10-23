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

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ivo Yanakiev
 */
public class SerialWordCountTest extends WordCountBase {

    @Before
    public void setUp() {
        wordCountSerial = new WordCountSerial(getRootDir(), 10);
    }

    @Test
    public void testWordCount() {
        Map<String, Integer> result = wordCountSerial.call();
        Assert.assertEquals(EXPECTED, result);
    }

    private WordCountSerial wordCountSerial;
}
