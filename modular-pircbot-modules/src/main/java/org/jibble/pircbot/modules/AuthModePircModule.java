package org.jibble.pircbot.modules;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jibble.pircbot.ExtendedPircBot;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * Automatically executes the {@code AUTH} and/or {@code MODE} commands when the bot connects to the
 * server.
 * 
 * @author Emmanuel Cron
 */
public class AuthModePircModule extends AbstractPircModule {

  private Optional<String> authUsername;

  private Optional<String> authPassword;

  private Optional<String> modes;

  /**
   * Creates a new AUTH/MODE module that sets the mode but does not authenticate.
   * 
   * @param modes the modes to request with the {@code MODE} command; this parameter cannot be
   *        {@code null} or empty
   */
  public AuthModePircModule(String modes) {
    checkArgument(!Strings.isNullOrEmpty(modes));
    this.modes = Optional.of(modes);
  }

  /**
   * Creates a new AUTH/MODE module.
   * 
   * @param authUsername the user name to user in the {@code AUTH} command
   * @param authPassword the password to user in the {@code AUTH} command
   * @param modes the modes to request with the {@code MODE} command; optional
   */
  public AuthModePircModule(String authUsername, String authPassword, Optional<String> modes) {
    checkArgument(!Strings.isNullOrEmpty(authUsername));
    checkArgument(!Strings.isNullOrEmpty(authPassword));
    if (checkNotNull(modes).isPresent()) {
      checkArgument(!Strings.isNullOrEmpty(modes.get()), "If specified, modes can't be empty");
    }

    this.authUsername = Optional.of(authUsername);
    this.authPassword = Optional.of(authPassword);
    this.modes = modes;
  }

  @Override
  public void onConnect(ExtendedPircBot bot) {
    if (authUsername.isPresent()) {
      bot.sendRawLineViaQueue(String.format("auth %s %s", authUsername.get(), authPassword.get()));
    }
    if (modes.isPresent()) {
      bot.sendRawLineViaQueue(String.format("mode %s %s", bot.getNick(), modes.get()));
    }
  }

}
