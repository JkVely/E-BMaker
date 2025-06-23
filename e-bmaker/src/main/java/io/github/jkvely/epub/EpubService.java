package io.github.jkvely.epub;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.awt.GraphicsEnvironment;

import io.github.jkvely.model.EpubExtractor;
import io.github.jkvely.model.Classes.EpubBook;




/**
 * EpubService provides methods to load and save EPUB files.
 *
 * This is a placeholder for EPUB logic. You can use epublib or similar libraries here.
 */
public class EpubService {
    /**
     * Loads an EPUB file from disk.
     * @param file the EPUB file
     * @return an EpubBook model, or null if loading fails
     */
    public static EpubBook loadEpub(File file) {
        String rutaArchivo = "";

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
            EpubExtractor.FindHtmlAndXhtml(rutaArchivo);
        } catch (Exception e) {
            System.err.println("Error al extraer contenido del archivo.");
        }
        return null;    
    }

    /**
     * Saves an EpubBook model to disk as an EPUB file.
     * @param book the EpubBook model
     * @param file the destination file
     */
    public void saveEpub(EpubBook book, File file) {
        // TODO: Implement EPUB saving logic using epublib or similar
    }
}
