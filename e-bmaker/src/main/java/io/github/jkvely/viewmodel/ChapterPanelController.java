package io.github.jkvely.viewmodel;

import io.github.jkvely.model.Classes.EpubChapter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

/**
 * Controlador para el panel de edición de capítulos.
 * Solo edición, sin preview, con toolbar y atajos.
 */
public class ChapterPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private Button boldBtn, italicBtn, quoteBtn, imageBtn, ulBtn, olBtn;

    private EpubChapter chapter;
    private Runnable onTitleChanged; // Callback para notificar cambios de título

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
        }
        editorArea.getStyleClass().add("eva-editor-area");
    }

    /**
     * Establece el capítulo a editar y configura los listeners.
     * @param chapter capítulo a editar
     */
    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            chapterTitleField.setText(chapter.getTitle());
            editorArea.setText(chapter.getContent());
        }
        chapterTitleField.textProperty().addListener((obs, old, val) -> {
            if (this.chapter != null) {
                this.chapter.setTitle(val);
                if (onTitleChanged != null) {
                    onTitleChanged.run();
                }
            }
        });
        editorArea.textProperty().addListener((obs, old, val) -> {
            if (this.chapter != null) this.chapter.setContent(val);
        });
    }

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

    private void insertImageMarkdown() {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Seleccionar imagen");
        fc.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );
        java.io.File file = fc.showOpenDialog(editorArea.getScene().getWindow());
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
     * Establece el callback que se ejecutará cuando cambie el título del capítulo.
     * @param onTitleChanged callback a ejecutar
     */
    public void setOnTitleChanged(Runnable onTitleChanged) {
        this.onTitleChanged = onTitleChanged;
    }
}
