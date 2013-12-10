package org.jibble.pircbot.modules.onaction;

import java.util.ArrayList;
import java.util.List;

/**
 * An object regrouping a probability of being triggered and associated possible
 * {@link PrivateAnswer}s and {@link ActionAnswer}s. The actual trigger is not saved by this object
 * but is rather set in the Pirc module that uses this class.
 * 
 * @author Emmanuel Cron
 */
public class OnActionAnswers {
  private int probability;
  private List<Answer> possibleAnswers = new ArrayList<Answer>();

  public OnActionAnswers(int probability, List<String> possiblePrivateAnswers,
      List<String> possibleActionAnswers) {
    this.probability = probability;
    for (String possiblePrivateAnswer : possiblePrivateAnswers) {
      possibleAnswers.add(new PrivateAnswer(possiblePrivateAnswer));
    }
    for (String possibleActionAnswer : possibleActionAnswers) {
      possibleAnswers.add(new ActionAnswer(possibleActionAnswer));
    }
  }

  public int getProbability() {
    return probability;
  }

  public List<Answer> getPossibleAnswers() {
    return possibleAnswers;
  }
}
