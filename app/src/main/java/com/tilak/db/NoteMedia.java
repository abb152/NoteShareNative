package com.tilak.db;

import com.orm.SugarRecord;

/**
 * Created by Wohlig on 06/10/15.
 */
public class NoteMedia extends SugarRecord {

    public String note;

    public NoteMedia(String note) {
        super();
        this.note = note;
    }

    public NoteMedia(){
        super();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
