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

import com.viridiansoftware.java.attributes.*;
import com.viridiansoftware.java.constants.ConstantClass;
import com.viridiansoftware.java.constants.ConstantNameAndType;
import com.viridiansoftware.java.constants.ConstantPool;
import com.viridiansoftware.java.descriptor.MethodDescriptor;
import com.viridiansoftware.java.descriptor.antlr.DescriptorParser;
import com.viridiansoftware.java.signature.ClassSignature;
import com.viridiansoftware.java.signature.antlr.SignatureParser;
import com.viridiansoftware.java.utils.ClassUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html
 */
public class ClassFile implements TypeVariableResolver {

    private final DataInputStream input;
    private final int             minorVersion;
    private final int             majorVersion;
    private final ConstantPool constantPool;
    private final int             accessFlags;
    private final List<ClassAccessFlag> classAccessFlags = new ArrayList<ClassAccessFlag>(2);
    private final ConstantClass thisClass;
    private final ConstantClass   superClass;
    private final ConstantClass[] interfaces;
    private final FieldInfo[]     fields;
    private final MethodInfo[]    methods;
    private final Attributes attributes;
    private String                thisSignature;
    private String                superSignature;
    private ClassSignature        classSignature;
    private NestHost              nestHost;
    private NestMembers           nestMembers;
    private InnerClasses          innerClasses;
    private EnclosingMethod       enclosingMethod;

    /**
     * Load a class file and create a model of the class.
     *
     * @param stream
     *            The InputStream of the class file. Will be closed if finish.
     * @throws IOException
     *             if this input stream reaches the end before reading the class file.
     */
    public ClassFile( InputStream stream ) throws IOException {
        this.input = new DataInputStream( stream );
        int magic = input.readInt();
        if( magic != 0xCAFEBABE ) {
            throw new IOException( "Invalid class magic: " + Integer.toHexString( magic ) );
        }
        minorVersion = input.readUnsignedShort();
        majorVersion = input.readUnsignedShort();

        constantPool = new ConstantPool(majorVersion, minorVersion, input );
        accessFlags = input.readUnsignedShort();

        for(ClassAccessFlag classAccessFlag : ClassAccessFlag.values()) {
            if((classAccessFlag.getMask() & accessFlags) == classAccessFlag.getMask()) {
                classAccessFlags.add(classAccessFlag);
            }
        }

        thisClass = (ConstantClass)constantPool.get( input.readUnsignedShort() );
        superClass = (ConstantClass)constantPool.get( input.readUnsignedShort() );
        interfaces = new ConstantClass[input.readUnsignedShort()];
        for( int i = 0; i < interfaces.length; i++ ) {
            interfaces[i] = (ConstantClass)constantPool.get( input.readUnsignedShort() );
        }
        fields = readFields();
        methods = readMethods();
        attributes = new Attributes( input, constantPool );

        stream.close();

        AttributeInfo info = attributes.get( "Signature" );
        if( info != null ) {
            int idx = info.getDataInputStream().readShort();
            String signature = (String)constantPool.get( idx );
            int count = 0;
            for( int i = 0; i < signature.length(); i++ ) {
                char ch = signature.charAt( i );
                switch( ch ) {
                    case '<':
                        count++;
                        continue;
                    case '>':
                        count--;
                        continue;
                }
                if( count == 0 ) {
                    thisSignature = signature.substring( 0, i );
                    superSignature = signature.substring( i );
                    break;
                }
            }
            classSignature = new ClassSignature(signature);
        }
        AttributeInfo nestHostInfo = attributes.get("NestHost");
        if(nestHostInfo != null) {
            nestHost = new NestHost(nestHostInfo.getDataInputStream(), constantPool);
        }
        AttributeInfo nestMembersInfo = attributes.get("NestMembers");
        if(nestMembersInfo != null) {
            nestMembers = new NestMembers(nestMembersInfo.getDataInputStream(), constantPool);
        }
        AttributeInfo innerClassesInfo = attributes.get("InnerClasses");
        if(innerClassesInfo != null) {
            innerClasses = new InnerClasses(innerClassesInfo.getDataInputStream(), constantPool);
        }
        AttributeInfo enclosingMethodInfo = attributes.get("EnclosingMethod");
        if(enclosingMethodInfo != null) {
            enclosingMethod = new EnclosingMethod(enclosingMethodInfo.getDataInputStream(), constantPool);
        }
    }

