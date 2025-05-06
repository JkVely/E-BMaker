package io.github.jkvely.model;

import lombok.Data;

@Data
public class EpubNotes extends EpubChapter{
    private int id;
    private String title;
    private String footNotes;

    public EpubNotes(int id, String title, String footNotes) {
        super(id, title, footNotes, null);
        this.id = id;
        this.title = title;
        this.footNotes = footNotes;
    }
}
