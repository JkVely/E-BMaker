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
        
        // Convertir diálogos literarios primero (más específico)
        markdown = convertDialogues(markdown);
        
        // Convertir otros elementos HTML
        markdown = convertBasicElements(markdown);
        
        return markdown;
    }
    
    /**
     * Convierte una sola línea de HTML a Markdown.
     */
    public static String convertLine(String html) {
        return convert(html);
    }
    
    /**
     * Convierte diálogos literarios de HTML a formato <diálogo> acotación.
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
    }
    
    /**
     * Parsea el contenido de un diálogo HTML y lo convierte al formato <diálogo> acotación.
     */
    private static String parseDialogueContent(String content) {
        // Remover tags HTML internos para obtener texto plano
        String plainText = removeHtmlTags(content);
        
        // Separar por &mdash; para identificar segmentos
        String[] parts = plainText.split("—");
        
        if (parts.length < 2) {
            // Solo diálogo sin acotación
            return "<" + plainText.trim() + ">";
        }
        
        StringBuilder result = new StringBuilder();
        
        // El primer segmento es siempre diálogo
        if (parts.length > 0 && !parts[0].trim().isEmpty()) {
            result.append("<").append(parts[0].trim()).append(">");
        }
        
        // Los segmentos restantes pueden ser acotaciones o más diálogos
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!part.isEmpty()) {
                if (i == parts.length - 1) {
                    // Última parte, es acotación
                    result.append(" ").append(part);
                } else {
                    // Parte intermedia, puede ser acotación seguida de más diálogo
                    // Buscar si hay patrón de diálogo al final
                    if (part.matches(".*\\s+.+")) {
                        String[] subParts = part.split("\\s+", 2);
                        if (subParts.length == 2) {
                            result.append(" ").append(subParts[0]);
                            result.append(" <").append(subParts[1]).append(">");
                        } else {
                            result.append(" ").append(part);
                        }
                    } else {
                        result.append(" ").append(part);
                    }
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * Convierte elementos HTML básicos a Markdown.
     */
    private static String convertBasicElements(String html) {
        String markdown = html;
        
        // Negrita+cursiva: <b><i>texto</i></b> -> ***texto***
        markdown = markdown.replaceAll("<b><i>(.+?)</i></b>", "***$1***");
        markdown = markdown.replaceAll("<i><b>(.+?)</b></i>", "***$1***");
        
        // Negrita: <b>texto</b> -> **texto**
        markdown = markdown.replaceAll("<b>(.+?)</b>", "**$1**");
        
        // Cursiva: <i>texto</i> -> *texto*
        markdown = markdown.replaceAll("<i>(.+?)</i>", "*$1*");
        
        // Imágenes: <img src='...' alt='...' /> -> ![alt](src)
        markdown = markdown.replaceAll("<img\\s+src=['\"](.+?)['\"]\\s+alt=['\"](.+?)['\"].*?/>", "![$2]($1)");
        markdown = markdown.replaceAll("<img\\s+alt=['\"](.+?)['\"]\\s+src=['\"](.+?)['\"].*?/>", "![$1]($2)");
        
        // Títulos
        markdown = markdown.replaceAll("<h1>(.+?)</h1>", "# $1");
        markdown = markdown.replaceAll("<h2>(.+?)</h2>", "## $1");
        markdown = markdown.replaceAll("<h3>(.+?)</h3>", "### $1");
        
        // Listas: primero convertir elementos <li>, luego remover <ul>
        markdown = markdown.replaceAll("<li>(.+?)</li>", "- $1");
        markdown = markdown.replaceAll("<ul>|</ul>", "");
        
        // Saltos de línea
        markdown = markdown.replaceAll("<br\\s*/?>", "\n");
        
        // Remover otros tags HTML residuales
        markdown = markdown.replaceAll("</?p[^>]*>", "");
        
        return markdown;
    }
    
    /**
     * Remueve todos los tags HTML de una cadena, manteniendo solo el texto.
     */
    private static String removeHtmlTags(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]+>", "").replaceAll("&mdash;", "—");
    }
}
