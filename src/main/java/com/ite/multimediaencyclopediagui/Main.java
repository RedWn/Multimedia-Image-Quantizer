package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.Algorithms.LloydsAlgorithm;
import com.ite.multimediaencyclopediagui.images.Algorithms.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.IOIndexed;
import com.ite.multimediaencyclopediagui.images.ImageUtils;
import com.ite.multimediaencyclopediagui.images.IndexedImage;
import com.ite.multimediaencyclopediagui.images.Pixel;
import com.ite.multimediaencyclopediagui.search.SearchTask;
import com.ite.multimediaencyclopediagui.search.Searcher;
import javafx.application.Application;
import javafx.concurrent.Task;
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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main extends Application {
    static Stage window;
    static ImageView imageViewOriginal = new ImageView();
    static ImageView imageViewFirstAlgo = new ImageView();
    static ImageView imageViewSecondAlgo = new ImageView();
    static RadioButton colorsSelectedToggle = new RadioButton();
    static RadioButton algorithmSelectedToggle = new RadioButton();

    /**
     * Directory where images are stored after applying the algorithm.
     */
    private String resultsDirectory = "D:\\";

    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private final FileChooser fileChooser = new FileChooser();
    private Label searchStatusLabel;
    private final Scene mainAlgorithmScene = this.getMainAlgorithmScene();
    private final Scene searchScene = this.getSearchScene();
    private ListView<String> folderListView;

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

    private Scene getMainAlgorithmScene() {
        imageViewOriginal.setPreserveRatio(true);
        imageViewFirstAlgo.setPreserveRatio(true);
        imageViewSecondAlgo.setPreserveRatio(true);

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

                // Convert to buffered images
                BufferedImage bufferedQuantizedImage = ImageUtils.PixelsToImage(quantizedPixels, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());
                BufferedImage bufferedQuantizedImage2 = ImageUtils.PixelsToImage(quantizedPixels2, originalPicture.getWidth(), originalPicture.getHeight(), originalPicture.getType());

                // Make Java happy
                Image nonBufferedQuantizedImageToMakeJavaHappy = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage);
                Image nonBufferedQuantizedImageToMakeJavaHappy2 = ImageUtils.ConvertBufferedImageToImage(bufferedQuantizedImage2);

                // Write to disk as indexed images
                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage, Path.of(resultsDirectory, FileUtils.getFileBaseName(chosenFile.getName()) + "_median_cut.rii").toString());
                IOIndexed.convertImageToIndexedAndWriteToDisk(bufferedQuantizedImage2, Path.of(resultsDirectory, FileUtils.getFileBaseName(chosenFile.getName()) + "_lloyd.rii").toString());

                // Show results
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

                Label label1 = new Label("Original image");
                Label label2 = new Label("Median Cut Algorithm");
                Label label3 = new Label("Lloyd's Algorithm");
                HBox hBoxLabels = new HBox(350);
                hBoxLabels.setAlignment(Pos.CENTER);
                hBoxLabels.getChildren().addAll(label1, label2, label3);

                VBox vBox = new VBox(10);
                vBox.getChildren().addAll(hBoxLabels, hBox);

                Scene popUpScene = new Scene(vBox, 1500, 600);

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

        chooseAlgorithmBox.getChildren().addAll(
                new Label("Choose an algorithm:"),
                chooseAlgorithmVBox(),
                ColorPalette.colorPaletteButton(),
                Histogram.histogramButton()
        );

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

        mainAlgorithmBox.getChildren().addAll(
                chooseDirectoryLabel,
                directoryBox,
                new Label("Choose how many colors do you want in the new image?"),
                this.getColorRadioButtonsHBox(),
                uploadImageButton,
                chooseAlgorithmBox,
                gotoSearchScene
        );

        Image backgroundImage = new Image("BG3.jpg", 1000, 500, false, true);
        ImageView backgroundImageView = new ImageView(backgroundImage);

        StackPane sceneRoot = new StackPane();
        sceneRoot.getChildren().addAll(backgroundImageView, mainAlgorithmBox);

        return new Scene(sceneRoot, 1000, 500);
    }

    private Scene getSearchScene() {
        folderListView = new ListView<>();
        folderListView.setMaxSize(600, 100);
        folderListView.scrollTo(10);

        Button gotoMainAlgorithmScene = new Button("Go Back");
        gotoMainAlgorithmScene.setOnAction(e -> window.setScene(mainAlgorithmScene));

        GridPane searchResultsGridPane = new GridPane();
        searchResultsGridPane.setAlignment(Pos.CENTER);
        searchResultsGridPane.setHgap(10);
        searchResultsGridPane.setVgap(10);

        int MAX_GRIDPANE_COLUMNS = 4;
        int MAX_GRIDPANE_ROWS = 4;

        searchStatusLabel = new Label(" ");
        searchStatusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");

        Button uploadSearchImageButton = new Button("Choose an image to search for");
        uploadSearchImageButton.setOnAction(e -> {

            File chosenFile = fileChooser.showOpenDialog(window);
            if (chosenFile == null) return;

            searchResultsGridPane.getChildren().clear();
            searchStatusLabel.setText("Loading...");

            try {
                System.out.println("Searching for images similar to " + chosenFile.getName());

                Searcher.setColorTarget(chosenFile, 10);
                Searcher.setDateTarget(chosenFile);
                Searcher.setSizeTarget(chosenFile);

                int numberOfSearchThreads = folderListView.getItems().size();
                ExecutorService executor = Executors.newFixedThreadPool(numberOfSearchThreads);

                ArrayList<File> foundFiles = new ArrayList<>();

                for (String folder : folderListView.getItems()) {
                    Callable<File[]> callable = new SearchTask(folder);
                    Future<File[]> future = executor.submit(callable);
                    for (File file : future.get()) {
                        foundFiles.add(file);
                    }
                }

                foundFiles.forEach(file -> {
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

                    int col = searchResultsGridPane.getChildren().size() % MAX_GRIDPANE_COLUMNS;
                    int row = searchResultsGridPane.getChildren().size() / MAX_GRIDPANE_ROWS;

                    Text filePathText = new Text(file.getAbsolutePath());
                    box.getChildren().addAll(imageView, filePathText);

                    // SPECIFY COLUMN THEN ROW
                    searchResultsGridPane.add(box, col, row);
                });
                if (foundFiles.size() == 0) {
                    searchStatusLabel.setText("No similar images were found.");
                } else {
                    searchStatusLabel.setText("Search done. Found " + foundFiles.size() + " files.");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        ArrayList<File> chosenSearchDirectories = new ArrayList<>();
        Text chosenSearchDirectoriesTextNode = new Text();

        Button chooseSearchDirectoriesButton = new Button("Select search directories");
        chooseSearchDirectoriesButton.setOnAction(e -> {
            directoryChooser.setTitle("Choose Search Folders");
            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                folderListView.getItems().add(selectedDirectory.getAbsolutePath());
            }
        });

        VBox searchSceneContainer = new VBox();
        searchSceneContainer.setAlignment(Pos.CENTER);
        searchSceneContainer.setSpacing(15);
        searchSceneContainer.getChildren().addAll(chooseSearchDirectoriesButton,
                folderListView,
                chosenSearchDirectoriesTextNode,
                uploadSearchImageButton,
                //getLoadingBox(chosenSearchDirectories),
                searchStatusLabel,
                searchResultsGridPane,
                gotoMainAlgorithmScene);

        ScrollPane searchScrollPane = new ScrollPane();
        searchScrollPane.setFitToWidth(true);
        searchScrollPane.setFitToHeight(true);
        searchScrollPane.setPadding(new Insets(20));
        searchScrollPane.setContent(searchSceneContainer);

        return new Scene(searchScrollPane, 1000, 500);
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

    private VBox getLoadingBox(ArrayList<File> chosenSearchDirectories) {
        ProgressBar pb = new ProgressBar();

        // Create a label to show the status of the task
        Label statusLabel = new Label("Status: ");

        // Create a task that simulates some work
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Update the progress and the message every second
                int numberOfFiles = 0;
                for (int i = 0; i < chosenSearchDirectories.size(); i++) {
                    numberOfFiles += FileUtils.getNumberOfFilesInDirectory(chosenSearchDirectories.get(i).toString());
                }
                while (Searcher.loadingNumber < numberOfFiles) {
                    updateProgress(Searcher.loadingNumber + 1, numberOfFiles);
                    updateMessage("Working... (" + (Searcher.loadingNumber + 1) + "/" + numberOfFiles + ")");
                    Thread.sleep(50);
                }
                // Update the message when the task is done
                updateMessage(" ");
                return null;
            }
        };

        // Bind the progress property of the controls to the progress property of the task
        pb.progressProperty().bind(task.progressProperty());

        // Bind the text property of the label to the message property of the task
        statusLabel.textProperty().bind(task.messageProperty());

        VBox loadingBarVBox = new VBox();
        loadingBarVBox.setSpacing(5);
        loadingBarVBox.setAlignment(Pos.CENTER);
        loadingBarVBox.getChildren().addAll(pb, statusLabel);
        if (searchStatusLabel.toString() != "Loading...") {
            loadingBarVBox.setVisible(true);
        }
        searchStatusLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            // Do something when the label text changes
            if (newValue.equals("Loading...")) {
                loadingBarVBox.setVisible(true);
                new Thread(task).start();
            }
            else {
                loadingBarVBox.setVisible(false);
            }
        });
        return loadingBarVBox;
    }
}