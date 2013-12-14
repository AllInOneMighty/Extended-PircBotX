package org.jibble.pircbot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.pircbotx.util.PropertiesUtils;

public class PropertiesUtilsTest {
  @Test
  public void getIntList() {
    List<Integer> ints = PropertiesUtils.getIntList("45-102");
    for (Integer i = 45, j = 0; i <= 102; i++, j++) {
      assertEquals(i, ints.get(j));
    }
  }

  @Test
  public void getStringList() throws IOException {
    Properties properties = new Properties();
    InputStream input = ClassLoader.getSystemResourceAsStream("actions.properties");
    Reader reader = new InputStreamReader(input, "UTF-8");
    properties.load(reader);
    reader.close();

    List<String> strings;

    strings = PropertiesUtils.getStringList("test.triggermessages", properties);
    assertEquals(4, strings.size());
    assertTrue(strings.contains("^lance .+$"));
    assertTrue(strings.contains("^utilise .+$"));
    assertTrue(strings.contains("^invoque .+$"));
    assertTrue(strings.contains("^incante .+$"));

    strings = PropertiesUtils.getStringList("test.possibleanswers.private", properties);
    assertEquals(3, strings.size());
    assertTrue(strings.contains("C'est super efficace!"));
    assertTrue(strings.contains("Ce n'est pas très efficace..."));
    assertTrue(strings.contains("{sender} devrait arrêter ça tout de suite!"));

    strings = PropertiesUtils.getStringList("test.probability", properties);
    assertEquals(0, strings.size());
  }
}
