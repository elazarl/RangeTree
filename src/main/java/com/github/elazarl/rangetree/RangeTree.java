package com.github.elazarl.rangetree;


import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A range tree, given a set of 2D points, uses O(nlgn) memory, and can answer in O(lgn+k) time which points lies in
 * a certain rectangle.
 */
public class RangeTree {
    Point[] points; // TODO: using two arrays for xs and ys is more effecient
    static public Logger logger = Logger.getLogger(RangeTree.class.getName());

    static {
        logger.setLevel(Level.WARNING);
    }

    String fractionTraction(Fraction frac) {
        List<String> out = Lists.newArrayList();
        for (int i=0; i<frac.points.length; i++) {
            out.add("{<FONT COLOR=\"red\">"+i+"</FONT>:"+frac.points[i]+"|{"+frac.left[i]+"|"+frac.right[i]+"}}");
        }
        return Joiner.on("|").join(out);
    }

    void toDot(StringBuilder builder, Node node) {
        Node lc = new Node(node).lc();
        Node rc = new Node(node).rc();
        String dotname = node.get().toDotName();
        builder.append(dotname).
            append(String.format("[label=\"%.2f,%.2f\"];\n", node.get().x, node.get().y));
        builder.append("frac_"+dotname).
                append("[fontsize=10,shape=\"record\",label=<").
                append(fractionTraction(fractions[node.index()])).append(">];\n").
                append(dotname).append(" -> ").
                append("frac_"+dotname).append(";\n");
        if (lc!=null) {
            builder.append("frac_"+dotname).
                append(" -> ").append(lc.get().toDotName()).
                append("[color=\"red\",arrowhead=\"vee\"]").
                append(";\n");
            toDot(builder, lc);
        }
        if (rc!=null) {
            builder.append("frac_"+dotname).append(" -> ").append(rc.get().toDotName()).
                append(";\n");
            toDot(builder, rc);
        }
    }

    String toDot() {
        StringBuilder builder = new StringBuilder("digraph {\n");
        toDot(builder, root());
        builder.append("}\n");
        return builder.toString();
    }

