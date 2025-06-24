package io.github.jkvely.viewmodel;

import io.github.jkvely.epub.EpubService;
import io.github.jkvely.epub.EpubWriter;
import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.model.Classes.EpubCover;
import io.github.jkvely.util.ProjectFolderUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.ArrayList;

/**
 * Controlador para el menú principal de la aplicación.
 * Se encarga de vincular la vista (FXML) con el ViewModel y manejar la lógica de interacción
 * del usuario en la interfaz principal.
 */
public class MainMenuController {
    /** ViewModel asociado a este controlador. */
    private final MainMenuViewModel viewModel = new MainMenuViewModel();    @FXML private ToggleButton editorBtn, previewBtn, splitBtn;
    @FXML private BorderPane rootPane;
    @FXML private ListView<String> epubBookContentsList;
    @FXML private Label selectedStructureLabel;
    @FXML private Button addChapterBtn;
    @FXML private ImageView sunIcon;
    @FXML private ImageView moonIcon;
    @FXML private ToggleButton themeSwitchBtn;
    @FXML private StackPane mainContentPane;
    
    // Menu items
    @FXML private MenuItem newFileMenuItem;
    @FXML private MenuItem openFileMenuItem;
    @FXML private MenuItem saveFileMenuItem;
    @FXML private MenuItem exportFileMenuItem;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem undoMenuItem;
    @FXML private MenuItem redoMenuItem;
    @FXML private MenuItem preferencesMenuItem;

