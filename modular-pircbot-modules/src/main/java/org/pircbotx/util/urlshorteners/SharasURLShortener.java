package org.pircbotx.util.urlshorteners;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * Shortens URLs using the <a href="http://shar.as/">http://shar.as/</a> service. This class can
 * optionally cache all shortened URLs. It is recommended if you often see URLs that are likely to
 * be reused by the bot.
 * 
 * @author Emmanuel Cron
 */
public class SharasURLShortener implements URLShortener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SharasURLShortener.class);

  private static final String SERVICE_URL = "http://shar.as/geturl.php?url=%s";

  private static final String CACHE_FILE_NAME = "shar.as-cache";

  private Map<String, String> cache;

  private Optional<Path> optCachePath;

  /**
   * Creates a new shortener, using the provided path as folder to store the cache of the shortened
   * URLs.
   * 
   * @param cachePath path to a local folder where to store the cache file
   */
  public SharasURLShortener(Optional<Path> cachePath) {
    this.optCachePath = checkNotNull(cachePath);
  }

  @Override
  public String shortenURL(String url) throws IOException {
    if (cache == null) {
      loadCache();
    }

    if (cache.containsKey(url)) {
      return cache.get(url);
    }

    URL finalURL = new URL(String.format(SERVICE_URL, url));
    URLConnection connection = finalURL.openConnection();
    Object content = connection.getContent();
    if (content instanceof InputStream) {
      try (InputStream input = (InputStream) content) {
        StringBuilder sharasBuilder = new StringBuilder();

        byte[] buffer = new byte[512];
        int read;
        while ((read = input.read(buffer)) >= 0) {
          sharasBuilder.append(new String(buffer, 0, read));
        }

        String sharas = sharasBuilder.toString();
        cache.put(url, sharas);
        saveCache();
        return sharas;
      }
    }
    throw new UnsupportedOperationException("Can't identify type returned by server: " + content);
  }

  private void loadCache() {
    LOGGER.info("Loading shar.as URL cache");

    // In case cache could not be loaded (otherwise this will be overwritten)
    cache = new HashMap<String, String>();

    if (!optCachePath.isPresent()) {
      LOGGER.warn("Cache path is not set, using memory-only cache");
      return;
    }

    Path cachePath = optCachePath.get();
    if (!Files.isDirectory(cachePath)) {
      try {
        LOGGER.info("Creating cache folder: {}", cachePath.toAbsolutePath().toString());
        Files.createDirectories(cachePath);
      } catch (IOException ioe) {
        LOGGER.error("Could not create cache folder, using memory-only cache", ioe);
        optCachePath = Optional.absent();
        return;
      }
    }

    File cacheFile = cachePath.resolve(Paths.get(CACHE_FILE_NAME)).toFile();
    if (!cacheFile.exists()) {
      LOGGER.info("Cache file does not exist, creating new cache in memory: {}", cachePath
          .toAbsolutePath().toString());
      return;
    }

    LOGGER.info("Cache found at: {}", cacheFile.getAbsolutePath());

    try (InputStream inputStream = new FileInputStream(cacheFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream)) {
      cache = (Map<String, String>) objectInputStream.readObject();
    } catch (ClassNotFoundException cnfe) {
      LOGGER.error("Could not find class for object in cache file. Is the cache file corrupt?",
          cnfe);
    } catch (IOException ioe) {
      LOGGER.error("Could not read from cache file", ioe);
    }
  }

  private synchronized void saveCache() {
    if (!optCachePath.isPresent()) {
      // No need for a warning; just ignoring the save
      return;
    }

    Path cachePath = optCachePath.get();
    if (!Files.isDirectory(cachePath)) {
      LOGGER.error("Cache file folder does not exist, aborting save: {}", cachePath
          .toAbsolutePath().toString());
      return;
    }

    File cacheFile = cachePath.resolve(Paths.get(CACHE_FILE_NAME)).toFile();

    try {
      cacheFile.createNewFile();

      try (OutputStream outputStream = new FileOutputStream(cacheFile);
          BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
          ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
        objectOutputStream.writeObject(cache);
      }
    } catch (IOException ioe) {
      LOGGER.error("Could not write cache to file", ioe);
    }
  }
}
