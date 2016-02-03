package com.noteshareapp.noteshare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageChooserActivity extends Activity {

	private static final int SELECT_PICTURE = 1;
	private static final int REQUEST_CAMERA = 2;
	public ImageView ivImage;
	public Button btnselected;
	public Bitmap chossedImage;
	public TextView textViewheaderTitle;
	
	public RelativeLayout chosserlayoutheader;
	public ImageButton imageButtonHamburg,imageButtoncalander;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagechooser_activity);
		
		
		chosserlayoutheader=(RelativeLayout) findViewById(R.id.chosserlayoutheader);
		imageButtoncalander=(ImageButton) chosserlayoutheader.findViewById(R.id.imageButtoncalander);
		imageButtonHamburg=(ImageButton) chosserlayoutheader.findViewById(R.id.imageButtonHamburg);
		imageButtoncalander.setVisibility(View.GONE);
		textViewheaderTitle=(TextView) chosserlayoutheader.findViewById(R.id.textViewheaderTitle);
		textViewheaderTitle.setText("CHOOSE PICTURE");
		
		ivImage = (ImageView) findViewById(R.id.ivImage1);
		btnselected = (Button) findViewById(R.id.btnSelectPhoto1);
		addlistners();
		
	}
	
	void addlistners()
	{
		btnselected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getImage();

			}
		});
		
		imageButtoncalander.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//done click
				finish();
			}
		});
		imageButtonHamburg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//back click
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ImageChooserActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_PICTURE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) 
		{
			imageButtoncalander.setVisibility(View.VISIBLE);

			
			if (requestCode == REQUEST_CAMERA) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

				File destination = new File(
						Environment.getExternalStorageDirectory(),
						System.currentTimeMillis() + ".jpg");

				FileOutputStream fo;
				try {
					destination.createNewFile();
					fo = new FileOutputStream(destination);
					fo.write(bytes.toByteArray());
					fo.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				ivImage.setImageBitmap(thumbnail);
				chossedImage=thumbnail;

			} else if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				String[] projection = { MediaColumns.DATA };
				Cursor cursor = managedQuery(selectedImageUri, projection,
						null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaColumns.DATA);
				cursor.moveToFirst();

				String selectedImagePath = cursor.getString(column_index);

				Bitmap bm;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(selectedImagePath, options);
				final int REQUIRED_SIZE = 200;
				int scale = 1;
				while (options.outWidth / scale / 2 >= REQUIRED_SIZE
						&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
					scale *= 2;
				options.inSampleSize = scale;
				options.inJustDecodeBounds = false;
				bm = BitmapFactory.decodeFile(selectedImagePath, options);

				ivImage.setImageBitmap(bm);
				chossedImage=bm;
			}
		}

	}
}