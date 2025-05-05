package io.github.jkvely.util;

public class MarkdownToHtmlConverter {
    /**
     * Convierte texto Markdown básico a HTML.
     * Soporta: **negrita**, _cursiva_, # títulos, listas y saltos de línea.
     */
    public static String convert(String markdown) {
        if (markdown == null) return "";
        String html = markdown;
        // Negrita: **texto**
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        // Cursiva: _texto_
        html = html.replaceAll("_(.+?)_", "<i>$1</i>");
        // Títulos: # Título
        html = html.replaceAll("(?m)^# (.+)$", "<h1>$1</h1>");
        html = html.replaceAll("(?m)^## (.+)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^### (.+)$", "<h3>$1</h3>");
        // Listas: - item
        html = html.replaceAll("(?m)^- (.+)$", "<li>$1</li>");
        // Agrupar <li> en <ul>
        html = html.replaceAll("((<li>.+?</li>\n?)+)", "<ul>$1</ul>\n");
        // Saltos de línea
        html = html.replaceAll("\n", "<br/>");
        return html;
    }
}
