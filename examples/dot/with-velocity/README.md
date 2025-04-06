# .DOT Examples When Using Velocity

Note: the actual changes are due to changing what DOTDependencyNodeVisitor
produces, not the change to a velocity template. However the ability to use
a template instead of making painstaking changes to the java code made it
easy to explore different approaches since the template is so close to the
final document.

## Generated images

Remember the legacy format was unusable - the equivalent images were often
around 32k by 600 pixels and could not be opened at all by some common
viewers.

This format adds an explicit node (html table) for each artifact, makes a
few human-friendly changes (replace scope with color coding, removes all
null values, does not show the 'jar' type), etc. It's still wide - but it
can now be viewed by most if not all common viewers and the user can usually
'zoom in' if they want to see the details.

- [!png](with-velocity.png "PNG")

- [!jpg](with-velocity.jpg "JPG")

- [!gif](with-velocity.gif "GIF")

- [!svg](with-velocity.svg "SVG")

Note: the `svg` format supports interactive elements like "tooltips".
I haven not included them (yet) but they could provide additional
information like (potentially) the projects title and/or description.

## .DOT template

This is the current template for .DOT output. It looks like many other
formats can use the same template - so much so that it may be possible
to generalize this to a single common template plus a few simple macros
pulled from a separate directory.

```
#**
This template produces the .dot format dependency tree
*#

digraph "$root.toNodeString()" {
    graph [ ranksep=1.5; nodesep=0.2; $style.defaultGraphFormat ]
    node [ shape="none"; margin=0; $style.defaultNodeFormat ]
    edge [ $style.defaultEdgeFormat ]

    #walkTreeForNodes($root)

    #walkTreeForEdges($root)
}

#macro(walkTreeForNodes $parent)
    #showSimpleDependency($parent)
    #foreach ($child in $parent.children)
        #walkTreeForNodes($child)
    #end
#end
#end

#macro (showSimpleDependency $dependency)
#set ($artifact = $dependency.artifact)
    "$dependency.alias" [ $dependency.nodeFormat label = <
       <table>
           <tr><td>$artifact.groupId</td></tr>
           <tr><td>$artifact.artifactId</td></tr>
           <tr><td>$artifact.version</td></tr>
       #if ($dependency.showType)
           <tr><td>$artifact.type</td></tr>
       #end
       #if ($dependency.showClassifier)
           <tr><td><b>$artifact.classifier</b></td></tr>
       #end
    </table>
    >;
]

#end

#macro(walkTreeForEdges $parent)
    #foreach ($child in $parent.children)
        "$parent.alias" -> "$child.alias" [ $child.edgeFormat ];
        #walkTreeForEdges($child)
    #end
#end
```

## Other formats

The other formats will have a similar approach but obviously different
details in the `showSimpleDependency` macro and the edge macro.

The main difference will be the addition of a (new) Velocity directive -
`#indent'. This handles proper indentation of nested outputs like json
or yaml.

```
#macro(walkTreeForNodes $parent)
#indent
    #showSimpleDependency($parent)
    #foreach ($child in $parent.children)
        #walkTreeForNodes($child)
    #end
#end
#end
```