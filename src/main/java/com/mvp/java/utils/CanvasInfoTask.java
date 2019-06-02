package com.mvp.java.utils;

import com.mvp.java.controllers.SalesmanTabController;
import com.mvp.java.model.salesman.Route;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CanvasInfoTask extends AnimationTimer {
    private final AtomicReference<Map<String, String>> info = new AtomicReference<>(null);
    private final SalesmanTabController salesmanTabController;

    public CanvasInfoTask(SalesmanTabController salesmanTabController) {
        this.salesmanTabController = salesmanTabController;
    }

    public void requestInfo(Map<String, String> info) {
        this.info.set(info);
        start(); // in case, not already started
    }

    public void handle(long now) {
        // check if new data is available
        Map<String, String> info = this.info.getAndSet(null);
        if (info != null) {
            redraw(info);
        }
    }

    private Route route;
    private List<Point2D> points;
    private Canvas graphCanvas;
    private GraphicsContext gc;


    protected void redraw(Map<String, String> info) {
        salesmanTabController.updateTSPInfo(info);
    }





}

