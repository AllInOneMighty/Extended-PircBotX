package org.jibble.pircbot;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.pircbotx.Configuration;
import org.pircbotx.ExtendedPircBotX;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.listeners.HelpListener;

public class ModularPircBotXTest {
  @Test
  public void buildHelp() {
    Configuration.Builder<PircBotX> configuration = new Configuration.Builder<PircBotX>()
        .setServer("some host", 1)
        .setName("Test")
        .addListener(new HelpListener("aide", "This is a test help message"));
    ExtendedPircBotX bot = new ExtendedPircBotX(configuration.buildConfiguration());

    User user = configuration.getBotFactory().createUser(bot, "TestUser");
    List<String> help = bot.buildHelp(user, false);
    // Intro is sent to channel (not accessible here)
    assertEquals(1, help.size());
    assertEquals("!aide", help.get(0));
  }
}
