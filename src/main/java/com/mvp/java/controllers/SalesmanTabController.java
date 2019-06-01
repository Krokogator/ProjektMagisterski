package com.mvp.java.controllers;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.services.SalesmanService;
import com.mvp.java.services.TwoOptService;
import com.mvp.java.strategy.salesman.GeneticSalesmanStrategy;
import com.mvp.java.strategy.salesman.SimulatedAnnealingSalesmanStrategy;
import com.mvp.java.utils.CanvasRedrawTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class SalesmanTabController {

    @Autowired
    SalesmanService salesmanService;

    @Autowired
    TwoOptService twoOptService;

    @FXML private Canvas graphCanvas;
    @FXML public TextField cityCountInput;
    @FXML public ColorPicker cityColorPicker;
    @FXML public ColorPicker roadColorPicker;
    private GraphicsContext gc;
    private List<Point2D> points;
    private Route route;

    private int cityCount = 70;
    private double citySize = 2.5;
    private Color cityColor = Color.valueOf("#e5e5e5");
    private double roadSize = 1.5;
    private Color roadColor = Color.valueOf("#4d4d4d");

    private double horizontalMargin = 10;
    private double verticalMargin = 10;
    private Color backgroundColor = Color.gray(0.12);

    public void initialize(){
        gc = graphCanvas.getGraphicsContext2D();
        task = new CanvasRedrawTask(this);
        this.cityColorPicker.setValue(cityColor);
        this.roadColorPicker.setValue(roadColor);
        this.cityCountInput.setText(String.valueOf(cityCount));
        clear();
//        generatePoints();
        try {
            salesmanService.init();
            this.route = salesmanService.getRoute();
            Collections.shuffle(this.route.getCities());
            loadPoints();
        } catch (IOException e) {
            e.printStackTrace();
        }
        draw();
    }

    private void loadUSAPoints() throws IOException {
        List<City> cities = this.route.getCities();
        List<Point2D> points = cities.stream()
                .map(city -> new Point2D(city.getX(), city.getY()))
                .collect(Collectors.toList());

        this.points = points.stream()
            .map(point -> {
                return new Point2D((point.getY()+130) * (graphCanvas.getWidth()/65f), ((point.getX()*-1+50)) * (graphCanvas.getHeight()/27f));
            })
            .collect(Collectors.toList());
    }

    private void loadPoints() throws IOException {
        loadUSAPoints();
//        loadXYPoints();
    }

    private void loadXYPoints() throws IOException {
        List<City> cities = this.route.getCities();
        List<Point2D> points = cities.stream()
                .map(city -> new Point2D(city.getX(), city.getY()))
                .collect(Collectors.toList());

        this.points = points.stream()
                .map(point -> {
                    return new Point2D(point.getX()*1/6, graphCanvas.getHeight() - point.getY()*1/8);
                })
                .collect(Collectors.toList());
    }

    private void generatePoints(){
        points = new ArrayList<>();

        Random r = new Random();

        for (int i = 0; i < cityCount; i++){
            points.add(
                    new Point2D(
                            r.nextInt((int) (graphCanvas.getWidth() - horizontalMargin * 2)) + horizontalMargin,
                            r.nextInt((int) (graphCanvas.getHeight() - verticalMargin * 2)) + verticalMargin
                    )
            );
        }
    }

    private void orderize(){
        List<Point2D> ordered = new ArrayList<>();

        ordered.add(points.get(0));
        points.remove(0);

        while (!points.isEmpty()){
            Point2D next = null;

            next = (points.stream().min(getNearest(ordered.get(ordered.size()-1))).get());
            points.remove(next);
            ordered.add(next);
            //draw();
        }

        points = ordered;
    }

    private void twoOpt(){
        CompletableFuture<Route> futureCities = twoOptService.optimise(this.route);
        futureCities.whenCompleteAsync((route, throwable) -> {
            this.route = route;
            clear();
            try {
                loadPoints();
            } catch (IOException e) {
                e.printStackTrace();
            }
            draw();
        });

        System.out.println(route.getLength());
    }

    private Comparator<Point2D> getNearest (Point2D firstPoint){
        final Point2D finalP = firstPoint;
        return (p0, p1) -> {
            double ds0 = p0.distance(finalP);
            double ds1 = p1.distance(finalP);
            return Double.compare(ds0, ds1);
        };
    }

    private void draw(){
        //First city + road to last city
        Point2D firstPoint = points.get(0);
        Point2D lastPoint = points.get(points.size() - 1);


        drawRoad(lastPoint.getX(), lastPoint.getY(),
                firstPoint.getX(), firstPoint.getY());
        drawCity(firstPoint.getX(), firstPoint.getY());

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

    public void reinitialize() {
        clear();
        String input = cityCountInput.getText();
        try{
            cityCount = Integer.valueOf(cityCountInput.getText());
        } catch (Exception e) {
            System.out.println("Invalid cities number");
        }
        generatePoints();
        draw();
    }


    public void orderClosest() {
        clear();
        orderize();
        draw();
    }

    public void orderTwoOpt() {
        //clear();
        twoOpt();
        //draw();
    }

    public void setColors(ActionEvent actionEvent) {
        this.cityColor = cityColorPicker.getValue();
        this.roadColor = roadColorPicker.getValue();
        clear();
        draw();
    }

    public void simulatedAnnealing(MouseEvent mouseEvent) {
        SimulatedAnnealingSalesmanStrategy strategy = new SimulatedAnnealingSalesmanStrategy(0.999999, 0.001, 30, this);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                strategy.solve(route);
            }
        });
        t1.start();



//        try {
//            loadPoints();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        clear();
//        draw();
    }

    public void genetic(MouseEvent mouseEvent) {
        GeneticSalesmanStrategy geneticSalesmanStrategy = new GeneticSalesmanStrategy(250, 4000, 0.05, 0.008, this);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                geneticSalesmanStrategy.solve(route);
            }
        });
        t1.start();
////        this.route =
//        try {
//            loadPoints();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        clear();
//        draw();
    }

    public void draw(Route route) {
        this.route = route;
        try {
            loadPoints();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clear();
        draw();
    }

    private CanvasRedrawTask task;


    public void onDataReceived(Route route) {
        // process data / prepare for redraw task
        // ...

        // handover data to redraw task
        task.requestRedraw(route);
    }
}
