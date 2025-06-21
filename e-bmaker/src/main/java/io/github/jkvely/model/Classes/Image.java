package io.github.jkvely.model.Classes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Image {
    private String name;
    private String mimeType;
    private byte[] data;
    private String type; // Ejemplo: "cover", "chapter"
}
