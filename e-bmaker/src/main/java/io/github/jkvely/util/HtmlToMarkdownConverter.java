package io.github.jkvely.util;

import java.util.List;
import java.util.ArrayList;

public class HtmlToMarkdownConverter {
    public static List<String> convert(List<String> html) {
        List<String> markdown = new ArrayList<>();
        for(String linehtml: html) {
            String linemarkdown = linehtml;
            // Negrita: **texto**
            linemarkdown = linemarkdown.replaceAll("<b>$1</b>","\\*\\*(.+?)\\*\\*");
            // Cursiva: _texto_
            linemarkdown = linemarkdown.replaceAll("<i>$1</i>","_(.+?)_");
            // Títulos: # Título
            linemarkdown = linemarkdown.replaceAll("<h1>$1</h1>","(?m)^# (.+)$");
            linemarkdown = linemarkdown.replaceAll("<h2>$1</h2>","(?m)^## (.+)$");
            linemarkdown = linemarkdown.replaceAll("<h3>$1</h3>", "(?m)^### (.+)$");
            // Listas: - item
            linemarkdown = linemarkdown.replaceAll("<li>$1</li>","(?m)^- (.+)$");
            // Agrupar <li> en <ul>
            linemarkdown = linemarkdown.replaceAll("<ul>$1</ul>\n","((<li>.+?</li>\n?)+)");
            // Saltos de línea
            linemarkdown = linemarkdown.replaceAll("<br/>", "\n");

            linemarkdown = linemarkdown.replaceAll("<p>", " ");
            linemarkdown = linemarkdown.replaceAll("</p>", " ");

            linemarkdown = linemarkdown.replaceAll("<section>", " ");
            linemarkdown = linemarkdown.replaceAll("</section>", " ");

            linemarkdown = linemarkdown.replaceAll("<body>", " ");
            linemarkdown = linemarkdown.replaceAll("</body>", " ");

            linemarkdown = linemarkdown.replaceAll("<html>", " ");
            linemarkdown = linemarkdown.replaceAll("</html>", " ");

            linemarkdown = linemarkdown.replaceAll("<div>", " ");
            linemarkdown = linemarkdown.replaceAll("</div>", " ");

            linemarkdown = linemarkdown.replaceAll("<head>", " ");
            linemarkdown = linemarkdown.replaceAll("</head>", " ");

            linemarkdown = linemarkdown.replaceAll("<footer>", " ");
            linemarkdown = linemarkdown.replaceAll("</footer>", " ");
            
            markdown.add(linemarkdown);

        }
        return markdown;
    }
}
