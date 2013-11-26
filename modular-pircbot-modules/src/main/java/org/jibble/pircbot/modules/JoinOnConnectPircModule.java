package org.jibble.pircbot.modules;

import org.jibble.pircbot.ExtendedPircBot;

/**
 * Makes the bot join a channel after it succesfully connects to the server.
 *
 * @author Emmanuel Cron
 */
public class JoinOnConnectPircModule extends AbstractPircModule {
  private String channel;
  
  /**
   * Creates a new join on connect module.
   * 
   * @param channel the channel to join when the bot is connected
   */
  public JoinOnServerResponsePircModule(String channel) {
    this.channel = channel;
  }

  @Override
  public void onConnect(ExtendedPircBot bot) {
    bot.joinChannel(channel);
  }
}
