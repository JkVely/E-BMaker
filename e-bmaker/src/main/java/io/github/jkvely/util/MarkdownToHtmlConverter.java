package io.github.jkvely.util;

public class MarkdownToHtmlConverter {
    /**
     * Convierte texto Markdown básico a HTML.
     * Soporta: **negrita**, _cursiva_, ***negrita+cursiva***, imágenes, diálogos <...> o > ... y listas.
     * Diálogo con acotación: <Diálogo> acotación → — Diálogo —acotación
     */
    public static String convert(String markdown) {
        if (markdown == null) return "";
        // Procesar markdown estándar primero
        String html = markdown;
        // Negrita+cursiva: ***texto***
        html = html.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "<b><i>$1</i></b>");
        // Negrita: **texto**
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        // Cursiva: *texto* (solo si hay al menos un carácter alfanumérico)
        html = html.replaceAll("(?<!\\*)\\*([A-Za-z0-9][^*]*[A-Za-z0-9])\\*(?!\\*)", "<i>$1</i>");
        // Cursiva: _texto_ (solo si hay al menos un carácter alfanumérico)
        html = html.replaceAll("(?<!_)_([A-Za-z0-9][^_]*[A-Za-z0-9])_(?!_)", "<i>$1</i>");
        // Imágenes: ![alt](src)
        html = html.replaceAll("!\\[(.*?)\\]\\((.+?)\\)", "<img src='$2' alt='$1' style='max-width:100%;'/>");
        html = html.replaceAll("(?m)^# (.+)$", "<h1>$1</h1>");
        html = html.replaceAll("(?m)^## (.+)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^### (.+)$", "<h3>$1</h3>");
        html = html.replaceAll("(?m)^- (.+)$", "<li>$1</li>");
        html = html.replaceAll("((<li>.+?</li>\\n?)+)", "<ul>$1</ul>\n");        // Procesar diálogos: tanto <...> como '> ...' al inicio de línea
        StringBuilder dialogBuilder = new StringBuilder();
        String[] lines = html.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            
            // Sintaxis personalizada <...>acotacion (debe estar todo en una sola línea)
            if (trimmed.startsWith("<") && trimmed.contains(">") && trimmed.length() > 2) {
                String processedLine = parseDialogueLine(trimmed);
                if (processedLine != null) {
                    dialogBuilder.append(processedLine);
                    // No agregar \n después de elementos de bloque como <p>
                } else {
                    dialogBuilder.append(line);
                    // Solo agregar \n si no es la última línea
                    if (i < lines.length - 1) {
                        dialogBuilder.append("\n");
                    }
                }
            // NO procesar > como diálogo nunca - solo texto normal
            } else {
                dialogBuilder.append(line);
                // Solo agregar \n si no es la última línea
                if (i < lines.length - 1) {
                    dialogBuilder.append("\n");
                }
            }
        }
        html = dialogBuilder.toString();
        // Convertir saltos de línea solo para texto normal (no para elementos de bloque)
        html = html.replaceAll("(?<!>)\n(?!<)", "<br/>");
        return html;
    }
    
    /**
     * Procesa markdown básico en línea sin recursión completa ni saltos de línea.
     * Solo para texto dentro de diálogos y acotaciones.
     */
    private static String processMarkdownInline(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        
        String html = text;
        // Negrita+cursiva: ***texto***
        html = html.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "<b><i>$1</i></b>");
        // Negrita: **texto**
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        // Cursiva: *texto* (solo si hay al menos un carácter alfanumérico)
        html = html.replaceAll("(?<!\\*)\\*([A-Za-z0-9][^*]*[A-Za-z0-9])\\*(?!\\*)", "<i>$1</i>");
        // Cursiva: _texto_ (solo si hay al menos un carácter alfanumérico)
        html = html.replaceAll("(?<!_)_([A-Za-z0-9][^_]*[A-Za-z0-9])_(?!_)", "<i>$1</i>");
        
        return html;
    }

    /**
     * Parsea una línea que contiene diálogos y acotaciones.
     * Maneja múltiples segmentos como: <Hola> exclamo <como estas?>
     * Resultado: — Hola —exclamo— como estas?
     */
    private static String parseDialogueLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        
        StringBuilder result = new StringBuilder();
        result.append("<p style='text-indent:2em;'>&mdash; ");
        
        int i = 0;
        boolean foundValidDialogue = false;
        
        while (i < line.length()) {
            if (line.charAt(i) == '<') {
                // Buscar el cierre del diálogo
                int closeIdx = line.indexOf('>', i + 1);
                if (closeIdx != -1) {
                    String dialogPart = line.substring(i + 1, closeIdx).trim();
                    
                    // Verificar que el diálogo tenga contenido alfanumérico
                    if (dialogPart.matches(".*[A-Za-z0-9].*")) {
                        foundValidDialogue = true;
                        
                        // Si no es el primer diálogo, agregar una raya
                        if (result.length() > "<p style='text-indent:2em;'>&mdash; ".length()) {
                            result.append("&mdash;");
                        }
                        
                        // Agregar el diálogo procesado
                        result.append(processMarkdownInline(dialogPart));
                        
                        i = closeIdx + 1;
                        
                        // Buscar texto de acotación hasta el siguiente < o final de línea
                        StringBuilder acotacion = new StringBuilder();
                        while (i < line.length() && line.charAt(i) != '<') {
                            acotacion.append(line.charAt(i));
                            i++;
                        }
                        
                        String acotacionText = acotacion.toString().trim();
                        if (!acotacionText.isEmpty()) {
                            result.append(" &mdash;");
                            result.append(processMarkdownInline(acotacionText));
                        }
                    } else {
                        // No es un diálogo válido, saltar
                        i = closeIdx + 1;
                    }
                } else {
                    // No hay cierre, saltar este carácter
                    i++;
                }
            } else {
                i++;
            }
        }
        
        result.append("</p>");
        
        // Solo retornar si encontramos al menos un diálogo válido
        return foundValidDialogue ? result.toString() : null;
    }
}
