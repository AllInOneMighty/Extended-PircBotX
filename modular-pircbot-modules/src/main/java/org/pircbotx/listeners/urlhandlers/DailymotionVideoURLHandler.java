package org.pircbotx.listeners.urlhandlers;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.net.InternetDomainName;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * A {@link VideoURLHandler} specifically made to retrieve video information from Dailymotion.
 *
 * @author Emmanuel Cron
 */
public class DailymotionVideoURLHandler extends VideoURLHandler {
  private class DailymotionApiVideoJson {
    private String id;
    private String title;
    @SerializedName("views_total")
    private long viewsTotal;
    private int duration;
    @SerializedName("owner.screenname")
    private String owner;
    @SerializedName("onair")
    private boolean onAir;
    private long audience;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(DailymotionVideoURLHandler.class);
  private static final String GRAPH_API_URL =
      "https://api.dailymotion.com/video/%s?fields=id,title,views_total,"
          + "duration,owner.screenname,onair,audience";
  private static final String DAILYMOTION_SHORT_URL = "http://www.dailymotion.com/video/%s";
  private static final String DAILYMOTION_COM = "dailymotion.com";

  private int connectTimeoutMillis;
  private int readTimeoutMillis;

  /**
   * Creates a new handler.
   *
   * @param connectTimeoutMillis millis after which the connection attempt is abandoned
   * @param readTimeoutMillis millis after which the read attempt is abandoned
   * @param format format to use when displaying video information on a channel
   */
  public DailymotionVideoURLHandler(int connectTimeoutMillis, int readTimeoutMillis,
      VideoInfoFormat format) {
    super(format);

    checkArgument(connectTimeoutMillis > 0, "Connect timeout must be > 0");
    checkArgument(readTimeoutMillis > 0, "Read timeout must be > 0");

    this.connectTimeoutMillis = connectTimeoutMillis;
    this.readTimeoutMillis = readTimeoutMillis;
  }

  @Override
  public int getUrlRequiredMinLength() {
    // dailymotion.com/video/<id>
    return 23;
  }

  @Override
  public boolean matches(URL url) {
    String host = InternetDomainName.from(url.getHost()).topPrivateDomain().toString();
    if (host.equalsIgnoreCase(DAILYMOTION_COM)) {
      return true;
    }
    return false;
  }

  @Override
  public VideoInfo retrieveVideoInfo(URL url) {
    String videoId = parseVideoId(url);
    if (Strings.isNullOrEmpty(videoId)) {
      return null;
    }

    URL apiVideoUrl;
    try {
      apiVideoUrl = new URL(String.format(GRAPH_API_URL, videoId));
    } catch (MalformedURLException e) {
      LOGGER.error("Could not format Dailymotion API URL with video id, ignoring", e);
      return null;
    }

    URLConnection connection;
    try {
      connection = apiVideoUrl.openConnection();
      connection.setConnectTimeout(connectTimeoutMillis);
      connection.setReadTimeout(readTimeoutMillis);
    } catch (IOException e) {
      LOGGER.error("Could not open connection to Dailymotion API", e);
      return null;
    }

    DailymotionApiVideoJson json;
    try (InputStream content = connection.getInputStream()) {
      Gson gson = new Gson();
      json = gson.fromJson(new InputStreamReader(content), DailymotionApiVideoJson.class);
    } catch (IOException e) {
      LOGGER.error("I/O exception while reading/parsing Dailymotion API response", e);
      return null;
    }

    URL videoUrl;
    try {
      videoUrl = new URL(String.format(DAILYMOTION_SHORT_URL, videoId));
    } catch (MalformedURLException e) {
      LOGGER.error("Could not format Dailymotion URL with video id, ignoring", e);
      return null;
    }
    VideoInfo videoInfo = new VideoInfo(videoUrl, json.title);
    if (!Strings.isNullOrEmpty(json.id)) {
      videoInfo.setId(json.id);
    }
    if (!Strings.isNullOrEmpty(json.owner)) {
      videoInfo.setUser(json.owner);
    }
    videoInfo.setDuration(new Duration(json.duration * 1000));
    videoInfo.setViews(json.viewsTotal);
    if (json.onAir) {
      videoInfo.setOnAir(true);
      videoInfo.setAudience(json.audience);
    }
    return videoInfo;
  }

  // internal helpers

  private String parseVideoId(URL url) {
    String path = url.getPath();
    if (path.length() < 8) {
      // Can't be of the form /video/<id>
      return null;
    }
    if (!path.startsWith("/video/")) {
      return null;
    }
    path = path.substring(7);

    int underscore = path.indexOf('_');
    // No id
    if (underscore <= 0) {
      return path;
    }
    return path.substring(0, underscore);
  }
}
