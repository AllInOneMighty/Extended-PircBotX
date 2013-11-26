package org.jibble.pircbot.modules;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.ExtendedPircBot;

/**
 * Automatically executes the <tt>AUTH</tt> and <tt>MODE</tt> commands when the
 * bot connects to the server.
 * 
 * @author Emmanuel Cron
 */
public class AuthModePircModule extends AbstractPircModule {
	
	private String authUsername;
	
	private String authPassword;
	
	private String modes;

  /**
   * Creates a new AUTH/MODE module that sets the mode but does not authenticate.
   * 
   * @param modes the modes to request with the <tt>MODE</tt> command; this
   *        parameter cannot be <tt>null</tt> or empty
   */
  public AuthModePircModule(String modes) {
    this.modes = modes;
  }

	/**
	 * Creates a new AUTH/MODE module.
	 * 
	 * @param authUsername the user name to user in the <tt>AUTH</tt> command
	 * @param authPassword the password to user in the <tt>AUTH</tt> command
	 * @param modes the modes to request with the <tt>MODE</tt> command; if no
	 *        particular mode needs to be requested, this parameter can be
	 *        <tt>null</tt> or empty
	 */
	public AuthModePircModule(String authUsername, String authPassword, String modes) {
		this.authUsername = authUsername;
		this.authPassword = authPassword;
		this.modes = modes;
	}

	@Override
	public void onConnect(ExtendedPircBot bot) {
    if (StringUtils.isNotBlank(authUsername)) {
      bot.sendRawLineViaQueue(
        String.format("auth %s %s", authUsername, authPassword));
    }
		if (StringUtils.isNotBlank(modes)) {
			bot.sendRawLineViaQueue(
        String.format("mode %s %s", bot.getNick(), modes));
		}
	}
	
}
