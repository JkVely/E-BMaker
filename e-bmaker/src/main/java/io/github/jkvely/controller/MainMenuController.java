package io.github.jkvely.controller;

import io.github.jkvely.model.EpubBook;
import io.github.jkvely.model.EpubChapter;
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
    private int currentChapterIndex = 1;    /**
     * Inicializa el controlador y configura los listeners para los botones de vista.
     * Realiza el enlace entre los botones y el ViewModel, y actualiza la vista central
     * según el modo seleccionado.
     */
    @FXML
    public void initialize() {
        // Configurar botones de vista como radio buttons
        setupViewButtons();

        // Cambia los paneles según el modo
        viewModel.viewModeProperty().addListener((obs, old, mode) -> updateCentralView(mode));
        
        // Configuración del tema (intercambio entre EVA-00 y EVA-01)
        themeSwitchBtn.selectedProperty().addListener((obs, oldVal, isDark) -> {
            if (rootPane.getScene() != null) {
                // Obtener el nodo raíz de la escena
                Parent sceneRoot = rootPane.getScene().getRoot();
                
                // Remover clases de tema anteriores
                sceneRoot.getStyleClass().removeIf(styleClass -> 
                    styleClass.equals("eva-00") || styleClass.equals("eva-01"));
                
                // Aplicar el tema correspondiente
                if (isDark) {
                    sceneRoot.getStyleClass().add("eva-01");
                    System.out.println("🌙 Tema cambiado a EVA-01 (Oscuro)");
                } else {
                    sceneRoot.getStyleClass().add("eva-00");
                    System.out.println("☀️ Tema cambiado a EVA-00 (Claro)");
                }
                
                // Forzar actualización visual
                sceneRoot.applyCss();
                sceneRoot.layout();
                
                // Actualizar iconos del interruptor con animación sutil
                updateThemeIcons(isDark);
            }
        });
        
        // Configuración inicial de iconos de tema
        setupThemeIcons();

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
            
            // Configurar callback para actualizar la estructura cuando cambie el título
            ctrl.setOnTitleChanged(() -> {
                updateBookStructureList();
                selectedStructureLabel.setText("Seleccionado: " + chapter.getTitle());
            });
            
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
    }    private void updateWindowTitle() {
        String bookTitle = (currentBook != null && currentBook.getTitle() != null && !currentBook.getTitle().isEmpty()) ? currentBook.getTitle() : "Sin título";
        javafx.stage.Stage stage = io.github.jkvely.MainApp.getPrimaryStage();
        if (stage != null) {
            stage.setTitle("E-BMaker | " + bookTitle + " (Editando)");
        }
    }
    
    /**
     * Configura los iconos iniciales del interruptor de tema.
     * Carga las imágenes de EVA-00 y EVA-01 y establece sus propiedades visuales.
     */
    private void setupThemeIcons() {
        try {
            // Cargar imágenes de los EVA desde recursos
            Image eva00Image = new Image(getClass().getResource("/styles/eva00-face.png").toExternalForm());
            Image eva01Image = new Image(getClass().getResource("/styles/eva01-face.png").toExternalForm());
            
            // Configurar imagen de EVA-00 (tema claro)
            sunIcon.setImage(eva00Image);
            sunIcon.setFitWidth(32);
            sunIcon.setFitHeight(32);
            sunIcon.setPreserveRatio(true);
            sunIcon.setSmooth(true);
            
            // Configurar imagen de EVA-01 (tema oscuro)
            moonIcon.setImage(eva01Image);
            moonIcon.setFitWidth(32);
            moonIcon.setFitHeight(32);
            moonIcon.setPreserveRatio(true);
            moonIcon.setSmooth(true);
            
            // Estado inicial: mostrar EVA-00, ocultar EVA-01
            sunIcon.setOpacity(1.0);
            moonIcon.setOpacity(0.3);
            
            System.out.println("🎨 Iconos de tema configurados correctamente");
            
        } catch (Exception e) {
            System.err.println("⚠️ Error al cargar iconos de tema: " + e.getMessage());
            // Configuración de respaldo con iconos de texto
            sunIcon.setImage(null);
            moonIcon.setImage(null);
        }
    }
    
    /**
     * Actualiza la visibilidad de los iconos del tema con una transición suave.
     * 
     * @param isDark true si el tema oscuro está activo, false para tema claro
     */
    private void updateThemeIcons(boolean isDark) {
        if (isDark) {
            // Tema oscuro activo: mostrar EVA-01, ocultar EVA-00
            sunIcon.setOpacity(0.3);
            moonIcon.setOpacity(1.0);
        } else {
            // Tema claro activo: mostrar EVA-00, ocultar EVA-01
            sunIcon.setOpacity(1.0);
            moonIcon.setOpacity(0.3);
        }
    }
    
    /**
     * Configura los botones de vista para funcionar como radio buttons.
     * Solo uno puede estar activo a la vez.
     */
    private void setupViewButtons() {
        // Configurar Editor button
        editorBtn.setOnAction(e -> {
            if (!editorBtn.isSelected()) {
                editorBtn.setSelected(true);
            }
            previewBtn.setSelected(false);
            splitBtn.setSelected(false);
            viewModel.setViewMode(MainMenuViewModel.CentralViewMode.EDITOR);
            System.out.println("✏️ Vista cambiada a Editor");
        });
        
        // Configurar Preview button
        previewBtn.setOnAction(e -> {
            if (!previewBtn.isSelected()) {
                previewBtn.setSelected(true);
            }
            editorBtn.setSelected(false);
            splitBtn.setSelected(false);
            viewModel.setViewMode(MainMenuViewModel.CentralViewMode.PREVIEW);
            System.out.println("👁️ Vista cambiada a Vista Previa");
        });
        
        // Configurar Split button
        splitBtn.setOnAction(e -> {
            if (!splitBtn.isSelected()) {
                splitBtn.setSelected(true);
            }
            editorBtn.setSelected(false);
            previewBtn.setSelected(false);
            viewModel.setViewMode(MainMenuViewModel.CentralViewMode.SPLIT);
            System.out.println("≡ Vista cambiada a Mitad-Mit");
        });
        
        // Establecer Editor como vista por defecto
        editorBtn.setSelected(true);
        previewBtn.setSelected(false);
        splitBtn.setSelected(false);
        viewModel.setViewMode(MainMenuViewModel.CentralViewMode.EDITOR);
    }
}
