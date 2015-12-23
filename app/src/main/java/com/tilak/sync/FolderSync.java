package com.tilak.sync;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tilak.db.Config;
import com.tilak.db.Folder;
import com.tilak.db.Sync;
import com.tilak.noteshare.RegularFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jay on 01-12-2015.
 */

enum FUNCTION{
    CREATE,
    DELETE,
    EDIT
}
public class FolderSync {

    //public static String SERVER_URL = "http://104.197.122.116/";
    //public static String SERVER_URL = "http://192.168.0.125:1337/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private int type=0; //1 create //2 delete //0 edit
    FUNCTION funcType;

    public void localToServer(){
        Sync sync = RegularFunctions.getSyncTime();
        Long time = sync.getFolderLocalToServer() - 10000;

        List<Folder> folders = getFolderList(time);
        if(folders.size() > 0) {

            for (int i = 0; i < folders.size(); i++) {

                if(folders.get(i).getServerid().equals("0"))
                    funcType = FUNCTION.CREATE;
                else if(folders.get(i).getCreationtime().equals("0"))
                    funcType = FUNCTION.DELETE;
                else
                    funcType = FUNCTION.EDIT;

                Log.e("jay funcType", funcType.name());

                try {
                    String json = localToServerFolderJson(folders.get(i).getName(), folders.get(i).creationtime, folders.get(i).getModifytime(), folders.get(i).getOrderNumber(), getUserId(), funcType, folders.get(i).getServerid()).toString();
                    String response = post(RegularFunctions.SERVER_URL + "folder/localtoserver", json);

                    JSONObject jsonObject = new JSONObject(response);

                    switch (funcType){
                        case CREATE:
                            String folderServerId = jsonObject.get("id").toString();
                            folders.get(i).setServerid(folderServerId);
                            folders.get(i).save();

                            Date createDate = new Date();
                            folders.get(i).setModifytime(dateToString(createDate));
                            folders.get(i).setmTime(createDate.getTime());

                            //folder localToServer time change and also lastSyncTime
                            RegularFunctions.changeFolderLocalToServerTime();

                            Log.e("jay create", "");
                            break;

                        case DELETE:
                            folders.get(i).delete();

                            //folder localToServer time change and also lastSyncTime
                            RegularFunctions.changeFolderLocalToServerTime();

                            Log.e("jay delete","");
                            break;

                        case EDIT:
                            Date editDate = new Date();
                            folders.get(i).setModifytime(dateToString(editDate));
                            folders.get(i).setmTime(editDate.getTime());

                            //folder localToServer time change and also lastSyncTime

                            RegularFunctions.changeFolderLocalToServerTime();

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
        //String foldermodifytime = "1970-01-01 00:00:00";

        Sync sync = RegularFunctions.getSyncTime();
        //Long time = sync.getFolderLocalToServer();

        String foldermodifytime = RegularFunctions.longToString(sync.getFolderLocalToServer() - 10000);

        try {
            String json = serverToLocalJson(getUserId(), foldermodifytime).toString();
            String response = post(RegularFunctions.SERVER_URL + "folder/servertolocal", json);

            JSONArray jsonArray = new JSONArray(response);
            //Log.e("jay length", String.valueOf(jsonArray.length()));
            //Log.e("jay jsonArray", jsonArray.toString());

            if(jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    String creationtime = jsonArray.getJSONObject(i).getString("creationtime");
                    String id = jsonArray.getJSONObject(i).getString("_id");
                    String name = jsonArray.getJSONObject(i).getString("name") ;
                    String modifytime = jsonArray.getJSONObject(i).getString("modifytime");
                    String order = jsonArray.getJSONObject(i).getString("order");

                    //funcType = null;

                    if(creationtime.equals(""))
                        funcType = FUNCTION.DELETE;
                    else if(!folderAlreadyExists(id))
                        funcType = FUNCTION.CREATE;
                    else
                        funcType = FUNCTION.EDIT;

                    Log.e("jay funcType", funcType.name());

                    switch (funcType){
                        case CREATE:
                            Folder createFolder = new Folder(name,Integer.parseInt(order), id, dateServerToLocal(creationtime), dateServerToLocal(modifytime) ,stringToDate(dateServerToLocal(creationtime)), stringToDate(dateServerToLocal(modifytime)));
                            createFolder.save();

                            RegularFunctions.changeFolderServerToLocalTime();

                            Log.e("jay created ***", String.valueOf(createFolder.getId()));
                            break;

                        case DELETE:
                            List<Folder> deleteFolder = Folder.findWithQuery(Folder.class, "Select * from FOLDER where serverid LIKE ?", id);
                            if(deleteFolder.size() == 0)
                                Log.e("jay already not there","***");
                            else
                                deleteFolder.get(0).delete();

                            RegularFunctions.changeFolderServerToLocalTime();

                            Log.e("jay deleted","***");
                            break;
                        case EDIT:
                            List<Folder> editFolder = Folder.findWithQuery(Folder.class, "Select * from FOLDER where serverid LIKE ?", id);
                            editFolder.get(0).setName(name);
                            editFolder.get(0).setOrderNumber(Integer.parseInt(order));
                            editFolder.get(0).setCreationtime(dateServerToLocal(creationtime));
                            editFolder.get(0).setModifytime(dateServerToLocal(modifytime));
                            editFolder.get(0).setcTime(stringToDate(dateServerToLocal(creationtime)));
                            editFolder.get(0).setmTime(stringToDate(dateServerToLocal(modifytime)));
                            editFolder.get(0).save();

                            RegularFunctions.changeFolderServerToLocalTime();

                            Log.e("jay edited", editFolder.get(0).getName());
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

    public boolean folderAlreadyExists(String id){
        List<Folder> folders = Folder.findWithQuery(Folder.class, "Select * from FOLDER where serverid LIKE ?", id);
        Log.e("jay size",String.valueOf(folders.size()));
        boolean exists;
        if( folders.size() > 0 ){
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

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(serverToLocalDateFormat(date));
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

    public List<Folder> getFolderList(Long time){
        //Long time = 1448954670000L;
        List<Folder> folders = Folder.findWithQuery(Folder.class, "Select * from FOLDER where MTIME > " + time + " ORDER BY MTIME ASC");
        return folders;
    }

    public JSONObject localToServerFolderJson(String name, String creationtime , String modifytime, int order, String user, FUNCTION function, String id) throws JSONException {

        JSONObject folder = new JSONObject();

        folder.put("name", name);
        folder.put("creationtime", creationtime);
        folder.put("modifytime", modifytime);
        folder.put("order", String.valueOf(order));
        folder.put("user", user);

        if(function != FUNCTION.CREATE){
            folder.put("_id", id);
        }
        Log.e("jay folder json", folder.toString());
        return folder;
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
