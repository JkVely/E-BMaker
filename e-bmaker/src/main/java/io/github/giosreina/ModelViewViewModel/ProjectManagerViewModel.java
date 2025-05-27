package io.github.giosreina.ModelViewViewModel;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;

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
        // Actualizar el modelo
        model.setLastAction("Crear Proyecto");
        
        // Mostrar mensaje (simulación sin backend)
        showInfoAlert("Crear Proyecto", "Funcionalidad para crear un nuevo proyecto.\n\nEsta acción abriría un diálogo para configurar un nuevo proyecto.");
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