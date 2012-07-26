package org.jibble.pircbot;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class ModularPircBotTest {
	@Test
	public void test() {
		ModularPircBot bot = new ModularPircBot(null, null, "Test");
		bot.setHelpIntro("This is a test help message");
		
		List<String> help = bot.buildHelp("TestUser", false);
		Assert.assertEquals(1, help.size());
		Assert.assertEquals("This is a test help message", help.get(0));
	}
}
