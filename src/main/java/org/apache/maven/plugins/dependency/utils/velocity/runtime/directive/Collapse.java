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
 * The Collapse directive removes internal whitespace
 * </p>
 * This directive allows the template to contain a human-friendly
 * definition while the generated output reduces the size of the
 * generated file by removing unnecessary whitespace. It leaves
 * the existing leading whitespace in place since that usually
 * provides important structural information to anyone inspecting
 * the results.
 * </p>
 * For instance
 * <pre>
 * #collapse
 *    <node id="$id">
 *      <data key="$key">
 *        <y:ShapeNode>
 *          <y:NodeLabel>$label</y:NodeLabel>
 *        </y:ShapeNode>
 *      </data>
 *    </node>
 * #end
 * </pre>
 * will be treated as if were written like
 * <pre>
 *    <node id="$id"><data key="$key"><y:ShapeNode><y:NodeLabel>$label</y:NodeLabel></y:ShapeNode></data></node>
 * </pre>
 * </p>
 * Implementation note: we could use `System.lineSeparator` instead of
 * ```\n``` but that complicates the ```.split()``` call since we have
 * no <i>a priori</i> knowledge of whether the template uses windows,
 * mac, or linux conventions. It seems best to assume a newline and let a
 * final step handle converting all line separators to the desired one.
 */
public class Collapse extends Block {

    private static final String NEWLINE = "\n";
    protected Info uberInfo;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "collapse";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);

        // Block requires a 'key' for error messages.
        key = rsvc.getString(RuntimeConstants.VM_BODY_REFERENCE, "bodyContent");

        maxDepth = rsvc.getInt(RuntimeConstants.VM_MAX_DEPTH, Integer.MAX_VALUE);

        uberInfo = new Info(getTemplateName(), getLine(), getColumn());
    }

    /**
     * Extract the leading whitespace.
     * </p>
     * This method returns an empty line, not the full string,
     * if passed a blank string. This may seem counterintuitive
     * but the goal is to strip all whitespace.
     * </p>
     *
     * @param line line to check
     * @return leading whitespace, or empty string
     */
    String preserveLeadingWhitespace(String line) {
        for (int idx = 0; idx < line.length(); idx++) {
            if (!Character.isWhitespace(line.charAt(idx))) {
                return line.substring(0, idx);
            }
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
        try (Writer w = new StringBuilderWriter()) {
            if (super.render(context, w)) {
                final String[] elements = w.toString().split(NEWLINE);
                // preserve leading whitespace
                // TODO: check multiple lines if first ones are blank?
                writer.write(preserveLeadingWhitespace(elements[0]));

                // remove all internal whitespace
                for (String element : elements) {
                    writer.write(element.trim());
                }

                // finally replace the final newline.
                writer.write(NEWLINE);

                return true;
            }

            return false;
        }
    }
}
