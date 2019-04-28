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

import com.viridiansoftware.java.signature.antlr.SignatureParser;

public class TypeArgumentUtils {

	public static boolean isExtendsWildcard(SignatureParser.TypeArgumentContext typeArgumentContext) {
		if(typeArgumentContext.ASTERISK() != null) {
			return false;
		}
		if(typeArgumentContext.WildcardIndicator() == null) {
			return false;
		}
		return typeArgumentContext.WildcardIndicator().getText().equals("+");
	}

	public static boolean isSuperWildcard(SignatureParser.TypeArgumentContext typeArgumentContext) {
		if(typeArgumentContext.ASTERISK() != null) {
			return false;
		}
		if(typeArgumentContext.WildcardIndicator() == null) {
			return false;
		}
		return typeArgumentContext.WildcardIndicator().getText().equals("-");
	}

	public boolean isAnyType(SignatureParser.TypeArgumentContext typeArgumentContext) {
		return typeArgumentContext.ASTERISK() != null;
	}
}