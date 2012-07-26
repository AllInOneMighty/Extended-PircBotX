package org.jibble.pircbot.util;

import java.io.IOException;

import junit.framework.Assert;

import org.jibble.pircbot.util.urlshorteners.SharasURLShortener;
import org.junit.Test;

public class SharaUtilsTest {
	@Test
	public void testSharasify() throws IOException {
		SharasURLShortener sharas = new SharasURLShortener("sharascache");
		String shortenedURL = sharas.shortenURL("http://www.judgehype.com");
		Assert.assertEquals("http://shar.as/Fg1e3", shortenedURL);
	}
}
