package io.github.jkvely.model;

import lombok.Data;

@Data
public class EpubNotes extends EpubChapter{

    public EpubNotes(int id, String title, String footNotes) {
        super(id, title, footNotes, null);
    }
}
