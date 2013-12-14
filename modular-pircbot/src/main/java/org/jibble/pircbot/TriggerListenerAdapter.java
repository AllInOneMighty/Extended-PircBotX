package org.jibble.pircbot;

import org.jibble.pircbot.listeners.PrivateListener;
import org.jibble.pircbot.listeners.PublicListener;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
              LOGGER.info("User {} cannot trigger {} module because he/she is not op", event
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
