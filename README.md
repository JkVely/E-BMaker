# E-BMaker

<div style="background-color: #4B0082; color: white; padding: 10px; border-radius: 5px; margin: 15px 0;">
<h2>The Ultimate eBook Creator</h2>
</div>

## About

**E-BMaker** is an open-source desktop application designed for creating and formatting EPUB files with professional quality and ease. Developed by **Juan Carlos Quintero Rubiano (JkVely)**, this tool aims to democratize digital publishing.

<div style="background-color: #543e8c; color: white; padding: 8px; border-radius: 5px; margin: 10px 0; border-left: 5px solid #7c53a3;">
<p>A powerful tool in your creative arsenal</p>
</div>

## Vision

E-BMaker will provide a comprehensive solution for authors, publishers, and content creators who need to:

- Create professionally formatted EPUB files
- Edit existing eBooks with precision
- Generate publication-ready digital books
- Support multiple languages and formats

<div style="background-color: #016930; color: white; padding: 8px; border-radius: 5px; margin: 10px 0; border-left: 5px solid #38761d;">
<p>Precision and power at your fingertips</p>
</div>

## Planned Features

- Intuitive drag-and-drop interface
- Real-time preview of formatting changes
- Automated table of contents generation
- Metadata editing and management
- Template system for consistent styling
- Cross-platform compatibility

<div style="background-color: #a01916; color: white; padding: 8px; border-radius: 5px; margin: 10px 0;">
<p>Transform your content into perfectly formatted eBooks</p>
</div>

## Project Structure

```
e-bmaker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/
│   │   │       └── github/
│   │   │           └── jkvely/
│   │   │               ├── MainApp.java         # Clase principal JavaFX
│   │   │               ├── FXMLController.java  # Controlador de la interfaz
│   │   │               ├── epub/                # (Recomendado) Lógica de manejo de EPUB
│   │   │               ├── model/               # (Recomendado) Modelos de datos
│   │   │               └── util/                # (Recomendado) Utilidades
│   │   └── resources/
│   │       ├── fxml/
│   │       │   └── Scene.fxml                   # Vista principal JavaFX
│   │       └── styles/
│   │           └── Styles.css                   # Estilos visuales
│   └── test/
│       └── java/                                # Pruebas unitarias
├── pom.xml                                      # Configuración Maven
└── README.md
```

- `MainApp.java`: Punto de entrada de la aplicación JavaFX.
- `FXMLController.java`: Controlador de la interfaz gráfica.
- `epub/`: Clases para manejo de archivos EPUB (lectura, escritura, edición).
- `model/`: Modelos de datos (capítulos, metadatos, etc).
- `util/`: Utilidades generales.
- `resources/fxml/`: Archivos FXML para las vistas.
- `resources/styles/`: Hojas de estilo CSS.

## How to Build and Run

1. Instala JDK 11+ y Maven.
2. Ejecuta:
   ```sh
   mvn clean javafx:run
   ```

## Contribution Guidelines

- Sigue la estructura de carpetas propuesta.
- Usa JavaDoc en todas las clases y métodos públicos.
- Los controladores deben estar bien documentados y separados de la lógica de negocio.

## Development Status

This project is currently in the initial planning phase. Contributions, ideas, and feedback are welcome!

---

<div style="background-color: #1a1a1a; color: #f5f5f5; padding: 5px; text-align: center; border-radius: 3px;">
Open Source Project by Juan Carlos Quintero Rubiano (JkVely)
</div>
