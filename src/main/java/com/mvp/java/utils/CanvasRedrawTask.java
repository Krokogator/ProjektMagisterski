package com.mvp.java.utils;

import com.mvp.java.controllers.SalesmanTabController;
import com.mvp.java.model.salesman.Route;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CanvasRedrawTask extends AnimationTimer {
    private final AtomicReference<Route> data = new AtomicReference<Route>(null);
    private final SalesmanTabController salesmanTabController;

    public CanvasRedrawTask(SalesmanTabController salesmanTabController) {
        this.salesmanTabController = salesmanTabController;
    }

    public void requestRedraw(Route dataToDraw) {
        data.set(dataToDraw);
        start(); // in case, not already started
    }

    public void handle(long now) {
        // check if new data is available
        Route dataToDraw = data.getAndSet(null);
        if (dataToDraw != null) {
            redraw(dataToDraw);
        }
    }

    private Route route;
    private List<Point2D> points;
    private Canvas graphCanvas;
    private GraphicsContext gc;


    protected void redraw(Route route) {
        salesmanTabController.draw(route);
    }





}

