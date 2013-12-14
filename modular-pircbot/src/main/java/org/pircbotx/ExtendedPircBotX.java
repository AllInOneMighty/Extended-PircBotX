package org.pircbotx;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.listeners.HelpListener;
import org.pircbotx.listeners.PrivateListener;
import org.pircbotx.listeners.PublicListener;
import org.pircbotx.listeners.RunnableListener;
import org.pircbotx.listeners.TriggerableListener;

import com.google.common.base.Strings;

/**
 * An extended {@link PircBotX} that supports triggerable and runnable listeners.
 * <p>
 * Listeners can be made runnable by extending {@link RunnableListener}, so you can make them do
 * some background work while the bot is running. The bot will automatically run and stop these
 * listeners when it connects to or completely quits from the IRC server.
 * <p>
 * Listeners can be made triggerable by extending {@link PrivateListener} and/or
 * {@link PublicListener} and implementing the appropriate trigger methods. Trigger detection is
 * automatically handled by the bot.
 * <p>
 * You can use your listeners by adding them to the {@link Configuration} given to the constructor
 * of this class. For example, you can have help support by adding the {@link HelpListener} to this
 * bot.
 *
 * @author Emmanuel Cron
 */
public class ExtendedPircBotX extends PircBotX {
  /**
   * Creates a new extended {@link PircBotX} using the given configuration to configure the bot.
   * This method makes sure runnable and triggerable listeners are correctly detected.
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
            "Public listener requires a trigger message: %s", listener.getClass().getSimpleName());
      }
      if (listener instanceof PrivateListener) {
        PrivateListener publicListener = (PrivateListener) listener;
        checkState(!Strings.isNullOrEmpty(publicListener.getPrivateTriggerMessage()),
            "Private listener requires a trigger message: %s", listener.getClass().getSimpleName());
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
    // User not found or not OP on any channel
    return false;
  }

  /**
   * Returns whether the bot will automatically reconnect after a disconnection.
   */
  public final boolean isReconnectStopped() {
    return reconnectStopped;
  }

  /**
   * Returns the trigger used to display the help.
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
   * returned strings list the actions that can be triggered on the bot, such as "{@code !news}",
   * associated to their description.
   *
   * @param user user that requested to display the help
   * @param inPrivate {@code true} if the help request was made in a private chat, {@code false} if
   *        it was made on a public channel
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
            // Listener can only be displayed to ops
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

  private String buildHelpLine(String trigger, TriggerableListener listener) {
    if (!Strings.isNullOrEmpty(listener.getHelpText())) {
      // We suppose commands are never bigger than 20 characters
      return Strings.padEnd(trigger, 20, ' ') + listener.getHelpText();
    }
    return trigger;
  }
}
