package org.jibble.pircbot.modules.facts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FortunesReader implements FactsReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(FortunesReader.class);

	private String fortunesPath;

	private String encoding;

	public FortunesReader(String fortunesPath, String encoding) {
		this.fortunesPath = fortunesPath;
		this.encoding = encoding;
	}
	
	@Override
	public List<List<String>> readFacts() {
		InputStream input = ClassLoader.getSystemResourceAsStream(fortunesPath);
		
		List<List<String>> fortunes = new ArrayList<List<String>>();
		List<String> fortune = new ArrayList<String>();
		try {
			Reader reader = new InputStreamReader(input, encoding);
			BufferedReader bufferedReader = new BufferedReader(reader);
			
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				
				// Ignore blank lines
				if (StringUtils.isBlank(line)) {
					continue;
				}
				
				if ("%".equals(line) && fortune.size() > 0) {
					fortunes.add(fortune);
					// Don't clear() here
					fortune = new ArrayList<String>();
				} else {
					fortune.add(line);
				}
			}
		} catch (IOException ioe) {
			LOGGER.error("Error while reading Chuck Norris file, will use what we have already read", ioe);
		} finally {
			try {
				input.close();
			} catch (IOException ioe) {
				LOGGER.error("Could not close Chuck Norris input stream reader", ioe);
			}
		}
		
		// Add last fortune
		if (fortune.size() > 0) {
			fortunes.add(fortune);
		}
		
		// Update fortunes
		return fortunes;
	}
}
