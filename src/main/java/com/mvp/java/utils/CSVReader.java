package com.mvp.java.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class CSVReader {

    public static CSVParser loadCSV(String path) throws IOException {
        File file = new File(path);
        file = ResourceUtils.getFile("classpath:cities/usa.csv");
        return CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
    }
}
