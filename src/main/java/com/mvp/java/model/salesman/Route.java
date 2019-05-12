package com.mvp.java.model.salesman;

import lombok.Data;

import java.util.List;

@Data
public class Route {

    private List<City> cities;

    public Route(List<City> cities) {
        this.cities = cities;
    }

    public double getLength(){
        double length = 0;

        if (cities.size() > 1){
            // distance between last and first
            int lastElementIndex = cities.size() - 1;
            length += cities.get(lastElementIndex).distance(cities.get(0));

            // distance between remaining cities
            for (int i = 1; i < cities.size(); i++) {
                length += cities.get(i-1).distance(cities.get(i));
            }
        }

        return length;
    }
}
