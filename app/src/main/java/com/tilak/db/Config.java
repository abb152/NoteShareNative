package com.tilak.db;

/**
 * Created by Wohlig on 06/10/15.
 */

import com.orm.SugarRecord;

public class Config extends SugarRecord {

    public String firstname;
    public String lastname;
    public String email;
    public String password;
    public String fbid;
    public String googleid;
    public int passcode;
    public String profilepic;
    public String username;
    public String deviceid;
    public String serverid;
    public String sort;
    public String view;
    public String gcmid;
    public int appversion;

    public Config() {
        super();
    }

    public Config(String firstname, String lastname, String email, String password, String fbid, String googleid, int passcode, String profilepic, String username, String deviceid, String serverid, String sort, String view, int appversion) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.fbid = fbid;
        this.googleid = googleid;
        this.passcode = passcode;
        this.profilepic = profilepic;
        this.username = username;
        this.deviceid = deviceid;
        this.serverid = serverid;
        this.sort = sort;
        this.view = view;
        this.appversion = appversion;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFbid() {
        return fbid;
    }

    public void setFbid(String fbid) {
        this.fbid = fbid;
    }

    public String getGoogleid() {
        return googleid;
    }

    public void setGoogleid(String googleid) {
        this.googleid = googleid;
    }

    public int getPasscode() {
        return passcode;
    }

    public void setPasscode(int passcode) {
        this.passcode = passcode;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public int getAppversion() {
        return appversion;
    }

    public void setAppversion(int appversion) {
        this.appversion = appversion;
    }

}