package com.tilak.db;

import com.orm.SugarRecord;

public class NoteElement extends SugarRecord {

    public String note;
    public int ordernumber;
    public String content;
    public String type;
    public String isSync;

    public NoteElement() {
        super();
    }

    public NoteElement(String note, int ordernumber, String content, String type, String isSync) {
        super();
        this.note = note;
        this.ordernumber = ordernumber;
        this.content = content;
        this.type = type;
        this.isSync = isSync;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(int ordernumber) {
        this.ordernumber = ordernumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }
}
