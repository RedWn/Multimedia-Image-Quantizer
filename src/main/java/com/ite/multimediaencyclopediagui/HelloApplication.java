package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.ImageManipulation;
import com.ite.multimediaencyclopediagui.images.MedianCut;
import com.ite.multimediaencyclopediagui.images.Pixel;
import javafx.application.Application;
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
        ImageView imageView1 = new ImageView();
        ImageView imageView2 = new ImageView();


        Image defaultImage = new Image("default_image.png");
        imageView1.setImage(defaultImage);
        imageView2.setImage(defaultImage);
        imageView1.setFitWidth(250);
        imageView1.setFitHeight(250);
        imageView2.setFitWidth(250);
        imageView2.setFitHeight(250);

        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(stage);


                Image image = new Image(chosenFile.toURI().toString());
                imageView1.setImage(image);
                imageView1.setPreserveRatio(true);
                imageView1.setFitWidth(250);
                imageView1.setFitHeight(250);

                BufferedImage myPicture = ImageIO.read(chosenFile);
                Pixel[] newImage = MedianCut.Algorithm(ImageManipulation.ImageToMatrix(myPicture), 16);

                ImageIO.write(ImageManipulation.MatrixToImage(newImage, myPicture), "jpg", new File("new-test.jpg"));

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
        vBox.getChildren().addAll(uploadImageButton, hBox);




        StackPane layout = new StackPane();
        layout.getChildren().addAll(vBox);

        stage.setScene(new Scene(layout, 800, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}