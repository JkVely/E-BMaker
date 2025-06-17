package io.github.jkvely;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Editor extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        // Debug: print root style classes
        System.out.println("Root node style classes: " + root.getStyleClass());
        Scene scene = new Scene(root);
        // Verify CSS resource loading
        URL cssUrl = getClass().getResource("/styles/eva.css");
        if (cssUrl == null) {
            System.err.println("Error: '/styles/eva.css' not found in classpath.");
        } else {
            String css = cssUrl.toExternalForm();
            scene.getStylesheets().add(css);
            System.out.println("Loaded stylesheet: " + css);
        }
        stage.setTitle("E-BMaker");
        stage.setScene(scene);
        stage.show();
        // List all applied stylesheets for debugging
        System.out.println("Active stylesheets: " + scene.getStylesheets());
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
