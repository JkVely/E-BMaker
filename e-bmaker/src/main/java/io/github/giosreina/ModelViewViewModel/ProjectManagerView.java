package io.github.giosreina.ModelViewViewModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ProjectManagerView {
    
    private VBox root;
    private ProjectManagerViewModel viewModel;
    
    public ProjectManagerView() {
        viewModel = new ProjectManagerViewModel();
        initializeView();
        bindEvents();
    }
    
    private void initializeView() {
        // Contenedor principal
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Título
        Label titleLabel = new Label("E-maker - Gestor de Proyectos");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #333333;");
        
        // Subtítulo
        Label subtitleLabel = new Label("Selecciona una opción para continuar");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #666666;");
        
        // Contenedor de botones
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setSpacing(20);
        
        // Botón Crear Proyecto
        Button createProjectButton = new Button("Crear Proyecto");
        styleButton(createProjectButton, "#4CAF50");
        
        // Botón Abrir Proyecto
        Button openProjectButton = new Button("Abrir Proyecto");
        styleButton(openProjectButton, "#2196F3");
        
        // Agregar botones al contenedor
        buttonContainer.getChildren().addAll(createProjectButton, openProjectButton);
        
        // Agregar todos los elementos al contenedor principal
        root.getChildren().addAll(titleLabel, subtitleLabel, buttonContainer);
        
        // Guardar referencias de los botones en el ViewModel
        viewModel.setCreateProjectButton(createProjectButton);
        viewModel.setOpenProjectButton(openProjectButton);
    }
    
    private void styleButton(Button button, String color) {
        button.setPrefWidth(150);
        button.setPrefHeight(50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        // Efectos hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: derive(" + color + ", -10%);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
    }
    
    private void bindEvents() {
        // Vincular eventos de los botones con el ViewModel
        viewModel.getCreateProjectButton().setOnAction(e -> viewModel.handleCreateProject());
        viewModel.getOpenProjectButton().setOnAction(e -> viewModel.handleOpenProject());
    }
    
    public Parent getRoot() {
        return root;
    }
}