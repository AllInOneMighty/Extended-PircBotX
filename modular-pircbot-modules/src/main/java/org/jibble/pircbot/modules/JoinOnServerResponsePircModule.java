package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

/**
 * Makes the bot join a channel on a specific server response.
 * <p>
 * This module is generally used when requesting a custom <tt>MODE</tt> from the
 * server, especially host protection (<tt>+x</tt>). Indeed, if the bot
 * immediately joins a channel after connecting to the server, even <i>after</i>
 * sending the host protection request, the users of the channel might see the
 * real IP address of the bot before it gets host proection. Therefore, it must
 * only join the channel after receiving the host protection confirmation from
 * the server (i.e. code <tt>396</tt> for servers in the Undernet network such
 * as Quakenet).
 * 
 * @see <a
 *      href="https://www.alien.net.au/irc/irc2numerics.html">https://www.alien.net.au/irc/irc2numerics.html</a>
 * @author Emmanuel Cron
 */
public class JoinOnServerResponsePircModule extends AbstractPircModule {
	private int triggerCode;
	
	private String channel;
	
	/**
	 * Creates a new join on server response module.
	 * 
	 * @param triggerCode the server response on which the module will be
	 *        triggered
	 * @param channel the channel to join on this response
	 */
	public JoinOnServerResponsePircModule(int triggerCode, String channel) {
		this.triggerCode = triggerCode;
		this.channel = channel;
	}

	@Override
	public void onServerResponse(ExtendedPircBot bot, int code, String response) {
		if (code == triggerCode) {
			bot.joinChannel(channel);
		}
	}
}
