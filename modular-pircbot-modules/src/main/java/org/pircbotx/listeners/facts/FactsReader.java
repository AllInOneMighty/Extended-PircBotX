package org.pircbotx.listeners.facts;

import java.util.List;

/**
 * A facts reader that provides a way to access facts in the form of a list. Implementation details
 * are left to the developer.
 * 
 * @author Emmanuel Cron
 */
public interface FactsReader {
  /**
   * Reads all the facts from its source into one single list of lists. Each list represents one
   * fact in the form of multiple strings that will be displayed sequentially on a public channel
   * when triggered.
   * 
   * @return a list of lists as described above
   */
  List<List<String>> readFacts();
}
