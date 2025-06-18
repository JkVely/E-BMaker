package io.github.jkvely.controller;

import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.viewmodel.MainMenuViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * Controlador para el menú principal de la aplicación.
 * Se encarga de vincular la vista (FXML) con el ViewModel y manejar la lógica de interacción
 * del usuario en la interfaz principal.
 */
public class MainMenuController {
    /** ViewModel asociado a este controlador. */
    private final MainMenuViewModel viewModel = new MainMenuViewModel();

    @FXML private ToggleButton editorBtn, previewBtn, splitBtn;
    @FXML private BorderPane rootPane;
    @FXML private ListView<String> epubBookContentsList;
    @FXML private Label selectedStructureLabel;
    @FXML private Button addChapterBtn;
    @FXML private ImageView sunIcon;
    @FXML private ImageView moonIcon;
    @FXML private ToggleButton themeSwitchBtn;
    @FXML private StackPane mainContentPane;

    private EpubBook currentBook;
    private ObservableList<String> bookStructureItems = FXCollections.observableArrayList();
    private int currentChapterIndex = 1;

    /**
     * Inicializa el controlador y configura los listeners para los botones de vista.
     * Realiza el enlace entre los botones y el ViewModel, y actualiza la vista central
     * según el modo seleccionado.
     */
    @FXML
    public void initialize() {
        // Botones de modo de vista
        editorBtn.setOnAction(e -> viewModel.setViewMode(MainMenuViewModel.CentralViewMode.EDITOR));
        previewBtn.setOnAction(e -> viewModel.setViewMode(MainMenuViewModel.CentralViewMode.PREVIEW));
        splitBtn.setOnAction(e -> viewModel.setViewMode(MainMenuViewModel.CentralViewMode.SPLIT));

        // Cambia los paneles según el modo
        viewModel.viewModeProperty().addListener((obs, old, mode) -> updateCentralView(mode));
        
        // Configuración del tema (solo usando sun/moon icons)
        themeSwitchBtn.selectedProperty().addListener((obs, oldVal, dark) -> {
            if (rootPane.getScene() != null) {
                rootPane.getScene().getRoot().getStyleClass().removeIf(s -> s.equals("dark"));
                if (dark) rootPane.getScene().getRoot().getStyleClass().add("dark");
                rootPane.getScene().getRoot().applyCss();
                rootPane.getScene().getRoot().layout();
                sunIcon.setOpacity(dark ? 0.0 : 1);
                moonIcon.setOpacity(dark ? 1 : 0.0);
            }
        });
        // Configuración inicial de opacidad para sol/luna
        sunIcon.setOpacity(1);
        moonIcon.setOpacity(0.2);
        // Asignar imágenes de sol y luna desde recursos
        sunIcon.setImage(new Image(getClass().getResource("/styles/eva00-face.png").toExternalForm()));
        moonIcon.setImage(new Image(getClass().getResource("/styles/eva01-face.png").toExternalForm()));

        sunIcon.setFitWidth(40);
        sunIcon.setFitHeight(40);
        moonIcon.setFitWidth(40);
        moonIcon.setFitHeight(40);

        // Estructura base EPUB: portada y capítulo 1
        currentBook = new EpubBook();
        currentBook.setTitle("Nuevo libro");
        EpubChapter cap1 = new EpubChapter(1, "Capítulo 1", "", null);
        currentBook.addChapter(cap1);
        
        // Configuración de la lista de estructura
        updateBookStructureList();
        epubBookContentsList.setItems(bookStructureItems);
        epubBookContentsList.setCellFactory(lv -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().remove("add-chapter-cell");
                } else {
                    setText(item);
                    if (item.equals("➕ Nuevo capítulo")) {
                        if (!getStyleClass().contains("add-chapter-cell")) {
                            getStyleClass().add("add-chapter-cell");
                        }
                    } else {
                        getStyleClass().remove("add-chapter-cell");
                    }
                }
            }
        });
        epubBookContentsList.getSelectionModel().select(0); // Selecciona portada al iniciar
        showCoverEditor(); // Muestra el panel de portada al iniciar
        
        // Ocultar el botón independiente de nuevo capítulo
        addChapterBtn.setVisible(false);

        // Agregar listener para selección de estructura, incluyendo opción 'Nuevo capítulo'
        epubBookContentsList.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            if (newIdx == null || newIdx.intValue() < 0) return;
            int idx = newIdx.intValue();
            int last = bookStructureItems.size() - 1;
            if (idx == last) {
                createNewChapter();
                return;
            }
            currentChapterIndex = idx;
            if (currentChapterIndex == 0) {
                showCoverEditor();
            } else {
                showChapterEditor(currentChapterIndex);
            }
        });

        // Reemplaza acción de botón con método reutilizable
        addChapterBtn.setOnAction(e -> createNewChapter());
        updateWindowTitle();
    }

    /**
     * Crea un nuevo capítulo y refresca la lista, seleccionando directamente.
     */
    private void createNewChapter() {
        int newId = currentBook.getChapters().size() + 1;
        EpubChapter newCap = new EpubChapter(newId, "Capítulo " + newId, "", null);
        currentBook.addChapter(newCap);
        updateBookStructureList();
        epubBookContentsList.getSelectionModel().select(newId);
        showChapterEditor(newId);
    }

    /**
     * Muestra el editor de portada
     */
    private void showCoverEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CoverPanel.fxml"));
            Parent coverPanel = loader.load();
            CoverPanelController ctrl = loader.getController();
            ctrl.setBook(currentBook);
            mainContentPane.getChildren().setAll(coverPanel);
            selectedStructureLabel.setText("Seleccionado: Portada");
            updateWindowTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra el editor de capítulos
     * @param idx índice del capítulo a mostrar
     */
    private void showChapterEditor(int idx) {
        if (idx <= 0 || currentBook.getChapters().size() < idx) return;  // invalid index guard
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChapterPanel.fxml"));
            Parent chapterPanel = loader.load();
            ChapterPanelController ctrl = loader.getController();
            EpubChapter chapter = currentBook.getChapters().get(idx-1);
            ctrl.setChapter(chapter);
            mainContentPane.getChildren().setAll(chapterPanel);
            selectedStructureLabel.setText("Seleccionado: " + chapter.getTitle());
            updateWindowTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la visibilidad de los paneles centrales según el modo seleccionado.
     * @param mode modo de visualización seleccionado
     */
    private void updateCentralView(MainMenuViewModel.CentralViewMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent panel = null;
            
            switch (mode) {
                case EDITOR:
                    loader.setLocation(getClass().getResource("/fxml/EditorPanel.fxml"));
                    panel = loader.load();
                    if (currentChapterIndex > 0 && currentBook.getChapters().size() >= currentChapterIndex) {
                        EditorPanelController ctrl = loader.getController();
                        ctrl.setChapter(currentBook.getChapters().get(currentChapterIndex - 1));
                    }
                    break;
                case PREVIEW:
                    loader.setLocation(getClass().getResource("/fxml/PreviewPanel.fxml"));
                    panel = loader.load();
                    if (currentChapterIndex > 0 && currentBook.getChapters().size() >= currentChapterIndex) {
                        PreviewPanelController ctrl = loader.getController();
                        ctrl.setChapter(currentBook.getChapters().get(currentChapterIndex - 1));
                    }
                    break;
                case SPLIT:
                    loader.setLocation(getClass().getResource("/fxml/SplitPanel.fxml"));
                    panel = loader.load();
                    if (currentChapterIndex > 0 && currentBook.getChapters().size() >= currentChapterIndex) {
                        SplitPanelController ctrl = loader.getController();
                        ctrl.setChapter(currentBook.getChapters().get(currentChapterIndex - 1));
                    }
                    break;
            }
            
            if (panel != null) {
                mainContentPane.getChildren().setAll(panel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la lista observable con la estructura del libro (portada y capítulos).
     */
    private void updateBookStructureList() {
        bookStructureItems.clear();
        bookStructureItems.add("Portada");
        for (EpubChapter chapter : currentBook.getChapters()) {
            bookStructureItems.add(chapter.getTitle());
        }
        // Item especial al final para crear un nuevo capítulo
        bookStructureItems.add("➕ Nuevo capítulo");
    }

    private void updateWindowTitle() {
        String bookTitle = (currentBook != null && currentBook.getTitle() != null && !currentBook.getTitle().isEmpty()) ? currentBook.getTitle() : "Sin título";
        javafx.stage.Stage stage = io.github.jkvely.Editor.getPrimaryStage();
        if (stage != null) {
            stage.setTitle("E-BMaker | " + bookTitle + " (Editando)");
        }
    }
}
