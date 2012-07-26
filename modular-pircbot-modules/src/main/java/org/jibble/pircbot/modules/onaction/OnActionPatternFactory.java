package org.jibble.pircbot.modules.onaction;

import java.util.regex.Pattern;

public class OnActionPatternFactory {
	private static final Pattern DYNAMIC_PATTERN_DETECTOR = Pattern.compile("^.*\\{.*[a-z]+.*\\}.*$",
		Pattern.CASE_INSENSITIVE);

	public static OnActionPattern build(String regex) {
		if (DYNAMIC_PATTERN_DETECTOR.matcher(regex).matches()) {
			return new DynamicOnActionPattern(regex);
		} else {
			return new StandardOnActionPattern(Pattern.compile(regex));
		}
	}
}
