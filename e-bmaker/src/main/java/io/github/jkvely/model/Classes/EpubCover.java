package io.github.jkvely.model.Classes;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EpubCover represents the cover image and metadata of an EPUB book.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpubCover {
    private String title;
    private List<String> authors;
    private Image image;
}
