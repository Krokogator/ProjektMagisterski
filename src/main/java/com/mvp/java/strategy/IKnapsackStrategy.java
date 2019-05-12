package com.mvp.java.strategy;

public interface IKnapsackStrategy {
    boolean[] solve(int[] profit, int[] weight, int maxWeight);
}
