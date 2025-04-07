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
import java.util.function.Function;

import org.apache.maven.plugins.dependency.utils.velocity.Style;
import org.apache.maven.plugins.dependency.utils.velocity.VelocityTemplateEngine;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * A dependency node visitor that serializes visited nodes to a writer using a velocity template.
 *
 * @author <a href="mailto:bgiles@coyotesong.com"> Bear Giles</a>
 * @since 3.9
 */
public class VelocityDependencyNodeVisitor extends AbstractSerializingVisitor
        implements DependencyNodeVisitor, Function<DependencyNode, Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger(VelocityDependencyNodeVisitor.class);

    public static final String DEFAULT_TEMPLATE_NAME = "templates/dependency-tree.vm";

    private String templateName;
    private String macroLibrary;
    private Style style;
    private Boolean nested = Boolean.FALSE;

    /**
     * {@inheritDoc}
     */
    public VelocityDependencyNodeVisitor(Writer writer) {
        super(writer);
    }

    /**
     * {@inheritDoc}
     *
     * @param macroLibrary the name of the macro library (usually format-specific)
     */
    protected VelocityDependencyNodeVisitor(Writer writer, String templateName, String macroLibrary) {
        this(writer);
        this.templateName = templateName;
        this.macroLibrary = macroLibrary;
    }

    @SuppressWarnings("unused")
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @SuppressWarnings("unused")
    public void setMacroLibrary(String macroLibrary) {
        this.macroLibrary = macroLibrary;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public void setNested(Boolean nested) {
        this.nested = nested;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean visit(DependencyNode node) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean endVisit(DependencyNode node) {
        if (node.getParent() == null || node.getParent() == node) {
            return Boolean.TRUE.equals(apply(node));
        }
        return true;
    }

    /**
     * Generates output using Velocity engine, template, and dependency tree.
     * </p>
     * {@inheritDoc}
     */
    @Override
    public Boolean apply(DependencyNode root) {
        if (style == null) {
            style = new Style();
        }

        final DecoratedNode decoratedRoot = new DecoratedNode(root, style);

        final MDC.MDCCloseable mdc = MDC.putCloseable("rootNode", root.toNodeString());
        try {
            final VelocityTemplateEngine engine = new VelocityTemplateEngine().withMacro(macroLibrary);
            writer.write(engine.withTemplateName(templateName)
                    .withStyle(style)
                    .put("root", decoratedRoot)
                    .put("nestedFormat", nested)
                    .evaluate());
        } catch (ResourceNotFoundException | ParseErrorException e) {
            LOG.info(
                    "{}: an error occurred while evaluating '{}' for root '{}'",
                    e.getClass().getName(),
                    templateName,
                    root.toNodeString(),
                    e);
            return Boolean.FALSE;
        } finally {
            mdc.close();
        }

        return Boolean.TRUE;
    }
}
