package io.github.jkvely.util;

public class MarkdownToHtmlConverter {
    /**
     * Convierte texto Markdown básico a HTML.
     * Soporta: **negrita**, _cursiva_, ***negrita+cursiva***, imágenes, diálogos y listas.
     */
    public static String convert(String markdown) {
        if (markdown == null) return "";
        String html = markdown;
        // Negrita+cursiva: ***texto***
        html = html.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "<b><i>$1</i></b>");
        // Negrita: **texto**
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        // Cursiva: _texto_ o *texto*
        html = html.replaceAll("_(.+?)_", "<i>$1</i>");
        html = html.replaceAll("\\*(.+?)\\*", "<i>$1</i>");
        // Imágenes: ![alt](src)
        html = html.replaceAll("!\\[(.*?)\\]\\((.+?)\\)", "<img src='$2' alt='$1' style='max-width:100%;'/>");
        // Títulos: # Título
        html = html.replaceAll("(?m)^# (.+)$", "<h1>$1</h1>");
        html = html.replaceAll("(?m)^## (.+)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^### (.+)$", "<h3>$1</h3>");
        // Listas: - item
        html = html.replaceAll("(?m)^- (.+)$", "<li>$1</li>");
        // Agrupar <li> en <ul>
        html = html.replaceAll("((<li>.+?</li>\\n?)+)", "<ul>$1</ul>\n");
        // Diálogos tipo libro: > texto → &mdash; texto
        html = html.replaceAll("(?m)^> ?(.+)$", "<p style='text-indent:2em;'><span style='font-weight:bold;'>&mdash;</span> $1</p>");
        // Saltos de línea
        html = html.replaceAll("\n", "<br/>");
        return html;
    }
}
