package com.github.elazarl.rangetree;

import com.google.common.collect.Lists;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import java.util.Iterator;
import java.util.List;

/**
 * A wrapper for range tree, that converts a range query, to a spherical (for earth lat-long)
 * range query
 */
public class LatLngRangeTree {
    RangeTree rangeTree;
    private static final double MAX_LNG = 180d;
    private static final double MIN_LNG = -180d;
    private static final double MAX_LAT = 90d;
    private static final double MIN_LAT = -90d;

    public LatLngRangeTree(RangeTree tree) {
        rangeTree = tree;
    }

    public LatLngRangeTree(Point... points) {
        rangeTree = new RangeTree(points);
    }

    public List<Point> coordInRange(double latmin, double latsup, double lngmin, double lngsup) {
        List<Point> out = Lists.newArrayList();
        coordInRange(out, latmin, latsup, lngmin, lngsup);
        return out;
    }

    public void coordInRange(List<Point> out, double latmin, double latsup,
                              double lngmin, double lngsup) {
        // is merdian 180 in range?
        if (lngmin > lngsup) {
            coordInRange(out, latmin, latsup, lngmin, MAX_LNG);
            coordInRange(out, latmin, latsup, MIN_LNG, lngsup);
        // is a pole in range?
        } else if (latmin > latsup) {
            coordInRange(out, latmin, MAX_LAT, lngmin, lngsup);
            coordInRange(out, MIN_LAT, latsup, lngmin, lngsup);
        } else {
            rangeTree.pointsInRange(out, latmin, latsup, lngmin, lngsup);
        }
    }

    public List<Point> coordInRect(double lat, double lng, double meters) {
        GeoLocation[] locs = GeoLocation.fromDegrees(lat, lng).
                boundingEarthCoordinatesInMeters(meters);
        return coordInRange(locs[0].getLatitudeInDegrees(), locs[1].getLatitudeInDegrees(),
                locs[0].getLongitudeInDegrees(), locs[1].getLongitudeInDegrees());
    }

    public List<Point> coordInRadius(double lat, double lng, double meters) {
        List<Point> points = coordInRect(lat, lng, meters);
        LatLng center = new LatLng(lat, lng), point = new LatLng(0, 0);
        for (Iterator<Point> it = points.iterator(); it.hasNext();) {
            Point p = it.next();
            point.setLatitudeLongitude(p.x, p.y);
            if (LatLngTool.distance(center, point, LengthUnit.METER)>meters) it.remove();
        }
        return points;
    }
}
