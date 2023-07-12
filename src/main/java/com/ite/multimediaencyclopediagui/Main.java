package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.Algorithms.LloydsAlgorithm;
import com.ite.multimediaencyclopediagui.images.Algorithms.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.Algorithms.OctreeAlgorithm;
import com.ite.multimediaencyclopediagui.images.IOIndexed;
import com.ite.multimediaencyclopediagui.images.ImageUtils;
import com.ite.multimediaencyclopediagui.images.IndexedImage;
import com.ite.multimediaencyclopediagui.images.Pixel;
import com.ite.multimediaencyclopediagui.search.SearchTask;
import com.ite.multimediaencyclopediagui.search.Searcher;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main extends Application {
    static Stage window;
    static File imageToSearchFor;
    static ImageView imageToSearchForImageView = new ImageView();

    static ImageView imageViewOriginal = new ImageView();
    static ImageView imageViewFirstAlgo = new ImageView();
    static ImageView imageViewSecondAlgo = new ImageView();
    static ImageView imageViewThirdAlgo = new ImageView();
    static RadioButton colorsSelectedToggle = new RadioButton();
    static RadioButton algorithmSelectedToggle = new RadioButton();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final FileChooser fileChooser = new FileChooser();
    /**
     * Directory where images are stored after applying the algorithm.
     */
    private String resultsDirectory = "D:\\";
    private final Scene mainAlgorithmScene = this.getMainAlgorithmScene();

    public static void main(String[] args) {
        launch();
    }

    private final Scene searchScene = this.getSearchScene();

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("Multimedia Project");
        window.setMinWidth(1000);
        window.setMinHeight(500);

        stage.setScene(mainAlgorithmScene);
        stage.show();
    }

    private Scene getMainAlgorithmScene() {
        imageViewOriginal.setPreserveRatio(true);
        imageViewFirstAlgo.setPreserveRatio(true);
        imageViewSecondAlgo.setPreserveRatio(true);
        imageViewThirdAlgo.setPreserveRatio(true);

        Text resultsDirectoryTextNode = new Text();
        resultsDirectoryTextNode.setText(resultsDirectory);

        Label chooseDirectoryLabel = new Label("Where to save the result images?");
        Button chooseDirectoryButton = new Button("Choose directory:");
        chooseDirectoryButton.setOnAction(e -> {
            File chosenDirectory = directoryChooser.showDialog(window);
            if (chosenDirectory != null) {
                this.resultsDirectory = chosenDirectory.toString();
                resultsDirectoryTextNode.setText(chosenDirectory.toString());
            }

        });

        Button uploadImageButton = new Button("Choose an image");
        uploadImageButton.setOnAction(e -> {
            try {
                File chosenFile = fileChooser.showOpenDialog(window);
                if (chosenFile == null) return;

                Image image = new Image(chosenFile.toURI().toString());
                imageViewOriginal.setImage(image);

                BufferedImage originalPicture = ImageIO.read(chosenFile);
                Pixel[] originalPicturePixels = ImageUtils.ImageToPixels(originalPicture);

                // Apply algorithms
                Pixel[] quantizedPixels = MedianCutAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.parseInt(colorsSelectedToggle.getText()));
                Pixel[] quantizedPixels2 = LloydsAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.parseInt(colorsSelectedToggle.getText()));
                Pixel[] quantizedPixels3 = OctreeAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.parseInt(colorsSelectedToggle.getText()));

                // Convert to buffered images
                BufferedImage bufferedQuantizedImage = ImageUtils.PixelsToImage(quantizedPixels, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());
                BufferedImage bufferedQuantizedImage2 = ImageUtils.PixelsToImage(quantizedPixels2, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());
                BufferedImage bufferedQuantizedImage3 = ImageUtils.PixelsToImage(quantizedPixels3, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());

                // Make Java happy
                Image nonBufferedQuantizedImageToMakeJavaHappy = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage);
                Image nonBufferedQuantizedImageToMakeJavaHappy2 = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage2);
                Image nonBufferedQuantizedImageToMakeJavaHappy3 = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage3);

                // Write to disk as indexed images
                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage, Path.of(resultsDirectory, FileUtils.getFileBaseName(chosenFile.getName()) + "_median_cut.rii").toString());
                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage2, Path.of(resultsDirectory, FileUtils.getFileBaseName(chosenFile.getName()) + "_lloyd.rii").toString());
                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage3, Path.of(resultsDirectory, FileUtils.getFileBaseName(chosenFile.getName()) + "_octree.rii").toString());

                // Show results
                imageViewFirstAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy);
                imageViewSecondAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy2);
                imageViewThirdAlgo.setImage(nonBufferedQuantizedImageToMakeJavaHappy3);

                Stage popUpStage = new Stage();
                popUpStage.initOwner(window);
                HBox hBoxAlgorithmImages = new HBox(10);

                imageViewOriginal.setFitWidth(500);
                imageViewFirstAlgo.setFitWidth(500);
                imageViewSecondAlgo.setFitWidth(500);
                imageViewThirdAlgo.setFitWidth(500);
                imageViewOriginal.setFitHeight(360);
                imageViewFirstAlgo.setFitHeight(360);
                imageViewSecondAlgo.setFitHeight(360);
                imageViewThirdAlgo.setFitHeight(360);

                hBoxAlgorithmImages.getChildren().addAll(imageViewFirstAlgo, imageViewSecondAlgo, imageViewThirdAlgo);
                hBoxAlgorithmImages.setAlignment(Pos.CENTER);

                Label labelOriginal = new Label("Original image");
                Label labelFirst = new Label("Median Cut Algorithm");
                Label labelSecond = new Label("Lloyd's Algorithm");
                Label labelThird = new Label("Octree Algorithm");
                HBox hBoxLabels = new HBox(340);
                hBoxLabels.setAlignment(Pos.CENTER);
                hBoxLabels.getChildren().addAll(labelFirst, labelSecond, labelThird);

                VBox vBox = new VBox(5);
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(labelOriginal, imageViewOriginal, hBoxLabels, hBoxAlgorithmImages);

                Scene popUpScene = new Scene(vBox, 1500, 780);
                popUpStage.setTitle("Algorithms");
                popUpStage.setScene(popUpScene);
                popUpStage.show();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        HBox chooseAlgorithmBox = new HBox();
        chooseAlgorithmBox.setAlignment(Pos.CENTER);
        chooseAlgorithmBox.setSpacing(50);

        chooseAlgorithmBox.getChildren().addAll(new Label("Choose an algorithm:"), chooseAlgorithmVBox(), ColorPalette.colorPaletteButton(), Histogram.histogramButton());

        VBox mainAlgorithmBox = new VBox();
        mainAlgorithmBox.setAlignment(Pos.CENTER);
        mainAlgorithmBox.setPadding(new Insets(10));
        mainAlgorithmBox.setSpacing(15);

        HBox directoryBox = new HBox();
        directoryBox.setAlignment(Pos.CENTER);
        directoryBox.setSpacing(10);
        directoryBox.getChildren().addAll(chooseDirectoryButton, resultsDirectoryTextNode);

        Button gotoSearchScene = new Button("Search for images");
        gotoSearchScene.setOnAction(e -> window.setScene(searchScene));

        mainAlgorithmBox.getChildren().addAll(chooseDirectoryLabel, directoryBox, new Label("Choose how many colors do you want in the new image?"), this.getColorRadioButtonsHBox(), uploadImageButton, chooseAlgorithmBox, gotoSearchScene);

        Image backgroundImage = new Image("BG3.jpg", 1000, 500, false, true);
        ImageView backgroundImageView = new ImageView(backgroundImage);

        StackPane sceneRoot = new StackPane();
        sceneRoot.getChildren().addAll(backgroundImageView, mainAlgorithmBox);

        return new Scene(sceneRoot, 1000, 500);
    }

    private Scene getSearchScene() {

        imageToSearchForImageView.setPreserveRatio(true);
        imageToSearchForImageView.setFitHeight(200);
        imageToSearchForImageView.setFitHeight(200);


        // Choose directories list and button
        ListView<String> folderListView = new ListView<>();

        folderListView.getItems().add("D:\\Tests\\small_tests");
        folderListView.getItems().add("D:\\Tests\\old_small_tests");
        folderListView.getItems().add("D:\\Tests\\big_tests");

        folderListView.setMaxSize(600, 100);
        folderListView.scrollTo(10);

        Button chooseSearchDirectoriesButton = new Button("Select search directories");
        chooseSearchDirectoriesButton.setOnAction(e -> {
            directoryChooser.setTitle("Choose Search Folders");
            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                folderListView.getItems().add(selectedDirectory.getAbsolutePath());
            }
        });

        // Search status label
        Label searchStatusLabel = new Label(" ");
        searchStatusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");

        // Search results grid
        GridPane searchResultsGridPane = new GridPane();
        searchResultsGridPane.setAlignment(Pos.CENTER);
        searchResultsGridPane.setHgap(10);
        searchResultsGridPane.setVgap(10);

        // Search options
        Text searchOptionsHeadline = new Text("Search options: ");
        searchOptionsHeadline.setStyle("-fx-font-weight: 700; -fx-font-size: 20px");

        CheckBox searchByColor = new CheckBox("Search by color");
        CheckBox searchByDate = new CheckBox("Search by date");
        CheckBox searchBySize = new CheckBox("Search by size");

        Label daysLabel = new Label("Date");
        daysLabel.setStyle("-fx-font-weight: 600;");

        TextField daysTextField = new TextField();
        daysTextField.setMaxWidth(200);
        daysTextField.setText("5");

        VBox searchOptions = new VBox();
        searchOptions.setAlignment(Pos.CENTER);
        searchOptions.setSpacing(10);
        searchOptions.getChildren().addAll(searchOptionsHeadline, searchByColor, searchByDate, searchBySize, daysLabel, daysTextField);

        Button compressImageButton = new Button("Compress image");

        TextField widthAfterCompressionTextField = new TextField("Width after compression");
        TextField heightAfterCompressionTextField = new TextField("Height after compression");

        compressImageButton.setOnAction(e -> {
            if (imageToSearchFor == null) return;

            BufferedImage originalImage = null;
            try {
                originalImage = ImageIO.read(imageToSearchFor);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

//            int COMPRESSED_WIDTH = 100;
            // Compress the image to the desired width while preserving the aspect ratio
//            double scalingFactor = (double) COMPRESSED_WIDTH / originalImage.getWidth();
//            int compressedHeight = (int) (originalImage.getHeight() * scalingFactor);

            int compressedWidth = Integer.parseInt(widthAfterCompressionTextField.getText());
            int compressedHeight = Integer.parseInt(heightAfterCompressionTextField.getText());

            BufferedImage compressedImage = new BufferedImage(compressedWidth, compressedHeight, BufferedImage.TYPE_INT_RGB);
            compressedImage.createGraphics().drawImage(originalImage.getScaledInstance(compressedWidth, compressedHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

            try {
                File newImage = new File("D:/Tests/bla.test");

                ImageIO.write(
                        compressedImage,
                        FileUtils.getFileExtension(imageToSearchFor.getName()),
                        newImage
                );

                imageToSearchFor = newImage;

                System.out.println("Writing done");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            imageToSearchForImageView.setImage(new Image(imageToSearchFor.toURI().toString()));

            System.out.println("🚀 Done compressing!");
        });

        Button cropImageButton = new Button("Crop image");

        cropImageButton.setOnAction(event -> {
            if (imageToSearchFor != null) {
                Crop imageCropper = new Crop();
                imageCropper.selectImage(imageToSearchFor);
            }
        });

        Button uploadSearchImageButton = new Button("Upload image");
        uploadSearchImageButton.setOnAction(e -> {
            imageToSearchFor = fileChooser.showOpenDialog(window);
            if (imageToSearchFor == null) return;

            imageToSearchForImageView.setImage(new Image(imageToSearchFor.toURI().toString()));
        });

        Button searchButton = new Button("Perform search");
        searchButton.setOnAction(e -> {
            if (imageToSearchFor == null) return;

            System.out.println("Searching...");

            searchResultsGridPane.getChildren().clear();
            searchStatusLabel.setText("Loading...");

            try {
                System.out.println("Searching for images similar to " + imageToSearchFor.getName());

                Searcher.setColorTarget(imageToSearchFor, 2);
                Searcher.setDateTarget(imageToSearchFor);
                Searcher.setSizeTarget(imageToSearchFor);

                Searcher.nDays = Integer.parseInt(daysTextField.getText());

                int numberOfSearchThreads = folderListView.getItems().size();
                ExecutorService executor = Executors.newFixedThreadPool(numberOfSearchThreads);

                ArrayList<File> searchResults = new ArrayList<>();

                for (String folder : folderListView.getItems()) {
                    Callable<File[]> callable = new SearchTask(folder, searchByColor.isSelected(), searchByDate.isSelected(), searchBySize.isSelected());
                    Future<File[]> future = executor.submit(callable);
                    for (File file : future.get()) {
                        searchResults.add(file);
                    }
                }

                System.out.println("Results size: " + searchResults.size());

                searchResults.forEach(file -> {
                    System.out.println(file.getAbsolutePath());

                    IndexedImage imageMatch = null;
                    try {
                        imageMatch = IOIndexed.readIndexedImageFromDisk(file.getAbsolutePath());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    BufferedImage bufferedImageMatch = ImageUtils.PixelsToImage(imageMatch.pixels, imageMatch.width, imageMatch.height, 2);
                    Image nonBufferedImageMatch = ImageUtils.ConvertBufferedImageToImage(bufferedImageMatch);

                    VBox box = new VBox();
                    box.setAlignment(Pos.BOTTOM_CENTER);
                    box.setSpacing(5);

                    ImageView imageView = new ImageView();
                    imageView.setImage(nonBufferedImageMatch);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(200);

                    int MAX_GRIDPANE_COLUMNS = 4;
                    int MAX_GRIDPANE_ROWS = 4;

                    int col = searchResultsGridPane.getChildren().size() % MAX_GRIDPANE_COLUMNS;
                    int row = searchResultsGridPane.getChildren().size() / MAX_GRIDPANE_ROWS;

                    Text filePathText = new Text(file.getAbsolutePath());
                    box.getChildren().addAll(imageView, filePathText);

                    // SPECIFY COLUMN THEN ROW
                    searchResultsGridPane.add(box, col, row);
                });
                if (searchResults.size() == 0) {
                    searchStatusLabel.setText("No similar images were found.");
                } else {
                    searchStatusLabel.setText("Search done. Found " + searchResults.size() + " files.");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        Button gotoMainAlgorithmScene = new Button("Go Back");
        gotoMainAlgorithmScene.setOnAction(e -> window.setScene(mainAlgorithmScene));

        VBox compressionBox = new VBox();
        compressionBox.getChildren().addAll(widthAfterCompressionTextField, heightAfterCompressionTextField);

        HBox actionButtons = new HBox();
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setSpacing(15);
        actionButtons.getChildren().addAll(compressionBox, uploadSearchImageButton, cropImageButton, compressImageButton, searchButton);

        // Final box
        VBox searchSceneContainer = new VBox();
        searchSceneContainer.setAlignment(Pos.CENTER);
        searchSceneContainer.setSpacing(15);
        searchSceneContainer.getChildren().addAll(imageToSearchForImageView, searchOptions, chooseSearchDirectoriesButton, folderListView, actionButtons,

                //getLoadingBox(chosenSearchDirectories),
                searchStatusLabel, searchResultsGridPane, gotoMainAlgorithmScene);

        // Final container
        ScrollPane searchScrollPane = new ScrollPane();
        searchScrollPane.setFitToWidth(true);
        searchScrollPane.setFitToHeight(true);
        searchScrollPane.setPadding(new Insets(20));
        searchScrollPane.setContent(searchSceneContainer);

        return new Scene(searchScrollPane, 1000, 700);
    }

    VBox chooseAlgorithmVBox() {
        ToggleGroup chooseAlgorithmToggleGroup = new ToggleGroup();
        RadioButton originalImageRadioButton = new RadioButton("Original image");
        RadioButton FirstAlgoRadioButton = new RadioButton("Median Cut Algorithm");
        RadioButton SecondAlgoRadioButton = new RadioButton("Lloyd's Algorithm");

        originalImageRadioButton.setToggleGroup(chooseAlgorithmToggleGroup);
        FirstAlgoRadioButton.setToggleGroup(chooseAlgorithmToggleGroup);
        SecondAlgoRadioButton.setToggleGroup(chooseAlgorithmToggleGroup);

        // Add a listener to the selected toggle property
        chooseAlgorithmToggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            // Get the selected radio button
            algorithmSelectedToggle = (RadioButton) chooseAlgorithmToggleGroup.getSelectedToggle();
        });

        originalImageRadioButton.setSelected(true);

        VBox chooseAlgorithmVBox = new VBox(10);
        chooseAlgorithmVBox.setPadding(new Insets(10));
        chooseAlgorithmVBox.getChildren().addAll(originalImageRadioButton, FirstAlgoRadioButton, SecondAlgoRadioButton);

        return chooseAlgorithmVBox;
    }

    private HBox getColorRadioButtonsHBox() {
        RadioButton twoColorsRadioButton = new RadioButton("2");
        RadioButton fourColorsRadioButton = new RadioButton("4");
        RadioButton eightColorsRadioButton = new RadioButton("8");
        RadioButton sixteenColorsRadioButton = new RadioButton("16");
        RadioButton thirtyTwoColorsRadioButton = new RadioButton("32");
        RadioButton sixtyFourColorsRadioButton = new RadioButton("64");
        RadioButton oneTwoEightColorsRadioButton = new RadioButton("128");
        RadioButton twoFiveSixColorsRadioButton = new RadioButton("256");

        ToggleGroup colorsToggleGroupRadioButtons = new ToggleGroup();

        // Add a listener to the selected toggle property
        colorsToggleGroupRadioButtons.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
            // Get the selected radio button
            colorsSelectedToggle = (RadioButton) colorsToggleGroupRadioButtons.getSelectedToggle();
        });
        eightColorsRadioButton.setSelected(true);

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
        hBoxColorsRadioButtons.getChildren().addAll(twoColorsRadioButton, fourColorsRadioButton, eightColorsRadioButton, sixteenColorsRadioButton, thirtyTwoColorsRadioButton, sixtyFourColorsRadioButton, oneTwoEightColorsRadioButton, twoFiveSixColorsRadioButton);

        return hBoxColorsRadioButtons;
    }

//    private VBox getLoadingBox(ArrayList<File> chosenSearchDirectories) {
//        ProgressBar pb = new ProgressBar();
//
//        // Create a label to show the status of the task
//        Label statusLabel = new Label("Status: ");
//
//        // Create a task that simulates some work
//        Task<Void> task = new Task<>() {
//            @Override
//            protected Void call() throws Exception {
//                // Update the progress and the message every second
//                int numberOfFiles = 0;
//                for (int i = 0; i < chosenSearchDirectories.size(); i++) {
//                    numberOfFiles += FileUtils.getNumberOfFilesInDirectory(chosenSearchDirectories.get(i).toString());
//                }
//                while (Searcher.loadingNumber < numberOfFiles) {
//                    updateProgress(Searcher.loadingNumber + 1, numberOfFiles);
//                    updateMessage("Working... (" + (Searcher.loadingNumber + 1) + "/" + numberOfFiles + ")");
//                    Thread.sleep(50);
//                }
//                // Update the message when the task is done
//                updateMessage(" ");
//                return null;
//            }
//        };
//
//        // Bind the progress property of the controls to the progress property of the task
//        pb.progressProperty().bind(task.progressProperty());
//
//        // Bind the text property of the label to the message property of the task
//        statusLabel.textProperty().bind(task.messageProperty());
//
//        VBox loadingBarVBox = new VBox();
//        loadingBarVBox.setSpacing(5);
//        loadingBarVBox.setAlignment(Pos.CENTER);
//        loadingBarVBox.getChildren().addAll(pb, statusLabel);
//        if (searchStatusLabel.toString() != "Loading...") {
//            loadingBarVBox.setVisible(true);
//        }
//        searchStatusLabel.textProperty().addListener((observable, oldValue, newValue) -> {
//            // Do something when the label text changes
//            if (newValue.equals("Loading...")) {
//                loadingBarVBox.setVisible(true);
//                new Thread(task).start();
//            } else {
//                loadingBarVBox.setVisible(false);
//            }
//        });
//        return loadingBarVBox;
//    }


}