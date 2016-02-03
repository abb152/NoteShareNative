package com.noteshareapp.db;

import com.orm.SugarRecord;

/**
 * Created by Wohlig on 06/10/15.
 */
public class Feeds extends SugarRecord {

    public String title;
    public String text;
    public String timestamp;
    public String serverid;

    public Feeds() {
        super();
    }

    public Feeds(String title, String text, String timestamp, String serverid) {
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
        this.serverid = serverid;
    }
}
