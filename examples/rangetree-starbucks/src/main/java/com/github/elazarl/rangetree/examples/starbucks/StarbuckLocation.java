package com.github.elazarl.rangetree.examples.starbucks;

/**
 * Simple data class for API to answer question about starbucks location
 */
public class StarbuckLocation {
    public double lat;
    public double lng;
    public String city;
    public String address;
    public StarbuckLocation(double lat, double lng, String city, String address) {
        this.lat     = lat;
        this.lng     = lng;
        this.city    = city;
        this.address = address;
    }
}
