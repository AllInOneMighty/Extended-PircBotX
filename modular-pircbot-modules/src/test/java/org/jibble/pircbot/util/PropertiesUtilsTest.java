package org.jibble.pircbot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

public class PropertiesUtilsTest {
	@Test
	public void testGetIntList() {
		List<Integer> ints = PropertiesUtils.getIntList("45-102");
		for (Integer i = 45, j = 0; i <= 102; i++, j++) {
			Assert.assertEquals(i, ints.get(j));
		}
	}
	
	@Test
	public void testGetStringList() throws IOException {
		Properties properties = new Properties();
		InputStream input = ClassLoader.getSystemResourceAsStream("actions.properties");
		Reader reader = new InputStreamReader(input, "UTF-8");
		properties.load(reader);
		reader.close();

		List<String> strings;
		
		strings = PropertiesUtils.getStringList("test.triggermessages", properties);
		Assert.assertEquals(4, strings.size());
		Assert.assertTrue(strings.contains("^lance .+$"));
		Assert.assertTrue(strings.contains("^utilise .+$"));
		Assert.assertTrue(strings.contains("^invoque .+$"));
		Assert.assertTrue(strings.contains("^incante .+$"));
		
		strings = PropertiesUtils.getStringList("test.possibleanswers.private", properties);
		Assert.assertEquals(3, strings.size());
		Assert.assertTrue(strings.contains("C'est super efficace!"));
		Assert.assertTrue(strings.contains("Ce n'est pas très efficace..."));
		Assert.assertTrue(strings.contains("{sender} devrait arrêter ça tout de suite!"));
		
		strings = PropertiesUtils.getStringList("test.probability", properties);
		Assert.assertEquals(0, strings.size());
	}
}
