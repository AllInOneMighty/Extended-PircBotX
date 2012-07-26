package org.jibble.pircbot.modules.onaction;

import junit.framework.Assert;

import org.junit.Test;

public class OnActionPatternFactoryTest {
	@Test
	public void testBuild() {
		Assert.assertTrue(OnActionPatternFactory.build("^pokes {botname}$") instanceof DynamicOnActionPattern);
		Assert.assertTrue(OnActionPatternFactory.build("^lance .+$") instanceof StandardOnActionPattern);
	}
}
