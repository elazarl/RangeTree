package com.github.elazarl.rangetree;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * tests for complex inner functions, and outer API for RangeTree
 */
public class RangeTreeTest {
    @BeforeClass
    static public void setUp() {
        CustomFormat formatter = new CustomFormat();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        for (Handler h : RangeTree.logger.getParent().getHandlers()) {
            RangeTree.logger.getParent().removeHandler(h);
        }
        RangeTree.logger.addHandler(consoleHandler);
    }
    @Test
    public void testXOnlyPointRange() {
        RangeTree rangeTree = new RangeTree(Point.makeArray(
                1,0, 7, 0, 19, 0, 200, 0, 3, 0, 15, 0
        ));
//        Graphviz.show(rangeTree.toDot());
        assertThat(Sets.newHashSet(rangeTree.pointsInRange(6, 20, 0, Double.MAX_VALUE)),
                equalTo(Sets.newHashSet(Point.makeArray(
                7, 0, 19, 0, 15, 0
        ))));
        assertThat(Sets.newHashSet(rangeTree.pointsInRange(1, 10, 0, Double.MAX_VALUE)),
                equalTo(Sets.newHashSet(Point.makeArray(
                        7, 0, 1, 0, 3, 0
        ))));
        rangeTree = new RangeTree(Point.makeArray(
                2, 19, 7, 10, 5, 80, 8, 37, 12, 3, 17, 62, 15, 99, 12, 49, 41, 95, 58, 59,
                93, 70, 33, 30, 52, 23, 67, 89
        ));
//        Graphviz.show(rangeTree.toDot());
    }

    @Test
    public void testPointRange() {
        RangeTree rangeTree = new RangeTree(Point.makeArray(
                1,0, 7,0, 19,0, 200,0, 3,0, 15,0
        ));
        assertThat(rangeTree.pointsInRange(0, 100, 1, 2),
                equalTo(Collections.<Point>emptyList()));
        assertThat(Sets.newHashSet(rangeTree.pointsInRange(0, 100, -1, 2)),
                equalTo(Sets.newHashSet(
                        Point.make(1, 0),
                        Point.make(7, 0),
                        Point.make(19, 0),
                        Point.make(3, 0),
                        Point.make(15, 0)
                )
        ));
        rangeTree = new RangeTree(Point.makeArray(
                1,9, 7,10, 19,3, 200,8, 3,7, 15,11
        ));
//        Graphviz.show(rangeTree.toDot());
        assertThat(Sets.newHashSet(rangeTree.pointsInRange(0, 100, 0, 10)),
                equalTo(Sets.newHashSet(
                        Point.make(1, 9),
                        Point.make(19, 3),
                        Point.make(3, 7)
                )
                ));
    }

    @Test
    public void testPointsOnEdge() {
        RangeTree rangeTree = new RangeTree(Point.makeArray(
                1,0, 1,7, 1,17, 1,20,
                2,20, 3,20, 7,20, 19,20,
                20,20, 20,3, 20,7, 20,0,
                2,0, 7,0, 8,0
        ));
//        Graphviz.show(rangeTree.toDot());
        assertThat(Sets.newHashSet(rangeTree.pointsInRange(1, 20, 0, 20)),
                equalTo(Sets.newHashSet(
                        Point.make(1, 0), Point.make(1, 7), Point.make(1, 17),
                        Point.make(2, 0), Point.make(7, 0), Point.make(8, 0)
                )));
    }

    @Test
    public void testPointRangeRandom() {
        Random gen = new Random(0xF434Ef100FL);
        for (int times=0; times< 100; times++) {
            final int rectSize = 1000;
            double  xmin = gen.nextDouble(), xsup = xmin+rectSize;
            double  ymin = gen.nextDouble(), ysup = ymin+rectSize;
            List<Point> inRect = Lists.newArrayList(), outRect = Lists.newArrayList();
            int numInRect = 100;
            int numOutRect = 1000;
            for (int i=0; i< numInRect; i++) {
                inRect.add(Point.make(xmin+gen.nextInt(rectSize), ymin+gen.nextInt(rectSize)));
            }
            for (int i=0; i< numOutRect; i++) {
                // x out, y in
                outRect.add(Point.make(xmin - 1 - gen.nextInt(rectSize / 2), ymin + gen.nextInt(rectSize)));
                // x in, y out
                outRect.add(Point.make(xmin+gen.nextInt(rectSize), ymin-1-gen.nextInt(rectSize/2)));
                // both out
                outRect.add(Point.make(xmin-1-gen.nextInt(rectSize*100),
                        ymin+rectSize+gen.nextInt(rectSize)));
            }
            RangeTree rangeTree = new RangeTree(
                    Iterables.toArray(Iterables.concat(inRect, outRect), Point.class));
    //        Graphviz.show(rangeTree.toDot());
            assertThat(Sets.newHashSet(rangeTree.pointsInRange(xmin, xsup, ymin, ysup)), equalTo(
                    Sets.newHashSet(inRect)
            ));
        }
    }

    @Test
    public void testSplitPoint() {
        RangeTree rangeTree = new RangeTree(Point.makeArray(
                1, 2,
                2, 3,
                3, 4,
                4, 5));
        assertThat(rangeTree.splitPoint(0, 5), equalTo(Point.make(2, 3)));
        assertThat(rangeTree.splitPoint(3, 3.1), equalTo(Point.make(3, 4)));
        assertThat(rangeTree.splitPoint(2.7, 3.1), equalTo(Point.make(3, 4)));
        assertThat(rangeTree.splitPoint(3.1, 3.2), nullValue());
        for (int i=1; i<5; i++) {
            assertThat(rangeTree.splitPoint(i-0.01d, i+0.01d), equalTo(Point.make(i, i + 1)));
        }
    }

    @Test
    public void testSimpleTree() throws Exception {
        // TODO(elazar): create a procedure that build a whole tree, and test everything
        RangeTree rangeTree = new RangeTree(Point.makeArray(
                1, 2,
                2, 3,
                3, 4,
                4, 5));
        assertThat(rangeTree.path("ll"), equalTo(
                (List) Lists.newArrayList(
                        Point.make(2, 3),
                        Point.make(1, 2), null)
        ));
        assertThat(rangeTree.path("lr"), equalTo(
                (List)Lists.newArrayList(
                        Point.make(2, 3),
                        Point.make(1, 2), null)
        ));
        assertThat(rangeTree.path("rl"), equalTo(
                (List)Lists.newArrayList(
                        Point.make(2, 3),
                        Point.make(3, 4), null)
        ));
        assertThat(rangeTree.path("rrl"), equalTo(
                (List)Lists.newArrayList(
                        Point.make(2, 3),
                        Point.make(3, 4),
                        Point.make(4, 5), null)
        ));

        rangeTree = new RangeTree(Point.makeArray(
                1, 2,
                2, 3,
                3, 4,
                4, 5,
                17, 100,
                19, 200,
                100, 100));

        assertThat(rangeTree.path("lll"), equalTo(
                (List)Lists.newArrayList(
                        Point.make(4, 5),
                        Point.make(2, 3),
                        Point.make(1, 2), null)
        ));
        assertThat(rangeTree.path("lrr"), equalTo(
                (List)Lists.newArrayList(
                        Point.make(4, 5),
                        Point.make(2, 3),
                        Point.make(3, 4), null)
        ));
    }
}
