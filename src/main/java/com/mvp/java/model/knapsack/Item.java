package com.mvp.java.model.knapsack;

public class Item {
    private int size;
    private int value;

    public Item(int size, int value) {
        this.size = size;
        this.value = value;
    }

    public int getSize() {
        return size;
    }

    public int getValue() {
        return value;
    }
}
