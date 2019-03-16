package com.mvp.java.neuralnetwork;

import java.util.Random;
import java.util.StringJoiner;

public class Neuron {

    private Double[] weightArray;

    public Neuron(int numberOfWeights){
        weightArray = new Double[numberOfWeights];
        Random r = new Random();
        for (int i = 0; i < weightArray.length; i++) {
            weightArray[i] = r.nextDouble() * 2 - 1;
        }
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");

        for ( Double w : weightArray ) {
            joiner.add("Weight: " + w.toString());
        }

        return joiner.toString();
    }
}
