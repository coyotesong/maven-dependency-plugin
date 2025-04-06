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
package org.apache.maven.plugins.dependency.utils.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * Optional styles, for the velocity templates that can use them.
 */
public class Style {
    private static String defaultFontname = "Helvetica,Arial,sans-serif";
    private static String defaultItalicFontname = "Helvetica:italic,Arial:italic,sans-serif:italic";
    private static int defaultFontSize = 9;
    private static String defaultColor = "black";

    private final Map<String, String> scopeColors = new HashMap<>();

    private String fontname = defaultFontname;
    private String italicFontname = defaultItalicFontname;
    private int fontsize = defaultFontSize;
    private String color = defaultColor;

    public Style() {
        scopeColors.put("compile", "black");
        scopeColors.put("test", "darkgreen");
        scopeColors.put("provided", "blue");
        scopeColors.put("system", "blue");
        scopeColors.put("runtime", "blue");
        scopeColors.put("import", "orange");
    }

    public String getFontname() {
        return fontname;
    }

    public void setFontname(String fontname) {
        this.fontname = fontname;
    }

    public String getItalicFontname() {
        return italicFontname;
    }

    public void setItalicFontname(String italicFontname) {
        this.italicFontname = italicFontname;
    }

    public int getFontsize() {
        return fontsize;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public String getColorForNode(DependencyNode node) {
        return scopeColors.getOrDefault(node.getArtifact().getScope(), "red");
    }
}
