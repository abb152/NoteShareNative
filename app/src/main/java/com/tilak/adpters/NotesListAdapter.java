package com.tilak.adpters;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tilak.datamodels.NOTETYPE;
import com.tilak.datamodels.NoteListDataModel;
import com.tilak.noteshare.R;

import java.io.IOException;
import java.util.ArrayList;

interface AudiorecordingClick {
	public void didClickPlaybutton(NoteListDataModel notelistData, int index);

	public void didClickStopbutton(NoteListDataModel notelistData, int index);
}

public class NotesListAdapter extends BaseAdapter {

	public Activity activity;
	LayoutInflater inflater;
	public ArrayList<NoteListDataModel> arrNoteListData;

	public NotesListAdapter(Activity context,
			ArrayList<NoteListDataModel> arrNoteListData) {

		this.activity = context;
		this.arrNoteListData = arrNoteListData;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.arrNoteListData.size();
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

		final ViewHolder holder;

		if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.notelistrow, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.textViewSlideMenuName = (TextView) vi
					.findViewById(R.id.textViewSlideMenuName);
			holder.imageViewSlideMenuImage = (ImageView) vi
					.findViewById(R.id.imageViewSlidemenu);
			// holder.viewScibble = (DrawingView)
			// vi.findViewById(R.id.viewScibble);

			holder.LayoutAudioRecording = (RelativeLayout) vi
					.findViewById(R.id.LayoutAudioRecording);

			holder.buttonPlay = (ImageButton) holder.LayoutAudioRecording
					.findViewById(R.id.buttonPlay);
			holder.buttonStop = (ImageButton) holder.LayoutAudioRecording
					.findViewById(R.id.buttonStop);
			holder.buttonRecord = (ImageButton) holder.LayoutAudioRecording
					.findViewById(R.id.buttonRecord);
			holder.buttonPause = (ImageButton) holder.LayoutAudioRecording
					.findViewById(R.id.buttonPause);

			holder.buttonRecordPause = (ImageButton) holder.LayoutAudioRecording
					.findViewById(R.id.buttonRecordPause);
			holder.progressRecord1 = (SeekBar) holder.LayoutAudioRecording
					.findViewById(R.id.progressRecord1);
			holder.textViewDuration = (TextView) holder.LayoutAudioRecording
					.findViewById(R.id.textViewDuration);
			holder.mediaPlayer = new MediaPlayer();

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();
		holder.buttonPause.setTag(position);
		holder.buttonPlay.setTag(position);

		final NoteListDataModel model = arrNoteListData.get(position);
		holder.textViewSlideMenuName.setVisibility(View.GONE);
		holder.imageViewSlideMenuImage.setVisibility(View.GONE);
		// holder.viewScibble.setVisibility(View.GONE);

		holder.LayoutAudioRecording.setVisibility(View.GONE);
		if (model.noteType == NOTETYPE.AUDIOMODE) {

			if (model.strAudioFilePath.length() > 0) {

				holder.LayoutAudioRecording.setVisibility(View.VISIBLE);
				System.out.println("the out put file path:"
						+ model.strAudioFilePath);

				holder.buttonRecord.setVisibility(View.GONE);
				holder.buttonStop.setVisibility(View.GONE);
				holder.buttonPlay.setVisibility(View.VISIBLE);
				holder.buttonRecordPause.setVisibility(View.GONE);
				holder.buttonPause.setVisibility(View.VISIBLE);
				holder.progressRecord1.setProgress(5);
				holder.progressRecord1.setVisibility(View.VISIBLE);

				String audioPath = Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Audio/";

				holder.buttonPlay.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						System.out.println("List button click play");

						try {

							System.out.println("file playing path:"
									+ model.strAudioFilePath);
							holder.mediaPlayer
									.setDataSource(model.getStrAudioFilePath());
						}

						catch (IOException e) {
							e.printStackTrace();
						}

						try {
							holder.mediaPlayer.prepare();

						}

						catch (IOException e) {

							e.printStackTrace();
						}

						holder.mediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer arg0) {

										holder.buttonPlay.setEnabled(true);
										holder.buttonPause.setEnabled(false);
										// progressRecord1.setProgress(0);
									}
								});

						holder.progressRecord1.setMax(holder.mediaPlayer
								.getDuration() / 1000);
						holder.mediaPlayer.start();

						final Handler mHandler = new Handler();
						// Make sure you update Seekbar on UI thread

						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (holder.mediaPlayer != null) {
									int mCurrentPosition = holder.mediaPlayer
											.getCurrentPosition() / 1000;
									String currentduration = getDurationBreakdown(holder.mediaPlayer
											.getCurrentPosition());
									String currentduration1 = getDurationBreakdown(holder.mediaPlayer
											.getDuration());

									if (mCurrentPosition <= holder.mediaPlayer
											.getDuration() / 1000) {
										System.out.println("CurrentDuration:"
												+ currentduration);
										holder.progressRecord1
												.setProgress(mCurrentPosition);

										holder.textViewDuration
												.setText(currentduration + "/"
														+ currentduration1);

									}

								}
								mHandler.postDelayed(this, 1000);
							}
						});

						Toast.makeText(activity, "Recording playing",
								Toast.LENGTH_SHORT).show();

					}
				});

				holder.buttonPause.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						System.out.println("List button click pause");
						holder.mediaPlayer.pause();

					}
				});

			}

		} else if (model.noteType == NOTETYPE.IMAGEMODE) {
			holder.imageViewSlideMenuImage.setVisibility(View.VISIBLE);
			holder.imageViewSlideMenuImage.setImageBitmap(model.getBitmap());

		} else if (model.noteType == NOTETYPE.SCRIBBLEMODE) {
			// holder.viewScibble.setVisibility(View.VISIBLE);
		} else if (model.noteType == NOTETYPE.TEXTMODE) {
			holder.textViewSlideMenuName.setVisibility(View.VISIBLE);
			if (model.stringtext != null && model.stringtext.length() > 0) {
				holder.textViewSlideMenuName.setText(model.stringtext);
			}

		}

		return vi;

	}

	public static class ViewHolder {

		public TextView textViewSlideMenuName, textViewDuration;
		public ImageView imageViewSlideMenuImage;
		public RelativeLayout LayoutAudioRecording;
		public ImageButton buttonPlay;
		public ImageButton buttonStop;
		public ImageButton buttonRecord, buttonPause, buttonRecordPause;
		public SeekBar progressRecord1;
		public MediaPlayer mediaPlayer;
		// public DrawingView viewScibble;

	}

	public static class ViewHolder1 {

		public TextView textViewusername;
		public ImageView imageViewUserImage;
		public TextView textViewUserbalance;

	}

	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException(
					"Duration must be greater than zero!");
		}

		// long days = TimeUnit.MILLISECONDS.toDays(millis);
		// millis -= TimeUnit.DAYS.toMillis(days);
		// long hours = TimeUnit.MILLISECONDS.toHours(millis);
		// millis -= TimeUnit.HOURS.toMillis(hours);
		// long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		// millis -= TimeUnit.MINUTES.toMillis(minutes);
		//
		// long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		// StringBuilder sb = new StringBuilder(64);

		StringBuffer sb = new StringBuffer();

		int hours = (int) (millis / (1000 * 60 * 60));
		int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
		int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

		sb.append(String.format("%02d", hours)).append(":")
				.append(String.format("%02d", minutes)).append(":")
				.append(String.format("%02d", seconds));

		System.out.println("time is:" + sb.toString());

		return sb.toString();

	}

}
