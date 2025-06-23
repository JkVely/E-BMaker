package io.github.jkvely.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementa el patrón Memento para manejar el historial de cambios en el editor.
 * Permite funcionalidad de deshacer (undo) y rehacer (redo).
 */
public class EditorHistory {
    
    /**
     * Memento que almacena el estado del texto en un momento dado.
     */
    public static class TextMemento {
        private final String text;
        private final int caretPosition;
        private final long timestamp;
        
        public TextMemento(String text, int caretPosition) {
            this.text = text;
            this.caretPosition = caretPosition;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getText() {
            return text;
        }
        
        public int getCaretPosition() {
            return caretPosition;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TextMemento that = (TextMemento) obj;
            return text.equals(that.text);
        }
    }
    
    private final List<TextMemento> history;
    private int currentIndex;
    private final int maxHistorySize;
    private boolean isUndoRedoInProgress;
    
    public EditorHistory() {
        this(100); // Por defecto, mantener 100 estados
    }
    
    public EditorHistory(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
        this.history = new ArrayList<>();
        this.currentIndex = -1;
        this.isUndoRedoInProgress = false;
    }
    
    /**
     * Guarda un nuevo estado en el historial.
     * @param text el texto actual
     * @param caretPosition la posición actual del cursor
     */
    public void saveState(String text, int caretPosition) {
        // Evitar guardar estados durante operaciones de undo/redo
        if (isUndoRedoInProgress) {
            return;
        }
        
        TextMemento newMemento = new TextMemento(text, caretPosition);
        
        // No guardar si es idéntico al estado actual
        if (!history.isEmpty() && currentIndex >= 0 && 
            history.get(currentIndex).equals(newMemento)) {
            return;
        }
        
        // Eliminar todos los estados después del índice actual (perdemos el "redo")
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);
        }
        
        // Agregar el nuevo estado
        history.add(newMemento);
        currentIndex++;
        
        // Limitar el tamaño del historial
        while (history.size() > maxHistorySize) {
            history.remove(0);
            currentIndex--;
        }
    }
    
    /**
     * Deshace el último cambio.
     * @return el memento anterior, o null si no hay nada que deshacer
     */
    public TextMemento undo() {
        if (!canUndo()) {
            return null;
        }
        
        isUndoRedoInProgress = true;
        currentIndex--;
        TextMemento memento = history.get(currentIndex);
        isUndoRedoInProgress = false;
        
        return memento;
    }
    
    /**
     * Rehace el último cambio deshecho.
     * @return el memento siguiente, o null si no hay nada que rehacer
     */
    public TextMemento redo() {
        if (!canRedo()) {
            return null;
        }
        
        isUndoRedoInProgress = true;
        currentIndex++;
        TextMemento memento = history.get(currentIndex);
        isUndoRedoInProgress = false;
        
        return memento;
    }
    
    /**
     * @return true si se puede deshacer un cambio
     */
    public boolean canUndo() {
        return currentIndex > 0;
    }
    
    /**
     * @return true si se puede rehacer un cambio
     */
    public boolean canRedo() {
        return currentIndex < history.size() - 1;
    }
    
    /**
     * Limpia todo el historial.
     */
    public void clear() {
        history.clear();
        currentIndex = -1;
    }
    
    /**
     * @return el estado actual del historial para depuración
     */
    public String getHistoryState() {
        return String.format("History size: %d, Current index: %d, Can undo: %s, Can redo: %s", 
                           history.size(), currentIndex, canUndo(), canRedo());
    }
}
