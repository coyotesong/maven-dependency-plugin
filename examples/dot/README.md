# Using dot files

## Requirements
 
The `dot` program is usually provided by the `graphviz` package. It may be available as a
standadlone package since `graphviz` is a very large package and few people need more than
a handful of its applications.

## Usage

To convert a dotfile to a specific format use

```shell
$ shell -Tfmt filename.dot
```

where `fmt` is one of following.

The most commonly used image formats:

- gif
- jpeg | jpg
- png
- svg
- svgz

Note: the `svg|svgz` format is dynamic - it allows the user to view tooltips and possibly more.

The most commonly used documentation formats:

- eps (encapsulated postscript)
- fig (xfig)
- pdf
- ps  (postscript)

The remaining formats:

- canon
- cmap
- cmapx
- cmapx_np
- dot
- dot_json
- gd
- gd2
- gv
- imap
- imap_np
- ismap
- jpe
- json0
- mp
- pic
- plain
- plain-ext
- pov
- ps2
- tk
- vdx
- vml
- vmlz
- vrml
- wbmp
- webp
- x11
- xdot
- xdot1.2
- xdot1.4
- xdot_json
- xlib

