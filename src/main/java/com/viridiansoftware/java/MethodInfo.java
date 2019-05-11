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
import com.viridiansoftware.java.attributes.Code;
import com.viridiansoftware.java.constants.ConstantNameAndType;
import com.viridiansoftware.java.constants.ConstantPool;
import com.viridiansoftware.java.signature.MethodSignature;
import com.viridiansoftware.java.signature.antlr.SignatureParser;
import com.viridiansoftware.java.utils.ClassUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MethodInfo implements Member, TypeVariableResolver {

    private final int          accessFlags;
    private final List<MethodAccessFlag> methodAccessFlags = new ArrayList<MethodAccessFlag>(4);
    private final String       name;
    private final String       description;
    private final Attributes attributes;
    private final ConstantPool constantPool;
    private Code code;
    private Exceptions         exceptions;
    private ClassFile          classFile;
    private Map<String,Map<String,Object>> annotations;

    private String signature;
    private MethodParameters methodParameters;
    private MethodSignature methodSignature;

    /**
     * Read the method_info structure http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6
     *
     * @param input
     *            the stream of the class file
     * @param constantPool
     *            the ConstantPool of the class
     * @param classFile
     *            the declaring class file
     * @throws IOException
     *             if an I/O error occurs
     */
    MethodInfo( DataInputStream input, ConstantPool constantPool, ClassFile classFile ) throws IOException {
        this.accessFlags = input.readUnsignedShort();

        for(MethodAccessFlag methodAccessFlag : MethodAccessFlag.values()) {
            if((methodAccessFlag.getMask() & accessFlags) == methodAccessFlag.getMask()) {
                methodAccessFlags.add(methodAccessFlag);
            }
        }

        this.name = (String)constantPool.get( input.readUnsignedShort() );
        this.description = (String)constantPool.get( input.readUnsignedShort() );
        this.constantPool = constantPool;
        this.attributes = new Attributes( input, constantPool );
        this.classFile = classFile;
    }

    /**
     * Get the declaring class file of the method
     * @return the ClassFile
     */
    public ClassFile getDeclaringClassFile() {
        return classFile;
    }

    /**
     * Get the access flags of the method.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6-200-A
     *
     * @return the flags
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    /**
     * Returns the access flags as a list of enums
     * @return
     */
    public List<MethodAccessFlag> getMethodAccessFlags() {
        return methodAccessFlags;
    }

    /**
     * If the method is a static method.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6-200-A
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#1522
     * @return true, if static
     * @see #getAccessFlags()
     */
    public boolean isStatic() {
        return (accessFlags & MethodAccessFlag.STATIC.getMask()) > 0;
    }

    /**
     * Returns if the method is public visibility
     * @return True if public visibility
     */
    public boolean isPublic() {
        return (accessFlags & MethodAccessFlag.PUBLIC.getMask()) > 0;
    }

    /**
     * Returns if the method is protected visibility
     * @return True if protected visibility
     */
    public boolean isProtected() {
        return (accessFlags & MethodAccessFlag.PROTECTED.getMask()) > 0;
    }

    /**
     * Returns if the method is private visibility
     * @return True if private visibility
     */
    public boolean isPrivate() {
        return (accessFlags & MethodAccessFlag.PRIVATE.getMask()) > 0;
    }

    /**
     * Returns if the method is a native method
     * @return True if the method is a native method
     */
    public boolean isNative() {
        return (accessFlags & MethodAccessFlag.NATIVE.getMask()) > 0;
    }

    /**
     * Returns if the method is an abstract method
     * @return True if the method is an abstract method
     */
    public boolean isAbstract() {
        return (accessFlags & MethodAccessFlag.ABSTRACT.getMask()) > 0;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return getDeclaringClassFile().getThisClass().getName();
    }

    /**
     * @return the attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    public Code getCode() throws IOException {
        if( code != null ){
            return code;
        }
        AttributeInfo data = attributes.get( "Code" );
        if( data != null ) {
            code = new Code( data.getDataInputStream(), constantPool );
        }
        return code;
    }

    /**
     * Get the signature of the method without generic types.
     */
    @Override
    public String getType() {
        return description;
    }

    /**
     * Get the signature of the method with generic types.
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
        } else {
            signature = description;
        }
        return signature;
    }

    public MethodParameters getMethodParameters() throws IOException {
        if(methodParameters != null) {
            return methodParameters;
        }
        AttributeInfo info = getAttributes().get( "MethodParameters" );
        if( info != null ) {
            methodParameters = new MethodParameters(info.getDataInputStream(), constantPool);
        } else {
            methodParameters = new MethodParameters(new String [0]);
        }
        return methodParameters;
    }

    /**
     * Returns the parsed {@link MethodSignature}
     */
    public MethodSignature getMethodSignature() throws IOException {
        if(methodSignature == null) {
            methodSignature = new MethodSignature(getSignature());
        }
        return methodSignature;
    }

    /**
     * Returns if this method is a Void method
     * @return True if void
     * @throws IOException
     */
    public boolean isVoidMethod() throws IOException {
        return getMethodSignature().isVoidMethod();
    }

    public int getTotalTypeParameters() throws IOException {
        return getMethodSignature().getTotalTypeParameters();
    }

    public SignatureParser.TypeParameterContext getTypeParameter(int i) throws IOException {
        return getMethodSignature().getTypeParameter(i);
    }

    public int getTotalMethodArguments() throws IOException {
        return getMethodSignature().getTotalMethodArguments();
    }

    public String getMethodArgumentName(int i) throws IOException {
        return getMethodParameters().getParameterNames()[i];
    }

    public SignatureParser.JavaTypeSignatureContext getMethodArgumentType(int i) throws IOException {
        return getMethodSignature().getMethodArgument(i);
    }

    public int getTotalThrowsSignatures() throws IOException {
        return getMethodSignature().getTotalThrowsSignatures();
    }

    public SignatureParser.ThrowsSignatureContext getThrowsSignature(int i) throws IOException {
        return getMethodSignature().getThrowsSignature(i);
    }

    public ResolvedTypeVariable resolveTypeVariable(String variableName) throws UnresolvedTypeVariableException, IOException {
        if(variableName.startsWith("T")) {
            variableName = variableName.substring(1);
        }

        for(int i = 0; i < methodSignature.getTotalTypeParameters(); i++) {
            final SignatureParser.TypeParameterContext typeParameterContext = methodSignature.getTypeParameter(i);
            if(!typeParameterContext.identifier().getText().equals(variableName)) {
                continue;
            }
            if(typeParameterContext.classBound().referenceTypeSignature() == null
                && typeParameterContext.interfaceBounds() == null) {
                continue;
            }
            return new ResolvedTypeVariable(variableName, typeParameterContext);
        }
        return classFile.resolveTypeVariable(variableName);
    }

    public SignatureParser.JavaTypeSignatureContext getResultSignature() throws IOException {
        return getMethodSignature().getReturnType();
    }

    public boolean matches(ConstantNameAndType constantNameAndType) throws IOException {
        if(!getName().equals(constantNameAndType.getName())) {
            return false;
        }
        if(description != null) {
            return description.equals(constantNameAndType.getType());
        }
        return ClassUtils.isSameType(constantNameAndType.asMethodDescriptor(), getMethodSignature());
    }

    /**
     * Get a single annotation or null
     * 
     * @param annotation
     *            the class name of the annotation
     * @return the value or null if not exists
     * @throws IOException
     *             if any I/O error occur
     */
    public Map<String, Object> getAnnotation( String annotation ) throws IOException {
        if( annotations == null ) {
            AttributeInfo data = attributes.get( "RuntimeInvisibleAnnotations" );
            if( data != null ) {
                annotations =  Annotations.read( data.getDataInputStream(), constantPool );
            } else {
                annotations = Collections.emptyMap();
            }
        }
        return annotations.get( annotation );
    }

    /**
     * Get the constant pool of the the current class.
     * @return the constant pool
     */
    public ConstantPool getConstantPool() {
        return constantPool;
    }
}
