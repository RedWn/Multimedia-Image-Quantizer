package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.ImageUtils;
import com.ite.multimediaencyclopediagui.images.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.Pixel;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
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
    /**
     * Directory where images are stored after applying the algorithm.
     */
    private SimpleStringProperty resultsDirectory = new SimpleStringProperty();
    Stage window;
    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("Multimedia Project");

        Image placeholderImage = new Image("default_image.png");

        ImageView imageViewOriginal = new ImageView();
        ImageView imageViewFirstAlgo = new ImageView();
        ImageView imageViewSecondAlgo = new ImageView();

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
            resultsDirectoryTextNode.setText("Selected directory: " + chosenDirectory.toString());
        });

        FileChooser fileChooser = new FileChooser();
        Button uploadImageButton = new Button("Choose a photo");

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(stage);
                System.out.println(resultsDirectory.getValue());

                Image image = new Image(chosenFile.toURI().toString());
                imageViewOriginal.setImage(image);

                BufferedImage originalPicture = ImageIO.read(chosenFile);
                Pixel[] originalPicturePixels = ImageUtils.ImageToPixels(originalPicture);
                Pixel[] quantizedPixels = MedianCutAlgorithm.GetQuantizedPixels(originalPicturePixels, 16);

                BufferedImage bufferedQuantizedImage = ImageUtils.PixelsToImage(quantizedPixels, originalPicture);
                Image nonBufferedQuantizedImageToMakeJavaHappy = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage);

                String pathname = Path.of(resultsDirectory.getValue(), "new-test.jpg").toString();
                ImageIO.write(bufferedQuantizedImage, "jpg", new File(pathname));

                imageViewFirstAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy);
                imageViewSecondAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy);

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

        HBox imagesContainer = new HBox();
        imagesContainer.setAlignment(Pos.CENTER);
        imagesContainer.getChildren().addAll(imageVBox1, separator1, imageVBox2, separator2, imageVBox3);

        VBox appContainer = new VBox();
        appContainer.setAlignment(Pos.CENTER);
        appContainer.setPadding(new Insets(10));
        appContainer.setSpacing(20);
        appContainer.getChildren().addAll(chooseDirectoryButton, resultsDirectoryTextNode, uploadImageButton, imagesContainer);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(appContainer);

        stage.setScene(new Scene(layout, 800, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}