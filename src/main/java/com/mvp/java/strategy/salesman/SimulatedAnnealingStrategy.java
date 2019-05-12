package com.mvp.java.strategy.salesman;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.strategy.ISalesmanStrategy;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealingStrategy implements ISalesmanStrategy {

    private double alpha;
    private double epsilon;
    private double tempStart;

    private double tempCurrent;
    private Route best;
    private Route current;
    private Route candidate;

    private Random random;

    public SimulatedAnnealingStrategy(){
        this.random = new Random();
    }

    public SimulatedAnnealingStrategy(
            double alpha,
            double epsilon,
            double temp
    ) {
        this();
        this.alpha = alpha;
        this.epsilon = epsilon;
        this.tempStart = temp;
    }

    @Override
    public Route solve(Route route) {
        tempCurrent = tempStart;
        best = route.clone();
        current = route.clone();

        while (tempCurrent > epsilon) {
            iterate();
        }

        return best;
    }

    private void iterate(){
        int k = random.nextInt(current.getCities().size());
        while (k <= 0) {
            k = random.nextInt(current.getCities().size());
        }
        int i = random.nextInt(k);
        createNewCandidate(i, k);

//        swapRandomTwo();

        if(isBest()) {
            this.best = candidate.clone();
            this.current = candidate.clone();
            System.out.println(current.getLength());

        } else if (isAccepted()) {
            this.current = candidate.clone();
            System.out.println(current.getLength());

        }

        decreaseTemp();
    }

    private void swapRandomTwo(){
        int size = current.getCities().size();
        int r = random.nextInt(size-2);
        List<City> cities = current.getCities();

        City temp = cities.get(r);
        cities.set(r, cities.get(r+1));
        cities.set(r+1, temp);

        this.candidate = new Route(cities);
    }

    private void createNewCandidate(int i, int k) {
        List<City> cities = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            cities.add(current.getCities().get(j));
        }
        for (int j = k; j >= i; j--) {
            cities.add(current.getCities().get(j));
        }
        if(current.getCities().size() > k+1)
        for (int j = k+1; j < current.getCities().size(); j++) {
            cities.add(current.getCities().get(j));
        }

        this.candidate = new Route(cities);
    }

    private boolean isBest() {
        if (candidate.getLength() > best.getLength()) {
            return false;
        }
        return true;
    }

    private boolean isAccepted() {
        double candidateLength = candidate.getLength();
        double currentLength = current.getLength();
        if (candidateLength < currentLength) {
            return true;
        } else if ( random.nextDouble() < Math.exp((currentLength - candidateLength) / tempCurrent) ) {
            System.out.println("CHANCE: " + tempCurrent);
            return true;
        }
        return false;
    }

    private void decreaseTemp(){
        this.tempCurrent *= alpha;
    }
}
