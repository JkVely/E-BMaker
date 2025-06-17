package io.github.jkvely.controller;

import java.io.File;

import io.github.jkvely.model.EpubBook;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class CoverPanelController {
    @FXML private Button uploadCoverBtn;
    @FXML private Button setCoverBtn;
    @FXML private TextField bookTitleField;
    @FXML private ImageView coverImageView;
    @FXML private HBox root;

    private EpubBook book;
    private io.github.jkvely.model.EpubCover epubCover;

    @FXML
    public void initialize() {
        uploadCoverBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(root.getScene().getWindow());
            if (file != null) {
                coverImageView.setImage(new Image(file.toURI().toString()));
                coverImageView.setUserData(file); // Guardar el archivo para luego
            }
        });
        setCoverBtn.setOnAction(e -> {
            saveCoverInfo();
            updateWindowTitle();
        });
        // Desactivar el botón si el título está vacío
        setCoverBtn.disableProperty().bind(bookTitleField.textProperty().isEmpty());
        bookTitleField.textProperty().addListener((obs, old, val) -> {
            if (this.book != null) this.book.setTitle(val);
            updateWindowTitle();
        });
    }

    private void saveCoverInfo() {
        String title = bookTitleField.getText();
        java.io.File file = (java.io.File) coverImageView.getUserData();
        io.github.jkvely.model.Image img = null;
        if (file != null && file.exists()) {
            try {
                byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                img = new io.github.jkvely.model.Image(file.getName(), "image/jpeg", data, "cover");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        epubCover = new io.github.jkvely.model.EpubCover(title, java.util.Collections.emptyList(), img);
        // Sincroniza la info del libro con la de la portada
        if (book != null) {
            book.setTitle(title);
            if (img != null) book.setCoverImage(img);
        }
    }

    private void updateWindowTitle() {
        String bookTitle = (book != null && book.getTitle() != null && !book.getTitle().isEmpty()) ? book.getTitle() : "Sin título";
        javafx.stage.Stage stage = io.github.jkvely.Editor.getPrimaryStage();
        if (stage != null) {
            stage.setTitle("E-BMaker | " + bookTitle + " (Editando)");
        }
    }

    public void setBook(EpubBook book) {
        this.book = book;
        if (book != null) {
            bookTitleField.setText(book.getTitle());
            if (book.getCoverImage() != null && book.getCoverImage().getData() != null) {
                javafx.scene.image.Image fxImg = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(book.getCoverImage().getData()));
                coverImageView.setImage(fxImg);
            }
        }
        bookTitleField.textProperty().addListener((obs, old, val) -> {
            if (this.book != null) this.book.setTitle(val);
        });
    }
}
