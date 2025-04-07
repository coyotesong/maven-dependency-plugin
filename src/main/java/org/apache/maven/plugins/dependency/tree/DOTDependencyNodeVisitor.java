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

import org.apache.maven.plugins.dependency.utils.velocity.Style;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * A dependency node visitor that serializes visited nodes to <a href="https://en.wikipedia.org/wiki/DOT_language">DOT
 * format</a>
 *
 * @author <a href="mailto:pi.songs@gmail.com">Pi Song</a>
 * @author <a href="mailto:bgiles@coyotesong.com">Bear Giles</a> (version 3.9)
 * @since 2.1
 */
public class DOTDependencyNodeVisitor extends VelocityDependencyNodeVisitor {
    public static final String DEFAULT_MACRO_LIBRARY = "templates/macros/dot/dot-macros-for-dependency-tree.vm";

    /**
     * {@inheritDoc}
     */
    public DOTDependencyNodeVisitor(Writer writer) {
        super(writer, DEFAULT_TEMPLATE_NAME, DEFAULT_MACRO_LIBRARY);
        super.setStyle(new DOTStyle());
    }

    /**
     * DOT-specific style
     * </p>
     * This class lets us eliminate a lot of nasty YTL logic, esp.
     * with the node- and edge-specific formats.
     */
    public static class DOTStyle extends Style {

        @SuppressWarnings("unused")
        public String getDefaultGraphFormat() {
            return String.format(
                    "color = \"%s\"; fontcolor = \"%s\"; fontname = \"%s\"; fontsize = %d",
                    getColor(), getColor(), getFontname(), getFontsize() + 2);
        }

        @SuppressWarnings("unused")
        public String getDefaultNodeFormat() {
            final String color = getDefaultColor();
            return String.format(
                    "color = \"%s\"; fontcolor = \"%s\"; fontname = \"%s\"; fontsize = %d; style = \"%s\"; fillcolor = \"%s\"",
                    color, color, getFontname(), getFontsize(), "filled", getBackgroundColor());
        }

        @SuppressWarnings("unused")
        public String getDefaultEdgeFormat() {
            final String color = getDefaultColor();
            return String.format(
                    "color = \"%s\"; fontcolor = \"%s\"; fontname = \"%s\"; fontsize = %d; style = \"%s\"",
                    color, color, getFontname(), getFontsize(), "solid");
        }

        public String getNodeFormat(DependencyNode node) {
            final String color = getColorForNode(node);
            final StringBuilder sb = new StringBuilder();
            if (!getDefaultColor().equals(color)) {
                sb.append(String.format("color = \"%s\", fontcolor = \"%s\"", color, color));
            }

            if (node.getArtifact().isOptional()) {
                sb.append(", style = \"dashed\"");
            }

            if (sb.length() > 0) {
                sb.append(";");
            }

            return sb.toString();
        }

        public String getEdgeFormat(@SuppressWarnings("unused") DependencyNode parent, DependencyNode child) {
            final String color = getColorForNode(child);
            final StringBuilder sb = new StringBuilder();
            if (!getDefaultColor().equals(color)) {
                sb.append(String.format("color = \"%s\", fontcolor = \"%s\"", color, color));
            }

            if (child.getArtifact().isOptional()) {
                sb.append(", style = \"dotted\"");
            }

            return sb.toString();
        }

        public boolean getShowType(DependencyNode node) {
            return !"jar".equals(node.getArtifact().getType());
        }
    }
}
