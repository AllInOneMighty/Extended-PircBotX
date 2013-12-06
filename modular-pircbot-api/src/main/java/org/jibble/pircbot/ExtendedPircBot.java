package org.jibble.pircbot;

import java.util.List;

/**
 * An extension of {@link PircBot} adding functionalities such as quit request status and help
 * support. This extension is mainly used by modules in a modular bot.
 * 
 * @author Emmanuel Cron
 */
public abstract class ExtendedPircBot extends PircBot {
  private boolean quitRequested;

  /**
   * Returns the trigger used to display the help.
   * 
   * @return the bot help trigger or {@code null} if none is available
   */
  public abstract String getHelpTrigger();

  /**
   * Builds a list of lines to be sent to the user that gives help information on this bot. The
   * returned strings are arbitrary. You usually want to give the actions that can be triggered on
   * the bot, such as "{@code !news}", associated to their description.
   * 
   * @param nick the nick of the user who requested help to be displayed
   * @param inPrivate {@code true} if the help request was made in a private chat, {@code false} if
   *        it was made on a public channel
   * @return a list of strings containing the help of this bot
   */
  public abstract List<String> buildHelp(String nick, boolean inPrivate);

  /**
   * Sets whether a user has requested the bot to quit. This is useful to avoid the bot to reconnect
   * when we are actually <i>really</i> quiting.
   * 
   * @param quitRequested {@code true} if a user has requested a quit action, {@code false}
   *        otherwise
   */
  public void setQuitRequested(boolean quitRequested) {
    this.quitRequested = quitRequested;
  }

  /**
   * Returns whether a quit action was requested on the bot by a user.
   * 
   * @return {@code true} if quit action was requested, {@code false} otherwise
   */
  public boolean isQuitRequested() {
    return quitRequested;
  }
}
