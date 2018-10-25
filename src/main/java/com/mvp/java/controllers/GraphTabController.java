package com.mvp.java.controllers;

import com.mvp.java.services.TwoOptService;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component
public class GraphTabController {

    @Autowired
    TwoOptService twoOptService;

    @FXML private Canvas graphCanvas;
    @FXML public TextField cityCountInput;
    @FXML public ColorPicker cityColorPicker;
    @FXML public ColorPicker roadColorPicker;
    private GraphicsContext gc;
    private List<Point2D> points;

    private int cityCount = 70;
    private double citySize = 4.5;
    private Color cityColor = Color.valueOf("#e5e5e5");
    private double roadSize = 2.5;
    private Color roadColor = Color.valueOf("#4d4d4d");

    private double horizontalMargin = 10;
    private double verticalMargin = 10;
    private Color backgroundColor = Color.gray(0.12);

    public void initialize(){
        gc = graphCanvas.getGraphicsContext2D();
        this.cityColorPicker.setValue(cityColor);
        this.roadColorPicker.setValue(roadColor);
        this.cityCountInput.setText(String.valueOf(cityCount));
        clear();
        generatePoints();
        draw();
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
        CompletableFuture<List<Point2D>> futureCities = twoOptService.optimise(points);
        futureCities.whenCompleteAsync((point2DS, throwable) -> {points = point2DS; clear(); draw();});
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
}
