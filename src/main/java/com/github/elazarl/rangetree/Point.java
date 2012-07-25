package com.github.elazarl.rangetree;

import com.google.common.base.Objects;

import java.text.DecimalFormat;

/**
 * Simple data class for a point in the plane
 */
public class Point {
    double x;
    double y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    static public Point make(double x, double y) {
        return new Point(x, y);
    }

    static public Point[] makeArray(double... points) {
        if (points.length%2!=0) throw new IllegalArgumentException("#numbers must be even");
        Point[] result = new Point[points.length/2];
        for (int i=0; i<points.length; i+=2) result[i/2] = make(points[i], points[i+1]);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }

    @Override
    public boolean equals(Object that) {
        if (this==that) return true;
        if (that==null) return false;
        if (that.getClass()!=this.getClass()) return false;
        Point other = (Point)that;
        return Objects.equal(this.x, other.x) && Objects.equal(this.y, other.y);
    }

    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }

    String toDotName() {
        DecimalFormat format = new DecimalFormat("#_##");
        return "p_"+format.format(x)+"_to_"+format.format(y);
    }
}
