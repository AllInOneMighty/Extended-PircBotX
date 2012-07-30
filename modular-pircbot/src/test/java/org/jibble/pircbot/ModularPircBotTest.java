package org.jibble.pircbot;

import java.util.List;

import junit.framework.Assert;

import org.jibble.pircbot.modules.HelpPircModule;
import org.junit.Test;

public class ModularPircBotTest {
	@Test
	public void test() {
		ModularPircBot bot = new ModularPircBot(null, null, "Test");
		bot.addModule(new HelpPircModule("aide", "This is a test help message"));
		
		List<String> help = bot.buildHelp("TestUser", false);
		// Intro is sent to channel (not accessible here)
		Assert.assertEquals(1, help.size());
		Assert.assertEquals("!aide", help.get(0));
	}
}
