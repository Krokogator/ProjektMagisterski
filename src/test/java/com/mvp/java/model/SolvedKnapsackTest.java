package com.mvp.java.model;

import com.mvp.java.model.knapsack.Item;
import com.mvp.java.model.knapsack.SolvedKnapsack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolvedKnapsackTest {

    @Test
    void solvedKnapsack(){
        Item[] items = {
                new Item(5, 20),
                new Item(10, 15),
                new Item(2, 30),
                new Item(30, 50)
        };
        SolvedKnapsack solvedKnapsack = new SolvedKnapsack(items, 50);

        assertEquals(solvedKnapsack.getMaxSize(), 50);
        assertEquals(solvedKnapsack.getUsedSize(), 47);
        assertEquals(solvedKnapsack.getValue(), 115);
    }

}