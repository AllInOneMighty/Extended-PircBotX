package org.jibble.pircbot.util;

import java.io.IOException;

public interface URLShortener {
	String shortenURL(String url) throws IOException;
}
