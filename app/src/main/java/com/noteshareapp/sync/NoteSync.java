package com.noteshareapp.sync;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.noteshareapp.db.Config;
import com.noteshareapp.db.Folder;
import com.noteshareapp.db.Note;
import com.noteshareapp.db.NoteElement;
import com.noteshareapp.db.Sync;
import com.noteshareapp.noteshare.MainActivity;
import com.noteshareapp.noteshare.RegularFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jay on 02-12-2015.
 */
enum NOTESYNCFUNCTION {
    CREATE,
    DELETE,
    EDIT
}

public class NoteSync {
    //public static String SERVER_URL = "http://104.197.122.116/";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private int type = 0; //1 create //2 delete //0 edit
    NOTESYNCFUNCTION funcType;




    public void localToServer() {

        Sync sync = RegularFunctions.getSyncTime();
        Long time = sync.getNoteLocalToServer() - 3600000;
        //Long time = sync.getNoteLocalToServer() - 86400000;
        List<Note> notes = getNoteList(time);
        if (notes.size() > 0) {

            for (int i = 0; i < notes.size(); i++) {

                if (notes.get(i).getServerid().equals("0"))
                    funcType = NOTESYNCFUNCTION.CREATE;
                else if (notes.get(i).getCreationtime().equals("0"))
                    funcType = NOTESYNCFUNCTION.DELETE;
                else
                    funcType = NOTESYNCFUNCTION.EDIT;

                Log.e("jay funcType", funcType.name());

                try {
                    String json = localToServerNoteJson(notes.get(i).getTitle(), notes.get(i).getTags(), notes.get(i).getColor(), notes.get(i).getFolder(), String.valueOf(notes.get(i).getRemindertime()), notes.get(i).getTimebomb(), notes.get(i).getBackground(), notes.get(i).getCreationtime(), notes.get(i).getModifytime(), String.valueOf(notes.get(i).getIslocked()), notes.get(i).getCtime(), notes.get(i).getMtime(), getUserId(), funcType, notes.get(i).getServerid(), notes.get(i).getId()).toString();
                    String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "note/localtoserver", json);

                    JSONObject jsonObject = new JSONObject(response);

                    switch (funcType) {
                        case CREATE:
                            String noteServerId = jsonObject.get("id").toString();
                            notes.get(i).setServerid(noteServerId);
                            notes.get(i).save();

                            //send note element media
                            sendNoteElementMedia(notes.get(i).getId());

                            Date createDate = new Date();
                            notes.get(i).setModifytime(dateToString(createDate));
                            notes.get(i).setMtime(createDate.getTime());

                            RegularFunctions.changeNoteLocalToServerTime();

                            Log.e("jay create", "");
                            break;

                        case DELETE:
                            notes.get(i).delete();

                            RegularFunctions.changeNoteLocalToServerTime();
                            Log.e("jay delete", "");
                            break;

                        case EDIT:
                            Date editDate = new Date();
                            notes.get(i).setModifytime(dateToString(editDate));
                            notes.get(i).setMtime(editDate.getTime());

                            //send note element media
                            sendNoteElementMedia(notes.get(i).getId());

                            RegularFunctions.changeNoteLocalToServerTime();
                            Log.e("jay edit", "");
                            break;

                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }

        }

        //RegularFunctions.changeLastSyncTime();
    }

    public void serverToLocal() {
        //String notemodifytime = "1970-01-01 00:00:00";

        Sync sync = RegularFunctions.getSyncTime();
        //Long time = sync.getFolderLocalToServer();

        //String notemodifytime = RegularFunctions.longToStringWithUTC(sync.getNoteLocalToServer() - 3600000);
        String notemodifytime = RegularFunctions.longToStringWithUTC(sync.getNoteLocalToServer() - 86400000);

        Log.e("jay long", String.valueOf(sync.getNoteLocalToServer()));

        Log.e("jay notemodifytime", notemodifytime);

        try {
            String json = serverToLocalJson(getUserId(), notemodifytime).toString();
            Log.e("jay s2l json", json);
            String response = null;
            try {
                response = RegularFunctions.post(RegularFunctions.SERVER_URL + "note/servertolocal", json);
            } catch (IOException io) {
                Log.e("jay exception io", Log.getStackTraceString(io));
            }

            JSONArray jsonArray = new JSONArray(response);
            //Log.e("jay length", String.valueOf(jsonArray.length()));
            //Log.e("jay jsonArray", jsonArray.toString());

            Log.e("jay note len", String.valueOf(jsonArray.length()));


            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    Log.e("jay note id", String.valueOf(jsonArray.getJSONObject(i).getString("_id")));
                    Log.e("jay note title", String.valueOf(jsonArray.getJSONObject(i).optString("folder")));


                    String title = jsonArray.getJSONObject(i).getString("title");
                    String color = jsonArray.getJSONObject(i).getString("color");
                    String folder = jsonArray.getJSONObject(i).optString("folder");
                    String background = jsonArray.getJSONObject(i).getString("background");
                    String tags = jsonArray.getJSONObject(i).getString("tags");
                    String creationtime = jsonArray.getJSONObject(i).getString("creationtime");
                    String modifytime = jsonArray.getJSONObject(i).getString("modifytime");
                    String islocked = jsonArray.getJSONObject(i).getString("islocked");
                    String remindertime = jsonArray.getJSONObject(i).getString("remindertime");
                    String timebomb = jsonArray.getJSONObject(i).getString("timebomb");
                    String id = jsonArray.getJSONObject(i).getString("_id");

                    Log.e("jay i ", String.valueOf(i));
                    Log.e("jay name", title);
                    Log.e("jay folder", folder);

                    if(folder.isEmpty())
                        folder = "0";

                    JSONArray noteElement = null;
                    try {
                        //noteElement = jsonArray.getJSONObject(i).getJSONArray("noteelements");
                        noteElement = jsonArray.getJSONObject(i).optJSONArray("noteelements");
                        //Log.e("jay ne size", String.valueOf(noteElement.length()));
                    } catch (JSONException je) {
                        Log.e("jay Some tag", Log.getStackTraceString(je));
                    }
                    //Log.e("jay ne size", String.valueOf(noteElement.length()));


                    funcType = null;

                    if (creationtime.equals(""))
                        funcType = NOTESYNCFUNCTION.DELETE;
                    else if (!noteAlreadyExists(id))
                        funcType = NOTESYNCFUNCTION.CREATE;
                    else
                        funcType = NOTESYNCFUNCTION.EDIT;

                    Log.e("jay funcType", funcType.name());

                    switch (funcType) {
                        case CREATE:
                            if (timebomb.equals("0"))
                                timebomb = "";

                            try {
                                Note createNote = new Note(title, tags, color, getFolderLocalId(folder), Long.parseLong(remindertime), timebomb, background, dateServerToLocal(creationtime), dateServerToLocal(modifytime), id, Integer.parseInt(islocked), stringToDate(dateServerToLocal(creationtime)), stringToDate(dateServerToLocal(modifytime)));
                                createNote.save();
                                serverToLocalNoteElementsNew(noteElement, createNote.getId());

                                RegularFunctions.changeNoteServerToLocalTime();

                                Log.e("jay created ***", String.valueOf(createNote.getId()));
                            } catch (ParseException pe) {
                                Log.e("jay parseexception", Log.getStackTraceString(pe));
                            }

                            break;

                        case DELETE:
                            List<Note> deleteNote = Note.findWithQuery(Note.class, "Select * from NOTE where serverid LIKE ?", id);

                            if (deleteNote.size() == 0)
                                Log.e("jay already not there", "***");
                            else {
                                deleteNoteElements(deleteNote.get(0).getId());
                                deleteNote.get(0).delete();
                            }

                            RegularFunctions.changeNoteServerToLocalTime();
                            Log.e("jay deleted", "***");
                            break;
                        case EDIT:
                            List<Note> editNote = Note.findWithQuery(Note.class, "Select * from NOTE where serverid LIKE ?", id);
                            editNote.get(0).setTitle(title);
                            editNote.get(0).setTags(tags);
                            editNote.get(0).setColor(color);
                            editNote.get(0).setFolder(getFolderLocalId(folder));

                            Log.e("jay note fl", folder);
                            Log.e("jay note folder local", getFolderLocalId(folder));

                            editNote.get(0).setRemindertime(Long.parseLong(remindertime));

                            if (timebomb.equals("0"))
                                editNote.get(0).setTimebomb("");
                            else
                                editNote.get(0).setTimebomb(timebomb);

                            editNote.get(0).setBackground(background);
                            editNote.get(0).setServerid(id);
                            editNote.get(0).setIslocked(Integer.parseInt(islocked));
                            try {
                                editNote.get(0).setCreationtime(dateServerToLocal(creationtime));
                                editNote.get(0).setModifytime(dateServerToLocal(modifytime));
                                editNote.get(0).setCtime(stringToDate(dateServerToLocal(creationtime)));
                                editNote.get(0).setMtime(stringToDate(dateServerToLocal(modifytime)));
                            } catch (ParseException pe) {
                                Log.e("jay parseException", Log.getStackTraceString(pe));
                            }
                            editNote.get(0).save();

                            deleteNoteElements(editNote.get(0).getId());
                            //changes in note elements
                            serverToLocalNoteElementsNew(noteElement, editNote.get(0).getId());

                            RegularFunctions.changeNoteServerToLocalTime();

                            Log.e("jay edited", editNote.get(0).getTitle());
                            break;
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("jay exception ns e", String.valueOf(e));
            //Log.e("jay Some tag", Log.getStackTraceString(e.getCause().getCause()));
            Log.e("jay Some tag", Log.getStackTraceString(e));
            //Log.getStackTraceString(new Exception());
        }/* catch (IOException io){
            Log.e("jay exception io", String.valueOf(io));
            io.printStackTrace();
        }*/
        RegularFunctions.changeLastSyncTime();
    }

    public boolean noteAlreadyExists(String id) {
        List<Note> notes = Note.findWithQuery(Note.class, "Select * from NOTE where serverid LIKE ?", id);
        Log.e("jay size", String.valueOf(notes.size()));
        boolean exists;
        if (notes.size() > 0) {
            exists = true;
        } else {
            exists = false;
        }
        return exists;
    }

    public String dateToString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public long stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
    }//

    public String dateServerToLocal(String date) throws ParseException {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(serverToLocalDateFormat(date)).toString();
    }//

    public static long serverToLocalDateFormat(String date) throws ParseException {

        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date).getTime();
    }//

