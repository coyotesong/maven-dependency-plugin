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
package org.apache.maven.plugins.dependency.resolvers;

import javax.inject.Inject;

import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.dependency.utils.ResolverUtil;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.shared.transfer.dependencies.resolve.DependencyResolver;
import org.apache.maven.shared.transfer.repository.RepositoryManager;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Goal that resolves the project source dependencies from the repository.
 *
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @since 2.0-alpha2/3.7.0
 */
@Mojo(
        name = "resolve-sources",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST,
        threadSafe = true)
public class ResolveDependencySourcesMojo extends ResolveDependenciesMojo {

    private static final String SOURCES_CLASSIFIER = "sources";

    @Inject
    // CHECKSTYLE_OFF: ParameterNumber
    public ResolveDependencySourcesMojo(
            MavenSession session,
            BuildContext buildContext,
            MavenProject project,
            ResolverUtil resolverUtil,
            DependencyResolver dependencyResolver,
            RepositoryManager repositoryManager,
            ProjectBuilder projectBuilder,
            ArtifactHandlerManager artifactHandlerManager) {
        super(
                session,
                buildContext,
                project,
                resolverUtil,
                dependencyResolver,
                repositoryManager,
                projectBuilder,
                artifactHandlerManager);
    }
    // CHECKSTYLE_ON: ParameterNumber

    @Parameter(name = "classifier", defaultValue = SOURCES_CLASSIFIER, readonly = true)
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
}
