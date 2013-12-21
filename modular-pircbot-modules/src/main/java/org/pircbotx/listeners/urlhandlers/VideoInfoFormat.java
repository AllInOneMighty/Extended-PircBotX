package org.pircbotx.listeners.urlhandlers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Format to use when displaying video information on a channel. Mainly used for localization. You
 * can get the default instance by calling {@link #getDefaultInstance()}.
 *
 * @author Emmanuel Cron
 */
public final class VideoInfoFormat {
  private final String by;
  private final String in;
  private final String length;
  private final String hours;
  private final String minutes;
  private final String seconds;
  private final String likes;
  private final String dislikes;
  private final String views;

  public static class Builder {
    private String by;
    private String in;
    private String length;
    private String hours;
    private String minutes;
    private String seconds;
    private String likes;
    private String dislikes;
    private String views;

    public Builder setBy(String by) {
      this.by = checkNotNull(by);
      return this;
    }

    public Builder setIn(String in) {
      this.in = checkNotNull(in);
      return this;
    }

    public Builder setLength(String length) {
      this.length = checkNotNull(length);
      return this;
    }

    public Builder setHours(String hours) {
      this.hours = checkNotNull(hours);
      return this;
    }

    public Builder setMinutes(String minutes) {
      this.minutes = checkNotNull(minutes);
      return this;
    }

    public Builder setSeconds(String seconds) {
      this.seconds = checkNotNull(seconds);
      return this;
    }

    public Builder setLikes(String likes) {
      this.likes = checkNotNull(likes);
      return this;
    }

    public Builder setDislikes(String dislikes) {
      this.dislikes = checkNotNull(dislikes);
      return this;
    }

    public Builder setViews(String views) {
      this.views = checkNotNull(views);
      return this;
    }

    public VideoInfoFormat build() {
      return new VideoInfoFormat(this);
    }
  }

  private VideoInfoFormat(Builder builder) {
    this.by = builder.by != null ? builder.by : "by";
    this.in = builder.in != null ? builder.in : "in";
    this.length = builder.length != null ? builder.length : "length";
    this.hours = builder.hours != null ? builder.hours : "hours";
    this.minutes = builder.minutes != null ? builder.minutes : "minutes";
    this.seconds = builder.seconds != null ? builder.seconds : "seconds";
    this.likes = builder.likes != null ? builder.likes : "likes";
    this.dislikes = builder.dislikes != null ? builder.dislikes : "dislikes";
    this.views = builder.views != null ? builder.views : "views";
  }

  public static VideoInfoFormat getDefaultInstance() {
    // Will use the default strings
    return new VideoInfoFormat(new Builder());
  }

  public String getBy() {
    return by;
  }

  public String getIn() {
    return in;
  }

  public String getLength() {
    return length;
  }

  public String getHours() {
    return hours;
  }

  public String getMinutes() {
    return minutes;
  }

  public String getSeconds() {
    return seconds;
  }

  public String getLikes() {
    return likes;
  }

  public String getDislikes() {
    return dislikes;
  }

  public String getViews() {
    return views;
  }
}
