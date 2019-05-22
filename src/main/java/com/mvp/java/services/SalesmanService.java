package com.mvp.java.services;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.utils.CSVReader;
import lombok.Data;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class SalesmanService {

    private Route route;

    public void init() throws IOException {

        CSVParser parser = CSVReader.loadCSV("C:\\Users\\micha\\Downloads\\simplemaps_worldcities_basicv1.4\\brd14501.csv");

        List<City> cities = parser.getRecords().stream()
            .map(record -> new City(Double.valueOf(record.get(0)), Double.valueOf(record.get(1))))
            .collect(Collectors.toList());

//        cities.forEach(x -> System.out.println(x.getX() + " : " + x.getY()));

        for(int i = 0; i < cities.size(); i++) {
            cities.get(i).setId(i);
        }

        this.route = new Route(cities);
//        System.out.println(route.getLength());
    }

}
