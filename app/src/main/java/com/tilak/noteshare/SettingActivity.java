package com.tilak.noteshare;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tilak.db.Config;
import com.tilak.db.Sync;

public class SettingActivity extends DrawerActivity {

	public LinearLayout layoutHeder;
	public ImageButton btnheaderMenu;
	public LinearLayout lastSyncLayout;
	public TextView tvTerms, tvAbout, tvSyncVia, tvLastSync;
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

		lastSyncLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvLastSync.setText("Please wait.. Syncing..");
				final ProgressDialog progressDialog = new ProgressDialog(SettingActivity.this);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Sync...");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();

				new AsyncTask<Void, Void, String>() {
					@Override
					protected String doInBackground(Void... params) {

						//Looper.loop();
						if (Looper.myLooper() == null) {
							Looper.prepare();
						}
						//Looper.prepare();
						RegularFunctions.syncNow();
						//Looper.myLooper().quit();
						return null;
					}

					@Override
					protected void onPostExecute(String s) {
						String time = RegularFunctions.lastSyncTime();
						tvLastSync.setText(time);
						progressDialog.dismiss();
					}
				}.execute(null, null, null);

				/*RegularFunctions.syncNow();
				String time = RegularFunctions.lastSyncTime();

				tvLastSync.setText(time);

				progressDialog.dismiss();*/
			}
		});

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
}
