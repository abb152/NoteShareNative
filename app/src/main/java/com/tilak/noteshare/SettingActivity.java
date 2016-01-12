package com.tilak.noteshare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tilak.db.Config;
import com.tilak.db.Folder;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;
import com.tilak.db.Sync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SettingActivity extends DrawerActivity {

	public LinearLayout layoutHeder;
	public ImageButton btnheaderMenu;
	public LinearLayout lastSyncLayout;
	public TextView tvTerms, tvAbout, tvSyncVia, tvLastSync, tvLogout;
	public RadioGroup syncGroup;
	public RadioButton syncRadio;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater
				.inflate(R.layout.activity_setting, null, false);
		mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(contentView);
	}
	void  initlizeUIElement(View contentView)
	{
		//mainHeadermenue
		layoutHeder=(LinearLayout) contentView.findViewById(R.id.actionBar);
		lastSyncLayout=(LinearLayout) contentView.findViewById(R.id.lastSyncLayout);
		btnheaderMenu=(ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);
		tvTerms = (TextView) findViewById(R.id.tvTerms);
		tvAbout = (TextView) findViewById(R.id.tvAbout);
		tvSyncVia = (TextView) findViewById(R.id.tvSyncVia);
		tvLogout = (TextView) findViewById(R.id.tvLogout);

		tvLastSync = (TextView) findViewById(R.id.tvLastSync);

		String time = RegularFunctions.lastSyncTime();

		tvLastSync.setText(time);

		addListners();
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
		tvAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, AboutNoteShareActivity.class));
			}
		});
		tvTerms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, TermsAndConditionsActivity.class));
			}
		});

		tvSyncVia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				syncDialog(SettingActivity.this);
			}
		});

		tvLogout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertWith("LOGOUT", "Are you sure you want to Log Out?", SettingActivity.this);
			}
		});

		lastSyncLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int type = RegularFunctions.checkInternetConnectivity(SettingActivity.this);

				if (type == 0) {
					Toast.makeText(SettingActivity.this, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
				} else if (type == 1) {
					startSync(false);
				} else if (type == 2) {
					Log.e("jay sync", "inside 2");
					Toast.makeText(getApplicationContext(), "Only on wifi and now on mobile", Toast.LENGTH_SHORT).show();
				}
			}
		});

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

	public void startSync(final boolean logout){
		tvLastSync.setText("Please wait.. Syncing..");
		final ProgressDialog progressDialog = new ProgressDialog(SettingActivity.this);
		progressDialog.setCancelable(false);

		if(logout)
			progressDialog.setMessage("Please wait while we sync your Notes and Folders before logging out...");
		else
			progressDialog.setMessage("Please wait while we sync your Notes and Folders...");

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

				if(logout){
					flushDatabase();
					finish();
					startActivity(new Intent(SettingActivity.this, LoginActivity.class));
				}
				else {
					String time = RegularFunctions.lastSyncTime();
					tvLastSync.setText(time);
				}
				if(progressDialog.isShowing())
					progressDialog.dismiss();
			}
		}.execute(null, null, null);
	}


	public void setPasscode(View v){
		Config con = Config.findById(Config.class, 1L);
		if (con.getPasscode() == 0) {
			Intent intent = new Intent(SettingActivity.this, PasscodeActivity.class);
			//intent.putExtra("FileId", "");
			intent.putExtra("Check", "0");
			startActivity(intent);
		} else {
			Intent intent = new Intent(SettingActivity.this, PasscodeActivity.class);
			intent.putExtra("Check", "4");
			startActivity(intent);
		}
	}

	public void syncDialog(Context context) {

		final Dialog dialog = new Dialog(context);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View contentView = inflater.inflate(R.layout.sync_dialog, null, false);

		TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("Sync Via");
		textViewTitleAlert.setTextColor(Color.WHITE);

		Sync sync = Sync.findById(Sync.class, 1l);
		int syncType = sync.getSyncType();

		switch (syncType){
			case 1:
				syncRadio = (RadioButton) contentView.findViewById(R.id.sync1);
				syncRadio.setChecked(true);
				break;
			case 2:
				syncRadio = (RadioButton) contentView.findViewById(R.id.sync2);
				syncRadio.setChecked(true);
				break;
			case 3:
				syncRadio = (RadioButton) contentView.findViewById(R.id.sync3);
				syncRadio.setChecked(true);
				break;
		}


		syncGroup = (RadioGroup) contentView.findViewById(R.id.syncGroup);

		syncGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				syncRadio = (RadioButton) contentView.findViewById(checkedId);
				int syncType = Integer.parseInt(syncRadio.getTag().toString());

				Sync syncSet = Sync.findById(Sync.class,1l);
				syncSet.setSyncType(syncType);
				syncSet.save();
			}
		});


		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(contentView);
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent i = new Intent(getApplication(), MainActivity.class);
		//i.putExtra("FolderId","-1");
		startActivity(i);
		finish();
	}


	void showAlertWith(String title, String message, Context context) {

		final Dialog dialog = new Dialog(context);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.alert_view, null, false);

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText(title);
		textViewTitleAlert.setTextColor(Color.WHITE);
		TextView textViewTitleAlertMessage = (TextView) contentView
				.findViewById(R.id.textViewTitleAlertMessage);
		textViewTitleAlertMessage.setText(message);

		Button buttonAlertCancel = (Button) contentView
				.findViewById(R.id.buttonAlertCancel);
		Button buttonAlertOk = (Button) contentView
				.findViewById(R.id.buttonAlertOk);
		buttonAlertCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		buttonAlertOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(RegularFunctions.checkIsOnlineViaIP()){
					dialog.dismiss();
					Log.e("jay sync status", String.valueOf(RegularFunctions.checkNeedForSync()));
					if(RegularFunctions.checkNeedForSync()){
						startSync(true);
					}else{
						finish();
						startActivity(new Intent(SettingActivity.this, LoginActivity.class));
					}
				}else{
					Toast.makeText(getApplicationContext(),"Please check you Internet Connection!",Toast.LENGTH_SHORT).show();
				}
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(contentView);
		dialog.show();

	}

	public void flushDatabase(){

		//remove gcmId of this device from server
		try {
			String logoutUrl = RegularFunctions.SERVER_URL+"user/logout";
			String logoutJson = getLogoutJson().toString();
			Log.e("jay logoutJSON", logoutJson);
			String removeGcm = RegularFunctions.post(logoutUrl,logoutJson);
			Log.e("jay removeGcm", removeGcm);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//flush sync values
		Sync s = Sync.findById(Sync.class, 1l);
		s.setFolderLocalToServer(0l);
		s.setFolderServerToLocal(0l);
		s.setNoteLocalToServer(0l);
		s.setNoteServerToLocal(0l);
		s.setLastSyncTime(0l);
		s.setSyncType(1);
		s.save();

		//flush config values
		Config c = Config.findById(Config.class, 1l);
		c.setFirstname("");
		c.setLastname("");
		c.setEmail("");
		c.setPassword("");
		c.setFbid("");
		c.setGoogleid("");
		c.setPasscode(0);
		c.setProfilepic("");
		c.setUsername("");
		c.setDeviceid("");
		c.setServerid("");
		c.setAppversion(0);
		c.save();

		//delete all folders
		Folder.deleteAll(Folder.class);

		//delete all notes
		Note.deleteAll(Note.class);

		//delete all noteElements
		NoteElement.deleteAll(NoteElement.class);

	}

	public JSONObject getLogoutJson(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("user", RegularFunctions.getUserId().trim());
			jsonObject.put("deviceid", RegularFunctions.getDeviceId().trim());
		}catch (JSONException je){

		}
		return jsonObject;
	}

}
