package com.tilak.noteshare;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TermsAndConditionsActivity extends DrawerActivity {
	public RelativeLayout layoutHeder;
	public ImageButton btnheaderMenu, btnsequence, btncalander;
	public TextView textheadertitle, textViewSubHeaderTitle;
	public LinearLayout layoutTitleHeaderview;
	public EditText editTexttermsAndCondition;

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
		layoutHeder = (RelativeLayout) contentView
				.findViewById(R.id.mainHeadermenue);
		btnheaderMenu = (ImageButton) layoutHeder
				.findViewById(R.id.imageButtonHamburg);

		btnsequence = (ImageButton) layoutHeder
				.findViewById(R.id.imageButtonsquence);
		btncalander = (ImageButton) layoutHeder
				.findViewById(R.id.imageButtoncalander);
		btncalander.setVisibility(View.GONE);
		btnsequence.setVisibility(View.GONE);

		// /textheadertitle=(TextView)
		// layoutHeder.findViewById(R.id.textViewheaderTitle);
		// textheadertitle.setText("");

		layoutTitleHeaderview = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderview1);
		textViewSubHeaderTitle = (TextView) layoutTitleHeaderview
				.findViewById(R.id.textViewHeaderTitle1);
		textViewSubHeaderTitle.setText("Terms & Conditions".toUpperCase());
		
		
		editTexttermsAndCondition=(EditText) contentView.findViewById(R.id.editTexttermsAndCondition);
		editTexttermsAndCondition.setText(getTandC());
		editTexttermsAndCondition.setKeyListener(null);
		editTexttermsAndCondition.setEnabled(false);
		editTexttermsAndCondition.setSelected(false);

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
	
	String getTandC()
	{
		
		String str="Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries but also the leap into electronic typesetting remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum. \n\nLorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
	
		return str;
	}
}
