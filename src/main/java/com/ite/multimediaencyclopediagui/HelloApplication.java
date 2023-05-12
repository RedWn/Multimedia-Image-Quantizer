package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.ImageManipulation;
import com.ite.multimediaencyclopediagui.images.MedianCut;
import com.ite.multimediaencyclopediagui.images.Pixel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class HelloApplication extends Application {
    Stage window;
    @Override
    public void start(Stage stage) {

        window = stage;
        window.setTitle("Multimedia Project");

        FileChooser fileChooser = new FileChooser();
        Button uploadImageButton = new Button("Choose a photo");

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(stage);
                BufferedImage myPicture = ImageIO.read(chosenFile);
                Pixel[] newImage = MedianCut.Algorithm(ImageManipulation.ImageToMatrix(myPicture), 16);

                ImageIO.write(ImageManipulation.MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });


        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(uploadImageButton);



        StackPane layout = new StackPane();
        layout.getChildren().addAll(vBox);

        stage.setScene(new Scene(layout, 500, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}