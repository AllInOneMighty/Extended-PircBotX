package org.jibble.pircbot.modules.onaction;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pircbotx.listeners.onaction.DynamicOnActionPattern;
import org.pircbotx.listeners.onaction.OnActionPatternFactory;
import org.pircbotx.listeners.onaction.StandardOnActionPattern;

public class OnActionPatternFactoryTest {
  @Test
  public void build() {
    assertTrue(OnActionPatternFactory.build("^pokes {botname}$") instanceof DynamicOnActionPattern);
    assertTrue(OnActionPatternFactory.build("^lance .+$") instanceof StandardOnActionPattern);
  }
}
