package org.jibble.pircbot.listeners;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * Automatically executes the {@code AUTH} and/or {@code MODE} commands when the bot connects to the
 * server.
 *
 * @author Emmanuel Cron
 */
public class AuthModeListener extends ListenerAdapter<PircBotX> {

  private Optional<String> authUsername;

  private Optional<String> authPassword;

  private Optional<String> modes;

  /**
   * Creates a new AUTH/MODE module that sets the mode but does not authenticate.
   *
   * @param modes the modes to request with the {@code MODE} command; this parameter cannot be
   *        {@code null} or empty
   */
  public AuthModeListener(String modes) {
    checkArgument(!Strings.isNullOrEmpty(modes), "Modes must be specified if not authenticating");

    this.modes = Optional.of(modes);
  }

  /**
   * Creates a new AUTH/MODE module.
   *
   * @param authUsername the user name to user in the {@code AUTH} command
   * @param authPassword the password to user in the {@code AUTH} command
   * @param modes the modes to request with the {@code MODE} command; optional
   */
  public AuthModeListener(String authUsername, String authPassword, Optional<String> modes) {
    checkArgument(!Strings.isNullOrEmpty(authUsername), "Username must be provided");
    checkArgument(!Strings.isNullOrEmpty(authPassword), "Password must be provided");
    if (checkNotNull(modes).isPresent()) {
      checkArgument(!Strings.isNullOrEmpty(modes.get()), "If specified, modes can't be empty");
    }

    this.authUsername = Optional.of(authUsername);
    this.authPassword = Optional.of(authPassword);
    this.modes = modes;
  }

  @Override
  public void onConnect(ConnectEvent<PircBotX> event) {
    if (authUsername.isPresent()) {
      event.getBot().sendRaw()
          .rawLine(String.format("AUTH %s %s", authUsername.get(), authPassword.get()));
    }
    if (modes.isPresent()) {
      event.getBot().sendIRC().mode(event.getBot().getNick(), modes.get());
    }
  }

}
