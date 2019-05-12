package com.mvp.java.strategy;

import com.mvp.java.model.salesman.Route;

public interface ISalesmanStrategy {

    Route solve(Route route);
}
