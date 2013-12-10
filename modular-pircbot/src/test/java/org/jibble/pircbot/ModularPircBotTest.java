package org.jibble.pircbot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.jibble.pircbot.modules.HelpPircModule;
import org.junit.Test;

public class ModularPircBotTest {
  @Test
  public void buildHelp() {
    List<Integer> ports = new ArrayList<Integer>();
    ports.add(1);

    ModularPircBot bot = new ModularPircBot("some host", ports, "Test");
    bot.addModule(new HelpPircModule("aide", "This is a test help message"));

    List<String> help = bot.buildHelp("TestUser", false);
    // Intro is sent to channel (not accessible here)
    assertEquals(1, help.size());
    assertEquals("!aide", help.get(0));
  }

  @Test
  public void addModule_twoHelpModules() {
    List<Integer> ports = new ArrayList<Integer>();
    ports.add(1);

    ModularPircBot bot = new ModularPircBot("some host", ports, "Test");
    bot.addModule(new HelpPircModule("aide", "This is a test help message"));
    try {
      bot.addModule(new HelpPircModule("aide", "This is a test help message"));
      fail("Should have failed");
    } catch (IllegalStateException ise) {
      // expected
    }
  }
}
