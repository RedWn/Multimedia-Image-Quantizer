module com.ite.multimediaencyclopediagui {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.ite.multimediaencyclopediagui to javafx.fxml;
    exports com.ite.multimediaencyclopediagui;
}