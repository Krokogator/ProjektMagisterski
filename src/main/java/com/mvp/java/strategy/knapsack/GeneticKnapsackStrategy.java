package com.mvp.java.strategy.knapsack;

import com.mvp.java.strategy.IKnapsackStrategy;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticKnapsackStrategy implements IKnapsackStrategy {

    // Init params
    private int epochs;
    private int generationSize;
    private double mutationRate;

    // Run parameters
    private int[] profit;
    private int[] weight;
    private int maxWeight;

    // Temp
    private boolean[][] generation;
    private Random random;

    public GeneticKnapsackStrategy(int epochs, int generationSize, double mutationRate) {
        this.epochs = epochs;
        this.generationSize = generationSize;
        this.mutationRate= mutationRate;
        this.random = new Random();
    }

    @Override
    public boolean[] solve(int[] profit, int[] weight, int maxWeight) {
        this.generation = new boolean[generationSize][profit.length];
        for (int i = 0; i < generationSize; i++) {
            for (int j = 0; j < profit.length; j++) {
                this.generation[i][j] = random.nextBoolean();
            }
        }
        this.profit = profit;
        this.weight = weight;
        this.maxWeight = maxWeight;

        run();

        // return best (first in sorted array)
        return generation[0];
    }


    private void run() {
        this.generation = orderize(this.generation);

        for (int i = 0 ; i < epochs ; i++) {
            runEpoch();
        }
    }

    private void runEpoch() {
        boolean[][] childGeneration = crossover();
        childGeneration = mutateGeneration(childGeneration);

        // Put parent and child generation and sort them from best to worst
        boolean[][] bothGeneration = new boolean[generationSize*2][generation[0].length];
        for(int i = 0; i < generationSize; i++ ) {
            bothGeneration[i] = this.generation[i];
        }
        for(int i = generationSize; i < generationSize*2; i++) {
            bothGeneration[i] = childGeneration[i - generationSize];
        }

        bothGeneration = orderize(bothGeneration);

        // Get best of parent and children to new generation
        for (int i = 0; i < generationSize; i++) {
            this.generation[i] = bothGeneration[i];
//            System.out.println(i + " : " + evaluateFitness(this.generation[i]));
        }

    }

    private boolean[][] crossover() {
        int i = 0;
        boolean[][] childGeneration = new boolean[generationSize][generation[0].length];
        while (i < generationSize) {
            boolean[] parent1 = selectParent();
            boolean[] parent2 = selectParent();
//            while (Arrays.equals(parent1, parent2)){
//                parent2 = selectParent();
//            }

            Pair<boolean[], boolean[]> childs = singlePointCrossover(parent1, parent2);

            if (i < generationSize){
                childGeneration[i] = childs.getKey();
                i++;
            }
            if (i < generationSize){
                childGeneration[i] = childs.getValue();
                i++;
            }

        }
        return childGeneration;
    }

    private Pair<boolean[], boolean[]> singlePointCrossover(boolean[] parent1, boolean[] parent2) {
        int cutPoint = random.nextInt(parent1.length-2) + 1;

        boolean[] offspring1 = new boolean[parent1.length];
        boolean[] offspring2 = new boolean[parent1.length];

        for (int i = 0; i < parent1.length; i++) {
            if (i < cutPoint) {
                offspring1[i] = parent1[i];
                offspring2[i] = parent2[i];
            } else {
                offspring1[i] = parent2[i];
                offspring2[i] = parent1[i];
            }
        }
        return new Pair<>(offspring1, offspring2);
    }

    private boolean[][] mutateGeneration(boolean[][] generation) {
        int mutations = (int) Math.round(generationSize * mutationRate);

        while (mutations > 0) {
            int gene = random.nextInt(generation[0].length);
            generation[mutations][gene] = ! generation[mutations][gene];
            mutations--;
        }

        return generation;
    }

    private boolean[] mutate(boolean[] chromosome) {

        int mutableGene = random.nextInt(generation[0].length);

        chromosome[mutableGene] = ! chromosome[mutableGene];

        return chromosome;
    }




    private boolean[] selectParent() {
        double size = 0;
        double[] rouletteWheel = new double[generation.length];
        for (int i = 0; i< rouletteWheel.length; i++) {
            size += i;
        }
        double val = 1 / size;
        for (int i = 0; i < rouletteWheel.length; i++ ) {
            rouletteWheel[i] = val * (i+1);
        }

        double rand = random.nextDouble();
        double temp = 0;

        for (int i = 0; i < rouletteWheel.length; i++ ) {
            temp += rouletteWheel[i];
            if (rand < temp) {
                return generation[generationSize-i-1];
            }
        }

        return null;
    }

    private boolean[][] orderize(boolean[][] population) {
        List<boolean[]> list = Arrays.asList(population);
        list.sort((o1, o2) -> evaluateFitness(o2) - evaluateFitness(o1));

        boolean[][] sorted = new boolean[population.length][population[0].length];
        for (int i = 0; i < population.length; i++) {
            sorted[i] = list.get(i);
        }

        return sorted;
    }

    private int evaluateFitness(boolean[] knapsack) {
        int totalWeight = 0;
        int totalProfit = 0;
        int maxPossibleProfit = 0;

        for (int i=0; i<knapsack.length; i++) {
            if (knapsack[i]) {
                totalWeight += this.weight[i];
                totalProfit += this.profit[i];
            }
            maxPossibleProfit += this.profit[i];

        }

        // if maxWeight exceeded -> return profit - max possible profit
        if (totalWeight > maxWeight) {
            return totalProfit - maxPossibleProfit;
        }
            // else return profit
        else return totalProfit;
    }

    private int evaluateProfit(boolean[] knapsack) {
        int totalWeight = 0;
        int totalProfit = 0;

        for (int i=0; i<knapsack.length; i++) {
            if (knapsack[i]) {
                totalWeight += this.weight[i];
                totalProfit += this.profit[i];
            }
        }

        // if maxWeight exceeded -> return 0
        if (totalWeight > maxWeight) return 0;
        // else return profit
        else return totalProfit;
    }

}
