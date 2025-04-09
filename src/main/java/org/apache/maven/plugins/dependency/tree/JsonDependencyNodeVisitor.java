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
package org.apache.maven.plugins.dependency.tree;

import java.io.Writer;

/**
 * A dependency node visitor that serializes visited nodes to a writer using the JSON format.
 *
 * @author unknown
 * @author <a href="mailto:bgiles@coyotesong.com">Bear Giles</a> (3.9)
 */
public class JsonDependencyNodeVisitor extends VelocityDependencyNodeVisitor {
    public static final String DEFAULT_MACRO_LIBRARY = "templates/macros/json-macros-for-dependency-tree.vm";

    /**
     * {@inheritDoc}
     */
    public JsonDependencyNodeVisitor(Writer writer) {
        super(writer, DEFAULT_NESTED_TEMPLATE_NAME, DEFAULT_MACRO_LIBRARY);
        super.setNested(true);
    }
}
