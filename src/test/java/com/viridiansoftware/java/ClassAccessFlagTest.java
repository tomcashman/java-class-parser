package com.viridiansoftware.java;

import org.junit.Assert;
import org.junit.Test;

public class ClassAccessFlagTest {

	@Test
	public void testPublicFinal() {
		Assert.assertEquals(ClassAccessFlag.FINAL.getMask(), 0x0011 & ClassAccessFlag.FINAL.getMask());
		Assert.assertEquals(ClassAccessFlag.PUBLIC.getMask(), 0x0011 & ClassAccessFlag.PUBLIC.getMask());

		Assert.assertEquals(0, 0x1000 & ClassAccessFlag.FINAL.getMask());
	}
}
