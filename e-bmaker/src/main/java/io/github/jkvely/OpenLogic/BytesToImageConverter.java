package io.github.jkvely.OpenLogic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Map;
import java.util.HashMap;

public class BytesToImageConverter {
    public static Map<String, BufferedImage> bytesToImage(Map<String, byte[]> imageBytes) throws IOException {
        Map<String, BufferedImage> images = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : imageBytes.entrySet()) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(entry.getValue())) {
                BufferedImage image = ImageIO.read(bais);
                if (image != null) {
                    images.put(entry.getKey(), image);
                } else {
                    System.err.println("Failed to convert bytes to image for: " + entry.getKey());
                }
            } catch (IOException e) {
                System.err.println("Error reading image bytes for: " + entry.getKey());
                System.out.println("Exception message: " + e.getMessage());
            
            }
        }
        return images;
    }
}
