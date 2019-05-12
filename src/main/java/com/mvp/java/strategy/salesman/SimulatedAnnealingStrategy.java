package com.mvp.java.strategy.salesman;

import com.mvp.java.model.salesman.Route;
import com.mvp.java.strategy.ISalesmanStrategy;

public class SimulatedAnnealingStrategy implements ISalesmanStrategy {

    private double alpha;
    private double epsilon;
    private double tempStart;

    private double tempCurrent;
    private Route best;
    private Route current;
    private Route candidate;

    public SimulatedAnnealingStrategy(
            double alpha,
            double epsilon,
            double temp
    ) {
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
        createNewCandidate(8, 20);
    }

    private void createNewCandidate(int i, int k) {
        candidate = new Route();
        for (int j = 0; j < i; k++) {

        }
    }
}
