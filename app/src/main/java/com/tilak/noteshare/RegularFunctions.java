package com.tilak.noteshare;

import android.content.Context;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tilak.db.Config;
import com.tilak.db.Note;
import com.tilak.db.Sync;
import com.tilak.sync.FolderSync;
import com.tilak.sync.NoteSync;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by Jay on 16-12-2015.
 */
public class RegularFunctions {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    public static String SERVER_URL = "http://104.197.122.116/";
    //public static String SERVER_URL = "http://192.168.0.125:1337/";

    public static int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int dpFromPx(final Context context, final float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public static String getDeviceId(){
        Config config = Config.findById(Config.class,1L);
        return config.getDeviceid();
    }

    public static String getUserName(){
        Config config = Config.findById(Config.class,1L);
        return config.getFirstname();
    }

    public static String getServerNoteId(String localNoteid){
        Note note = Note.findById(Note.class, Long.parseLong(localNoteid));
        return note.getServerid();
    }

    public static String getUserId(){
        Config config = Config.findById(Config.class,1L);
        return config.getServerid();
    }

    public static String getNoteName(String noteId) {
        Note note = Note.findById(Note.class, Long.parseLong(noteId));
        return note.getTitle();
    }

    static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static void syncNow() {
        FolderSync folderSync = new FolderSync();
        folderSync.localToServer();
        folderSync.serverToLocal();

        NoteSync noteSync = new NoteSync();
        noteSync.localToServer();
        noteSync.serverToLocal();
    }

    public static String lastSyncTime(){
        Sync sync = Sync.findById(Sync.class, 1l);
        if(sync.getLastSyncTime() == 0)
            return "Not Synced yet";
        else
            return "Last Synced: " + longToString(sync.getLastSyncTime());
    }

    public static String longToString(long date){

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static long getCurrentTimeLong(){
        Date date = new Date();
        return date.getTime();
    }

    public static Sync getSyncTime(){
        Sync s = Sync.findById(Sync.class, 1l);
        return s;
    }

    public static long stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
    }

    public static void changeFolderLocalToServerTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setFolderLocalToServer(currentTime);
        s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeNoteServerToLocalTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setNoteServerToLocal(currentTime);
        s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeNoteLocalToServerTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setNoteLocalToServer(currentTime);
        s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeFolderServerToLocalTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setFolderServerToLocal(currentTime);
        s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeLastSyncTime() {
        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setLastSyncTime(currentTime);
        s.save();
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
