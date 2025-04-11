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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper classes for DOT dependency charts.
 *
 * TODO: add logging via org.apache.velocity.runtime.log.LogChute
 *
 * @author <a href="mailto:bgiles@coyotesong.com">Bear Giles</a>
 * @since 2.1
 */
public class VelocityHelper {
    private static final Logger LOG = LoggerFactory.getLogger(VelocityHelper.class);
    private static final Style DEFAULT_STYLE = new Style();

    boolean verbose = true;

    private final List<Node> roots = new ArrayList<>();

    private final Stack<Node> stack = new Stack<>();
    private final Map<String, Node> dependencies = new LinkedHashMap<>();
    private final Map<String, Cluster> clusters = new LinkedHashMap<>();

    private final VelocityEngine engine;

    public VelocityHelper() throws ResourceNotFoundException, ParseErrorException {
        final Properties props = new Properties();
        props.setProperty(Velocity.RESOURCE_LOADERS, "classpath, file");
        props.setProperty("resource.loader.file.class", FileResourceLoader.class.getName());
        props.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());

        this.engine = new VelocityEngine(props);
        // this.engine = new VelocityEngine();
        engine.init();

        engine.getTemplate("templates/dot/dot2.vm");
    }

    /**
     * Add a dependency to the list.
     * <p>
     * Implementation note - we do not perform a recursive descent ourselvess
     * since the plugin may have applied filters to the list of dependencies.
     * </p>
     * <p>
     * Implementation note - we log warnings if our assumptions about the data
     * we receive are wrong.
     * </p>
     *
     * @param dependencyNode dependency being added
     * @return true
     */
    public boolean push(DependencyNode dependencyNode) {
        String key = dependencyNode.toNodeString();
        if (dependencies.containsKey(key)) {
            LOG.warn("broken assumption: unexpected second visit to {}", dependencyNode.toNodeString());
            // have we already visited this node during recursive descent?
            stack.push(dependencies.get(key));
            return true;
        }

        Node node = new Node(dependencyNode);
        dependencies.put(key, node);
        if (!stack.isEmpty()) {
            Node parent = stack.peek();
            if (dependencyNode.getParent() == null) {
                LOG.warn("broken assumption: unexpected null parent for {}", dependencyNode.toNodeString());
            } else if (!dependencyNode.getParent().toNodeString().equals(parent.getNodeString())) {
                LOG.warn(
                        "broken assumption: unexpected parent for {}: {} vs {}",
                        dependencyNode.getArtifact().getBaseVersion(),
                        dependencyNode.getParent().getArtifact().getBaseVersion(),
                        parent.getArtifact().getBaseVersion());
            } else {
                parent.getChildren().add(node);
            }
        }

        if (stack.isEmpty() && (dependencyNode.getParent() != null)) {
            LOG.warn("broken assumption: top of stack and parent is not null for {}", dependencyNode.toNodeString());
        } else if (!stack.isEmpty() && (dependencyNode.getParent() == null)) {
            LOG.warn("broken assumption: not top of stack and parent is null for {}", dependencyNode.toNodeString());
        }

        if (!stack.isEmpty()) {
            String groupId = node.getGroupId();
            if (clusters.containsKey(groupId)) {
                clusters.get(groupId).add(node);
            } else {
                // handle special cases first
                if (groupId.startsWith("commons-") && groupId.equals(node.getArtifactId())) {
                    final Cluster cluster = new Cluster(groupId, null);
                    clusters.put(groupId, cluster);
                    clusters.get("org.apache.commons").addChild(cluster);
                    backfill("org.apache.commons");
                } else if (groupId.equals("junit")) {
                    final Cluster cluster = new Cluster(groupId, null);
                    clusters.put(groupId, cluster);
                    backfill("junit");
                    clusters.get("org.junit").addChild(cluster);
                } else {
                    backfill(groupId);
                }
                clusters.get(groupId).add(node);
            }
        }
        stack.push(node);

        if (dependencyNode.getParent() == null) {
            roots.add(node);
        }

        return true;
    }

    public void backfill(String groupId) {
        if (clusters.containsKey(groupId)) {
            return;
        }

        List<String> components = Arrays.asList(groupId.split("\\."));
        int ndots = 2;
        if (groupId.startsWith("org.junit")) {
            ndots = 1;
        }

        if (components.size() <= ndots) {
            clusters.put(groupId, new Cluster(groupId, null));
            return;
        }

        // create intermediate clusters
        Stack<Cluster> stack = new Stack<>();
        for (int i = components.size(); i > ndots; i--) {
            String partialGroupId = String.join(".", components.subList(0, i));
            if (clusters.containsKey(partialGroupId)) {
                stack.push(clusters.get(partialGroupId));
                break;
            }
            Cluster cluster = new Cluster(partialGroupId, null);
            clusters.put(partialGroupId, cluster);
            stack.push(cluster);
        }

        // knit the clusters together
        while (stack.size() > 1) {
            Cluster parent = stack.pop();
            parent.addChild(stack.peek());
        }
    }

    public String merge(String templateName, List<String> macroList) {
        return merge(templateName, macroList, DEFAULT_STYLE);
    }

    // TODO: replace with default velocity.properties file.
    Properties loadProperties() {
        final Properties props = new Properties();
        // props.setProperty(
        //        VelocityEngine.RESOURCE_MANAGER_CLASS, "org.apache.velocity.runtime.resource.ResourceManagerImpl");
        // props.setProperty(VelocityEngine.PARSER_POOL_CLASS,
        // "org.apache.velocity.runtime.resource.DefaultParserPool");
        // props.setProperty(Velocity.RESOURCE_MANAGER_LOGWHENFOUND, "true");

        props.setProperty(Velocity.RESOURCE_LOADERS, "classpath, file");
        props.setProperty("resource.loader.file.class", FileResourceLoader.class.getName());
        props.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());

        return props;
    }

    public String merge(String templateName, List<String> macroList, Style style)
            throws ResourceNotFoundException, ParseErrorException {

        Context context = new VelocityContext();

        context.put("style", style);
        context.put("title", roots.get(0).toString());
        context.put("roots", roots);
        context.put("dependencies", dependencies);
        context.put(
                "clusters",
                clusters.values().stream().filter(Cluster::isNotNested).collect(Collectors.toList()));
        context.put("useDependencies", false);

        // Velocity.reset();
        // Velocity.init(loadProperties());
        final Template t = engine.getTemplate(templateName);

        try (StringWriter sw = new StringWriter()) {
            t.merge(context, sw, macroList);
            return sw.toString();
        } catch (ResourceNotFoundException | ParseErrorException e) {
            // rethrow the exception
            throw e;
        } catch (IOException e) {
            // this should never happen... I saw the recommendation for throwing
            // AssertionErrors as a good way to handle this as long is it won't
            // cause a critical system to croak since it can't be ignored.
            LOG.warn("impossible condition - IO error with StringWriter");
            throw new AssertionError("Impossible condition - IOException from StringWriter", e);
        }
    }

    /**
     * Accepts 'completion' notice.
     *
     * @param dependencyNode finished dependency
     * @return true
     */
    public boolean pop(DependencyNode dependencyNode) {
        // todo - verify of stack and parameter match
        stack.pop();
        return stack.isEmpty();
    }

    public List<Node> getDependencies() {
        return new ArrayList<>(dependencies.values());
    }
}
