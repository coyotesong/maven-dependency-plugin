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

import java.util.Collections;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for VelocityTemplateEngine
 */
public class TestVelocityTemplateEngine {

    @Test
    public void testGivenNoTemplateThenFailure() {
        final VelocityTemplateEngine engine = new VelocityTemplateEngine();

        final IllegalStateException thrown = assertThrows(IllegalStateException.class, engine::evaluate);

        assertEquals(VelocityTemplateEngine.MESSAGE_NO_TEMPLATE_SPECIFIED, thrown.getMessage());
    }

    @Test
    public void testGivenUnknownTemplateThenFailure() {
        final String templateName = "templates/tests/unknown.vm";
        final VelocityTemplateEngine engine = new VelocityTemplateEngine();

        final ResourceNotFoundException thrown =
                assertThrows(ResourceNotFoundException.class, () -> engine.withTemplateName(templateName));

        final String expected = String.format(VelocityTemplateEngine.MESSAGE_RESOURCE_NOT_FOUND, templateName);
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    public void testGivenValidTemplateWhenHelloWorldThenSuccess() {
        final String templateName = "templates/tests/helloworld.vm";
        final VelocityTemplateEngine engine = new VelocityTemplateEngine();

        final String expected = "Hello, world!";
        final String actual = engine.withTemplateName(templateName).evaluate();

        assertEquals(expected, actual.trim());
    }

    @Test
    public void testGivenValidTemplateWhenEchoThenSuccess() {
        final String templateName = "templates/tests/echo.vm";
        final String expected = "Hello, world!";
        final VelocityTemplateEngine engine = new VelocityTemplateEngine();

        final String actual =
                engine.withTemplateName(templateName).put("message", expected).evaluate();

        assertEquals(expected, actual.trim());
    }

    @Test
    public void testGivenValidDynamicTemplateWhenEchoThenSuccess() {
        final String template = "$message";
        final String expected = "Hello, world!";
        final VelocityTemplateEngine engine = new VelocityTemplateEngine();

        final String actual =
                engine.withDynamicTemplate(template).put("message", expected).evaluate();

        assertEquals(expected, actual);
    }

    @Test
    public void testGivenInvalidDynamicTemplateThenFailure() {
        final String template = "#foreach( $item in $list)]\n#endfor\n";
        final VelocityTemplateEngine engine = new VelocityTemplateEngine();

        assertThrows(ParseErrorException.class, () -> engine.withDynamicTemplate(template)
                .put("list", Collections.emptyList())
                .evaluate());
    }
}
