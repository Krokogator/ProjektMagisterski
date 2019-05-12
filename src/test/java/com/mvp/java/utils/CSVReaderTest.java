package com.mvp.java.utils;

import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.ServerException;

import static org.junit.jupiter.api.Assertions.*;

class CSVReaderTest {

    private CSVReader csvReader;

    @BeforeEach
    public void init(){
        this.csvReader = new CSVReader();
    }

    @Test
    void loadCSV() {

        try {
            CSVParser parser = csvReader.loadCSV("C:\\Users\\micha\\Downloads\\simplemaps_worldcities_basicv1.4\\newworld.csv");
            parser.getRecords().forEach(x -> x.forEach(System.out::println));
        } catch (IOException e) {
            System.out.println("File not found");
        }


    }
}