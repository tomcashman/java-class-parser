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

import com.viridiansoftware.java.descriptor.FieldDescriptor;
import com.viridiansoftware.java.descriptor.MethodDescriptor;

public class ConstantNameAndType{

    private final String name;
    private final String type;

    private FieldDescriptor fieldDescriptor;
    private MethodDescriptor methodDescriptor;

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

    public FieldDescriptor asFieldDescriptor() {
        if(fieldDescriptor == null) {
            fieldDescriptor = new FieldDescriptor(type);
        }
        return fieldDescriptor;
    }

    public MethodDescriptor asMethodDescriptor() {
        if(methodDescriptor == null) {
            methodDescriptor = new MethodDescriptor(type);
        }
        return methodDescriptor;
    }
}
