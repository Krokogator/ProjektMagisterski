package com.mvp.java.model.salesman;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Route {

    private List<City> cities = new ArrayList<>();

    public Route(List<City> cities) {
        this.cities = new ArrayList<>(cities);
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
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

    @Override
    public Route clone() {
        return new Route(cities);
    }
}
