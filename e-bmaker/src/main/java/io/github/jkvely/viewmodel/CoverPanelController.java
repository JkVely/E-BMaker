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

public class CoverPanelController {    @FXML private Button uploadCoverBtn;
    @FXML private Button setCoverBtn;
    @FXML private TextField bookTitleField;
    @FXML private TextField authorField;
    @FXML private TextField genresField;
    @FXML private ImageView coverImageView;
    @FXML private HBox root;    private EpubBook book;    @FXML
    public void initialize() {        uploadCoverBtn.setOnAction(e -> {
            // Usar selector nativo de archivos
            String selectedImagePath = io.github.jkvely.util.NativeFileManager.selectImageFile();
            
            if (selectedImagePath != null) {
                File file = new File(selectedImagePath);
                
                // Verificar que es un formato de imagen soportado
                if (!io.github.jkvely.util.ImageManager.isSupportedImageFormat(file)) {
                    System.err.println("Formato de imagen no soportado para portada: " + file.getName());
                    return;
                }
                
                try {
                    // Mostrar la imagen en el ImageView
                    coverImageView.setImage(new Image(file.toURI().toString()));
                    coverImageView.setUserData(file); // Guardar el archivo para después
                    
                    System.out.println("🎨 Imagen de portada seleccionada: " + file.getName());
                } catch (Exception ex) {
                    System.err.println("Error al cargar la imagen de portada: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        setCoverBtn.setOnAction(e -> {
            saveCoverInfo();
            updateWindowTitle();
        });        // Desactivar el botón si el título está vacío
        setCoverBtn.disableProperty().bind(bookTitleField.textProperty().isEmpty());
        
        // Listeners para actualizar el libro cuando cambien los campos
        bookTitleField.textProperty().addListener((obs, old, val) -> {
            if (this.book != null) this.book.getCover().setTitle(val);
            updateWindowTitle();
        });
        
        authorField.textProperty().addListener((obs, old, val) -> {
            if (this.book != null) {
                // Update book metadata with author info
                this.book.getMetadata().put("creator", val != null ? val.trim() : "");
            }
        });
        
        genresField.textProperty().addListener((obs, old, val) -> {
            if (this.book != null) {
                // Update book subjects with genres
                if (val != null && !val.trim().isEmpty()) {
                    String[] genres = val.split(",");
                    java.util.List<String> genreList = new java.util.ArrayList<>();
                    for (String genre : genres) {
                        String trimmed = genre.trim();
                        if (!trimmed.isEmpty()) {
                            genreList.add(trimmed);
                        }
                    }
                    this.book.setSubjects(genreList);
                } else {
                    this.book.setSubjects(new java.util.ArrayList<>());
                }
            }
        });
    }    private void saveCoverInfo() {
        String title = bookTitleField.getText();
        String author = authorField.getText();
        String genres = genresField.getText();
        
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
                    ex2.printStackTrace();                }
            }
        }
          // Update the book's cover directly
        if (book != null) {
            book.getCover().setTitle(title);
            if (img != null) book.getCover().setImage(img);
            
            // Save author to metadata
            if (author != null && !author.trim().isEmpty()) {
                book.getMetadata().put("creator", author.trim());
            }
            
            // Save genres to subjects
            if (genres != null && !genres.trim().isEmpty()) {
                String[] genreArray = genres.split(",");
                java.util.List<String> genreList = new java.util.ArrayList<>();
                for (String genre : genreArray) {
                    String trimmed = genre.trim();
                    if (!trimmed.isEmpty()) {
                        genreList.add(trimmed);
                    }
                }
                book.setSubjects(genreList);
            }
        }
    }

    private void updateWindowTitle() {
        String bookTitle = (book != null && book.getCover().getTitle() != null && !book.getCover().getTitle().isEmpty()) ? book.getCover().getTitle() : "Sin título";
        javafx.stage.Stage stage = io.github.jkvely.Editor.getPrimaryStage();
        if (stage != null) {
            stage.setTitle("E-BMaker | " + bookTitle + " (Editando)");
        }
    }    public void setBook(EpubBook book) {
        this.book = book;
        if (book != null) {
            // Set title
            bookTitleField.setText(book.getCover().getTitle());
            
            // Set author from metadata
            String author = book.getMetadata().getOrDefault("creator", "");
            authorField.setText(author);
            
            // Set genres from subjects
            if (book.getSubjects() != null && !book.getSubjects().isEmpty()) {
                String genres = String.join(", ", book.getSubjects());
                genresField.setText(genres);
            } else {
                genresField.setText("");
            }
            
            // Set cover image
            if (book.getCover().getImage() != null && book.getCover().getImage().getData() != null) {
                javafx.scene.image.Image fxImg = new javafx.scene.image.Image(new java.io.ByteArrayInputStream(book.getCover().getImage().getData()));
                coverImageView.setImage(fxImg);
            }
        }
    }
}
