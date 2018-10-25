package com.mvp.java.controllers;

import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TabPaneManger {

    private final ConsoleTabController consoleTabController;
    private final LoggerTabController loggerTabController;
    private final GraphTabController graphTabController;
    private final NeuronTabController neuronTabController;

    @Autowired
    public TabPaneManger(ConsoleTabController consoleTabController, LoggerTabController loggerTabController, GraphTabController graphTabController, NeuronTabController neuronTabController) {
        this.consoleTabController = consoleTabController;
        this.loggerTabController = loggerTabController;
        this.graphTabController = graphTabController;
        this.neuronTabController = neuronTabController;
    }

    public TextArea getVisualLog() {
        return loggerTabController.getLoggerTxtArea();
    }

}
