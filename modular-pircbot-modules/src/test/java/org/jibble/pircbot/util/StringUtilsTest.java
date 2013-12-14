package org.jibble.pircbot.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.pircbotx.util.StringUtils;

public class StringUtilsTest {
  @Test
  public void countMatches() {
    assertEquals(2, StringUtils.countMatches("%s %s", "%s"));
    assertEquals(1, StringUtils.countMatches("%s %b", "%s"));
    assertEquals(3, StringUtils.countMatches("%s%s{%s_%.s", "%s"));

    assertEquals(2, StringUtils.countMatches("%s%s%s%s%s", "%s%"));
  }
}
