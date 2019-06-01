package com.mvp.java.strategy.salesman;

import com.mvp.java.controllers.SalesmanTabController;
import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.strategy.ISalesmanStrategy;
import javafx.application.Platform;
import javafx.util.Pair;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeneticSalesmanStrategy implements ISalesmanStrategy {

    private int population;
    private int epochs;
    private double crossoverRate;
    private double mutationRate;

    private Route route;
    private List<Route> generation = new ArrayList<>();

    private Random random;


    // temp values
    private int currentEpoch;
    private boolean stopConditionNotMet;

    private SalesmanTabController salesmanTabController;

    public GeneticSalesmanStrategy(int population, int epochs, double crossoverRate, double mutationRate, SalesmanTabController salesmanTabController) {
        this.population = population;
        this.epochs = epochs;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.random = new SecureRandom();
        this.salesmanTabController = salesmanTabController;
    }

    @Override
    public Route solve(Route route) {
        this.route = route;
        initGeneration();
        generation.set(0, route);
        run();
        return generation.get(0);
    }

    private void initGeneration(){
        IntStream.range(0, population).forEachOrdered(n -> {
            List<City> newCities = new ArrayList<>(route.getCities());
            Collections.shuffle(newCities);
            generation.add(new Route(newCities));
        });
    }

    private void run(){
        stopConditionNotMet = true;
        evaluateGeneration();
        while(stopConditionNotMet) {
            runEpoch();
            updateStopCondition();

            Route best = generation.get(0).clone();
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    salesmanTabController.onDataReceived(best);
//                    salesmanTabController.draw(toDraw);
                }
            });
        }
    }

    private void updateStopCondition() {
        if (currentEpoch < epochs) {
            currentEpoch++;
            return;
        }
        this.stopConditionNotMet = false;
    }

    private void runEpoch(){

        List<Route> childGeneration = crossover();

        System.out.println("Generation: " + currentEpoch);
//        System.out.println("BEFORE MUTATE");
//        childGeneration.forEach(x -> System.out.println(x.getLength()));

        childGeneration = mutate(childGeneration);

//        System.out.println("AFTER MUTATE");
//        childGeneration.forEach(x -> System.out.println(x.getLength()));


        this.generation.addAll(childGeneration);
        evaluateGeneration();
        this.generation = generation.subList(0, population);
        System.out.println("BEST: " + generation.get(0).getLength());
//        this.generation.forEach(x -> System.out.println(x.getLength()));
    }

    // Sorts from best to worst
    private void evaluateGeneration() {
        generation.sort(Comparator.comparingDouble(Route::getLength));

    }

    // Implementation of CX2 (Cycle Crossover Operator - Optimised)
    private List<Route> crossover() {


        List<Route> newGeneration = new ArrayList<>();

        while (newGeneration.size() < generation.size()) {
            // Step 1 - choose two parents for mating
            Route parent1 = selectParent();
            Route parent2 = selectParent();

            Pair<Route, Route> offspringPair = crossoverPM(parent1, parent2);
            newGeneration.add(offspringPair.getValue());
            newGeneration.add(offspringPair.getKey());
        }

        if (newGeneration.size() > generation.size()) {
            newGeneration.remove(newGeneration.size() - 1);
        }

        return newGeneration;
    }

    public Pair<Route, Route> crossoverCX2(Route parentRoute1, Route parentRoute2) {
        List<City> offspring1 = new ArrayList<>();
        List<City> offspring2 = new ArrayList<>();

        List<City> parent1 = parentRoute1.getCities();
        List<City> parent2 = parentRoute2.getCities();

        // Step 2 - first bit from 2nd parent as 1st bit offspring
        City step2City = parent2.get(0);
        offspring1.add(step2City);

        City step3City = new City(0, 0);
        step3City.setId(0);

        City firstCityfirstParent = parent1.get(0);

        // Step 6 (while other cycles are left)
        while (offspring2.size() < parent1.size()) {
            // Step 5
            // while 1st bit of first parent will not come in second offspring (complete cycle) repeat steps 3 and 4
            while (true){
                // Step 3

                City temp = parent2.get(parent1.indexOf(step2City));
//                int index = parent2.indexOf(parent1.stream().filter(city -> city.getId() == temp.getId()).findFirst().get());
                step3City = parent2.get(parent1.indexOf(temp));
                offspring2.add(step3City);

                if (firstCityfirstParent.equals(step3City)) {
                    break;
                }

                // Step 4
                step2City = parent2.get(parent1.indexOf(step3City));
                offspring1.add(step2City);
            }

            // find first city that still exists in parent
            for (int i = 0; i < parent2.size() ; i++ ) {
                if (offspring1.indexOf(parent2.get(i)) == -1) {
                    step2City = parent2.get(i);
                    firstCityfirstParent = parent1.get(i);
                    offspring1.add(step2City);
                    break;
                }
            }
        }

        return new Pair(new Route(offspring1), new Route(offspring2));
    }

    public Pair<Route, Route> crossoverPM(Route parentRoute1, Route parentRoute2) {
        List<City> offspring1 = IntStream.range(0, parentRoute1.getCities().size()).mapToObj(x -> new City(-2)).collect(Collectors.toList());
        List<City> offspring2 = IntStream.range(0, parentRoute1.getCities().size()).mapToObj(x -> new City(-2)).collect(Collectors.toList());



        List<City> parent1 = parentRoute1.getCities();
        List<City> parent2 = parentRoute2.getCities();

        int mappingSize = parent1.size() - (int) Math.round(parent1.size() * this.crossoverRate);

        int firstCutPoint = random.nextInt(parent1.size() - mappingSize);

        // mapping
        for (int i = firstCutPoint; i < firstCutPoint + mappingSize; i++) {
            offspring1.set(i, parent2.get(i));
            offspring2.set(i, parent1.get(i));
        }

        // fill if possible before cut point
        for (int i = 0; i < firstCutPoint; i++ ) {
            if (!offspring1.contains(parent1.get(i))) {
                offspring1.set(i, parent1.get(i));
            }
            if (!offspring2.contains(parent2.get(i))) {
                offspring2.set(i, parent2.get(i));
            }
        }

        //fill if possible after cut point
        for (int i = firstCutPoint + mappingSize; i < parent1.size(); i++ ) {
            if (!offspring1.contains(parent1.get(i))) {
                offspring1.set(i, parent1.get(i));
            }
            if (!offspring2.contains(parent2.get(i))) {
                offspring2.set(i, parent2.get(i));
            }
        }

        for (int i = 0; i < parent1.size(); i++ ) {
            if (offspring1.get(i).getId() == -2) {
                offspring1.set(i, findMap(parent1.get(i), offspring1, offspring2));
            }
            if (offspring2.get(i).getId() == -2) {
                offspring2.set(i, findMap(parent2.get(i), offspring2, offspring1));
            }
        }


        return new Pair<>(new Route(offspring1), new Route(offspring2));
    }

    private City findMap(City city, List<City> offspring1, List<City> offspring2) {
        int index = offspring1.indexOf(city);
        City potential = offspring2.get(index);
        if (offspring1.contains(potential)) {
            return findMap(potential, offspring1, offspring2);
        }

        return potential;
    }

    private Route selectParent() {
        double size = 0;
        double[] rouletteWheel = new double[generation.size()];
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
                return generation.get(population-i-1);
            }
        }

        return null;
    }

    private List<Route> mutate (List<Route> generation){
        int twoOptSize = (int) Math.round(generation.get(0).getCities().size() * this.crossoverRate);

        return generation.stream()
                .map(route -> twoOptSwap(route, twoOptSize))
                .collect(Collectors.toList());
    }

    private Route twoOptSwap(Route route, int twoOptSize) {
        int k = random.nextInt(route.getCities().size());
        while (k <= 0) {
            k = random.nextInt(route.getCities().size());
        }
        int i = random.nextInt(k);
        return createNewCandidate(i, k, route);
    }

    private Route createNewCandidate(int i, int k, Route route) {

        List<City> cities = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            cities.add(route.getCities().get(j));
        }
        for (int j = k; j >= i; j--) {
            cities.add(route.getCities().get(j));
        }
        if(route.getCities().size() > k+1)
            for (int j = k+1; j < route.getCities().size(); j++) {
                cities.add(route.getCities().get(j));
            }

        return new Route(cities);
    }
}