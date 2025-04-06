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

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugins.dependency.utils.templates.Style;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * Optional styles, for the velocity templates that can use them.
 */
public class DOTStyle extends Style {
    private final Map<String, String> scopeColors = new HashMap<>();

    public DOTStyle() {
        scopeColors.put("compile", "black");
        scopeColors.put("test", "darkgreen");
        scopeColors.put("provided", "blue");
        scopeColors.put("system", "blue");
        scopeColors.put("runtime", "blue");
        scopeColors.put("import", "orange");
    }

    public String getDefaultGraphFormat() {
        return String.format(
                "color = \"%s\"; fontcolor = \"%s\"; fontname = \"%s\"; fontsize = %d",
                "black", "black", getFontname(), getFontsize() + 2);
    }

    public String getDefaultNodeFormat() {
        final String color = getDefaultColor();
        return String.format(
                "color = \"%s\"; fontcolor = \"%s\"; fontname = \"%s\"; fontsize = %d; style = \"%s\"; fillcolor = \"%s\"",
                color, color, getFontname(), getFontsize(), "filled", "white");
    }

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

        if (node.getArtifact().isSnapshot()) {
            // do nothing for now...
        }

        if (sb.length() > 0) {
            sb.append(";");
        }

        return sb.toString();
    }

    public String getEdgeFormat(DependencyNode parent, DependencyNode child) {
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
