package io.github.jkvely.WelcomeInterface;

import java.io.IOException;

import javax.print.DocFlavor.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProjectManagerViewModel {
    
    private Button createProjectButton;
    private Button openProjectButton;
    private ProjectModel model;
    
    public ProjectManagerViewModel() {
        model = new ProjectModel();
    }
    
    // Getters y Setters para los botones
    public Button getCreateProjectButton() {
        return createProjectButton;
    }
    
    public void setCreateProjectButton(Button createProjectButton) {
        this.createProjectButton = createProjectButton;
    }
    
    public Button getOpenProjectButton() {
        return openProjectButton;
    }
    
    public void setOpenProjectButton(Button openProjectButton) {
        this.openProjectButton = openProjectButton;
    }
    
    // Métodos para manejar eventos
    public void handleCreateProject() {
        model.setLastAction("Crear Proyecto");

        Stage currentStage = (Stage) createProjectButton.getScene().getWindow();
        currentStage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            // Agrega el estilo si está disponible
            java.net.URL css = getClass().getResource("/styles/eva.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            Stage stage = new Stage();
            stage.setTitle("E-BMaker");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showInfoAlert("Error", "No se pudo cargar la interfaz del otro proyecto.");
        }
    }

    
    public void handleOpenProject() {
        // Actualizar el modelo
        model.setLastAction("Abrir Proyecto");
        
        // Mostrar mensaje (simulación sin backend)
        showInfoAlert("Abrir Proyecto", "Funcionalidad para abrir un proyecto existente.\n\nEsta acción abriría un explorador de archivos para seleccionar un proyecto.");
    }
    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}