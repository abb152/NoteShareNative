package com.tilak.db;

import com.orm.SugarRecord;

public class Note extends SugarRecord {

    public String title;
    public String tags;
    public String color;
    public String folder;
    public String remindertime;
    public String timebomb;
    public String background;
    public String creationtime;
    public String modificationtime;
    public String serverid;

    public Note() {
        super();
    }

    public Note(String title, String tags, String color, String folder, String remindertime, String timebomb, String background, String creationtime, String modificationtime, String serverid) {
        this.title = title;
        this.tags = tags;
        this.color = color;
        this.folder = folder;
        this.remindertime = remindertime;
        this.timebomb = timebomb;
        this.background = background;
        this.creationtime = creationtime;
        this.modificationtime = modificationtime;
        this.serverid = serverid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getRemindertime() {
        return remindertime;
    }

    public void setRemindertime(String remindertime) {
        this.remindertime = remindertime;
    }

    public String getTimebomb() {
        return timebomb;
    }

    public void setTimebomb(String timebomb) {
        this.timebomb = timebomb;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }
}
