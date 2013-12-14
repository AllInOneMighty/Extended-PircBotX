package org.pircbotx.listeners.onaction;

/**
 * A type of {@link Answer} that is sent to the channel as a normal chat.
 * 
 * @author Emmanuel Cron
 */
public class PrivateAnswer extends Answer {
  public PrivateAnswer(String answer) {
    super(answer);
  }

  @Override
  public boolean isAction() {
    return false;
  }
}
