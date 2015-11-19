package com.tilak.db;

import com.orm.SugarRecord;

public class NoteElement extends SugarRecord {

    public long noteid;
    public int ordernumber;
    public String content;
    public String contentA;
    public String contentB;
    public String type;
    public String isSync;

    public NoteElement() {
        super();
    }

    public NoteElement(long noteid, int ordernumber, String isSync, String type, String content, String contentA, String contentB) {
        this.content = content;
        this.contentA = contentA;
        this.contentB = contentB;
        this.isSync = isSync;
        this.noteid = noteid;
        this.ordernumber = ordernumber;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentA() {
        return contentA;
    }

    public void setContentA(String contentA) {
        this.contentA = contentA;
    }

    public String getContentB() {
        return contentB;
    }

    public void setContentB(String contentB) {
        this.contentB = contentB;
    }

    public String getIsSync() {
        return isSync;
    }

    public void setIsSync(String isSync) {
        this.isSync = isSync;
    }

    public long getNoteid() {
        return noteid;
    }

    public void setNoteid(long noteid) {
        this.noteid = noteid;
    }

    public int getOrderNumber() {
        return ordernumber;
    }

    public void setOrderNumber(int order) {
        this.ordernumber = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
