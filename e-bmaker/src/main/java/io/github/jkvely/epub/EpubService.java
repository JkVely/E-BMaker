package io.github.jkvely.epub;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import io.github.jkvely.model.Classes.EpubBook;
import io.github.jkvely.model.EpubExtractor;




/**
 * EpubService provides methods to load and save EPUB files.
 *
 * This is a placeholder for EPUB logic. You can use epublib or similar libraries here.
 */
public class EpubService {
    private static String rutaArchivo = "";
    /**
     * Loads an EPUB file from disk.
     * @param file the EPUB file
     * @return an EpubBook model, or null if loading fails
     */
    public static EpubBook loadEpub() {

        if (GraphicsEnvironment.isHeadless()) {
            try {
                ProcessBuilder pb = new ProcessBuilder("zenity", "--file-selection", "--title=Selecciona un archivo");
                Process proceso = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
                rutaArchivo = reader.readLine();
                reader.close();

                if (rutaArchivo == null || rutaArchivo.isEmpty()) {
                    System.out.println("No se seleccionó ningún archivo.");
                    return null;
                }
            } catch (IOException e) {
                System.err.println("Error al ejecutar zenity.");
                return null;
            }
        } else {
            javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
            int resultado = selector.showOpenDialog(null);

            if (resultado == javax.swing.JFileChooser.APPROVE_OPTION) {
                rutaArchivo = selector.getSelectedFile().getAbsolutePath();
            } else {
                System.out.println("No se seleccionó ningún archivo.");
                return null;
            }
        }

        try {
            EpubBook libro = EpubExtractor.FindHtmlAndXhtml(rutaArchivo);
            return libro;    
        } catch (Exception e) {
            System.err.println("Error al extraer contenido del archivo.");
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
    }

    /**
     * Saves an EpubBook model with file selection dialog.
     * @param book the EpubBook model to save
     * @return true if the book was saved successfully, false otherwise
     */
    public static boolean saveEpubWithDialog(EpubBook book) {
        if (book == null) {
            System.out.println("No hay libro para guardar.");
            return false;
        }

        String saveFilePath = null;

        if (GraphicsEnvironment.isHeadless()) {
            try {
                ProcessBuilder pb = new ProcessBuilder("zenity", "--file-selection", "--save", 
                    "--title=Guardar como EPUB", "--filename=libro.epub");
                Process proceso = pb.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                    saveFilePath = reader.readLine();
                }

                if (saveFilePath == null || saveFilePath.isEmpty()) {
                    System.out.println("No se seleccionó ubicación para guardar.");
                    return false;
                }
            } catch (IOException e) {
                System.err.println("Error al ejecutar zenity para guardar: " + e.getMessage());
                return false;
            }
        } else {
            javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
            selector.setDialogTitle("Guardar como EPUB");
            selector.setSelectedFile(new File("libro.epub"));
            
            // Add EPUB file filter
            javax.swing.filechooser.FileNameExtensionFilter filter = 
                new javax.swing.filechooser.FileNameExtensionFilter("Archivos EPUB (*.epub)", "epub");
            selector.setFileFilter(filter);
            
            int resultado = selector.showSaveDialog(null);
            
            if (resultado == javax.swing.JFileChooser.APPROVE_OPTION) {
                File selectedFile = selector.getSelectedFile();
                saveFilePath = selectedFile.getAbsolutePath();
                
                // Ensure .epub extension
                if (!saveFilePath.toLowerCase().endsWith(".epub")) {
                    saveFilePath += ".epub";
                }
            } else {
                System.out.println("No se seleccionó ubicación para guardar.");
                return false;
            }
        }

        try {
            File outputFile = new File(saveFilePath);
            saveEpub(book, outputFile);
            System.out.println("Libro guardado exitosamente en: " + outputFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar el libro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
