package org.pircbotx;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.listeners.RunnableListener;
import org.pircbotx.listeners.StoppableListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A special listener that starts and stop runnable listeners when the bot first connects to the
 * server and shuts them down when it completely disconnects from it.
 *
 * @author Emmanuel Cron
 */
class ExecuteRunnableListenerAdapter extends ListenerAdapter<ExtendedPircBotX> {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ExecuteRunnableListenerAdapter.class);

  private ThreadGroup threadGroup = new ThreadGroup(getClass().getSimpleName());

  private boolean listenersStarted;

  @Override
  public void onConnect(ConnectEvent<ExtendedPircBotX> event) {
    if (!listenersStarted) {
      for (Listener<PircBotX> listener : event.getBot().getConfiguration().getListenerManager()
          .getListeners()) {
        if (listener instanceof RunnableListener) {
          RunnableListener runnableListener = (RunnableListener) listener;
          runnableListener.setBot(event.getBot());
          LOGGER.info("Launching listener thread: {}", runnableListener);
          new Thread(threadGroup, runnableListener).start();
        }
      }
      listenersStarted = true;
    }
  }

  @Override
  public void onDisconnect(DisconnectEvent<ExtendedPircBotX> event) {
    if (!event.getBot().isReconnectStopped()) {
      // Not a requested quit
      return;
    }

    // Quit requested, stopping threads and exiting
    for (Listener<PircBotX> listener : event.getBot().getConfiguration().getListenerManager()
        .getListeners()) {
      if (listener instanceof StoppableListener) {
        StoppableListener stoppableListener = (StoppableListener) listener;
        stoppableListener.stop();
      }
    }

    int stopChecks = 0;
    do {
      stopChecks++;
      // Wait a maximum of 15s
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ie) {
        LOGGER.error("Could not wait until threads were stopped, some might be killed", ie);
      }
    } while (stopChecks < 3 && threadGroup.activeCount() > 0);

    if (threadGroup.activeCount() > 0) {
      LOGGER.warn("One or more thread are still running, they will be killed");
    }
  }
}
