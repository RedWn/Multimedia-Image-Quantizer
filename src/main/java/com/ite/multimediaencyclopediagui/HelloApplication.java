package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.Algorithms.LloydsAlgorithm;
import com.ite.multimediaencyclopediagui.images.Algorithms.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
import java.io.IOException;
import java.nio.file.Path;


public class HelloApplication extends Application {
    static Stage window;
    static ImageView imageViewOriginal = new ImageView();
    static ImageView imageViewFirstAlgo = new ImageView();
    static ImageView imageViewSecondAlgo = new ImageView();
    static RadioButton originalImageRadioButton = new RadioButton("Original");
    static RadioButton FirstAlgoRadioButton = new RadioButton("1st Algorithm");
    static RadioButton SecondAlgoRadioButton = new RadioButton("2nd Algorithm");
    static RadioButton colorsSelectedToggle = new RadioButton();

    /**
     * Directory where images are stored after applying the algorithm.
     */
    private String resultsDirectory = "D:\\Test";
    private final Scene mainAlgorithmScene = this.getMainAlgorithmScene();
    private final Scene searchScene = this.getSearchScene();



    public static void main(String[] args) {
        launch();
    }


    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("Multimedia Project");
        window.setMinWidth(1000);
        window.setMinHeight(500);

        stage.setScene(mainAlgorithmScene);
        stage.show();
    }

    private HBox getColorRadioButtonsHBox() {
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

        return hBoxColorsRadioButtons;
    }

    private Scene getMainAlgorithmScene() {
        Image placeholderImage = new Image("default_image.png");

        imageViewOriginal.setImage(placeholderImage);
        imageViewFirstAlgo.setImage(placeholderImage);
        imageViewSecondAlgo.setImage(placeholderImage);

        imageViewOriginal.setPreserveRatio(true);
        imageViewFirstAlgo.setPreserveRatio(true);
        imageViewSecondAlgo.setPreserveRatio(true);

        DirectoryChooser resultsDirectoryChooser = new DirectoryChooser();

        Text resultsDirectoryTextNode = new Text();
        resultsDirectoryTextNode.setText(resultsDirectory);

        Button chooseDirectoryButton = new Button("Change directory:");
        chooseDirectoryButton.setOnAction(e -> {
            File chosenDirectory = resultsDirectoryChooser.showDialog(window);
            if (chosenDirectory != null) {
                this.resultsDirectory = chosenDirectory.toString();
                resultsDirectoryTextNode.setText(chosenDirectory.toString());
            }

        });

        FileChooser userImageChooser = new FileChooser();
        Button uploadImageButton = new Button("Choose an image");

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = userImageChooser.showOpenDialog(window);

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

                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage, Path.of(resultsDirectory, ImageUtils.getFileBaseName(chosenFile.getName()) + "_median_cut.rii").toString());
                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage2, Path.of(resultsDirectory, ImageUtils.getFileBaseName(chosenFile.getName()) + "_lloyd.rii").toString());

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

                Scene popUpScene = new Scene(vBox, 1500, 400);

                popUpStage.setTitle("Algorithms");
                popUpStage.setScene(popUpScene);
                popUpStage.show();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        ToggleGroup chooseAlgorithmToggleGroup = new ToggleGroup();
        originalImageRadioButton.setToggleGroup(chooseAlgorithmToggleGroup);
        FirstAlgoRadioButton.setToggleGroup(chooseAlgorithmToggleGroup);
        SecondAlgoRadioButton.setToggleGroup(chooseAlgorithmToggleGroup);

        originalImageRadioButton.setSelected(true);

        VBox chooseAlgorithmVBox = new VBox(10);
        chooseAlgorithmVBox.setPadding(new Insets(10));
        chooseAlgorithmVBox.getChildren().addAll(originalImageRadioButton, FirstAlgoRadioButton, SecondAlgoRadioButton);

        HBox lowerHBox = new HBox();
        lowerHBox.setAlignment(Pos.CENTER);
        lowerHBox.setSpacing(50);

        lowerHBox.getChildren().addAll(
                new Label("Choose an algorithm:"),
                chooseAlgorithmVBox,
                ColorPalette.colorPaletteButton(),
                Histogram.histogramButton()
        );

        VBox mainAlgorithmSceneContainer = new VBox();
        mainAlgorithmSceneContainer.setAlignment(Pos.CENTER);
        mainAlgorithmSceneContainer.setPadding(new Insets(10));
        mainAlgorithmSceneContainer.setSpacing(15);

        HBox directoryBox = new HBox();
        directoryBox.setAlignment(Pos.CENTER);
        directoryBox.setSpacing(10);
        directoryBox.getChildren().addAll(chooseDirectoryButton, resultsDirectoryTextNode);

        Button gotoSearchScene = new Button("Search for images");
        gotoSearchScene.setOnAction(e -> {
            window.setScene(searchScene);
        });

        mainAlgorithmSceneContainer.getChildren().addAll(
                directoryBox,
                new Label("Choose how many colors do you want in the new image?"),
                this.getColorRadioButtonsHBox(),
                uploadImageButton,
                lowerHBox,
                gotoSearchScene
        );

        StackPane mainAlgorithmLayout = new StackPane();
        mainAlgorithmLayout.getChildren().addAll(mainAlgorithmSceneContainer);

        return new Scene(mainAlgorithmLayout, 1000, 500);
    }

    private Scene getSearchScene() {
        FileChooser searchImageChooser = new FileChooser();

        Button gotoMainAlgorithmScene = new Button("Go Back");
        gotoMainAlgorithmScene.setOnAction(e -> {
            window.setScene(mainAlgorithmScene);
        });

        Button uploadSearchImage = new Button("Choose an image to search for");

        VBox searchSceneContainer = new VBox();
        searchSceneContainer.setAlignment(Pos.CENTER);
        searchSceneContainer.setSpacing(15);
        searchSceneContainer.getChildren().addAll(uploadSearchImage, gotoMainAlgorithmScene);

        uploadSearchImage.setOnAction(e -> {
            File chosenFile = searchImageChooser.showOpenDialog(window);
            try {
                Searcher.setTarget(chosenFile, 10);
                System.out.print("Searching for images similar to ");
                System.out.print(chosenFile.getName());
                System.out.println();
                File[] foundFiles = Searcher.Search(resultsDirectory);

                System.out.printf("Search done. Found %d files.\n", foundFiles.length);
                for (int i = 0; i < foundFiles.length; i++) {
                    System.out.println(foundFiles[i].getAbsolutePath());

                    IndexedImage imageMatch = IOIndexed.readIndexedImageFromDisk(foundFiles[i].getAbsolutePath());
                    BufferedImage bufferedImageMatch = ImageUtils.PixelsToImage(imageMatch.pixels, imageMatch.width, imageMatch.height, 2);
                    Image nonBufferedImageMatch = ImageUtils.ConvertBufferedImageToImage(bufferedImageMatch);

                    ImageView imageView = new ImageView();

                    imageView.setImage(nonBufferedImageMatch);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(200);

                    searchSceneContainer.getChildren().add(imageView);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        StackPane searchLayout = new StackPane();
        searchLayout.getChildren().addAll(searchSceneContainer);

        return new Scene(searchLayout, 1000, 500);
    }


}