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
