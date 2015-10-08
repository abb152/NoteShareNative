package com.tilak.noteshare;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TextChooserAdapter extends BaseAdapter {

	public Activity activity;
	public String[] arrData;
	LayoutInflater inflater;

	public ArrayList<String> arrDataMenu;

	public TextChooserAdapter(Activity context, String[] arrData,
			ArrayList<String> arrDataMenu) {
		this.arrData = arrData;
		this.activity = context;
		this.arrDataMenu = arrDataMenu;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.arrDataMenu.size() ;
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
				vi = inflater.inflate(R.layout.slidemenurow1, null);

				/****** View Holder Object to contain tabitem.xml file elements ******/

				holder = new ViewHolder();
				holder.textViewSlideMenuName = (TextView) vi
						.findViewById(R.id.textViewSlideMenuName);
				holder.imageViewSlideMenuImage = (ImageView) vi
						.findViewById(R.id.imageViewSlidemenu);
				holder.layoutsepreter= (View) vi
						.findViewById(R.id.layoutsepreter);

				/************ Set holder with LayoutInflater ************/
				vi.setTag(holder);
			} else
				holder = (ViewHolder) vi.getTag();

			String model = arrDataMenu.get(position );
			holder.textViewSlideMenuName.setText(model);
			
		
			
			return vi;
			
		

		//return vi;
	}

	public static class ViewHolder {

		public TextView textViewSlideMenuName;
		public ImageView imageViewSlideMenuImage;
		public View layoutsepreter;

	}

	public static class ViewHolder1 {

		public TextView textViewusername;
		public ImageView imageViewUserImage;
		public TextView textViewUserbalance;
		

	}

}
