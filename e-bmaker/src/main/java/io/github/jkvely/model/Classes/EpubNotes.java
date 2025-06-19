package io.github.jkvely.model.Classes;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class EpubNotes extends EpubChapter{

    public EpubNotes(int id, String title, String footNotes) {
        super(id, title, footNotes, null);
    }
}
