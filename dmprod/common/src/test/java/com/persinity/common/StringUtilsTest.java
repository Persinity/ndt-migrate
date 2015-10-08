package com.persinity.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ivo Yanakiev
 */
public class StringUtilsTest {

    @Test
    public void testPlainString() {
        String result = StringUtils.format("1 2 3");
        Assert.assertEquals("1 2 3", result);
    }

    @Test
    public void testDifferentObjects() {
        String result = StringUtils.format(" {} {} {} ", 1, "2", '3');
        Assert.assertEquals(" 1 2 3 ", result);
    }

    @Test
    public void testDifferentObjectsWithTrimmedTemplate() {
        String result = StringUtils.format("{} {} {}", 1, "2", '3');
        Assert.assertEquals("1 2 3", result);
    }

    @Test
    public void testMinimalTemplate() {
        String result = StringUtils.format(" {}{}{} ", 1, "2", '3');
        Assert.assertEquals(" 123 ", result);
    }

    @Test
    public void testMinimalTrimmedTemplate() {
        String result = StringUtils.format("{}{}{}", 1, "2", '3');
        Assert.assertEquals("123", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlaceholders() {
        StringUtils.format(" test ", 1);
    }

    @Test
    public void testMultipleBrackets() {
        String result = StringUtils.format(" {{}} ", 1);
        Assert.assertEquals(" {1} ", result);
    }

    @Test
    public void testMultipleBracketsWithTrimmedTemplate() {
        String result = StringUtils.format("{{}}", 1);
        Assert.assertEquals("{1}", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultiplePlaceholders() {
        StringUtils.format(" {} {} ", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyString() {
        StringUtils.format("", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPlaceholdersLeft() {
        StringUtils.format(" {} {} {} ", 1);
    }

    @Test
    public void testPlaceholderInArgumentsValue() {
        String result = StringUtils.format("test {} test", "opaaa {} test");
        Assert.assertEquals("test opaaa {} test test", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test() {
        StringUtils.format("{}{}", 1);
    }

    @Test
    public void testExtractLastSegments() throws Exception {
        String res;

        res = StringUtils.extractLastSegments("", '-', 0);
        assertThat(res, is(""));

        res = StringUtils.extractLastSegments("", '-', 1);
        assertThat(res, is(""));

        res = StringUtils.extractLastSegments("", '-', 10);
        assertThat(res, is(""));

        res = StringUtils.extractLastSegments("-", '-', 0);
        assertThat(res, is("-"));

        res = StringUtils.extractLastSegments("-", '-', 1);
        assertThat(res, is(""));

        res = StringUtils.extractLastSegments("a-", '-', 1);
        assertThat(res, is(""));

        res = StringUtils.extractLastSegments("-a", '-', 1);
        assertThat(res, is("a"));

        res = StringUtils.extractLastSegments("-a-b", '-', 1);
        assertThat(res, is("b"));

        res = StringUtils.extractLastSegments("-a-b", '-', 2);
        assertThat(res, is("a-b"));

        res = StringUtils.extractLastSegments("-a-b", '-', 3);
        assertThat(res, is("-a-b"));

        res = StringUtils.extractLastSegments("0-134-223-3e34-43f-567", '-', 3);
        assertThat(res, is("3e34-43f-567"));
    }

    @Test(expected = NullPointerException.class)
    public void testHashString_InvalidInput() {
        StringUtils.hashString(null);
    }

    @Test
    public void testHashString() {
        final Object o = new Object() {
            @Override
            public int hashCode() {
                return 1;
            }
        };
        Assert.assertEquals("1", StringUtils.hashString(o));

        final Object o1 = new Object() {
            @Override
            public int hashCode() {
                return -1;
            }
        };
        Assert.assertEquals("_1", StringUtils.hashString(o1));
    }

}
