package org.jibble.pircbot.modules.facts;

import java.util.List;

public interface FactsReader {
	List<List<String>> readFacts();
}
