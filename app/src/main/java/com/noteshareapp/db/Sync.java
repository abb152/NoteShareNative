package com.noteshareapp.db;

import com.orm.SugarRecord;

/**
 * Created by Jay on 16-12-2015.
 */
public class Sync extends SugarRecord{

    public Long folderServerToLocal;
    public Long folderLocalToServer;
    public Long noteServerToLocal;
    public Long noteLocalToServer;
    public Long lastSyncTime;
    public int syncType;

    public Sync() {
        super();
    }

    public Sync(Long folderLocalToServer, Long folderServerToLocal, Long noteLocalToServer, Long noteServerToLocal, Long lastSyncTime, int syncType) {
        this.folderLocalToServer = folderLocalToServer;
        this.folderServerToLocal = folderServerToLocal;
        this.noteLocalToServer = noteLocalToServer;
        this.noteServerToLocal = noteServerToLocal;
        this.lastSyncTime = lastSyncTime;
        this.syncType = syncType;
    }

    public Long getFolderLocalToServer() {
        return folderLocalToServer;
    }

    public void setFolderLocalToServer(Long folderLocalToServer) {
        this.folderLocalToServer = folderLocalToServer;
    }

    public Long getFolderServerToLocal() {
        return folderServerToLocal;
    }

    public void setFolderServerToLocal(Long folderServerToLocal) {
        this.folderServerToLocal = folderServerToLocal;
    }

    public Long getNoteLocalToServer() {
        return noteLocalToServer;
    }

    public void setNoteLocalToServer(Long noteLocalToServer) {
        this.noteLocalToServer = noteLocalToServer;
    }

    public Long getNoteServerToLocal() {
        return noteServerToLocal;
    }

    public void setNoteServerToLocal(Long noteServerToLocal) {
        this.noteServerToLocal = noteServerToLocal;
    }

    public Long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public int getSyncType() {
        return syncType;
    }

    public void setSyncType(int syncType) {
        this.syncType = syncType;
    }
}
