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
package org.apache.maven.plugins.dependency.utils.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Block;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.StringBuilderWriter;
import org.apache.velocity.util.introspection.Info;

/**
 * Indent directive is used to indent blocks of output
 */
public class Indent extends Block {

    private String padding = "  ";

    protected Info uberInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "indent";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);

        key = rsvc.getString(RuntimeConstants.VM_BODY_REFERENCE, "bodyContent");

        maxDepth = rsvc.getInt(RuntimeConstants.VM_MAX_DEPTH, Integer.MAX_VALUE);

        uberInfo = new Info(getTemplateName(), getLine(), getColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
        try (Writer w = new StringBuilderWriter()) {
            if (super.render(context, w)) {
                // for some reason s.replace() isn't reliable...
                for (String element : w.toString().split("\n")) {
                    writer.write(padding + element + "\n");
                }

                return true;
            }

            return false;
        }
    }
}
