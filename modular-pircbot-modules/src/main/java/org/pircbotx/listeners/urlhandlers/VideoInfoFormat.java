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
  private final String live;
  private final String viewers;

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
    private String live;
    private String viewers;

    /**
     * Precedes the video author name.
     *
     * <pre>
     * by {author}
     * </pre>
     */
    public Builder setBy(String by) {
      this.by = checkNotNull(by).trim();
      return this;
    }

    /**
     * Precedes the category name.
     *
     * <pre>
     * in {category}
     * </pre>
     */
    public Builder setIn(String in) {
      this.in = checkNotNull(in).trim();
      return this;
    }

    /**
     * Precedes the length details of the video. You might want to capitalize the first char.
     *
     * <pre>
     * Length: {length}
     * </pre>
     */
    public Builder setLength(String length) {
      this.length = checkNotNull(length).trim();
      return this;
    }

    /**
     * Suffixes the length, in hours, of the video.
     *
     * <pre>
     * {hours} hours
     * </pre>
     */
    public Builder setHours(String hours) {
      this.hours = checkNotNull(hours).trim();
      return this;
    }

    /**
     * Suffixes the length, in minutes within an hour, of the video.
     *
     * <pre>
     * {minutes} mins
     * </pre>
     */
    public Builder setMinutes(String minutes) {
      this.minutes = checkNotNull(minutes).trim();
      return this;
    }

    /**
     * Suffixes the length, in seconds within a minute, of the video.
     *
     * <pre>
     * {seconds} secs
     * </pre>
     */
    public Builder setSeconds(String seconds) {
      this.seconds = checkNotNull(seconds).trim();
      return this;
    }

    /**
     * Suffixes the number of likes received by the video.
     *
     * <pre>
     * {likes} likes
     * </pre>
     */
    public Builder setLikes(String likes) {
      this.likes = checkNotNull(likes).trim();
      return this;
    }

    /**
     * Suffixes the number of dislikes received by the video.
     *
     * <pre>
     * {dislikes} dislikes
     * </pre>
     */
    public Builder setDislikes(String dislikes) {
      this.dislikes = checkNotNull(dislikes).trim();
      return this;
    }

    /**
     * Suffixes the number of views of the video.
     *
     * <pre>
     * {views} views
     * </pre>
     */
    public Builder setViews(String views) {
      this.views = checkNotNull(views).trim();
      return this;
    }

    /**
     * Indicates the video is live (= being streamed). Default value is "{@code LIVE}".
     *
     * <pre>
     * {live}
     * </pre>
     */
    public Builder setLive(String live) {
      this.live = checkNotNull(live).trim();
      return this;
    }

    /**
     * Suffixes the number of viewers of the video in live streaming.
     *
     * <pre>
     * ({viewers} viewers)
     * </pre>
     */
    public Builder setViewers(String viewers) {
      this.viewers = checkNotNull(viewers).trim();
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
    this.live = builder.live != null ? builder.live : "LIVE";
    this.viewers = builder.viewers != null ? builder.viewers : "viewers";
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

  public String getLive() {
    return live;
  }

  public String getViewers() {
    return viewers;
  }
}
