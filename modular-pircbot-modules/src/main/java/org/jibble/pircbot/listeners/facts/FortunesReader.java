package org.jibble.pircbot.listeners.facts;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Reads <a href="http://en.wikipedia.org/wiki/Fortune_(Unix)#Fortune_files">fortunes files</a>
 * using the single file version.
 * 
 * @author Emmanuel Cron
 */
public class FortunesReader implements FactsReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(FortunesReader.class);

  private String fortunesPath;

  private String encoding;

  /**
   * Creates a new fortunes reader.
   * 
   * @param fortunesPath the path to the fortunes file to read
   * @param encoding the encoding in which the file is stored
   */
  public FortunesReader(String fortunesPath, String encoding) {
    checkArgument(!Strings.isNullOrEmpty(fortunesPath), "No path to fortunes file provided");
    checkArgument(!Strings.isNullOrEmpty(encoding), "Fortunes file encoding must be specified");

    this.fortunesPath = fortunesPath;
    this.encoding = encoding;
  }

  @Override
  public List<List<String>> readFacts() {
    List<List<String>> fortunes = new ArrayList<List<String>>();
    List<String> fortune = new ArrayList<String>();
    try (InputStream input = ClassLoader.getSystemResourceAsStream(fortunesPath)) {
      Reader reader = new InputStreamReader(input, encoding);
      BufferedReader bufferedReader = new BufferedReader(reader);

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        line = line.trim();

        // Ignore blank lines
        if (Strings.isNullOrEmpty(line)) {
          continue;
        }

        if ("%".equals(line) && fortune.size() > 0) {
          fortunes.add(fortune);
          // Don't clear() here
          fortune = new ArrayList<String>();
        } else {
          fortune.add(line);
        }
      }
    } catch (IOException ioe) {
      LOGGER.error("Error while reading fortunes file, will use what we have already read", ioe);
    }

    // Add last fortune
    if (fortune.size() > 0) {
      fortunes.add(fortune);
    }

    return fortunes;
  }
}
