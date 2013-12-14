package org.jibble.pircbot.modules.onaction;

import static org.junit.Assert.assertTrue;

import org.jibble.pircbot.listeners.onaction.DynamicOnActionPattern;
import org.jibble.pircbot.listeners.onaction.OnActionPatternFactory;
import org.jibble.pircbot.listeners.onaction.StandardOnActionPattern;
import org.junit.Test;

public class OnActionPatternFactoryTest {
  @Test
  public void build() {
    assertTrue(OnActionPatternFactory.build("^pokes {botname}$") instanceof DynamicOnActionPattern);
    assertTrue(OnActionPatternFactory.build("^lance .+$") instanceof StandardOnActionPattern);
  }
}
