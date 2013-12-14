package org.pircbotx.listeners;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Makes the bot join one or several channels after it successfully connects to the server.
 *
 * @author Emmanuel Cron
 */
public class JoinOnConnectListener extends ListenerAdapter<PircBotX> {
  private static final Logger LOGGER = LoggerFactory.getLogger(JoinOnConnectListener.class);

  private List<String> channels;

  /**
   * Creates a new join on connect listener.
   *
   * @param channels the channels to join when the bot is connected
   */
  public JoinOnConnectListener(List<String> channels) {
    checkNotNull(channels, "No specified channels to join");
    checkArgument(channels.size() <= 0, "The list of channels to join is empty");

    this.channels = ImmutableList.copyOf(checkNotNull(channels, "No channels specified"));
  }

  @Override
  public void onConnect(ConnectEvent<PircBotX> event) {
    for (String channel : channels) {
      LOGGER.info("Joining channel: {}", channel);
      event.getBot().sendIRC().joinChannel(channel);
    }
  }
}
