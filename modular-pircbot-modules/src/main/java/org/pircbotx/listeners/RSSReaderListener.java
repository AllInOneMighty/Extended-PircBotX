package org.pircbotx.listeners;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.ExtendedPircBotX;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.util.urlshorteners.URLShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.DiskFeedInfoCache;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

/**
 * Displays any new item of a given RSS feed in all public channels the bot has joined. Users can
 * also ask the bot to send the last 3 entries (amount is customizable) as {@code NOTICE} when in a
 * public channel or as a normal message when talking privately to the bot.
 * <p>
 * This listener supports the use of an URL shortener service to display smaller messages when
 * announcing new RSS news. If you want to use such a service, call the
 * {@link #setURLShortener(URLShortener)} method.
 * <p>
 * Feeds retrieved from the web are automatically cached in a flat file by the underlying feed
 * fetcher. Additionally, the listener is limited to one fetch attempt every given interval
 * (interval set when creating the bot) to avoid spamming the server that hosts it.
 *
 * @author Emmanuel Cron
 */
public class RSSReaderListener extends ListenerAdapter<PircBotX> implements RunnableListener,
    PublicListener, PrivateListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(RSSReaderListener.class);

  private String trigger;

  private String helpMessage;

  private FeedFetcherCache feedFetcherCache;

  private URL feedURL;

  private SyndFeed lastFeedRetrieved;

  private int checkIntervalMillis;

  private URLShortener urlShortener;

  private Date lastAnnouncedPublishDate = new Date(0L);

  private int defaultToDisplay = 3;

  private PircBotX bot;

  private boolean run;

  /**
   * Creates a new RSS reader listener.
   *
   * @param trigger the word to say in a public or private chat to trigger the display of the last
   *        news; in a public channel, this word must be prefixed by "{@code !}"
   * @param cachePath where to store the cache files of the retrieved feeds
   * @param feedURL URL where to find the feed to retrieve
   * @param checkInterval interval, in seconds, between two feed fetching operations
   */
  public RSSReaderListener(String trigger, Path cachePath, URL feedURL, int checkInterval) {
    checkArgument(!Strings.isNullOrEmpty(trigger));
    checkNotNull(cachePath, "No cache path specified");
    if (!Files.exists(cachePath)) {
      try {
        Files.createDirectories(cachePath);
      } catch (IOException ioe) {
        throw new IllegalStateException("Could not create necessary directory for feed cache", ioe);
      }
    }
    checkArgument(Files.isDirectory(cachePath), "Cache path isn't a directory: %s", cachePath
        .toAbsolutePath().toString());
    checkArgument(checkInterval > 0, "RSS feed check interval must be > 0");

    this.trigger = trigger;
    this.feedFetcherCache = new DiskFeedInfoCache(cachePath.toString());
    this.feedURL = checkNotNull(feedURL);
    this.checkIntervalMillis = checkInterval * 1000;
  }

  /**
   * Sets how many news to display when requesting the latest news by using the trigger command. If
   * there are less news in the feed that this amount, only the available news are displayed.
   *
   * @param defaultToDisplay number of news to display
   */
  public void setDefaultToDisplay(int defaultToDisplay) {
    this.defaultToDisplay = defaultToDisplay;
  }

  /**
   * Sets a service to shorten URLs of the items contained in the RSS feed, if they have one.
   *
   * @param urlShortener an URL shortener service, or <tt>null</tt> if you want to remove one
   *        previously set
   */
  public void setURLShortener(URLShortener urlShortener) {
    this.urlShortener = urlShortener;
  }

  @Override
  public String getTriggerMessage() {
    return trigger;
  }

  public void setHelp(String helpMessage) {
    // Can accept nulls to clear the message
    this.helpMessage = helpMessage;
  }

  @Override
  public String getHelpText() {
    return helpMessage;
  }

  @Override
  public String getPrivateTriggerMessage() {
    return trigger;
  }

  @Override
  public boolean isOpRequired() {
    return false;
  }

  @Override
  public void setBot(PircBotX bot) {
    this.bot = bot;
  }

  @Override
  public void run() {
    run = true;
    long nextCheck = 0L;
    do {
      // Either retrieve or sleep; don't do both on the same loop
      if (System.currentTimeMillis() > nextCheck) {
        SyndFeed feed = retrieveFeed();
        if (feed != null) {
          lastFeedRetrieved = feed;
          announceUndisplayedNews(bot, lastFeedRetrieved);
        }
        nextCheck = System.currentTimeMillis() + checkIntervalMillis;
      } else {
        try {
          Thread.sleep(500);
        } catch (InterruptedException ie) {
          LOGGER.error(
              "Couldn't put thread to sleep; to avoid CPU overheat, thread will be killed", ie);
          run = false;
        }
      }
    } while (run);
  }

  @Override
  public void stop() {
    run = false;
  }

  @Override
  public void onTriggerMessage(MessageEvent<ExtendedPircBotX> event) {
    if (lastFeedRetrieved == null) {
      event.getUser().send()
          .notice("I am currently retrieving the news, please retry in a few seconds!");
      return;
    }

    // Now send info to the user
    @SuppressWarnings("unchecked")
    List<SyndEntry> entries = lastFeedRetrieved.getEntries();
    for (int i = 0; i < defaultToDisplay && i < entries.size(); i++) {
      event.getUser().send().notice(buildMessageFromNewsEntry(entries.get(i)));
    }
  }

  @Override
  public void onTriggerPrivateMessage(PrivateMessageEvent<ExtendedPircBotX> event) {
    if (lastFeedRetrieved == null) {
      event.getUser().send()
          .message("I am currently retrieving the news, please retry in a few seconds!");
      return;
    }

    // Now send info to the user
    @SuppressWarnings("unchecked")
    List<SyndEntry> entries = lastFeedRetrieved.getEntries();
    for (int i = 0; i < defaultToDisplay && i < entries.size(); i++) {
      event.getUser().send().message(buildMessageFromNewsEntry(entries.get(i)));
    }
  }

  // internal helpers

  private String buildMessageFromNewsEntry(SyndEntry entry) {
    String url = entry.getLink();
    if (urlShortener != null) {
      try {
        url = urlShortener.shortenURL(url);
      } catch (IOException ioe) {
        LOGGER.error("Could not shorten URL, using normal URL instead", ioe);
      }
    }

    return "[\u0002News\u0002] " + entry.getTitle() + " - " + url;
  }

  private void announceUndisplayedNews(PircBotX bot, SyndFeed feed) {
    // Safety check
    if (feed == null) {
      return;
    }

    Date mostRecentPublishDate = null;

    @SuppressWarnings("unchecked")
    List<SyndEntry> entries = feed.getEntries();
    int displayed = 0;
    while (displayed < defaultToDisplay && displayed < entries.size()) {
      SyndEntry entry = entries.get(displayed);

      // Save most recent date
      if (mostRecentPublishDate == null) {
        if (entry.getPublishedDate() == null) {
          // Feed entry date is null, forcing it to now
          mostRecentPublishDate = new Date();
          entry.setPublishedDate(mostRecentPublishDate);
        } else {
          mostRecentPublishDate = entry.getPublishedDate();
        }
      }

      if (entry.getPublishedDate().after(lastAnnouncedPublishDate)) {
        // Announce the latest news
        for (Channel channel : bot.getUserBot().getChannels()) {
          channel.send().message(buildMessageFromNewsEntry(entry));
        }
        displayed++;
      } else {
        // Stop as soon as we encounter news older than the last
        // announced publish date
        break;
      }
    }
    lastAnnouncedPublishDate = mostRecentPublishDate;
  }

  private SyndFeed retrieveFeed() {
    FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedFetcherCache);
    SyndFeed feed = null;
    try {
      feed = feedFetcher.retrieveFeed(feedURL);
    } catch (FetcherException fe) {
      LOGGER.warn("Could not retrieve feed", fe);
    } catch (FeedException fe) {
      LOGGER.error("Feed is of invalid format", fe);
    } catch (IOException ioe) {
      LOGGER.warn("I/O Exception while retrieving feed", ioe);
    }
    return feed;
  }
}
