# E-BMaker

<div style="background-color: #2e0854; color: #39ff14; padding: 10px; border-radius: 5px; margin: 15px 0;">
<h2>The Ultimate eBook Creator</h2>
</div>

## About

**E-BMaker** is an open-source desktop application designed for creating and editing EPUB files with professional quality and ease. Developed by **Juan Carlos Quintero Rubiano (JkVely)**, this tool aims to democratize digital publishing.

<div style="background-color: #4b0082; color: #e0e0e0; padding: 8px; border-radius: 5px; margin: 10px 0; border-left: 5px solid #7c53a3;">
A powerful tool in your creative arsenal
</div>

## Vision

E-BMaker provides a comprehensive solution for authors, publishers, and content creators who need to:

- Create professionally formatted EPUB files
- Edit existing eBooks with precision
- Generate publication-ready digital books
- Support multiple languages and formats

<div style="background-color: #016930; color: #e0e0e0; padding: 8px; border-radius: 5px; margin: 10px 0; border-left: 5px solid #39ff14;">
Precision and power at your fingertips
</div>

## Planned Features

- Intuitive drag-and-drop interface
- Real-time preview of formatting changes
- Automated table of contents generation
- Metadata editing and management
- Template system for consistent styling
- Cross-platform compatibility

<div style="background-color: #a01916; color: #e0e0e0; padding: 8px; border-radius: 5px; margin: 10px 0;">
Transform your content into perfectly formatted eBooks
</div>

## Project Folder Structure

```text
 e-bmaker/
 ├── src/
 │   ├── main/
 │   │   ├── java/
 │   │   │   └── io/
 │   │   │       └── github/
 │   │   │           └── jkvely/
 │   │   │               ├── epub/
 │   │   │               ├── model/
 │   │   │               └── util/
 │   │   └── resources/
 │   │       ├── fxml/
 │   │       └── styles/
 │   └── test/
 │       └── java/
 └── target/
```

- `epub/`: EPUB file handling logic
- `model/`: Data models (chapters, metadata, etc.)
- `util/`: General utilities
- `resources/fxml/`: FXML files for JavaFX views
- `resources/styles/`: CSS stylesheets

## How to Build and Run

1. Install JDK 11+ and Maven.
2. Run:
   ```sh
   mvn clean javafx:run
   ```

## Contribution Guidelines

- Follow the proposed folder structure.
- Use JavaDoc for all public classes and methods.
- Keep controllers well documented and separated from business logic.

---

<div style="background-color: #1a1a1a; color: #39ff14; padding: 5px; text-align: center; border-radius: 3px;">
Open Source Project by Juan Carlos Quintero Rubiano (JkVely)
</div>
