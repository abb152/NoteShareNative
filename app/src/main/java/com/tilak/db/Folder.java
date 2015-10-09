package com.tilak.db;

import com.orm.SugarRecord;

/**
 * Created by Wohlig on 06/10/15.
 */
public class Folder extends SugarRecord {

    public String name;
    public int ordernumber;
    public String serverid;
    public String creationtime;
    public String modificationtime;

    public Folder() {
        super();
    }

    public Folder(String name, int ordernumber, String serverid, String creationtime, String modificationtime) {
        super();
        this.name = name;
        this.ordernumber = ordernumber;
        this.serverid = serverid;
        this.creationtime = creationtime;
        this.modificationtime = modificationtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(int ordernumber) {
        this.ordernumber = ordernumber;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }

    public String getModificationtime() {
        return modificationtime;
    }

    public void setModificationtime(String modificationtime) {
        this.modificationtime = modificationtime;
    }
}
