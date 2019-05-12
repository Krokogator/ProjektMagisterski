package com.mvp.java.strategy.salesman;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.strategy.ISalesmanStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class GeneticStrategy implements ISalesmanStrategy {

    private int population;
    private int epochs;

    private Route route;
    private List<Route> generation = new ArrayList<>();

    public GeneticStrategy(int population, int epochs) {
        this.population = population;
        this.epochs = epochs;
    }

    @Override
    public Route solve(Route route) {
        this.route = route;
        initGeneration();
        runEpoch();
        return null;
    }

    private void initGeneration(){
        IntStream.range(0, population).forEachOrdered(n -> {
            List<City> newCities = new ArrayList<>(route.getCities());
            Collections.shuffle(newCities);
            generation.add(new Route(newCities));
        });
    }

    private void runEpoch(){
        evaluateGeneration();
        crossover();
    }

    private void evaluateGeneration() {
        generation.sort((o1, o2) -> (int) Math.round(o1.getLength() - o2.getLength()));
        generation.forEach(x -> System.out.println(x.getLength()));
    }

    private void crossover() {

        
    }
}