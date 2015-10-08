package com.tilak.adpters;

import java.util.ArrayList;





import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.SideMenuitems;
import com.tilak.noteshare.R;
import com.tilak.noteshare.RoundImage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TextFont_Size_ChooseAdapter extends BaseAdapter {

	public Activity activity;
	public String[] arrData;
	LayoutInflater inflater;

	public ArrayList<SideMenuitems> arrDataMenu;

	public TextFont_Size_ChooseAdapter(Activity context, String[] arrData) {
		this.arrData = arrData;
		this.activity = context;
		

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrData.length;
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
				vi = inflater.inflate(R.layout.text_font_size_choose_row, null);

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

			holder.textViewSlideMenuName.setText("");
			String strFont=arrData[position].toString();
			holder.textViewSlideMenuName.setText(strFont.toUpperCase());
			
			
			
		

		return vi;
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
