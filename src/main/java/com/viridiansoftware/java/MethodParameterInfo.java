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

public class MethodParameterInfo {
	private final PrimitiveOrReferenceType primitiveOrReferenceType;
	private final String parameterName;
	private final String description;

	public MethodParameterInfo(PrimitiveOrReferenceType primitiveOrReferenceType, String parameterName, String description) {
		this.primitiveOrReferenceType = primitiveOrReferenceType;
		this.parameterName = parameterName;
		this.description = description;
	}

	public PrimitiveOrReferenceType getPrimitiveOrReferenceType() {
		return primitiveOrReferenceType;
	}

	public String getParameterName() {
		return parameterName;
	}

	public boolean isArray() {
		return ClassUtils.isArray(description);
	}

	public int getArrayDimensions() {
		return ClassUtils.getArrayDimensions(description);
	}

	public boolean isPrimitive() {
		return ClassUtils.isPrimitive(description);
	}

	public boolean isObject() {
		return ClassUtils.isObject(description);
	}

	public boolean isArrayOfPrimitives() {
		return ClassUtils.isArrayOfPrimitives(description);
	}

	public boolean isArrayOfObjects() {
		return ClassUtils.isArrayOfObjects(description);
	}

	public PrimitiveType getPrimitiveType() {
		return ClassUtils.getPrimitiveType(description);
	}

	public String getReferenceClass() {
		return ClassUtils.getReferenceClass(description);
	}
}
