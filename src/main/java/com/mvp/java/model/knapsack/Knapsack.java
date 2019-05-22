package com.mvp.java.model.knapsack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Knapsack {

    private int[] profit;
    private int[] weight;
    private int size;
    private int maxWeight;
}
