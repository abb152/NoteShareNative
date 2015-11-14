package com.tilak.noteshare;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tilak.adpters.NotificationListAdapter;
import com.tilak.datamodels.SideMenuitems;

import java.util.ArrayList;

public class NotificationCenterActivity extends DrawerActivity {
	public LinearLayout layoutHeder;
	public ImageButton btnheaderMenu,btnsequence,btncalander;
	public TextView textheadertitle,textViewSubHeaderTitle;
	public LinearLayout layoutTitleHeaderview;
	public ListView listviewNotification;
	public NotificationListAdapter adapter;

	public ArrayList<SideMenuitems> arrnotificationItems;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater
				.inflate(R.layout.notificationlist_activity, null, false);
		mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(contentView);
	}
	void  initlizeUIElement(View contentView)
	{
		//mainHeadermenue
		layoutHeder=(LinearLayout) contentView.findViewById(R.id.actionBar);
		btnheaderMenu=(ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);

		/*btnsequence=(ImageButton) layoutHeder.findViewById(R.id.imageButtonsquence);
		btncalander=(ImageButton) layoutHeder.findViewById(R.id.imageButtoncalander);
		btncalander.setVisibility(View.GONE);
		btnsequence.setVisibility(View.GONE);

		///textheadertitle=(TextView) layoutHeder.findViewById(R.id.textViewheaderTitle);
		//textheadertitle.setText("");


		layoutTitleHeaderview=(LinearLayout) contentView.findViewById(R.id.titleHeaderview1);
		textViewSubHeaderTitle=(TextView) layoutTitleHeaderview.findViewById(R.id.textViewHeaderTitle1);
		textViewSubHeaderTitle.setText("Notification Center");*/

		arrnotificationItems=new ArrayList<SideMenuitems>();

		SideMenuitems item1=new SideMenuitems();
		item1.setMenuName("New Brushes added");
		item1.setMenuNameDetail("Wed at 4:15 PM");
		item1.setResourceId(R.drawable.scrbble_newdraw_1);
		arrnotificationItems.add(item1);

		SideMenuitems item2=new SideMenuitems();
		item2.setMenuName("New Fonts added");
		item2.setMenuNameDetail("Sun at 5:53 PM");
		item2.setResourceId(R.drawable.text_icon_2);
		arrnotificationItems.add(item2);

		SideMenuitems item3=new SideMenuitems();
		item3.setMenuName("New Textures added");
		item3.setMenuNameDetail("Tue at 8:53 AM");
		item3.setResourceId(R.drawable.paper_deafult);
		arrnotificationItems.add(item3);
		SideMenuitems item4=new SideMenuitems();
		item4.setMenuName("New Colours added");
		item4.setMenuNameDetail("Fri at 2:15 PM");
		item4.setResourceId(R.drawable.color_header_icon);
		arrnotificationItems.add(item4);



		listviewNotification=(ListView) contentView.findViewById(R.id.listviewNotification);
		adapter=new NotificationListAdapter(NotificationCenterActivity.this, arrnotificationItems);
		listviewNotification.setAdapter(adapter);


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

		listviewNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(NotificationCenterActivity.this, "pos"+arg2, Toast.LENGTH_SHORT).show();
			}
		});

	}
}
