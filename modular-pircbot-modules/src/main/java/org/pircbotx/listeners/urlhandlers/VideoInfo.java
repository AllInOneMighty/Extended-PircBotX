package org.pircbotx.listeners.urlhandlers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import org.joda.time.Duration;

import com.google.common.base.Strings;

/**
 * Information about a video on the internet.
 *
 * @author Emmanuel Cron
 */
public class VideoInfo {
  private String id;
  private URL url;
  private String title;
  private String user;
  private String category;
  private Duration duration;
  private int likes;
  private int dislikes;
  private long views;
  private boolean onAir;
  private long audience;

  public VideoInfo(URL url, String title) {
    checkArgument(!Strings.isNullOrEmpty(title));

    this.url = checkNotNull(url);
    this.title = title;
  }

  public URL getUrl() {
    return url;
  }

  /**
   * The title of the video. Never {@code null} nor empty.
   */
  public String getTitle() {
    return title;
  }

  protected void setId(String id) {
    checkArgument(!Strings.isNullOrEmpty(id));
    this.id = id;
  }

  /**
   * Returns the id of this video as a resource that can be used on an API.
   */
  public String getId() {
    return id;
  }

  protected void setUser(String user) {
    checkArgument(!Strings.isNullOrEmpty(user));
    this.user = user;
  }

  /**
   * The name/nickname of the user who own the video, usually the uploader. Can be {@code null}.
   */
  public String getUser() {
    return user;
  }

  protected void setCategory(String category) {
    checkArgument(!Strings.isNullOrEmpty(category));
    this.category = category;
  }

  /**
   * The category of this video as a literal string ('Games', 'Music', ...). Can be {@code null}.
   */
  public String getCategory() {
    return category;
  }

  protected void setDuration(Duration duration) {
    this.duration = checkNotNull(duration);
  }

  /**
   * The duration of this video. Can be {@code null}.
   */
  public Duration getDuration() {
    return duration;
  }

  protected void setLikes(int likes) {
    this.likes = likes;
  }

  /**
   * The number of likes/+1/... that this video received. If this data could not be found, the
   * method returns {@code 0}.
   */
  public int getLikes() {
    return likes;
  }

  protected void setDislikes(int dislikes) {
    this.dislikes = dislikes;
  }

  /**
   * The number of dislikes/-1/... that this video received. If this data could not be found, the
   * method returns {@code 0}.
   */
  public int getDislikes() {
    return dislikes;
  }

  protected void setViews(long views) {
    this.views = views;
  }

  /**
   * The number of times this video was viewed. If this data could not be found, the method returns
   * {@code 0}.
   */
  public long getViews() {
    return views;
  }

  protected void setOnAir(boolean onAir) {
    this.onAir = onAir;
  }

  /**
   * If this video is currently being streamed lived on the web.
   */
  public boolean isOnAir() {
    return onAir;
  }

  protected void setAudience(long audience) {
    this.audience = audience;
  }

  /**
   * If this video {@link #isOnAir()}, returns the current number of viewers.
   */
  public long getAudience() {
    return audience;
  }
}
