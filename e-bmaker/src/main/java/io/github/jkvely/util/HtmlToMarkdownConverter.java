package io.github.jkvely.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convierte HTML a Markdown, especialmente diálogos literarios.
 * Maneja la conversión inversa de MarkdownToHtmlConverter.
 */
public class HtmlToMarkdownConverter {
    
    /**
     * Convierte una lista de líneas HTML a Markdown.
     */
    public static List<String> convert(List<String> html) {
        List<String> markdown = new ArrayList<>();
        for(String linehtml: html) {
            markdown.add(convertLine(linehtml));
        }
        return markdown;
    }
      /**
     * Convierte una cadena HTML completa a Markdown.
     */
    public static String convert(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        
        String markdown = html;
        
        // Limpiar el HTML primero - remover elementos estructurales del EPUB
        markdown = cleanEpubStructure(markdown);
        
        // Convertir diálogos literarios primero (más específico)
        markdown = convertDialogues(markdown);
        
        // Convertir otros elementos HTML
        markdown = convertBasicElements(markdown);
        
        // Limpiar espacios extra y líneas vacías
        markdown = cleanupText(markdown);
        
        return markdown;
    }
    
    /**
     * Limpia la estructura del EPUB y extrae solo el contenido del libro.
     */
    private static String cleanEpubStructure(String html) {
        String content = html;
        
        // Remover elementos comunes del EPUB que no son contenido
        content = content.replaceAll("(?i)<\\?xml[^>]*>", ""); // XML declaration
        content = content.replaceAll("(?i)<!DOCTYPE[^>]*>", ""); // DOCTYPE
        content = content.replaceAll("(?i)<html[^>]*>", ""); // html tag
        content = content.replaceAll("(?i)</html>", "");
        content = content.replaceAll("(?i)<head>.*?</head>", ""); // head section
        content = content.replaceAll("(?i)<body[^>]*>", ""); // body tag
        content = content.replaceAll("(?i)</body>", "");
        content = content.replaceAll("(?i)<link[^>]*>", ""); // CSS links
        content = content.replaceAll("(?i)<meta[^>]*>", ""); // meta tags
        content = content.replaceAll("(?i)<style[^>]*>.*?</style>", ""); // style sections
        content = content.replaceAll("(?i)<script[^>]*>.*?</script>", ""); // scripts
        
        // Remover divs de estructura/navegación comunes
        content = content.replaceAll("(?i)<div[^>]*class=['\"][^'\"]*nav[^'\"]*['\"][^>]*>.*?</div>", "");
        content = content.replaceAll("(?i)<div[^>]*class=['\"][^'\"]*header[^'\"]*['\"][^>]*>.*?</div>", "");
        content = content.replaceAll("(?i)<div[^>]*class=['\"][^'\"]*footer[^'\"]*['\"][^>]*>.*?</div>", "");
        content = content.replaceAll("(?i)<nav[^>]*>.*?</nav>", "");
        
        return content;
    }
    
    /**
     * Extrae el título del capítulo del HTML.
     */
    public static String extractChapterTitle(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "Capítulo sin título";
        }
        
        // Buscar títulos en orden de prioridad
        String[] titlePatterns = {
            "(?i)<h1[^>]*>(.*?)</h1>",
            "(?i)<h2[^>]*>(.*?)</h2>",
            "(?i)<title[^>]*>(.*?)</title>",
            "(?i)<h3[^>]*>(.*?)</h3>"
        };
        
        for (String pattern : titlePatterns) {
            Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
            Matcher m = p.matcher(html);
            if (m.find()) {
                String title = m.group(1).trim();
                // Limpiar HTML del título
                title = title.replaceAll("<[^>]+>", "").trim();
                if (!title.isEmpty() && !isGenericTitle(title)) {
                    return title;
                }
            }
        }
        
