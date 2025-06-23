package io.github.jkvely.model.Classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private String name;
    private String mimeType;
    private byte[] data;
    private String type; // Ejemplo: "cover", "chapter"
}
