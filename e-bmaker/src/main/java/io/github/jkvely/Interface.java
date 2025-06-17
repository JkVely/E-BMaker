package io.github.jkvely;

import io.github.jkvely.WelcomeInterface.ProjectManagerView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Interface extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Crear la vista
            ProjectManagerView view = new ProjectManagerView();
            
            // Crear la escena
            Scene scene = new Scene(view.getRoot(), 600, 400);
            
            // Configurar la ventana principal
            primaryStage.setTitle("Gestor de Proyectos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}