package org.jibble.pircbot.modules.onaction;

import java.util.regex.Pattern;

public class StandardOnActionPattern extends OnActionPattern {
	public StandardOnActionPattern(Pattern pattern) {
		setPattern(pattern);
	}
	
	@Override
	public String toString() {
		return getPattern().pattern();
	}
}
