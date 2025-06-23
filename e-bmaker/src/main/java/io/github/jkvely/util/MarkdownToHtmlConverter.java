package io.github.jkvely.util;

public class MarkdownToHtmlConverter {
    /**
     * Convierte texto Markdown básico a HTML.
     * Soporta: **negrita**, _cursiva_, ***negrita+cursiva***, imágenes, diálogos <...> y listas.
     */
    public static String convert(String markdown) {
        if (markdown == null) return "";
        // Procesar markdown estándar primero
        String html = markdown;
        // Negrita+cursiva: ***texto***
        html = html.replaceAll("\\*\\*\\*(.+?)\\*\\*\\*", "<b><i>$1</i></b>");
        // Negrita: **texto**
        html = html.replaceAll("\\*\\*(.+?)\\*\\*", "<b>$1</b>");
        // Cursiva: *texto* (no precedido ni seguido de asterisco)
        html = html.replaceAll("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)", "<i>$1</i>");
        // Cursiva: _texto_ (no precedido ni seguido de guion bajo)
        html = html.replaceAll("(?<!_)_(?!_)(.+?)(?<!_)_(?!_)", "<i>$1</i>");
        // Imágenes: ![alt](src)
        html = html.replaceAll("!\\[(.*?)\\]\\((.+?)\\)", "<img src='$2' alt='$1' style='max-width:100%;'/>");
        html = html.replaceAll("(?m)^# (.+)$", "<h1>$1</h1>");
        html = html.replaceAll("(?m)^## (.+)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^### (.+)$", "<h3>$1</h3>");
        html = html.replaceAll("(?m)^- (.+)$", "<li>$1</li>");
        html = html.replaceAll("((<li>.+?</li>\\n?)+)", "<ul>$1</ul>\n");
        // Procesar diálogos <...> línea por línea, permitiendo markdown dentro
        StringBuilder dialogBuilder = new StringBuilder();
        String[] lines = html.split("\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("<") && trimmed.contains(">")) {
                int close = trimmed.indexOf('>');
                String dialog = trimmed.substring(1, close).trim();
                String after = trimmed.substring(close+1).trim();
                String result = "<p style='text-indent:2em;'>&mdash; " + dialog;
                if (!after.isEmpty()) {
                    result += " &mdash;" + after;
                }
                result += "</p>";
                dialogBuilder.append(result);
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
