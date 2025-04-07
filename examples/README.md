# Examples pre/post velocity changes

## Initial Motivation

The initial motivation was modifying `DOTDependencyNodeVisitor` so it creates usuable results.

The best way to demonstrate the need and improvements can be seen in the `png` files:

| Release        | Generated image                                               |
+----------------+---------------------------------------------------------------+ 
| 3.8.2-SNAPSHOT | PNG image data, 32767 x 472, 8-bit/color RGB, non-interlaced  |
| refactored     | PNG image data, 7687 x 1243, 8-bit/color RGBA, non-interlaced |

Note: the original image was scaled - this is the max. width for a PNG image.

## Additional Motivation

I knew I wanted to experiment with the output - the DOT format is used for image generation,
not just machine-to-machine communications - so it opens up a lot of possibilities.

At a minimum that requires two passes - one for nodes and one for edges. (They could
probably be intertwined but that would make it harder to manually review the output.)
In addition I knew I might end up with fairly complex outputs when I started to experiment
with things like grouping nodes (artifacts) by their groupId or other criteria.

It made a lot more sense to use a template - and velocity was already a project
dependency. It was only used during tests but it was already a dependency...

I was aware that at least some of the other formats could be easily handled via a
velocity template, most notably JSON, so I decided to refactor it as well in order
to ensure that velocity templates could be used for more than the relatively simple
DOT format. Lather, rinse, repeat, and all of the existing outputs have been converted.

This may seem like overkill but even seemingly simple formats (e.g., JSON) can
require surprisingly complex code. The current implementation could have been
simplified with a `StringBuffer` (easy to remove the final comma) but I think the
velocity macros will still be easier to maintain as long as you don't need to
change the ```#if``` - and even then you can often precompute anything complicated.


## Velocity extensions

I created two new Velocity Engine Directives for this - I'll be contributing them
to the Velocity project but there will probably need to be an embedded copy for awhile.
The contributed Directives may have some additional features but this project doesn't
need them (yet).

The Directives are:

 - #indent - handles indentation of nested elements
 - #collapse - collapses a multiline block to a single line. This makes the template
   easy to edit while reducing the size of the final document.


## TXT output

I haven't verified this yet but I think this can also be used to generate the TXT
format after adding the ability to specify the padding. We'll be close, at least,
if we use ```" | "``` as the padding and either ```" +- "``` or ```" `-"``` in
the template (macro) itself. We can select between the last two items by checking
```$property.last```.

I know it's a little more complicated than this - it looks like the existing code
supports three(?) different sets of characters. This would be easy to handle since
there's already a ```Style``` object to handle this type of detail.

The main benefit to using Velocity would be consistency with all other formats.
