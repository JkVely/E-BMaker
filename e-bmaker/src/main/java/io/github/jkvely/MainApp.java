package io.github.jkvely;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación E-BMaker.
 * 
 * E-BMaker es un editor de libros EPUB de código abierto diseñado para ser
 * intuitivo y accesible para usuarios no técnicos. La aplicación permite
 * crear, editar y exportar libros en formato EPUB de manera sencilla.
 * 
 * CARACTERÍSTICAS PRINCIPALES:
 * - Editor de texto enriquecido con vista previa en tiempo real
 * - Gestión visual de capítulos y estructura del libro
 * - Dos temas inspirados en Evangelion (EVA-00 claro y EVA-01 oscuro)
 * - Interfaz intuitiva diseñada para escritores no programadores
 * - Exportación directa a formato EPUB estándar
 * 
 * TECNOLOGÍAS UTILIZADAS:
 * - JavaFX para la interfaz gráfica de usuario
 * - Patrón MVVM para separación de responsabilidades
 * - CSS personalizado para temas visuales
 * - Arquitectura modular para fácil mantenimiento
 * 
 * @author Equipo E-BMaker
 * @version 1.0
 * @since JavaFX 8
 */
public class MainApp extends Application {

    /** Referencia estática a la ventana principal para acceso global */
    private static Stage primaryStage;

    /**
     * Método principal de inicio de la aplicación JavaFX.
     * Configura la ventana principal, carga las hojas de estilo y
     * establece el tema por defecto.
     * 
     * @param stage La ventana principal proporcionada por JavaFX
     * @throws Exception Si ocurre un error durante la inicialización
     */@Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        
        Scene scene = new Scene(root);
        
        // Cargar hojas de estilo en orden correcto
        loadStylesheets(scene);
        
        // Configurar ventana principal
        stage.setTitle("E-BMaker - Editor de EPUB");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        
        // Aplicar tema por defecto (claro)
        root.getStyleClass().add("eva-00");
        
        stage.show();
    }
    
    /**
     * Carga las hojas de estilo de la aplicación en el orden correcto.
     * Esta función asegura que todos los temas estén disponibles.
     * 
     * @param scene La escena donde cargar los estilos
     */
    private void loadStylesheets(Scene scene) {
        String[] stylesheets = {
            "/styles/eva-main.css",
            "/styles/eva-00-light-new.css", 
            "/styles/eva-01-dark-new.css"
        };
        
        for (String stylesheet : stylesheets) {
            URL cssUrl = getClass().getResource(stylesheet);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("✓ Cargada hoja de estilo: " + stylesheet);
            } else {
                System.err.println("✗ Error: No se encontró '" + stylesheet + "' en el classpath.");
            }
        }
        
        System.out.println("📄 Total de hojas de estilo cargadas: " + scene.getStylesheets().size());
    }    /**
     * Obtiene una referencia a la ventana principal de la aplicación.
     * Esta función permite el acceso global al Stage principal desde
     * cualquier controlador o componente de la aplicación.
     * 
     * @return La ventana principal de la aplicación
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Punto de entrada principal de la aplicación.
     * 
     * En aplicaciones JavaFX correctamente desplegadas, este método main()
     * es ignorado. Solo sirve como respaldo en caso de que la aplicación
     * no pueda lanzarse a través de los artefactos de despliegue, como
     * puede ocurrir en algunos IDEs con soporte limitado para JavaFX.
     * 
     * NetBeans ignora este método main() cuando la aplicación se ejecuta
     * normalmente.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando E-BMaker - Editor de EPUB...");
        System.out.println("📖 Versión: 1.0");
        System.out.println("🎨 Temas disponibles: EVA-00 (Claro) y EVA-01 (Oscuro)");
        System.out.println("════════════════════════════════════════════════════════");
        
        launch(args);
    }

}
