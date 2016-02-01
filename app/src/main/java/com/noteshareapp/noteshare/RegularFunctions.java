package com.noteshareapp.noteshare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.noteshareapp.db.Config;
import com.noteshareapp.db.Folder;
import com.noteshareapp.db.Note;
import com.noteshareapp.db.Sync;
import com.noteshareapp.sync.FolderSync;
import com.noteshareapp.sync.NoteSync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Jay on 16-12-2015.
 */
public class RegularFunctions {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    public static String SERVER_URL = "http://104.197.47.172/";
    //public static String SERVER_URL = "http://192.168.0.122:1337/";

    public static  String PREFERENCES = "NotesharePreferences";

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

    public static String getServerFolderId(String localFolderid){
        Folder folder = Folder.findById(Folder.class, Long.parseLong(localFolderid));
        return folder.getServerid();
    }


    public static String getUserId(){
        Config config = Config.findById(Config.class,1L);
        return config.getServerid();
    }

    public static String getNoteName(String noteId) {
        Note note = Note.findById(Note.class, Long.parseLong(noteId));
        return note.getTitle();
    }

    public static String post(String url, String json) throws IOException {
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

        changeLastSyncTime();
    }

    public static boolean checkLastSyncDifference(){

        long currentTime = getCurrentTimeLong();
        long lastTime = lastSyncLong();

        long difference = currentTime - lastTime;

        if (difference > 10800000L) {
            return true;
        }
        return false;
    }

    public static String lastSyncTime(){
        Sync sync = Sync.findById(Sync.class, 1l);
        if(sync.getLastSyncTime() == 0)
            return "Not Synced yet";
        else
            return "Last Synced: " + longToStringWithUTC(sync.getLastSyncTime());
    }

    public static long lastSyncLong(){
        Sync sync = Sync.findById(Sync.class,1l);
        if(sync.getLastSyncTime() == 0)
            return 1451610000000L;
        else
            return sync.getLastSyncTime();
    }

    public static String longToStringWithUTC(long date){

        TimeZone tz = TimeZone.getDefault();
        String time = "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /*if (tz == null || "".equalsIgnoreCase(tz.trim())) {
            tz = Calendar.getInstance().getTimeZone().getID();
        }*/


        // set timezone to SimpleDateFormat
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));


        return simpleDateFormat.format(date);
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
        //s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeNoteServerToLocalTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setNoteServerToLocal(currentTime);
        //s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeNoteLocalToServerTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setNoteLocalToServer(currentTime);
        //s.setLastSyncTime(currentTime);
        s.save();
    }

    public static void changeFolderServerToLocalTime() {

        Sync s = Sync.findById(Sync.class, 1l);
        long currentTime = getCurrentTimeLong();

        s.setFolderServerToLocal(currentTime);
        //s.setLastSyncTime(currentTime);
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

    public static int getUserSyncType(){

        Sync syncSet = Sync.findById(Sync.class,1l);
        return syncSet.getSyncType();
    }

    public static int checkInternetConnectivity(Context context){

        if(checkIsOnlineViaIP()){

            int type = typeOfInternetConnection(context);
            int userType = getUserSyncType();

            Log.e("jay sync type", String.valueOf(type));
            Log.e("jay sync userType", String.valueOf(userType));

            if(userType == 3 && (type == 1 || type == 2)){ // if sync via both is selected
                Log.e("jay sync via","both");
                return 1;
            } else if(type == userType){ // if sync via mobile or wifi selected and current network type is also same
                Log.e("jay sync via is same", String.valueOf(type));
                return 1;
            } else if (type == 2 && userType == 2) { // if sync via only wifi is selected and current network type is mobile
                Log.e("jay sync via type mob","and current mob");
                return 2;
            }
            else
                return 2;
        }
        return 0;
    }


    public static int typeOfInternetConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        int connectionType = 0; //0 for no connection, 1 for wifi, 2 for mobile


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
                if (ni.isConnected()){
                    haveConnectedMobile = true;
                    connectionType = 2;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("WIFI")){
                if (ni.isConnected()){
                    haveConnectedWifi = true;
                    connectionType = 1;
                }
            }
        }
        return connectionType;
    }



    public static boolean checkIsOnlineViaIP() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 104.197.47.172");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e){
            e.printStackTrace();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        return false;
    }


    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     */
    /*public static boolean isConnected(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    *//**
     * Check if there is any connectivity to a Wifi network
     *//*
    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }*/

    /**
     * Check if there is any connectivity to a mobile network
    */

    /*public static boolean isConnectedMobile(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }*/


    public static boolean checkNeedForSync(){
        Sync syncNote = RegularFunctions.getSyncTime();
        Long timeNote = syncNote.getNoteLocalToServer() - 3600000;
        List<Note> notes = getNoteList(timeNote);

        Sync syncFolder = RegularFunctions.getSyncTime();
        Long timeFolder = syncFolder.getFolderLocalToServer() - 3600000;
        List<Folder> folders = getFolderList(timeFolder);

        Log.e("jay sync notes", String.valueOf(notes.size()));
        Log.e("jay sync folders",String.valueOf(folders.size()));

        return notes.size() > 0 || folders.size() > 0;
    }

    public static List<Note> getNoteList(Long time) {
        //Long time = 1448954670000L;
        List<Note> notes = Note.findWithQuery(Note.class, "Select * from NOTE where MTIME > " + time + " ORDER BY MTIME ASC");
        return notes;
    }

    public static List<Folder> getFolderList(Long time){
        //Long time = 1448954670000L;
        List<Folder> folders = Folder.findWithQuery(Folder.class, "Select * from FOLDER where MTIME > " + time + " ORDER BY MTIME ASC");
        return folders;
    }


    public static void getBitmapFromURL(String src, String picname) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            try {
                // Saving Image file
                //String profilePicture = String.valueOf("profile");
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/.NoteShare/" + picname);
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mediaStorageDir));
            } catch (FileNotFoundException e) {}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Typeface getAgendaBoldFont(Activity activity){
        return Typeface.createFromAsset(activity.getAssets(), "fonts/AgendaBold.ttf");
    }

    public static Typeface getAgendaMediumFont(Activity activity){
        return Typeface.createFromAsset(activity.getAssets(), "fonts/Agenda-Medium.ttf");
    }

    public static JSONObject getNotificationsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", RegularFunctions.getUserId().trim());
        } catch (JSONException je) {

        }
        return jsonObject;
    }

    public static int getNotificationCount(Context context){

        try {
            String notificationJson = getNotificationsJson().toString();
            Log.e("jay sharejson", notificationJson);

            String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "notification/find", notificationJson);
            Log.e("jay response", response);

            JSONArray jsonArray = new JSONArray(response);

            Log.e("jay json size", String.valueOf(jsonArray.length()));

            //String value = jsonObject.get("value").toString();

            if (jsonArray.length() >= 0) {

                SharedPreferences sharedpreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("NotificationCount", jsonArray.length());
                editor.apply();

                Log.e("jay count", String.valueOf(sharedpreferences.getInt("NotificationCount",0)));

                return jsonArray.length();
            } else {
                Log.e("jay ", "no notifications");
                return 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }

        return 0;
    }
}
