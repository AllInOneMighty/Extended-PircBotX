package org.jibble.pircbot.modules;

import org.jibble.pircbot.PircBot;


public abstract class AbstractRunnablePircModule extends AbstractStoppablePircModule implements Runnable {
	public abstract void setBot(PircBot bot);
}
