package com.mvp.java.model;

public class SolvedKnapsack {

    private Item[] items;
    private int maxSize;
    private int usedSize;
    private int value;

    public SolvedKnapsack(Item[] items, int maxSize) {
        this.items = items;
        this.maxSize = maxSize;

        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            usedSize += item.getSize();
            value += item.getValue();
        }
    }

    public Item[] getItems() {
        return items;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getUsedSize() {
        return usedSize;
    }

    public int getValue() {
        return value;
    }
}


