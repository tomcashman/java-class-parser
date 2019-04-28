/*******************************************************************************
 * Copyright 2019 Volker Berlin (i-net software)
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
package com.viridiansoftware.java.constants;

import com.viridiansoftware.java.PrimitiveType;
import com.viridiansoftware.java.utils.ClassUtils;

public class ConstantNameAndType{

    private final String name;
    private final String type;

    public ConstantNameAndType(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String getName(){
        return name;
    }

    /**
     * The type of the variable in class file syntax.
     * @return the type
     */
    public String getType(){
        return type;
    }

    public boolean isArray() {
        return ClassUtils.isArray(type);
    }

    public int getArrayDimensions() {
        return ClassUtils.getArrayDimensions(type);
    }

    public boolean isPrimitive() {
        return ClassUtils.isPrimitive(type);
    }

    public boolean isObject() {
        return ClassUtils.isObject(type);
    }

    public boolean isArrayOfPrimitives() {
        return ClassUtils.isArrayOfPrimitives(type);
    }

    public boolean isArrayOfObjects() {
        return ClassUtils.isArrayOfObjects(type);
    }

    public PrimitiveType getPrimitiveType() {
        return ClassUtils.getPrimitiveType(type);
    }

    public String getReferenceClass() {
        return ClassUtils.getReferenceClass(type);
    }
}
