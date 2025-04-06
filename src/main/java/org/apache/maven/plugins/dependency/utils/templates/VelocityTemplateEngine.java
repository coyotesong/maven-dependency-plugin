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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.PrintExceptions;
import org.apache.velocity.app.event.implement.ReportInvalidReferences;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.ParserPoolImpl;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Implementation of TemplateEngine using Velocity
 *
 * {@see https://velocity.apache.org/engine/developer-guide.html#orgapachevelocityappeventinvalidreferenceevennthandler}
 *
 * @author <a href="mailto:bgiles@coyotesong.com">Bear Giles</a>
 * @since 3.8.2
 */
public class VelocityTemplateEngine implements TemplateEngine {
    private static final Logger LOG = LoggerFactory.getLogger(VelocityTemplateEngine.class);

    // these could/should be loaded from a ResourceBundle
    static final String MESSAGE_NO_TEMPLATE_SPECIFIED = "No template was specified";
    static final String MESSAGE_RESOURCE_NOT_FOUND = "Unable to find resource '%s'";

    private final Context context = new VelocityContext();
    private final List<String> macroList = new ArrayList<>();
    private final VelocityEngine engine;

    private Style style;
    private String input;
    private String templateName;
    private Template template;

    /**
     * Default constructor
     *
     * @throws ResourceNotFoundException
     * @throws ParseErrorException
     */
    public VelocityTemplateEngine() throws ResourceNotFoundException, ParseErrorException {
        this(Collections.emptyList());
    }

    /**
     * Constructor accepting a collection of directives.
     *
     * @param directives
     * @throws ResourceNotFoundException
     * @throws ParseErrorException
     */
    public VelocityTemplateEngine(Collection<String> directives) throws ResourceNotFoundException, ParseErrorException {
        engine = new VelocityEngine();

        // note: we can't use the default velocity.properties file since that breaks
        // some of the tests.
        //
        // This is explicitly added since I've seen inconsistent behavior without it.
        engine.setProperty(RuntimeConstants.RESOURCE_MANAGER_CLASS, ResourceManagerImpl.class.getName());
        engine.setProperty(RuntimeConstants.PARSER_POOL_CLASS, ParserPoolImpl.class.getName());
        engine.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, UberspectImpl.class.getName());

        // throw exception if the template contains an invalid reference. This will
        // usually better for us than creating an invalid result.klk
        engine.setProperty(RuntimeConstants.EVENTHANDLER_INVALIDREFERENCES, ReportInvalidReferences.class.getName());
        engine.setProperty("event_handler.invalid_references.exception", "true");

        // log any exception thrown by our code
        engine.setProperty(RuntimeConstants.EVENTHANDLER_METHODEXCEPTION, PrintExceptions.class.getName());

        // application-specific properties
        engine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath,file");
        engine.setProperty(
                RuntimeConstants.RESOURCE_LOADER + ".classpath.class", ClasspathResourceLoader.class.getName());

        directives.forEach(engine::loadDirective);

        engine.init();
    }

    /**
     * {@inheritDoc}
     */
    public boolean resourceExists(String resourceName) {
        return engine.resourceExists(resourceName);
    }

    /**
     * {@inheritDoc}
     */
    public VelocityTemplateEngine withDynamicTemplate(String template) throws ResourceNotFoundException {
        this.input = template;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public VelocityTemplateEngine withTemplateName(String templateName) throws ResourceNotFoundException {
        final MDC.MDCCloseable mdc = MDC.putCloseable("templateName", templateName);
        try {
            this.templateName = templateName;
            template = engine.getTemplate(templateName);
        } catch (ResourceNotFoundException e) {
            // this looks silly but allows for future I18N
            // we can't use 'resourceExists' since a template isn't considered a resource. (!!)
            throw new ResourceNotFoundException(String.format(MESSAGE_RESOURCE_NOT_FOUND, templateName), e);
        } finally {
            mdc.close();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public VelocityTemplateEngine withMacro(String... library) {
        return withMacros(Arrays.asList(library));
    }

    /**
     * {@inheritDoc}
     */
    public VelocityTemplateEngine withMacros(List<String> libraries) {
        this.macroList.addAll(libraries);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public VelocityTemplateEngine withStyle(Style style) {
        this.style = style;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public VelocityTemplateEngine put(String name, Object value) {
        context.put(name, value);
        return this;
    }

    /**
     * Clean up string - trim it, use proper line separators, etc.
     *
     * @param dirty output from Velocity engine
     * @return cleaned up content
     */
    String cleanup(String dirty) {
        String clean = dirty.trim();
        if (clean.indexOf("\n") > 0) {
            clean = clean + "\n";
        }
        if (!"\n".equals(System.lineSeparator())) {
            clean = clean.replace("\n", System.lineSeparator());
        }
        return clean;
    }

    /**
     * {@inheritDoc}
     */
    public String evaluate() throws ResourceNotFoundException, ParseErrorException {
        if ((template == null) && (input == null)) {
            throw new IllegalStateException(MESSAGE_NO_TEMPLATE_SPECIFIED);
        }

        if (style != null) {
            context.put("style", style);
        }

        // future work - support I18N as above.
        try (StringWriter sw = new StringWriter()) {
            if (template != null) {
                final MDC.MDCCloseable mdc = MDC.putCloseable("templateName", templateName);
                try {
                    template.merge(context, sw, macroList);
                } finally {
                    mdc.close();
                }
            } else {
                engine.evaluate(context, sw, "dynamic", input);
            }
            return cleanup(sw.toString());
        } catch (IOException e) {
            // this should never happen... I saw the recommendation for throwing
            // AssertionErrors as a good way to handle this as long is it won't
            // cause a critical system to croak since it can't be ignored.
            LOG.warn("impossible condition - IO error with StringWriter");
            throw new AssertionError("Impossible condition - IOException from StringWriter", e);
        }
    }
}
