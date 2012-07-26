package org.jibble.pircbot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class PropertiesUtils {
	public static List<Integer> getIntList(String property) {
		List<Integer> integers = new ArrayList<Integer>();

		String[] values = StringUtils.split(property, ",");
		for (String value : values) {
			if (value.contains("-")) {
				String[] boundaries = value.split("-");
				int i1 = Integer.parseInt(boundaries[0]);
				int i2 = Integer.parseInt(boundaries[1]);
				
				// Now add all values between boundaries, including
				int min = Math.min(i1, i2);
				int max = Math.max(i1, i2);
				for (int i = min; i <= max; i++) {
					integers.add(i);
				}
			} else {
				integers.add(Integer.parseInt(value));
			}
		}
		return integers;
	}
	
	public static List<String> getStringList(String property) {
		List<String> strings = new ArrayList<String>();
		for (String value : property.split("\\|")) {
			strings.add(value);
		}
		return strings;
	}

	public static List<String> getStringList(String property, Properties properties) {
		List<String> strings = new ArrayList<String>();
		// Use an insanely big limit that should never be reached by the user
		for (int i = 1; i < 999; i++) {
			String key = property + "." + i;
			if (properties.containsKey(key)) {
				strings.add(properties.getProperty(key));
			} else {
				break;
			}
		}
		return strings;
	}
	
	public static int getInt(String property) {
		return Integer.parseInt(property.trim());
	}
	
	public static boolean getBoolean(String property) {
		return Boolean.parseBoolean(property.trim());
	}
}