    private EpubBook currentBook;
    private ObservableList<String> bookStructureItems = FXCollections.observableArrayList();
    private int currentChapterIndex = 1;    /**
     * Inicializa el controlador y configura los listeners para los botones de vista.
     * Realiza el enlace entre los botones y el ViewModel, y actualiza la vista central
     * según el modo seleccionado.
     */    @FXML
    public void initialize() {
        // Configurar menu items
        setupMenuItems();
        
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
        });        // Configuración inicial de iconos de tema
        setupThemeIcons();        
        
        // Check if there's a loaded book from opening an EPUB file
        EpubBook loadedEpubBook = getAndClearLoadedBook();
        if (loadedEpubBook != null) {
            // Use the loaded book
            currentBook = loadedEpubBook;
            // Set project path if not already set
            if (currentBook.getProjectPath() == null || currentBook.getProjectPath().trim().isEmpty()) {
                currentBook.setProjectPath(currentProjectPath);
            }
        } else {
            // Create new book structure: cover and chapter 1
            currentBook = new EpubBook();
            currentBook.setCover(new EpubCover("Nuevo libro", new ArrayList<>(), null));
            currentBook.setProjectPath(currentProjectPath); // Set project path if available
            EpubChapter cap1 = new EpubChapter(1, "Capítulo 1", "", null);
            currentBook.addChapter(cap1);
        }
        
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
        
        // Configurar menu items
        setupMenuItems();
    }
    
    /**
     * Configura los menu items con sus manejadores de eventos.
     */
    private void setupMenuItems() {
        if (newFileMenuItem != null) {
            newFileMenuItem.setOnAction(e -> handleNewFile());
        }
        if (openFileMenuItem != null) {
            openFileMenuItem.setOnAction(e -> handleOpenFile());
        }
        if (saveFileMenuItem != null) {
            saveFileMenuItem.setOnAction(e -> handleSaveFile());
        }
        if (exportFileMenuItem != null) {
            exportFileMenuItem.setOnAction(e -> handleExportEpub());
        }
        if (exitMenuItem != null) {
            exitMenuItem.setOnAction(e -> handleExit());
        }
        if (undoMenuItem != null) {
            undoMenuItem.setOnAction(e -> handleUndo());
        }
        if (redoMenuItem != null) {
            redoMenuItem.setOnAction(e -> handleRedo());
        }
        if (preferencesMenuItem != null) {
            preferencesMenuItem.setOnAction(e -> handlePreferences());
        }
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
            bookStructureItems.add(chapter.getTitle());        }
        // Item especial al final para crear un nuevo capítulo
        bookStructureItems.add("➕ Nuevo capítulo");
    }
    
    private void updateWindowTitle() {
        String bookTitle = (currentBook != null && currentBook.getCover() != null && currentBook.getCover().getTitle() != null && !currentBook.getCover().getTitle().isEmpty()) ? currentBook.getCover().getTitle() : "Sin título";
        javafx.stage.Stage stage = io.github.jkvely.Editor.getPrimaryStage();
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
      /**
     * Maneja la creación de un nuevo archivo.
     */
    @FXML
    private void handleNewFile() {
        try {
            // Create a new project folder if none exists
            if (currentProjectPath == null || currentProjectPath.trim().isEmpty()) {
                currentProjectPath = ProjectFolderUtils.createProjectFolder(null, null);
                showAlert(Alert.AlertType.INFORMATION, "Proyecto Creado", 
                    "Se ha creado un nuevo proyecto en:\n" + currentProjectPath);
            }
            
            // Reinicializar el libro con portada y un capítulo vacío
            currentBook = new EpubBook();
            if (currentBook.getCover() == null) {
                currentBook.setCover(new EpubCover("Nuevo libro", new ArrayList<>(), null));
            }
            currentBook.setProjectPath(currentProjectPath); // Set project path
            EpubChapter cap1 = new EpubChapter(1, "Capítulo 1", "", null);
            currentBook.addChapter(cap1);
            
            // Actualizar UI
            updateBookStructureList();
            epubBookContentsList.getSelectionModel().select(0);
            showCoverEditor();
            updateWindowTitle();
            
            showAlert(Alert.AlertType.INFORMATION, "Nuevo archivo", "Se ha creado un nuevo libro.");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                "No se pudo crear la carpeta del proyecto: " + e.getMessage());
            
            // Continue without project folder if creation fails
            currentBook = new EpubBook();
            if (currentBook.getCover() == null) {
                currentBook.setCover(new EpubCover("Nuevo libro", new ArrayList<>(), null));
            }
            EpubChapter cap1 = new EpubChapter(1, "Capítulo 1", "", null);
            currentBook.addChapter(cap1);
            
            updateBookStructureList();
            epubBookContentsList.getSelectionModel().select(0);
            showCoverEditor();
            updateWindowTitle();
            
            showAlert(Alert.AlertType.INFORMATION, "Nuevo archivo", "Se ha creado un nuevo libro sin carpeta de proyecto.");
        }
    }
      /**
     * Maneja la apertura de un archivo EPUB existente.
     */
    @FXML
    private void handleOpenFile() {
        EpubBook loadedBook = EpubService.loadEpub();
        if (loadedBook != null) {
            currentBook = loadedBook;
            
            // Debug: Print book information
            System.out.println("📖 Libro cargado: " + currentBook.getCover().getTitle());
            System.out.println("👤 Autor: " + currentBook.getMetadata().getOrDefault("creator", "Desconocido"));
            System.out.println("📚 Capítulos: " + currentBook.getChapters().size());
            
            // Refresh the UI to show the loaded content
            refreshBookUI();
            
            showAlert(Alert.AlertType.INFORMATION, "Archivo abierto", "El libro se ha cargado correctamente.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Error al abrir", "No se pudo cargar el archivo seleccionado.");
        }
    }
    
    /**
     * Maneja el guardado del archivo actual.
     */
    @FXML
    private void handleSaveFile() {
        if (currentBook == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "No hay un libro para guardar.");
            return;
        }
        
        // Por ahora, usar exportar como funcionalidad de guardado
        handleExportEpub();
    }
      /**
     * Maneja la exportación del libro actual a formato EPUB.
     */
    @FXML
    private void handleExportEpub() {
        if (currentBook == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "No hay un libro para exportar.");
            return;
        }
        
        // Generar nombre sugerido basado en el título del libro
        String suggestedName = "libro.epub";
        if (currentBook.getCover() != null && currentBook.getCover().getTitle() != null) {
            suggestedName = sanitizeFileName(currentBook.getCover().getTitle()) + ".epub";
        }
        
        // Usar el administrador nativo de archivos para seleccionar donde guardar
        String outputPath = io.github.jkvely.util.NativeFileManager.saveEpubFile(suggestedName);
        
        if (outputPath != null) {
            // Asegurar extensión .epub
            if (!outputPath.toLowerCase().endsWith(".epub")) {
                outputPath += ".epub";
            }
            
            File outputFile = new File(outputPath);
            
            // Realizar exportación en hilo separado para no bloquear UI
            Task<Void> exportTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Asegurar que el libro tiene la estructura mínima necesaria
                        validateAndPrepareBook();
                        
                        // Exportar usando EpubWriter
                        EpubWriter.exportEpub(currentBook, outputFile);
                        
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION, "Exportación exitosa", 
                                "El libro se ha exportado correctamente a:\n" + outputFile.getAbsolutePath());
                        });
                        
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.ERROR, "Error de exportación", 
                                "No se pudo exportar el libro:\n" + e.getMessage());
                        });
                        throw e;
                    }
                    return null;
                }
            };
            
            // Ejecutar tarea de exportación
            Thread exportThread = new Thread(exportTask);
            exportThread.setDaemon(true);
            exportThread.start();
        }
    }
    
    /**
     * Valida y prepara el libro para exportación, asegurando estructura mínima.
     */
    private void validateAndPrepareBook() {
        // Asegurar que hay una portada
        if (currentBook.getCover() == null) {
            currentBook.setCover(new EpubCover("Libro sin título", new ArrayList<>(), null));
        }
        if (currentBook.getCover().getTitle() == null || currentBook.getCover().getTitle().trim().isEmpty()) {
            currentBook.getCover().setTitle("Libro sin título");
        }
        if (currentBook.getCover().getAuthors() == null || currentBook.getCover().getAuthors().isEmpty()) {
            currentBook.getCover().setAuthors(new ArrayList<>());
            currentBook.getCover().getAuthors().add("Autor desconocido");
        }
        
        // Asegurar que hay al menos un capítulo
        if (currentBook.getChapters() == null || currentBook.getChapters().isEmpty()) {
            EpubChapter emptyChapter = new EpubChapter(1, "Capítulo 1", "Contenido vacío", null);
            currentBook.addChapter(emptyChapter);
        }
        
        // Asegurar identificador único
        if (currentBook.getIdentifier() == null || currentBook.getIdentifier().trim().isEmpty()) {
            currentBook.setIdentifier("urn:uuid:" + java.util.UUID.randomUUID().toString());
        }
        
        // Asegurar idioma
        if (currentBook.getLanguage() == null || currentBook.getLanguage().trim().isEmpty()) {
            currentBook.setLanguage("es");
        }
    }
    
    /**
     * Maneja la salida de la aplicación.
     */
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    /**
     * Maneja la funcionalidad de deshacer.
     */
    @FXML
    private void handleUndo() {
        // TODO: Implementar funcionalidad de deshacer a nivel global
        showAlert(Alert.AlertType.INFORMATION, "Deshacer", "Funcionalidad en desarrollo.");
    }
    
    /**
     * Maneja la funcionalidad de rehacer.
     */
    @FXML
    private void handleRedo() {
        // TODO: Implementar funcionalidad de rehacer a nivel global
        showAlert(Alert.AlertType.INFORMATION, "Rehacer", "Funcionalidad en desarrollo.");
    }
    
    /**
     * Maneja las preferencias de la aplicación.
     */
    @FXML
    private void handlePreferences() {
        showAlert(Alert.AlertType.INFORMATION, "Preferencias", "Panel de preferencias en desarrollo.");
    }
    
    /**
     * Limpia un nombre de archivo eliminando caracteres no válidos.
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "libro";
        return fileName.replaceAll("[^a-zA-Z0-9áéíóúñÁÉÍÓÚÑ._-]", "_").trim();
    }
    
    /**
     * Muestra un alert con el tipo, título y mensaje especificados.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }    // Project context for folder management
    private static String currentProjectPath = null;
    private static EpubBook loadedBook = null;
    
    /**
     * Sets the current project path for the application session.
     * @param projectPath the path to the project folder
     */
    public static void setCurrentProjectPath(String projectPath) {
        currentProjectPath = projectPath;
    }
    
    /**
     * Gets the current project path.
     * @return the current project path or null if none set
     */
    public static String getCurrentProjectPath() {
        return currentProjectPath;
    }
    
    /**
     * Sets the loaded book for the application session.
     * This is used when opening an existing EPUB file.
     * @param book the loaded EPUB book
     */
    public static void setLoadedBook(EpubBook book) {
        loadedBook = book;
    }
    
    /**
     * Gets the loaded book and clears the static reference.
     * @return the loaded book or null if none set
     */
    public static EpubBook getAndClearLoadedBook() {
        EpubBook book = loadedBook;
        loadedBook = null; // Clear after getting to avoid memory leaks
        return book;
    }
      /**
     * Refreshes the UI to reflect the current book state.
     * This includes updating the structure list, window title, and showing the cover panel.
     */
    private void refreshBookUI() {
        // Update the book structure list
        updateBookStructureList();
        
        // Update window title with book title
        updateWindowTitle();
        
        // Show the cover panel by default for loaded books
        if (currentBook != null && currentBook.getCover() != null) {
            Platform.runLater(() -> {
                epubBookContentsList.getSelectionModel().select(0); // Select "Portada"
                showCoverEditor();
                
                // Debug: Print cover information
                System.out.println("🎨 Portada mostrada: " + currentBook.getCover().getTitle());
                if (currentBook.getChapters() != null && !currentBook.getChapters().isEmpty()) {
                    System.out.println("📝 Primer capítulo: " + currentBook.getChapters().get(0).getTitle());
                    System.out.println("📄 Contenido preview: " + 
                        currentBook.getChapters().get(0).getContent().substring(0, 
                        Math.min(100, currentBook.getChapters().get(0).getContent().length())) + "...");
                }
            });
        }
    }
}