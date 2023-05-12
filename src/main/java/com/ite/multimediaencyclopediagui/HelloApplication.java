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

        // Before + After image views
        ImageView imageView1 = new ImageView();
        ImageView imageView2 = new ImageView();

        imageView1.setImage(placeholderImage);
        imageView2.setImage(placeholderImage);

        imageView1.setFitWidth(250);
        imageView1.setFitHeight(250);

        imageView2.setFitWidth(250);
        imageView2.setFitHeight(250);

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
                imageView1.setImage(image);
                imageView1.setPreserveRatio(true);
                imageView1.setFitWidth(250);
                imageView1.setFitHeight(250);

                BufferedImage originalPicture = ImageIO.read(chosenFile);
                Pixel[] originalPicturePixels = ImageUtils.ImageToPixels(originalPicture);
                Pixel[] quantizedPixels = MedianCutAlgorithm.GetQuantizedPixels(originalPicturePixels, 16);

                BufferedImage bufferedQuantizedImage = ImageUtils.PixelsToImage(quantizedPixels, originalPicture);
                Image nonBufferedQuantizedImageToMakeJavaHappy = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage);

                String pathname = Path.of(resultsDirectory.getValue(), "new-test.jpg").toString();
                ImageIO.write(bufferedQuantizedImage, "jpg", new File(pathname));

                imageView2.setImage(nonBufferedQuantizedImageToMakeJavaHappy);
                imageView1.setPreserveRatio(true);
                imageView1.setFitWidth(250);
                imageView1.setFitHeight(250);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        HBox hBox = new HBox();
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        separator.setPrefHeight(80);
        separator.setValignment(VPos.CENTER);
        separator.setPadding(new Insets(10));

        Label label1 = new Label("Before");
        Label label2 = new Label("After");
        VBox imageVBox1 = new VBox();
        VBox imageVBox2 = new VBox();
        imageVBox1.setAlignment(Pos.CENTER);
        imageVBox2.setAlignment(Pos.CENTER);
        imageVBox1.getChildren().addAll(label1, imageView1);
        imageVBox2.getChildren().addAll(label2, imageView2);

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(imageVBox1, separator, imageVBox2);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(20);
        vBox.getChildren().addAll(chooseDirectoryButton, resultsDirectoryTextNode, uploadImageButton, hBox);

        StackPane layout = new StackPane();
        layout.getChildren().addAll(vBox);

        stage.setScene(new Scene(layout, 800, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}