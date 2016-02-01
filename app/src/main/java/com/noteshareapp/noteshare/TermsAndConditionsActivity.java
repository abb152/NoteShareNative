package com.noteshareapp.noteshare;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TermsAndConditionsActivity extends DrawerActivity {
	public LinearLayout layoutHeder;
	public ImageButton btnheaderMenu, btnsequence, btncalander;
	public LinearLayout layoutTitleHeaderview;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.termsandconditions_activity, null,
				false);
		mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(contentView);
	}

	void initlizeUIElement(View contentView) {
		// mainHeadermenue
		layoutHeder = (LinearLayout) contentView
				.findViewById(R.id.actionBar);
		btnheaderMenu = (ImageButton) layoutHeder
				.findViewById(R.id.imageButtonHamburg);

		TextView tvTCHead = (TextView) contentView.findViewById(R.id.tvTCHead);
		tvTCHead.setTypeface(RegularFunctions.getAgendaBoldFont(this));

		/*btnsequence = (ImageButton) layoutHeder
				.findViewById(R.id.imageButtonsquence);
		btncalander = (ImageButton) layoutHeder
				.findViewById(R.id.imageButtoncalander);
		btncalander.setVisibility(View.GONE);
		btnsequence.setVisibility(View.GONE);

		// /textheadertitle=(TextView)
		// layoutHeder.findViewById(R.id.textViewheaderTitle);
		// textheadertitle.setText("");

		layoutTitleHeaderview = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderview1);*/

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
}
