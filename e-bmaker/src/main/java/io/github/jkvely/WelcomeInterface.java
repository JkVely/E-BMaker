package io.github.jkvely;

import io.github.jkvely.view.InterfaceView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WelcomeInterface extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Crear la vista
            InterfaceView view = new InterfaceView();
            
            // Crear la escena
            Scene scene = new Scene(view.getRoot(), 600, 400);
            
            // Configurar la ventana principal
            primaryStage.setTitle("Gestor de Proyectos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            System.out.println("Error al iniciar la aplicación: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}