package com.mvp.java.controllers;

import com.mvp.java.neuralnetwork.Neuron;
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
public class NeuronTabController {

    @FXML
    private Canvas neuronCanvas;


    private GraphicsContext gc;
    private List<Point2D> points;

    private double citySize = 5;
    private Color neuronColor = Color.valueOf("#e5e5e5");
    private double roadSize = 0.5;
    private Color connectionColor = Color.gray(0.8);

    private double horizontalMargin = 10;
    private double verticalMargin = 10;
    private Color backgroundColor = Color.gray(0.12);

    List<Point2D> layer0;
    List<Point2D> layer1;
    List<Point2D> layer2;
    List<Point2D> layer3;

    public void initialize(){
        gc = neuronCanvas.getGraphicsContext2D();

        clear();
        generatePoints();
        draw();
    }


    private void generatePoints(){
        layer0 = new ArrayList<>();
        layer1 = new ArrayList<>();
        layer2 = new ArrayList<>();
        layer3 = new ArrayList<>();

        generateLayer(layer0, 0, 40, 15, 20);
        generateLayer(layer1, 1, 20, 40, 38);
        generateLayer(layer2, 2, 10, 100, 70);
        generateLayer(layer3, 3, 2, 260, 200);

    }

    private void generateLayer(List<Point2D> layer, int index, int size, int verticalOffset, int spacing){
        for (int i = 0; i < size; i++){
            layer.add(
                    new Point2D(
                            horizontalMargin + index * 600,
                            i * spacing + verticalOffset
                    )
            );
        }
    }

    private void drawNeurons(List<Point2D> layer){
        for (Point2D p : layer) {
            drawCity(p.getX(), p.getY());
        }
    }

    private void drawConnections(List<Point2D> layerA, List<Point2D> layerB){
        for(Point2D p1 : layerA){
            for(Point2D p2 : layerB){
                drawRoad(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
        }
    }



    private void draw(){
        drawNeurons(layer0);
        drawNeurons(layer1);
        drawNeurons(layer2);
        drawNeurons(layer3);

        drawConnections(layer0, layer1);
        drawConnections(layer1, layer2);
        drawConnections(layer2, layer3);
    }

    private void drawCity(double x, double y) {
        gc.setFill(neuronColor);
        gc.fillOval(x-citySize/2, y-citySize/2, citySize, citySize);
    }

    private void drawRoad(double x1, double y1, double x2, double y2){
        gc.setStroke(connectionColor);
        gc.setLineWidth(roadSize);
        gc.strokeLine(x1, y1, x2, y2);
    }

    public void clear(){
        gc.setFill(backgroundColor);
        gc.fillRect(0,0,neuronCanvas.getWidth(), neuronCanvas.getHeight());
    }

    public void test(MouseEvent mouseEvent) {
        Neuron n = new Neuron(5);
        System.out.println(n.toString());
    }
}

