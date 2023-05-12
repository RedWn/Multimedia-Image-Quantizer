package com.ite.multimediaencyclopediagui;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Histogram {
    public static Button histogramButton() {
        Button histogramButton = new Button("Histogram");
        histogramButton.setOnAction(event -> {

            // Create a new stage for the pop-up window
            Stage popupStage = new Stage();

            Image image;
            if (HelloApplication.originalImageRadioButton.isSelected()) {
                image = HelloApplication.imageViewOriginal.getImage();
            }
            else if(HelloApplication.FirstAlgoRadioButton.isSelected()){
                image = HelloApplication.imageViewFirstAlgo.getImage();
            }
            else{
                image = HelloApplication.imageViewSecondAlgo.getImage();
            }

            // Create the chart axes
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            // Create the chart
            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setCategoryGap(0);
            chart.setBarGap(0);

            // Create the data series
            XYChart.Series<String, Number> redSeries = new XYChart.Series<>();
            redSeries.setName("Red");
            XYChart.Series<String, Number> greenSeries = new XYChart.Series<>();
            greenSeries.setName("Green");
            XYChart.Series<String, Number> blueSeries = new XYChart.Series<>();
            blueSeries.setName("Blue");

            // Compute the histogram data
            int[] redHistogram = new int[256];
            int[] greenHistogram = new int[256];
            int[] blueHistogram = new int[256];
            PixelReader pixelReader = image.getPixelReader();
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color color = pixelReader.getColor(x, y);
                    int red = (int) (color.getRed() * 255);
                    int green = (int) (color.getGreen() * 255);
                    int blue = (int) (color.getBlue() * 255);
                    redHistogram[red]++;
                    greenHistogram[green]++;
                    blueHistogram[blue]++;
                }
            }

            // Add the data to the series
            for (int i = 0; i < 256; i++) {
                redSeries.getData().add(new XYChart.Data<>(Integer.toString(i), redHistogram[i]));
                greenSeries.getData().add(new XYChart.Data<>(Integer.toString(i), greenHistogram[i]));
                blueSeries.getData().add(new XYChart.Data<>(Integer.toString(i), blueHistogram[i]));
            }

            // Add the series to the chart
            chart.getData().addAll(redSeries, greenSeries, blueSeries);

            // Create a scene and show the primary stage
            Scene scene = new Scene(chart);
            popupStage.setTitle("Histogram");
            popupStage.setScene(scene);
            popupStage.show();

        });
        return histogramButton;
    }
}
