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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Information about a collection of dependencies
 * <p>
 * This class forms a tree based on the dependency's groupId
 */
public class Cluster {
    private static final Logger LOG = LoggerFactory.getLogger(Cluster.class);

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final String id;
    private final String groupId;

    private String version;
    private String edgeId;

    private List<Node> dependencies = new ArrayList<>();
    private boolean isNested = false;

    private Map<String, Cluster> subclusters = new LinkedHashMap<>();

    public Cluster(String groupId, String version) {
        this.id = "cluster_" + COUNTER.incrementAndGet();
        this.groupId = groupId;
        this.version = version;
        this.edgeId = "\"" + groupId + "\"";
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public List<Cluster> getClusterChildren() {
        return new ArrayList<>(subclusters.values());
    }

    public List<Node> getDependencies() {
        return dependencies;
    }

    public String getLabel() {

        if (version == null) {
            return groupId;
        }
        return groupId + " | " + version;
    }

    public boolean isNested() {
        return isNested;
    }

    public boolean isNotNested() {
        return !isNested();
    }

    void add(Node node) {
        node.setCluster(this);
        node.setPort(this.dependencies.size());
        this.dependencies.add(node);
        if ((version != null) && !version.equals(node.getVersion())) {
            version = null;
        }
    }

    void addChild(Cluster cluster) {
        if (subclusters.containsKey(cluster.groupId)) {
            LOG.info("cluster already contains this subcluster! {}", cluster.groupId);
        } else {
            subclusters.put(cluster.groupId, cluster);
            cluster.isNested = true;
        }
    }
}
