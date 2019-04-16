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
package com.viridiansoftware.java.attributes;

import com.viridiansoftware.java.constants.ConstantPool;
import lombok.NonNull;

import java.io.DataInputStream;
import java.io.IOException;

public class LocalVariableTable {

    private final ConstantPool constantPool;
    private LocalVariable[] tablePosition;
    private LocalVariable[] table;
    private int count;

    /**
     * Create a new instance of the code attribute "LocalVariableTable".
     * 
     * @param maxLocals
     *            the count of local variables in the memory
     * @param constantPool
     *            Reference to the current ConstantPool
     */
    public LocalVariableTable( int maxLocals, ConstantPool constantPool ) {
        table = new LocalVariable[maxLocals];
        tablePosition = new LocalVariable[maxLocals];
        this.constantPool = constantPool;
    }

    /**
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.13
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#5956
     *
     * @param input
     *            the stream of the class
     * @param withPositions
     *            a hack if we find a better solution to map the positions LocalVariableTypeTable
     * @throws IOException
     *             if any I/O error occurs.
     */
    void read( DataInputStream input, boolean withPositions ) throws IOException {
        count = input.readUnsignedShort();
        boolean[] wasSet = new boolean[table.length];
        for( int i = 0; i < count; i++ ) {
            LocalVariable var = new LocalVariable( input, i, constantPool );
            int idx = var.getIndex();
            if( !wasSet[idx] ) { // does not use index of reused variable
                table[idx] = var;
                wasSet[idx] = true;
            }
        }

        if( withPositions ) {
            for( int i = 0, t = 0; i < table.length; i++ ) {
                LocalVariable var = table[i];
                if( var != null ) {
                    tablePosition[t++] = var;
                }
            }
        }
    }

    /**
     * Get the count of variables.
     * @return the count
     */
    public int getPositionSize() {
        return tablePosition.length;
    }

    /**
     * Get the count of storage places a 4 bytes for local variables. Double and long variables need 2 of this places.
     * 
     * @return the local stack size
     */
    public int getSize() {
        return table.length;
    }

    /**
     * Get the LocalVariable with it position. The position is continue also with double and long variables. Or if a variable is reused from a other block.
     *
     * @param pos
     *            the position
     */
    @NonNull
    public LocalVariable getPosition( int pos ) {
        return tablePosition[pos];
    }

    /**
     * Get the LocalVariable with its memory location (slot). The index has empty places with double and long variables.
     *
     * @param idx
     *            the index in the memory
     * @return the LocalVariable
     */
    @NonNull
    public LocalVariable get( int idx ) {
        return table[idx];
    }

    /**
     * Find a LocalVariable with a given name.
     *
     * @param name
     *            needed for evaluate the name.
     * @return the LocalVariable or null
     */
    public LocalVariable get( String name ) {
        if(name == null) {
            return null;
        }
        for( int i=0; i<table.length; i++ ){
            if( name.equals( table[i].getName() )) {
                return table[i];
            }
        }
        return null;
    }
}
