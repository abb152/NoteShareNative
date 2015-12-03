package com.tilak.sync;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tilak.db.Config;
import com.tilak.db.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static String SERVER_URL = "http://104.197.122.116/";
    //http://104.197.122.116/folder/localtoserver
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private int type=0; //1 create //2 delete //0 edit
    NOTESYNCFUNCTION funcType;

    public void localToServer(){
        Long time = 1448954670000L;
        List<Note> notes = getNoteList(time);
        if(notes.size() > 0) {

            for (int i = 0; i < notes.size(); i++) {

                if(notes.get(i).getServerid().equals("0"))
                    funcType = NOTESYNCFUNCTION.CREATE;
                else if(notes.get(i).getCreationtime().equals("0"))
                    funcType = NOTESYNCFUNCTION.DELETE;
                else
                    funcType = NOTESYNCFUNCTION.EDIT;

                Log.e("jay funcType", funcType.name());

                try {
                    String json = localToServerNoteJson(notes.get(i).getTitle(), notes.get(i).getTags(), notes.get(i).getColor(), notes.get(i).getFolder(), String.valueOf(notes.get(i).getRemindertime()), notes.get(i).getTimebomb(), notes.get(i).getBackground(), notes.get(i).getCreationtime(), notes.get(i).getModifytime(), String.valueOf(notes.get(i).getIslocked()), notes.get(i).getCtime(), notes.get(i).getMtime(), getUserId(), funcType, notes.get(i).getServerid()).toString();
                    String response = post(SERVER_URL + "note/localtoserver", json);

                    JSONObject jsonObject = new JSONObject(response);

                    switch (funcType){
                        case CREATE:
                            String folderServerId = jsonObject.get("id").toString();
                            notes.get(i).setServerid(folderServerId);
                            notes.get(i).save();

                            Date createDate = new Date();
                            notes.get(i).setModifytime(dateToString(createDate));
                            notes.get(i).setMtime(createDate.getTime());
                            Log.e("jay create", "");
                            break;

                        case DELETE:
                            notes.get(i).delete();
                            Log.e("jay delete","");
                            break;

                        case EDIT:
                            Date editDate = new Date();
                            notes.get(i).setModifytime(dateToString(editDate));
                            notes.get(i).setMtime(editDate.getTime());
                            Log.e("jay edit", "");
                            break;

                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (IOException io){
                    io.printStackTrace();
                }
            }

        }
    }

    public void serverToLocal(){
        String notemodifytime = "1970-01-01 00:00:00";

        try {
            String json = serverToLocalJson(getUserId(), notemodifytime).toString();
            String response = post(SERVER_URL + "note/servertolocal", json);

            JSONArray jsonArray = new JSONArray(response);
            //Log.e("jay length", String.valueOf(jsonArray.length()));
            //Log.e("jay jsonArray", jsonArray.toString());

            if(jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    String title = jsonArray.getJSONObject(i).getString("title");
                    String color = jsonArray.getJSONObject(i).getString("color");
                    String folder = jsonArray.getJSONObject(i).getString("folder");
                    String background = jsonArray.getJSONObject(i).getString("background");
                    String tags = jsonArray.getJSONObject(i).getString("tags");
                    String creationtime = jsonArray.getJSONObject(i).getString("creationtime");
                    String modifytime = jsonArray.getJSONObject(i).getString("modifytime");
                    String islocked = jsonArray.getJSONObject(i).getString("islocked");
                    String remindertime = jsonArray.getJSONObject(i).getString("remindertime");
                    String timebomb = jsonArray.getJSONObject(i).getString("timebomb");
                    String id = jsonArray.getJSONObject(i).getString("_id");

                    //funcType = null;

                    if(creationtime.equals(""))
                        funcType = NOTESYNCFUNCTION.DELETE;
                    else if(!noteAlreadyExists(id))
                        funcType = NOTESYNCFUNCTION.CREATE;
                    else
                        funcType = NOTESYNCFUNCTION.EDIT;

                    Log.e("jay funcType", funcType.name());

                    switch (funcType){
                        case CREATE:
                            if(timebomb.equals("0"))
                                timebomb = "";

                            Note createNote = new Note(title,tags,color,folder,Long.parseLong(remindertime),timebomb,background, dateServerToLocal(creationtime), dateServerToLocal(modifytime) ,id,Integer.parseInt(islocked),stringToDate(dateServerToLocal(creationtime)), stringToDate(dateServerToLocal(modifytime)));
                            createNote.save();
                            Log.e("jay created ***", String.valueOf(createNote.getId()));
                            break;

                        case DELETE:
                            List<Note> deleteNote = Note.findWithQuery(Note.class, "Select * from NOTE where serverid LIKE ?", id);
                            if(deleteNote.size() == 0)
                                Log.e("jay already not there","***");
                            else
                                deleteNote.get(0).delete();

                            Log.e("jay deleted","***");
                            break;
                        case EDIT:
                            List<Note> editNote = Note.findWithQuery(Note.class, "Select * from NOTE where serverid LIKE ?", id);
                            editNote.get(0).setTitle(title);
                            editNote.get(0).setTags(tags);
                            editNote.get(0).setColor(color);
                            editNote.get(0).setFolder(folder);
                            editNote.get(0).setRemindertime(Long.parseLong(remindertime));

                            if(timebomb.equals("0"))
                                editNote.get(0).setTimebomb("");
                            else
                                editNote.get(0).setTimebomb(timebomb);

                            editNote.get(0).setBackground(background);
                            editNote.get(0).setCreationtime(dateServerToLocal(creationtime));
                            editNote.get(0).setModifytime(dateServerToLocal(modifytime));
                            editNote.get(0).setServerid(id);
                            editNote.get(0).setIslocked(Integer.parseInt(islocked));
                            editNote.get(0).setCtime(stringToDate(dateServerToLocal(creationtime)));
                            editNote.get(0).setMtime(stringToDate(dateServerToLocal(modifytime)));
                            editNote.get(0).save();

                            Log.e("jay edited", editNote.get(0).getTitle());
                            break;
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("jay exception e", String.valueOf(e));
        } catch (IOException io){
            Log.e("jay exception io", String.valueOf(io));
            io.printStackTrace();
        }catch (ParseException pe){
            Log.e("jay exception pe", String.valueOf(pe));
            pe.printStackTrace();
        }
    }

    public boolean noteAlreadyExists(String id){
        List<Note> notes = Note.findWithQuery(Note.class, "Select * from NOTE where serverid LIKE ?", id);
        Log.e("jay size",String.valueOf(notes.size()));
        boolean exists;
        if( notes.size() > 0 ){
            exists = true;
        }else{
            exists = false;
        }
        return exists;
    }

    public String dateToString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }


    public long stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
    }//

    public String dateServerToLocal(String date) throws ParseException{

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(serverToLocalDateFormat(date)).toString();
    }//

    public static long serverToLocalDateFormat(String date) throws ParseException {

        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date).getTime();
    }//

    public String getUserId(){
        return Config.findById(Config.class, 1L).getServerid();
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        //Log.e("jay response",String.valueOf(response.isSuccessful()));
        //Log.e("jay response body",response.body().string());
        return response.body().string();
    }

    public List<Note> getNoteList(Long time){
        //Long time = 1448954670000L;
        List<Note> notes = Note.findWithQuery(Note.class, "Select * from NOTE where MTIME > " + time + " ORDER BY MTIME ASC");
        return notes;
    }

    public JSONObject localToServerNoteJson(String title, String tags, String color, String folder, String remindertime, String timebomb, String background, String creationtime, String modifytime, String islocked, long ctime, long mtime ,String user, NOTESYNCFUNCTION function, String id) throws JSONException {


        JSONObject note = new JSONObject();

        note.put("user", user);
        note.put("title", title);
        note.put("color", color);
        note.put("folder", folder);
        note.put("background", background);
        note.put("tags", tags);

        note.put("creationtime", creationtime);
        note.put("modifytime", modifytime);
        note.put("islocked", String.valueOf(islocked));
        note.put("remindertime", remindertime);

        if(timebomb.equals(""))
            note.put("timebomb", "0");
        else
            note.put("timebomb", timebomb);

        if(function != NOTESYNCFUNCTION.CREATE){
            note.put("_id", id);
        }
        Log.e("jay folder json", note.toString());
        return note;
    }

    public JSONObject serverToLocalJson(String user, String modifytime) throws JSONException{
        //Log.e("jay inside json","");
        JSONObject folder = new JSONObject();

        folder.put("user", user);
        folder.put("modifytime", modifytime);
        //Log.e("jay json", folder.toString());
        return folder;
    }
}

