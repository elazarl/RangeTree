package com.github.elazarl.rangetree.examples.starbucks;

import com.github.elazarl.rangetree.Point;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import javax.ws.rs.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * accepts requests of the form of long/lat, and returns list of starbucks in 10km
 */
@Path("starbucks")
public class StarbucksNear {
    @Inject
    StarbucksFinder finder;
    // standard unit is meters
    private static final int KILOMETER = 1000;

    @GET
    @Path("get/{id}")
    public String get(@PathParam("id") int id) {
        return Arrays.toString(finder.csv.get(id));
    }

    @GET
    @Path("near")
    @Produces("application/json")
    public List<StarbuckLocation> root(@QueryParam("lat") double lat, @QueryParam("lng") double lng) {
        List<Point> starbucks = finder.tree.coordInRadius(lat, lng, 5 * KILOMETER);
        List<StarbuckLocation> locations = Lists.newArrayList();
        for (Point coord : starbucks) {
            String[] row = finder.coord2csv.get(coord);
            if (row==null) Logger.getAnonymousLogger().info("Illegal starbucks: "+coord);
            locations.add(new StarbuckLocation(coord.x, coord.y, row[2], row[3]));
        }
        return locations;
    }
}
