package org.jibble.pircbot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Used by some modules to convert properties into Java types. You can use this
 * class to convert your own properties.
 * 
 * @author Emmanuel Cron
 */
public class PropertiesUtils {
	/**
	 * Builds a list containing integers and ranges of integers, read from a
	 * property having the following format: "<tt>{exp}, {exp}, {exp}, ...</tt>
	 * ".
	 * <p>
	 * An expression (<tt>{exp}</tt>) can be:
	 * <ul>
	 * <li>A single integer. This value will be added to the returned list.</li>
	 * <li>A range, having the following format: "<tt>{min}-{max}</tt>". All
	 * values between the <tt>{min}</tt> and <tt>{max}</tt> values are inserted
	 * in the list.</li>
	 * </ul>
	 * <p>
	 * For example, if the property is equal to "<tt>3, 10-14, 91</tt>", the
	 * returned list will contain 7 elements:
	 * <tt>[3, 10, 11, 12, 13, 14, 91]</tt>.
	 * 
	 * @param property the property to read as a list of integer expressions
	 * @return a list of integers as described above
	 * 
	 * @throws NumberFormatException if integers cannot be read from the
	 *         property
	 */
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
	
	/**
	 * Converts a string property to a list of strings. Strings are separated
	 * using the "<tt>|</tt>" character. If this character cannot be found in
	 * the property, the returned list will contain only one element with the
	 * full value.
	 * 
	 * @param property the value to read as a list of strings
	 * @return a list of strings as described above
	 */
	public static List<String> getStringList(String property) {
		List<String> strings = new ArrayList<String>();
		for (String value : property.split("\\|")) {
			strings.add(value);
		}
		return strings;
	}

	/**
	 * Reads a group of properties (strings only) having the same name but
	 * ending with an incremental numeric suffix. For example, this method is
	 * able to read the following properties in one call: <tt>my.property.1</tt>
	 * , <tt>my.property.2</tt>, <tt>my.property.3</tt>, ...
	 * 
	 * @param property the name of the property without the trailing dot (in the
	 *        example, it would be "<tt>my.property"</tt>)
	 * @param properties the properties file from which read the values
	 * @return a list of all the properties read
	 */
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
	
	/**
	 * Converts a string property to an integer. For example, this method will
	 * convert "<tt>12</tt>" to <tt>12</tt>.
	 * 
	 * @param property the property to convert
	 * @return the number representation of the property
	 * 
	 * @throws NumberFormatException if the property cannot be read as a number
	 */
	public static int getInt(String property) {
		return Integer.parseInt(property.trim());
	}
	
	/**
	 * Converts a string property to a boolean. A property is considered
	 * <tt>true</tt> if it is equal, ignoring case, to the "<tt>true</tt>"
	 * string. Otherwise, <tt>false</tt> is returned.
	 * 
	 * @param property the property to convert
	 * @return <tt>true</tt> or <tt>false</tt> depending on the value of the
	 *         property
	 */
	public static boolean getBoolean(String property) {
		return Boolean.parseBoolean(property.trim());
	}
}
