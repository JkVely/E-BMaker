package io.github.jkvely.viewmodel;

import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.util.MarkdownToHtmlConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

/**
 * Controlador para el panel dividido (editor + vista previa)
 */
public class SplitPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private WebView previewWebView;
    
    private EpubChapter chapter;
    
    @FXML
    public void initialize() {
        // Configurar actualización automática de la vista previa cuando cambia el texto
        editorArea.textProperty().addListener((obs, old, val) -> {
            updatePreview(val);
        });
    }
    
    /**
     * Establece el capítulo a editar/mostrar y actualiza la UI
     * @param chapter el capítulo a editar
     */
    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            chapterTitleField.setText(chapter.getTitle());
            editorArea.setText(chapter.getContent());
            updatePreview(chapter.getContent());
            
            // Configurar listeners para sincronización bidireccional
            chapterTitleField.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setTitle(val);
            });
            
            editorArea.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setContent(val);
            });
        }
    }
    
    /**
     * Actualiza la vista previa con el contenido en HTML
     * @param markdown texto en markdown a convertir
     */
    private void updatePreview(String markdown) {
        String html = MarkdownToHtmlConverter.convert(markdown);
        previewWebView.getEngine().loadContent(html, "text/html");
    }
}