    static final Comparator<Point> compareXCoord = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return ComparisonChain.start().
                    compare(o1.x, o2.x).
                    compare(o1.y, o2.y).
                    result();
        }
    };

    static final Comparator<Point> compareYCoord = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return ComparisonChain.start().
                    compare(o1.y, o2.y).
                    compare(o1.x, o2.x).result();
        }
    };

    public RangeTree(Point... points) {
        this.points = points;
        Arrays.sort(points, compareXCoord);
        fractions = new Fraction[points.length];
        buildFractions();
    }

    private void buildFractions() {
        Node root = root();
        fractions[root.index()] = new Fraction(Arrays.copyOf(points, points.length));
        buildFractions(root);
    }

    private void buildFractions(Node node) {
        // invariant: node already has his fraction filled
        int index = node.index(), from = node.from, to = node.to;
        if (node.lc()!=null) {
            fractions[node.index()] = new Fraction(Arrays.copyOfRange(points,
                    node.from, node.to));
            linkFraction(fractions[index],fractions[index].left, fractions[node.index()]);
            buildFractions(node);
        } else Arrays.fill(fractions[index].left, -1);
        node.become(from, to);
        if (node.rc()!=null) {
            fractions[node.index()] = new Fraction(Arrays.copyOfRange(points,
                    node.from, node.to));
            linkFraction(fractions[index],fractions[index].right, fractions[node.index()]);
            buildFractions(node);
        }  else Arrays.fill(fractions[index].right, -1);
    }

    private void linkFraction(Fraction parent, int[] parentLinks, Fraction child) {
        int p = 0, c = 0;
        while (p<parentLinks.length && c<child.points.length) {
            if (child.points[c].y >= parent.points[p].y) {
                parentLinks[p] = c;
                p++;
            }
            else c++;
        }
        for (; p<parentLinks.length; p++) parentLinks[p] = -1;
    }

    Fraction[] fractions;

    static class Fraction {
        Point[] points;
        int[] right;
        int[] left;

        Fraction(Point[] points) {
            this.points = points;
            Arrays.sort(points, compareYCoord);
            right = new int[points.length];
            left  = new int[points.length];
        }
    }

    class Node {
        int from, to;
        Node(Node node) {
            from = node.from;
            to   = node.to;
        }
        Node(int from, int to) {
            this.from = from;
            this.to   = to;
        }
        Node rc() {
            if (to-from==1) return null;
            return become(index()+1, to);
        }
        Node lc() {
            if (to-from==1) return null;
            return become(from, index());
        }
        int index() {
            return from+(to-from+1)/2-1;
        }
        Point get() {
            return points[index()];
        }
        Node become(int from, int to) {
            if (from==to) return null;
            this.from = from;
            this.to = to;
            return this;
        }
        @Override
        public String toString() {
            return "["+from+","+to+")["+index()+"]="+get();
        }
    }

    private Node root() {
        return make(0, points.length);
    }

    private Node make(int from, int to) {
        if (from==to) return null;
        return new Node(from, to);
    }

    private Node splitNode(double start, double end) {
        return splitNode(root(), start, end);
    }

    private Node splitNode(Node node, double start, double end) {
        if (node == null) return node;
        Point point = node.get();
        if (point.x>=end) return splitNode(node.lc(), start, end);
        else if (point.x<start) return splitNode(node.rc(), start, end);
        return node;
    }

    public Point splitPoint(double start, double end) {
        Node node = splitNode(start, end);
        return node!=null ? node.get() : null;
    }

    private void getAll(List<Point> out, Node node, int ymin_ix, double ysup) {
//        logger.info("all " + node);
        if (node==null || ymin_ix==-1) return;
        Point[] toadd = fractions[node.index()].points;
        for (int i=ymin_ix; i<toadd.length && toadd[i].y<ysup; i++) {
            out.add(toadd[i]);
        }
    }

    private void getSmaller(List<Point> out, Node node, double val,
                            int ymin_ix,
                            double ymin, double ysup) {
//        logger.info("Smaller " + node);
        if (node==null || ymin_ix==-1) return;
        Fraction frac = fractions[node.index()];
        Point point = node.get();
        if (point.x<val) {
            int from = node.from, to = node.to;
            if (point.y<ysup && point.y>=ymin) out.add(point);
            getSmaller(out, node.rc(), val, frac.right[ymin_ix], ymin, ysup);
            node.become(from, to);
            getAll(out, node.lc(), frac.left[ymin_ix], ysup);
        }
        else {
            getSmaller(out, node.lc(), val, frac.left[ymin_ix], ymin, ysup);
        }
    }

    private void getLargerEqual(List<Point> out, Node node, double val,
                                int ymin_ix,
                                double ymin, double ysup) {
//        logger.info("Greater or equal " + node);
        if (node==null || ymin_ix==-1) return;
        Fraction frac = fractions[node.index()];
        Point point = node.get();
        if (point.x>=val) {
            int from = node.from, to = node.to;
            if (point.y<ysup && point.y>=ymin) out.add(point);
            getLargerEqual(out, node.lc(), val, frac.left[ymin_ix], ymin, ysup);
            node.become(from, to);
            getAll(out, node.rc(), frac.right[ymin_ix], ysup);
        }
        else {
            getLargerEqual(out, node.rc(), val, frac.right[ymin_ix],
                    ymin, ysup);
        }
    }

    public void pointsInRange(List<Point> out, double xmin, double xsup,
                              double ymin, double ysup) {
//        logger.info("Find " + xmin + ":" + xsup + " " + ymin + ":" + ysup);
        Node node = splitNode(xmin, xsup);
        if (node==null) return;
        Fraction frac = fractions[node.index()];
        Point[] ypoints = fractions[node.index()].points;
        int ymin_ix = Arrays.binarySearch(ypoints, Point.make(Double.NEGATIVE_INFINITY, ymin),
                compareYCoord);
        if (ymin_ix<0) ymin_ix = -1*ymin_ix-1;
//        logger.info("Split node: " + node + " ix "+ymin_ix);
        if (ymin_ix==ypoints.length) return;
        Point point = node.get();
        if (point.y<ysup && point.y>=ymin) out.add(point);
        Node lc = new Node(node).lc();
        Node rc = new Node(node).rc();
        getSmaller(out, rc, xsup, frac.right[ymin_ix], ymin, ysup);
        getLargerEqual(out, lc, xmin, frac.left[ymin_ix], ymin, ysup);
    }

    public List<Point> pointsInRange(double xmin, double xsup, double ymin, double ysup) {
        List<Point> out = Lists.newArrayList();
        pointsInRange(out, xmin, xsup, ymin, ysup);
        return out;
    }

    /**
     * Debug only procedure to make sure a certain path in the tree is correct
     * @param dirs a string of directions, left or right
     * @return a list of points in the path
     */
    public List<Point> path(String dirs) {
        Node node = root();
        List<Point> result = Lists.newArrayList();
        result.add(points[node.index()]);
        for (char d : dirs.toCharArray()) {
            if (node==null) continue;
            if (d=='l') node = node.lc();
            else node = node.rc();
            result.add(node!=null ? points[node.index()] : null);
        }
        return result;
    }
}
