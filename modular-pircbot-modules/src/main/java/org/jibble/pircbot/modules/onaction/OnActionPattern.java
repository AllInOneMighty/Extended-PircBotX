package org.jibble.pircbot.modules.onaction;

import java.util.regex.Pattern;

public abstract class OnActionPattern {
	private Pattern pattern;

	protected final void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public final Pattern getPattern() {
		return pattern;
	}
	
	public boolean matches(String action) {
		return getPattern().matcher(action).matches();
	}
}
