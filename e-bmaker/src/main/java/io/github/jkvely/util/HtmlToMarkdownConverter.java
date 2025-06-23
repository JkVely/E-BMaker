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
}
