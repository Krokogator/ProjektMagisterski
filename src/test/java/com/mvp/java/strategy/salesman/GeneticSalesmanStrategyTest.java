package com.mvp.java.strategy.salesman;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class GeneticSalesmanStrategyTest {

    @Test
    void crossoverWithOneCycle() {
        GeneticSalesmanStrategy strategy = new GeneticSalesmanStrategy(100, 1);

        List<Integer> p1 = Arrays.asList(3, 4, 8, 2, 7, 1, 6, 5);
        List<Integer> p2 = Arrays.asList(4, 2, 5, 1, 6, 8, 3, 7);

        List<City> parent1 = p1.stream()
                .map(City::new)
                .collect(Collectors.toList());
        List<City> parent2 = p2.stream()
                .map(City::new)
                .collect(Collectors.toList());
        Route route1 = new Route(parent1);
        Route route2 = new Route(parent2);

        strategy.crossover(route1, route2);
    }

    @Test
    void crossoverWithMultipleCycles() {
        GeneticSalesmanStrategy strategy = new GeneticSalesmanStrategy(100, 1);

        List<Integer> p1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> p2 = Arrays.asList(2, 7, 5, 8, 4, 1, 6, 3);

        List<City> parent1 = p1.stream()
                .map(City::new)
                .collect(Collectors.toList());
        List<City> parent2 = p2.stream()
                .map(City::new)
                .collect(Collectors.toList());
        Route route1 = new Route(parent1);
        Route route2 = new Route(parent2);

        Pair<Route, Route> pair = strategy.crossover(route1, route2);
    }
}