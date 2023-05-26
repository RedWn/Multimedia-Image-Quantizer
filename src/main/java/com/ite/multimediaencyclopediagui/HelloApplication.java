package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;


public class HelloApplication extends Application {
    static Stage window;
    static ImageView imageViewOriginal, imageViewFirstAlgo, imageViewSecondAlgo;
    static RadioButton originalImageRadioButton, FirstAlgoRadioButton, SecondAlgoRadioButton, colorsSelectedToggle;
    /**
     * Directory where images are stored after applying the algorithm.
     */
    private String resultsDirectory = new String();

    public static void main(String[] args) {
        launch();
    }

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

        //hard-coded, for speedy reasons
        resultsDirectory = "D:\\";

        imageViewOriginal.setPreserveRatio(true);
        imageViewFirstAlgo.setPreserveRatio(true);
        imageViewSecondAlgo.setPreserveRatio(true);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        Text resultsDirectoryTextNode = new Text();
        resultsDirectoryTextNode.setText("Selected directory: " + resultsDirectory);

        Button chooseDirectoryButton = new Button("Choose a directory");
        chooseDirectoryButton.setOnAction(e -> {
            File chosenDirectory = directoryChooser.showDialog(stage);
            this.resultsDirectory = chosenDirectory.toString();
            resultsDirectoryTextNode.setText("Selected directory: " + chosenDirectory);
        });

        FileChooser fileChooser = new FileChooser();
        Button uploadImageButton = new Button("Choose an image");

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(stage);
                System.out.println(resultsDirectory);

                Image image = new Image(chosenFile.toURI().toString());
                imageViewOriginal.setImage(image);

                BufferedImage originalPicture = ImageIO.read(chosenFile);
                Pixel[] originalPicturePixels = ImageUtils.ImageToPixels(originalPicture);
                Pixel[] quantizedPixels = MedianCutAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.valueOf(HelloApplication.colorsSelectedToggle.getText()));
                Pixel[] quantizedPixels2 = LloydsAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.valueOf(HelloApplication.colorsSelectedToggle.getText()));

                BufferedImage bufferedQuantizedImage = ImageUtils.PixelsToImage(quantizedPixels, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());
                Image nonBufferedQuantizedImageToMakeJavaHappy = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage);

                BufferedImage bufferedQuantizedImage2 = ImageUtils.PixelsToImage(quantizedPixels2, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());
                Image nonBufferedQuantizedImageToMakeJavaHappy2 = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage2);

                String pathname = Path.of(resultsDirectory, "new-test.jpg").toString();
                String pathname2 = Path.of(resultsDirectory, "new-test2.jpg").toString();
                IOIndexed.writeIndexed(bufferedQuantizedImage, pathname);
                IOIndexed.writeIndexed(bufferedQuantizedImage2, pathname2);

//                IndexedImage indexed = IOIndexed.readIndexed("output.rii");
//                BufferedImage bufferedQuantizedImageX = ImageUtils.PixelsToImage(indexed.pixels, indexed.width, indexed.height, originalPicture.getType());
//                Image nonBufferedQuantizedImageToMakeJavaHappyX = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImageX);
                
//                ImageIO.write(bufferedQuantizedImage, "jpg", new File(pathname));
//                ImageIO.write(bufferedQuantizedImage2, "jpg", new File(pathname2));

                imageViewFirstAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy);
                imageViewSecondAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy2);

                Stage popUpStage = new Stage();
                popUpStage.initOwner(window);
                HBox hBox = new HBox(10);

                imageViewOriginal.setFitWidth(500);
                imageViewFirstAlgo.setFitWidth(500);
                imageViewSecondAlgo.setFitWidth(500);

                hBox.getChildren().addAll(imageViewOriginal, imageViewFirstAlgo, imageViewSecondAlgo);
                hBox.setAlignment(Pos.CENTER);

                Label label1 = new Label("Original");
                Label label2 = new Label("1st Algorithm");
                Label label3 = new Label("2nd Algorithm");
                HBox hBoxLabels = new HBox(350);
                hBoxLabels.setAlignment(Pos.CENTER);
                hBoxLabels.getChildren().addAll(label1, label2, label3);

                VBox vBox = new VBox(10);
                vBox.getChildren().addAll(hBoxLabels, hBox);

                Scene popUpScene = new Scene(vBox,  1500, 400);

                popUpStage.setTitle("Algorithms");
                popUpStage.setScene(popUpScene);
                popUpStage.show();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        Label colorsLabel = new Label("Choose how many colors do you want in the new image?");
        // Create the radio buttons
        RadioButton twoColorsRadioButton = new RadioButton("2");
        RadioButton fourColorsRadioButton = new RadioButton("4");
        RadioButton eightColorsRadioButton = new RadioButton("8");
        RadioButton sixteenColorsRadioButton = new RadioButton("16");
        RadioButton thirtyTwoColorsRadioButton = new RadioButton("32");
        RadioButton sixtyFourColorsRadioButton = new RadioButton("64");
        RadioButton oneTwoEightColorsRadioButton = new RadioButton("128");
        RadioButton twoFiveSixColorsRadioButton = new RadioButton("256");

        // Create a toggle group and add the radio buttons to it
        ToggleGroup colorsToggleGroupRadioButtons = new ToggleGroup();

        // Add a listener to the selected toggle property
        colorsToggleGroupRadioButtons.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            // Get the selected radio button
            colorsSelectedToggle = (RadioButton) colorsToggleGroupRadioButtons.getSelectedToggle();
        });
        twoColorsRadioButton.setSelected(true);

        twoColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        fourColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        eightColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        sixteenColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        thirtyTwoColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        sixtyFourColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        oneTwoEightColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);
        twoFiveSixColorsRadioButton.setToggleGroup(colorsToggleGroupRadioButtons);

        HBox hBoxColorsRadioButtons = new HBox(10);
        hBoxColorsRadioButtons.setAlignment(Pos.CENTER);
        hBoxColorsRadioButtons.getChildren().addAll(twoColorsRadioButton, fourColorsRadioButton, eightColorsRadioButton,
                sixteenColorsRadioButton, thirtyTwoColorsRadioButton, sixtyFourColorsRadioButton, oneTwoEightColorsRadioButton,
                twoFiveSixColorsRadioButton);

        Label label = new Label("Choose an Image:");
        // Create the radio buttons
        originalImageRadioButton = new RadioButton("Original");
        FirstAlgoRadioButton = new RadioButton("1st Algorithm");
        SecondAlgoRadioButton = new RadioButton("2nd Algorithm");

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

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(50);
        hBox.getChildren().addAll(label, vBoxRadioButtons, ColorPalette.colorPaletteButton(), Histogram.histogramButton());

        VBox appContainer = new VBox();
        appContainer.setAlignment(Pos.CENTER);
        appContainer.setPadding(new Insets(10));
        appContainer.setSpacing(15);
        appContainer.getChildren().addAll(chooseDirectoryButton, resultsDirectoryTextNode, colorsLabel, hBoxColorsRadioButtons, uploadImageButton, hBox);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(appContainer);

        stage.setScene(new Scene(layout, 600, 300));
        stage.show();
    }
}