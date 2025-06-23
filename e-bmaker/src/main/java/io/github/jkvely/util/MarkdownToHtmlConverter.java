package io.github.jkvely.util;

public class MarkdownToHtmlConverter {
    /**
     * Convierte texto Markdown básico a HTML.
     * Soporta: **negrita**, _cursiva_, ***negrita+cursiva***, imágenes, diálogos con guiones y listas.
     * Diálogo con acotación: - diálogo - acotación - diálogo2 - → — diálogo —acotación— diálogo2
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
        html = html.replaceAll("((<li>.+?</li>\\n?)+)", "<ul>$1</ul>\n");        
        
        // Procesar diálogos con nuevo formato de guiones
        StringBuilder dialogBuilder = new StringBuilder();
        String[] lines = html.split("\n");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            
            // Nuevo formato: líneas que empiezan con - y contienen diálogos
            if (trimmed.startsWith("- ") && containsDialoguePattern(trimmed)) {
                String processedLine = parseDialogueLineWithDashes(trimmed);
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
    }    /**
     * Verifica si una línea contiene un patrón de diálogo con guiones.
     * El patrón debe ser: - texto - texto - (puede tener más segmentos)
     */
    private static boolean containsDialoguePattern(String line) {
        if (line == null || !line.startsWith("- ")) return false;
        
        // Contar guiones que no estén al principio
        String afterFirstDash = line.substring(2); // Remove "- "
        int dashCount = afterFirstDash.split(" - ", -1).length - 1;
        
        // Debe tener al menos un guión más (para la acotación)
        return dashCount >= 1;
    }

    /**
     * Parsea una línea que contiene diálogos y acotaciones con formato de guiones.
     * Formato: - diálogo - acotación - diálogo2 -
     * Resultado: — diálogo —acotación— diálogo2
     */
    private static String parseDialogueLineWithDashes(String line) {
        if (line == null || line.trim().isEmpty() || !line.startsWith("- ")) return null;
        
        // Remover el "- " inicial
        String content = line.substring(2);
        
        // Dividir por " - " pero conservar los separadores
        String[] parts = content.split(" - ");
        
        if (parts.length < 2) return null; // Necesita al menos diálogo y acotación
        
        StringBuilder result = new StringBuilder();
        result.append("<p style='text-indent:2em;'>&mdash; ");
        
        boolean foundValidContent = false;
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            
            // Si es el último elemento y termina con -, remover el guión final
            if (i == parts.length - 1 && part.endsWith("-")) {
                part = part.substring(0, part.length() - 1).trim();
            }
            
            if (!part.isEmpty() && part.matches(".*[A-Za-z0-9].*")) {
                foundValidContent = true;
                
                if (i == 0) {
                    // Primer diálogo
                    result.append(processMarkdownInline(part));
                } else if (i % 2 == 1) {
                    // Acotaciones (índices impares)
                    result.append(" &mdash;");
                    result.append(processMarkdownInline(part));
                } else {
                    // Diálogos adicionales (índices pares > 0)
                    result.append("&mdash; ");
                    result.append(processMarkdownInline(part));
                }
            }
        }
        
        result.append("</p>");
        
        // Solo retornar si encontramos contenido válido
        return foundValidContent ? result.toString() : null;
    }
}
