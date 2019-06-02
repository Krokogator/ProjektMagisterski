package com.mvp.java.controllers;

import com.mvp.java.model.knapsack.Knapsack;
import com.mvp.java.services.KnapsackService;
import com.mvp.java.strategy.knapsack.BacktrackingKnapsackStrategy;
import com.mvp.java.strategy.knapsack.GeneticKnapsackStrategy;
import com.mvp.java.utils.KnapsackBacktrackInfoTask;
import com.mvp.java.utils.KnapsackGeneticInfoTask;
import com.mvp.java.utils.KnapsackReader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Component
public class KnapsackTabController {

    @Autowired
    private KnapsackService knapsackService;

    @FXML private Label itemsNumber;
    @FXML private Label itemsWeight;
    @FXML private TextField inputMaxWeight;

    // Backtracking
    @FXML private Label outputBackProfit;
    @FXML private Label outputBackWeight;

    // Genetic
    // Input
    @FXML private TextField inputPopulationSize;
    @FXML private TextField inputMutationRate;
    @FXML private TextField inputMaxGeneration;

    // Output
    @FXML private Label outGeneticProfit;
    @FXML private Label outGeneticWeight;
    @FXML private Label outGeneticGeneration;

    private FileChooser fileChooser;

    private int[] profit;
    private int[] weight;

    private KnapsackGeneticInfoTask task;
    private KnapsackBacktrackInfoTask backTask;

    public void initialize() {
        initFileChooser();
        this.task = new KnapsackGeneticInfoTask(this);
        this.backTask = new KnapsackBacktrackInfoTask(this);
    }

    private void initFileChooser() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    public void chooseFile(MouseEvent mouseEvent) {
        Stage stage = (Stage) itemsNumber.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Knapsack knapsack = null;
            try {
                knapsack = KnapsackReader.load(selectedFile);
            } catch (IOException e) {
                System.out.println("Failed to get route from file!");
            }
            if (knapsack != null) {
                fileChooser.setInitialDirectory(selectedFile.getParentFile());
                loadKnapsack(knapsack);
            }
        }
    }

    private void loadAndSortWeightAndProfit(Knapsack knapsack) {
        int[] profit = knapsack.getProfit();
        int[] weight = knapsack.getWeight();

        List<Pair<Integer, Integer>> map = new ArrayList<>();
        for(int i = 0; i< profit.length; i++){
            map.add(new Pair(weight[i], profit[i]));
        }

        map.sort(Comparator.comparingDouble(o -> new Double(o.getValue()) / new Double(o.getKey())));
        Collections.reverse(map);

        for (Pair<Integer, Integer> p: map) {
            System.out.println("Value: "+ new Double(p.getValue()) / new Double(p.getKey()) +" : "+p.getKey() +" : "+p.getValue());
        }

        this.profit = map.stream().mapToInt(x -> x.getValue()).toArray();
        this.weight = map.stream().mapToInt(x -> x.getKey()).toArray();
    }

    public void loadKnapsack(Knapsack knapsack) {

        loadAndSortWeightAndProfit(knapsack);

        itemsNumber.setText(String.valueOf(knapsack.getSize()));
        itemsWeight.setText(String.valueOf(knapsack.getTotalWeight()));
    }

    private int getMaxWeight() {
        return Integer.valueOf(inputMaxWeight.getText());
    }

    private void setProfitAndWeightLabel(Label outputProfit, Label outputWeight) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                knapsackService.solve(profit.clone(), weight.clone(), getMaxWeight());
            }
        });
        t1.start();


//        int profit = 0;
//        int weight = 0;
//        for (int i = 0; i < result.length; i++) {
//            if (result[i]) {
//                profit += this.profit[i];
//                weight += this.weight[i];
//            }
//        }
//
//        outputProfit.setText(String.valueOf(profit));
//        outputWeight.setText(String.valueOf(weight));
    }


    public void runBacktracking(MouseEvent mouseEvent) {
        knapsackService.setStrategy(new BacktrackingKnapsackStrategy(this));
        setProfitAndWeightLabel(outputBackProfit, outputBackWeight);
    }


    public void runGenetic(MouseEvent mouseEvent) {
        int epochs = Integer.valueOf(inputMaxGeneration.getText());
        int populationSize = Integer.valueOf(inputPopulationSize.getText());
        double mutationRate = Double.valueOf(inputMutationRate.getText());

        knapsackService.setStrategy(new GeneticKnapsackStrategy(epochs, populationSize, mutationRate, this));
        setProfitAndWeightLabel(outGeneticProfit, outGeneticWeight);


    }


    public void updateGeneticInfo(Map<String, String> info) {
        outGeneticGeneration.setText(info.get("outputGeneration"));
        outGeneticWeight.setText(info.get("outputWeight"));
        outGeneticProfit.setText(info.get("outputProfit"));
    }

    public void updateBackInfo(Map<String, String> info) {
        outputBackProfit.setText(info.get("outputBackProfit"));
        outputBackWeight.setText(info.get("outputBackWeight"));
    }

    public void redrawInfo(Map<String,String> info) {
        task.requestInfo(info);
    }

    public void redrawBackInfo(Map<String, String> info) {
        backTask.requestInfo(info);
    }
}
