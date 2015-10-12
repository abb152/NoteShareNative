package com.tilak.noteshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends DrawerActivity {

	public RelativeLayout layoutHeder;
	public ImageButton btnheaderMenu,btnsequence,btncalander;
	public TextView textheadertitle,textViewSubHeaderTitle;
	public LinearLayout layoutTitleHeaderview;


protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	LayoutInflater inflater = (LayoutInflater) this
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// inflate your activity layout here!
	View contentView = inflater
			.inflate(R.layout.checklist_activity, null, false);
	mDrawerLayout.addView(contentView, 0);
	initlizeUIElement(contentView);
}
void  initlizeUIElement(View contentView)
{
	//mainHeadermenue
	layoutHeder=(RelativeLayout) contentView.findViewById(R.id.mainHeadermenue);
	btnheaderMenu=(ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);
	
	btnsequence=(ImageButton) layoutHeder.findViewById(R.id.imageButtonsquence);
	btncalander=(ImageButton) layoutHeder.findViewById(R.id.imageButtoncalander);
	btncalander.setVisibility(View.GONE);
	btnsequence.setVisibility(View.GONE);
	
	///textheadertitle=(TextView) layoutHeder.findViewById(R.id.textViewheaderTitle);
	//textheadertitle.setText("");
	
	
	layoutTitleHeaderview=(LinearLayout) contentView.findViewById(R.id.titleHeaderview1);
	textViewSubHeaderTitle=(TextView) layoutTitleHeaderview.findViewById(R.id.textViewHeaderTitle1);
	textViewSubHeaderTitle.setText("Settings");
	
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
	
}
	public void setPasscode(View v){
		Intent intent = new Intent(SettingActivity.this, PasscodeActivity.class);
		intent.putExtra("FileId", "");
		intent.putExtra("Check", "0");
		startActivity(intent);
	}
}
