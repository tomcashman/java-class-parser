package com.viridiansoftware.java.constants;

import com.viridiansoftware.java.PrimitiveOrReferenceType;
import com.viridiansoftware.java.PrimitiveType;

public class ClassUtils {

	public static boolean isArray(String name) {
		return name.startsWith("[");
	}

	public static int getArrayDimensions(String name) {
		int totalDimensions = 0;
		for(int i = 0; i < name.length(); i++) {
			if(name.charAt(i) == '[') {
				totalDimensions++;
			}
		}
		return totalDimensions;
	}

	public static boolean isPrimitive(String name) {
		return !name.startsWith("L") && !name.startsWith("[");
	}

	public static boolean isObject(String name) {
		return name.startsWith("L");
	}

	public static boolean isArrayOfPrimitives(String name) {
		if(!name.startsWith("[")) {
			return false;
		}
		for(int i = 1; i < name.length(); i++) {
			if(name.charAt(i) == '[') {
				continue;
			}
			return name.charAt(i) != 'L';
		}
		return false;
	}

	public static boolean isArrayOfObjects(String name) {
		if(!name.startsWith("[")) {
			return false;
		}
		for(int i = 1; i < name.length(); i++) {
			if(name.charAt(i) == '[') {
				continue;
			}
			return name.charAt(i) == 'L';
		}
		return false;
	}

	public static PrimitiveType getPrimitiveType(String name) {
		while(name.charAt(0) == '[') {
			name = name.substring(1);
		}
		for(PrimitiveType primitiveType : PrimitiveType.values()) {
			if(name.charAt(0) == primitiveType.getTerm()) {
				return primitiveType;
			}
		}
		return null;
	}

	public static String getReferenceClass(String name) {
		int offset = 0;

		switch(name.charAt(0)) {
		case '[':
			offset = 1;
			for(int i = 1; i < name.length(); i++) {
				if(name.charAt(i) == '[') {
					offset++;
					continue;
				}
				if(name.charAt(i) == 'L') {
					offset++;
					break;
				}
				return null;
			}
			break;
		case 'L':
			offset = 1;
			break;
		default:
			for(PrimitiveOrReferenceType typeOrRef : PrimitiveOrReferenceType.values()) {
				if(typeOrRef.getTerm() == name.charAt(0)) {
					return null;
				}
			}
			break;
		}

		if(name.endsWith(";")) {
			name = name.substring(offset);
			return name.substring(0, name.length() - 1);
		}
		return name.substring(offset);
	}
}
