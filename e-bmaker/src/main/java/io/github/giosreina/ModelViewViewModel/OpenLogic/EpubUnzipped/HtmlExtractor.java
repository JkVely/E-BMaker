package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.*;

public class HtmlExtractor {
    public static List<String> extractHtmlContent(File file) {
        List<String> htmlContent = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlContent;
    }
    public static List<String> extractHtml(String filePath) {
        return null;
    }

    public List<String> extractHtmlFiles(String epubPath, String outputDir) throws IOException {
        List<String> extractedFiles = new ArrayList<>();
        
        // Crear directorio de salida si no existe
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(epubPath))) {
            ZipEntry entry = zipIn.getNextEntry();
            
            while (entry != null) {
                String entryName = entry.getName().toLowerCase();
                
                // Filtrar solo archivos XHTML y HTML
                if (!entry.isDirectory() && 
                    (entryName.endsWith(".xhtml") || entryName.endsWith(".html"))) {
                    
                    String filePath = outputDir + File.separator + entry.getName();
                    
                    // Crear directorios padre si no existen
                    File parentDir = new File(filePath).getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    
                    // Extraer archivo
                    extractFile(zipIn, filePath);
                    extractedFiles.add(filePath);
                    
                    System.out.println("Extraído: " + entry.getName());
                }
                
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        
        System.out.println("Total de archivos HTML/XHTML extraídos: " + extractedFiles.size());
        return extractedFiles;
    }
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
    public List<String> extractHtmlFilesFromEpub(String epubPath, String outputDir) {
        try {
            List<String> htmlContent = new ArrayList<>();
            for (String file : extractHtmlFiles(epubPath, outputDir)) {
                List<String> content = extractHtmlContent(new File(file));
                if (content != null && !content.isEmpty()) {
                    htmlContent.addAll(content);
                }
            }
            return htmlContent;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
