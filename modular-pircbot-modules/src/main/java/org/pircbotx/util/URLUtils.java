package org.pircbotx.util;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Provides simple utility methods to parse query parameters of a URL.
 *
 * @author Emmanuel Cron
 */
public final class URLUtils {
  private static final Splitter QUERY_PARAMS_SPLITTER = Splitter.on('&');
  private static final Splitter QUERY_VALUES_SPLITTER = Splitter.on('=');

  /**
   * Get all query parameters of the given URL as a map. Keys are the parameter names and their
   * corresponding value are all the values parsed for that parameter in the URL. Parameters often
   * have only one value, in that case the {@link #getQueryParamFirstValue(URL, String)} method is
   * useful.
   */
  public static Map<String, List<String>> getQueryParams(URL url) {
    Map<String, List<String>> queryParams = Maps.newHashMap();
    if (Strings.isNullOrEmpty(url.getQuery())) {
      return queryParams;
    }

    for (String entry : QUERY_PARAMS_SPLITTER.split(url.getQuery())) {
      Iterator<String> entryParts = QUERY_VALUES_SPLITTER.split(entry).iterator();
      String param = entryParts.next();
      String value = null;
      if (entryParts.hasNext()) {
        value = entryParts.next();
      }

      if (!queryParams.containsKey(param)) {
        queryParams.put(param, Lists.<String>newArrayList());
      }
      if (!Strings.isNullOrEmpty(value)) {
        queryParams.get(param).add(value);
      }
    }
    return queryParams;
  }

  /**
   * Returns the first value of the given query parameter in the passed URL.
   *
   * @return {@code null} if no such query parameter exists, {@code ""} if it exists and its first
   *         value is empty, otherwise its first value
   */
  public static String getQueryParamFirstValue(URL url, String queryParam) {
    Map<String, List<String>> queryParams = getQueryParams(url);
    if (!queryParams.containsKey(queryParam)) {
      // Param not here, no value
      return null;
    }
    if (queryParams.get(queryParam).isEmpty()) {
      // Param with empty value
      return "";
    }
    return queryParams.get(queryParam).get(0);
  }
}
