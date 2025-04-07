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
 * The Indent directive is used with recursive calls
 * </p>
 * Many templates use recursion to handle trees and other
 * nested data structures. We want the output to be human-friendly
 * and that often involves indentation matching the recursion
 * depth. (Not always!)
 * </p>
 * This seems hard to do in pure VTL.
 * </p>
 * This Directive solves this problem by adding a predefined
 * padding at each level of recursion.
 * </p>
 * POSSIBLE ENHANCEMENTS
 * </p>
 * The most obvious is allowing the user to define their own
 * padding. I'm looking into this, esp. if it should be a
 * string literal, a reference, or either. This may be added
 * to the 'maven-dependency-plugin' version.
 * </p>
 * The second is allowing the user to specify a reference that
 * will be filled with the current depth of recursion. See #for
 * for a good analogy.
 * </p>
 * The third and fourth is allowing the user to specify a reference
 * that  be filled with the current position (among children)
 * at the current depth of recursion.
 * </p>
 * This can be used to provide document labels like "II", "C", "4", "f)".
 * The user would use the standard '#indent' but then call a macro
 * using both depth and position to get the desired label.
 */
public class Indent extends Block {

    private static final String NEWLINE = "\n";

    // not yet configurable
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
                // using 'split()' gives us the most flexiblity. See #collapse.
                for (String element : w.toString().split(NEWLINE)) {
                    writer.write(padding + element + NEWLINE);
                }

                return true;
            }

            return false;
        }
    }
}
