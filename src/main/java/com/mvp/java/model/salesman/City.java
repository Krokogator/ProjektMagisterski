package com.mvp.java.model.salesman;

import org.apache.sis.distance.DistanceUtils;

public class City {

    private double x;
    private double y;
    private int id;


    public City(int id){
        this.id = id;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double distance(City city) {
        return DistanceUtils.getHaversineDistance(this.x, this.y, city.getX(), city.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getId() != ((City) obj).getId()) {
            return false;
        }

        return true;
    }
}
