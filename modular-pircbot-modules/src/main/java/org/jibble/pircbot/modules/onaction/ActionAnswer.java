package org.jibble.pircbot.modules.onaction;

/**
 * A type of {@link Answer} that is sent to the channel as an action ("{@code /me does something}").
 * 
 * @author Emmanuel Cron
 */
public class ActionAnswer extends Answer {
  public ActionAnswer(String answer) {
    super(answer);
  }

  @Override
  public boolean isAction() {
    return true;
  }
}
