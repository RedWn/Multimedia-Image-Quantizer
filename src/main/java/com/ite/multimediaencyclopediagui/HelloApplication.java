package com.ite.multimediaencyclopediagui;

import com.ite.multimediaencyclopediagui.images.Algorithms.LloydsAlgorithm;
import com.ite.multimediaencyclopediagui.images.Algorithms.MedianCutAlgorithm;
import com.ite.multimediaencyclopediagui.images.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Vector;


public class HelloApplication extends Application {
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

    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private FileChooser fileChooser = new FileChooser();
    private Label searchResultsLabel;
    private final Scene mainAlgorithmScene = this.getMainAlgorithmScene();
    private final Scene searchScene = this.getSearchScene();
    private ListView<String> folderListView;
    private void browseFolders() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Search Folders");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            folderListView.getItems().add(selectedDirectory.getAbsolutePath());
        }
    }

    private ObservableList<String> search() {
        // Retrieve the selected folders
        ObservableList<String> selectedFolders = folderListView.getItems();

        // Perform search operation on the selected folders
        for (String folder : selectedFolders) {
            System.out.println("Searching in folder: " + folder);
            // Perform your search logic here
        }
        return selectedFolders;
    }

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
        Image placeholderImage = new Image("default_image.png");

        imageViewOriginal.setImage(placeholderImage);
        imageViewFirstAlgo.setImage(placeholderImage);
        imageViewSecondAlgo.setImage(placeholderImage);

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

                Pixel[] quantizedPixels = MedianCutAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.valueOf(colorsSelectedToggle.getText()));
                Pixel[] quantizedPixels2 = LloydsAlgorithm.GetQuantizedPixels(originalPicturePixels, Integer.valueOf(colorsSelectedToggle.getText()));

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

        HBox lowerHBox = new HBox();
        lowerHBox.setAlignment(Pos.CENTER);
        lowerHBox.setSpacing(50);

        lowerHBox.getChildren().addAll(
                new Label("Choose an algorithm:"),
                chooseAlgorithmVBox(),
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
                chooseDirectoryLabel,
                directoryBox,
                new Label("Choose how many colors do you want in the new image?"),
                this.getColorRadioButtonsHBox(),
                uploadImageButton,
                lowerHBox,
                gotoSearchScene
        );

        Image bg = new Image("BG3.jpg", 1000, 500, false, true);
        ImageView imageView = new ImageView(bg);

        StackPane root = new StackPane();
        root.getChildren().addAll(imageView, mainAlgorithmSceneContainer);

        return new Scene(root, 1000, 500);
    }

    private Scene getSearchScene() {
        folderListView = new ListView<>();
        folderListView.setMaxSize(600,100);
        folderListView.scrollTo(10);
        FileChooser searchImageChooser = new FileChooser();

        Button gotoMainAlgorithmScene = new Button("Go Back");
        gotoMainAlgorithmScene.setOnAction(e -> {
            window.setScene(mainAlgorithmScene);
        });

        Button uploadSearchImageButton = new Button("Choose an image to search for");

        GridPane searchResultsGridPane = new GridPane();
        searchResultsGridPane.setAlignment(Pos.CENTER);
        searchResultsGridPane.setHgap(10);
        searchResultsGridPane.setVgap(10);

        int MAX_GRIDPANE_COLUMNS = 4;
        int MAX_GRIDPANE_ROWS = 4;

        searchResultsLabel = new Label();
        searchResultsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 700;");

        uploadSearchImageButton.setOnAction(e ->
        {

            File chosenFile = searchImageChooser.showOpenDialog(window);
            if (chosenFile == null) return;
            searchResultsLabel.setText("Loading...");

            try {
                search();
                Searcher.setTarget(chosenFile, 10);
                System.out.print("Searching for images similar to ");
                System.out.print(chosenFile.getName());
                System.out.println();
                Vector<File[]> foundFiles = new Vector();
                for (String folder : folderListView.getItems()) {
                    foundFiles.add(Searcher.Search(folder));
                    // Perform your search logic here
                }


                if (foundFiles.size() == 0) {
                    searchResultsLabel.setText("No similar images were found.");
                } else {
                    searchResultsLabel.setText("Search done. Found " + foundFiles.size() + " files.");
                }

                for (int i = 0; i < foundFiles.size(); i++) {
                    for (int j=0; j<foundFiles.elementAt(i).length; j++) {


                        System.out.println(foundFiles.elementAt(i)[j].getAbsolutePath());


                        IndexedImage imageMatch = IOIndexed.readIndexedImageFromDisk(foundFiles.elementAt(i)[j].getAbsolutePath());
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

                        Text filePathText = new Text(foundFiles.elementAt(i)[j].getAbsolutePath());
                        box.getChildren().addAll(imageView, filePathText);

                        // SPECIFY COLUMN THEN ROW
                        searchResultsGridPane.add(box, col, row);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Vector<File> chosenSearchDirectories = new Vector();
        Text chosenSearchDirectoriesTextNode = new Text();

        Button chooseSearchDirectoriesButton = new Button("Select search directories");
        chooseSearchDirectoriesButton.setOnAction(e -> browseFolders()/*{
            File searchDirFile = directoryChooser.showDialog(window);
            if (searchDirFile == null) return;

            chosenSearchDirectories.add(searchDirFile);
            StringBuilder chosenSearchDirectoriesString = new StringBuilder();
            chosenSearchDirectories.forEach(directory -> {
                chosenSearchDirectoriesString
                        .append(directory)
                        .append(" \n");
            });

            chosenSearchDirectoriesTextNode.setText(chosenSearchDirectoriesString.toString());
        }*/);

        VBox searchSceneContainer = new VBox();
        searchSceneContainer.setAlignment(Pos.CENTER);
        searchSceneContainer.setSpacing(15);
        searchSceneContainer.getChildren().addAll(chooseSearchDirectoriesButton, folderListView, chosenSearchDirectoriesTextNode, uploadSearchImageButton, loading(chosenSearchDirectories), searchResultsLabel, searchResultsGridPane, gotoMainAlgorithmScene);

        ScrollPane searchScrollPane = new ScrollPane();
        searchScrollPane.setFitToWidth(true);
        searchScrollPane.setFitToHeight(true);
        searchScrollPane.setPadding(new Insets(20));
        searchScrollPane.setContent(searchSceneContainer);

        return new Scene(searchScrollPane, 1000, 500);
    }
    VBox chooseAlgorithmVBox(){
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
    private VBox loading(Vector<File> chosenSearchDirectories){
        // Create a progress bar and a progress indicator
        ProgressBar pb = new ProgressBar();
        ProgressIndicator pi = new ProgressIndicator();

        // Create a label to show the status of the task
        Label statusLabel = new Label("Status: ");

        // Create a task that simulates some work
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Update the progress and the message every second
                int numberOfFiles = 0;
                for(int i = 0 ; i < chosenSearchDirectories.size() ; i++){
                    numberOfFiles += numberOfFilesInDir(chosenSearchDirectories.elementAt(i).toString());
                }
                while(Searcher.loadingNumber < numberOfFiles) {
                    updateProgress(Searcher.loadingNumber + 1, numberOfFiles);
                    updateMessage("Working... (" + (Searcher.loadingNumber + 1) + "/" + numberOfFiles + ")");
                    Thread.sleep(500);
                }
                // Update the message when the task is done
                updateMessage("Done!");
                return null;
            }
        };

        // Bind the progress property of the controls to the progress property of the task
        pb.progressProperty().bind(task.progressProperty());
        pi.progressProperty().bind(task.progressProperty());

        // Bind the text property of the label to the message property of the task
        statusLabel.textProperty().bind(task.messageProperty());

        HBox hb = new HBox();
        hb.setSpacing(10);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pb, pi);

        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(hb, statusLabel);
        if(searchResultsLabel.toString() != "Loading..."){
            vb.setVisible(false);
        }
        searchResultsLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            // Do something when the label text changes
            if(newValue == "Loading...") {
                new Thread(task).start();
                vb.setVisible(true);
            }
        });
        return vb;
    }
    int numberOfFilesInDir(String dir){
        // Create a File object for the directory
        File fileDir = new File(dir);

        // Get an array of File objects for the files and directories in the directory
        File[] filesInDir = fileDir.listFiles();

        // Initialize a counter for the number of files
        int numFiles = 0;

        // Loop through the array and increment the counter if it is a file
        for (File file : filesInDir) {
            if (file.isFile()) {
                numFiles++;
            }
        }
        return numFiles;
    }
}