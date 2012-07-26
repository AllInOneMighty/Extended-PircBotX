package org.jibble.pircbot.util.urlshorteners;

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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jibble.pircbot.util.URLShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharasURLShortener implements URLShortener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SharasURLShortener.class);
	
	private static final String SERVICE_URL = "http://shar.as/geturl.php?url=%s";
	
	private static final String CACHE_FILE_NAME = "cache";
	
	private Map<String, String> cache;
	
	private String cachePath;
	
	public SharasURLShortener(String cachePath) {
		this.cachePath = cachePath;
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
			InputStream input = (InputStream) content;
			try {
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
			} finally {
				input.close();
			}
		}
		throw new UnsupportedOperationException("Can't identify type returned by server: " + content);
	}
	
	private void loadCache() {
		if (StringUtils.isBlank(cachePath)) {
			LOGGER.warn("Cache path is not set, creating memory-only cache");
			cache = new HashMap<String, String>();
			return;
		}
		
		File cacheFile = new File(cachePath, CACHE_FILE_NAME);
		if (!cacheFile.exists()) {
			LOGGER.info("Cache file does not exist, skipping");
			cache = new HashMap<String, String>();
			return;
		}
		
		InputStream inputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			inputStream = new FileInputStream(cacheFile);
			inputStream = new BufferedInputStream(inputStream);
			objectInputStream = new ObjectInputStream(inputStream);
			cache = (Map<String, String>) objectInputStream.readObject();
		} catch (ClassNotFoundException cnfe) {
			LOGGER.error("Could not find class for object in cache file. Is the cache file corrupt?", cnfe);
		} catch (IOException ioe) {
			LOGGER.error("Could not read from cache file", ioe);
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException ioe) {
					LOGGER.warn("Could not close cache file input stream (object reader)", ioe);
				}
			}
		}
		
		if (cache == null) {
			// Make sure cache is never null after calling this method
			cache = new HashMap<String, String>();
		}
	}
	
	private synchronized void saveCache() {
		if (StringUtils.isBlank(cachePath)) {
			LOGGER.warn("Cache path is not set, saving to memory-only cache");
			return;
		}
		
		File cacheFile = new File(cachePath, CACHE_FILE_NAME);
		OutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			cacheFile.createNewFile();
			
			outputStream = new FileOutputStream(cacheFile);
			outputStream = new BufferedOutputStream(outputStream);
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(cache);
		} catch (IOException ioe) {
			LOGGER.error("Could not write cache to file", ioe);
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException ioe) {
					LOGGER.warn("Could not close cache file output stream (file writer)", ioe);
				}
			}
		}
	}
}
