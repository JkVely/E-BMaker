package io.github.jkvely.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * ViewModel principal para el menú principal de la aplicación.
 * Gestiona el estado de la vista central (editor, vista previa, o dividida)
 * siguiendo el patrón Model-View-ViewModel (MVVM).
 */
public class MainMenuViewModel {
    /**
     * Enumera los modos de visualización posibles para el panel central.
     */
    public enum CentralViewMode {
        /** Modo solo editor. */
        EDITOR,
        /** Modo solo vista previa. */
        PREVIEW,
        /** Modo dividido: editor y vista previa. */
        SPLIT
    }

    /** Propiedad observable que representa el modo de visualización actual. */
    private final ObjectProperty<CentralViewMode> viewMode = new SimpleObjectProperty<>(CentralViewMode.EDITOR);

    /**
     * Devuelve la propiedad observable del modo de visualización.
     * Permite el enlace bidireccional con la vista.
     * @return propiedad observable del modo de visualización
     */
    public ObjectProperty<CentralViewMode> viewModeProperty() {
        return viewMode;
    }

    /**
     * Obtiene el modo de visualización actual.
     * @return modo de visualización actual
     */
    public CentralViewMode getViewMode() {
        return viewMode.get();
    }

    /**
     * Establece el modo de visualización actual.
     * @param mode nuevo modo de visualización
     */
    public void setViewMode(CentralViewMode mode) {
        viewMode.set(mode);
    }
}
