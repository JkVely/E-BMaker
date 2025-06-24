package io.github.jkvely.model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.model.Classes.EpubCover;
import io.github.jkvely.model.Classes.Image;
import io.github.jkvely.util.BytesToImageConverter;
import io.github.jkvely.util.HtmlToMarkdownConverter;

public class EpubExtractor {    public static EpubBook FindHtmlAndXhtml(String fileDirectory) {
        Map<String, List<String>> HtmlAndXhtmlContent = new HashMap<>();
        
        try {
            ZipFile file = new ZipFile(fileDirectory);
            java.util.Enumeration<? extends ZipEntry> entries = file.entries();
            
            // First pass: collect all HTML/XHTML content
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".html") || name.endsWith(".xhtml")) {
                    List<String> content = ReadFileFromZip(file, entry);
                    HtmlAndXhtmlContent.put(name, content);
                }
            }
            
            // Try to extract metadata from OPF file
            Map<String, String> metadata = extractMetadata(file);
            
            file.close();
            
            // Extract metadata with default values
            String lang = metadata.getOrDefault("language", "es");
            String identifier = metadata.getOrDefault("identifier", "unknown");
            String description = metadata.getOrDefault("description", "");
            String title = metadata.getOrDefault("title", "Libro sin título");
            String creator = metadata.getOrDefault("creator", "Autor desconocido");
            List<String> subjects = new ArrayList<>();
            if (metadata.containsKey("subject")) {
                subjects = List.of(metadata.get("subject").split(","));
            }
            String series = metadata.getOrDefault("series", "");
            int index = 0;
            try {
                index = Integer.parseInt(metadata.getOrDefault("index", "0"));
            } catch (NumberFormatException e) {
                index = 0;
            }
            
            // Extract images and create cover
            Map<String, byte[]> imageMap = extractImages(fileDirectory);
            Image coverImage = null;
            
            // Try to find a cover image
            for (String imageName : imageMap.keySet()) {
                if (imageName.toLowerCase().contains("cover") || 
                    imageName.toLowerCase().contains("portada")) {
                    coverImage = new Image("cover", "image/png", imageMap.get(imageName), "cover");
                    break;
                }
            }
            
            // If no cover found, create a default one
            if (coverImage == null && !imageMap.isEmpty()) {
                String firstImage = imageMap.keySet().iterator().next();
                coverImage = new Image("cover", "image/png", imageMap.get(firstImage), "cover");
            }
              // Create cover with available information
            List<String> coverInfo = new ArrayList<>();
            if (!title.isEmpty()) coverInfo.add("title=" + title);
            if (!creator.isEmpty()) coverInfo.add("creator=" + creator);
            if (!lang.isEmpty()) coverInfo.add("language=" + lang);
            if (!description.isEmpty()) coverInfo.add("description=" + description);
            
            EpubCover cover = new EpubCover(title, coverInfo, coverImage);
            
            // Initialize chapters list
            List<EpubChapter> chapters = new ArrayList<>();
              // Create the EPUB book
            EpubBook epubBook = new EpubBook(cover, lang, identifier, description, subjects, 
                                           "Hecho con E-Bmaker", series, index, "./", chapters, metadata);
            
            // Process HTML/XHTML files and convert to chapters
            processHtmlContent(HtmlAndXhtmlContent, epubBook);
            
            return epubBook;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, byte[]> extractImages(String epubFile) throws IOException {
        Map<String, byte[]> imageMap = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(epubFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // Filtrar solo archivos de imagen
                if (entry.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|svg)$")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    imageMap.put(new File(entry.getName()).getName(), baos.toByteArray());
                }
            }
            File carpetaDeImagenes = new File("Images");
            if (!carpetaDeImagenes.exists()) {
                carpetaDeImagenes.mkdir();
            }
            Map<String, BufferedImage> images = BytesToImageConverter.bytesToImage(imageMap);
            for (Map.Entry<String, BufferedImage> imageEntry : images.entrySet()) {
                File archivoSalida = new File(carpetaDeImagenes, imageEntry.getKey());
                try {
                    ImageIO.write(imageEntry.getValue(), "png", archivoSalida);
                } catch (IOException e) {
                    System.err.println("Error al guardar la imagen: " + imageEntry.getKey());
                    e.printStackTrace();
                }
            }
        }
        return imageMap;
    }
    public static List<String> ReadFileFromZip(ZipFile file, ZipEntry entry) {
        List<String> content = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(file.getInputStream(entry)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
    /**
     * Extrae metadatos del archivo EPUB parseando el archivo OPF
     */
    private static Map<String, String> extractMetadata(ZipFile file) {
        Map<String, String> metadata = new HashMap<>();
        
        try {
            // Try to find and parse META-INF/container.xml to get OPF file location
            ZipEntry containerEntry = file.getEntry("META-INF/container.xml");
            String opfFilePath = null;
            
            if (containerEntry != null) {
                List<String> containerContent = ReadFileFromZip(file, containerEntry);
                for (String line : containerContent) {
                    if (line.contains("full-path=")) {
                        // Extract OPF file path from container.xml
                        int start = line.indexOf("full-path=\"") + 11;
                        int end = line.indexOf("\"", start);
                        if (start > 10 && end > start) {
                            opfFilePath = line.substring(start, end);
                            break;
                        }
                    }
                }
            }
            
            // If no container.xml found, try common OPF file locations
            if (opfFilePath == null) {
                String[] commonPaths = {"content.opf", "package.opf", "book.opf", "OEBPS/content.opf"};
                for (String path : commonPaths) {
                    if (file.getEntry(path) != null) {
                        opfFilePath = path;
                        break;
                    }
                }
            }
            
            // Parse OPF file if found
            if (opfFilePath != null) {
                ZipEntry opfEntry = file.getEntry(opfFilePath);
                if (opfEntry != null) {
                    List<String> opfContent = ReadFileFromZip(file, opfEntry);
                    parseOPFMetadata(opfContent, metadata);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error extracting metadata: " + e.getMessage());
        }
        
        return metadata;
    }
    
    /**
     * Parsea los metadatos del contenido OPF
     */
    private static void parseOPFMetadata(List<String> opfContent, Map<String, String> metadata) {
        boolean inMetadata = false;
        
        for (String line : opfContent) {
            String trimmedLine = line.trim();
            
            if (trimmedLine.contains("<metadata")) {
                inMetadata = true;
                continue;
            }
            if (trimmedLine.contains("</metadata>")) {
                inMetadata = false;
                continue;
            }
            
            if (inMetadata) {
                // Parse different metadata elements
                if (trimmedLine.contains("<dc:title")) {
                    extractElementContent(trimmedLine, "dc:title", metadata, "title");
                } else if (trimmedLine.contains("<dc:creator")) {
                    extractElementContent(trimmedLine, "dc:creator", metadata, "creator");
                } else if (trimmedLine.contains("<dc:language")) {
                    extractElementContent(trimmedLine, "dc:language", metadata, "language");
                } else if (trimmedLine.contains("<dc:identifier")) {
                    extractElementContent(trimmedLine, "dc:identifier", metadata, "identifier");
                } else if (trimmedLine.contains("<dc:description")) {
                    extractElementContent(trimmedLine, "dc:description", metadata, "description");
                } else if (trimmedLine.contains("<dc:subject")) {
                    String subject = extractSimpleElementContent(trimmedLine, "dc:subject");
                    if (subject != null) {
                        String existingSubjects = metadata.getOrDefault("subject", "");
                        metadata.put("subject", existingSubjects.isEmpty() ? subject : existingSubjects + "," + subject);
                    }
                }
            }
        }
    }
    
    /**
     * Extrae el contenido de un elemento XML
     */
    private static void extractElementContent(String line, String element, Map<String, String> metadata, String key) {
        String content = extractSimpleElementContent(line, element);
        if (content != null) {
            metadata.put(key, content);
        }
    }
    
    /**
     * Extrae el contenido simple de un elemento XML
     */
    private static String extractSimpleElementContent(String line, String element) {
        try {
            int startTag = line.indexOf("<" + element);
            if (startTag == -1) return null;
            
            int contentStart = line.indexOf(">", startTag) + 1;
            int contentEnd = line.indexOf("</" + element.split(" ")[0], contentStart);
            
            if (contentStart > 0 && contentEnd > contentStart) {
                return line.substring(contentStart, contentEnd).trim();
            }
        } catch (Exception e) {
            // Ignore parsing errors for individual elements
        }
        return null;
    }
    /**
     * Procesa el contenido HTML/XHTML y crea capítulos apropiados.
     * Filtra archivos no deseados y extrae títulos apropiados.
     */
    private static void processHtmlContent(Map<String, List<String>> htmlContent, EpubBook epubBook) {
        int chapterNumber = 1;
        
        for (Map.Entry<String, List<String>> entry : htmlContent.entrySet()) {
            String filename = entry.getKey();
            List<String> htmlLines = entry.getValue();
              // Skip files that are not actual book content
            if (isStructuralFile(filename)) {
                System.out.println("📄 Saltando archivo estructural: " + filename);
                continue;
            }
            
            // Join HTML lines into a single string
            String htmlContent_str = String.join("\n", htmlLines);
            
            // Extract chapter title using the new method
            String chapterTitle = HtmlToMarkdownConverter.extractChapterTitle(htmlContent_str);
            if (chapterTitle.equals("Capítulo sin título")) {
                chapterTitle = "Capítulo " + chapterNumber;
            }
            
            // Convert HTML to Markdown using the improved converter
            String markdown = HtmlToMarkdownConverter.convert(htmlContent_str);
              // Create chapter only if we have meaningful content
            if (markdown != null && !markdown.trim().isEmpty() && hasActualContent(markdown)) {
                EpubChapter chapter = EpubChapter.builder()
                        .id(chapterNumber)
                        .title(chapterTitle)
                        .content(markdown)
                        .build();
                epubBook.addChapter(chapter);
                System.out.println("📖 Capítulo agregado: " + chapterTitle);
                chapterNumber++;
            } else {
                System.out.println("⚠️ Archivo sin contenido útil: " + filename);
            }
        }
        
        // If no chapters were created, add a default empty chapter
        if (epubBook.getChapters().isEmpty()) {
            EpubChapter defaultChapter = EpubChapter.builder()
                    .id(1)
                    .title("Capítulo 1")
                    .content("Contenido del libro cargado.")
                    .build();
            epubBook.addChapter(defaultChapter);
        }
    }
      /**
     * Checks if a file is structural (cover, title page, etc.) and should be skipped.
     */
    private static boolean isStructuralFile(String filename) {
        String lowerFilename = filename.toLowerCase();
        
        // Common structural file patterns
        return lowerFilename.contains("cover") ||
               lowerFilename.contains("title") ||
               lowerFilename.contains("copyright") ||
               lowerFilename.contains("toc") ||
               lowerFilename.contains("contents") ||
               lowerFilename.contains("index") ||
               lowerFilename.contains("nav") ||
               lowerFilename.contains("portada") ||
               lowerFilename.contains("indice") ||
               lowerFilename.contains("contenido") ||
               lowerFilename.equals("titlepage.xhtml") ||
               lowerFilename.equals("toc.xhtml") ||
               (lowerFilename.matches(".*\\d{1,2}\\.x?html?") && lowerFilename.contains("front"));
    }
    
    /**
     * Checks if the markdown content has actual readable content.
     */
    private static boolean hasActualContent(String markdown) {
        if (markdown == null) return false;
        
        String cleanText = markdown.trim()
                .replaceAll("#+ ", "") // Remove headings
                .replaceAll("\\*+", "") // Remove formatting
                .replaceAll("- ", "") // Remove list markers
                .replaceAll("\\s+", " ") // Normalize whitespace
                .trim();
          // Consider it actual content if it has more than 10 characters
        return cleanText.length() > 10;
    }
}
