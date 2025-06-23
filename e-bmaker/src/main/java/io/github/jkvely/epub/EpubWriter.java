package io.github.jkvely.epub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.model.Classes.Image;
import io.github.jkvely.util.MarkdownToHtmlConverter;

/**
 * EpubWriter handles the complete EPUB export process.
 * Creates a valid EPUB file with proper structure and metadata.
 */
public class EpubWriter {
    
    private static final String MIMETYPE_CONTENT = "application/epub+zip";
    private static final String CONTAINER_XML = 
        "<?xml version=\"1.0\"?>\n" +
        "<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n" +
        "  <rootfiles>\n" +
        "    <rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\n" +
        "  </rootfiles>\n" +
        "</container>";
    
    /**
     * Exports an EpubBook to a valid EPUB file.
     */
    public static void exportEpub(EpubBook book, File outputFile) throws IOException {
        if (book == null) {
            throw new IllegalArgumentException("EpubBook cannot be null");
        }
        
        // Create temporary directory for EPUB structure
        Path tempDir = Files.createTempDirectory("epub_export");
        
        try {
            // Create EPUB directory structure
            createEpubStructure(tempDir, book);
            
            // Create the EPUB ZIP file
            createEpubArchive(tempDir, outputFile);
            
        } finally {
            // Clean up temporary directory
            deleteDirectory(tempDir);
        }
    }
    
    /**
     * Creates the complete EPUB directory structure and files.
     */
    private static void createEpubStructure(Path tempDir, EpubBook book) throws IOException {
        // Create required directories
        Path metaInfDir = tempDir.resolve("META-INF");
        Path oebpsDir = tempDir.resolve("OEBPS");
        Path textDir = oebpsDir.resolve("Text");
        Path imagesDir = oebpsDir.resolve("Images");
        Path stylesDir = oebpsDir.resolve("Styles");
        
        Files.createDirectories(metaInfDir);
        Files.createDirectories(textDir);
        Files.createDirectories(imagesDir);
        Files.createDirectories(stylesDir);
        
        // Create mimetype file (must be first and uncompressed)
        Files.write(tempDir.resolve("mimetype"), MIMETYPE_CONTENT.getBytes());
        
        // Create META-INF/container.xml
        Files.write(metaInfDir.resolve("container.xml"), CONTAINER_XML.getBytes());
        
        // Create default CSS
        createDefaultCSS(stylesDir);
        
        // Create cover XHTML if cover exists
        if (book.getCover() != null) {
            createCoverXHTML(textDir, book);
            
            // Save cover image
            if (book.getCover().getImage() != null) {
                saveCoverImage(imagesDir, book.getCover().getImage());
            }
        }
        
        // Create chapter XHTML files
        createChapterXHTMLFiles(textDir, book.getChapters());
        
        // Save chapter images
        saveChapterImages(imagesDir, book.getChapters());
        
        // Create content.opf manifest
        createContentOPF(oebpsDir, book);
        
        // Create toc.ncx navigation
        createTOCNCX(oebpsDir, book);
    }
    
