package org.pircbotx.listeners;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Makes the bot join channels on a specific server response.
 * <p>
 * Example: Let's say your bot requests the host protection ({@code +x}) custom mode {@code MODE}
 * from a server. If the bot immediately joins a channel after connecting to the server, even
 * <i>after</i> sending the host protection request, the users of the channel might see the real IP
 * address of the bot before it gets host protection. Therefore, it must only join the channel after
 * receiving the confirmation from the server that the mode was set (i.e. code {@code 396} for
 * servers in the Undernet network such as Quakenet).
 *
 * @see <a
 *      href="https://www.alien.net.au/irc/irc2numerics.html">https://www.alien.net.au/irc/irc2numerics.html</a>
 * @author Emmanuel Cron
 */
public class JoinOnServerResponseListener extends ListenerAdapter<PircBotX> {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(JoinOnServerResponseListener.class);

  private int triggerCode;

  private List<String> channels;

  /**
   * Creates a new join on server response module.
   *
   * @param triggerCode the server response on which the module will be triggered
   * @param channels the channels to join on this response
   */
  public JoinOnServerResponseListener(int triggerCode, List<String> channels) {
    checkNotNull(channels, "No specified channels to join");
    checkArgument(channels.size() > 0, "The list of channels to join is empty");

    this.triggerCode = triggerCode;
    this.channels = ImmutableList.copyOf(channels);
  }

  @Override
  public void onServerResponse(ServerResponseEvent<PircBotX> event) {
    if (event.getCode() == triggerCode) {
      LOGGER.info("Received trigger code {} from server, joining channels", triggerCode);
      for (String channel : channels) {
        LOGGER.info("Joining channel: {}", channel);
        event.getBot().sendIRC().joinChannel(channel);
      }
    }
  }
}