    /**
     * Get value of SourceFile if available.
     *
     * @return the source file name or null.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public String getSourceFile() throws IOException {
        return attributes.getSourceFile();
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public ConstantClass getThisClass() {
        return thisClass;
    }

    public ConstantClass getSuperClass() {
        return superClass;
    }

    public ConstantClass[] getInterfaces(){
        return interfaces;
    }

    public MethodInfo[] getMethods() {
        return methods;
    }

    public List<MethodInfo> getClassInitialisationMethods() {
        final List<MethodInfo> results = new ArrayList<MethodInfo>(2);
        for( MethodInfo method : methods ) {
            if( method.getName().equals("<clinit>")) {
                results.add(method);
            }
        }
        return results;
    }

    public List<MethodInfo> getConstructorMethods() {
        final List<MethodInfo> results = new ArrayList<MethodInfo>(2);
        for( MethodInfo method : methods ) {
            if( method.getName().equals("<init>")) {
                results.add(method);
            }
        }
        return results;
    }

    public List<MethodInfo> getNonInitMethods()
    {
        final List<MethodInfo> results = new ArrayList<MethodInfo>(2);
        for( MethodInfo method : methods ) {
            if( method.getName().equals("<init>")) {
                continue;
            }
            if( method.getName().equals("<clinit>")) {
                continue;
            }
            results.add(method);
        }
        return results;
    }

    public List<MethodInfo> getMethod(String name) {
        final List<MethodInfo> results = new ArrayList<MethodInfo>(2);
        for( MethodInfo method : methods ) {
            if( name.equals( method.getName() ) ) {
                results.add(method);
            }
        }
        return results;
    }

    public MethodInfo getMethod(ConstantNameAndType constantNameAndType) throws IOException {
        for( MethodInfo method : methods ) {
            if(method.matches(constantNameAndType)) {
                return method;
            }
        }
        return null;
    }

    public int getMethodCount( String name ) {
        int count = 0;
        for( MethodInfo method : methods ) {
            if( name.equals( method.getName() ) ) {
                count++;
            }
        }
        return count;
    }

    public FieldInfo getField( String name ) {
        for( FieldInfo field : fields ) {
            if( name.equals( field.getName() ) ) {
                return field;
            }
        }
        return null;
    }

    /**
     * Get the fields of the class.
     * 
     * @return the fields
     */
    public FieldInfo[] getFields() {
        return fields;
    }

    /**
     * The access flags of the class.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1-200-E
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#23242
     * @see java.lang.Class#isInterface()
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    /**
     * Returns access flags as list of enums
     * @return
     */
    public List<ClassAccessFlag> getClassAccessFlags() {
        return classAccessFlags;
    }

    private FieldInfo[] readFields() throws IOException {
        FieldInfo[] fields = new FieldInfo[input.readUnsignedShort()];
        for( int i = 0; i < fields.length; i++ ) {
            fields[i] = new FieldInfo( input, constantPool );
        }
        return fields;
    }

    private MethodInfo[] readMethods() throws IOException {
        MethodInfo[] methods = new MethodInfo[input.readUnsignedShort()];
        for( int i = 0; i < methods.length; i++ ) {
            methods[i] = new MethodInfo( input, constantPool, this );
        }
        return methods;
    }

    /**
     * Get the signature of the class with generic types.
     */
    public String getThisSignature() {
        return thisSignature;
    }

    /**
     * Get the signature of the super class with generic types.
     */
    public String getSuperSignature() {
        return superSignature;
    }

    /**
     * Gets the parsed {@link ClassSignature}
     */
    public ClassSignature getClassSignature() {
        return classSignature;
    }

    public ResolvedTypeVariable resolveTypeVariable(String variableName) throws UnresolvedTypeVariableException, IOException {
        if(classSignature == null) {
            throw new UnresolvedTypeVariableException(getSourceFile(), variableName);
        }
        if(variableName.startsWith("T")) {
            variableName = variableName.substring(1);
        }
        for(int i = 0; i < classSignature.getTotalTypeParameters(); i++) {
            final SignatureParser.TypeParameterContext typeParameterContext = classSignature.getTypeParameter(i);
            if(typeParameterContext.identifier().getText().equals(variableName)) {
                return new ResolvedTypeVariable(variableName, typeParameterContext);
            }
        }
        throw new UnresolvedTypeVariableException(getSourceFile(), variableName);
    }

    public NestHost getNestHost() {
        return nestHost;
    }

    public NestMembers getNestMembers() {
        return nestMembers;
    }

    public InnerClasses getInnerClasses() {
        return innerClasses;
    }

    public EnclosingMethod getEnclosingMethod() {
        return enclosingMethod;
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
     * Returns if the class is public visibility
     * @return True if public visibility
     */
    public boolean isPublic() {
        return (accessFlags & ClassAccessFlag.PUBLIC.getMask()) > 0;
    }

    /**
     * Returns if the class is protected visibility
     * @return True if protected visibility
     */
    public boolean isProtected() {
        return (accessFlags & ClassAccessFlag.PROTECTED.getMask()) > 0;
    }

    /**
     * Returns if the class is private visibility
     * @return True if private visibility
     */
    public boolean isPrivate() {
        return (accessFlags & ClassAccessFlag.PRIVATE.getMask()) > 0;
    }

    /**
     * Get the type of class.
     */
    public Type getType() {
        if( (accessFlags & 0x0200) == 0x0200) {
            return Type.Interface;
        }
        if( superClass != null && superClass.getName() != null &&
                superClass.getName().equals( "java/lang/Enum" ) ) {
            return Type.Enum;
        }
        return Type.Class;
    }

    public static enum Type {
        Class, Interface, Enum;
    }
}
