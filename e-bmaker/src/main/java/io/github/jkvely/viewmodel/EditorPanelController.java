package io.github.jkvely.viewmodel;

import java.io.File;

import io.github.jkvely.model.Classes.EpubChapter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

/**
 * Controlador para el panel de edición de capítulos.
 * Barra de herramientas tipo GitHub, atajos de teclado y MVVM.
 */
public class EditorPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private Button boldBtn, italicBtn, quoteBtn, imageBtn, ulBtn, olBtn, dialogueBtn;

    private EpubChapter chapter;

    /**
     * Inicializa el controlador, listeners de barra y atajos de teclado.
     */    @FXML
    public void initialize() {
        // Botones Markdown
        boldBtn.setOnAction(e -> insertMarkdown("**", "**", "negrilla"));
        italicBtn.setOnAction(e -> insertMarkdown("*", "*", "itálica"));
        quoteBtn.setOnAction(e -> insertMarkdown("*", "*", "texto en cursiva"));
        imageBtn.setOnAction(e -> insertImageMarkdown());
        ulBtn.setOnAction(e -> insertBlockMarkdown("- ", "elemento de lista"));
        olBtn.setOnAction(e -> insertBlockMarkdown("1. ", "elemento numerado"));
        dialogueBtn.setOnAction(e -> insertDialogueMarkdown());

        // Atajos de teclado estilo GitHub
        editorArea.addEventFilter(KeyEvent.KEY_PRESSED, this::handleShortcuts);
        editorArea.getStyleClass().add("eva-editor-area");
    }

    /**
     * Asigna el capítulo a editar y sincroniza UI.
     * @param chapter el capítulo a editar
     */
    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            chapterTitleField.setText(chapter.getTitle());
            editorArea.setText(chapter.getContent());
            chapterTitleField.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setTitle(val);
            });
            editorArea.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setContent(val);
            });
        }
    }

    /**
     * Maneja los atajos de teclado para formato Markdown.
     * @param event evento de teclado
     */
    private void handleShortcuts(KeyEvent event) {        if (new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("**", "**", "negrilla");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("*", "*", "itálica");
            event.consume();        } else if (new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("*", "*", "texto en cursiva");
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
        } else if (new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN).match(event)) {
            insertDialogueMarkdown();
            event.consume();
        }
    }

    /**
     * Inserta formato Markdown en la selección actual.
     * @param prefix prefijo Markdown (ej: **)
     * @param suffix sufijo Markdown (ej: **)
     * @param placeholder texto de ejemplo
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
     * @param prefix prefijo (ej: > , - , 1. )
     * @param placeholder texto de ejemplo
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
     * Inserta sintaxis de diálogo literario con acotación: <Diálogo> acotación
     */
    private void insertDialogueMarkdown() {
        int start = editorArea.getSelection().getStart();
        int end = editorArea.getSelection().getEnd();
        String selected = editorArea.getSelectedText();
        // Si no hay selección, poner <dialogo>acotacion
        if (selected.isEmpty()) {
            String before = editorArea.getText(0, start);
            String after = editorArea.getText(end, editorArea.getLength());
            editorArea.setText(before + "<diálogo> acotación" + after);
            editorArea.positionCaret(before.length() + 8); // Cursor después de <diálogo>
        } else {
            // Si hay salto de línea en la selección, solo tomar la primera línea como diálogo y el resto como acotación
            String[] parts = selected.split("\\n", 2);
            String dialogo = parts[0];
            String acotacion = parts.length > 1 ? parts[1] : "acotación";
            String before = editorArea.getText(0, start);
            String after = editorArea.getText(end, editorArea.getLength());
            String insert = "<" + dialogo + "> " + acotacion;
            editorArea.setText(before + insert + after);
            editorArea.positionCaret(before.length() + insert.length());
        }
    }
}