    /**
     * Creates default CSS styling for the EPUB.
     */
    private static void createDefaultCSS(Path stylesDir) throws IOException {
        String css = 
            "/* Default EPUB Styles */\n" +
            "body {\n" +
            "    font-family: 'Georgia', 'Times New Roman', serif;\n" +
            "    line-height: 1.6;\n" +
            "    margin: 0;\n" +
            "    padding: 1em;\n" +
            "    text-align: justify;\n" +
            "}\n\n" +
            "h1, h2, h3, h4, h5, h6 {\n" +
            "    color: #333;\n" +
            "    margin-top: 1.5em;\n" +
            "    margin-bottom: 0.5em;\n" +
            "    text-align: center;\n" +
            "}\n\n" +
            "h1 { font-size: 2em; }\n" +
            "h2 { font-size: 1.6em; }\n" +
            "h3 { font-size: 1.3em; }\n\n" +
            "p {\n" +
            "    margin: 0 0 1em 0;\n" +
            "    text-indent: 1.2em;\n" +
            "}\n\n" +
            "/* Dialogue styles */\n" +
            ".dialogo {\n" +
            "    margin: 1em 0;\n" +
            "    text-indent: 0;\n" +
            "}\n\n" +
            ".acotacion {\n" +
            "    font-style: italic;\n" +
            "    color: #666;\n" +
            "}\n\n" +
            "/* Custom tags */\n" +
            ".titulo {\n" +
            "    text-align: center;\n" +
            "    font-weight: bold;\n" +
            "    font-size: 1.2em;\n" +
            "    margin: 1.5em 0;\n" +
            "}\n\n" +
            ".lugar {\n" +
            "    text-align: center;\n" +
            "    font-style: italic;\n" +
            "    margin: 1em 0;\n" +
            "}\n\n" +
            ".tiempo {\n" +
            "    text-align: center;\n" +
            "    font-style: italic;\n" +
            "    margin: 1em 0;\n" +
            "}\n\n" +
            "img {\n" +
            "    max-width: 100%;\n" +
            "    height: auto;\n" +
            "    display: block;\n" +
            "    margin: 1em auto;\n" +
            "}\n\n" +
            "/* Cover styles */\n" +
            ".cover {\n" +
            "    text-align: center;\n" +
            "    page-break-after: always;\n" +
            "}\n\n" +
            ".cover img {\n" +
            "    max-height: 100vh;\n" +
            "    max-width: 100vw;\n" +
            "}\n\n" +
            ".cover-title {\n" +
            "    font-size: 2.5em;\n" +
            "    font-weight: bold;\n" +
            "    margin: 1em 0;\n" +
            "}\n\n" +
            ".cover-author {\n" +
            "    font-size: 1.5em;\n" +
            "    margin: 0.5em 0;\n" +
            "}";
        
        Files.write(stylesDir.resolve("styles.css"), css.getBytes());
    }
    
    /**
     * Creates the cover XHTML file.
     */
    private static void createCoverXHTML(Path textDir, EpubBook book) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        html.append("<head>\n");
        html.append("  <title>Cover</title>\n");
        html.append("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../Styles/styles.css\"/>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("  <div class=\"cover\">\n");
        
        // Add cover image if exists
        if (book.getCover().getImage() != null) {
            String imageName = sanitizeFileName(book.getCover().getImage().getName());
            html.append("    <img src=\"../Images/").append(imageName).append("\" alt=\"Cover\"/>\n");
        }
        
        // Add title
        if (book.getCover().getTitle() != null && !book.getCover().getTitle().trim().isEmpty()) {
            html.append("    <h1 class=\"cover-title\">").append(escapeXml(book.getCover().getTitle())).append("</h1>\n");
        }
        
        // Add authors
        if (book.getCover().getAuthors() != null && !book.getCover().getAuthors().isEmpty()) {
            for (String author : book.getCover().getAuthors()) {
                html.append("    <p class=\"cover-author\">").append(escapeXml(author)).append("</p>\n");
            }
        }
        
        html.append("  </div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        Files.write(textDir.resolve("cover.xhtml"), html.toString().getBytes());
    }
    
