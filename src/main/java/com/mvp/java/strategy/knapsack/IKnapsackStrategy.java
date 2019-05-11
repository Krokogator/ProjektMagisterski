package com.mvp.java.strategy.knapsack;

public interface IKnapsackStrategy {
    boolean[] solve(int[] profit, int[] weight, int maxWeight);
}
