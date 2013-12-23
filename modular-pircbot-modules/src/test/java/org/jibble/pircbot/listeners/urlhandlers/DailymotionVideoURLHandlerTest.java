package org.jibble.pircbot.listeners.urlhandlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.listeners.urlhandlers.DailymotionVideoURLHandler;
import org.pircbotx.listeners.urlhandlers.VideoInfo;
import org.pircbotx.listeners.urlhandlers.VideoInfoFormat;

public class DailymotionVideoURLHandlerTest {
  private DailymotionVideoURLHandler handler;

  @Before
  public void setUp() {
    handler = new DailymotionVideoURLHandler(1000, 3000, VideoInfoFormat.getDefaultInstance());
  }

  @Test
  public void matches() throws MalformedURLException {
    assertTrue(handler.matches(new URL("http://dailymotion.com")));
    assertTrue(handler.matches(new URL("http://dailymotion.com/v/lfkew")));
    assertTrue(handler.matches(new URL("http://www.dailymotion.com")));
    assertTrue(handler.matches(new URL("https://www.dailymotion.com")));

    assertFalse(handler.matches(new URL("https://www.dailmotion.com")));
  }

  @Test
  public void retrieveVideoInfo() throws MalformedURLException {
    VideoInfo videoInfo0 = handler.retrieveVideoInfo(
        new URL("http://www.dailymotion.com/video/x18dpu3_league-of-legends-cosplayer-"
            + "taking-it-to-the-next-level_tech"));
    assertNotNull(videoInfo0);
    assertEquals("x18dpu3", videoInfo0.getId());
    assertEquals(new URL("http://www.dailymotion.com/video/x18dpu3"), videoInfo0.getUrl());
    assertEquals("League of Legends Cosplayer taking it to the next level", videoInfo0.getTitle());
    assertEquals("Cosplay Boom", videoInfo0.getUser());
    assertEquals(new Duration(210000L), videoInfo0.getDuration());
    assertTrue(videoInfo0.getViews() >= 3419);
    assertFalse(videoInfo0.isOnAir());
  }
}
