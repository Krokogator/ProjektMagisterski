package com.mvp.java.utils;

import com.mvp.java.model.knapsack.Knapsack;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class KnapsackReader {

    public static Knapsack load(String resourcePath) throws IOException {
        File file = ResourceUtils.getFile("classpath:" + resourcePath);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String info = bufferedReader.readLine();
        String[] infos = info.split(" ");
        int size = Integer.valueOf(infos[0]);
        int maxWeight = Integer.valueOf(infos[1]);

        int[] profit = new int[size];
        int[] weight = new int[size];

        for (int i = 0; i < size ; i++) {
            String[] values = bufferedReader.readLine().split(" ");
            profit[i] = Integer.valueOf(values[0]);
            weight[i] = Integer.valueOf(values[1]);
        }

        return new Knapsack(profit, weight, size, maxWeight);
    }
}
