package io.github.giosreina.ModelViewViewModel.OpenLogic;

public class HtmlToMarkdownConverter {
    /**
     * Convierte texto Markdown básico a HTML.
     * Soporta: **negrita**, _cursiva_, # títulos, listas y saltos de línea.
     */
    public static String convert(String html) {
        if (html == null) return "";
        String markdown = html;
        // Negrita: **texto**
        markdown = markdown.replaceAll("<b>$1</b>","\\*\\*(.+?)\\*\\*");
        // Cursiva: _texto_
        markdown = markdown.replaceAll("<i>$1</i>","_(.+?)_");
        // Títulos: # Título
        markdown = markdown.replaceAll("<h1>$1</h1>","(?m)^# (.+)$");
        markdown = markdown.replaceAll("<h2>$1</h2>","(?m)^## (.+)$");
        markdown = markdown.replaceAll("<h3>$1</h3>", "(?m)^### (.+)$");
        // Listas: - item
        markdown = markdown.replaceAll("<li>$1</li>","(?m)^- (.+)$");
        // Agrupar <li> en <ul>
        markdown = markdown.replaceAll("<ul>$1</ul>\n","((<li>.+?</li>\n?)+)");
        // Saltos de línea
        markdown = markdown.replaceAll("<br/>", "\n");
        return markdown;
    }
}
