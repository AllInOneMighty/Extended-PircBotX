package org.jibble.pircbot.listeners.onaction;

/**
 * An answer that the bot may use when being triggerd by an action. It automatically replaces the "
 * <tt>{sender}</tt>" string in the answer by the person who sent the trigger.
 * 
 * @author Emmanuel Cron
 */
public abstract class Answer {
  private String answer;

  public Answer(String answer) {
    this.answer = answer;
  }

  public String buildAnswer(String sender) {
    return answer.replaceAll("\\{sender\\}", sender);
  }

  public abstract boolean isAction();

  @Override
  public String toString() {
    return answer;
  }
}
