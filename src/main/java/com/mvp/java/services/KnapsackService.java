package com.mvp.java.services;

import com.mvp.java.strategy.IKnapsackStrategy;
import org.springframework.stereotype.Service;

@Service
public class KnapsackService {

    IKnapsackStrategy iKnapsackStrategy;

    public void solve(final int[] profit,
                      final int[] weight,
                      final int maxWeight) {
        boolean[] solution = iKnapsackStrategy.solve(profit, weight, maxWeight);
        printSolution(solution, profit, weight);
    }

    private void printSolution(boolean[] solution, int[] profit, int[] weight) {
        int totalProfit = 0;
        for(int i = 0; i < solution.length; i++){
            if(solution[i]) {
                totalProfit += profit[i];
                System.out.println(i + " - profit: " +  profit[i] + ", weight: " + weight[i]);
            }
        }
        System.out.println("Total profit: " + totalProfit);
    }

    public void setStrategy(IKnapsackStrategy strategy){
        this.iKnapsackStrategy = strategy;
    }
}