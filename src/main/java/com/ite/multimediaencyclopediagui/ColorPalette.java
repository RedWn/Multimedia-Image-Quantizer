package com.ite.multimediaencyclopediagui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColorPalette {
        public static Button colorPaletteButton() {
            Button colorButton = new Button("Color Palette");
            colorButton.setOnAction(event -> {

                // Create a new stage for the pop-up window
                Stage popupStage = new Stage();

                // Set the owner of the pop-up window to the primary stage
                popupStage.initOwner(HelloApplication.window);

                // Set the modality of the pop-up window
                popupStage.initModality(Modality.WINDOW_MODAL);

                Image image = HelloApplication.imageViewOriginal.getImage();

                List<Color> colors = generateColorPalette(image);
                HBox colorBox = new HBox();
                for (Color color : colors) {
                    Rectangle rectangle = new Rectangle(20, 20, color);
                    colorBox.getChildren().add(rectangle);
                }
                HBox root = new HBox();
                root.getChildren().addAll(colorBox);

                Scene scene = new Scene(root, 300, 100);

                // Set the scene and show the pop-up window
                popupStage.setTitle("Color Palette");
                popupStage.setScene(scene);
                popupStage.show();
            });
        return colorButton;
        }

    private static List<Color> generateColorPalette(Image image) {
        // Get the pixel reader for the image
        PixelReader pixelReader = image.getPixelReader();

        // Create a list to hold the colors
        List<Color> colors = new ArrayList<>();

        // Add all the colors in the image to the list
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                colors.add(color);
            }
        }

        // Create a list to hold the buckets
        List<List<Color>> buckets = new ArrayList<>();
        buckets.add(colors);

        // Subdivide the buckets until we have 16 buckets
        while (buckets.size() < 16) {
            // Find the bucket with the greatest range in any color channel
            List<Color> bucketToDivide = null;
            double maxRange = 0;
            for (List<Color> bucket : buckets) {
                double minRed = bucket.stream().mapToDouble(Color::getRed).min().getAsDouble();
                double maxRed = bucket.stream().mapToDouble(Color::getRed).max().getAsDouble();
                double minGreen = bucket.stream().mapToDouble(Color::getGreen).min().getAsDouble();
                double maxGreen = bucket.stream().mapToDouble(Color::getGreen).max().getAsDouble();
                double minBlue = bucket.stream().mapToDouble(Color::getBlue).min().getAsDouble();
                double maxBlue = bucket.stream().mapToDouble(Color::getBlue).max().getAsDouble();
                double range = Math.max(maxRed - minRed, Math.max(maxGreen - minGreen, maxBlue - minBlue));
                if (range > maxRange) {
                    maxRange = range;
                    bucketToDivide = bucket;
                }
            }

            // Find out which color channel has the greatest range
            double minRed = bucketToDivide.stream().mapToDouble(Color::getRed).min().getAsDouble();
            double maxRed = bucketToDivide.stream().mapToDouble(Color::getRed).max().getAsDouble();
            double minGreen = bucketToDivide.stream().mapToDouble(Color::getGreen).min().getAsDouble();
            double maxGreen = bucketToDivide.stream().mapToDouble(Color::getGreen).max().getAsDouble();
            double minBlue = bucketToDivide.stream().mapToDouble(Color::getBlue).min().getAsDouble();
            double maxBlue = bucketToDivide.stream().mapToDouble(Color::getBlue).max().getAsDouble();
            String channel;
            if (maxRed - minRed > maxGreen - minGreen && maxRed - minRed > maxBlue - minBlue) {
                channel = "red";
            } else if (maxGreen - minGreen > maxBlue - minBlue) {
                channel = "green";
            } else {
                channel = "blue";
            }

            // Sort the pixels according to that channel's values
            Comparator<Color> comparator;
            switch (channel) {
                case "red":
                    comparator = Comparator.comparing(Color::getRed);
                    break;
                case "green":
                    comparator = Comparator.comparing(Color::getGreen);
                    break;
                default:
                    comparator = Comparator.comparing(Color::getBlue);
                    break;
            }
            bucketToDivide.sort(comparator);

            // Move the upper half of the pixels into a new bucket
            int medianIndex = bucketToDivide.size() / 2;
            List<Color> newBucket = new ArrayList<>(bucketToDivide.subList(medianIndex, bucketToDivide.size()));
            bucketToDivide.subList(medianIndex, bucketToDivide.size()).clear();
            buckets.add(newBucket);
        }

        // Average the pixels in each bucket to get the final color palette
        List<Color> palette = new ArrayList<>();
        for (List<Color> bucket : buckets) {
            double redSum = 0;
            double greenSum = 0;
            double blueSum = 0;
            for (Color color : bucket) {
                redSum += color.getRed();
                greenSum += color.getGreen();
                blueSum += color.getBlue();
            }
            Color averageColor = Color.color(redSum / bucket.size(), greenSum / bucket.size(), blueSum / bucket.size());
            palette.add(averageColor);
        }

        return palette;
    }
}