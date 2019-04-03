package com.mvp.java.services;

import com.mvp.java.strategy.BacktrackingKnapsackStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackServiceTest {

    KnapsackService knapsackService;

    @BeforeEach
    void init(){
        knapsackService = new KnapsackService();
    }

    @Test
    void dynamicKnapsack() {
        Random r = new Random();
        int W = 70;
        int n = 50;
        int wt[] = r.ints(5, 20).limit(n).toArray();
        int val[] = r.ints(5, 20).limit(n).toArray();

        System.out.println(Arrays.toString(wt));
        System.out.println(Arrays.toString(val));
        //int result = knapsackService.knapSack(W, wt, val);

        //System.out.println(result);
    }

    // Algorytm z powrotami
    @Test
    void backtrackingKnapsack() {
        int[] weight = {0, 2, 5, 10, 5};
        int[] profit = {0, 40, 30, 50, 10};


        knapsackService.setStrategy(new BacktrackingKnapsackStrategy());
        knapsackService.solve(profit, weight, 16);
    }
}