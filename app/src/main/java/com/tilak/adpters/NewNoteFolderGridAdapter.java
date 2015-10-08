package com.tilak.adpters;

import java.util.ArrayList;

import com.tilak.datamodels.SideMenuitems;
import com.tilak.noteshare.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewNoteFolderGridAdapter extends BaseAdapter {

	public Activity activity;
	
	LayoutInflater inflater;

	public ArrayList<SideMenuitems> arrDataMenu;

	public NewNoteFolderGridAdapter(Activity context,
			ArrayList<SideMenuitems> arrDataMenu) {
		
		this.activity = context;
		this.arrDataMenu = arrDataMenu;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.arrDataMenu.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.notefoldergridrow, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.textViewSlideMenuName = (TextView) vi
					.findViewById(R.id.textViewSlideMenuName);
			holder.layoutsepreter = (View) vi.findViewById(R.id.layoutsepreter);
			holder.textViewSlideMenuNameSubTitle=(TextView)vi.findViewById(R.id.textViewSlideMenuNameSubTitle);
			holder.layoutnotefolderAdapter=(LinearLayout)vi.findViewById(R.id.layoutnotefolderAdapter);
			holder.containerLayout=(LinearLayout)vi.findViewById(R.id.containerLayout);
			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		SideMenuitems model = arrDataMenu.get(position);
		holder.textViewSlideMenuName.setText(model.getMenuName());
		holder.textViewSlideMenuNameSubTitle.setText(model.getMenuNameDetail());

		holder.layoutsepreter.setVisibility(View.VISIBLE);
		holder.containerLayout.setBackgroundColor(Color.parseColor(model.getColours()));;//activity.getResources().getColor(model.getColours())
		

		return vi;
	}

	public static class ViewHolder {

		public TextView textViewSlideMenuName;
		public TextView  textViewSlideMenuNameSubTitle;
		public LinearLayout layoutnotefolderAdapter,containerLayout;
		public View layoutsepreter;

	}

	

}
