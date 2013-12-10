package org.jibble.pircbot.modules.onaction;

import java.util.regex.Pattern;

/**
 * Creates the appropriate {@link OnActionPattern} depending on a given regexp. Right now, a dynamic
 * pattern is used if the regexp contains text enclosed in brackets (such as "<tt>{something}</tt> 
 * ").
 * 
 * @author Emmanuel Cron
 */
public class OnActionPatternFactory {
  private static final Pattern DYNAMIC_PATTERN_DETECTOR = Pattern.compile("^.*\\{.*[a-z]+.*\\}.*$",
      Pattern.CASE_INSENSITIVE);

  public static OnActionPattern build(String regex) {
    if (DYNAMIC_PATTERN_DETECTOR.matcher(regex).matches()) {
      return new DynamicOnActionPattern(regex);
    } else {
      return new StandardOnActionPattern(Pattern.compile(regex));
    }
  }
}
