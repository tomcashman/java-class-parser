package com.viridiansoftware.java;

import org.junit.Assert;
import org.junit.Test;

public class AccessFlagDefTest {

	@Test
	public void testPublicFinal() {
		Assert.assertEquals(AccessFlagDef.FINAL.getMask(), 0x0011 & AccessFlagDef.FINAL.getMask());
		Assert.assertEquals(AccessFlagDef.PUBLIC.getMask(), 0x0011 & AccessFlagDef.PUBLIC.getMask());
	}
}
