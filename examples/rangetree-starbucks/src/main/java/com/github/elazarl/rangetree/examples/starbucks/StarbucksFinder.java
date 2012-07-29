package com.github.elazarl.rangetree.examples.starbucks;

import au.com.bytecode.opencsv.CSVReader;
import com.github.elazarl.rangetree.LatLngRangeTree;
import com.github.elazarl.rangetree.Point;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provides;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Finds all starbucks near a certain point
 */
class StarbucksFinder {
    LatLngRangeTree tree;
    List<String[]> csv;
    // this is bad for performance, should inject that into the tree, but I'm out of time allocated
    // for this test
    Map<Point, String[]> coord2csv  = Maps.newHashMap();
    public StarbucksFinder() throws IOException {
        InputStream starbucks = getClass().getClassLoader().getResourceAsStream("USA-Starbucks.csv");
        CSVReader reader = new CSVReader(new InputStreamReader(starbucks));
        csv = reader.readAll();
        reader.close();
        csv = csv.subList(1, csv.size());
        Point[] points = new Point[csv.size()];
        for (int i=0; i<csv.size(); i++) {
            String[] row = csv.get(i);
            points[i] = Point.make(Double.parseDouble(row[1]), Double.parseDouble(row[0]));
            coord2csv.put(points[i], row);
        }
        tree = new LatLngRangeTree(points);
    }
}
