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
    public String modifytime;

    public Folder() {
        super();
    }

    public Folder(String name, int ordernumber, String serverid, String creationtime, String modifytime) {
        this.creationtime = creationtime;
        this.modifytime = modifytime;
        this.name = name;
        this.ordernumber = ordernumber;
        this.serverid = serverid;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNumber() {
        return ordernumber;
    }

    public void setOrderNumber(int order) {
        this.ordernumber = order;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }
}
