package com.viridiansoftware.java;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestParsing {

	@Test
	public void testParsingByteClass() throws IOException {
		final ClassFile classFile = new ClassFile(getClass().getResourceAsStream("/Byte.class"));
		Assert.assertEquals("Byte.java", classFile.getSourceFile());
		Assert.assertEquals(ClassFile.Type.Class, classFile.getType());
		Assert.assertEquals(2, classFile.getMethodCount("toString"));
	}
}
