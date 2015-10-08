package com.tilak.noteshare;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AudioChooserActivity extends Activity {

	public Button play, stop, record;
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	public TextView textViewRecordTitle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audiochooser_activity);

		play = (Button) findViewById(R.id.buttonPlay);
		stop = (Button) findViewById(R.id.buttonStop);
		record = (Button) findViewById(R.id.buttonRecord);
		textViewRecordTitle=(TextView) findViewById(R.id.textViewRecordTitle);
		textViewRecordTitle.setText("");

		stop.setEnabled(false);
		play.setEnabled(false);
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHH_mm");
		 String date = sdf.format(new Date(System.currentTimeMillis()));

		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/recording_"
				+ date+".3gpp";
		;

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
		
		addListners();

	}

	void addListners() {

		record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					textViewRecordTitle.setText("Audio Recording Started");
					
					myAudioRecorder.prepare();
					myAudioRecorder.start();
				}

				catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				record.setEnabled(false);
				stop.setEnabled(true);

				//Toast.makeText(getApplicationContext(), "Recording started",
						//Toast.LENGTH_LONG).show();
			}
		});
		stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				textViewRecordTitle.setText("Audio Recorded");
				myAudioRecorder.stop();
				myAudioRecorder.release();
				myAudioRecorder = null;

				stop.setEnabled(false);
				play.setEnabled(true);

//				Toast.makeText(getApplicationContext(),
//						"Audio recorded successfully", Toast.LENGTH_LONG)
//						.show();
			}
		});
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) throws IllegalArgumentException,
					SecurityException, IllegalStateException {
				MediaPlayer m = new MediaPlayer();
				textViewRecordTitle.setText("Playing Recorded Audio" );
				try {
					
					System.out.println("file playing path:"+outputFile);
					m.setDataSource(outputFile);
				}

				catch (IOException e) {
					e.printStackTrace();
				}

				try 
				{
					m.prepare();
				}

				catch (IOException e) {
					
					e.printStackTrace();
				}

				m.start();
//				Toast.makeText(getApplicationContext(), "Playing audio",
//						Toast.LENGTH_LONG).show();
			}
		});

	}

}
