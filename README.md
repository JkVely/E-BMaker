# 📚 E-BMaker
### *Professional EPUB Editor with Evangelion-Inspired Themes*

<div align="center">

![E-BMaker Logo](https://img.shields.io/badge/E--BMaker-EPUB%20Editor-purple?style=for-the-badge&logo=bookstack&logoColor=white)
![Version](https://img.shields.io/badge/Version-2.0-brightgreen?style=for-the-badge)
![License](https://img.shields.io/badge/License-Open%20Source-blue?style=for-the-badge)

**Una herramienta moderna y elegante para crear y editar EPUBs de forma profesional**

[🚀 Comenzar](#-instalación-rápida) • [📖 Documentación](#-documentación) • [🎨 Temas](#-temas-eva) • [🤝 Contribuir](#-contribuir)

</div>

---

## 🌟 ¿Qué es E-BMaker?

**E-BMaker** es un editor de EPUB de código abierto diseñado para **escritores, editores y creadores de contenido** que buscan una experiencia profesional e intuitiva. Con temas inspirados en **Evangelion** y una interfaz moderna, crear libros electrónicos nunca fue tan elegante.

### ✨ **Características Destacadas**

🎨 **Temas EVA Únicos** → Interfaz inspirada en las unidades EVA-00 y EVA-01  
📝 **Editor Intuitivo** → Markdown + Vista previa en tiempo real  
📚 **Gestión Completa** → Capítulos, metadatos y estructura organizados  
🔄 **Sincronización Automática** → Los cambios se reflejan inmediatamente  
🖥️ **Multiplataforma** → Windows, Linux y macOS  
🆓 **100% Gratuito** → Código abierto bajo licencia permisiva  

---

## 🚀 Instalación Rápida

### ⚙️ Prerrequisitos
- **Java 21+** ([Descargar aquí](https://adoptium.net/))
- **Maven 3.6+** ([Instalación](https://maven.apache.org/install.html))

### 📥 Pasos de Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/JkVely/E-BMaker.git
cd E-BMaker/e-bmaker

# 2. Compilar y ejecutar
mvn clean javafx:run
```

**¡Listo!** 🎉 La aplicación se abrirá con el tema EVA-00 por defecto.

---

## 🎨 Temas EVA

E-BMaker incluye dos temas profesionales inspirados en Neon Genesis Evangelion:

<table>
<tr>
<td align="center" width="50%">

### 🔵 **EVA-00 (Claro)**
*Inspirado en Rei Ayanami*

![EVA-00 Theme](https://img.shields.io/badge/EVA--00-Tema%20Claro-2196F3?style=for-the-badge)

✦ Paleta azul corporativo y naranja  
✦ Ideal para trabajo diurno  
✦ Diseño limpio y profesional  
✦ Óptimo para sesiones largas  

</td>
<td align="center" width="50%">

### 🟣 **EVA-01 (Oscuro)**
*Inspirado en Shinji Ikari*

![EVA-01 Theme](https://img.shields.io/badge/EVA--01-Tema%20Oscuro-9C27B0?style=for-the-badge)

✦ Paleta morado y efectos neón  
✦ Perfecto para trabajo nocturno  
✦ Estilo futurista cinematográfico  
✦ Reduce fatiga ocular  

</td>
</tr>
</table>

**🔄 Cambio instantáneo** entre temas con el botón de la barra superior.

---

## 🛠️ Funcionalidades Principales

<details>
<summary><b>📝 Editor de Contenido</b></summary>

- **Markdown Support**: Escribir con sintaxis Markdown familiar
- **Vista Previa Live**: Ver cambios en tiempo real
- **Autoguardado**: Nunca pierdas tu progreso
- **Navegación Rápida**: Salto entre capítulos instantáneo

</details>

<details>
<summary><b>📚 Gestión de Estructura</b></summary>

- **Organización Visual**: Lista interactiva de capítulos
- **Títulos Dinámicos**: Actualización automática en la estructura
- **Agregar Capítulos**: Un clic para nuevos capítulos
- **Reordenamiento**: Arrastra y suelta (próximamente)

</details>

<details>
<summary><b>🖼️ Editor de Portada</b></summary>

- **Carga de Imágenes**: Drag & drop para portadas
- **Metadatos Completos**: Título, autor, descripción
- **Vista Previa**: Visualización del resultado final
- **Formatos Soportados**: JPG, PNG, WebP

</details>

<details>
<summary><b>⚡ Modos de Vista</b></summary>

- **📝 Editor**: Enfoque total en la escritura
- **👁️ Vista Previa**: Visualización del EPUB final
- **≡ Mitad-Mit**: Editor y previa lado a lado

</details>

---

## 🏗️ Tecnologías y Arquitectura

<div align="center">

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9-red?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

</div>

### 📁 Estructura del Proyecto

```
📁 e-bmaker/
├── 📂 src/main/java/io/github/jkvely/
│   ├── 🎮 controller/        # Controladores de UI
│   ├── 📊 model/            # Modelos de datos (EpubBook, Chapter...)
│   ├── 📚 epub/             # Lógica de manejo EPUB
│   ├── 🛠️ util/             # Utilidades (Markdown, File...)
│   └── 🖼️ viewmodel/        # ViewModels MVVM
├── 📂 src/main/resources/
│   ├── 🎨 styles/           # Temas CSS EVA
│   └── 🖥️ fxml/             # Vistas JavaFX
└── 📂 target/classes/       # Archivos compilados
```

### 🎯 Patrones de Diseño
- **MVVM**: Separación clara de responsabilidades
- **Observer**: Sincronización automática de datos
- **Factory**: Creación de componentes EPUB
- **Strategy**: Múltiples formatos de exportación

---

## 📖 Documentación

| 📚 Documento | 📝 Descripción |
|--------------|---------------|
| [🎨 **Guía de Temas**](THEMES_GUIDE.md) | Manual completo de los temas EVA |

### 🚀 Guías Rápidas

<details>
<summary><b>🖊️ Mi Primer EPUB</b></summary>

1. **Abrir E-BMaker** y seleccionar "Portada"
2. **Añadir título, autor e imagen** de portada
3. **Crear capítulo** con el botón "+ Nuevo capítulo"
4. **Escribir contenido** en Markdown
5. **Exportar** tu EPUB listo para publicar

</details>

<details>
<summary><b>⌨️ Atajos de Teclado</b></summary>

| Atajo | Acción |
|-------|--------|
| `Ctrl + N` | Nuevo capítulo |
| `Ctrl + S` | Guardar proyecto |
| `Ctrl + P` | Vista previa |
| `F11` | Pantalla completa |
| `Ctrl + T` | Cambiar tema |

</details>

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Este proyecto es mantenido por la comunidad.

### 🌟 **Formas de Contribuir**

```bash
# 🍴 Fork del proyecto
git clone https://github.com/tu-usuario/E-BMaker.git

# 🌿 Crear rama para tu feature
git checkout -b feature/nueva-funcionalidad

# 💾 Commit de cambios
git commit -m "✨ Añadir nueva funcionalidad increíble"

# 📤 Push y Pull Request
git push origin feature/nueva-funcionalidad
```

### 📋 **Guidelines**

- ✅ **Código limpio**: Documentar métodos públicos con JavaDoc
- 🧪 **Tests**: Incluir pruebas para nuevas funcionalidades  
- 🎨 **Consistencia**: Seguir los patrones de diseño existentes
- 📝 **Commits**: Usar conventional commits (`feat:`, `fix:`, `docs:`)

### 🐛 **Reportar Bugs**

Encontraste un problema? [Crear issue](https://github.com/JkVely/E-BMaker/issues) con:
- 🖥️ Sistema operativo y versión de Java
- 📸 Screenshots del problema  
- 🔢 Pasos para reproducir
- 🎨 Tema usado (EVA-00 o EVA-01)

---

## 🚀 Roadmap

### 🎯 **Próximas Características**

- [ ] 🌈 **Más temas EVA** (Unidad 02, 08, etc.)
- [ ] 🔍 **Búsqueda y reemplazo** avanzado
- [ ] 📱 **Modo responsive** para ventanas pequeñas
- [ ] 🔗 **Exportación a múltiples formatos** (PDF, MOBI)
- [ ] 🌐 **Internacionalización** (inglés, francés, etc.)
- [ ] 🔌 **Sistema de plugins** extensible

### 💡 **Ideas Futuras**
- 🤖 **AI Writing Assistant** integrado
- ☁️ **Sincronización en la nube**
- 👥 **Colaboración en tiempo real**
- 📊 **Analytics de escritura**

---

## 🏆 Créditos y Reconocimientos

<div align="center">

### 👨‍💻 **Equipo Principal**

<table>
<tr>
<td align="center" width="33%">

![JkVely](https://img.shields.io/badge/Lead%20Developer-JkVely-purple?style=for-the-badge)

**Juan Carlos Quintero Rubiano**  
🎯 Arquitectura y desarrollo principal  
🎨 Diseño de temas EVA  

[GitHub](https://github.com/JkVely) • [LinkedIn](https://linkedin.com/in/jkvely)

</td>
<td align="center" width="33%">

![Giosreina](https://img.shields.io/badge/Co%20Developer-Giosreina-blue?style=for-the-badge)

**Giovanny Sierra Reina**  
🛠️ Desarrollo de funcionalidades  
🧪 Testing y QA  

[GitHub](https://github.com/Giosreina)

</td>
<td align="center" width="33%">

![Community](https://img.shields.io/badge/Community-Contributors-green?style=for-the-badge)

**¡Tú puedes estar aquí!**  
🤝 Contribuciones bienvenidas  
⭐ Dale una estrella al proyecto  

[Contribuir](CONTRIBUTING.md)

</td>
</tr>
</table>

</div>

### 🎊 **Agradecimientos Especiales**

- 🤖 **Hideaki Anno** - Por crear Neon Genesis Evangelion, inspiración de nuestros temas
- 📚 **Comunidad JavaFX** - Por las herramientas excepcionales
- 🌟 **OpenSource Community** - Por hacer posible proyectos como este

---

## 📜 Licencia y Legal

<div align="center">

**E-BMaker** es software libre distribuido bajo licencia permisiva.

[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

```
Copyright (c) 2025 Juan Carlos Quintero Rubiano & Contributors
Permission is hereby granted, free of charge, to any person obtaining a copy...
```

**Uso de Marcas**: Los temas EVA son inspiración artística. Evangelion© pertenece a sus respectivos dueños.

</div>

---

<div align="center">

### 💫 **¡Gracias por usar E-BMaker!**

*"Shinji, get in the robot... y escribe tu EPUB."* 🤖📚

[![Stars](https://img.shields.io/github/stars/JkVely/E-BMaker?style=social)](https://github.com/JkVely/E-BMaker/stargazers)
[![Forks](https://img.shields.io/github/forks/JkVely/E-BMaker?style=social)](https://github.com/JkVely/E-BMaker/network)
[![Issues](https://img.shields.io/github/issues/JkVely/E-BMaker?style=social)](https://github.com/JkVely/E-BMaker/issues)

**[⬆️ Volver al inicio](#-e-bmaker)**

</div>
