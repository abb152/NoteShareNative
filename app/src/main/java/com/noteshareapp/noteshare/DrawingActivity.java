package com.noteshareapp.noteshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class DrawingActivity extends Activity implements OnClickListener {

	private float smallBrush, mediumBrush, largeBrush;
	// private ImageButton currPaint, drawBtn;
	DrawingView drawView;
	private boolean erase = false;
	private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawing_activity);
		initlizeViewa();
	}

	void initlizeViewa() {

		drawView = (DrawingView) findViewById(R.id.drawing);

		LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currPaint = (ImageButton) paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(
				R.drawable.paint_pressed));

		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);
		drawBtn = (ImageButton) findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);
		drawView.setBrushSize(mediumBrush);

		eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
		eraseBtn.setOnClickListener(this);
		newBtn = (ImageButton) findViewById(R.id.new_btn);
		newBtn.setOnClickListener(this);

		saveBtn = (ImageButton) findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);

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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.draw_btn) {
			// draw button clicked
			drawView.setErase(false);
			drawView.setBrushSize(drawView.getLastBrushSize());

			final Dialog brushDialog = new Dialog(DrawingActivity.this);
			// brushDialog.setTitle("Brush size:");
			brushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			brushDialog.setCancelable(false);
			brushDialog.setContentView(R.layout.brush_chooser);

			TextView textViewSizesHeader = (TextView) brushDialog
					.findViewById(R.id.textViewSizesHeader);
			textViewSizesHeader.setText("BRUSH SIZES");

			/*ImageButton smallBtn = (ImageButton) brushDialog
					.findViewById(R.id.small_brush);
			smallBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setBrushSize(smallBrush);
					drawView.setLastBrushSize(smallBrush);
					brushDialog.dismiss();
				}
			});*/

			/*ImageButton mediumBtn = (ImageButton) brushDialog
					.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setBrushSize(mediumBrush);
					drawView.setLastBrushSize(mediumBrush);
					brushDialog.dismiss();
				}
			});*/

			/*ImageButton largeBtn = (ImageButton) brushDialog
					.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setBrushSize(largeBrush);
					drawView.setLastBrushSize(largeBrush);
					brushDialog.dismiss();
				}
			});*/

			brushDialog.show();

		} else if (view.getId() == R.id.erase_btn) {
			// switch to erase - choose size

			final Dialog brushDialog1 = new Dialog(DrawingActivity.this);

			brushDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
			brushDialog1.setCancelable(true);
			brushDialog1.setContentView(R.layout.brush_chooser);
			TextView textViewSizesHeader = (TextView) brushDialog1
					.findViewById(R.id.textViewSizesHeader);

			/*textViewSizesHeader.setText("ERASER SIZES");
			ImageButton smallBtn = (ImageButton) brushDialog1
					.findViewById(R.id.small_brush);

			smallBtn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(smallBrush);
					brushDialog1.dismiss();
				}
			});

			ImageButton mediumBtn = (ImageButton) brushDialog1
					.findViewById(R.id.medium_brush);
			mediumBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(mediumBrush);
					brushDialog1.dismiss();
				}
			});
			ImageButton largeBtn = (ImageButton) brushDialog1
					.findViewById(R.id.large_brush);
			largeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					drawView.setErase(true);
					drawView.setBrushSize(largeBrush);
					brushDialog1.dismiss();
				}
			});*/
			brushDialog1.show();

		} else if (view.getId() == R.id.new_btn) {

			final Dialog dialog = new Dialog(DrawingActivity.this);

			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View contentView = inflater.inflate(R.layout.alert_view, null,
					false);

			TextView textViewTitleAlert = (TextView) contentView
					.findViewById(R.id.textViewTitleAlert);
			textViewTitleAlert.setText("NEW DRAWING");
			textViewTitleAlert.setTextColor(Color.WHITE);
			TextView textViewTitleAlertMessage = (TextView) contentView
					.findViewById(R.id.textViewTitleAlertMessage);
			textViewTitleAlertMessage
					.setText("Start new drawing (you will lose the current drawing)?");

			Button buttonAlertCancel = (Button) contentView
					.findViewById(R.id.buttonAlertCancel);
			Button buttonAlertOk = (Button) contentView
					.findViewById(R.id.buttonAlertOk);
			buttonAlertCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					dialog.dismiss();

				}
			});
			buttonAlertOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					drawView.startNew();
					dialog.dismiss();
				}
			});

			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setContentView(contentView);
			dialog.show();

		} else if (view.getId() == R.id.save_btn) {

			final Dialog dialog = new Dialog(DrawingActivity.this);

			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View contentView = inflater.inflate(R.layout.alert_view, null,
					false);

			TextView textViewTitleAlert = (TextView) contentView
					.findViewById(R.id.textViewTitleAlert);
			textViewTitleAlert.setText("SAVE DRAWING");
			textViewTitleAlert.setTextColor(Color.WHITE);
			TextView textViewTitleAlertMessage = (TextView) contentView
					.findViewById(R.id.textViewTitleAlertMessage);
			textViewTitleAlertMessage
					.setText("Save drawing to device Gallery?");

			Button buttonAlertCancel = (Button) contentView
					.findViewById(R.id.buttonAlertCancel);
			Button buttonAlertOk = (Button) contentView
					.findViewById(R.id.buttonAlertOk);

			buttonAlertCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					dialog.dismiss();

				}
			});
			buttonAlertOk.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					drawView.setDrawingCacheEnabled(true);
					String imgSaved = MediaStore.Images.Media.insertImage(
							getContentResolver(), drawView.getDrawingCache(),
							UUID.randomUUID().toString() + ".png", "drawing");
					System.out.println("the string uri:" + imgSaved);

					if (imgSaved != null) {
						Toast savedToast = Toast
								.makeText(getApplicationContext(),
										"Drawing saved to Gallery!",
										Toast.LENGTH_SHORT);
						// savedToast.show();
						drawView.destroyDrawingCache();
					} else {
						Toast unsavedToast = Toast.makeText(
								getApplicationContext(),
								"Oops! Image could not be saved.",
								Toast.LENGTH_SHORT);
						// unsavedToast.show();
					}

					dialog.dismiss();
				}
			});

			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setContentView(contentView);
			dialog.show();

			/*
			 * AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
			 * saveDialog.setTitle("Save drawing");
			 * saveDialog.setMessage("Save drawing to device Gallery?");
			 * saveDialog.setPositiveButton("Yes", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) { // save drawing
			 * 
			 * drawView.setDrawingCacheEnabled(true); String imgSaved =
			 * MediaStore.Images.Media .insertImage(getContentResolver(),
			 * drawView .getDrawingCache(), UUID .randomUUID().toString() +
			 * ".png", "drawing"); System.out.println("the string uri:" +
			 * imgSaved);
			 * 
			 * // Bitmap bitmap = //
			 * MediaStore.Images.Media.getBitmap(this.getContentResolver(), //
			 * imageUri);
			 * 
			 * if (imgSaved != null) { Toast savedToast = Toast.makeText(
			 * getApplicationContext(), "Drawing saved to Gallery!",
			 * Toast.LENGTH_SHORT); savedToast.show();
			 * drawView.destroyDrawingCache(); } else { Toast unsavedToast =
			 * Toast.makeText( getApplicationContext(),
			 * "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
			 * unsavedToast.show(); } } });
			 * saveDialog.setNegativeButton("Cancel", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) { dialog.cancel(); }
			 * }); saveDialog.show();
			 */
		}

	}

	void showAlertWith(String message, Context context) {

		final Dialog dialog = new Dialog(context);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View contentView = inflater.inflate(R.layout.alert_view, null, false);

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("ALERT");
		textViewTitleAlert.setTextColor(Color.WHITE);
		TextView textViewTitleAlertMessage = (TextView) contentView
				.findViewById(R.id.textViewTitleAlertMessage);
		textViewTitleAlertMessage.setText(message);

		Button buttonAlertCancel = (Button) contentView
				.findViewById(R.id.buttonAlertCancel);
		Button buttonAlertOk = (Button) contentView
				.findViewById(R.id.buttonAlertOk);
		buttonAlertCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();

			}
		});
		buttonAlertOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(contentView);
		dialog.show();

	}

	@SuppressWarnings("deprecation")
	public void paintClicked(View view) {
		// use chosen color
		if (view != currPaint) {
			// update color
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();
			//drawView.setColor(color);

			imgView.setImageDrawable(getResources().getDrawable(
					R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(
					R.drawable.paint));
			currPaint = (ImageButton) view;
		}

	}
}
