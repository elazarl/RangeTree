RangeTree
=========

An implementation of Range Tree with fractional cascading.
See Computational Geometry by Berg et al.

See a visualization for example. The range tree is an binary tree ordered
by the X coordinates, every node has an array of all points below it ordered
by the Y coordinate. This array pictured as a long rectangle, divided into
cells. Each cell contains a point in the plane (the topmost part), and two
indices (the two bottom squares).
