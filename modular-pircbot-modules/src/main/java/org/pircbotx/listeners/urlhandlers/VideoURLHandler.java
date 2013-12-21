package org.pircbotx.listeners.urlhandlers;

import java.net.URL;

import org.joda.time.Duration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.google.common.base.Strings;

/**
 * A URL handler specifically designed to retrieve video information as a {@link VideoInfo}. Classes
 * extending this one must implement a single method, {@link #retrieveVideoInfo(URL)}, that gathers
 * data about the video found at the given link.
 *
 * @author Emmanuel Cron
 */
abstract class VideoURLHandler implements URLHandler {

  @Override
  public final void handle(MessageEvent<PircBotX> event, URL url) {
    VideoInfo videoInfo = retrieveVideoInfo(url);
    if (videoInfo != null) {
      event.getChannel().send().message(buildVideoDescription(videoInfo));
    }
  }

  /**
   * Retrieves information of the video found at the given URL in a {@link VideoInfo} and returns
   * it. This method must be prepared to receive URLs that do not point to a video. If no video
   * could be found or its information was unavailable, it should return {@code null}.
   */
  protected abstract VideoInfo retrieveVideoInfo(URL url);

  // internal helpers

  private String buildVideoDescription(VideoInfo videoInfo) {
    StringBuilder videoTitle = new StringBuilder("'" + videoInfo.getTitle() + "'");
    if (!Strings.isNullOrEmpty(videoInfo.getUser())) {
      videoTitle.append(" by " + videoInfo.getUser());
    }
    if (!Strings.isNullOrEmpty(videoInfo.getCategory())) {
      videoTitle.append(" in " + videoInfo.getCategory());
    }
    videoTitle.append(" -");
    if (videoInfo.getDuration() != null) {
      boolean displayed = false;
      Duration duration = videoInfo.getDuration();
      if (duration.getStandardHours() > 0) {
        videoTitle.append(" " + duration.getStandardHours() + " hours");
        displayed = true;
      }
      if (displayed || duration.getStandardMinutes() > 0) {
        videoTitle.append(" " + duration.getStandardHours() + " mins");
        displayed = true;
      }
      if (displayed || duration.getStandardSeconds() > 0) {
        videoTitle.append(" " + duration.getStandardHours() + " secs");
      }
    }
    if (videoInfo.getLikes() > 0) {
      videoTitle.append(" " + videoInfo.getLikes() + " likes");
    }
    if (videoInfo.getDislikes() > 0) {
      videoTitle.append(" / " + videoInfo.getDislikes() + " dislikes");
    }
    if (videoInfo.getViews() > 0) {
      videoTitle.append(", " + videoInfo.getViews() + " views");
    }

    // Removing the dash if that what's left
    if (videoTitle.toString().endsWith(" -")) {
      videoTitle.delete(videoTitle.length() - 2, videoTitle.length());
    }
    return videoTitle.toString();
  }
}
