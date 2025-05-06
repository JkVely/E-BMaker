package io.github.jkvely.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EpubChapter represents a chapter or section in an EPUB book, supporting nested chapters and navigation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpubChapter {
    private int id;
    private String title;
    private String content;
    private List<Image> images;

    /**
     * Adds an image to the chapter.
     * @param image the image to add
     */
    public void addImage(Image image) {
        this.images.add(image);
    }

    /**
     * Removes an image from the chapter.
     * @param image the image to remove
     */
    public void removeImage(Image image) {
        this.images.remove(image);
    }
}
