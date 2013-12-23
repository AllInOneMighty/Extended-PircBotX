package org.jibble.pircbot.listeners.urlhandlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.pircbotx.listeners.urlhandlers.VideoInfo;
import org.pircbotx.listeners.urlhandlers.VideoInfoFormat;
import org.pircbotx.listeners.urlhandlers.YouTubeVideoURLHandler;

public class YouTubeVideoURLHandlerTest {
  private YouTubeVideoURLHandler handler;

  @Before
  public void setUp() {
    handler =
        new YouTubeVideoURLHandler("HypeBotTest", "fr", 3000, 3000,
            VideoInfoFormat.getDefaultInstance());
  }

  @Test
  public void matches() throws MalformedURLException {
    assertTrue(handler.matches(new URL("http://youtube.com")));
    assertTrue(handler.matches(new URL("http://youtube.com/v/lfkew")));
    assertTrue(handler.matches(new URL("http://www.youtube.com")));
    assertTrue(handler.matches(new URL("https://www.youtube.com")));

    assertTrue(handler.matches(new URL("http://youtu.be")));
    assertTrue(handler.matches(new URL("http://youtu.be/v/efw6588")));
    assertFalse(handler.matches(new URL("http://www.youtu.be")));
    assertFalse(handler.matches(new URL("https://www.youtu.be")));

    assertFalse(handler.matches(new URL("http://www.godizilla.com")));
  }

  @Test
  public void retrieveVideoInfo() throws MalformedURLException {
    assertNull(handler.retrieveVideoInfo(new URL("http://www.google.com/watch?v=ZOf7Jb5Ylg8")));
    assertNull(handler.retrieveVideoInfo(new URL("http://youtu.be/")));
    assertNull(handler.retrieveVideoInfo(new URL("http://youtube.com/watch")));
    assertNull(handler.retrieveVideoInfo(new URL("http://youtube.com/watch?")));
    assertNull(handler.retrieveVideoInfo(new URL("http://youtube.com/watch?v=")));

    VideoInfo videoInfo0 =
        handler.retrieveVideoInfo(new URL("http://youtube.com/watch?v=ZOf7Jb5Ylg8"));
    assertNotNull(videoInfo0);
    assertEquals("ZOf7Jb5Ylg8", videoInfo0.getId());
    assertEquals(new URL("http://youtu.be/ZOf7Jb5Ylg8"), videoInfo0.getUrl());
    assertEquals("BlizzCon 2013: On a retrouvé l'épouse du Roi Liche !", videoInfo0.getTitle());
    assertEquals("JudgeHype", videoInfo0.getUser());
    assertEquals("Jeux vidéo et autres", videoInfo0.getCategory());
    assertEquals(new Duration(44000L), videoInfo0.getDuration());
    assertTrue(videoInfo0.getLikes() > 10);
    assertTrue(videoInfo0.getDislikes() >= 0);
    assertTrue(videoInfo0.getViews() >= 12229);
    assertFalse(videoInfo0.isOnAir());

    VideoInfo videoInfo1 =
        handler.retrieveVideoInfo(new URL("http://www.youtube.com/v/_6Ogb7FWr1A"));
    assertNotNull(videoInfo1);
    assertEquals("_6Ogb7FWr1A", videoInfo1.getId());
    assertEquals(new URL("http://youtu.be/_6Ogb7FWr1A"), videoInfo1.getUrl());
    assertEquals("World of Warcraft - PTR 5.4.2: Monture Saccage ciel de Fer (Iron Skyreaver)",
        videoInfo1.getTitle());
    assertEquals("JudgeHype", videoInfo1.getUser());
    assertEquals("Jeux vidéo et autres", videoInfo1.getCategory());
    assertEquals(new Duration(58000L), videoInfo1.getDuration());
    assertTrue(videoInfo1.getLikes() > 5);
    assertTrue(videoInfo1.getDislikes() >= 0);
    assertTrue(videoInfo1.getViews() >= 4212);
    assertFalse(videoInfo1.isOnAir());

    VideoInfo videoInfo2 =
        handler.retrieveVideoInfo(new URL("http://www.youtube.com/watch?v=hfecYn-UEQg"));
    assertNotNull(videoInfo2);
    assertEquals("hfecYn-UEQg", videoInfo2.getId());
    assertEquals(new URL("http://youtu.be/hfecYn-UEQg"), videoInfo2.getUrl());
    assertEquals("Heroes of the Storm : Scènes de gameplay (BlizzCon 2013)", videoInfo2.getTitle());
    assertEquals("JudgeHype", videoInfo2.getUser());
    assertEquals("Jeux vidéo et autres", videoInfo2.getCategory());
    assertEquals(new Duration(302000L), videoInfo2.getDuration());
    assertTrue(videoInfo2.getLikes() > 20);
    assertTrue(videoInfo2.getDislikes() >= 0);
    assertTrue(videoInfo2.getViews() >= 8373);
    assertFalse(videoInfo2.isOnAir());
  }
}
