package org.pircbotx.listeners;

import static com.google.common.base.Preconditions.checkArgument;

import org.pircbotx.ExtendedPircBotX;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import com.google.common.base.Strings;

/**
 * Automatically provides help that users can request by typing a trigger either on a public channel
 * (help will be sent as {@code NOTICE} messages) or in a private chat.
 * <p>
 * The listener automatically detects which listeners the bot has and adds one line of description
 * for each of them that can be triggered. Additionally, it detects if the user requesting help is
 * an administrator, and hides any listener that is OP-only to normal users.
 *
 * @author Emmanuel Cron
 */
public final class HelpListener extends ListenerAdapter<PircBotX> implements PublicListener,
    PrivateListener {

  private String trigger;

  private String helpIntro;

  private String helpText;

  /**
   * Creates a new help listener.
   *
   * @param trigger the trigger users have to say in a public channel (prefixed by "{@code !}") or
   *        in a private chat (not prefixed) to display help
   * @param helpIntro an optional introduction sentence such as "These are the available commands:";
   *        this can be {@code null} or empty
   */
  public HelpListener(String trigger, String helpIntro) {
    checkArgument(!Strings.isNullOrEmpty(trigger), "Trigger can't be null or empty");

    this.trigger = trigger;
    this.helpIntro = helpIntro;
  }

  @Override
  public boolean isOpRequired() {
    return false;
  }

  public void setHelpText(String helpText) {
    this.helpText = helpText;
  }

  @Override
  public String getHelpText() {
    return helpText;
  }

  @Override
  public String getTriggerMessage() {
    return trigger;
  }

  @Override
  public void onTriggerMessage(MessageEvent<ExtendedPircBotX> event) {
    if (!Strings.isNullOrEmpty(helpIntro)) {
      event.getUser().send().notice(helpIntro);
    }
    for (String line : event.getBot().buildHelp(event.getUser(), false)) {
      event.getUser().send().notice(line);
    }
  }

  @Override
  public String getPrivateTriggerMessage() {
    return trigger;
  }

  @Override
  public void onTriggerPrivateMessage(PrivateMessageEvent<ExtendedPircBotX> event) {
    if (!Strings.isNullOrEmpty(helpIntro)) {
      event.respond(helpIntro);
    }
    for (String line : event.getBot().buildHelp(event.getUser(), true)) {
      event.respond(line);
    }
  }
}
