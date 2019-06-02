package com.mvp.java.controllers;

import com.mvp.java.model.salesman.City;
import com.mvp.java.model.salesman.Route;
import com.mvp.java.services.SalesmanService;
import com.mvp.java.services.TwoOptService;
import com.mvp.java.strategy.salesman.GeneticSalesmanStrategy;
import com.mvp.java.strategy.salesman.SimulatedAnnealingSalesmanStrategy;
import com.mvp.java.utils.CanvasInfoTask;
import com.mvp.java.utils.CanvasRedrawTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
    @FXML public ColorPicker cityColorPicker;
    @FXML public ColorPicker roadColorPicker;

    // Simulated annealing
    // Inputs

    @FXML private TextField inputTemp;
    @FXML private TextField inputEpsilon;
    @FXML private TextField inputAlpha;

    // Outputs

    @FXML private Label outputTemp;
    @FXML private Label outputCurrent;
    @FXML private Label outputBest;


    private Stage stage;
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

    private FileChooser fileChooser;

    public void initialize(){

        this.fileChooser = new FileChooser();
        gc = graphCanvas.getGraphicsContext2D();
        redrawTask = new CanvasRedrawTask(this);
        infoTask = new CanvasInfoTask(this);
        this.cityColorPicker.setValue(cityColor);
        this.roadColorPicker.setValue(roadColor);
        clear();
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

    private void loadUSAPoints() {
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

    private void loadPoints() {
//        loadUSAPoints();
        loadXYPoints();
    }

    private void loadXYPoints() {
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
            loadPoints();
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

        drawCity(firstPoint.getX(), firstPoint.getY());

    }

    private void redraw() throws IOException {
        loadPoints();
        clear();
        draw();
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

        Double temp = Double.valueOf(Optional.of(inputTemp.getText()).orElse("10"));
        Double eps = Double.valueOf(Optional.of(inputEpsilon.getText()).orElse("0.01"));
        Double alpha = Double.valueOf(Optional.of(inputAlpha.getText()).orElse("0.9"));
        SimulatedAnnealingSalesmanStrategy strategy = new SimulatedAnnealingSalesmanStrategy(alpha, eps, temp, this);

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
    }

    public void draw(Route route) {
        this.route = route;
        loadPoints();
        clear();
        draw();
    }

    private CanvasRedrawTask redrawTask;
    private CanvasInfoTask infoTask;


    public void redrawRoute(Route route) {
        redrawTask.requestRedraw(route);
    }

    public void redrawInfo(Map<String,String> info) {
        infoTask.requestInfo(info);
    }

    public void chooseFile(MouseEvent mouseEvent) {
        Stage stage = (Stage) graphCanvas.getScene().getWindow();


        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
//                new ExtensionFilter("Text Files", "*.txt"),
//                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
//                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(stage);


        if (selectedFile != null) {
            try {
                System.out.println(selectedFile.getName());
                this.route = salesmanService.getRoute(selectedFile);
                redraw();
                fileChooser.setInitialDirectory(selectedFile.getParentFile());
            } catch (IOException e) {
                System.out.println("Failed to get route from file!");
            }
        }
    }

    public void updateTSPInfo(Map<String, String> infoMap) {
        String temp = infoMap.getOrDefault("outputTemp", "0");
        String current = infoMap.getOrDefault("outputCurrent", "0");
        String best = infoMap.getOrDefault("outputBest", "0");

        outputTemp.setText(temp);
        outputCurrent.setText(current);
        outputBest.setText(best);

    }
}
