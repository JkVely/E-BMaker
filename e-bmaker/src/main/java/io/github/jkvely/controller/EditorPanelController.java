package io.github.jkvely.controller;

import io.github.jkvely.model.Classes.EpubChapter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controlador para el panel de edición
 */
public class EditorPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    
    private EpubChapter chapter;
    
    @FXML
    public void initialize() {
        // Inicialización del controlador
    }
    
    /**
     * Establece el capítulo a editar y actualiza la UI
     * @param chapter el capítulo a editar
     */
    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            chapterTitleField.setText(chapter.getTitle());
            editorArea.setText(chapter.getContent());
            
            // Configurar listeners para sincronización bidireccional
            chapterTitleField.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setTitle(val);
            });
            
            editorArea.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setContent(val);
            });
        }
    }
}