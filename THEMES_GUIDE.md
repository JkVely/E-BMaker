# 🎨 Guía de Temas EVA para E-BMaker - v2.0

## 📋 Introducción

E-BMaker ahora incluye un sistema de temas completamente renovado basado en las unidades Evangelion EVA-00 y EVA-01. Esta nueva interfaz está diseñada para ser intuitiva y cómoda para usuarios no programadores.

## ✅ Mejoras Recientes (v2.0)

### 🔧 Problemas Resueltos:
- ❌ **Blur eliminado**: Se quitó el efecto de desenfoque feo en elementos seleccionados del modo oscuro
- 🔘 **Botones como radio**: Los botones de vista ahora funcionan correctamente (solo uno activo)
- 🧹 **CSS limpio**: Se eliminaron archivos CSS redundantes para mejor organización
- 🌑 **Espacios en blanco**: Se corrigieron los fondos del modo oscuro
- 🔄 **Sincronización de títulos**: Los nombres de capítulos se actualizan automáticamente en la estructura

### 📁 **Archivos CSS Actuales:**
```
📁 styles/
├── 📄 eva-variables.css      # Variables globales
├── 📄 eva-main.css          # Estilos base
├── 📄 eva-00-light-new.css  # Tema EVA-00 (claro)  
└── 📄 eva-01-dark-new.css   # Tema EVA-01 (oscuro)
```

*Archivos eliminados: eva-00-light.css, eva-01-dark.css, eva-backup.css, eva.css, variables.css*

## 🚀 Características Principales

### ✨ Temas Disponibles

#### 🔵 **EVA-00 (Tema Claro)**
- **Inspiración**: Rei Ayanami y la unidad EVA-00
- **Paleta de colores**: Azul corporativo y naranja vibrante
- **Estilo**: Limpio, profesional y minimalista
- **Ideal para**: Trabajo durante el día, sesiones largas de escritura

#### 🟣 **EVA-01 (Tema Oscuro)**
- **Inspiración**: Shinji Ikari y la unidad EVA-01
- **Paleta de colores**: Morado profundo con efectos neón
- **Estilo**: Futurista, cinematográfico con efectos de resplandor
- **Ideal para**: Trabajo nocturno, ambiente inmersivo

### 🔄 Cambio de Temas

El cambio entre temas es **instantáneo** y se puede realizar desde:
- **Botón de interruptor** en la barra de herramientas (esquina superior derecha)
- **Iconos visuales**: Sol para EVA-00, Luna para EVA-01
- **Transiciones suaves** entre temas

## 🎯 Mejoras de Usabilidad

### 👤 Para Usuarios No Programadores

1. **Interfaz Intuitiva**
   - Iconos claros y reconocibles
   - Etiquetas en español
   - Navegación simplificada

2. **Retroalimentación Visual**
   - Estados hover claramente definidos
   - Botones con feedback inmediato
   - Indicadores de estado visibles

3. **Accesibilidad**
   - Contraste optimizado para lectura
   - Tamaños de fuente apropiados
   - Navegación por teclado mejorada

### 💻 Características Técnicas

#### 🏗️ Arquitectura CSS Modular

```
📁 styles/
├── 📄 eva-variables.css      # Variables globales y tokens de diseño
├── 📄 eva-main.css          # Estilos base y utilidades
├── 📄 eva-00-light-new.css  # Tema claro EVA-00
└── 📄 eva-01-dark-new.css   # Tema oscuro EVA-01
```

#### 🎨 Sistema de Variables CSS

**EVA-00 (Claro):**
- `--primary-color`: #2196F3 (Azul EVA-00)
- `--accent-color`: #FF5722 (Naranja vibrante)
- `--background`: #FAFAFA (Blanco suave)

**EVA-01 (Oscuro):**
- `--primary-color`: #9C27B0 (Morado EVA-01)
- `--accent-color`: #E91E63 (Rosa neón)
- `--background`: #0D1117 (Negro espacial)

## 🔧 Funcionalidades Avanzadas

### ⚡ Efectos Especiales (Solo EVA-01)

- **Resplandor Neón**: Efectos de glow en elementos interactivos
- **Animaciones Suaves**: Transiciones fluidas en hover
- **Bordes Luminosos**: Contornos que brillan sutilmente

### 📱 Diseño Responsivo

- **Adaptable**: La interfaz se ajusta a diferentes tamaños de ventana
- **Mínimo**: Ventana mínima de 900x600 píxeles
- **Escalable**: Elementos que crecen con el espacio disponible

## 🛠️ Componentes Principales

### 📖 Panel de Estructura EPUB
- Lista navegable de capítulos
- Botón para agregar nuevos capítulos
- Indicador de capítulo seleccionado

### ✏️ Editor Principal
- Área de texto con resaltado
- Campo de título del capítulo
- Barra de herramientas intuitiva

### 👁️ Vista Previa
- Renderizado en tiempo real
- Diseño similar al resultado final
- Panel webview integrado

### 🖼️ Editor de Portada
- Carga de imágenes drag-and-drop
- Vista previa en tiempo real
- Campos para metadatos del libro

## 🎮 Controles de Navegación

### 🔘 Botones de Vista
- **Editor**: Vista de edición pura
- **Vista Previa**: Renderizado del contenido
- **Mitad-Mit**: Vista dividida editor/previa

### ⚙️ Configuraciones
- **Archivo**: Nuevo, Abrir, Guardar, Salir
- **Editar**: Deshacer, Rehacer
- **Ver**: Preferencias y configuraciones

## 🚀 Rendimiento y Optimización

### ⚡ Carga Rápida
- CSS optimizado para carga rápida
- Recursos mínimos requeridos
- Transiciones hardware-accelerated

### 💾 Gestión de Memoria
- Temas lazy-loaded
- Recursos compartidos entre temas
- Limpieza automática de elementos no utilizados

## 🎯 Próximas Características

### 🔮 Futuras Mejoras Planificadas
- **Más temas EVA**: Unidades 02, 08, etc.
- **Personalización avanzada**: Colores personalizables
- **Modo cinema**: Vista immersiva para escritura
- **Atajos de teclado**: Navegación completa por teclado

## 📝 Notas para Desarrolladores

### 🏗️ Estructura de Clases CSS

**Clases Principales:**
- `.eva-00`: Tema claro base
- `.eva-01`: Tema oscuro base
- `.eva-*`: Componentes específicos

**Utilidades:**
- `.spacing-*`: Control de espaciado
- `.text-*`: Tamaños de fuente
- `.align-*`: Alineación de contenido

### 🎨 Personalización

Para personalizar temas, editar las variables en `eva-variables.css`:

```css
:root {
  --custom-primary: #tu-color;
  --custom-accent: #tu-acento;
}
```

## 📞 Soporte

Para reportar problemas o sugerir mejoras:
1. Abrir un issue en el repositorio
2. Incluir screenshots del problema
3. Especificar tema y sistema operativo

---

**¡Disfruta escribiendo con E-BMaker y los temas EVA!** 🎉

*"Shinji, get in the robot... y escribe tu EPUB."* - Gendo Ikari, probablemente
