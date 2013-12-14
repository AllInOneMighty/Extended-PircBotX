package org.pircbotx;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.listeners.PrivateListener;
import org.pircbotx.listeners.PublicListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A special listener that calls the trigger methods of {@link PublicListener}s and
 * {@link PrivateListener}s if their associated triggers are seen in a public or private chat.
 *
 * @author Emmanuel Cron
 */
class TriggerListenerAdapter extends ListenerAdapter<ExtendedPircBotX> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TriggerListenerAdapter.class);

  @Override
  public void onMessage(MessageEvent<ExtendedPircBotX> event) {
    for (Listener<PircBotX> listener : event.getBot().getConfiguration().getListenerManager()
        .getListeners()) {
      if (listener instanceof PublicListener) {
        PublicListener publicListener = (PublicListener) listener;
        String triggerMessage = "!" + publicListener.getTriggerMessage();
        if (event.getMessage().equalsIgnoreCase(triggerMessage)) {
          publicListener.onTriggerMessage(event);
        }
      }
    }
  }

  @Override
  public void onPrivateMessage(PrivateMessageEvent<ExtendedPircBotX> event) {
    Boolean isSenderOp = null;

    for (Listener<PircBotX> listener : event.getBot().getConfiguration().getListenerManager()
        .getListeners()) {
      if (listener instanceof PrivateListener) {
        PrivateListener privateListener = (PrivateListener) listener;

        // Is message a trigger?
        if (event.getMessage().equalsIgnoreCase(privateListener.getPrivateTriggerMessage())) {
          // Is op required?
          if (privateListener.isOpRequired()) {
            // Check if user is op only the first time
            if (isSenderOp == null) {
              isSenderOp = event.getBot().isUserOpOnAnyJoinedChannel(event.getUser());
            }

            if (!isSenderOp) {
              // Op required but user not op, skipping
              LOGGER.info("User {} cannot trigger {} listener because he/she is not op", event
                  .getUser().getNick(), listener.getClass());
              continue;
            }
          }

          privateListener.onTriggerPrivateMessage(event);
        }
      }
    }
  }
}
