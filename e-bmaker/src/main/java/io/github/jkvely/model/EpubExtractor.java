package io.github.jkvely.model;

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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import io.github.jkvely.util.BytesToImageConverter;
import io.github.jkvely.util.HtmlToMarkdownConverter;
import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.Classes.EpubChapter;
import io.github.jkvely.model.Classes.EpubCover;
import io.github.jkvely.model.Classes.Image;

public class EpubExtractor {
    public static void FindHtmlAndXhtml(String fileDirectory) {
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
            file.close();
            // Procesar la información del libro
            // Extraer los metadatos del libro
            // Asumimos que la información del libro está en un archivo específico llamado "Info"
            String lang = "";
            String identifier = "";
            String description = "";
            List<String> subjects = new ArrayList<>();
            String series = "";
            int index = 0;
            for (String linea:HtmlAndXhtmlContent.get("Info")){
                if (linea.contains("lang=")) {
                    lang = linea.split("lang=\"")[1].split("\"")[0];
                }
                if (linea.contains("identifier=")) {
                    identifier = linea.split("identifier=\"")[1].split("\"")[0];
                }
                if(linea.contains("description=")) {
                    description = linea.split("description=\"")[1].split("\"")[0];
                }
                if(linea.contains("subjects=")) {
                    String genres = linea.split("subjects=\"")[1].split("\"")[0];
                    subjects = List.of(genres.split(","));
                }
                if(linea.contains("series=")) {
                    series = linea.split("series=\"")[1].split("\"")[0];
                }
                if(linea.contains("index=")) {
                    index = Integer.parseInt(linea.split("index=\"")[1].split("\"")[0]);
                }
            }
            // Inicializar las listas y mapas necesarios
            List<EpubChapter> chapters = new ArrayList<>();
            Map<String, String> metadata = new HashMap<>();
            // Extraer las imágenes y crear la portada
            Map<String, byte[]> imageMap = extractImages(fileDirectory);
            //Crear la portada del libro
            Image coverImage = new Image("cover", "image/png", imageMap.get("cover.png"), "cover");
            EpubCover cover = new EpubCover(fileDirectory, HtmlAndXhtmlContent.get("Info"), coverImage);
            // Crear el libro EPUB
            EpubBook epubBook = new EpubBook(cover, lang, identifier, description, subjects, "Hecho con E-Bmaker", series, index,chapters,metadata);
            // Añadir los capítulos al libro
            for(Map.Entry<String, List<String>> entry : HtmlAndXhtmlContent.entrySet()) {
                List<String> content = entry.getValue();
                List<String> markdown = HtmlToMarkdownConverter.convert(content);
                EpubChapter chapter = EpubChapter.builder()
                        .title(entry.getKey())
                        .content(String.join("\n", markdown))
                        .build();
                epubBook.addChapter(chapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
