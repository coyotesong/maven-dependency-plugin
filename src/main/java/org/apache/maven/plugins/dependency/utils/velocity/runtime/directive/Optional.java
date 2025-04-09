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
import java.util.ArrayList;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.introspection.Info;

/**
 * The Optional directive is used with single-line changes
 * </p>
 * This is an #evaluate with an additional boolean property.
 * </p>
 * It is common for templates to omit null or blank values.
 * This can be done with a standard #if block if multiple lines
 * are added or removed but when multiple properties must be
 * individually checked that overhead quickly adds up to visual
 * clutter.
 * </p>
 * The purpose of this directive is to replace a conditional
 * block containing a single line with a single line containing
 * both the conditional and value. The value should only be
 * evaluated if the conditional expression evaluates to 'true'.
 * </p>
 * A downside of this approach is that it may leave orphan whitespace.
 * </p>
 * There are several possible extensions:
 * <ul>
 *    <li>Add a third property, for "else"</li>
 *    <li>Provide a minimalized version that quietly handles the most common case of 'not null'.</li>
 * </ul>
 */
public class Optional extends Directive {

    protected Info uberInfo;

    @Override
    public int getType() {
        return Directive.LINE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "optional";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);

        /*
        key = rsvc.getString(RuntimeConstants.VM_BODY_REFERENCE, "bodyContent");

        maxDepth = rsvc.getInt(RuntimeConstants.VM_MAX_DEPTH, Integer.MAX_VALUE);
         */

        uberInfo = new Info(getTemplateName(), getLine(), getColumn());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
        /*
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
         */
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkArgs(ArrayList<Integer> argtypes, Token t, String templateName) throws ParseException {
        super.checkArgs(argtypes, t, templateName);

        // see Define and Evaluate.
    }
}
