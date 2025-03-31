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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Optional styles, for the velocity templates that can use them.
 */
public class Style {
    private static final Logger LOG = LoggerFactory.getLogger(Style.class);

    public static final Style DEFAULT_STYLE = new Style();

    private static String defaultFontname;
    private static String defaultItalicFontname;
    private static int defaultFontSize;

    static {
        final String filename = Style.class.getName().replace('.', '/') + "properties";
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
            final Properties props = new Properties();
            props.load(is);
            defaultFontname = props.getProperty("defaultFontName", "Helvetica,Arial,sans-serif");
            defaultItalicFontname = props.getProperty("defaultFontName", "Helvetica,Arial,sans-serif");
            defaultFontSize = Integer.parseInt(props.getProperty("defaultFontSize", "9"), 10);
        } catch (IOException e) {
            LOG.info("unable to load property file '{}'", filename);
            // safe to ignore...
        }
    }

    private String fontname = defaultFontname;
    private String italicFontname = defaultItalicFontname;
    private int fontsize = defaultFontSize;

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
}
