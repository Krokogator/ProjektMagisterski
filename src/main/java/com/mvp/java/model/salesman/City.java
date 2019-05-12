package com.mvp.java.model.salesman;

import org.apache.sis.distance.DistanceUtils;

public class City {

    private double x;
    private double y;

    public City(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distance(City city) {
        return DistanceUtils.getHaversineDistance(this.x, this.y, city.getX(), city.getY());
    }

}
