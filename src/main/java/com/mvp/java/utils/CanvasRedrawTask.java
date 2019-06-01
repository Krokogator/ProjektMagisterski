package com.mvp.java.utils;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CanvasRedrawTask extends AnimationTimer {
    private final AtomicReference<Route> data = new AtomicReference<Route>(null);
    private final Canvas canvas;

    public CanvasRedrawTask(Canvas canvas) {
        this.canvas = canvas;
    }

    public void requestRedraw(Route dataToDraw) {
        data.set(dataToDraw);
        start(); // in case, not already started
    }

    public void handle(long now) {
        // check if new data is available
        Route dataToDraw = data.getAndSet(null);
        if (dataToDraw != null) {
            redraw(canvas, dataToDraw);
        }
    }

    private Route route;
    private List<Point2D> points;
    private Canvas graphCanvas;
    private GraphicsContext gc;


    protected void redraw(Canvas canvas, Route route) {
        this.route = route;
        this.graphCanvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        try {
            loadUSAPoints(canvas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clear();
        draw();
    }
    private Color backgroundColor = Color.gray(0.12);


    private void loadUSAPoints(Canvas canvas) throws IOException {
        List<City> cities = this.route.getCities();
        List<Point2D> points = cities.stream()
                .map(city -> new Point2D(city.getX(), city.getY()))
                .collect(Collectors.toList());

        this.points = points.stream()
                .map(point -> {
                    return new Point2D((point.getY()+130) * (canvas.getWidth()/65f), ((point.getX()*-1+50)) * (canvas.getHeight()/27f));
                })
                .collect(Collectors.toList());
    }


    private void draw(){
        //First city + road to last city
        Point2D firstPoint = points.get(0);
        Point2D lastPoint = points.get(points.size() - 1);

        drawCity(firstPoint.getX(), firstPoint.getY());
        drawRoad(lastPoint.getX(), lastPoint.getY(),
                firstPoint.getX(), firstPoint.getY());

        //All other
        for(int i = 1; i < points.size(); i++){
            Point2D previous = points.get(i-1);
            Point2D p = points.get(i);

            //Draws path between cities
            drawRoad(previous.getX(), previous.getY(), p.getX(), p.getY());
        }

        //All other
        for(int i = 1; i < points.size(); i++){
            Point2D p = points.get(i);

            //Draws a dot of a single city
            drawCity(p.getX(), p.getY());
        }

    }

    private double citySize = 4.5;
    private Color cityColor = Color.valueOf("#e5e5e5");
    private double roadSize = 2.5;
    private Color roadColor = Color.valueOf("#4d4d4d");


    private void drawCity(double x, double y) {
        gc.setFill(cityColor);
        gc.fillOval(x-citySize/2, y-citySize/2, citySize, citySize);
    }

    private void drawRoad(double x1, double y1, double x2, double y2){
        gc.setStroke(roadColor);
        gc.setLineWidth(roadSize);
        gc.strokeLine(x1, y1, x2, y2);
    }

    public void clear(){
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        gc.setFill(backgroundColor);
        gc.fillRect(0,0,graphCanvas.getWidth(), graphCanvas.getHeight());
    }

}

