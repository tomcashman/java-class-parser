/*******************************************************************************
 * Copyright 2019 Viridian Software Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.viridiansoftware.java.utils;

import com.viridiansoftware.java.PrimitiveType;

public class ClassUtils {
	private static final String [] EMPTY_GENERICS = new String[0];

	public static boolean hasGenerics(String className) {
		if(!className.contains("<")) {
			return false;
		}
		if(!className.contains(">")) {
			return false;
		}
		return true;
	}

	public static String [] getGenericTypes(String className) {
		if(!hasGenerics(className)) {
			return EMPTY_GENERICS;
		}
		final String genericClasses = className.substring(className.indexOf('<') + 1, className.lastIndexOf('>'));
		return genericClasses.split(";");
	}

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
		return getPrimitiveType(name.charAt(0));
	}

	public static PrimitiveType getPrimitiveType(char c) {
		for(PrimitiveType primitiveType : PrimitiveType.values()) {
			if(c == primitiveType.getTerm()) {
				return primitiveType;
			}
		}
		return null;
	}

	static PrimitiveOrReferenceType getPrimitiveOrReferenceType(char c) {
		for(PrimitiveOrReferenceType primitiveOrRefType : PrimitiveOrReferenceType.values()) {
			if(c == primitiveOrRefType.getTerm()) {
				return primitiveOrRefType;
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
