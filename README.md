# E-BMaker

## Languages and Technologies

This project is built using the following programming languages and tools:

[![Java](https://skillicons.dev/icons?i=java)](https://skillicons.dev)
[![Maven](https://skillicons.dev/icons?i=maven)](https://skillicons.dev)
[![Languages](https://skillicons.dev/icons?i=html,css)](https://skillicons.dev)

## Platforms

This application is compatible with the following operating systems:

[![Windows](https://skillicons.dev/icons?i=windows)](https://skillicons.dev)
[![Linux](https://skillicons.dev/icons?i=linux)](https://skillicons.dev)

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
2. Clone the repository:

   ```sh
   git clone https://github.com/JkVely/E-BMaker.git
   cd E-BMaker
   ```

3. Run:

   ```sh
   cd e-bmaker
   mvn clean javafx:run
   ```

## Contribution Guidelines

- Follow the proposed folder structure.
- Use JavaDoc for all public classes and methods.
- Keep controllers well documented and separated from business logic.

---

<div style="background-color: #1a1a1a; color: #39ff14; padding: 5px; text-align: center; border-radius: 3px;">
   Open Source Project by:
   <ul style="list-style-type: none; padding: 0 0 0 20px; margin: 0; color: #ffffff;">
      <li>Juan Carlos Quintero Rubiano (<b><a href="https://github.com/JkVely">JkVely</a></b>)</li>
      <li>Giovanny Sierra Reina (<b><a href="https://github.com/Giosreina">JkVely</a></b>)</li>
      <li>Contributors are welcome!</li>
   </ul>
</div>
