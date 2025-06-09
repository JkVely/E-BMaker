package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;
import java.util.zip.*;
import java.util.*;

public class PruebaEpub {
    public static void main(String[] args) {
        try {
            // Asegúrate de que el archivo ZIP exista en la ruta especificada
            ZipFile file = new ZipFile("C:\\Users\\ASUS\\Documents\\htmlsDePrueba.zip");
            System.out.println("El archivo existe: " + file);
            Map<String, List<String>> htmlAndXhtmlContent = EpubExtractor.FindHtmlAndXhtml(file);

            
            for(List<String> content : htmlAndXhtmlContent.values()) {
                for(String line : content) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
