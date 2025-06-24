package io.github.jkvely.epub;

import java.io.File;
import java.io.IOException;

import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.EpubExtractor;
import io.github.jkvely.util.NativeFileManager;




/**
 * EpubService provides methods to load and save EPUB files.
 *
 * This is a placeholder for EPUB logic. You can use epublib or similar libraries here.
 */
public class EpubService {
    private static String rutaArchivo = "";    /**
     * Loads an EPUB file from disk using native file dialog.
     * @return an EpubBook model, or null if loading fails
     */
    public static EpubBook loadEpub() {
        // Use native file manager to select EPUB file
        String selectedFilePath = NativeFileManager.selectEpubFile();
        
        if (selectedFilePath == null || selectedFilePath.trim().isEmpty()) {
            System.out.println("No se seleccionó ningún archivo.");
            return null;
        }
        
        // Store the path for later reference
        rutaArchivo = selectedFilePath;
        
        try {
            EpubBook libro = EpubExtractor.FindHtmlAndXhtml(rutaArchivo);
            System.out.println("📖 Archivo EPUB cargado desde: " + rutaArchivo);
            return libro;    
        } catch (Exception e) {
            System.err.println("Error al extraer contenido del archivo: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static String getRutaArchivo() {
        return rutaArchivo;
    }    /**
     * Saves an EpubBook model to disk as an EPUB file.
     * @param book the EpubBook model
     * @param file the destination file
     */
    public static void saveEpub(EpubBook book, File file) throws IOException {
        EpubWriter.exportEpub(book, file);
    }    /**
     * Saves an EpubBook model with native file selection dialog.
     * @param book the EpubBook model to save
     * @return true if the book was saved successfully, false otherwise
     */
    public static boolean saveEpubWithDialog(EpubBook book) {
        if (book == null) {
            System.out.println("No hay libro para guardar.");
            return false;
        }

        // Generate suggested filename based on book title
        String suggestedName = "libro.epub";
        if (book.getCover() != null && book.getCover().getTitle() != null) {
            String title = book.getCover().getTitle().replaceAll("[\\\\/:*?\"<>|]", "_");
            suggestedName = title + ".epub";
        }

        // Use native file manager to get save location
        String saveFilePath = NativeFileManager.saveEpubFile(suggestedName);
        
        if (saveFilePath == null || saveFilePath.trim().isEmpty()) {
            System.out.println("No se seleccionó ubicación para guardar.");
            return false;
        }

        // Ensure .epub extension
        if (!saveFilePath.toLowerCase().endsWith(".epub")) {
            saveFilePath += ".epub";
        }

        try {
            File outputFile = new File(saveFilePath);
            saveEpub(book, outputFile);
            System.out.println("📚 Libro guardado exitosamente en: " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el libro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
