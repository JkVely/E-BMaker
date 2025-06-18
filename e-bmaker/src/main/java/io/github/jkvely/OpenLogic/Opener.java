package io.github.jkvely.OpenLogic;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Opener {
    public static String getRuta() {
        String rutaArchivo = null;

        if (GraphicsEnvironment.isHeadless()) {
            try {
                ProcessBuilder pb = new ProcessBuilder("zenity", "--file-selection", "--title=Selecciona un archivo");
                Process proceso = pb.start();

                // Leer la salida del proceso
                BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
                rutaArchivo = reader.readLine();  // zenity devuelve la ruta seleccionada
                reader.close();

                if (rutaArchivo == null || rutaArchivo.isEmpty()) {
                    System.out.println("No se seleccionó ningún archivo.");
                }

            } catch (Exception e) {
                System.err.println("Error al ejecutar zenity o no está instalado.");
                e.printStackTrace();
            }
        } else {
            // Modo gráfico tradicional con Swing
            javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
            int resultado = selector.showOpenDialog(null);

            if (resultado == javax.swing.JFileChooser.APPROVE_OPTION) {
                rutaArchivo = selector.getSelectedFile().getAbsolutePath();
            } else {
                System.out.println("No se seleccionó ningún archivo.");
            }
        }

        // Mostrar la ruta seleccionada
        return rutaArchivo;
    }
}
