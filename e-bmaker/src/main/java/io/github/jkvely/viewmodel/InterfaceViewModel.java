package io.github.jkvely.viewmodel;

import java.io.IOException;

import io.github.jkvely.model.InterfaceModel;
import io.github.jkvely.util.Opener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class InterfaceViewModel {
    
    private Button createProjectButton;
    private Button openProjectButton;
    private final InterfaceModel model;
    
    public InterfaceViewModel() {
        model = new InterfaceModel();
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
        closeCurrentStage(createProjectButton);
        loadMainMenu("E-BMaker", "No se pudo cargar el editor.");
    }

    public void handleOpenProject() {
        model.setLastAction("Abrir Proyecto");
        String ruta = Opener.getRuta();

        if (ruta != null && !ruta.isEmpty()) {
            closeCurrentStage(openProjectButton);
            loadMainMenu("E-BMaker", "No se pudo cargar el documento.");
        } else {
            showInfoAlert("Información", "No se seleccionó ningún archivo.");
        }
    }

    private void closeCurrentStage(Button button) {
        Stage currentStage = (Stage) button.getScene().getWindow();
        currentStage.close();
    }

    private void loadMainMenu(String title, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Cargar estilos
            String[] stylesheets = {
                "/styles/eva-main.css",
                "/styles/eva-00-light-new.css",
                "/styles/eva-01-dark-new.css"
            };
            for (String stylesheet : stylesheets) {
                java.net.URL css = getClass().getResource(stylesheet);
                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showInfoAlert("Error", errorMessage);
        }
    }

    
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}