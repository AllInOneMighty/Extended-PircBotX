package org.jibble.pircbot;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jibble.pircbot.listeners.HelpListener;
import org.jibble.pircbot.listeners.PrivateListener;
import org.jibble.pircbot.listeners.PublicListener;
import org.jibble.pircbot.listeners.RunnableListener;
import org.jibble.pircbot.listeners.TriggerableListener;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;

import com.google.common.base.Strings;

/**
 * A modular {@link PircBot} in which you can add modules.
 * <p>
 * Modules can be made runnable (by extending {@link RunnableListener}) to have them do some
 * background work while the bot is running. The bot will automatically run and stop these modules
 * when it connects to or quits from the IRC server.
 * <p>
 * You can add your modules by calling the {@link #addModule(AbstractPircModule)} method. For
 * example, you can add help support by adding the {@link HelpListener} module to this bot.
 * <p>
 * After all modules are added, you can then connect the bot by calling {@link #connect()}. This
 * method automatically retries connections until the bot is connected.
 *
 * @author Emmanuel Cron
 */
public class ExtendedPircBotX extends PircBotX {
  /**
   * Creates a new modular {@link PircBot}.
   *
   * @param host host name or IP of the server to which connect the bot
   * @param ports ports that should successively be tried until the bot is connected to the server
   *        (if the last port is reached, it will retry with the first port)
   * @param name nick that the bot should be using on the server
   */
  public ExtendedPircBotX(Configuration<PircBotX> configuration) {
    super(configuration);

    boolean hasRunnableListener = false;
    boolean hasTriggerableListener = false;
    for (Listener<PircBotX> listener : getConfiguration().getListenerManager().getListeners()) {
      if (listener instanceof RunnableListener && !hasRunnableListener) {
        configuration.getListenerManager().addListener(new ExecuteRunnableListenerAdapter());
        hasRunnableListener = true;
      }
      if (listener instanceof TriggerableListener && !hasTriggerableListener) {
        configuration.getListenerManager().addListener(new TriggerListenerAdapter());
        hasTriggerableListener = true;
      }

      if (listener instanceof PublicListener) {
        PublicListener publicListener = (PublicListener) listener;
        checkState(!Strings.isNullOrEmpty(publicListener.getTriggerMessage()),
            "Public module requires a trigger message: %s", listener.getClass().getSimpleName());
      }
      if (listener instanceof PrivateListener) {
        PrivateListener publicListener = (PrivateListener) listener;
        checkState(!Strings.isNullOrEmpty(publicListener.getPrivateTriggerMessage()),
            "Private module requires a trigger message: %s", listener.getClass().getSimpleName());
      }
    }
  }

  public boolean isUserOpOnAnyJoinedChannel(User user) {
    Set<Channel> channels = getUserBot().getChannels();
    // User may be found on any channel
    for (Channel channel : channels) {
      Set<User> ops = channel.getOps();
      for (User op : ops) {
        // If the user is found
        if (op.equals(user)) {
          return true;
        }
      }
    }
    // User not found or not op on any channel
    return false;
  }

  /**
   * Returns whether a quit action was requested on the bot by a user.
   *
   * @return {@code true} if quit action was requested, {@code false} otherwise
   */
  public final boolean isReconnectStopped() {
    return reconnectStopped;
  }

  /**
   * Returns the trigger used to display the help.
   *
   * @return the bot help trigger or {@code null} if none is available
   */
  public String getHelpTrigger() {
    for (Listener<PircBotX> listener : getConfiguration().getListenerManager().getListeners()) {
      if (listener instanceof HelpListener) {
        return ((HelpListener) listener).getTriggerMessage();
      }
    }
    // No help listener, no help trigger
    return null;
  }

  /**
   * Builds a list of lines to be sent to the user that gives help information on this bot. The
   * returned strings are arbitrary. You usually want to give the actions that can be triggered on
   * the bot, such as "{@code !news}", associated to their description.
   *
   * @param nick the nick of the user who requested help to be displayed
   * @param inPrivate {@code true} if the help request was made in a private chat, {@code false} if
   *        it was made on a public channel
   * @return a list of strings containing the help of this bot
   */
  public List<String> buildHelp(User user, boolean inPrivate) {
    List<String> help = new ArrayList<String>();

    Map<String, String> helpMap = new TreeMap<String, String>();
    if (inPrivate) {
      // Private
      boolean isUserOp = isUserOpOnAnyJoinedChannel(user);

      for (Listener<PircBotX> listener : getConfiguration().getListenerManager().getListeners()) {
        if (listener instanceof PrivateListener) {
          PrivateListener privateListener = (PrivateListener) listener;
          if (privateListener.isOpRequired() && !isUserOp) {
            // Module can only be displayed to ops
            continue;
          }

          String trigger = privateListener.getPrivateTriggerMessage();
          helpMap.put(trigger, buildHelpLine(trigger, privateListener));
        }
      }
    } else {
      // Public
      for (Listener<PircBotX> listener : getConfiguration().getListenerManager().getListeners()) {
        if (listener instanceof PublicListener) {
          PublicListener publicListener = (PublicListener) listener;
          String trigger = "!" + publicListener.getTriggerMessage();
          helpMap.put(trigger, buildHelpLine(trigger, publicListener));
        }
      }
    }
    help.addAll(new TreeMap<String, String>(helpMap).values());

    return help;
  }

  // internal helpers

  private String buildHelpLine(String trigger, TriggerableListener module) {
    if (!Strings.isNullOrEmpty(module.getHelpText())) {
      // We suppose commands are never bigger than 20 characters
      return Strings.padEnd(trigger, 20, ' ') + module.getHelpText();
    }
    return trigger;
  }
}
