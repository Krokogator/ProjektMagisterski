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

        Knapsack knapsack = null;
        try {
            knapsack = KnapsackReader.load("large_scale/knapPI_1_1000_1000_1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // NEW

        int[] profit2 = knapsack.getProfit();
        int[] weight2 = knapsack.getWeight();
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
        knapsackService.setStrategy(new GeneticKnapsackStrategy(1000, 500, 1));
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