package com.tilak.noteshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tilak.dataAccess.DataManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserProfileActivity extends Activity {

	public Button btnnext, btnUploadprofilepic;
	public LinearLayout layoutusernickname;
	public EditText textnickname;
	public ImageButton userprofilepicture;
	public Bitmap chossedImage;
	public TextView chooseImage;

	private static final int SELECT_PICTURE = 1;
	private static final int REQUEST_CAMERA = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userprofile_activity);

		initlizeUIElement(null);
		getUserProfile();

	}

	void initlizeUIElement(View contentview) {

		userprofilepicture = (ImageButton) findViewById(R.id.userprofilepicture);
		btnnext = (Button) findViewById(R.id.btnprofileNext);
		//btnUploadprofilepic = (Button) findViewById(R.id.btnprofileuploadProfile1);
		chooseImage = (TextView) findViewById(R.id.btnprofileuploadProfile1);
		layoutusernickname = (LinearLayout) findViewById(R.id.usernickname);

		textnickname = (EditText) layoutusernickname
				.findViewById(R.id.editTextlogin);

		textnickname.setHint("Select a User Name");

		addlistners();

	}

	void addlistners() {

		btnnext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent newIntent = new Intent(getApplicationContext(),DrawerActivity.class);
				startActivity(newIntent);
				finish();
			}
		});
		
//		btnUploadprofilepic.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//				// startActivity(new Intent(getApplicationContext(),
//				// RegistrationActivity.class));
//
//			}
//		});

		userprofilepicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getImage();
			}
		});
		chooseImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getImage();
			}
		});

	}

	public void getImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(
				UserProfileActivity.this);
		
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
		if (resultCode == RESULT_OK) {
			// imageButtoncalander.setVisibility(View.VISIBLE);

			if (requestCode == REQUEST_CAMERA) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

				String imgDir = "NoteShare/Images/";
				File destination = new File(
						Environment.getExternalStoragePublicDirectory(imgDir),
						"profile-picture" + ".jpg");

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

				// userprofilepicture.setImageBitmap(thumbnail);

				/*RoundImage roundedImage = new RoundImage(thumbnail);
				userprofilepicture.setImageDrawable(roundedImage);
				DataManager.sharedDataManager().setUserImageBitMap(thumbnail);
				chossedImage = thumbnail;*/

				userprofilepicture.setImageBitmap(getRoundedCornerImage(thumbnail));

			} else if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				String[] projection = { MediaColumns.DATA };
				@SuppressWarnings("deprecation")
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

				// userprofilepicture.setImageBitmap(bm);

				RoundImage roundedImage = new RoundImage(bm);
				userprofilepicture.setImageDrawable(roundedImage);
				DataManager.sharedDataManager().setUserImageBitMap(bm);

				chossedImage = bm;
			}
		}

	}

	public void getUserProfile() {
		File file = new File(Environment.getExternalStoragePublicDirectory("NoteShare/Images") + "/profile-picture.jpg");
		Bitmap bmp = BitmapFactory.decodeFile(String.valueOf(file));
		if(file.exists()) {
			//userprofilepicture.setImageBitmap(bmp);
			userprofilepicture.setImageBitmap(getRoundedCornerImage(bmp));
			//userprofilepicture.setImageResource(R.drawable.circular_view);//setImageBitmap(getRoundedCornerImage(bmp));
			//userprofilepicture.setBackground(Drawable.createFromPath(String.valueOf(file)));
		}

	}

	public static Bitmap getRoundedCornerImage(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 1000;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		//canvas.drawCircle(bitmap.getWidth(), bitmap.getHeight(), roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;

	}
}
