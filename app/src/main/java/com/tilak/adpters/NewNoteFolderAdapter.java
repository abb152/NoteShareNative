package com.tilak.adpters;

import java.util.ArrayList;

import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.SideMenuitems;
import com.tilak.noteshare.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewNoteFolderAdapter extends BaseAdapter {

	public Activity activity;

	LayoutInflater inflater;

	public boolean theviewtype;

	public ArrayList<SideMenuitems> arrDataMenu;
	public ArrayList<SideMenuitems> mDisplayedValues;

	public NewNoteFolderAdapter(Activity context,
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
		} else {
			holder.textViewSlideMenuNameSubTitle.setVisibility(View.VISIBLE);
			holder.textViewSlideMenuNameSubTitle.setText(model
					.getMenuNameDetail());
			System.out.println("detail");
		}

		holder.layoutsepreter.setVisibility(View.VISIBLE);
		vi.setBackgroundColor(Color
				.parseColor(model.getColours()));
		
		if (model.getColours().equalsIgnoreCase("#ffffff"))
		{
			
		}else
		{
			holder.layoutsepreter.setBackgroundColor(Color
					.parseColor(model.getColours()));
			
		}
		
		

		return vi;
	}

	public static class ViewHolder {

		public TextView textViewSlideMenuName;
		public TextView textViewSlideMenuNameSubTitle;
		public LinearLayout layoutnotefolderAdapter;
		public View layoutsepreter;

	}
	
//	public Filter getFilter()
//	{
//	    Filter filter = new Filter() {
//
//	        @SuppressWarnings("unchecked")
//	        @Override
//	        protected void publishResults(CharSequence constraint,FilterResults results) {
//
//	            mDisplayedValues = (ArrayList<SideMenuitems>) results.values; // has the filtered values
//	            notifyDataSetChanged();  // notifies the data with new filtered values
//	        }
//
//	        protected FilterResults performFiltering(CharSequence constraint) {
//	            FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
//	            ArrayList<SideMenuitems> FilteredArrList = new ArrayList<SideMenuitems>();
//
//	            if (arrDataMenu == null) {
//	            	arrDataMenu = new ArrayList<SideMenuitems>(mDisplayedValues); // saves the original data in mOriginalValues
//	            }
//
//	            /********
//	             * 
//	             *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
//	             *  else does the Filtering and returns FilteredArrList(Filtered)  
//	             *
//	             ********/
//	            if (constraint == null || constraint.length() == 0) {
//
//	                // set the Original result to return  
//	                results.count = arrDataMenu.size();
//	                results.values = arrDataMenu;
//	            } else {
//	                constraint = constraint.toString().toLowerCase();
//	                for (int i = 0; i < arrDataMenu.size(); i++)
//	                {
//	                    String data = arrDataMenu.get(i).getMenuName();
//	                    if (data.toLowerCase().startsWith(constraint.toString()))
//	                    {
//	                        FilteredArrList.add(arrDataMenu.get(i));
//	                    }
//	                }
//	                // set the Filtered result to return
//	                results.count = FilteredArrList.size();
//	                results.values = FilteredArrList;
//	            }
//	            return results;
//	        }
//
//			@Override
//			public boolean onLoadClass(Class clazz) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//	    };
//	    return filter;
//
//		
//	}

}