    /**
     * Creates XHTML files for all chapters.
     */
    private static void createChapterXHTMLFiles(Path textDir, List<EpubChapter> chapters) throws IOException {
        for (int i = 0; i < chapters.size(); i++) {
            EpubChapter chapter = chapters.get(i);
            String fileName = String.format("chapter_%03d.xhtml", i + 1);
            
            StringBuilder html = new StringBuilder();
            html.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
            html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            html.append("<head>\n");
            html.append("  <title>").append(escapeXml(chapter.getTitle() != null ? chapter.getTitle() : "Chapter " + (i + 1))).append("</title>\n");
            html.append("  <link rel=\"stylesheet\" type=\"text/css\" href=\"../Styles/styles.css\"/>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            
            // Add chapter title
            if (chapter.getTitle() != null && !chapter.getTitle().trim().isEmpty()) {
                html.append("  <h1>").append(escapeXml(chapter.getTitle())).append("</h1>\n");
            }
            
            // Convert markdown content to HTML
            if (chapter.getContent() != null && !chapter.getContent().trim().isEmpty()) {
                String htmlContent = MarkdownToHtmlConverter.convert(chapter.getContent());
                html.append("  <div class=\"chapter-content\">\n");
                html.append(htmlContent);
                html.append("\n  </div>\n");
            }
            
            html.append("</body>\n");
            html.append("</html>");
            
            Files.write(textDir.resolve(fileName), html.toString().getBytes());
        }
    }
    
    /**
     * Saves the cover image to the Images directory.
     */
    private static void saveCoverImage(Path imagesDir, Image coverImage) throws IOException {
        if (coverImage != null && coverImage.getData() != null) {
            String fileName = sanitizeFileName(coverImage.getName());
            Files.write(imagesDir.resolve(fileName), coverImage.getData());
        }
    }
    
    /**
     * Saves all chapter images to the Images directory.
     */
    private static void saveChapterImages(Path imagesDir, List<EpubChapter> chapters) throws IOException {
        for (EpubChapter chapter : chapters) {
            if (chapter.getImages() != null) {
                for (Image image : chapter.getImages()) {
                    if (image.getData() != null) {
                        String fileName = sanitizeFileName(image.getName());
                        Files.write(imagesDir.resolve(fileName), image.getData());
                    }
                }
            }
        }
    }
    
    /**
     * Creates the content.opf manifest file.
     */
    private static void createContentOPF(Path oebpsDir, EpubBook book) throws IOException {
        StringBuilder opf = new StringBuilder();
        
        // Generate unique identifier if not present
        String identifier = book.getIdentifier();
        if (identifier == null || identifier.trim().isEmpty()) {
            identifier = "urn:uuid:" + UUID.randomUUID().toString();
        }
        
        // Header
        opf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        opf.append("<package version=\"2.0\" xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"BookId\">\n");
        
        // Metadata
        opf.append("  <metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:opf=\"http://www.idpf.org/2007/opf\">\n");
        opf.append("    <dc:identifier id=\"BookId\">").append(escapeXml(identifier)).append("</dc:identifier>\n");
        
        // Title from cover or default
        String title = "Untitled Book";
        if (book.getCover() != null && book.getCover().getTitle() != null) {
            title = book.getCover().getTitle();
        }
        opf.append("    <dc:title>").append(escapeXml(title)).append("</dc:title>\n");
        
        // Authors from cover
        if (book.getCover() != null && book.getCover().getAuthors() != null) {
            for (String author : book.getCover().getAuthors()) {
                opf.append("    <dc:creator>").append(escapeXml(author)).append("</dc:creator>\n");
            }
        } else {
            opf.append("    <dc:creator>Unknown Author</dc:creator>\n");
        }
        
        // Language
        String language = book.getLanguage() != null ? book.getLanguage() : "es";
        opf.append("    <dc:language>").append(escapeXml(language)).append("</dc:language>\n");
        
        // Description
        if (book.getDescription() != null && !book.getDescription().trim().isEmpty()) {
            opf.append("    <dc:description>").append(escapeXml(book.getDescription())).append("</dc:description>\n");
        }
        
        // Subjects
        if (book.getSubjects() != null && !book.getSubjects().isEmpty()) {
            for (String subject : book.getSubjects()) {
                opf.append("    <dc:subject>").append(escapeXml(subject)).append("</dc:subject>\n");
            }
        }
        
        // Rights
        if (book.getRights() != null && !book.getRights().trim().isEmpty()) {
            opf.append("    <dc:rights>").append(escapeXml(book.getRights())).append("</dc:rights>\n");
        }
        
        // Series information
        if (book.getSeries() != null && !book.getSeries().trim().isEmpty()) {
            opf.append("    <meta name=\"calibre:series\" content=\"").append(escapeXml(book.getSeries())).append("\"/>\n");
            if (book.getSeriesIndex() > 0) {
                opf.append("    <meta name=\"calibre:series_index\" content=\"").append(book.getSeriesIndex()).append("\"/>\n");
            }
        }
        
        // Additional metadata
        if (book.getMetadata() != null) {
            for (String key : book.getMetadata().keySet()) {
                String value = book.getMetadata().get(key);
                opf.append("    <meta name=\"").append(escapeXml(key)).append("\" content=\"").append(escapeXml(value)).append("\"/>\n");
            }
        }
        
        // Date
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        opf.append("    <dc:date>").append(currentDate).append("</dc:date>\n");
        
        opf.append("  </metadata>\n");
        
        // Manifest
        opf.append("  <manifest>\n");
        opf.append("    <item id=\"ncx\" href=\"toc.ncx\" media-type=\"application/x-dtbncx+xml\"/>\n");
        opf.append("    <item id=\"styles\" href=\"Styles/styles.css\" media-type=\"text/css\"/>\n");
        
        // Cover XHTML
        if (book.getCover() != null) {
            opf.append("    <item id=\"cover\" href=\"Text/cover.xhtml\" media-type=\"application/xhtml+xml\"/>\n");
            
            // Cover image
            if (book.getCover().getImage() != null) {
                String imageName = sanitizeFileName(book.getCover().getImage().getName());
                String mimeType = book.getCover().getImage().getMimeType();
                if (mimeType == null || mimeType.trim().isEmpty()) {
                    mimeType = guessMimeType(imageName);
                }
                opf.append("    <item id=\"cover-image\" href=\"Images/").append(imageName)
                   .append("\" media-type=\"").append(mimeType).append("\"/>\n");
            }
        }
        
        // Chapter XHTML files
        for (int i = 0; i < book.getChapters().size(); i++) {
            String fileName = String.format("chapter_%03d.xhtml", i + 1);
            opf.append("    <item id=\"chapter").append(i + 1).append("\" href=\"Text/").append(fileName)
               .append("\" media-type=\"application/xhtml+xml\"/>\n");
        }
        
        // Chapter images
        List<String> processedImages = new ArrayList<>();
        for (EpubChapter chapter : book.getChapters()) {
            if (chapter.getImages() != null) {
                for (Image image : chapter.getImages()) {
                    String imageName = sanitizeFileName(image.getName());
                    if (!processedImages.contains(imageName)) {
                        processedImages.add(imageName);
                        String mimeType = image.getMimeType();
                        if (mimeType == null || mimeType.trim().isEmpty()) {
                            mimeType = guessMimeType(imageName);
                        }
                        opf.append("    <item id=\"img-").append(processedImages.size())
                           .append("\" href=\"Images/").append(imageName)
                           .append("\" media-type=\"").append(mimeType).append("\"/>\n");
                    }
                }
            }
        }
        
        opf.append("  </manifest>\n");
        
        // Spine
        opf.append("  <spine toc=\"ncx\">\n");
        if (book.getCover() != null) {
            opf.append("    <itemref idref=\"cover\"/>\n");
        }
        for (int i = 0; i < book.getChapters().size(); i++) {
            opf.append("    <itemref idref=\"chapter").append(i + 1).append("\"/>\n");
        }
        opf.append("  </spine>\n");
        
        // Guide
        if (book.getCover() != null) {
            opf.append("  <guide>\n");
            opf.append("    <reference type=\"cover\" title=\"Cover\" href=\"Text/cover.xhtml\"/>\n");
            opf.append("  </guide>\n");
        }
        
        opf.append("</package>");
        
        Files.write(oebpsDir.resolve("content.opf"), opf.toString().getBytes());
    }
    
    /**
     * Creates the toc.ncx navigation file.
     */
    private static void createTOCNCX(Path oebpsDir, EpubBook book) throws IOException {
        StringBuilder ncx = new StringBuilder();
        
        // Generate unique identifier
        String identifier = book.getIdentifier();
        if (identifier == null || identifier.trim().isEmpty()) {
            identifier = "urn:uuid:" + UUID.randomUUID().toString();
        }
        
        // Header
        ncx.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        ncx.append("<!DOCTYPE ncx PUBLIC \"-//NISO//DTD ncx 2005-1//EN\" \"http://www.daisy.org/z3986/2005/ncx-2005-1.dtd\">\n");
        ncx.append("<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\">\n");
        
        // Head
        ncx.append("  <head>\n");
        ncx.append("    <meta name=\"dtb:uid\" content=\"").append(escapeXml(identifier)).append("\"/>\n");
        ncx.append("    <meta name=\"dtb:depth\" content=\"1\"/>\n");
        ncx.append("    <meta name=\"dtb:totalPageCount\" content=\"0\"/>\n");
        ncx.append("    <meta name=\"dtb:maxPageNumber\" content=\"0\"/>\n");
        ncx.append("  </head>\n");
        
        // Doc title
        String title = "Untitled Book";
        if (book.getCover() != null && book.getCover().getTitle() != null) {
            title = book.getCover().getTitle();
        }
        ncx.append("  <docTitle>\n");
        ncx.append("    <text>").append(escapeXml(title)).append("</text>\n");
        ncx.append("  </docTitle>\n");
        
        // Nav map
        ncx.append("  <navMap>\n");
        
        int playOrder = 1;
        
        // Cover
        if (book.getCover() != null) {
            ncx.append("    <navPoint id=\"navpoint-cover\" playOrder=\"").append(playOrder++).append("\">\n");
            ncx.append("      <navLabel>\n");
            ncx.append("        <text>Cover</text>\n");
            ncx.append("      </navLabel>\n");
            ncx.append("      <content src=\"Text/cover.xhtml\"/>\n");
            ncx.append("    </navPoint>\n");
        }
        
        // Chapters
        for (int i = 0; i < book.getChapters().size(); i++) {
            EpubChapter chapter = book.getChapters().get(i);
            String chapterTitle = chapter.getTitle() != null ? chapter.getTitle() : "Chapter " + (i + 1);
            String fileName = String.format("chapter_%03d.xhtml", i + 1);
            
            ncx.append("    <navPoint id=\"navpoint-").append(i + 1).append("\" playOrder=\"").append(playOrder++).append("\">\n");
            ncx.append("      <navLabel>\n");
            ncx.append("        <text>").append(escapeXml(chapterTitle)).append("</text>\n");
            ncx.append("      </navLabel>\n");
            ncx.append("      <content src=\"Text/").append(fileName).append("\"/>\n");
            ncx.append("    </navPoint>\n");
        }
        
        ncx.append("  </navMap>\n");
        ncx.append("</ncx>");
        
        Files.write(oebpsDir.resolve("toc.ncx"), ncx.toString().getBytes());
    }
    
    /**
     * Creates the final EPUB ZIP archive.
     */
    private static void createEpubArchive(Path tempDir, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            // First add mimetype (must be first and uncompressed)
            ZipEntry mimetypeEntry = new ZipEntry("mimetype");
            mimetypeEntry.setMethod(ZipEntry.STORED);
            byte[] mimetypeBytes = MIMETYPE_CONTENT.getBytes();
            mimetypeEntry.setSize(mimetypeBytes.length);
            mimetypeEntry.setCompressedSize(mimetypeBytes.length);
            mimetypeEntry.setCrc(calculateCRC32(mimetypeBytes));
            zos.putNextEntry(mimetypeEntry);
            zos.write(mimetypeBytes);
            zos.closeEntry();
            
            // Add all other files
            Files.walk(tempDir)
                .filter(path -> !path.equals(tempDir) && !path.getFileName().toString().equals("mimetype"))
                .forEach(path -> {
                    try {
                        String entryName = tempDir.relativize(path).toString().replace('\\', '/');
                        if (Files.isDirectory(path)) {
                            return; // Skip directories
                        }
                        
                        ZipEntry entry = new ZipEntry(entryName);
                        zos.putNextEntry(entry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }
    
    /**
     * Utility methods
     */
    
    private static String sanitizeFileName(String fileName) {
        if (fileName == null) return "unknown";
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    private static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
    
    private static String guessMimeType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
    
    private static long calculateCRC32(byte[] data) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(data);
        return crc.getValue();
    }
    
    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted((p1, p2) -> p2.compareTo(p1)) // Reverse order to delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore deletion errors
                    }
                });
        }
    }
}
