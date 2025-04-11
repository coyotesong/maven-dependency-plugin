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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * Wrapper around DependencyNode - it allows us to modify the
 * effective content without affecting the rest of the system.
 * <p>
 * This class forms a tree based on maven dependencies.
 */
public class Node implements Comparable<Node> {
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final DependencyNode node;
    private final Node parent;

    private String id;
    private int port;
    private List<Node> children = new ArrayList<>();
    private Cluster cluster;

    public Node(DependencyNode node) {
        this(node, null);
    }

    public Node(DependencyNode node, Node parent) {
        this.node = node;
        this.parent = parent;

        this.id = "node_" + COUNTER.incrementAndGet();
    }

    public String getId() {
        return id;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    /**
     * An 'edgeId' is similar to an 'id' but includes an optional
     * 'port'. This allows us to have edges point to specific rows
     * in a table. This should only be used when creating 'dot'
     * edges.
     *
     * @return edgeId (string)
     */
    public String getEdgeId() {
        if (cluster == null) {
            return "\"" + node.getArtifact().getGroupId() + "\"";
        }
        return "\"" + cluster.getGroupId() + "\":f" + port;
    }

    public String getNodeString() {
        return node.toNodeString();
    }

    public Artifact getArtifact() {
        return node.getArtifact();
    }

    public String getGroupId() {
        return node.getArtifact().getGroupId();
    }

    public String getArtifactId() {
        return node.getArtifact().getArtifactId();
    }

    public String getVersion() {
        return node.getArtifact().getVersion();
    }

    public String getScope() {
        return node.getArtifact().getScope();
    }

    public String getType() {
        return node.getArtifact().getType();
    }

    public boolean isHasClassifier() {
        return node.getArtifact().getClassifier() != null;
    }

    public String getClassifier() {
        return node.getArtifact().getClassifier();
    }

    public boolean isOptional() {
        return node.getArtifact().isOptional();
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return node.toNodeString();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Node that) {
        return this.node.getArtifact().compareTo(that.node.getArtifact());
    }
}
