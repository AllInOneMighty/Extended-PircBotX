package org.jibble.pircbot;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jibble.pircbot.modules.AbstractPircModule;
import org.jibble.pircbot.modules.AbstractRunnablePircModule;
import org.jibble.pircbot.modules.AbstractStoppablePircModule;
import org.jibble.pircbot.modules.HelpPircModule;
import org.jibble.pircbot.modules.PrivatePircModule;
import org.jibble.pircbot.modules.PublicPircModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * A modular {@link PircBot} in which you can add modules.
 * <p>
 * Modules can be made runnable (by extending {@link AbstractRunnablePircModule}) to have them do
 * some background work while the bot is running. The bot will automatically run and stop these
 * modules when it connects to or quits from the IRC server.
 * <p>
 * You can add your modules by calling the {@link #addModule(AbstractPircModule)} method. For
 * example, you can add help support by adding the {@link HelpPircModule} module to this bot.
 * <p>
 * After all modules are added, you can then connect the bot by calling {@link #connect()}. This
 * method automatically retries connections until the bot is connected.
 * 
 * @author Emmanuel Cron
 */
public class ModularPircBot extends ExtendedPircBot {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModularPircBot.class);

  private ThreadGroup threadGroup = new ThreadGroup(getClass().getSimpleName());

  private String host;

  private List<Integer> ports;

  private Set<AbstractPircModule> modules = new HashSet<AbstractPircModule>();

  private boolean helpModuleAdded;

  private boolean botStarted;

  private boolean modulesStarted;

  /**
   * Creates a new modular {@link PircBot}.
   * 
   * @param host host name or IP of the server to which connect the bot
   * @param ports ports that should successively be tried until the bot is connected to the server
   *        (if the last port is reached, it will retry with the first port)
   * @param name nick that the bot should be using on the server
   */
  public ModularPircBot(String host, List<Integer> ports, String name) {
    checkArgument(!Strings.isNullOrEmpty(host), "No specified host to connect to");
    checkNotNull(ports, "No specified ports to connect to");
    checkArgument(ports.size() > 0, "List of ports to connect to is empty");
    checkArgument(!Strings.isNullOrEmpty(name), "Bot does not have a name");

    this.host = host;
    this.ports = ImmutableList.copyOf(ports);
    setName(name);
  }

  /**
   * Adds a module to the bot. You must call this method for each module you wish to add before
   * calling {@link #connect()}. Runnable modules will be launched when the bot connects to the
   * server.
   * 
   * @param module a {@link PircBot} module
   */
  public void addModule(AbstractPircModule module) {
    checkNotNull(module, "No module specified");
    checkState(!botStarted, "Modules cannot be added after bot has been started");

    if (module instanceof HelpPircModule) {
      if (helpModuleAdded) {
        throw new IllegalStateException("Bot cannot have more than one help module");
      }

      helpModuleAdded = true;
    }
    if (module instanceof PublicPircModule) {
      PublicPircModule publicModule = (PublicPircModule) module;
      checkState(!Strings.isNullOrEmpty(publicModule.getTriggerMessage()),
          "Public module requires a trigger message: %s", module.getClass().getSimpleName());
    }
    if (module instanceof PrivatePircModule) {
      PrivatePircModule privateModule = (PrivatePircModule) module;
      checkState(!Strings.isNullOrEmpty(privateModule.getPrivateTriggerMessage()),
          "Private module requires a trigger message: %s", module.getClass().getSimpleName());
    }
    modules.add(module);
  }

  /**
   * Connects the bot to the server and ports specified when constructing this class. This method
   * will successively try all ports until the bot is connected. If the last port is reached, the
   * bot retries with the first port and so on.
   */
  public void connect() {
    if (!botStarted) {
      botStarted = true;
    }

    int i = 0;
    while (true) {
      try {
        LOGGER.info("Connecting to {}:{}", host, ports.get(i));
        connect(host, ports.get(i));
        return;
      } catch (IrcException ie) {
        LOGGER.warn("IRC Exception while connecting to the server", ie);
      } catch (IOException ioe) {
        LOGGER.error("I/O Exception while connecting to the server", ioe);
      }

      // Next port on next loop
      i = ++i % ports.size();

      // Sleep the thread to avoid network spam
      try {
        LOGGER.info("Sleeping 1 second...");
        Thread.sleep(1000);
      } catch (InterruptedException ie) {
        LOGGER.error("Could not sleep connection thread, your logs might get huge!");
      }
    }
  }

  @Override
  protected void onConnect() {
    LOGGER.info("Connected with name: {}", getNick());

    // First run onConnect() methods of all modules
    for (AbstractPircModule module : modules) {
      module.onConnect(this);
    }

    // Then launch the runnable modules if required
    if (!modulesStarted) {
      for (AbstractPircModule module : modules) {
        if (module instanceof AbstractRunnablePircModule) {
          AbstractRunnablePircModule runnableModule = (AbstractRunnablePircModule) module;
          runnableModule.setBot(this);
          LOGGER.info("Launching module thread: {}", runnableModule);
          new Thread(threadGroup, runnableModule).start();
        }
      }
      modulesStarted = true;
    }
  }

  @Override
  protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
    for (AbstractPircModule module : modules) {
      module.onTopic(this, channel, topic, setBy, date, changed);
    }
  }

  @Override
  protected void onMessage(String channel, String sender, String login, String hostname,
      String message) {
    for (AbstractPircModule module : modules) {
      module.onMessage(this, channel, sender, login, hostname, message);
      if (module instanceof PublicPircModule) {
        PublicPircModule publicModule = (PublicPircModule) module;
        String triggerMessage = "!" + publicModule.getTriggerMessage();
        if (message.equalsIgnoreCase(triggerMessage)) {
          publicModule.onTriggerMessage(this, channel, sender, login, hostname);
        }
      }
    }
  }

  @Override
  protected void onPrivateMessage(String sender, String login, String hostname, String message) {
    Boolean isSenderOp = null;

    for (AbstractPircModule module : modules) {
      module.onPrivateMessage(this, sender, login, hostname, message);

      if (module instanceof PrivatePircModule) {
        PrivatePircModule privateModule = (PrivatePircModule) module;

        // Is message a trigger?
        if (message.equalsIgnoreCase(privateModule.getPrivateTriggerMessage())) {
          // Is op required?
          if (privateModule.isOpRequired()) {
            // Check if user is op only the first time
            if (isSenderOp == null) {
              isSenderOp = isUserOp(sender);
            }

            if (!isSenderOp) {
              // Op required but user not op, skipping
              LOGGER.info("User {} cannot trigger {} module because he/she is not op", sender,
                  module.getClass());
              continue;
            }
          }

          privateModule.onTriggerPrivateMessage(this, sender, login, hostname);
        }
      }
    }
  }

  @Override
  protected void onAction(String sender, String login, String hostname, String target, String action) {
    for (AbstractPircModule module : modules) {
      module.onAction(this, sender, login, hostname, target, action);
    }
  }

  @Override
  protected void onServerResponse(int code, String response) {
    for (AbstractPircModule module : modules) {
      module.onServerResponse(this, code, response);
    }
  }

  @Override
  protected void onJoin(String channel, String sender, String login, String hostname) {
    for (AbstractPircModule module : modules) {
      module.onJoin(this, channel, sender, login, hostname);
    }
  }

  @Override
  protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
    for (AbstractPircModule module : modules) {
      module.onNickChange(this, oldNick, login, hostname, newNick);
    }
  }

  @Override
  protected void onPart(String channel, String sender, String login, String hostname) {
    for (AbstractPircModule module : modules) {
      module.onPart(this, channel, sender, login, hostname);
    }
  }

  @Override
  protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
    for (AbstractPircModule module : modules) {
      module.onQuit(this, sourceNick, sourceLogin, sourceHostname, reason);
    }
  }

  @Override
  protected void onMode(String channel, String sourceNick, String sourceLogin,
      String sourceHostname, String mode) {
    for (AbstractPircModule module : modules) {
      module.onMode(this, channel, sourceNick, sourceLogin, sourceHostname, mode);
    }
  }

  @Override
  protected void onUserMode(String targetNick, String sourceNick, String sourceLogin,
      String sourceHostname, String mode) {
    for (AbstractPircModule module : modules) {
      module.onUserMode(this, targetNick, sourceNick, sourceLogin, sourceHostname, mode);
    }
  }

  @Override
  protected void onKick(String channel, String kickerNick, String kickerLogin,
      String kickerHostname, String recipientNick, String reason) {
    for (AbstractPircModule module : modules) {
      module.onKick(this, channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
    }
  }

  @Override
  protected void onDisconnect() {
    LOGGER.info("Bot disconnected");

    // Trigger event on modules
    for (AbstractPircModule module : modules) {
      module.onDisconnect(this);
    }

    if (!isQuitRequested()) {
      // Not a wanted quit, forcing reconnect
      LOGGER.info("Unexpected disconnection detected, reconnecting");
      connect();
      return;
    }

    // Quit requested, stopping threads and exiting
    for (AbstractPircModule module : modules) {
      if (module instanceof AbstractStoppablePircModule) {
        AbstractStoppablePircModule stoppableModule = (AbstractStoppablePircModule) module;
        stoppableModule.stop();
      }
    }

    int stopChecks = 0;
    do {
      stopChecks++;
      // Wait a total of 4.5s
      try {
        Thread.sleep(1500);
      } catch (InterruptedException ie) {
        LOGGER.error("Could not wait until threads were stopped, some might be killed", ie);
      }
    } while (stopChecks < 3 && threadGroup.activeCount() > 0);

    if (threadGroup.activeCount() > 0) {
      LOGGER.warn("One or more thread are still running, they will now be killed");
    }

    LOGGER.info("Exiting");

    System.exit(0);
  }

  @Override
  public String getHelpTrigger() {
    for (AbstractPircModule module : modules) {
      if (module instanceof HelpPircModule) {
        return ((HelpPircModule) module).getTriggerMessage();
      }
    }
    // No help module, no help trigger
    return null;
  }

  @Override
  public List<String> buildHelp(String nick, boolean inPrivate) {
    List<String> help = new ArrayList<String>();

    Map<String, String> helpMap = new TreeMap<String, String>();
    if (!inPrivate) {
      // Public
      for (AbstractPircModule module : modules) {
        if (module instanceof PublicPircModule) {
          PublicPircModule publicModule = (PublicPircModule) module;
          String trigger = "!" + publicModule.getTriggerMessage();
          String line = trigger;
          if (!Strings.isNullOrEmpty(publicModule.getHelpText())) {
            // We suppose commands are never bigger than 20 characters
            line = Strings.padEnd(line, 20, ' ') + publicModule.getHelpText();
          }
          helpMap.put(trigger, line);
        }
      }
    } else {
      // Private
      boolean isUserOp = isUserOp(nick);

      for (AbstractPircModule module : modules) {
        if (module instanceof PrivatePircModule) {
          PrivatePircModule privateModule = (PrivatePircModule) module;
          if (privateModule.isOpRequired() && !isUserOp) {
            // Module can only be displayed to ops
            continue;
          }

          String trigger = privateModule.getPrivateTriggerMessage();
          String line = trigger;
          // Adding public help if it is available
          if (module instanceof PublicPircModule) {
            PublicPircModule publicModule = (PublicPircModule) module;
            if (!Strings.isNullOrEmpty(publicModule.getHelpText())) {
              // We suppose commands are never bigger than 20 characters
              line = Strings.padEnd(line, 20, ' ') + publicModule.getHelpText();
            }
          }
          helpMap.put(trigger, line);
        }
      }
    }
    help.addAll(new TreeMap<String, String>(helpMap).values());

    return help;
  }

  // internal helpers

  private boolean isUserOp(String nick) {
    String[] channels = getChannels();
    // User may be found on any channel
    for (String channel : channels) {
      User[] users = getUsers(channel);
      for (User user : users) {
        // If the user is found
        if (user.getNick().equals(nick)) {
          // Check if OP
          if (user.isOp()) {
            return true;
          } else {
            // Break the loop for this channel, go for next one
            break;
          }
        }
      }
    }
    // User not found or not op on any channel
    return false;
  }
}
