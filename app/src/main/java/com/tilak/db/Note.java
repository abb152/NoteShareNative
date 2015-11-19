package com.tilak.db;

import com.orm.SugarRecord;

public class Note extends SugarRecord {

    public String title;
    public String tags;
    public String color; //in hex
    public String folder;
    public String remindertime; //epoch
    public String timebomb;
    public String background;
    public String creationtime;
    public String modifytime;
    public String serverid;
    public String shownote; //remove
    public int islocked;

    public Note() {
        super();
    }

    public Note(String title, String tags, String color, String folder, String remindertime, String timebomb, String background, String creationtime, String modifytime, String serverid, String shownote, int islocked) {
        this.background = background;
        this.color = color;
        this.creationtime = creationtime;
        this.folder = folder;
        this.islocked = islocked;
        this.modifytime = modifytime;
        this.remindertime = remindertime;
        this.serverid = serverid;
        this.shownote = shownote;
        this.tags = tags;
        this.timebomb = timebomb;
        this.title = title;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getIslocked() {
        return islocked;
    }

    public void setIslocked(int islocked) {
        this.islocked = islocked;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }

    public String getRemindertime() {
        return remindertime;
    }

    public void setRemindertime(String remindertime) {
        this.remindertime = remindertime;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getShownote() {
        return shownote;
    }

    public void setShownote(String shownote) {
        this.shownote = shownote;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTimebomb() {
        return timebomb;
    }

    public void setTimebomb(String timebomb) {
        this.timebomb = timebomb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
