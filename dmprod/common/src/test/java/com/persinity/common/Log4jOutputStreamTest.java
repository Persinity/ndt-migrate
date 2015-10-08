/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.common;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Doichin Yordanov
 */
public class Log4jOutputStreamTest {

	private static final String MESSAGE = "Test log message";

	/**
	 * Test method for {@link com.persinity.common.Log4jOutputStream#write()} and
	 * {@link com.persinity.common.Log4jOutputStream#flush()} .
	 */
	@Test
	public void testFlush() {
		final Logger logController = EasyMock.createMock(Logger.class);
		logController.log(logController.getName(), Level.DEBUG, MESSAGE, null);
		EasyMock.expectLastCall();
		EasyMock.replay(logController);

		final Log4jOutputStream testee = new Log4jOutputStream(logController, Level.DEBUG);
		final Charset latin1Charset = Charset.forName("ISO-8859-1");
		try {
			testee.write(latin1Charset.encode(CharBuffer.wrap(MESSAGE.toCharArray())).array());
			testee.flush();
		} catch (final IOException e) {
			Assert.fail();
		} finally {
			testee.close();
		}

		EasyMock.verify(logController);

	}
}
