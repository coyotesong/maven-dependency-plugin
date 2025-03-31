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

import java.util.List;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * Abstraction of template engines (velocity, jinjava,...)
 *
 * <p>
 * Note: the exceptions are velocity-specific but can be generalized
 * when another template engine is implemented.
 * </p>
 *
 * @author <a href="mailto:bgiles@coyotesong.com">Bear Giles</a>
 * @since 3.8.2
 */
public interface TemplateEngine {

    /**
     * Determine whether the specified resource exists.
     *
     * @param resourceName name of resource
     * @return true if resource exists
     */
    boolean resourceExists(String resourceName);

    /**
     * Use the provided string as the template.
     *
     * @param template dynamically created string
     * @return this (for fluent programming)
     * @throws ResourceNotFoundException a resource was not found
     */
    TemplateEngine withDynamicTemplate(String template) throws ResourceNotFoundException;

    /**
     * Use the specified template file.
     * <p>
     * The default configuration only checks the classpath but that is easily modified.
     * </p>
     * @param templateName location of template file
     * @return this (for fluent programming)
     * @throws ResourceNotFoundException
     */
    TemplateEngine withTemplateName(String templateName) throws ResourceNotFoundException;

    /**
     * Use the (implementation-specific) macro library(s)
     *
     * @param library implementation-specific macro library(s)
     * @return this (for fluent programming)
     */
    TemplateEngine withMacro(String... library);

    /**
     * Use the (implementation-specific) macro library(s)
     *
     * @param libraries implementation-specific macro library(s)
     * @return this (for fluent programming)
     */
    TemplateEngine withMacros(List<String> libraries);

    /**
     * Use the specified style, when appropriate
     *
     * @param style desired style
     * @return this (for fluent programming)
     */
    TemplateEngine withStyle(Style style);

    /**
     * Add (name, value) pair to values passed to template engine
     *
     * @param name name, must not be null
     * @param value value, may be null
     * @return this (for fluent programming)
     */
    TemplateEngine put(String name, Object value);

    /**
     * Evaluate the template and return the generated content
     *
     * @return generated content
     * @throws ResourceNotFoundException a resoure was not found
     * @throws ParseErrorException the template was malformed
     */
    String evaluate() throws ResourceNotFoundException, ParseErrorException;
}
