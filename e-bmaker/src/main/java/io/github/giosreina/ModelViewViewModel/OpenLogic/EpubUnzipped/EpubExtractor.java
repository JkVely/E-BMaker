package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;

public class EpubExtractor {
    public static Map<String, List<String>> FindHtmlAndXhtml(ZipFile file) {
        Map<String, List<String>> HtmlAndXhtmlContent = new HashMap<>();

        try {
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
