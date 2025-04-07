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
package org.apache.maven.plugins.dependency.utils.velocity;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * Style properties, for the templates that can use them.
 */
public class Style {
    private static String defaultFontname = "Helvetica,Arial,sans-serif";
    private static int defaultFontSize = 9;
    private static String defaultColor = "black";
    private static String defaultBackgroundColor = "white";

    private final Map<String, String> scopeColors = new HashMap<>();

    private String fontname = defaultFontname;
    private int fontsize = defaultFontSize;
    private String color = defaultColor;
    private String backgroundColor = defaultBackgroundColor;

    public Style() {
        scopeColors.put("compile", "black");
        scopeColors.put("test", "darkgreen");
        scopeColors.put("provided", "blue");
        scopeColors.put("system", "blue");
        scopeColors.put("runtime", "blue");
        scopeColors.put("import", "orange");
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDefaultColor() {
        return color;
    }

    public String getFontname() {
        return fontname;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    public int getFontsize() {
        return fontsize;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }

    public String getColorForNode(DependencyNode node) {
        return scopeColors.getOrDefault(node.getArtifact().getScope(), "red");
    }
}
