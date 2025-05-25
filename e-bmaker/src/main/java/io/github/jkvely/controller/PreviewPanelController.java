package io.github.jkvely.controller;

import io.github.jkvely.model.EpubChapter;
import io.github.jkvely.util.MarkdownToHtmlConverter;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

/**
 * Controlador para el panel de vista previa
 */
public class PreviewPanelController {
    @FXML private WebView previewWebView;
    
    private EpubChapter chapter;
    
    @FXML
    public void initialize() {
        // Inicialización del controlador
    }
    
    /**
     * Establece el capítulo a mostrar y actualiza la vista previa
     * @param chapter el capítulo a mostrar
     */
    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            updatePreview(chapter.getContent());
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