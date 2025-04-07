# DOT generated files

The lsegacy format was unusable - the equivalent images were often
around 32k by 600 pixels and could not be opened at all by some common
viewers.

The new DOT format is 

- [with-velocity.dot](with-velocity.dot)

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

This template isn't quite finished - I can make the HTML table a bit cleaner.
Unfortunately this is only pseudo-HTML and I need to find the example
I used in order to get the details.


## Velocity template and macros

The current implementation uses a single template with a single macro library
per output format. This made the individual macros, esp. for the nested formats,
a bit more complicated but everything now works. I will add documentation explaining
how each macro implements the designed behavior.

The dot format also has `src/main/resources/templates/legacy-dependency-tree.dot.vm`.

## Files

```shell
$ file *
with-velocity.dot: HTML document, ASCII text
with-velocity.gif: GIF image data, version 87a, 7687 x 1243
with-velocity.jpg: JPEG image data, JFIF standard 1.01, resolution (DPI), density 96x96, segment length 16, comment: "CREATOR: gd-jpeg v1.0 (using IJG JPEG v80), default quality", baseline, precision 8, 7687x1243, components 3
with-velocity.pdf: PDF document, version 1.7
with-velocity.png: PNG image data, 7687 x 1243, 8-bit/color RGBA, non-interlaced
with-velocity.ps:  PostScript document text conforming DSC level 3.0
```
