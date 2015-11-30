package com.tilak.adpters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tilak.datamodels.SideMenuitems;
import com.tilak.db.Config;
import com.tilak.noteshare.R;

import java.io.File;
import java.util.ArrayList;

public class SlideMenuAdapter extends BaseAdapter {

	public Activity activity;
	public String[] arrData;
	LayoutInflater inflater;

	public ArrayList<SideMenuitems> arrDataMenu;

	public SlideMenuAdapter(Activity context, String[] arrData,
			ArrayList<SideMenuitems> arrDataMenu) {
		this.arrData = arrData;
		this.activity = context;
		this.arrDataMenu = arrDataMenu;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.arrDataMenu.size() + 1;
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

		View vi = convertView=null;
		
		switch (position) {
		case 0:
		{
			ViewHolder1 holder1;

			if (convertView == null) {
				vi = inflater.inflate(R.layout.userprofile, null);
				holder1 = new ViewHolder1();
				holder1.imageViewUserImage = (ImageView) vi
						.findViewById(R.id.imageViewUserProfile);
				//holder1.textViewUserbalance = (TextView) vi.findViewById(R.id.textViewBalance);
				holder1.textViewusername = (TextView) vi
						.findViewById(R.id.textViewUsername);

				vi.setTag(holder1);
			} else {
				holder1 = (ViewHolder1) vi.getTag();
			}

			Config config = Config.findById(Config.class,1l);
			//holder1.textViewusername.setText("Jay");

			String name = "profile.jpg";
			File f = new File(Environment.getExternalStorageDirectory() + "/NoteShare/.NoteShare/" + name);
			Bitmap b = BitmapFactory.decodeFile(String.valueOf(f));
			vi = inflater.inflate(R.layout.userprofile, null);
			holder1 = new ViewHolder1();
			holder1.imageViewUserImage = (ImageView) vi
					.findViewById(R.id.imageViewUserProfile);
			holder1.imageViewUserImage.setImageBitmap(getRoundedCornerImage(getSquareImage(b)));
			holder1.textViewusername = (TextView) vi.findViewById(R.id.textViewUsername);
			holder1.textViewusername.setText(config.firstname/* +" "+config.lastname*/);
			//holder1.textViewUserbalance.setText("");
			
			/*Bitmap bm =DataManager.sharedDataManager().getUserImageBitMap();
			if(bm==null)
			{
				bm=BitmapFactory.decodeResource(activity.getResources(),R.drawable.ic_launcher);
			}*/

			//RoundImage  roundedImage = new RoundImage(bm);
            //holder1.imageViewUserImage.setImageDrawable(roundedImage);
		
            //return vi;
		
		}
			
			break;

		default:
		{


			ViewHolder holder;

			if (convertView == null) {

				/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
				vi = inflater.inflate(R.layout.slidemenurow, null);

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

			SideMenuitems model = arrDataMenu.get(position - 1);
			holder.textViewSlideMenuName.setText(model.getMenuName());
			holder.imageViewSlideMenuImage.setImageResource(model.resourceId);

		
			holder.layoutsepreter.setVisibility(View.GONE);
			if (position==2)
			{
				holder.layoutsepreter.setVisibility(View.VISIBLE);
			}
			
			//return vi;
			
		
		}
			break;
		}

//		if (position!= 0) 
//		{} else {}

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

	public static Bitmap getSquareImage(Bitmap bitmap) {
		Bitmap tempBitmap;
		if (bitmap.getWidth() >= bitmap.getHeight()){
			tempBitmap = Bitmap.createBitmap(
					bitmap,
					bitmap.getWidth()/2 - bitmap.getHeight()/2,
					0,
					bitmap.getHeight(),
					bitmap.getHeight()
			);

		} else{
			tempBitmap = Bitmap.createBitmap(
					bitmap,
					0,
					bitmap.getHeight()/2 - bitmap.getWidth()/2,
					bitmap.getWidth(),
					bitmap.getWidth()
			);
		}
		return tempBitmap;
	}

	public static Bitmap getRoundedCornerImage(Bitmap bitmap){
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 140;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

}
