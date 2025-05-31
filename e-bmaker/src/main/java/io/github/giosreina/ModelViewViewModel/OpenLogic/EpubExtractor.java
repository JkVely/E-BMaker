package io.github.giosreina.ModelViewViewModel.OpenLogic;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.util.ArrayList;
import java.util.List;

public class EpubExtractor {
    /**
     * Extrae todo el contenido del archivo EPUB
     */
    public void extractEpub(String epubPath, String outputDir) throws IOException {
        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(outputPath);
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(epubPath))) {
            ZipEntry entry = zipIn.getNextEntry();
            
            while (entry != null) {
                String filePath = outputDir + File.separator + entry.getName();
                
                if (!entry.isDirectory()) {
                    // Crear directorios padre si no existen
                    Path parentDir = Paths.get(filePath).getParent();
                    if (parentDir != null) {
                        Files.createDirectories(parentDir);
                    }
                    
                    // Extraer archivo
                    extractFile(zipIn, filePath);
                    System.out.println("Extraído: " + entry.getName());
                } else {
                    // Crear directorio
                    Files.createDirectories(Paths.get(filePath));
                }
                
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        
        System.out.println("EPUB extraído completamente en: " + outputDir);
    }
    
    /**
     * Extrae solo los archivos XHTML del EPUB
     */
    public List<String> extractXhtmlFiles(String epubPath, String outputDir) throws IOException {
        List<String> xhtmlFiles = new ArrayList<>();
        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(outputPath);
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(epubPath))) {
            ZipEntry entry = zipIn.getNextEntry();
            
            while (entry != null) {
                String entryName = entry.getName().toLowerCase();
                
                // Filtrar solo archivos XHTML/HTML
                if (!entry.isDirectory() && 
                    (entryName.endsWith(".xhtml") || entryName.endsWith(".html"))) {
                    
                    String filePath = outputDir + File.separator + entry.getName();
                    
                    // Crear directorios padre si no existen
                    Path parentDir = Paths.get(filePath).getParent();
                    if (parentDir != null) {
                        Files.createDirectories(parentDir);
                    }
                    
                    // Extraer archivo XHTML
                    extractFile(zipIn, filePath);
                    xhtmlFiles.add(filePath);
                    System.out.println("Archivo XHTML extraído: " + entry.getName());
                }
                
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        
        return xhtmlFiles;
    }
    
    /**
     * Extrae un archivo individual del ZIP
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
    
    /**
     * Lista todos los archivos dentro del EPUB sin extraerlos
     */
    public void listEpubContents(String epubPath) throws IOException {
        System.out.println("Contenido del archivo EPUB:");
        System.out.println("============================");
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(epubPath))) {
            ZipEntry entry = zipIn.getNextEntry();
            
            while (entry != null) {
                String type = entry.isDirectory() ? "[DIR]" : "[FILE]";
                System.out.println(type + " " + entry.getName());
                
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }
    
    /**
     * Extrae archivos XHTML y devuelve su contenido como String
     */
    public List<XhtmlContent> getXhtmlContent(String epubPath) throws IOException {
        List<XhtmlContent> contents = new ArrayList<>();
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(epubPath))) {
            ZipEntry entry = zipIn.getNextEntry();
            
            while (entry != null) {
                String entryName = entry.getName().toLowerCase();
                
                if (!entry.isDirectory() && 
                    (entryName.endsWith(".xhtml") || entryName.endsWith(".html"))) {
                    
                    // Leer contenido del archivo
                    StringBuilder content = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipIn));
                    String line;
                    
                    // Crear una copia del stream para leer
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipIn.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                    
                    String fileContent = baos.toString("UTF-8");
                    contents.add(new XhtmlContent(entry.getName(), fileContent));
                }
                
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        
        return contents;
    }
    
    /**
     * Clase para almacenar contenido XHTML
     */
    public static class XhtmlContent {
        private String fileName;
        private String content;
        
        public XhtmlContent(String fileName, String content) {
            this.fileName = fileName;
            this.content = content;
        }
        
        public String getFileName() { return fileName; }
        public String getContent() { return content; }
        
        @Override
        public String toString() {
            return "Archivo: " + fileName + " (Tamaño: " + content.length() + " caracteres)";
        }
    }
}
