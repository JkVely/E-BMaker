package io.github.jkvely.viewmodel;

import java.io.File;

import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.util.EditorHistory;
import io.github.jkvely.util.EditorHistory.TextMemento;
import javafx.application.Platform;
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
 * Barra de herramientas tipo GitHub, atajos de teclado, undo/redo y MVVM.
 */
public class EditorPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private Button boldBtn, italicBtn, quoteBtn, imageBtn, ulBtn, olBtn;

    private EpubChapter chapter;
    private EditorHistory editorHistory;
    private boolean isRestoringState = false;    /**
     * Inicializa el controlador, listeners de barra y atajos de teclado.
     */    @FXML
    public void initialize() {
        // Inicializar historial de undo/redo
        editorHistory = new EditorHistory();
          // Botones Markdown
        boldBtn.setOnAction(e -> insertMarkdown("**", "**", "negrilla"));
        italicBtn.setOnAction(e -> insertMarkdown("*", "*", "itálica"));
        quoteBtn.setOnAction(e -> insertDialogueMarkdown());
        imageBtn.setOnAction(e -> insertImageMarkdown());
        ulBtn.setOnAction(e -> insertBlockMarkdown("- ", "elemento de lista"));
        olBtn.setOnAction(e -> insertBlockMarkdown("1. ", "elemento numerado"));

        // Atajos de teclado estilo GitHub
        editorArea.addEventFilter(KeyEvent.KEY_PRESSED, this::handleShortcuts);
        
        // Listener para guardar estados en el historial
        editorArea.textProperty().addListener((obs, oldText, newText) -> {
            if (!isRestoringState && newText != null) {
                // Usar Platform.runLater para que el caret esté en la posición correcta
                Platform.runLater(() -> {
                    editorHistory.saveState(newText, editorArea.getCaretPosition());
                });
            }
        });
        
        editorArea.getStyleClass().add("eva-editor-area");
    }    /**
     * Asigna el capítulo a editar y sincroniza UI.
     * @param chapter el capítulo a editar
     */
    public void setChapter(EpubChapter chapter) {
        this.chapter = chapter;
        if (chapter != null) {
            chapterTitleField.setText(chapter.getTitle());
            
            // Restaurar el texto sin disparar el historial
            isRestoringState = true;
            editorArea.setText(chapter.getContent());
            isRestoringState = false;
            
            // Guardar estado inicial en el historial
            editorHistory.clear();
            editorHistory.saveState(chapter.getContent(), 0);
            
            chapterTitleField.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setTitle(val);
            });
            editorArea.textProperty().addListener((obs, old, val) -> {
                if (this.chapter != null) this.chapter.setContent(val);
            });
        }
    }    /**
     * Maneja los atajos de teclado para formato Markdown y undo/redo.
     * @param event evento de teclado
     */
    private void handleShortcuts(KeyEvent event) {
        // Undo/Redo
        if (new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN).match(event)) {
            performUndo();
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN).match(event)) {
            performRedo();
            event.consume();
        } 
        // Formato Markdown
        else if (new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("**", "**", "negrilla");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN).match(event)) {
            insertMarkdown("*", "*", "itálica");
            event.consume();        } else if (new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN).match(event)) {
            insertDialogueMarkdown();
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN).match(event)) {
            insertBlockMarkdown("- ", "elemento de lista");
            event.consume();
        } else if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
            insertBlockMarkdown("1. ", "elemento numerado");
            event.consume();        } else if (new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
            insertImageMarkdown();
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
    }    /**
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

    /**
     * Deshace el último cambio en el editor.
     */
    private void performUndo() {
        TextMemento memento = editorHistory.undo();
        if (memento != null) {
            restoreState(memento);
        }
    }

    /**
     * Rehace el último cambio deshecho en el editor.
     */
    private void performRedo() {
        TextMemento memento = editorHistory.redo();
        if (memento != null) {
            restoreState(memento);
        }
    }

    /**
     * Restaura el estado del editor desde un memento.
     * @param memento el estado a restaurar
     */
    private void restoreState(TextMemento memento) {
        isRestoringState = true;
        
        // Restaurar el texto
        editorArea.setText(memento.getText());
        
        // Restaurar la posición del cursor
        int caretPos = Math.min(memento.getCaretPosition(), editorArea.getLength());
        editorArea.positionCaret(caretPos);
        
        // Actualizar el capítulo si existe
        if (chapter != null) {
            chapter.setContent(memento.getText());
        }
        
        isRestoringState = false;
    }
}