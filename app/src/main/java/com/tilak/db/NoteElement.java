package com.tilak.db;

import com.orm.SugarRecord;

public class NoteElement extends SugarRecord {

    public long noteid;
    public int ordernumber;
    public String content;
    public String type;
    public String isSync;

    public NoteElement() {
        super();
    }

    public long getNoteid() {
        return noteid;
    }

    public void setNoteid(long noteid) {
        this.noteid = noteid;
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

    public NoteElement(long noteid, int ordernumber, String isSync, String type, String content) {

        this.noteid = noteid;
        this.ordernumber = ordernumber;
        this.isSync = isSync;
        this.type = type;
        this.content = content;
    }
}
