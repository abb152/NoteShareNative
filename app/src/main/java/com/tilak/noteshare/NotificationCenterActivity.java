package com.tilak.noteshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
	private ArrayList<HashMap<String,String>> list;
	//public static String SERVER_URL = "http://104.197.122.116/";
	public static String SERVER_URL = "http://192.168.0.125:1337/";
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
	void  initlizeUIElement(View contentView)
	{
		//mainHeadermenue
		layoutHeder=(LinearLayout) contentView.findViewById(R.id.actionBar);
		btnheaderMenu=(ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);

		listviewNotification=(ListView) contentView.findViewById(R.id.listviewNotification);
		//adapter=new NotificationListAdapter(NotificationCenterActivity.this, arrnotificationItems);
		getNotifications();
		addListners();
	}

	public void test(){
		initlizeUIElement(contentView);
	}

	public void getNotifications(){

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Fetching your Notifications...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.show();

		new AsyncTask<Void, Void, String>(){

			boolean received = false;

			@Override
			protected String doInBackground(Void... params) {

				if (Looper.myLooper() == null) {
					Looper.prepare();
				}

				String sample = "jay,visariya";

				List<String> sampleList = Arrays.asList(sample.split(","));

				Log.e("sample1", sampleList.get(0));
				Log.e("sample2", sampleList.get(1));


				list = new ArrayList<HashMap<String, String>>();
				try {
					String notificationJson = getNotificationsJson().toString();
					Log.e("jay sharejson", notificationJson);

					String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "notification/find", notificationJson);
					Log.e("jay response", response);

					JSONArray jsonArray = new JSONArray(response);

					Log.e("jay json size", String.valueOf(jsonArray.length()));

					//String value = jsonObject.get("value").toString();


					if(jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);

							String noteId = jsonObject.opt("note").toString();
							String noteName = jsonObject.opt("notename").toString();
							String username = jsonObject.opt("username").toString();

							HashMap<String,String> map = new HashMap<String,String>();
							map.put("note", noteId);
							map.put("notename", noteName);
							map.put("username", username);

							list.add(map);

							received= true;
							progressDialog.dismiss();
						}
					} else {
						received = false;

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

				if(progressDialog.isShowing()){
					progressDialog.dismiss();
				}

				if(received){
					if(list.size() >0){
						OurNotificationListAdapter adapter = new OurNotificationListAdapter(NotificationCenterActivity.this, list);
						listviewNotification.setAdapter(adapter);
					}
				}else{

				}
			}
		}.execute(null, null, null);

	}

	public void acceptAndSync(final View v){

		final ImageButton imageButton = (ImageButton) v;
		imageButton.setClickable(false);

		progressDialog = new ProgressDialog(NotificationCenterActivity.this);
		progressDialog.setMessage("Wait while we get your new Notes and Folders...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(true);
		progressDialog.show();

		final String serverNoteId = imageButton.getTag().toString();

		new AsyncTask<Void,Void,String>(){

			boolean received = false;
			@Override
			protected String doInBackground(Void... params) {

				try{

					String json = getAcceptNotificationsJson(serverNoteId).toString();
					String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "notification/noteStatus", json);

					JSONObject jsonObject = new JSONObject(response);

					String value = jsonObject.optString("value");
					Log.e("jay value", value);

					if(value.equals("true")){
						//Toast.makeText(this,"Wait to sync",Toast.LENGTH_LONG).show();

						RegularFunctions.syncNow();

						received = true;

						progressDialog.dismiss();

					}else{
						progressDialog.dismiss();
						received = false;

						//Toast.makeText(NotificationCenterActivity.this,"Oops something went wrong",Toast.LENGTH_LONG).show();
					}

				}catch(JSONException je){

				}catch (IOException io){

				}

				return null;
			}

			@Override
			protected void onPostExecute(String s) {

				if(received){
					//ImageButton ib = (ImageButton) v;
					imageButton.setImageResource(R.drawable.ic_like);
					imageButton.setClickable(false);

					Toast.makeText(NotificationCenterActivity.this, "Note Received", Toast.LENGTH_LONG).show();
				}else {
					imageButton.setClickable(false);
					Toast.makeText(NotificationCenterActivity.this, "Oops something went wrong", Toast.LENGTH_LONG).show();
				}
			}
		}.execute(null,null,null);


	}

	public JSONObject getAcceptNotificationsJson(String serverNoteId) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("user", RegularFunctions.getUserId());
			jsonObject.put("status", "true");
			jsonObject.put("note", serverNoteId);

		} catch (JSONException je) {

		}
		return jsonObject;
	}

	public JSONObject getNotificationsJson(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("user", RegularFunctions.getUserId().trim());
		}catch (JSONException je){

		}
		return jsonObject;
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
