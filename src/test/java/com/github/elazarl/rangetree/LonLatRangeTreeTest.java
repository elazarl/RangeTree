package com.github.elazarl.rangetree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * This class is not nearly tested enough, but it's a start.
 */
public class LonLatRangeTreeTest {
    @Test
    public void testLatLong() {
        // verified with google maps ruler distance between two points is ~450 meter,
        LatLngRangeTree tree = new LatLngRangeTree(
                Point.make(
                        37.461131, -122.160029
                )
        );
        List<Point> result = Lists.newArrayList(Point.make(37.461131, -122.160029));
        List<Point> empty = Collections.emptyList();
        assertThat(tree.coordNear(37.462707, -122.153292, 600), equalTo(result));
        assertThat(tree.coordNear(37.462707, -122.153292, 300), equalTo(empty));
    }

    @Test
    public void testAcros180Merdith() {
        LatLngRangeTree tree = new LatLngRangeTree(
                Point.makeArray(
                        51, 119,
                        55.677584, 118.652344,
                        54.927142, -132.099609,
                        57.183902,-120
                )
        );
        assertThat(Sets.newHashSet(tree.coordInRange(42.940339, 60, 116, -130)),
                equalTo(Sets.newHashSet(Point.makeArray(
                        51, 119,
                        55.677584, 118.652344,
                        54.927142, -132.099609)
        )));
    }
}
