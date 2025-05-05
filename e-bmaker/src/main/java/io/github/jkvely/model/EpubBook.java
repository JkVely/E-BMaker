package io.github.jkvely.model;

import lombok.Data;
import java.util.List;

/**
 * EpubBook represents the structure of an EPUB book.
 */
@Data
public class EpubBook {
    private String title;
    private String author;
    private List<EpubChapter> chapters;
    private String language;
    private String identifier;
    // Add more metadata fields as needed
}
