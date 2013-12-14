package org.pircbotx.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;

/**
 * Some useful methods to handle {@link String}s correctly.
 * 
 * @author Emmanuel Cron
 */
public final class StringUtils {
  public static int countMatches(String str, String sub) {
    checkArgument(!Strings.isNullOrEmpty(str), "String to search in is null or empty");
    checkArgument(!Strings.isNullOrEmpty(sub), "String to match is null or empty");

    int matches = 0;
    int searchIndex = 0;
    while ((searchIndex = str.indexOf(sub, searchIndex)) != -1) {
      matches++;
      searchIndex += sub.length();
    }
    return matches;
  }
}
