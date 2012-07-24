package com.github.elazarl.rangetree;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

/**
 * Tests memory, runtime and build time of Range trees of various sizes.
 * Note that benchmarking JVM is an hard to master art, so this tests are providing crude measure
 * at best.
 * I really should provide stddev as well, though it's probably no more than 20%.
 *
 * Results, Intel i5, Ubuntu, no -server:
 * Building a 1M elements range tree takes ~6 seconds
 * Searching a 1M element with result of ~5k elements takes up to 5ms
 * (whoa, that's A LOT, can be improved a ton, probably)
 * Memory usage of 1M element tree is up to 400Mb
 * VERY rough measurements, but should be enough to prove that this is usable.
 */
public class RangeTreeBenchmark {

    Random gen = new Random(0xF213F09E);

    // note that generating the random points also takes time, could be 20%, but we want onl
    // a rough measurement
    public Point[] randomPoints(int length) {
        Point[] points = new Point[length];
        for (int i=0; i<points.length; i++) {
            points[i] = new Point(gen.nextDouble(), gen.nextDouble());
        }
        return points;
    }

    public void build1MTree() {
        RangeTree rangeTree = new RangeTree(randomPoints(1000*1000));
    }

    double times = 10;

    @Test
    public void buildTreeBenchmark() {
        // warmup phase
        for (int i=0; i<10; i++) build1MTree();
        long before = System.currentTimeMillis();
        for (int i=0; i<times; i++) build1MTree();
        System.out.println("10MTree: "+(System.currentTimeMillis()-before)/times);
    }

    // from SO answer: http://stackoverflow.com/a/5599842/55094
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    @Test
    public void buildTreeMemoryUsage() {
        long before = memoryUsage();
        RangeTree rangeTree = new RangeTree(randomPoints(1000*1000));
        System.out.println("memory usage: "+readableFileSize(memoryUsage()-before));
    }

    @Test
    public void searchInTree() {
        RangeTree rangeTree = new RangeTree(randomPoints(1000*1000));
        List<Point> out = Lists.newArrayList();
        // warm up
        for (int i=0; i<10; i++) {
            double xmin = gen.nextDouble(), xsup = xmin+gen.nextInt(10000);
            double ymin = gen.nextDouble(), ysup = ymin+gen.nextInt(10000);
            rangeTree.pointsInRange(out, xmin, xsup, ymin, ysup);
        }

        long before = System.currentTimeMillis();
        for (int i=0; i<times; i++) {
            double xmin = gen.nextDouble(), xsup = xmin+gen.nextInt(10000);
            double ymin = gen.nextDouble(), ysup = ymin+gen.nextInt(10000);
            rangeTree.pointsInRange(out, xmin, xsup, ymin, ysup);
            out.clear();
        }
        System.out.println("Search in 1M tree: "+(System.currentTimeMillis()-before)/times);
    }

    private long memoryUsage() {
        return Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
    }
}
