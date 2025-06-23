package io.github.jkvely.viewmodel;

import java.io.File;

import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.util.ProjectFolderUtils;
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
    private io.github.jkvely.model.Classes.EpubCover epubCover;

    @FXML
    public void initialize() {        uploadCoverBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes JPG", "*.jpg", "*.jpeg"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes PNG", "*.png"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todas las imágenes", "*.jpg", "*.jpeg", "*.png"));
            
            // Set initial directory to project's Images folder if available
            if (book != null && book.getProjectPath() != null && !book.getProjectPath().trim().isEmpty()) {
                String imagesPath = ProjectFolderUtils.getImagesPath(book.getProjectPath());
                File imagesDir = new File(imagesPath);
                if (imagesDir.exists() && imagesDir.isDirectory()) {
                    fc.setInitialDirectory(imagesDir);
                }
            }
            
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
            if (this.book != null) this.book.getCover().setTitle(val);
            updateWindowTitle();
        });
    }    private void saveCoverInfo() {
        String title = bookTitleField.getText();
        java.io.File file = (java.io.File) coverImageView.getUserData();
        io.github.jkvely.model.Classes.Image img = null;
        if (file != null && file.exists()) {
            try {
                // Copy image to project Images folder if project path is available
                java.io.File targetFile = file;
                if (book != null && book.getProjectPath() != null && !book.getProjectPath().trim().isEmpty()) {
                    String imagesPath = ProjectFolderUtils.getImagesPath(book.getProjectPath());
                    java.io.File imagesDir = new java.io.File(imagesPath);
                    if (imagesDir.exists()) {
                        // Create a unique filename for the cover image
                        String extension = file.getName().substring(file.getName().lastIndexOf('.'));
                        String coverFileName = "cover" + extension;
                        targetFile = new java.io.File(imagesDir, coverFileName);
                        
                        // Copy file if it's not already in the project folder
                        if (!file.getAbsolutePath().equals(targetFile.getAbsolutePath())) {
                            java.nio.file.Files.copy(file.toPath(), targetFile.toPath(), 
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("📁 Imagen de portada copiada a: " + targetFile.getAbsolutePath());
                        }
                    }
                }
                
                byte[] data = java.nio.file.Files.readAllBytes(targetFile.toPath());
                String mimeType = file.getName().toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
                img = new io.github.jkvely.model.Classes.Image(targetFile.getName(), mimeType, data, "cover");
            } catch (Exception ex) {
                ex.printStackTrace();
                // Fallback to original file if copy fails
                try {
                    byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                    String mimeType = file.getName().toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
                    img = new io.github.jkvely.model.Classes.Image(file.getName(), mimeType, data, "cover");
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        epubCover = new io.github.jkvely.model.Classes.EpubCover(title, java.util.Collections.emptyList(), img);
        // Sincroniza la info del libro con la de la portada
        if (book != null) {
            book.getCover().setTitle(title);
            if (img != null) book.getCover().setImage(img);
        }
    }

    private void updateWindowTitle() {
        String bookTitle = (book != null && book.getCover().getTitle() != null && !book.getCover().getTitle().isEmpty()) ? book.getCover().getTitle() : "Sin título";
        javafx.stage.Stage stage = io.github.jkvely.Editor.getPrimaryStage();
        if (stage != null) {
            stage.setTitle("E-BMaker | " + bookTitle + " (Editando)");
        }
    }

    public void setBook(EpubBook book) {
        this.book = book;
        if (book != null) {
            bookTitleField.setText(book.getCover().getTitle());
            if (book.getCover().getImage() != null && book.getCover().getImage().getData() != null) {
                javafx.scene.image.Image fxImg = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(book.getCover().getImage().getData()));
                coverImageView.setImage(fxImg);
            }
        }
        bookTitleField.textProperty().addListener((obs, old, val) -> {
            if (this.book != null) this.book.getCover().setTitle(val);
        });
    }
}
