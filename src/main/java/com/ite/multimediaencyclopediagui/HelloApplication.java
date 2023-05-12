package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.ImageManipulation;
import com.ite.multimediaencyclopediagui.images.MedianCut;
import com.ite.multimediaencyclopediagui.images.Pixel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BufferedImage myPicture = ImageIO.read(new File("test.jpg"));
        Pixel[] newImage = MedianCut.Algorithm(ImageManipulation.ImageToMatrix(myPicture), 16);
        ImageIO.write(ImageManipulation.MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 700);
        stage.setTitle("Multimedia Project");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}