package io.github.jkvely.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Singular;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * EpubBook represents the structure and metadata of an EPUB book.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpubBook {
    private String title;
    @Singular
    private List<String> authors;
    private String language;
    /** Unique identifier (ISBN, UUID, etc). */
    private String identifier;
    private String description;
    @Singular
    private List<String> subjects;
    private String rights;
    private Image coverImage;
    private String series;
    private int seriesIndex;
    @Singular
    private List<EpubChapter> chapters;
    @Singular("metadata")
    private Map<String, String> metadata;

    /**
     * Adds a chapter to the book.
     * @param chapter the chapter to add
     */
    public void addChapter(EpubChapter chapter) {
        this.chapters.add(chapter);
    }

    /**
     * Removes a chapter from the book.
     * @param chapter the chapter to remove
     */
    public void removeChapter(EpubChapter chapter) {
        this.chapters.remove(chapter);
    }

    /**
     * Adds a custom metadata entry.
     * @param key metadata key
     * @param value metadata value
     */
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
}
