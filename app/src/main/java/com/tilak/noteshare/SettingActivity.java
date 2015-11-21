package com.tilak.noteshare;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tilak.db.Config;

public class SettingActivity extends DrawerActivity {

	public LinearLayout layoutHeder;
	public ImageButton btnheaderMenu,btnsequence,btncalander;
	public TextView textheadertitle,textViewSubHeaderTitle;
	public LinearLayout layoutTitleHeaderview;
	public TextView tvLikeFacebook, tvFeedback, tvRateUs, tvSyncVia;

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
		btnheaderMenu=(ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);
		tvLikeFacebook = (TextView) findViewById(R.id.tvLikeFacebook);
		tvFeedback = (TextView) findViewById(R.id.tvFeedback);
		tvRateUs = (TextView) findViewById(R.id.tvRateUs);
		tvSyncVia = (TextView) findViewById(R.id.tvSyncVia);

		/*btnsequence=(ImageButton) layoutHeder.findViewById(R.id.imageButtonsquence);
		btncalander=(ImageButton) layoutHeder.findViewById(R.id.imageButtoncalander);
		btncalander.setVisibility(View.GONE);
		btnsequence.setVisibility(View.GONE);*/

		///textheadertitle=(TextView) layoutHeder.findViewById(R.id.textViewheaderTitle);
		//textheadertitle.setText("");


		/*layoutTitleHeaderview=(LinearLayout) contentView.findViewById(R.id.titleHeaderview1);
		textViewSubHeaderTitle=(TextView) layoutTitleHeaderview.findViewById(R.id.textViewHeaderTitle1);
		textViewSubHeaderTitle.setText("Settings");*/

		/*Button b = (Button) findViewById(R.id.syncButton);
		b.setText(Html.fromHtml("Auto Sync<br><span style=\"color: #cccccc; font-size: 14px;\">Last Sync:<span>"));*/

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

		tvLikeFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/wohlig/")));
			}
		});

		tvFeedback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, SendFeedbackActivity.class));
			}
		});

		tvRateUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("market://details?id=" + "com.wohlig.stakes");
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				// To count with Play market backstack, After pressing back button,
				// to taken back to our application, we need to add following flags to intent.
				goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
				try {
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + "com.wohlig.stakes")));
				}
			}
		});

		tvSyncVia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				syncDialog(SettingActivity.this);
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
		View contentView = inflater.inflate(R.layout.sync_dialog, null, false);

		TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("Sync Via");
		textViewTitleAlert.setTextColor(Color.WHITE);

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