        return "Capítulo sin título";
    }
    
    /**
     * Verifica si un título es genérico (como "Cover", "Title Page", etc.)
     */
    private static boolean isGenericTitle(String title) {
        String lowerTitle = title.toLowerCase();
        String[] genericTitles = {
            "cover", "title page", "copyright", "table of contents", "toc",
            "portada", "índice", "contenido", "derechos", "página de título"
        };
        
        for (String generic : genericTitles) {
            if (lowerTitle.contains(generic)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Limpia el texto final removiendo espacios extra y líneas vacías.
     */
    private static String cleanupText(String text) {
        if (text == null) return "";
        
        // Remover líneas vacías múltiples
        text = text.replaceAll("\n\\s*\n\\s*\n+", "\n\n");
        
        // Remover espacios al inicio y final de líneas
        text = text.replaceAll("(?m)^\\s+", "");
        text = text.replaceAll("(?m)\\s+$", "");
        
        // Remover espacios múltiples
        text = text.replaceAll(" +", " ");
        
        return text.trim();
    }
    
    /**
     * Convierte una sola línea de HTML a Markdown.
     */
    public static String convertLine(String html) {
        return convert(html);
    }
      /**
     * Convierte diálogos literarios de HTML a formato de guiones: - diálogo - acotación - diálogo2 -
     */
    private static String convertDialogues(String html) {
        // Patrón para detectar diálogos: <p style='text-indent:2em;'>&mdash; diálogo &mdash;acotación</p>
        Pattern dialoguePattern = Pattern.compile(
            "<p\\s+style=['\"]text-indent:2em;['\"]>&mdash;\\s*(.*?)\\s*</p>", 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
        
        Matcher matcher = dialoguePattern.matcher(html);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String content = matcher.group(1);
            String converted = parseDialogueContent(content);
            matcher.appendReplacement(result, converted);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }    /**
     * Parsea el contenido de un diálogo HTML y lo convierte al formato de guiones: - diálogo - acotación - diálogo2 -
     */
    private static String parseDialogueContent(String content) {
        // Convertir HTML interno a texto plano pero preservar estructura
        String cleanContent = convertInlineHtmlToMarkdown(content);
        
        // Dividir por &mdash; para identificar segmentos
        String[] parts = cleanContent.split("—");
        
        if (parts.length < 1) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("- ");
        
        boolean hasContent = false;
        
        for (String part : parts) {
            String trimmedPart = part.trim();
            
            if (!trimmedPart.isEmpty()) {
                if (hasContent) {
                    result.append(" - ");
                }
                result.append(trimmedPart);
                hasContent = true;
            }
        }
        
        if (hasContent) {
            result.append(" -");
        } else {
            return "";
        }
        
        return result.toString();
    }
    
    /**
     * Convierte HTML inline (como negrita, cursiva) a Markdown dentro del contenido del diálogo.
     */
    private static String convertInlineHtmlToMarkdown(String html) {
        if (html == null) return "";
        
        String markdown = html;
        
        // Negrita+cursiva: <b><i>texto</i></b> -> ***texto***
        markdown = markdown.replaceAll("<b><i>(.+?)</i></b>", "***$1***");
        markdown = markdown.replaceAll("<i><b>(.+?)</b></i>", "***$1***");
        
        // Negrita: <b>texto</b> -> **texto**
        markdown = markdown.replaceAll("<b>(.+?)</b>", "**$1**");
        
        // Cursiva: <i>texto</i> -> *texto*
        markdown = markdown.replaceAll("<i>(.+?)</i>", "*$1*");
        
        // Reemplazar &mdash; por —
        markdown = markdown.replaceAll("&mdash;", "—");
        
        // Remover otros tags HTML residuales
        markdown = markdown.replaceAll("<[^>]+>", "");
        
        return markdown;
    }
      /**
     * Convierte elementos HTML básicos a Markdown.
     */
    private static String convertBasicElements(String html) {
        String markdown = html;
        
        // Convertir títulos SOLO si no son de estructura
        markdown = markdown.replaceAll("(?i)<h1[^>]*>((?!.*(?:cover|title page|copyright|table of contents|toc|portada|índice|contenido|derechos)).+?)</h1>", "# $1");
        markdown = markdown.replaceAll("(?i)<h2[^>]*>((?!.*(?:cover|title page|copyright|table of contents|toc|portada|índice|contenido|derechos)).+?)</h2>", "## $1");
        markdown = markdown.replaceAll("(?i)<h3[^>]*>((?!.*(?:cover|title page|copyright|table of contents|toc|portada|índice|contenido|derechos)).+?)</h3>", "### $1");
        
        // Remover títulos de estructura que no queremos
        markdown = markdown.replaceAll("(?i)<h[1-6][^>]*>.*?(?:cover|title page|copyright|table of contents|toc|portada|índice|contenido|derechos).*?</h[1-6]>", "");
        
        // Negrita+cursiva: <b><i>texto</i></b> -> ***texto***
        markdown = markdown.replaceAll("<b><i>(.+?)</i></b>", "***$1***");
        markdown = markdown.replaceAll("<i><b>(.+?)</b></i>", "***$1***");
        markdown = markdown.replaceAll("<strong><em>(.+?)</em></strong>", "***$1***");
        markdown = markdown.replaceAll("<em><strong>(.+?)</strong></em>", "***$1***");
        
        // Negrita: <b>texto</b> -> **texto**
        markdown = markdown.replaceAll("<b>(.+?)</b>", "**$1**");
        markdown = markdown.replaceAll("<strong>(.+?)</strong>", "**$1**");
        
        // Cursiva: <i>texto</i> -> *texto*
        markdown = markdown.replaceAll("<i>(.+?)</i>", "*$1*");
        markdown = markdown.replaceAll("<em>(.+?)</em>", "*$1*");
          // Imágenes: <img src='...' alt='...' /> -> ![alt](src)
        markdown = markdown.replaceAll("<img\\s+src=['\"](.+?)['\"]\\s+alt=['\"](.+?)['\"].*?/?>", "![$2]($1)");
        markdown = markdown.replaceAll("<img\\s+alt=['\"](.+?)['\"]\\s+src=['\"](.+?)['\"].*?/?>", "![$1]($2)");
        markdown = markdown.replaceAll("<img\\s+src=['\"](.+?)['\"].*?/?>", "![]($1)");
        
        // Remover elementos de lista sin convertir a Markdown
        // Solo extraemos el contenido sin crear listas con guiones
        markdown = markdown.replaceAll("<li[^>]*>(.+?)</li>", "$1\n");
        markdown = markdown.replaceAll("</?[uo]l[^>]*>", "");
        
        // Saltos de línea
        markdown = markdown.replaceAll("<br\\s*/?>", "\n");
        
        // Párrafos - convertir a saltos de línea dobles pero solo si contienen texto
        markdown = markdown.replaceAll("<p[^>]*>\\s*</p>", ""); // Remover párrafos vacíos
        markdown = markdown.replaceAll("<p[^>]*>", "\n");
        markdown = markdown.replaceAll("</p>", "\n");
        
        // Remover divs estructurales comunes
        markdown = markdown.replaceAll("(?i)<div[^>]*class=['\"][^'\"]*(?:nav|header|footer|toc|copyright)[^'\"]*['\"][^>]*>.*?</div>", "");
        markdown = markdown.replaceAll("</?div[^>]*>", ""); // Remover divs restantes
        
        // Remover spans sin formato especial
        markdown = markdown.replaceAll("<span[^>]*>", "");
        markdown = markdown.replaceAll("</span>", "");
        
        // Remover cualquier otro tag HTML restante
        markdown = markdown.replaceAll("<[^>]+>", "");
          // Limpiar entidades HTML
        markdown = markdown.replaceAll("&nbsp;", " ");
        markdown = markdown.replaceAll("&mdash;", "—");
        markdown = markdown.replaceAll("&ndash;", "–");
        markdown = markdown.replaceAll("&ldquo;", "\"");
        markdown = markdown.replaceAll("&rdquo;", "\"");
        markdown = markdown.replaceAll("&lsquo;", "'");
        markdown = markdown.replaceAll("&rsquo;", "'");
        markdown = markdown.replaceAll("&amp;", "&");
        markdown = markdown.replaceAll("&lt;", "<");
        markdown = markdown.replaceAll("&gt;", ">");
        markdown = markdown.replaceAll("&quot;", "\"");
        
        return markdown;
    }
}
