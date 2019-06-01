package com.mvp.java.services;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.utils.CSVReader;
import lombok.Data;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class SalesmanService {

    private Route route;

    public void init() throws IOException {

        List<CSVRecord> records = CSVReader.loadCSV("C:\\Users\\micha\\Downloads\\simplemaps_worldcities_basicv1.4\\brd14501.csv");
        this.route = mapToRoute(records);
    }

    public Route getRoute(File file) throws IOException {
        List<CSVRecord> records = CSVReader.loadCSV(file);
        return mapToRoute(records);
    }

    private Route mapToRoute(List<CSVRecord> records){
        List<City> cities = records.stream()
                .map(record -> new City(Double.valueOf(record.get(0)), Double.valueOf(record.get(1))))
                .collect(Collectors.toList());

        for(int i = 0; i < cities.size(); i++) {
            cities.get(i).setId(i);
        }

        return new Route(cities);
    }

}
