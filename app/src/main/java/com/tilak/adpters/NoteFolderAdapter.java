package com.tilak.adpters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.SideMenuitems;
import com.tilak.noteshare.MainActivity;
import com.tilak.noteshare.R;

import java.util.ArrayList;

public class NoteFolderAdapter extends BaseAdapter {

	public Activity activity;

	LayoutInflater inflater;

	public boolean theviewtype;

	public ArrayList<SideMenuitems> arrDataMenu;

	public NoteFolderAdapter(Activity context,
			ArrayList<SideMenuitems> arrDataMenu) {

		this.activity = context;
		this.arrDataMenu = arrDataMenu;

		System.out.println("adapter notify");
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
			vi = inflater.inflate(R.layout.notefolderrow, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.textViewSlideMenuName = (TextView) vi
					.findViewById(R.id.textViewSlideMenuName);
			holder.layoutsepreter = (View) vi.findViewById(R.id.layoutsepreter);
			holder.textViewSlideMenuNameSubTitle = (TextView) vi
					.findViewById(R.id.textViewSlideMenuNameSubTitle);
			holder.layoutnotefolderAdapter = (LinearLayout) vi
					.findViewById(R.id.layoutnotefolderAdapter);
			holder.layoutDateAndShare = (RelativeLayout) vi
					.findViewById(R.id.layoutDateAndShare);
			holder.layoutgap = (View) vi.findViewById(R.id.layoutgap);
			holder.layoutOptionse = (LinearLayout) vi
					.findViewById(R.id.layoutOptionse);
			holder.btnclose1=(ImageButton) vi
					.findViewById(R.id.btnclose1);
			holder.btnclose2=(ImageButton) vi
					.findViewById(R.id.btnclose2);
			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		SideMenuitems model = arrDataMenu.get(position);
		holder.textViewSlideMenuName.setText(model.getMenuName());
		this.theviewtype = DataManager.sharedDataManager().isTypeofListView();

		if (theviewtype == true) {
			holder.textViewSlideMenuNameSubTitle.setText(model
					.getMenuNameDetail());
			holder.textViewSlideMenuNameSubTitle.setVisibility(View.GONE);
			System.out.println("list title");
			ViewGroup.LayoutParams params = holder.textViewSlideMenuName
					.getLayoutParams();
			params.height = 100;
			holder.textViewSlideMenuName.setLayoutParams(params);
			holder.layoutDateAndShare.setVisibility(View.GONE);
			holder.layoutgap.setVisibility(View.GONE);

			ViewGroup.LayoutParams params1 = holder.layoutOptionse
					.getLayoutParams();
			params1.height = 100;

		} else {
			holder.textViewSlideMenuNameSubTitle.setVisibility(View.VISIBLE);
			holder.textViewSlideMenuNameSubTitle.setText(model
					.getMenuNameDetail());
			System.out.println("detail");
			ViewGroup.LayoutParams params = holder.textViewSlideMenuName
					.getLayoutParams();
			params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			holder.textViewSlideMenuName.setLayoutParams(params);
			holder.layoutDateAndShare.setVisibility(View.VISIBLE);
			holder.layoutgap.setVisibility(View.VISIBLE);

			ViewGroup.LayoutParams params1 = holder.layoutOptionse
					.getLayoutParams();
			params1.height = 180;

		}

		if (DataManager.sharedDataManager().getSelectedIndex() == position) {
			holder.layoutnotefolderAdapter.setVisibility(View.GONE);
			holder.layoutOptionse.setVisibility(View.VISIBLE);
		} else {
			holder.layoutnotefolderAdapter.setVisibility(View.VISIBLE);
			holder.layoutOptionse.setVisibility(View.GONE);
		}

		holder.layoutsepreter.setVisibility(View.VISIBLE);
		// holder.layoutnotefolderAdapter.setBackgroundColor(Color
		// .parseColor(model.getColours()));

		vi.setBackgroundColor(Color.parseColor("#ffffff"));
		holder.layoutsepreter.setBackgroundColor(Color.parseColor("#eeeeee"));
		if (model.getColours().equalsIgnoreCase("#ffffff")) {

		} else {
			vi.setBackgroundColor(Color.parseColor(model.getColours()));
			holder.layoutsepreter.setBackgroundColor(Color.parseColor(model
					.getColours()));
		}

		
		holder.btnclose1.setTag(position);
		holder.btnclose2.setTag(position);
		holder.btnclose1.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				btnClicks(arg0.getTag());
				
			}
		});
		holder.btnclose2.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btnClicks(arg0.getTag());
			}
		});
		
		
		return vi;
	}

	public static class ViewHolder {

		public TextView textViewSlideMenuName;
		public TextView textViewSlideMenuNameSubTitle;
		public LinearLayout layoutnotefolderAdapter;//
		public View layoutsepreter, layoutgap, layoutOptionse;
		public RelativeLayout layoutDateAndShare;

		public ImageButton btnclose1, btnclose2, btnlock, btntimebomb, btnmove,
				btndelete;

	}
	
	public void btnClicks(Object object)
	{
		((MainActivity)activity).btnCallbacks(object);
		
	}

}
