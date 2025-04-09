<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Velocity Templates

These are the top-level templates that should be as format-agnostic as possible -
keeping those details in the macros will make the templates as reusable as
possible.

Of course everything in moderation!

## Standard dependency graphs

The standard dependency graphs fall into one of two broad categories.
The first is "flat" since the artifacts and dependencies are specified
separately and there's no need for indentation in the final document.

The second is "nested" since the artifacts and dependencies are
specified at the same time via a parent-child relationship. It's
important to note that the dependency graph is a directed acyclic
graph (DAG), not a true tree, and this approach may result in duplication
unless back-references are supported.

## Advanced dependency graphs

The DOT format is used to create images and this opens the door to
more creative dependency graphs. For instances the artifacts in each
rank (distance from root) could be grouped by `groupId` and presented
in a single table. This can dramatically reduce the width of the
generated image and make it much more usable.

While there's no guarantee that all artifacts will have the same `version`
it can be checked since, when true, it allows that column to be removed
and the width will be further reduced.
