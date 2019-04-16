package com.viridiansoftware.java;

public enum PrimitiveOrReferenceType {
	BYTE('B'),
	CHAR('C'),
	DOUBLE('D'),
	FLOAT('F'),
	INT('I'),
	LONG('J'),
	OBJECT('L'),
	SHORT('S'),
	BOOLEAN('Z'),
	ARRAY('[');

	private final char term;

	PrimitiveOrReferenceType(char term) {
		this.term = term;
	}

	public char getTerm() {
		return term;
	}
}
