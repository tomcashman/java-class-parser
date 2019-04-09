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

import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Attributes {

    private final AttributeInfo[] attributes;

    private final ConstantPool    constantPool;

    Attributes(@NonNull DataInputStream input, @NonNull ConstantPool constantPool ) throws IOException {
        this.constantPool = constantPool;
        this.attributes = readAttributs( input );
    }

    private AttributeInfo[] readAttributs( @NonNull DataInputStream input ) throws IOException {
        AttributeInfo[] attrs = new AttributeInfo[input.readUnsignedShort()];
        for( int i = 0; i < attrs.length; i++ ) {
            attrs[i] = new AttributeInfo( input, constantPool );
        }
        return attrs;
    }

    AttributeInfo get( String name ) {
        if(name == null) {
            return null;
        }
        for( AttributeInfo attr : attributes ) {
            if( attr.getName().equals( name ) ) {
                return attr;
            }
        }
        return null;
    }

    static class AttributeInfo {

        private final String name;

        private final byte[] info;

        AttributeInfo( @NonNull DataInputStream input, @NonNull ConstantPool constantPool ) throws IOException {
            this.name = (String)constantPool.get( input.readUnsignedShort() );
            this.info = new byte[input.readInt()];
            input.readFully( this.info );
        }

        String getName() {
            return name;
        }

        byte[] getData() {
            return info;
        }

        DataInputStream getDataInputStream(){
            return new DataInputStream( new ByteArrayInputStream( info ) );
        }
    }

    /**
     * Get value of SourceFile if available.
     * @return the source file name or null.
     * @throws IOException if an I/O error occurs.
     */
    public String getSourceFile() throws IOException{
        AttributeInfo data = get( "SourceFile" );
        if( data == null ) {
            return null;
        }
        return (String)constantPool.get( data.getDataInputStream().readUnsignedShort() );
    }
}
