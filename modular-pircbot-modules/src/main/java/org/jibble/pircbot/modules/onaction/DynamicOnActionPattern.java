package org.jibble.pircbot.modules.onaction;

import java.util.regex.Pattern;

public class DynamicOnActionPattern extends OnActionPattern {
	private String dynamicRegex;
	
	public DynamicOnActionPattern(String dynamicRegex) {
		this.dynamicRegex = dynamicRegex;
		updatePattern("");
	}

	public void updatePattern(String botname) {
		String regex = dynamicRegex.replaceAll("\\{botname\\}", botname);
		setPattern(Pattern.compile(regex));
	}
	
	@Override
	public String toString() {
		return getPattern().pattern() + " (regex: " + dynamicRegex + ")";
	}
}
