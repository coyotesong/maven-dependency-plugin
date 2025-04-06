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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.apache.maven.plugins.dependency.utils.templates.TemplateEngine;
import org.apache.maven.plugins.dependency.utils.templates.VelocityTemplateEngine;
import org.apache.maven.plugins.dependency.utils.velocity.runtime.directive.Indent;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * A dependency node visitor that serializes visited nodes to <a href="https://en.wikipedia.org/wiki/DOT_language">DOT
 * format</a>
 *
 * @author <a href="mailto:pi.songs@gmail.com">Pi Song</a>
 * @since 2.1
 */
public class DOTDependencyNodeVisitor extends AbstractSerializingVisitor
        implements DependencyNodeVisitor, Function<DependencyNode, Boolean> {
    private static final Logger LOG = LoggerFactory.getLogger(DOTDependencyNodeVisitor.class);

    private String templateName;
    private DOTStyle style = new DOTStyle();

    /**
     * Constructor.
     *
     * @param writer the writer to write to.
     */
    public DOTDependencyNodeVisitor(Writer writer) {
        super(writer);

        // this could be pulled from mojo properties
        // templateName = "templates/dot/legacy-dependency-tree.dot.vm";
        templateName = "templates/dot/dependency-tree.dot.vm";
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
     * {@inheritDoc}
     */
    @Override
    public Boolean apply(DependencyNode root) {
        final DecoratedNode decoratedRoot = new DecoratedNode(root, style);

        final MDC.MDCCloseable mdc = MDC.putCloseable("rootNode", root.toNodeString());
        try {
            final List<String> directives = Collections.singletonList(Indent.class.getName());
            final TemplateEngine engine = new VelocityTemplateEngine(directives);
            writer.write(engine.withTemplateName(templateName)
                    .withStyle(style)
                    .put("root", decoratedRoot)
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
