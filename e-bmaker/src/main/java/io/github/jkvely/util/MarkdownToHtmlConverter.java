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
        html = html.replaceAll("((<li>.+?</li>\\n?)+)", "<ul>$1</ul>\n");
        // Procesar diálogos: tanto <...> como '> ...' al inicio de línea
        StringBuilder dialogBuilder = new StringBuilder();
        String[] lines = html.split("\n");        for (String line : lines) {
            String trimmed = line.trim();
            // Sintaxis personalizada <...>acotacion (debe estar todo en una sola línea)
            if (trimmed.startsWith("<") && trimmed.contains(">") && trimmed.length() > 2) {
                int closeIdx = trimmed.indexOf('>');
                // Solo procesar si hay texto después del >
                if (closeIdx < trimmed.length() - 1) {
                    String dialogPart = trimmed.substring(1, closeIdx).trim();
                    String acotacionPart = trimmed.substring(closeIdx + 1).trim();
                    if (dialogPart.matches(".*[A-Za-z0-9].*")) {
                        String dialogHtml = convert(dialogPart);
                        String acotacionHtml = convert(acotacionPart);
                        dialogBuilder.append("<p style='text-indent:2em;'>&mdash; ");
                        dialogBuilder.append(dialogHtml);
                        if (!acotacionHtml.isEmpty()) {
                            dialogBuilder.append(" <span class='acotacion'>&mdash; ");
                            dialogBuilder.append(acotacionHtml);
                            dialogBuilder.append("</span>");
                        }
                        dialogBuilder.append("</p>");
                    } else {
                        dialogBuilder.append(line);
                    }
                } else {
                    // Si solo es <algo> sin acotación, procesar como diálogo simple
                    String dialogPart = trimmed.substring(1, closeIdx).trim();
                    if (dialogPart.matches(".*[A-Za-z0-9].*")) {
                        String dialogHtml = convert(dialogPart);
                        dialogBuilder.append("<p style='text-indent:2em;'>&mdash; ");
                        dialogBuilder.append(dialogHtml);
                        dialogBuilder.append("</p>");
                    } else {
                        dialogBuilder.append(line);
                    }
                }
            // NO procesar > como diálogo nunca - solo texto normal
            } else {
                dialogBuilder.append(line);
            }
            dialogBuilder.append("\n");
        }
        html = dialogBuilder.toString();
        // Saltos de línea
        html = html.replaceAll("\n", "<br/>");
        return html;
    }
}
