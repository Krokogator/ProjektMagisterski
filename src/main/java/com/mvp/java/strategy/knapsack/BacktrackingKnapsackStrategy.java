package com.mvp.java.strategy.knapsack;

import com.mvp.java.strategy.IKnapsackStrategy;

public class BacktrackingKnapsackStrategy implements IKnapsackStrategy {

    int[] p;
    int[] w;
    int maxWeight;

    int maxProfit;
    int numbest;
    boolean[] bestset;
    boolean[] include;
    int n;

    @Override
    public boolean[] solve(int[] profit, int[] weight, int maxWeight) {
        this.p = profit;
        this.w = weight;
        this.maxWeight = maxWeight;
        this.n = profit.length - 1;

        this.maxProfit = 0;

        include = new boolean[n+1];
        bestset = new boolean[n+1];

        knapsack(-1,0,0);

        for(int i = 0; i <= n ; i++){
            System.out.println(i + " : " + bestset[i]);
        }

        return bestset;
    }

    void knapsack(int i, int profit, int weight){
        if ( weight <= maxWeight && profit > maxProfit) {
            maxProfit = profit;
            bestset = include.clone();
        }

        if(promising(i, profit, weight)){
            include[i+1] = true;
            knapsack(i+1, profit + p[i+1], weight + w[i+1]);
            include[i+1] = false;
            knapsack(i+1, profit, weight);
        }
    }

    private boolean promising(int i, int profit, int weight) {
        int j, k;
        int totweight;
        float bound;

        if(weight >= maxWeight){
            return false;
        } else {
            j = i+1;
            bound = profit;
            totweight = weight;
            while(j <= n && totweight + w[j] <= maxWeight){
                totweight = totweight+w[j];
                bound = bound+p[j];
                j++;
            }
            k = j;
            if(k <= n){
                bound = bound + (float)(maxWeight - totweight) * (float)p[k] / (float)w[k];
            }

            return bound > maxProfit;
        }
    }

}
