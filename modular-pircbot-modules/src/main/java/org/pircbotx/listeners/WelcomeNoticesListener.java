package org.pircbotx.listeners;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.pircbotx.ExtendedPircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * Sends notices to any user that connects to the channel. In the notices, you may use the following
 * codes that will automatically be replaced by the bot:
 * <dl>
 * <dt>{botname}</dt>
 * <dd>Current name of the bot</dd>
 * <dt>{channel}</dt>
 * <dd>Channel that the user has just joined</dd>
 * <dt>{helptrigger}</dt>
 * <dd>Command to send in a public channel where the bot is connected to display the help.</dd>
 * <dt>{helpprivatetrigger}</dt>
 * <dd>Command to send in private chat with the bot to display the help.</dd>
 * </dl>
 *
 * @author Emmanuel Cron
 */
public class WelcomeNoticesListener extends ListenerAdapter<ExtendedPircBotX> {
  private static final String NO_HELP_TRIGGER_SET = "<no help trigger set>";

  private List<String> welcomeNotices;

  /**
   * Creates a new welcome module.
   *
   * @param welcomeNotices the messages to send to a user that connects to a channel where the bot
   *        is connected; special codes used in these messages will be replaced (see class
   *        description)
   * @param helpTrigger the command that needs to be sent to the bot to display the help
   */
  public WelcomeNoticesListener(List<String> welcomeNotices) {
    this.welcomeNotices = ImmutableList.copyOf(checkNotNull(welcomeNotices));
  }

  @Override
  public void onJoin(JoinEvent<ExtendedPircBotX> event) {
    if (event.getUser().getNick().equals(event.getBot().getNick())) {
      // Don't react to own joins
      return;
    }

    String helpTrigger;
    String helpPrivateTrigger;
    if (Strings.isNullOrEmpty(event.getBot().getHelpTrigger())) {
      helpTrigger = NO_HELP_TRIGGER_SET;
      helpPrivateTrigger = NO_HELP_TRIGGER_SET;
    } else {
      helpTrigger = "!" + event.getBot().getHelpTrigger();
      helpPrivateTrigger = event.getBot().getHelpTrigger();
    }

    for (String welcomeNotice : welcomeNotices) {
      welcomeNotice = welcomeNotice.replace("{botname}", event.getBot().getNick());
      welcomeNotice = welcomeNotice.replace("{channel}", event.getChannel().getName());
      welcomeNotice = welcomeNotice.replace("{helptrigger}", helpTrigger);
      welcomeNotice = welcomeNotice.replace("{helpprivatetrigger}", helpPrivateTrigger);
      event.getUser().send().notice(welcomeNotice);
    }
  }
}
