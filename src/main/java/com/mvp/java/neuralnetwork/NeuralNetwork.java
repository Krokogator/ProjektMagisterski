package com.mvp.java.neuralnetwork;

import java.util.StringJoiner;

public class NeuralNetwork {

    private Layer[] layerArray;

    public NeuralNetwork(int inputLayer, int outputLayer, int...hiddenLayers){
        layerArray = new Layer[2 + hiddenLayers.length];

        layerArray[0] = new Layer(inputLayer, 0);

        for (int i = 1; i < layerArray.length - 1; i++) {
            layerArray[i] = new Layer(hiddenLayers[i-1], layerArray[i-1].size());
        }

        layerArray[layerArray.length - 1] = new Layer(outputLayer, layerArray[layerArray.length - 2].size());

    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");

        for ( Layer l : layerArray ) {
            joiner.add(l.toString());
        }

        return joiner.toString();
    }
}
