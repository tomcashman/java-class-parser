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

/**
 * Described a field, method, function, etc.
 */
public interface Member {

    /**
     * The simple name without package
     * 
     * @return the name
     */
    String getName();

    /**
     * The class name of the declaring class.
     * 
     * @return the class name
     */
    String getClassName();

    /**
     * Get the type of the method or field. For example "(Ljava.lang.String;)I"
     * 
     * @return the type
     */
    String getType();
}
