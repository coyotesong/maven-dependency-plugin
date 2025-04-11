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
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.dependency.utils.templates.VelocityHelper;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dependency node visitor that serializes visited nodes to <a href="https://en.wikipedia.org/wiki/DOT_language">DOT
 * format</a>
 *
 * @author <a href="mailto:pi.songs@gmail.com">Pi Song</a>
 * @since 2.1
 */
public class VelocityDependencyNodeVisitor extends AbstractSerializingVisitor implements DependencyNodeVisitor {
    // FIXME - this is temporary - need to get access to AbstractMojo.getLog()
    private static final Logger LOG = LoggerFactory.getLogger(VelocityDependencyNodeVisitor.class);

    private final VelocityHelper helper;

    private final String templateName;
    private List<String> macroLibraries = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param writer the writer to write to.
     */
    public VelocityDependencyNodeVisitor(Writer writer, String templateName) {
        super(writer);
        this.templateName = templateName;
        helper = new VelocityHelper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean visit(DependencyNode node) {
        return helper.push(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean endVisit(DependencyNode node) {
        boolean done = helper.pop(node);

        if (done) {
            try {
                writer.write(helper.merge(templateName, macroLibraries));
            } catch (ResourceNotFoundException | ParseErrorException e) {
                LOG.info("velocity error: {}", e.getMessage(), e);
                return false;
            }
        }

        return true;
    }
}
