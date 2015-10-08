package com.tilak.noteshare;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class ProfileSettingActivity extends DrawerActivity {

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
		
		
	}
}
