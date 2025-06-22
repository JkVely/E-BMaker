package io.github.jkvely.model;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Opener {
    public static String getRuta() {
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
            EpubExtractor.extractImages(rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al extraer contenido del archivo.");
        }

        return rutaArchivo;
    }
}
