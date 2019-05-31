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
package com.viridiansoftware.java;

import com.viridiansoftware.java.attributes.AttributeInfo;
import com.viridiansoftware.java.attributes.Attributes;
import com.viridiansoftware.java.constants.ConstantPool;
import com.viridiansoftware.java.descriptor.FieldDescriptor;
import com.viridiansoftware.java.descriptor.MethodDescriptor;
import com.viridiansoftware.java.signature.FieldSignature;
import com.viridiansoftware.java.utils.ClassUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Described a Field of a class.
 * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.5
 * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#2877
 */
public class FieldInfo {
    private final int        accessFlags;
    private final List<FieldAccessFlag> fieldAccessFlags = new ArrayList<FieldAccessFlag>(2);
    private final String     name;
    private final String     description;
    private final ConstantPool constantPool;
    private final Attributes attributes;

    private String signature;
    private FieldSignature fieldSignature;
    private FieldDescriptor fieldDescriptor;

    /**
     * Read a single FieldInfo.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.5
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#2877
     * @param input
     * @param constantPool
     * @throws IOException
     */
    FieldInfo(DataInputStream input, ConstantPool constantPool) throws IOException {
        this.accessFlags = input.readUnsignedShort();
        this.constantPool = constantPool;

        for(FieldAccessFlag fieldAccessFlag : FieldAccessFlag.values()) {
            if((fieldAccessFlag.getMask() & accessFlags) == fieldAccessFlag.getMask()) {
                fieldAccessFlags.add(fieldAccessFlag);
            }
        }

        this.name = (String)constantPool.get( input.readUnsignedShort() );
        this.description = (String)constantPool.get( input.readUnsignedShort() );
        this.attributes = new Attributes( input, constantPool );
    }

    /**
     * Get the access flags of the method.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.5-200-A
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#87652
     *
     * @return the flags
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    public List<FieldAccessFlag> getFieldAccessFlags() {
        return fieldAccessFlags;
    }

    /**
     * Returns if the field is package visibility
     * @return True if package visibility
     */
    public boolean isDefaultScope() {
        if(isPrivate()) {
            return false;
        }
        if(isPublic()) {
            return false;
        }
        if(isProtected()) {
            return false;
        }
        return true;
    }

    /**
     * Returns if the field is public visibility
     * @return True if public visibility
     */
    public boolean isPublic() {
        return (accessFlags & FieldAccessFlag.PUBLIC.getMask()) > 0;
    }

    /**
     * Returns if the field is protected visibility
     * @return True if protected visibility
     */
    public boolean isProtected() {
        return (accessFlags & FieldAccessFlag.PROTECTED.getMask()) > 0;
    }

    /**
     * Returns if the field is private visibility
     * @return True if private visibility
     */
    public boolean isPrivate() {
        return (accessFlags & FieldAccessFlag.PRIVATE.getMask()) > 0;
    }

    /**
     * If this field is static or not
     * @return true, if static
     */
    public boolean isStatic() {
        return (accessFlags & FieldAccessFlag.STATIC.getMask()) > 0;
    }

    /**
     * Get the name of the field
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the field.
     * 
     * @return the type
     */
    public String getType() {
        return description;
    }

    /**
     * Get the signature of the field with generic types.
     *
     * @return the signature
     * @throws IOException
     *             if an I/O error occurs
     */
    public String getSignature() throws IOException {
        if(signature != null) {
            return signature;
        }
        AttributeInfo info = getAttributes().get( "Signature" );
        if( info != null ) {
            int idx = info.getDataInputStream().readShort();
            signature = (String)constantPool.get( idx );
        }
        return signature;
    }

    public FieldDescriptor getFieldDescriptor() {
        if(fieldDescriptor == null) {
            fieldDescriptor = new FieldDescriptor(getType());
        }
        return fieldDescriptor;
    }

    /**
     * @return the attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    public FieldSignature getFieldSignature() throws IOException {
        if(fieldSignature == null) {
            if(getSignature() != null)
            {
                fieldSignature = new FieldSignature(getSignature());
            }
        }
        return fieldSignature;
    }
}