package io.github.jkvely.controller;

import io.github.jkvely.viewmodel.MainViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    private final MainViewModel viewModel = new MainViewModel();

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        messageLabel.textProperty().bind(viewModel.messageProperty());
    }
}
