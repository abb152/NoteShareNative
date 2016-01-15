package com.tilak.noteshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tilak.adpters.OurNotificationListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NotificationCenterActivity extends DrawerActivity {
    public LinearLayout layoutHeder;
    public ImageButton btnheaderMenu;
    public ListView listviewNotification;
    private ArrayList<HashMap<String, String>> list;
    View contentView;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        contentView = inflater.inflate(R.layout.notificationlist_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        initlizeUIElement(contentView);
    }

    void initlizeUIElement(View contentView) {
        //mainHeadermenue
        layoutHeder = (LinearLayout) contentView.findViewById(R.id.actionBar);
        btnheaderMenu = (ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);

        listviewNotification = (ListView) contentView.findViewById(R.id.listviewNotification);
        addListners();

        TextView tvNotificationHead = (TextView) contentView.findViewById(R.id.tvNotificationHead);
        tvNotificationHead.setTypeface(RegularFunctions.getAgendaBoldFont(this));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getNotifications();
            }
        }, 300);
    }

    public void test() {
        initlizeUIElement(contentView);
    }

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public void getNotifications() {
        if (RegularFunctions.checkIsOnlineViaIP()) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Fetching your Notifications...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);

            if (active)
                progressDialog.show();

            new AsyncTask<Void, Void, String>() {
                boolean received = false;

                @Override
                protected String doInBackground(Void... params) {

                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    list = new ArrayList<HashMap<String, String>>();
                    try {
                        String notificationJson = getNotificationsJson().toString();
                        Log.e("jay sharejson", notificationJson);

                        String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "notification/find", notificationJson);
                        Log.e("jay response", response);

                        JSONArray jsonArray = new JSONArray(response);

                        Log.e("jay json size", String.valueOf(jsonArray.length()));

                        //String value = jsonObject.get("value").toString();


                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String noteId = jsonObject.optString("note");
                                String folderId = jsonObject.optString("folder");

                                String type = "";
                                String id = "";
                                String name = "";

                                if (!noteId.isEmpty()) {
                                    type = "note";
                                    name = jsonObject.optString("notename");
                                    id = noteId;
                                } else if (!folderId.isEmpty()) {
                                    type = "folder";
                                    id = folderId;
                                    name = jsonObject.optString("foldername");
                                }

                                String username = jsonObject.optString("username");
                                String profilepic = jsonObject.optString("profilepic");
                                //String name = jsonObject.optString("notename");
                                String userid = jsonObject.optString("userid");

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("type", type);
                                map.put("name", name);
                                map.put("username", username);
                                map.put("profilepic", profilepic);
                                map.put("userid", userid);
                                map.put("id", id);

                                list.add(map);

                                received = true;
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        } else {
                            received = false;
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("jay ", "no notifications");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String s) {

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (received) {
                        if (list.size() > 0) {
                            OurNotificationListAdapter adapter = new OurNotificationListAdapter(NotificationCenterActivity.this, list);
                            listviewNotification.setAdapter(adapter);
                        }
                    } else {

                    }
                }
            }.execute(null, null, null);

        } else {
            Toast.makeText(getApplicationContext(), "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    public void acceptRejectAndSync(final View v) {

        final ImageButton imageButton = (ImageButton) v;
        imageButton.setClickable(false);

        progressDialog = new ProgressDialog(NotificationCenterActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String tag = imageButton.getTag().toString();
        List<String> tagList = Arrays.asList(tag.split(","));

        final String type = tagList.get(0);
        final String elementid = tagList.get(1);                    // note/folder id
        final String userid = tagList.get(2);                        // user id who has shared the note
        final String valueTF = tagList.get(3);                        //true or false

        new AsyncTask<Void, Void, String>() {
            boolean received = false;

            @Override
            protected String doInBackground(Void... params) {

                try {

                    String json = getNotificationsJson(elementid, valueTF, type, userid).toString();

                    Log.e("jay noti json", json);

                    String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "notification/noteStatus", json);

                    Log.e("jay response", response);

                    JSONObject jsonObject = new JSONObject(response);

                    String value = jsonObject.optString("value");
                    Log.e("jay value", value);

                    if (value.equals("true")) {
                        //Toast.makeText(this,"Wait to sync",Toast.LENGTH_LONG).show();

                        //if (valueTF.equals("true"))
                            //RegularFunctions.syncNow();

                        received = true;

                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        received = false;
                        //Toast.makeText(NotificationCenterActivity.this,"Oops something went wrong",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException je) {

                } catch (IOException io) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {

                if (received) {
                    View v = (View) imageButton.getParent();
                    v.setVisibility(View.GONE);

                    String message = "";

                    if (type.equals("note"))
                        message = "Note ";
                    else
                        message = "Folder ";

                    if (valueTF.equals("true"))
                        message = message + "Accepted";
                    else
                        message = message + "Rejected";

                    Toast.makeText(NotificationCenterActivity.this, message, Toast.LENGTH_SHORT).show();
                    //onRestart();

                    if (valueTF.equals("true"))
                        syncNowOrLater();

                } else {
                    //imageButton.setClickable(false);
                    Toast.makeText(NotificationCenterActivity.this, "Oops something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);


    }

    public JSONObject getNotificationsJson(String elementid, String status, String type, String senderid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", RegularFunctions.getUserId());
            jsonObject.put(type, elementid);
            jsonObject.put("userid", senderid);
            jsonObject.put("status", status);
        } catch (JSONException je) {

        }
        return jsonObject;
    }

    public JSONObject getNotificationsJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user", RegularFunctions.getUserId().trim());
        } catch (JSONException je) {

        }
        return jsonObject;
    }

    public void syncNowOrLater(){
        int type = RegularFunctions.checkInternetConnectivity(NotificationCenterActivity.this);

        if (type == 0) {
            Toast.makeText(NotificationCenterActivity.this, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
        } else if (type == 1) {
            //RegularFunctions.syncNow();
            startSync();
        } else if (type == 2) {
            Log.e("jay sync", "inside 2");
            Toast.makeText(getApplicationContext(), "Sync your new Notes and Folders later when you are on wifi!", Toast.LENGTH_SHORT).show();
        }
    }


    public void startSync(){
        final ProgressDialog progressDialog = new ProgressDialog(NotificationCenterActivity.this);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Please wait while we sync your new Notes and Folders...");

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                RegularFunctions.syncNow();

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(NotificationCenterActivity.this, "New Notes and Folders received", Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }



    @Override
    public void addListners() {
        // TODO Auto-generated method stub
        super.addListners();
        btnheaderMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openSlideMenu();
            }
        });

        listviewNotification.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                //Toast.makeText(NotificationCenterActivity.this, "pos"+arg2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplication(), MainActivity.class);
        //i.putExtra("FolderId","-1");
        startActivity(i);
        finish();
    }
}
