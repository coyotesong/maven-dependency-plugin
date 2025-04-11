/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.dependency.utils.templates;

import junit.framework.TestCase;

public class TestVelocityHelper extends TestCase {

    /**
     * For now just verify that we can initialize the VelocityEngine
     */
    public void testConstructor() {
        VelocityHelper helper = new VelocityHelper();
        assertNotNull(helper);

        /*
        VelocityHelper helper = new VelocityHelper();
        String s  = helper.merge("templates/dot/dot2.vm", Collections.emptyList());
        System.out.println(s);
         */
    }
}
