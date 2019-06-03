package com.mvp.java.elements;

import com.mvp.java.controllers.SalesmanTabController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;

public class ResizableCanvas extends Canvas {


    public ResizableCanvas(SalesmanTabController salesmanTabController) {
        widthProperty().addListener(evt -> {
            try {
                salesmanTabController.redraw();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        heightProperty().addListener(evt -> {
            try {
                salesmanTabController.redraw();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(0, height, width, 0);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

}