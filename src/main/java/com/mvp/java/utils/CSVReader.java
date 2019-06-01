package com.mvp.java.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class CSVReader {

    public static List<CSVRecord> loadCSV(String path) throws IOException {
        File file = new File(path);
        file = ResourceUtils.getFile("classpath:cities/usaSmall2.csv");
        return CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT).getRecords();
    }

    public static List<CSVRecord> loadCSV(File file) throws IOException {
        return CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT).getRecords();
    }
}
