package io.github.jkvely.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * EpubChapter represents a chapter or section in an EPUB book, supporting nested chapters and navigation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpubChapter {
    private String id;
    private String title;
    private String content;
    private int order;
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
