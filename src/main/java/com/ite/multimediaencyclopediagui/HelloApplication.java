package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.ImageManipulation;
import com.ite.multimediaencyclopediagui.images.MedianCut;
import com.ite.multimediaencyclopediagui.images.Pixel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        Button uploadImageButton = new Button("Choose a photo");

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(stage);
                BufferedImage myPicture = ImageIO.read(chosenFile);
                Pixel[] newImage = MedianCut.Algorithm(ImageManipulation.ImageToMatrix(myPicture), 16);

                ImageIO.write(ImageManipulation.MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));

            } catch (Exception exception) {
                System.out.println(exception);
            }
        });

        StackPane layout = new StackPane();
        layout.getChildren().addAll(uploadImageButton);

        stage.setScene(new Scene(layout, 500, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}