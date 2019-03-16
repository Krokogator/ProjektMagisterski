package com.mvp.java.neuralnetwork;

import org.junit.Test;

public class NeuralNetworkTest {

    @Test
    public void initializationTest(){
        NeuralNetwork network = new NeuralNetwork(20, 2, 10, 5);
        System.out.print(network.toString());
    }
}