package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.ImageUtils;
import com.ite.multimediaencyclopediagui.images.LloydsAlgorithm;
import com.ite.multimediaencyclopediagui.images.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.Pixel;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;


public class HelloApplication extends Application {
    /**
     * Directory where images are stored after applying the algorithm.
     */
    private final SimpleStringProperty resultsDirectory = new SimpleStringProperty();
    static Stage window;
    static ImageView imageViewOriginal, imageViewFirstAlgo, imageViewSecondAlgo;
    static RadioButton originalImageRadioButton, FirstAlgoRadioButton, SecondAlgoRadioButton;
    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("Multimedia Project");

        Image placeholderImage = new Image("default_image.png");

        imageViewOriginal = new ImageView();
        imageViewFirstAlgo = new ImageView();
        imageViewSecondAlgo = new ImageView();

        imageViewOriginal.setImage(placeholderImage);
        imageViewFirstAlgo.setImage(placeholderImage);
        imageViewSecondAlgo.setImage(placeholderImage);

        imageViewOriginal.setFitWidth(250);
        imageViewFirstAlgo.setFitWidth(250);
        imageViewSecondAlgo.setFitWidth(250);

        imageViewOriginal.setFitHeight(250);
        imageViewFirstAlgo.setFitHeight(250);
        imageViewSecondAlgo.setFitHeight(250);

        imageViewOriginal.setPreserveRatio(true);
        imageViewFirstAlgo.setPreserveRatio(true);
        imageViewSecondAlgo.setPreserveRatio(true);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        Text resultsDirectoryTextNode = new Text();
        Button chooseDirectoryButton = new Button("Choose a directory");

        chooseDirectoryButton.setOnAction(e -> {
            File chosenDirectory = directoryChooser.showDialog(stage);
            this.resultsDirectory.set(chosenDirectory.toString());
            resultsDirectoryTextNode.setText("Selected directory: " + chosenDirectory);
        });

        FileChooser fileChooser = new FileChooser();
        Button uploadImageButton = new Button("Choose an image");

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(stage);
                System.out.println(resultsDirectory.getValue());

                Image image = new Image(chosenFile.toURI().toString());
                imageViewOriginal.setImage(image);

                BufferedImage originalPicture = ImageIO.read(chosenFile);
                Pixel[] originalPicturePixels = ImageUtils.ImageToPixels(originalPicture);
                Pixel[] quantizedPixels = MedianCutAlgorithm.GetQuantizedPixels(originalPicturePixels, 32);
                Pixel[] quantizedPixels2 = LloydsAlgorithm.GetQuantizedPixels(originalPicturePixels, 32);

                BufferedImage bufferedQuantizedImage = ImageUtils.PixelsToImage(quantizedPixels, originalPicture);
                Image nonBufferedQuantizedImageToMakeJavaHappy = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage);

                BufferedImage bufferedQuantizedImage2 = ImageUtils.PixelsToImage(quantizedPixels2, originalPicture);
                Image nonBufferedQuantizedImageToMakeJavaHappy2 = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage2);

                String pathname = Path.of(resultsDirectory.getValue(), "new-test.jpg").toString();
                String pathname2 = Path.of(resultsDirectory.getValue(), "new-test2.jpg").toString();
                ImageIO.write(bufferedQuantizedImage, "jpg", new File(pathname));
                ImageIO.write(bufferedQuantizedImage2, "jpg", new File(pathname2));

                imageViewFirstAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy);
                imageViewSecondAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy2);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });



        Separator separator1 = new Separator();
        separator1.setOrientation(Orientation.VERTICAL);
        separator1.setPrefHeight(80);
        separator1.setValignment(VPos.CENTER);
        separator1.setPadding(new Insets(10));

        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);
        separator2.setPrefHeight(80);
        separator2.setValignment(VPos.CENTER);
        separator2.setPadding(new Insets(10));

        Label label1 = new Label("Original");
        Label label2 = new Label("1st Algorithm");
        Label label3 = new Label("2nd Algorithm");

        VBox imageVBox1 = new VBox();
        VBox imageVBox2 = new VBox();
        VBox imageVBox3 = new VBox();

        imageVBox1.setAlignment(Pos.CENTER);
        imageVBox2.setAlignment(Pos.CENTER);
        imageVBox3.setAlignment(Pos.CENTER);

        imageVBox1.getChildren().addAll(label1, imageViewOriginal);
        imageVBox2.getChildren().addAll(label2, imageViewFirstAlgo);
        imageVBox3.getChildren().addAll(label3, imageViewSecondAlgo);

        Label label = new Label("Choose an Image:");
        // Create the radio buttons
        originalImageRadioButton = new RadioButton("Original");
        FirstAlgoRadioButton = new RadioButton("1st Algorithm");
        SecondAlgoRadioButton = new RadioButton("2ed Algorithm");

        // Create a toggle group and add the radio buttons to it
        ToggleGroup toggleGroupRadioButtons = new ToggleGroup();
        originalImageRadioButton.setToggleGroup(toggleGroupRadioButtons);
        FirstAlgoRadioButton.setToggleGroup(toggleGroupRadioButtons);
        SecondAlgoRadioButton.setToggleGroup(toggleGroupRadioButtons);

        // Select the first radio button by default
        originalImageRadioButton.setSelected(true);

        // Create a VBox to hold the label and radio buttons
        VBox vBoxRadioButtons = new VBox(10);
        vBoxRadioButtons.setPadding(new Insets(10));
        vBoxRadioButtons.getChildren().addAll(originalImageRadioButton, FirstAlgoRadioButton, SecondAlgoRadioButton);

        HBox imagesContainer = new HBox();
        imagesContainer.setAlignment(Pos.CENTER);
        imagesContainer.getChildren().addAll(imageVBox1, separator1, imageVBox2, separator2, imageVBox3);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(50);
        hBox.getChildren().addAll(label, vBoxRadioButtons, ColorPalette.colorPaletteButton(), Histogram.histogramButton());

        VBox appContainer = new VBox();
        appContainer.setAlignment(Pos.CENTER);
        appContainer.setPadding(new Insets(10));
        appContainer.setSpacing(10);
        appContainer.getChildren().addAll(chooseDirectoryButton, resultsDirectoryTextNode, uploadImageButton, imagesContainer, hBox);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(appContainer);

        stage.setScene(new Scene(layout, 800, 500));
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}