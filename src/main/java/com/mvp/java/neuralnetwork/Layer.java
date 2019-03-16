package com.mvp.java.neuralnetwork;

import java.util.StringJoiner;

public class Layer {

    private Neuron[] neuronArray;

    public Layer(int layerSize, int previousLayerSize){
        neuronArray = new Neuron[layerSize];

        for ( int i = 0; i < layerSize; i++) {
            neuronArray[i] = new Neuron(previousLayerSize);
        }
    }

    public int size(){
        return neuronArray.length;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");

        for ( Neuron n : neuronArray ) {
            joiner.add("Neuron: \n" + n.toString());
        }

        return joiner.toString();
    }
}
