package io.github.jkvely.WelcomeInterface.OpenLogic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BytesToImageConverter {
    public static BufferedImage bytesToImage(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bais);
        }
    }
}
