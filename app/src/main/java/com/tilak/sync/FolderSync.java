package com.tilak.sync;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tilak.db.Config;
import com.tilak.db.Folder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    public static String SERVER_URL = "http://104.197.122.116/";
    //http://104.197.122.116/folder/localtoserver
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();
    private int type=0; //1 create //2 delete //0 edit
    FUNCTION funcType;

    public void localToServer(){
        List<Folder> folders = getFolderList();
        if(folders.size() > 0) {

            Config config = Config.findById(Config.class,1L);
            String userId = config.getServerid();
            for (int i = 0; i < folders.size(); i++) {
                Log.e("jay folder name", folders.get(i).getName());
                Log.e("jay folder mtime", String.valueOf(folders.get(i).getmTime()));
                Log.e("jay folder serverid", folders.get(i).getServerid());
                //folders.get(i).setServerid("");
                //String response;

                if(folders.get(i).getServerid().equals("0"))
                    funcType = FUNCTION.CREATE;
                else if(folders.get(i).getCreationtime().equals("0"))
                    funcType = FUNCTION.DELETE;
                else
                    funcType = FUNCTION.EDIT;

                Log.e("jay funcType", funcType.name());

                try {
                    String json = folderJson(folders.get(i).getName(), folders.get(i).creationtime, folders.get(i).getModifytime(), folders.get(i).getOrderNumber(), userId, funcType, folders.get(i).getServerid());
                    String response = post(SERVER_URL + "folder/localtoserver", json);

                    JSONObject jsonObject = new JSONObject(response);

                    switch (funcType){
                        case CREATE:
                            String folderServerId = jsonObject.get("id").toString();
                            folders.get(i).setServerid(folderServerId);
                            folders.get(i).save();

                            Date createDate = new Date();
                            folders.get(i).setModifytime(dateToString(createDate));
                            folders.get(i).setmTime(createDate.getTime());
                            Log.e("jay create", "");
                            break;

                        case DELETE:
                            folders.get(i).delete();
                            Log.e("jay delete","");
                            break;

                        case EDIT:
                            Date editDate = new Date();
                            folders.get(i).setModifytime(dateToString(editDate));
                            folders.get(i).setmTime(editDate.getTime());
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

    public String dateToString(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        Log.e("jay response true", response.body().toString());
        return response.body().string();
    }

    public List<Folder> getFolderList(){
        Long time = 1448954670000L;
        List<Folder> folders = Folder.findWithQuery(Folder.class, "Select * from FOLDER where MTIME > "+ time +" ORDER BY MTIME ASC");
        return folders;
    }

    public String folderJson(String name, String creationtime , String modifytime, int order, String user, FUNCTION function, String id) throws JSONException {

        JSONObject folder = new JSONObject();

        folder.put("name", name);
        folder.put("creationtime", creationtime);
        folder.put("modifytime", modifytime);
        folder.put("order", order);
        folder.put("user", user);

        if(function != FUNCTION.CREATE){
            folder.put("_id", id);
        }
        Log.e("jay folder json", folder.toString());
        return folder.toString();
    }
}
