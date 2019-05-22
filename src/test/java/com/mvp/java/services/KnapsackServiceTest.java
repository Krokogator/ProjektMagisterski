package com.mvp.java.services;

import com.mvp.java.model.knapsack.Knapsack;
import com.mvp.java.strategy.knapsack.BacktrackingKnapsackStrategy;
import com.mvp.java.strategy.knapsack.GeneticKnapsackStrategy;
import com.mvp.java.utils.KnapsackReader;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class KnapsackServiceTest {

    KnapsackService knapsackService;

    private int[] weight;
    private int[] profit;
    private int maxWeight;

    @BeforeEach
    void init(){
        knapsackService = new KnapsackService();

        this.maxWeight = 6404180;

        int[] weight2 = {
                382745,
                799601,
                909247,
                729069,
                467902,
                44328,
                34610,
                698150,
                823460,
                903959,
                853665,
                551830,
                610856,
                670702,
                488960,
                951111,
                323046,
                446298,
                931161,
                31385,
                496951,
                264724,
                224916,
                169684
        };

        int[] profit2 = {
                825594,
                1677009,
                1676628,
                1523970,
                943972,
                97426,
                69666,
                1296457,
                1679693,
                1902996,
                1844992,
                1049289,
                1252836,
                1319836,
                953277,
                2067538,
                675367,
                853655,
                1826027,
                65731,
                901489,
                577243,
                466257,
                369261
        };

        Knapsack knapsack = null;
        try {
            knapsack = KnapsackReader.load("large_scale/knapPI_3_10000_1000_1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // NEW

        profit2 = knapsack.getProfit();
        weight2 = knapsack.getWeight();
        this.maxWeight = knapsack.getMaxWeight();

        // NEW

        List<Pair<Integer, Integer>> map = new ArrayList<>();
        for(int i = 0; i< profit2.length; i++){
            map.add(new Pair(weight2[i], profit2[i]));
        }

        map.sort(Comparator.comparingDouble(o -> new Double(o.getValue()) / new Double(o.getKey())));
        Collections.reverse(map);

        for (Pair<Integer, Integer> p: map) {
            System.out.println("Value: "+ new Double(p.getValue()) / new Double(p.getKey()) +" : "+p.getKey() +" : "+p.getValue());
        }





        this.profit = map.stream().mapToInt(x -> x.getValue()).toArray();
        this.weight = map.stream().mapToInt(x -> x.getKey()).toArray();
    }

    // Algorytm z powrotami
    @Test
    void backtrackingKnapsack() {

        knapsackService.setStrategy(new BacktrackingKnapsackStrategy());
        boolean[] result = knapsackService.solve(profit, weight, maxWeight);

//        boolean[] result = knapsackService.solve(profit, weight, 16);

        printResult(result);

    }


    @Test
    public void geneticKnapsack() {
        knapsackService.setStrategy(new GeneticKnapsackStrategy(200, 500, 0.9));
        boolean[] result = knapsackService.solve(profit, weight, maxWeight);

        printResult(result);
    }

    private void printResult(boolean[] result) {
        int totalProfit = 0;
        int capacity = 0;

        for (int i = 0; i < result.length ; i++ ){
            if (result[i]) {
//                System.out.println(profit[i]);
                totalProfit+= profit[i];
                capacity+= weight[i];
            }
        }

        System.out.println(totalProfit);
        System.out.println(capacity);
        System.out.println(this.maxWeight);
    }
}