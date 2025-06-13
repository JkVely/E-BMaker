package io.github.giosreina.ModelViewViewModel.OpenLogic.EpubUnzipped;
import java.util.zip.*;
import java.util.*;

public class PruebaEpub {
    public static void main(String[] args) {
        try {
            Map<String, byte[]> images = EpubExtractor.extractImages("C:\\Users\\ASUS\\Documents\\Mahouka Koukou no Rettousei - Volumen 01 [LNM&M].epub");
            for (Map.Entry<String, byte[]> entry : images.entrySet()) {
                String imageName = entry.getKey();
                byte[] imageData = entry.getValue();
                System.out.println("Image Name: " + imageName + ", Size: " + imageData.length + " bytes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
