package io.github.jkvely.viewmodel;

import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.util.MarkdownToHtmlConverter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import java.io.File;

/**
 * Controlador para el panel dividido (editor + vista previa)
 * Incluye barra de herramientas tipo GitHub y atajos de teclado.
 */
public class SplitPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private WebView previewWebView;
    @FXML private Button boldBtn, italicBtn, quoteBtn, imageBtn, ulBtn, olBtn;

    private EpubChapter chapter;

    /**
     * Inicializa la barra de herramientas y listeners de teclado.
     */
    @FXML
    public void initialize() {
        // Botones Markdown
        if (boldBtn != null) boldBtn.setOnAction(e -> insertMarkdown("**", "**", "negrilla"));
        if (italicBtn != null) italicBtn.setOnAction(e -> insertMarkdown("*", "*", "itálica"));
        if (quoteBtn != null) quoteBtn.setOnAction(e -> insertBlockMarkdown("> ", "diálogo o cita"));
        if (imageBtn != null) imageBtn.setOnAction(e -> insertImageMarkdown());
        if (ulBtn != null) ulBtn.setOnAction(e -> insertBlockMarkdown("- ", "elemento de lista"));
        if (olBtn != null) olBtn.setOnAction(e -> insertBlockMarkdown("1. ", "elemento numerado"));
        // Atajos de teclado
        if (editorArea != null) {
            editorArea.addEventFilter(KeyEvent.KEY_PRESSED, this::handleShortcuts);
            editorArea.textProperty().addListener((obs, old, val) -> updatePreview(val));
        }
        editorArea.getStyleClass().add("eva-editor-area");
        previewWebView.getStyleClass().add("eva-preview-web");
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
            chapterTitleField.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setTitle(val);
            });
            editorArea.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setContent(val);
            });
        }
    }

    /**
     * Atajos de teclado para formato Markdown.
     */
    private void handleShortcuts(KeyEvent event) {
        if (new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("**", "**", "negrilla");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("*", "*", "itálica");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN).match(event)) {
            insertBlockMarkdown("> ", "diálogo o cita");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN).match(event)) {
            insertBlockMarkdown("- ", "elemento de lista");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
            insertBlockMarkdown("1. ", "elemento numerado");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
            insertImageMarkdown();
            event.consume();
        }
    }

    /**
     * Inserta formato Markdown en la selección actual.
     */
    private void insertMarkdown(String prefix, String suffix, String placeholder) {
        int start = editorArea.getSelection().getStart();
        int end = editorArea.getSelection().getEnd();
        String selected = editorArea.getSelectedText();
        if (selected.isEmpty()) selected = placeholder;
        String before = editorArea.getText(0, start);
        String after = editorArea.getText(end, editorArea.getLength());
        String insert = prefix + selected + suffix;
        editorArea.setText(before + insert + after);
        editorArea.positionCaret(before.length() + insert.length());
    }

    /**
     * Inserta un prefijo Markdown al inicio de la línea o selección.
     */
    private void insertBlockMarkdown(String prefix, String placeholder) {
        int start = editorArea.getSelection().getStart();
        int end = editorArea.getSelection().getEnd();
        String selected = editorArea.getSelectedText();
        if (selected.isEmpty()) selected = placeholder;
        String before = editorArea.getText(0, start);
        String after = editorArea.getText(end, editorArea.getLength());
        String[] lines = selected.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(prefix).append(line).append("\n");
        }
        String insert = sb.toString();
        editorArea.setText(before + insert + after);
        editorArea.positionCaret(before.length() + insert.length());
    }

    /**
     * Inserta sintaxis Markdown para imagen, abriendo un selector de archivos.
     */
    private void insertImageMarkdown() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar imagen");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );
        File file = fc.showOpenDialog(editorArea.getScene().getWindow());
        if (file != null) {
            String imageMarkdown = "![](" + file.getName() + ")";
            int caret = editorArea.getCaretPosition();
            String before = editorArea.getText(0, caret);
            String after = editorArea.getText(caret, editorArea.getLength());
            editorArea.setText(before + imageMarkdown + after);
            editorArea.positionCaret(before.length() + imageMarkdown.length());
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