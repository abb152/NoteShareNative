package com.tilak.noteshare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tilak.db.Config;

import java.util.List;

public class SettingActivity extends DrawerActivity {

	public LinearLayout layoutHeder;
	public ImageButton btnheaderMenu,btnsequence,btncalander;
	public TextView textheadertitle,textViewSubHeaderTitle;
	public LinearLayout layoutTitleHeaderview;
	public TextView tvAbout, tvInvite, tvLikeFacebook, tvFeedback, tvTerms;

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
	tvAbout = (TextView) findViewById(R.id.tvAbout);
	tvInvite = (TextView) findViewById(R.id.tvInvite);
	tvLikeFacebook = (TextView) findViewById(R.id.tvLikeFacebook);
	tvFeedback = (TextView) findViewById(R.id.tvFeedback);
	tvTerms = (TextView) findViewById(R.id.tvTerms);
	
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

	List<Config> config = Config.listAll(Config.class);
	if(config.size() == 0) {
		Config c = new Config("Noteshare", "", "", "", "", "", 0, "", "username", "1", "1");
		c.save();
	}
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

		tvInvite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("android.resource://com.tilak.noteshare/drawable/ic_launcher");
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/*");
				share.putExtra(Intent.EXTRA_STREAM, uri);
				share.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_friends_text));
				startActivity(Intent.createChooser(share, "Invite friends"));
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

		tvTerms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SettingActivity.this, TermsAndConditionsActivity.class));
			}
		});

	}
	public void setPasscode(View v){
		Config con = Config.findById(Config.class, 1L);
		if (con.getPasscode() == 0) {
			Intent intent = new Intent(SettingActivity.this, PasscodeActivity.class);
			intent.putExtra("FileId", "");
			intent.putExtra("Check", "0");
			startActivity(intent);
		} else {
			Intent intent = new Intent(SettingActivity.this, PasscodeActivity.class);
			intent.putExtra("Check", "4");
			startActivity(intent);
		}
	}
}
