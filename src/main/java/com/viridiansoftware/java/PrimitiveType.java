package com.viridiansoftware.java;

public enum PrimitiveType {
	BYTE('B'),
	CHAR('C'),
	DOUBLE('D'),
	FLOAT('F'),
	INT('I'),
	LONG('J'),
	SHORT('S'),
	BOOLEAN('Z');

	private final char term;

	PrimitiveType(char term) {
		this.term = term;
	}

	public char getTerm() {
		return term;
	}
}
