package io.github.jkvely.model;

import lombok.Data;

/**
 * EpubChapter represents a chapter or section in an EPUB book.
 */
@Data
public class EpubChapter {
    private String title;
    private String content; // HTML or XHTML content
}
