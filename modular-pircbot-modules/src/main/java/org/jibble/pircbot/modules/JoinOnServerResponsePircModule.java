package org.jibble.pircbot.modules;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.jibble.pircbot.ExtendedPircBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Makes the bot join channels on a specific server response.
 * <p>
 * This module can be used when requesting a custom <tt>MODE</tt> from the server, especially host
 * protection (<tt>+x</tt>). Indeed, if the bot immediately joins a channel after connecting to the
 * server, even <i>after</i> sending the host protection request, the users of the channel might see
 * the real IP address of the bot before it gets host protection. Therefore, it must only join the
 * channel after receiving the confirmation from the server (i.e. code <tt>396</tt> for servers in
 * the Undernet network such as Quakenet).
 * 
 * @see <a
 *      href="https://www.alien.net.au/irc/irc2numerics.html">https://www.alien.net.au/irc/irc2numerics.html</a>
 * @author Emmanuel Cron
 */
public class JoinOnServerResponsePircModule extends AbstractPircModule {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(JoinOnServerResponsePircModule.class);

  private int triggerCode;

  private List<String> channels;

  /**
   * Creates a new join on server response module.
   * 
   * @param triggerCode the server response on which the module will be triggered
   * @param channels the channels to join on this response
   */
  public JoinOnServerResponsePircModule(int triggerCode, List<String> channels) {
    this.triggerCode = triggerCode;
    this.channels = ImmutableList.copyOf(checkNotNull(channels));
  }

  @Override
  public void onServerResponse(ExtendedPircBot bot, int code, String response) {
    if (code == triggerCode) {
      LOGGER.info("Received trigger code {} from server, joining channels", triggerCode);
      for (String channel : channels) {
        LOGGER.info("Joining channel: {}", channel);
        bot.joinChannel(channel);
      }
    }
  }
}