    public String getUserId() {
        return Config.findById(Config.class, 1L).getServerid();
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        /*Response response = null;
        try {
            response = client.newCall(request).execute();
        }catch (SocketTimeoutException se){

        }*/
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void sendNoteElementMedia(long noteId){

        List<NoteElement> noteElements = getNoteElementList(noteId);

        for(int i=0; i < noteElements.size(); i++){
            String content = noteElements.get(i).getContent();
            String type = noteElements.get(i).getType().trim();
            if(type.equals("image") || type.equals("scribble") || type.equals("audio")) {
                if (!checkIfUploaded(content)) {
                    try {
                        upload(type, content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean checkIfUploaded(String filename){
        boolean alreadyUploaded = false;
        try {
            String response = checkMediaAlreadyUploadedResponse(RegularFunctions.SERVER_URL+"/searchmedia?file="+filename);
            JSONObject jsonObject = new JSONObject(response);
            String value = jsonObject.getString("value");

            if(value.equals("true"))
                alreadyUploaded = true;

        }catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }

        return alreadyUploaded;
    }

    String checkMediaAlreadyUploadedResponse(String url) throws IOException{

        Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
    }


    public void downloadMedia(String url, String type, String name){
        byte[] media= null;
        try {
            media = getMedia(url+name);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(type.equals("image")){
            try {
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + name);
                if(!mediaStorageDir.exists()) {
                    Bitmap b = BitmapFactory.decodeByteArray(media, 0, media.length);
                    b.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(mediaStorageDir));

                    // Refreshing Gallery to view Image in Gallery
                    MainActivity mainActivity = new MainActivity();

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, mediaStorageDir.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    mainActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }catch(FileNotFoundException fe){

            }catch(NullPointerException npe){

            }
        }

        else if(type.equals("scribble")){
            try {
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/.NoteShare/" + name);
                if(!mediaStorageDir.exists()) {
                    Bitmap b = BitmapFactory.decodeByteArray(media, 0, media.length);
                    b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(mediaStorageDir));
                }
            }catch(FileNotFoundException fe){

            }catch(NullPointerException npe){

            }
        }

        else if(type.equals("audio")){
            try {
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Audio/" + name);
                if(!mediaStorageDir.exists()) {
                    FileOutputStream fos = new FileOutputStream(mediaStorageDir);
                    fos.write(media);
                    fos.flush();
                    fos.close();
                }
            }catch(FileNotFoundException fe){

            }catch (IOException io){

            }catch(NullPointerException npe){

            }
        }
    }

    byte[] getMedia(String url) throws IOException {
        //RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().bytes();
    }



    public List<Note> getNoteList(Long time) {
        //Long time = 1448954670000L;
        List<Note> notes = Note.findWithQuery(Note.class, "Select * from NOTE where MTIME > " + time + " ORDER BY MTIME ASC");
        return notes;
    }

    public JSONObject localToServerNoteJson(String title, String tags, String color, String folder, String remindertime, String timebomb, String background, String creationtime, String modifytime, String islocked, long ctime, long mtime, String user, NOTESYNCFUNCTION function, String id, long localId) throws JSONException {


        JSONObject note = new JSONObject();

        note.put("user", user);
        note.put("title", title);
        note.put("color", color);

        note.put("folder", getFolderServerId(folder));

        Log.e("note fs", folder);
        Log.e("note folder ServerId", getFolderServerId(folder));

        note.put("background", background);
        note.put("tags", tags);

        note.put("creationtime", creationtime);
        note.put("modifytime", modifytime);
        note.put("islocked", String.valueOf(islocked));
        note.put("remindertime", remindertime);

        JSONArray jsonArray = new JSONArray();

        List<NoteElement> noteElements = getNoteElementList(localId);
        if (noteElements.size() > 0) {
            for (int i = 0; i < noteElements.size(); i++) {
                jsonArray.put(noteElementJson(noteElements.get(i).getContent(), noteElements.get(i).getContentA(), noteElements.get(i).getContentB(), noteElements.get(i).getType(), noteElements.get(i).getOrderNumber()));
            }
        }
        //jsonArray.put(serverToLocalJson(getUserId(), getUserId()));

        note.put("noteelements", jsonArray);


        if (timebomb.equals(""))
            note.put("timebomb", "0");
        else
            note.put("timebomb", timebomb);

        if (function != NOTESYNCFUNCTION.CREATE) {
            note.put("_id", id);
        }
        Log.e("jay folder json", note.toString());
        return note;
    }

    public JSONObject serverToLocalJson(String user, String modifytime) throws JSONException {
        //Log.e("jay inside json","");
        JSONObject folder = new JSONObject();

        folder.put("user", user);
        folder.put("modifytime", modifytime);
        //Log.e("jay json", folder.toString());
        return folder;
    }

    public JSONObject noteElementJson(String content, String contentA, String contentB, String type, int order) throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("content", content);
        jsonObject.put("contentA", contentA);
        jsonObject.put("contentB", contentB);
        jsonObject.put("type", type);
        jsonObject.put("order", String.valueOf(order));

        return jsonObject;
    }

    public List<NoteElement> getNoteElementList(long localId) {
        //List<NoteElement> noteElements = NoteElement.findWithQuery(NoteElement.class, "Select * from NOTEELEMENT where id = ?", String.valueOf(localId));
        return NoteElement.findWithQuery(NoteElement.class, "Select * from NOTE_ELEMENT where noteid = ?", String.valueOf(localId));
    }

    public void serverToLocalNoteElementsNew(JSONArray noteElements, long noteid) {
        String content, contentA, contentB, type, isSync = "yes";
        int ordernumber;

        if (noteElements.length() > 0) {

            for (int j = 0; j < noteElements.length(); j++) {
                try {
                    JSONObject jsonObject = noteElements.getJSONObject(j);

                    content = jsonObject.get("content").toString();
                    contentA = jsonObject.get("contentA").toString();
                    contentB = jsonObject.get("contentB").toString();
                    type = jsonObject.get("type").toString();
                    ordernumber = Integer.parseInt(jsonObject.get("order").toString());

                    NoteElement noteElement = new NoteElement(noteid, ordernumber, isSync, type, content, contentA, contentB);
                    noteElement.save();

                    downloadMedia( RegularFunctions.SERVER_URL +"user/getmedia?file=", type, content);

                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }
    }

    public void deleteNoteElements(long noteId) {
        List<NoteElement> noteElements = NoteElement.findWithQuery(NoteElement.class, "Select * from NOTE_ELEMENT where noteid = ?", String.valueOf(noteId));
        if (noteElements.size() > 0) {
            for (int i = 0; i < noteElements.size(); i++) {
                noteElements.get(i).delete();
                Log.e("jay deleted noteelement", "");
            }
        }
    }

    public void upload(String type, String name) throws IOException {

        Log.e("jay type", type);

        File file;
        RequestBody requestBody = null;
        try {
            if(type.equals("audio")){
                file = new File(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Audio/" + name);
                requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(MediaType.parse("audio/m4a"), file))
                        .build();

            }else if(type.equals("image")){
                file = new File(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Images/" + name);
                requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(MediaType.parse("image/jpg"), file))
                        .build();

            }else if(type.equals("scribble")){
                Log.e("jay inside", "scribble");
                file = new File(Environment.getExternalStorageDirectory() + "/NoteShare/.NoteShare/" + name);
                requestBody = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(MediaType.parse("image/png"), file))
                        .build();

            }

            Request request = new Request.Builder()
                    .url(RegularFunctions.SERVER_URL+"user/mediaupload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    // Handle the error
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Handle the error
                        Log.e("jay not successful","");
                    }
                    else{
                        Log.e("jay successful","");
                    }
                    // Upload successful
                }
            });

        } catch (Exception ex) {
            // Handle the error

            Log.e("jay exception", Log.getStackTraceString(ex));
        }

    }

    public String getFolderServerId(String folderLocalId){
        if(folderLocalId.equals("0")){
            return "0";
        }else{
            Log.e("jay folder id", folderLocalId);
            Folder folder = Folder.findById(Folder.class, Long.parseLong(folderLocalId));
            return folder.getServerid();
        }
    }

    public String getFolderLocalId(String folderServerId){
        if(folderServerId.equals("0")){
            return "0";
        }else{
            List<Folder> folders = Folder.findWithQuery(Folder.class, "Select * from FOLDER where SERVERID = ?",folderServerId);
            return folders.get(0).getId().toString();
        }
    }

}

