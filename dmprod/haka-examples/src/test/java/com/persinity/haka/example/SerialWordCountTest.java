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
