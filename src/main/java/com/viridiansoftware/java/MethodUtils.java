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
package com.viridiansoftware.java;

import com.viridiansoftware.java.constants.ClassUtils;

import java.util.List;

public class MethodUtils {

	public static void getMethodParameters(List<MethodParameterInfo> result, final String signature, final MethodParameters parameters) {
		final String parametersSignature = signature.substring(1);
		int signatureIndex = 0;
		for(int i = 0; i < parameters.getParameterNames().length; i++) {
			final PrimitiveOrReferenceType primitiveOrReferenceType = ClassUtils.getPrimitiveOrReferenceType(parametersSignature.charAt(signatureIndex));
			final MethodParameterInfo methodParameterInfo;
			switch(primitiveOrReferenceType) {
			case OBJECT: {
				int start = signatureIndex;
				int length = 1;
				for (int j = start + 1; j < parametersSignature.length(); j++) {
					if (parametersSignature.charAt(j) == ';') {
						length = j - start;
						break;
					}
				}
				methodParameterInfo = new MethodParameterInfo(primitiveOrReferenceType,
						parameters.getParameterNames()[i],
						parametersSignature.substring(signatureIndex, signatureIndex + length));
				signatureIndex += length;
				break;
			}
			case ARRAY: {
				final PrimitiveOrReferenceType arrayType = ClassUtils.getPrimitiveOrReferenceType(parametersSignature.charAt(signatureIndex + 1));
				if(arrayType.equals(PrimitiveOrReferenceType.ARRAY)) {
					int offset = 1;
					PrimitiveOrReferenceType nestedArrayType = ClassUtils.getPrimitiveOrReferenceType(parametersSignature.charAt(signatureIndex + 1 + offset));
					while(nestedArrayType.equals(PrimitiveOrReferenceType.ARRAY)) {
						offset++;
						nestedArrayType = ClassUtils.getPrimitiveOrReferenceType(parametersSignature.charAt(signatureIndex + 1 + offset));
					}

					if(nestedArrayType.equals(PrimitiveOrReferenceType.OBJECT)) {
						int length = 1;
						for (int k = signatureIndex + 1 + offset; k < parametersSignature.length(); k++) {
							if (parametersSignature.charAt(k) == ';') {
								length = k - signatureIndex;
								break;
							}
						}
						methodParameterInfo = new MethodParameterInfo(primitiveOrReferenceType,
								parameters.getParameterNames()[i],
								parametersSignature.substring(signatureIndex, signatureIndex + length));
						signatureIndex += length;
					} else {
						methodParameterInfo = new MethodParameterInfo(primitiveOrReferenceType,
								parameters.getParameterNames()[i],
								parametersSignature.substring(signatureIndex, signatureIndex + offset));
						signatureIndex += offset;
					}
				} else if(arrayType.equals(PrimitiveOrReferenceType.OBJECT)) {
					int length = 1;
					for (int k = signatureIndex + 2; k < parametersSignature.length(); k++) {
						if (parametersSignature.charAt(k) == ';') {
							length = k - signatureIndex;
							break;
						}
					}
					methodParameterInfo = new MethodParameterInfo(primitiveOrReferenceType,
							parameters.getParameterNames()[i],
							parametersSignature.substring(signatureIndex, signatureIndex + length));
					signatureIndex += length;
				} else {
					methodParameterInfo = new MethodParameterInfo(primitiveOrReferenceType,
							parameters.getParameterNames()[i],
							parametersSignature.substring(signatureIndex, signatureIndex + 2));
					signatureIndex += 2;
				}
				break;
			}
			default:
				methodParameterInfo = new MethodParameterInfo(primitiveOrReferenceType,
						parameters.getParameterNames()[i],
						"" + parametersSignature.charAt(signatureIndex));
				signatureIndex++;
				break;
			}

			result.add(methodParameterInfo);
		}
	}
}
