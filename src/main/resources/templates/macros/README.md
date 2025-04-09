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
# Velocity macros

This directory contains the format-specific macros.

## Format-specific escaping

This has only come up in one place with the DependencyNode object
but we need to be aware of the need to escape some values in both
the velocity template and format-specific macros.

The most common example is the XML/HTML special characters -
<, >, &, and potentially more. 

Another is the ':' character in YAML documents. Any 'value' containing
a colon must be in quotes.

A more subtle example is that YAML supports multiline values but
it's handled by putting a specific value on the current line.
Plus all leading whitespace is ignored unless explicitly quoted.

On top of all of this we need to consider nested quotes.

I think velocity already has some support for this but if not we
can either add some type of filter in the document or precompute
it when converting from a `DependencyNode` to a `DecoratedNode`.
The latter is tempting - it's easy to implement - but it would
prevent a `DecoratedNode` from being used to create multiple output
formats.

In either case we can attach the code to the `Style` class. See
`DOTDependencyNodeVisitor.DOTStyle` for an example.

## Nested patterns

Nested patterns always are always recursive calls with indentation.
We can use multiple `#indent` directives if we want a clean separation
between parent and child, see YAML as an example. It's important to
keep the indentation pattern consistent throughout the document - it
doesn't matter for JSON or XML but does matter for YAML.

One big difference is that some formats (JSON) drop the final
separator. This could be determined via another macro but the
logic can be greatly simplified if the data structure includes a
pregenerated `isLast()` method.

For all formats:

```
#marco (showNode $node)
  ## implement format-specific code here **
#end
```

but note that the implementation may require something like

```
#if ($dependency.children.empty)
  "optional": "$dependency.optional"
#else
  "optional": "$dependency.optional",
#end  
```

If all children are treated the same:

```
#macro (showChildDependencies $parent)
  children:
  #for ($child in $parent.children index $idx)
  #indent
    #showNestingDependencies($child, $idx, $parent.children.size)
  #end
  #end
#end

#macro (showNestingDependencies $dependency)
#indent
- #showNode($dependency)
#if (!$dependency.children.empty)
  #showChildDependencies($dependency)
#end
#end
#end
```

If the final child is treated differently:

```
#macro (showChildDependencies $parent)
  "children":
  [
  #indent
  #foreach ($child in $parent.children)
    #indent
    #showNestingDependencies($child)
    #end
  #end
  #end
  ]
#end

#macro (showNestingDependencies $dependency)
{
  #showDependency($dependency)
#if (!$dependency.children.empty)
  #showChildDependencies($dependency)
#end
#if ($dependency.last)
}
#else
},
#end
#end
```

## DOT-specific formatting trick.

DOT usually draws edges between nodes - and this is confusing if a single
node contains multiple artifacts as discussed in the parent directory's README.

Fortunately there's a simple solution to this - we can specify a `rank`
to each table row and then append an **unquoted** `:rank` to the edge. This
will draw the edge to the correct row within the table.

Note: this `rank` has nothing to do with the node's rank (distance from the
root artifact). It is best to think of it as a row number although its more
general than that.

## TODO

- can we use a variant of ${dependency.last|null} ? 
