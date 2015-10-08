package com.tilak.noteshare;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InviteFriendsActivity extends DrawerActivity {
	public RelativeLayout layoutHeder;
	public ImageButton btnheaderMenu, btnsequence, btncalander;
	public TextView textheadertitle, textViewSubHeaderTitle;
	public LinearLayout layoutTitleHeaderview, layoutViaWhatsApp,
			layoutViaFacebookmessanger, layoutViaEmail, layoutViaSMS;;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.invitefriends_activity,
				null, false);
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

		layoutTitleHeaderview = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderview1);
		textViewSubHeaderTitle = (TextView) layoutTitleHeaderview
				.findViewById(R.id.textViewHeaderTitle1);
		textViewSubHeaderTitle.setText("Invites Friends");

		layoutViaWhatsApp = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderviewViaWhatsApp);
		final TextView textViewSlideMenuName = (TextView) layoutViaWhatsApp
				.findViewById(R.id.textViewSlideMenuName);
		textViewSlideMenuName.setText("Via WhatsApp");
		// textViewSlideMenuName.setTextColor(color.black);
		final ImageView imageViewSlidemenu = (ImageView) layoutViaWhatsApp
				.findViewById(R.id.imageViewSlidemenu);
		imageViewSlidemenu.setImageResource(R.drawable.ws_small_icon);;

		layoutViaFacebookmessanger = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderviewViaFacebookmessanger);
		final TextView textViewSlideMenuName1 = (TextView) layoutViaFacebookmessanger
				.findViewById(R.id.textViewSlideMenuName);
		textViewSlideMenuName1.setText("Via Facebook Messanger");
		// textViewSlideMenuName1.setTextColor(color.black);
		final ImageView imageViewSlidemenu1 = (ImageView) layoutViaFacebookmessanger
				.findViewById(R.id.imageViewSlidemenu);
		imageViewSlidemenu1.setImageResource(R.drawable.fb_small_icon);

		layoutViaEmail = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderviewViaEmail);

		final TextView textViewSlideMenuName2 = (TextView) layoutViaEmail
				.findViewById(R.id.textViewSlideMenuName);
		textViewSlideMenuName2.setText("Via Email");
		// textViewSlideMenuName2.setTextColor(color.black);
		final ImageView imageViewSlidemenu2 = (ImageView) layoutViaEmail
				.findViewById(R.id.imageViewSlidemenu);
		imageViewSlidemenu2.setImageResource(R.drawable.email_small_icon);

		layoutViaSMS = (LinearLayout) contentView
				.findViewById(R.id.titleHeaderviewViaSMS);
		final TextView textViewSlideMenuName3 = (TextView) layoutViaSMS
				.findViewById(R.id.textViewSlideMenuName);
		textViewSlideMenuName3.setText("Via SMS");
		// textViewSlideMenuName3.setTextColor(color.black);
		final ImageView imageViewSlidemenu3 = (ImageView) layoutViaSMS
				.findViewById(R.id.imageViewSlidemenu);
		imageViewSlidemenu3.setImageResource(R.drawable.sms_small_icon);

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
