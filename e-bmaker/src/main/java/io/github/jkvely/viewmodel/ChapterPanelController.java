package io.github.jkvely.viewmodel;

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

/**
 * Controlador para el panel de edición de capítulos.
 * Solo edición, sin preview, con toolbar, atajos y undo/redo.
 */
public class ChapterPanelController {
    @FXML private TextField chapterTitleField;
    @FXML private TextArea editorArea;
    @FXML private Button boldBtn, italicBtn, quoteBtn, imageBtn, ulBtn, olBtn;

    private EpubChapter chapter;
    private Runnable onTitleChanged; // Callback para notificar cambios de título
    private EditorHistory editorHistory;
    private boolean isRestoringState = false;    @FXML
    public void initialize() {
        // Inicializar historial de undo/redo
        editorHistory = new EditorHistory();
          // Botones Markdown
        if (boldBtn != null) boldBtn.setOnAction(e -> insertMarkdown("**", "**", "negrilla"));
        if (italicBtn != null) italicBtn.setOnAction(e -> insertMarkdown("*", "*", "itálica"));
        if (quoteBtn != null) quoteBtn.setOnAction(e -> insertDialogueMarkdown());
        if (imageBtn != null) imageBtn.setOnAction(e -> insertImageMarkdown());
        if (ulBtn != null) ulBtn.setOnAction(e -> insertBlockMarkdown("- ", "elemento de lista"));
        if (olBtn != null) olBtn.setOnAction(e -> insertBlockMarkdown("1. ", "elemento numerado"));
        
        // Atajos de teclado
        if (editorArea != null) {
            editorArea.addEventFilter(KeyEvent.KEY_PRESSED, this::handleShortcuts);
            
            // Listener para guardar estados en el historial
            editorArea.textProperty().addListener((obs, oldText, newText) -> {
                if (!isRestoringState && newText != null) {
                    Platform.runLater(() -> {
                        editorHistory.saveState(newText, editorArea.getCaretPosition());
                    });
                }
            });
        }
        editorArea.getStyleClass().add("eva-editor-area");
    }    /**
     * Establece el capítulo a editar y configura los listeners.
     * @param chapter capítulo a editar
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
    }    private void handleShortcuts(KeyEvent event) {
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
    }    /**
     * Inserta sintaxis Markdown para imagen, abriendo un selector de archivos nativo.
     * Copia la imagen al proyecto y usa la ruta relativa.
     */
    private void insertImageMarkdown() {
        String selectedImagePath = io.github.jkvely.util.NativeFileManager.selectImageFile();
        
        if (selectedImagePath != null) {
            java.io.File selectedFile = new java.io.File(selectedImagePath);
            
            // Verificar que es un formato de imagen soportado
            if (!io.github.jkvely.util.ImageManager.isSupportedImageFormat(selectedFile)) {
                System.err.println("Formato de imagen no soportado: " + selectedFile.getName());
                return;
            }
            
            try {
                // Obtener la ruta del proyecto desde MainMenuController
                String projectPath = io.github.jkvely.viewmodel.MainMenuController.getCurrentProjectPath();
                
                String imageReference;
                if (projectPath != null && !projectPath.trim().isEmpty()) {
                    // Copiar imagen al proyecto y obtener referencia relativa
                    imageReference = io.github.jkvely.util.ImageManager.copyImageToProject(selectedFile, projectPath);
                    System.out.println("📁 Imagen copiada al proyecto: " + imageReference);
                } else {
                    // Si no hay proyecto, usar referencia directa al archivo
                    imageReference = selectedFile.getName();
                    System.out.println("⚠️ No hay proyecto activo, usando referencia directa: " + imageReference);
                }
                
                // Insertar markdown con la referencia apropiada
                String imageMarkdown = "![](" + imageReference + ")";
                int caret = editorArea.getCaretPosition();
                String before = editorArea.getText(0, caret);
                String after = editorArea.getText(caret, editorArea.getLength());
                editorArea.setText(before + imageMarkdown + after);
                editorArea.positionCaret(before.length() + imageMarkdown.length());
                
            } catch (Exception e) {
                System.err.println("Error al procesar la imagen: " + e.getMessage());
                e.printStackTrace();
                // Fallback: usar referencia directa
                String imageMarkdown = "![](" + selectedFile.getName() + ")";
                int caret = editorArea.getCaretPosition();
                String before = editorArea.getText(0, caret);
                String after = editorArea.getText(caret, editorArea.getLength());
                editorArea.setText(before + imageMarkdown + after);
                editorArea.positionCaret(before.length() + imageMarkdown.length());
            }
        }
    }/**
     * Inserta sintaxis de diálogo literario con acotación: - diálogo - acotación -
     */
    private void insertDialogueMarkdown() {
        int start = editorArea.getSelection().getStart();
        int end = editorArea.getSelection().getEnd();
        String selected = editorArea.getSelectedText();
        
        // Si no hay selección, insertar plantilla
        if (selected.isEmpty()) {
            String before = editorArea.getText(0, start);
            String after = editorArea.getText(end, editorArea.getLength());
            editorArea.setText(before + "- diálogo - acotación -" + after);
            editorArea.positionCaret(before.length() + 2); // Cursor después de "- "
        } else {
            // Si hay salto de línea en la selección, tomar la primera línea como diálogo y el resto como acotación
            String[] parts = selected.split("\\n", 2);
            String dialogo = parts[0].trim();
            String acotacion = parts.length > 1 ? parts[1].trim() : "acotación";
            
            String before = editorArea.getText(0, start);
            String after = editorArea.getText(end, editorArea.getLength());
            String insert = "- " + dialogo + " - " + acotacion + " -";
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

    /**
     * Establece el callback que se ejecutará cuando cambie el título del capítulo.
     * @param onTitleChanged callback a ejecutar
     */
    public void setOnTitleChanged(Runnable onTitleChanged) {
        this.onTitleChanged = onTitleChanged;
    }
}
