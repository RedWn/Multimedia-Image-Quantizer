package com.ite.multimediaencyclopediagui;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.ite.multimediaencyclopediagui.Main.*;

public class Crop {
    private static ImageView beforeCropimageView = new ImageView();
    private static Rectangle selectionRect;
    private static Stage cropStage;
    private static Image croppedImage;

    public void selectImage(File imageFile) {
        if (imageFile != null) {
            try {

                Image image = new Image(new FileInputStream(imageFile));
                System.out.println("Test Here4");
                beforeCropimageView.setImage(image);


                // Display image size in a pop-up dialog
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Image Size");
                alert.setHeaderText(null);
                alert.setContentText("Image Width: " + image.getWidth() + "\nImage Height: " + image.getHeight());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();

                // Set up cropping stage
                cropStage = new Stage();
                cropStage.initModality(Modality.APPLICATION_MODAL);
                cropStage.setTitle("Crop Image");

                Pane cropPane = new Pane();
                ImageView cropImageView = new ImageView();
                cropImageView.setImage(image);
                cropPane.getChildren().add(cropImageView);

                selectionRect = new Rectangle();
                selectionRect.setFill(null);
                selectionRect.setStroke(javafx.scene.paint.Color.RED);
                cropPane.getChildren().add(selectionRect);

                Scene cropScene = new Scene(cropPane, image.getWidth(), image.getHeight());
                cropStage.setScene(cropScene);
                cropStage.show();

                cropImageView.setOnMousePressed(event -> {
                    selectionRect.setX(event.getX());
                    selectionRect.setY(event.getY());
                    selectionRect.setWidth(0);
                    selectionRect.setHeight(0);
                });

                cropImageView.setOnMouseDragged(event -> {
                    selectionRect.setWidth(Math.abs(event.getX() - selectionRect.getX()));
                    selectionRect.setHeight(Math.abs(event.getY() - selectionRect.getY()));
                    selectionRect.setX(Math.min(event.getX(), selectionRect.getX()));
                    selectionRect.setY(Math.min(event.getY(), selectionRect.getY()));
                });

                cropImageView.setOnMouseReleased(event -> {
                    Rectangle2D croppedRect = new Rectangle2D(
                            selectionRect.getX(), selectionRect.getY(),
                            selectionRect.getWidth(), selectionRect.getHeight());

                    croppedImage = cropImage(image, croppedRect);
                    cropStage.close();

                    imageToSearchFor = saveCroppedImage(window);
                    Main.imageToSearchForImageView.setImage(new Image(imageToSearchFor.toURI().toString()));

                });
                System.out.println("Test Herehsh");
                //return croppedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //  return null;
    }

    private Image cropImage(Image image, Rectangle2D cropArea) {
        int x = (int) cropArea.getMinX();
        int y = (int) cropArea.getMinY();
        int width = (int) cropArea.getWidth();
        int height = (int) cropArea.getHeight();

        javafx.scene.image.PixelReader pixelReader = image.getPixelReader();
        javafx.scene.image.WritableImage croppedImage = new javafx.scene.image.WritableImage(width, height);
        javafx.scene.image.PixelWriter pixelWriter = croppedImage.getPixelWriter();

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                javafx.scene.paint.Color color = pixelReader.getColor(x + col, y + row);
                pixelWriter.setColor(col, row, color);
            }
        }

        return croppedImage;
    }

    private  File saveCroppedImage(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        File outputFile = fileChooser.showSaveDialog(primaryStage);
        if (outputFile != null) {
            String fileExtension = FileUtils.getFileExtension(outputFile.getName());
            try {
                ImageIO.write(
                        javafx.embed.swing.SwingFXUtils.fromFXImage(croppedImage, null),
                        fileExtension,
                        outputFile);
                showSaveSuccessDialog();
                return outputFile;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private  void showSaveSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Success");
        alert.setHeaderText(null);
        alert.setContentText("Cropped image saved successfully!");
        alert.showAndWait();
    }

}
