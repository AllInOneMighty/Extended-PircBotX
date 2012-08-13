package org.jibble.pircbot.util;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.jibble.pircbot.util.urlshorteners.SharasURLShortener;
import org.junit.BeforeClass;
import org.junit.Test;

public class SharaUtilsTest {
	private static final String CACHE_FOLDER = "target/cache";

	@BeforeClass
	public static void createCacheFolder() {
		new File(CACHE_FOLDER).mkdir();
	}
	
	@Test
	public void testSharasify() throws IOException {
		SharasURLShortener sharas = new SharasURLShortener(CACHE_FOLDER);
		String shortenedURL = sharas.shortenURL("http://www.judgehype.com");
		Assert.assertEquals("http://shar.as/Fg1e3", shortenedURL);
	}
}
