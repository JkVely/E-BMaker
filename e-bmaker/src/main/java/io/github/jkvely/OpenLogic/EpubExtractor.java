package io.github.jkvely.OpenLogic;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class EpubExtractor {
    public static Map<String, List<String>> FindHtmlAndXhtml(String fileDirectory) {
        Map<String, List<String>> HtmlAndXhtmlContent = new HashMap<>();

        try {
            ZipFile file = new ZipFile(fileDirectory);
            java.util.Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".html") || name.endsWith(".xhtml")) {
                    List<String> content = ReadFileFromZip(file, entry);
                    HtmlAndXhtmlContent.put(name, content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HtmlAndXhtmlContent;
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
}
