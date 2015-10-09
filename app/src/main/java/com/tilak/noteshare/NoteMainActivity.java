package com.tilak.noteshare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.tilak.adpters.NotesListAdapter;
import com.tilak.adpters.TextFont_Size_ChooseAdapter;
import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.NOTETYPE;
import com.tilak.datamodels.NoteListDataModel;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
//import android.support.annotation.Keep;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class NoteMainActivity extends DrawerActivity implements OnClickListener {

	public ImageButton imageButtonHamburg, imageButtoncalander,
			imageButtonsquence;
	private float smallBrush, mediumBrush, largeBrush;
	public ImageButton imageButtonTextMode, imageButtonImageMode,
			imageButtonPaintMode, imageButtonAudioMode, imageButtonShareMode,
			imageButtonMoreMode;
	public SpannableString spanUpdted, spanold;
	MediaPlayer mediaPlayer;// = new MediaPlayer();
	public int typefacae = Typeface.NORMAL;

	public TextView textViewheaderTitle,progressRecordtext;
	public RelativeLayout layoutHeader;

	public int currentAudioIndex = 0;

	EditText edittextEditer, txtViewer;
	Button btnAddText;
	ImageButton buttonPlay;
	ImageButton buttonStop;
	ImageButton buttonRecord, buttonPause, buttonRecordPause;
	ProgressBar progressRecord;
	SeekBar progressRecord1;
	RelativeLayout LayoutAudioRecording;
	private AudioRecorder mAudioRecorder;

	private String mActiveRecordFileName;

	public LinearLayout layOutDrawingView, textNoteControls,
			layout_note_more_Info, layout_audio_notechooser;

	public TextView textViewAdd, textViewDuration;

	final Context context = this;
	public ArrayList<NoteListDataModel> arrNoteListData;
	public ListView listviewNotes;
	public NotesListAdapter adapter;
	private static final int SELECT_PICTURE = 1;
	private static final int REQUEST_CAMERA = 2;
	public DrawingView drawView;
	private ImageButton currPaint;
	public Dialog dialogColor;

	boolean isErase;
	public Dialog brushDialog1;

	boolean isUnderLine = false, isBold = false, isItalic = false;
	public int currentFontSize;
	public String currentFontTypeface;
	public int currentFontColor;
	boolean isRecordingAudio = false;
	private MediaRecorder myAudioRecorder;
	View contentView;
	private String outputFile = null;

	// /Drawing Controls

	public LinearLayout drawingControls;
	public RelativeLayout LayoutTextWritingView;

	public ImageButton imageButtondrawback, imageButtondrawnew,
			imageButtondrawdraw, imageButtondrawcolors,
			imageButtondrawbrushsize, imageButtondrawerase,
			imageButtondrawMore;

	public ImageButton textButtondrawback, textButtondrawnew,
			textButtondrawdraw, textButtondrawcolors, textButtondrawbrushsize,
			textButtondrawerase, textButtondrawMore;

	public ImageButton audioButtondrawback, audioButtondrawnew;

	public boolean isMoreShown = false;
	public boolean isTextmodeSelected = false;

	public TextFont_Size_ChooseAdapter TextFont_sizeAdapter;
	public String[] fonts_sizeName, fonts_Name_Display, arrStrings;
	public String[] fontSizes;
	ImageView background_bg;

	// 8b241b selected bg

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		contentView = inflater
				.inflate(R.layout.note_activity_main, null, false);
		mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(contentView);

	}

	void initlizeUIElement(View contentview) {

		/* Default Initlization */
		currentFontSize = 8;
		currentFontTypeface = NoteShareFonts.arial;
		currentFontColor = Color.BLACK;

		background_bg = (ImageView) contentview
				.findViewById(R.id.background_bg);
		layoutHeader = (RelativeLayout) contentview
				.findViewById(R.id.mainHeadermenue);
		imageButtoncalander = (ImageButton) layoutHeader

		.findViewById(R.id.imageButtoncalander);
		textViewheaderTitle = (TextView) layoutHeader
				.findViewById(R.id.textViewheaderTitle);
		imageButtonHamburg = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtonHamburg);
		imageButtonsquence = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtonsquence);

		imageButtonsquence.setImageResource(R.drawable.color_header_icon);
		imageButtonHamburg.setImageResource(R.drawable.back_icon_1);
		imageButtoncalander.setImageResource(R.drawable.done_icon);

		// textViewAdd = (im) findViewById(R.id.textViewAdd);

		// audio controls
		audioRecording(contentView);
		initlizesAudioNoteControls(contentview);

		// MoreInfo View

		initlizesMoreInfoView(contentview);

		// scribble controls
		initlizesScibbleNoteControles(contentview);

		// text note controls

		initlizesTextNoteControls(contentview);

		// Main controls

		imageButtonAudioMode = (ImageButton) contentview
				.findViewById(R.id.imageButtonAudioMode);
		imageButtonImageMode = (ImageButton) contentview
				.findViewById(R.id.imageButtonImageMode);
		imageButtonPaintMode = (ImageButton) contentview
				.findViewById(R.id.imageButtonPaintMode);
		imageButtonShareMode = (ImageButton) contentview
				.findViewById(R.id.imageButtonShareMode);
		imageButtonTextMode = (ImageButton) contentview
				.findViewById(R.id.imageButtonTextMode);
		imageButtonMoreMode = (ImageButton) contentview
				.findViewById(R.id.imageButtonMoreMode);
		textViewheaderTitle.setText("NOTE");

		LayoutTextWritingView = (RelativeLayout) contentview
				.findViewById(R.id.LayoutTextWritingView);
		LayoutTextWritingView.setVisibility(View.GONE);
		edittextEditer = (EditText) contentview
				.findViewById(R.id.edittextEditer);
		btnAddText = (Button) contentview.findViewById(R.id.btnAddText);
		;
		txtViewer = (EditText) contentview.findViewById(R.id.txtViewer);
		;
			///Text change Listner added
		edittextEditer.addTextChangedListener(watch);
		
		
		
		btnAddText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				updatedText(isUnderLine, isBold, isItalic, currentFontSize,
						currentFontColor, edittextEditer.getText().toString());
				edittextEditer.setText("");

			}
		});

		arrNoteListData = new ArrayList<NoteListDataModel>();

		adapter = new NotesListAdapter(NoteMainActivity.this, arrNoteListData);
		listviewNotes = (ListView) contentview.findViewById(R.id.listviewNotes);
		listviewNotes.setAdapter(adapter);

		if (DataManager.sharedDataManager().getArrNoteListData() != null) {
			if (DataManager.sharedDataManager().getArrNoteListData().size() > 0) {
				arrNoteListData.addAll(DataManager.sharedDataManager()
						.getArrNoteListData());
				adapter.notifyDataSetChanged();
				listviewNotes
						.smoothScrollToPosition(arrNoteListData.size() - 1);

			}
		}

		/** Layout Audio Recording **/

		addlistners();
		addScribbleControlListners();
		addTextNoteControlsListners();
		addAudioNoteListners();

		updateHeaderControls(R.id.imageButtonHamburg);
		imageButtonsquence.setVisibility(View.VISIBLE);

		fonts_sizeName = getResources().getStringArray(R.array.Font_Size_px);
		fontSizes = getResources().getStringArray(R.array.Font_Size);
		fonts_Name_Display = getResources().getStringArray(
				R.array.Font_Name_Display);

	}

	// public AudioRecorder createRecorder(String targetFileName) {
	// mAudioRecorder = AudioRecorder.build(this, targetFileName);
	// return mAudioRecorder;
	// }
	//
	// public AudioRecorder getRecorder() {
	// return mAudioRecorder;
	// }

	void initlizesAudioNoteControls(View contentview) {
		layout_audio_notechooser = (LinearLayout) contentview
				.findViewById(R.id.audioControls);
		layout_audio_notechooser.setVisibility(View.GONE);
		audioButtondrawback = (ImageButton) layout_audio_notechooser
				.findViewById(R.id.imageButtondrawback);
		audioButtondrawnew = (ImageButton) layout_audio_notechooser
				.findViewById(R.id.imageButtondrawnew);
		updateAudioNoteUI(R.id.imageButtondrawback);
		audioButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				updateButtonUI(-1);

			}
		});

	}

	void updateAudioNoteUI(int elementId) {

		audioButtondrawback.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		audioButtondrawnew.setBackgroundColor(getResources().getColor(
				R.color.header_bg));

		switch (elementId) {
		case R.id.imageButtondrawback:
			audioButtondrawback.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtondrawnew:
			audioButtondrawnew.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;

		default:
			break;
		}

	}

	void addAudioNoteListners() {

		audioButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				updateAudioNoteUI(arg0.getId());
				layout_audio_notechooser.setVisibility(View.GONE);
			}
		});

		audioButtondrawnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				;
				LayoutAudioRecording.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.GONE);
				buttonRecordPause.setVisibility(View.GONE);
				buttonPause.setVisibility(View.GONE);

				buttonRecord.setVisibility(View.VISIBLE);
				buttonStop.setVisibility(View.VISIBLE);
				progressRecord1.setVisibility(View.GONE);
				textViewDuration.setVisibility(View.GONE);
				buttonRecord.setEnabled(true);
				buttonStop.setEnabled(true);

				initlizeAudiorecoder();

				updateAudioNoteUI(arg0.getId());
			}
		});
	}

	void initlizeAudiorecoder() {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHH_mm_ss");
		String date = sdf.format(new Date(System.currentTimeMillis()));

		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/recording_" + date + ".3gpp";
		;

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	void audioRecording(View contentview) {

		LayoutAudioRecording = (RelativeLayout) contentview
				.findViewById(R.id.LayoutAudioRecording);
		buttonPlay = (ImageButton) LayoutAudioRecording
				.findViewById(R.id.buttonPlay);
		buttonStop = (ImageButton) LayoutAudioRecording
				.findViewById(R.id.buttonStop);
		buttonRecord = (ImageButton) LayoutAudioRecording
				.findViewById(R.id.buttonRecord);
		buttonPause = (ImageButton) LayoutAudioRecording
				.findViewById(R.id.buttonPause);

		buttonRecordPause = (ImageButton) LayoutAudioRecording
				.findViewById(R.id.buttonRecordPause);

		progressRecord1 = (SeekBar) LayoutAudioRecording
				.findViewById(R.id.progressRecord1);

		textViewDuration = (TextView) LayoutAudioRecording
				.findViewById(R.id.textViewDuration);
		
		
		progressRecordtext=(TextView) LayoutAudioRecording
				.findViewById(R.id.progressRecordtext);

		textViewDuration.setVisibility(View.GONE);
		LayoutAudioRecording.setVisibility(View.GONE);
		progressRecord1.setVisibility(View.GONE);
		progressRecordtext.setVisibility(View.GONE);

		buttonRecordPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				buttonRecord.setVisibility(View.VISIBLE);
				buttonRecordPause.setVisibility(View.GONE);
				myAudioRecorder.stop();
				progressRecordtext.setText("");

			}
		});

		buttonPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				buttonPause.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.VISIBLE);

				buttonPlay.setEnabled(true);
				buttonPause.setEnabled(false);
				progressRecordtext.setVisibility(View.GONE);

				buttonRecord.setVisibility(View.GONE);
				buttonStop.setVisibility(View.GONE);

				try {

					mediaPlayer.pause();
					Toast.makeText(NoteMainActivity.this, "Recording pause",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Toast.makeText(NoteMainActivity.this, "pause error",
							Toast.LENGTH_SHORT).show();
				}
				progressRecordtext.setText("");

			}
		});

		buttonPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) throws IllegalArgumentException,
					SecurityException, IllegalStateException {
				// TODO Auto-generated method stub
				// Button play Click
				progressRecord1.setProgress(0);

				mediaPlayer = new MediaPlayer();
				progressRecordtext.setVisibility(View.GONE);

				System.out.println("get total duration"
						+ mediaPlayer.getDuration());

				// final String
				// duration=getDurationBreakdown(mediaPlayer.getDuration());
				// final String
				// currduration=getDurationBreakdown(mediaPlayer.getCurrentPosition());

				// textViewDuration.setText(currduration+"/"+duration);

				// / System.out.println("get total duration"
				// + mediaPlayer.getDuration()+"duration in time:"+duration);

				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {

						buttonPlay.setEnabled(true);
						buttonPause.setEnabled(false);
						// progressRecord1.setProgress(0);

					}
				});
				textViewDuration.setVisibility(View.VISIBLE);

				buttonPause.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.VISIBLE);

				buttonPlay.setEnabled(false);
				buttonPause.setEnabled(true);

				buttonRecord.setVisibility(View.GONE);
				buttonStop.setVisibility(View.GONE);

				try {

					System.out.println("file playing path:" + outputFile);
					mediaPlayer.setDataSource(outputFile);
				}

				catch (IOException e) {
					e.printStackTrace();
				}

				try {
					mediaPlayer.prepare();

				}

				catch (IOException e) {

					e.printStackTrace();
				}

				progressRecord1.setMax(mediaPlayer.getDuration() / 1000);
				mediaPlayer.start();

				final Handler mHandler = new Handler();
				// Make sure you update Seekbar on UI thread
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (mediaPlayer != null) {
							int mCurrentPosition = mediaPlayer
									.getCurrentPosition() / 1000;
							String currentduration = getDurationBreakdown(mediaPlayer
									.getCurrentPosition());
							String currentduration1 = getDurationBreakdown(mediaPlayer
									.getDuration());

							if (mCurrentPosition <= mediaPlayer.getDuration() / 1000) {
								System.out.println("CurrentDuration:"
										+ currentduration);
								progressRecord1.setProgress(mCurrentPosition);

								textViewDuration.setText(currentduration + "/"
										+ currentduration1);

							}

						}
						mHandler.postDelayed(this, 1000);
					}
				});

				Toast.makeText(NoteMainActivity.this, "Recording playing",
						Toast.LENGTH_SHORT).show();
				progressRecordtext.setText("");

			}
		});

		buttonStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				progressRecord1.setVisibility(View.VISIBLE);

				myAudioRecorder.stop();
				myAudioRecorder.release();
				myAudioRecorder = null;

				buttonPause.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.VISIBLE);

				buttonPlay.setEnabled(true);
				buttonPause.setEnabled(false);
				// textViewDuration.setVisibility(View.VISIBLE);

				buttonRecord.setVisibility(View.GONE);
				buttonStop.setVisibility(View.GONE);
				Toast.makeText(NoteMainActivity.this, "Recording stopped",
						Toast.LENGTH_SHORT).show();

				System.out.println("Current Index:" + currentAudioIndex);

				NoteListDataModel noteListdatamodel = new NoteListDataModel();
				noteListdatamodel.noteType = NOTETYPE.AUDIOMODE;
				noteListdatamodel.setStrAudioFilePath(outputFile);
				arrNoteListData.add(currentAudioIndex, noteListdatamodel);

				adapter.notifyDataSetChanged();
				listviewNotes.smoothScrollToPosition(currentAudioIndex);

				LayoutAudioRecording.setVisibility(View.GONE);
				progressRecordtext.setText("");

			}
		});
		buttonRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				isRecordingAudio = true;
				progressRecordtext.setVisibility(View.VISIBLE);

				// TODO Auto-generated method stub
				try {

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

				buttonPause.setVisibility(View.GONE);
				buttonPlay.setVisibility(View.GONE);
				buttonRecordPause.setVisibility(View.GONE);
				textViewDuration.setVisibility(View.GONE);

				buttonStop.setEnabled(true);
				buttonRecord.setEnabled(false);
				Toast.makeText(NoteMainActivity.this, "Recording started",
						Toast.LENGTH_SHORT).show();
				progressRecordtext.setText("Recording...");

				if (arrNoteListData.size() > 0) {
					currentAudioIndex = arrNoteListData.size();
				} else {
					currentAudioIndex = 0;
				}

			}
		});
	}

	void initlizesTextNoteControls(View contentview) {
		//

		textNoteControls = (LinearLayout) contentview
				.findViewById(R.id.textNoteControls);
		textNoteControls.setVisibility(View.GONE);

		textButtondrawback = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawback);
		textButtondrawnew = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawnew);
		textButtondrawdraw = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawdraw);
		textButtondrawcolors = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawcolors);
		textButtondrawbrushsize = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawbrushsize);
		textButtondrawerase = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawerase);
		textButtondrawMore = (ImageButton) textNoteControls
				.findViewById(R.id.textButtondrawMore);

		textButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				LayoutTextWritingView.setVisibility(View.GONE);
				isTextmodeSelected = false;

			}
		});

	}

	void initlizesScibbleNoteControles(View contentview) {

		layOutDrawingView = (LinearLayout) contentview
				.findViewById(R.id.layOutDrawingView);
		drawView = (DrawingView) layOutDrawingView
				.findViewById(R.id.viewScibble);

		layOutDrawingView.setVisibility(View.GONE);

		smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		drawView.setBrushSize(smallBrush);

		drawingControls = (LinearLayout) contentview
				.findViewById(R.id.drawingControls);
		drawingControls.setVisibility(View.GONE);

		imageButtondrawback = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawback);
		imageButtondrawnew = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawnew);
		imageButtondrawdraw = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawdraw);
		imageButtondrawcolors = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawcolors);
		imageButtondrawbrushsize = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawbrushsize);
		imageButtondrawerase = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawerase);
		imageButtondrawMore = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawMore);

	}

	void addTextNoteControlsListners() {

		textButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// BACK MODE
				updateTextNoteControlListners(arg0.getId());
				textNoteControls.setVisibility(View.GONE);
				System.out.println("BACK MODE");
				updateButtonUI(-1);
				imageButtonsquence.setVisibility(View.VISIBLE);

				LayoutTextWritingView.setVisibility(View.GONE);

			}
		});
		textButtondrawnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// BULLET MODE
				updateTextNoteControlListners(arg0.getId());
				System.out.println("BULLET  MODE");

			}
		});
		textButtondrawdraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// NUMBERING MODE
				updateTextNoteControlListners(arg0.getId());
				System.out.println("NUMBERING  MODE");
			}
		});
		textButtondrawcolors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// BOLD MODE

				System.out.println("BOLD  MODE");

				if (isBold == true) {
					isBold = false;
					updateTextNoteControlListners(-1);
				} else {
					isBold = true;
					updateTextNoteControlListners(arg0.getId());
				}

			}
		});
		textButtondrawbrushsize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// ITIELIC MODE
				// updateTextNoteControlListners(arg0.getId());
				System.out.println("ITIELIC  MODE");
				if (isItalic == true) {
					isItalic = false;
					updateTextNoteControlListners(-1);
				} else {
					isItalic = true;
					updateTextNoteControlListners(arg0.getId());
				}
			}
		});
		textButtondrawerase.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// UNDERLINE MODE
				// updateTextNoteControlListners(arg0.getId());
				System.out.println("UNDERLINE  MODE");
				if (isUnderLine == true) {
					isUnderLine = false;
					updateTextNoteControlListners(-1);
				} else {
					isUnderLine = true;
					updateTextNoteControlListners(arg0.getId());
				}

			}
		});
		textButtondrawMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// MORE MODE
				updateTextNoteControlListners(arg0.getId());
				System.out.println("MORE  MODE");
			}
		});
	}

	void addScribbleControlListners() {
		imageButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());

				if (drawView.getUserDrawn() == true) {
					SaveDrawingDialog();
				} else {

					drawingControls.setVisibility(View.GONE);
					layOutDrawingView.setVisibility(View.GONE);
					updateButtonUI(-1);
				}

			}
		});
		imageButtondrawnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());
				showNewDrawingDialog();

			}
		});
		imageButtondrawdraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());
				drawView.setErase(false);
				drawView.setBrushSize(drawView.getLastBrushSize());

			}
		});
		imageButtondrawcolors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());
				showColorAlert("", NoteMainActivity.this);
			}
		});
		imageButtondrawbrushsize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());
				showBrushSizeDialog(false);
			}
		});
		imageButtondrawerase.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());
				// showEraserDialog();
				showBrushSizeDialog(true);
			}
		});
		imageButtondrawMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateScribbleControlListners(v.getId());

			}
		});
	}

	void updateTextNoteControlListners(int elementId) {

		textButtondrawback.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		textButtondrawnew.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		textButtondrawdraw.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		textButtondrawcolors.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		textButtondrawbrushsize.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		textButtondrawerase.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		textButtondrawMore.setBackgroundColor(getResources().getColor(
				R.color.header_bg));

		switch (elementId) {
		case R.id.textButtondrawback:
			textButtondrawback.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.textButtondrawnew:
			textButtondrawnew.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.textButtondrawdraw:
			textButtondrawdraw.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.textButtondrawcolors:
			textButtondrawcolors.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.textButtondrawbrushsize:
			textButtondrawbrushsize.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.textButtondrawerase:
			textButtondrawerase.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.textButtondrawMore:
			textButtondrawMore.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;

		default:
			break;
		}

	}

	void updateScribbleControlListners(int elementId) {

		imageButtondrawback.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawnew.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawdraw.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawcolors.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawbrushsize.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawerase.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawMore.setBackgroundColor(getResources().getColor(
				R.color.header_bg));

		switch (elementId) {
		case R.id.imageButtondrawback:
			imageButtondrawback.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtondrawnew:
			imageButtondrawnew.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtondrawdraw:
			imageButtondrawdraw.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtondrawcolors:
			imageButtondrawcolors.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtondrawbrushsize:
			imageButtondrawbrushsize.setBackgroundColor(getResources()
					.getColor(R.color.A8b241b));
			break;
		case R.id.imageButtondrawerase:
			imageButtondrawerase.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtondrawMore:
			imageButtondrawMore.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;

		default:
			break;
		}

	}

	void updateHeaderControls(int itemId) {
		imageButtonHamburg.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtoncalander.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonsquence.setBackgroundColor(getResources().getColor(
				R.color.header_bg));

		switch (itemId) {
		case R.id.imageButtonHamburg:
			imageButtonHamburg.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtoncalander:
			imageButtoncalander.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;
		case R.id.imageButtonsquence:
			imageButtonsquence.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
			break;

		default:
			break;
		}

	}

	/************* moreinfo control Here ************/
	void initlizesMoreInfoView(View contentView) {
		layout_note_more_Info = (LinearLayout) contentView
				.findViewById(R.id.layout_note_more_Info);
		layout_note_more_Info.setVisibility(View.GONE);

		Button buttonLock = (Button) layout_note_more_Info
				.findViewById(R.id.buttonLock);
		Button buttonDelete = (Button) layout_note_more_Info
				.findViewById(R.id.buttonDelete);
		Button buttonRemind = (Button) layout_note_more_Info
				.findViewById(R.id.buttonRemind);
		Button buttonTimeBomb = (Button) layout_note_more_Info
				.findViewById(R.id.buttonTimeBomb);
		Button buttonAttach = (Button) layout_note_more_Info
				.findViewById(R.id.buttonAttach);

		buttonLock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("button lock");
			}
		});
		buttonDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("button delete");
			}
		});
		buttonRemind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("button remind");
			}
		});
		buttonTimeBomb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("button time bomb");
			}
		});
		buttonAttach.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("button attached");
			}
		});

	}

	/************* main list control Here ************/
	void addlistners() {

		imageButtoncalander.setVisibility(View.GONE);

		// #NOTE ITEM CLCIK

		listviewNotes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				NoteListDataModel notelistitem = arrNoteListData.get(arg2);
				Toast.makeText(getApplicationContext(), "clicke",
						Toast.LENGTH_SHORT).show();
				Log.v("GET ITEM", "seletecd item" + arg2 + "TYPE:"
						+ notelistitem.noteType);

				switch (notelistitem.noteType) {
				case TEXTMODE:
				{
					Toast.makeText(getApplicationContext(), "Text mode",
							Toast.LENGTH_SHORT).show();
					
					DataManager.sharedDataManager().setNotelistData(
							notelistitem);

					startActivity(new Intent(NoteMainActivity.this,
							NoteTextIItemFullScreen.class));
					
				}
					break;
				case IMAGEMODE: {
					Toast.makeText(getApplicationContext(), "Image mode",
							Toast.LENGTH_SHORT).show();
					DataManager.sharedDataManager().setNotelistData(
							notelistitem);

					startActivity(new Intent(NoteMainActivity.this,
							NoteItemFullScreen.class));
				}
					break;
				case SCRIBBLEMODE: {
					Toast.makeText(getApplicationContext(), "scribble mode",
							Toast.LENGTH_SHORT).show();
					DataManager.sharedDataManager().setNotelistData(
							notelistitem);
				}
					break;
				case AUDIOMODE: {
					Toast.makeText(getApplicationContext(), "audio mode",
							Toast.LENGTH_SHORT).show();

				}
					break;

				default:
					break;
				}

			}
		});

		// #BUTTON CHECKED CLICK TEXTSAVE

		imageButtoncalander.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				layout_note_more_Info.setVisibility(View.GONE);
				imageButtonsquence.setVisibility(View.VISIBLE);
				isMoreShown = false;
				// Save click
				updateHeaderControls(v.getId());

				// Save test he
				// Updated text in list view

				if (txtViewer.getText().length() > 0) {
					NoteListDataModel model = new NoteListDataModel();
					model.noteType = NOTETYPE.TEXTMODE;
					model.stringtext = new SpannableString(txtViewer.getText());
					Log.d("Note Text",txtViewer.getText().toString());
					//get all notes
					List<Note> allnotes = Note.findWithQuery(Note.class, "Select * from Note");
					int incremented = allnotes.size() + 1;

					SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currentDateStr = formatter.format(new Date());

					Note note = new Note("Test Note "+ incremented, "", "#FFFFFF", "", "", "", "", currentDateStr, "", "");
					note.save();

					NoteElement noteElem = new NoteElement(incremented + "", 1,txtViewer.getText().toString() , "text", "yes");
					noteElem.save();

					finish();
					startActivity(new Intent(context, MainActivity.class));

					arrNoteListData.add(model);
					adapter.notifyDataSetChanged();
					listviewNotes.smoothScrollToPosition(arrNoteListData.size() - 1);
				}
				updateHeaderControls(-1);
				;
				textNoteControls.setVisibility(View.GONE);
				LayoutTextWritingView.setVisibility(View.GONE);
				isTextmodeSelected=false;
			}
		});

		imageButtonHamburg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// openSlideMenu();
				LayoutTextWritingView.setVisibility(View.GONE);
				isTextmodeSelected = false;
				layout_note_more_Info.setVisibility(View.GONE);
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_audio_notechooser.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.GONE);
				isMoreShown = false;
				finish();

			}
		});

		imageButtonsquence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutTextWritingView.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.GONE);
				
				if (isTextmodeSelected == true) {
					LayoutTextWritingView.setVisibility(View.VISIBLE);
					imageButtoncalander.setVisibility(View.VISIBLE);
				}

				layout_note_more_Info.setVisibility(View.GONE);
				layout_audio_notechooser.setVisibility(View.GONE);
				isMoreShown = false;
				
				// Color ICON
				updateHeaderControls(v.getId());

				// Show IN TEXT NOTE
				showTextNoteDialog();

			}
		});

		imageButtonAudioMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawingControls.setVisibility(View.GONE);
				LayoutTextWritingView.setVisibility(View.GONE);
				updateButtonUI(R.id.imageButtonAudioMode);
				System.out.println("audio mode");
				// startActivity(new
				// Intent(getApplicationContext(),AudioChooserActivity.class));
				updatenoteList(NOTETYPE.AUDIOMODE);
				// listviewNotes.setScrollContainer(true);
				layOutDrawingView.setVisibility(View.GONE);
				isTextmodeSelected = false;
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;

				layout_audio_notechooser.setVisibility(View.VISIBLE);
				imageButtoncalander.setVisibility(View.GONE);

				// audioRecording(contentView);
				// initlizesAudioNoteControls(contentView);

			}
		});
		imageButtonImageMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("image  mode");
				updateButtonUI(R.id.imageButtonImageMode);
				// listviewNotes.setScrollContainer(true);
				// startActivity(new
				// Intent(getApplicationContext(),ImageChooserActivity.class));
				LayoutTextWritingView.setVisibility(View.GONE);
				isTextmodeSelected = false;
				drawingControls.setVisibility(View.GONE);
				layOutDrawingView.setVisibility(View.GONE);
				showImageChooserAlertWith("", NoteMainActivity.this);
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;
				layout_audio_notechooser.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.GONE);
			}
		});
		imageButtonPaintMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("paint mode");

				updateButtonUI(R.id.imageButtonPaintMode);

				// startActivity(new
				// Intent(getApplicationContext(),DrawingActivity.class));
				// updatenoteList(NOTETYPE.SCRIBBLEMODE);
				// listviewNotes.setScrollContainer(false);

				// drawView.clear();

				layOutDrawingView.setVisibility(View.VISIBLE);
				drawingControls.setVisibility(View.VISIBLE);
				//
				LayoutTextWritingView.setVisibility(View.GONE);
				isTextmodeSelected = false;
				updateScribbleControlListners(R.id.imageButtondrawback);
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;
				layout_audio_notechooser.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.GONE);

			}
		});
		imageButtonShareMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawingControls.setVisibility(View.GONE);
				updateButtonUI(R.id.imageButtonShareMode);
				System.out.println("share mode");
				layOutDrawingView.setVisibility(View.GONE);
				LayoutTextWritingView.setVisibility(View.GONE);
				isTextmodeSelected = false;
				// listviewNotes.setScrollContainer(true);
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;
				layout_audio_notechooser.setVisibility(View.GONE);
			}
		});
		imageButtonTextMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateButtonUI(R.id.imageButtonTextMode);
				System.out.println("text mode");
				layOutDrawingView.setVisibility(View.GONE);
				// startActivity(new
				// Intent(getApplicationContext(),TextChooserActivity.class));

				// updatenoteList(NOTETYPE.TEXTMODE);
				// listviewNotes.setScrollContainer(true);
				LayoutTextWritingView.setVisibility(View.VISIBLE);
				isTextmodeSelected = true;

				drawingControls.setVisibility(View.GONE);
				textNoteControls.setVisibility(View.VISIBLE);
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;
				layout_audio_notechooser.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.VISIBLE);

			}
		});
		imageButtonMoreMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imageButtonsquence.setVisibility(View.VISIBLE);
				drawingControls.setVisibility(View.GONE);
				System.out.println("more mode");
				layOutDrawingView.setVisibility(View.GONE);
				updateButtonUI(R.id.imageButtonMoreMode);
				layout_audio_notechooser.setVisibility(View.GONE);
				LayoutTextWritingView.setVisibility(View.GONE);

				if (isMoreShown == false) {
					isMoreShown = true;
					layout_note_more_Info.setVisibility(View.VISIBLE);
				} else {
					isMoreShown = false;
					layout_note_more_Info.setVisibility(View.GONE);
				}
				imageButtoncalander.setVisibility(View.GONE);

			}
		});

	}

	void updatenoteList(NOTETYPE notetype) {
		NoteListDataModel noteListdatamodel = new NoteListDataModel();
		noteListdatamodel.noteType = notetype;

		if (notetype == NOTETYPE.AUDIOMODE) {
			noteListdatamodel.setStrAudioFilePath("");
			if (arrNoteListData.size() > 0) {
				currentAudioIndex = arrNoteListData.size() - 1;
			} else {
				currentAudioIndex = 0;
			}

		} else {
			arrNoteListData.add(noteListdatamodel);
		}

		adapter.notifyDataSetChanged();
		listviewNotes.smoothScrollToPosition(arrNoteListData.size() - 1);
	}

	void updateButtonUI(int id) {

		imageButtonAudioMode.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonTextMode.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonShareMode.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonPaintMode.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonMoreMode.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonImageMode.setBackgroundColor(getResources().getColor(
				R.color.header_bg));

		switch (id) {

		case R.id.imageButtonMoreMode: {

			imageButtonMoreMode.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));
		}
			break;
		case R.id.imageButtonTextMode: {

			imageButtonTextMode.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));

		}
			break;
		case R.id.imageButtonPaintMode: {

			imageButtonPaintMode.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));

		}
			break;
		case R.id.imageButtonShareMode: {

			imageButtonShareMode.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));

		}
			break;
		case R.id.imageButtonAudioMode: {
			imageButtonAudioMode.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));

		}
			break;
		case R.id.imageButtonImageMode: {
			imageButtonImageMode.setBackgroundColor(getResources().getColor(
					R.color.A8b241b));

		}
			break;

		default:
			break;
		}

	}

	public void getImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				NoteMainActivity.this);
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

				if (DataManager.sharedDataManager().getArrNoteListData() != null) {
					arrNoteListData = DataManager.sharedDataManager()
							.getArrNoteListData();
					NoteListDataModel noteListdatamodel = new NoteListDataModel();
					noteListdatamodel.noteType = NOTETYPE.IMAGEMODE;
					noteListdatamodel.setBitmap(thumbnail);
					arrNoteListData.add(noteListdatamodel);
					DataManager.sharedDataManager().setArrNoteListData(
							arrNoteListData);
				} else {
					NoteListDataModel noteListdatamodel = new NoteListDataModel();
					noteListdatamodel.noteType = NOTETYPE.IMAGEMODE;
					noteListdatamodel.setBitmap(thumbnail);
					arrNoteListData.add(noteListdatamodel);
					DataManager.sharedDataManager().setArrNoteListData(
							arrNoteListData);
				}
				adapter.notifyDataSetChanged();

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
				final int REQUIRED_SIZE = 320;
				int scale = 1;
				while (options.outWidth / scale / 2 >= REQUIRED_SIZE
						&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
					scale *= 2;
				options.inSampleSize = scale;
				options.inJustDecodeBounds = false;

				bm = BitmapFactory.decodeFile(selectedImagePath, options);

				if (DataManager.sharedDataManager().getArrNoteListData() != null) {
					arrNoteListData = DataManager.sharedDataManager()
							.getArrNoteListData();
					NoteListDataModel noteListdatamodel = new NoteListDataModel();
					noteListdatamodel.noteType = NOTETYPE.IMAGEMODE;
					noteListdatamodel.setBitmap(bm);
					arrNoteListData.add(noteListdatamodel);
					DataManager.sharedDataManager().setArrNoteListData(
							arrNoteListData);
				} else {
					NoteListDataModel noteListdatamodel = new NoteListDataModel();
					noteListdatamodel.noteType = NOTETYPE.IMAGEMODE;
					noteListdatamodel.setBitmap(bm);
					arrNoteListData.add(noteListdatamodel);
					DataManager.sharedDataManager().setArrNoteListData(
							arrNoteListData);
				}
				adapter.notifyDataSetChanged();
				listviewNotes
						.smoothScrollToPosition(arrNoteListData.size() - 1);

			}
		}

	}

	void showColorAlert(String message, Context context) {

		dialogColor = new Dialog(context);

		dialogColor.setCanceledOnTouchOutside(true);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.paintcolor, null, false);
		LinearLayout paintLayout = (LinearLayout) contentView
				.findViewById(R.id.paint_colors);
		LinearLayout paintLayout1 = (LinearLayout) contentView
				.findViewById(R.id.paint_colors1);
		// currPaint = (ImageButton) paintLayout.getChildAt(0);
		// currPaint.setImageDrawable(getResources().getDrawable(
		// R.drawable.paint_pressed));

		ImageButton colorbutton1 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton1);
		ImageButton colorbutton2 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton2);
		ImageButton colorbutton3 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton3);
		ImageButton colorbutton4 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton4);
		ImageButton colorbutton5 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton5);
		ImageButton colorbutton6 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton6);
		ImageButton colorbutton7 = (ImageButton) paintLayout
				.findViewById(R.id.colorbutton7);
		ImageButton colorbutton8 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton8);
		ImageButton colorbutton9 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton9);
		ImageButton colorbutton10 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton10);
		ImageButton colorbutton11 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton11);
		ImageButton colorbutton12 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton12);
		ImageButton colorbutton13 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton13);
		ImageButton colorbutton14 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton14);
		colorbutton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);

			}
		});
		colorbutton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);

			}
		});
		colorbutton3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);

			}
		});
		colorbutton4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});

		colorbutton8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});

		colorbutton9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton11.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton12.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton13.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});
		colorbutton14.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				paintClicked(v);
			}
		});

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("SELECT COLOR");
		textViewTitleAlert.setTextColor(Color.WHITE);

		dialogColor.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogColor.setCancelable(false);

		dialogColor.setContentView(contentView);
		dialogColor.show();
	}

	void showImageChooserAlertWith(String message, Context context) {

		final Dialog dialog = new Dialog(context);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.chooseimagealertview,
				null, false);

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("SELECT IMAGE");
		textViewTitleAlert.setTextColor(Color.WHITE);

		TextView buttonAlertCancel = (TextView) contentView
				.findViewById(R.id.buttonAlertCancel);

		TextView textViewTitleTakePicture = (TextView) contentView
				.findViewById(R.id.textViewTitleTakePicture);

		TextView textViewTitlechosserfromgallary = (TextView) contentView
				.findViewById(R.id.textViewTitlechosserfromgallary);

		buttonAlertCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
		textViewTitleTakePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// System.exit(0);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CAMERA);

				dialog.dismiss();

			}
		});

		textViewTitlechosserfromgallary
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// System.exit(0);

						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(
								Intent.createChooser(intent, "Select File"),
								SELECT_PICTURE);

						dialog.dismiss();

					}
				});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(contentView);
		dialog.show();

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

	/************* show brush control Here ************/
	void showBrushSizeDialog(boolean iserase) {

		brushDialog1 = new Dialog(NoteMainActivity.this);

		isErase = iserase;

		brushDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		brushDialog1.setCancelable(false);
		brushDialog1.setContentView(R.layout.brush_chooser);
		TextView textViewSizesHeader = (TextView) brushDialog1
				.findViewById(R.id.textViewSizesHeader);
		if (iserase == true) {
			textViewSizesHeader.setText("ERASE SIZES");
		} else {
			textViewSizesHeader.setText("BRUSH SIZES");
		}

		ImageButton smallBtn = (ImageButton) brushDialog1
				.findViewById(R.id.small_brush);
		ImageButton smallBtn_12 = (ImageButton) brushDialog1
				.findViewById(R.id.small_brush_12);
		ImageButton smallBtn_14 = (ImageButton) brushDialog1
				.findViewById(R.id.small_brush_14);
		ImageButton smallBtn_16 = (ImageButton) brushDialog1
				.findViewById(R.id.small_brush_16);
		ImageButton smallBtn_18 = (ImageButton) brushDialog1
				.findViewById(R.id.small_brush_18);

		smallBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				drawView.setErase(isErase);
				smallBrush = getResources().getInteger(R.integer.small_size);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		smallBtn_12.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				drawView.setErase(isErase);
				smallBrush = getResources().getInteger(R.integer.small_size_12);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		smallBtn_14.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				drawView.setErase(isErase);
				smallBrush = getResources().getInteger(R.integer.small_size_14);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		smallBtn_16.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				drawView.setErase(isErase);
				smallBrush = getResources().getInteger(R.integer.small_size_16);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		smallBtn_18.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				drawView.setErase(isErase);
				smallBrush = getResources().getInteger(R.integer.small_size_18);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});

		ImageButton mediumBtn = (ImageButton) brushDialog1
				.findViewById(R.id.medium_brush);

		ImageButton medium_brush_22 = (ImageButton) brushDialog1
				.findViewById(R.id.medium_brush_22);
		ImageButton medium_brush_24 = (ImageButton) brushDialog1
				.findViewById(R.id.medium_brush_24);
		ImageButton medium_brush_26 = (ImageButton) brushDialog1
				.findViewById(R.id.medium_brush_26);
		ImageButton medium_brush_28 = (ImageButton) brushDialog1
				.findViewById(R.id.medium_brush_28);

		mediumBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				smallBrush = getResources().getInteger(R.integer.medium_size);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		medium_brush_22.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				smallBrush = getResources()
						.getInteger(R.integer.medium_size_22);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		medium_brush_24.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				smallBrush = getResources()
						.getInteger(R.integer.medium_size_24);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		medium_brush_26.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				smallBrush = getResources()
						.getInteger(R.integer.medium_size_26);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});

		medium_brush_28.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				smallBrush = getResources()
						.getInteger(R.integer.medium_size_28);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});

		ImageButton large_brush = (ImageButton) brushDialog1
				.findViewById(R.id.large_brush);

		ImageButton large_brush_32 = (ImageButton) brushDialog1
				.findViewById(R.id.large_brush_32);
		ImageButton large_brush_34 = (ImageButton) brushDialog1
				.findViewById(R.id.large_brush_34);
		ImageButton large_brush_36 = (ImageButton) brushDialog1
				.findViewById(R.id.large_brush_36);
		ImageButton large_brush_38 = (ImageButton) brushDialog1
				.findViewById(R.id.large_brush_38);

		large_brush.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				smallBrush = getResources().getInteger(R.integer.large_size);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		large_brush_32.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				smallBrush = getResources().getInteger(R.integer.large_size_32);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		large_brush_34.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				smallBrush = getResources().getInteger(R.integer.large_size_34);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		large_brush_36.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				smallBrush = getResources().getInteger(R.integer.large_size_36);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});
		large_brush_38.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				smallBrush = getResources().getInteger(R.integer.large_size_38);
				drawView.setErase(isErase);
				drawView.setBrushSize(smallBrush);
				brushDialog1.dismiss();
			}
		});

		brushDialog1.show();

	}

	/************* Erase control Here ************/
	void showEraserDialog() {

		final Dialog brushDialog1 = new Dialog(NoteMainActivity.this);

		brushDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		brushDialog1.setCancelable(true);
		brushDialog1.setContentView(R.layout.brush_chooser);

		TextView textViewSizesHeader = (TextView) brushDialog1
				.findViewById(R.id.textViewSizesHeader);

		textViewSizesHeader.setText("ERASER SIZES");

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
		});
		brushDialog1.show();

	}

	/************* text control Here ************/
	void showTextNoteDialog() {

		final Dialog dialog = new Dialog(NoteMainActivity.this);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// View contentView = inflater.inflate(R.layout.note_text_style_chooser,
		// null, false);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.note_text_style_chooser);

		final LinearLayout layoutPapers = (LinearLayout) dialog
				.findViewById(R.id.layoutPapers);
		final LinearLayout layoutColors1 = (LinearLayout) dialog
				.findViewById(R.id.layoutColors1);
		final ListView ListViewItems = (ListView) dialog
				.findViewById(R.id.ListViewItems);

		final Button buttonFont = (Button) dialog.findViewById(R.id.buttonFont);
		final Button buttonSize = (Button) dialog.findViewById(R.id.buttonSize);
		final Button buttonColors = (Button) dialog
				.findViewById(R.id.buttonColors);
		final Button buttonPaper = (Button) dialog
				.findViewById(R.id.buttonPaper);

		layoutPapers.setVisibility(View.GONE);
		layoutColors1.setVisibility(View.GONE);
		ListViewItems.setVisibility(View.VISIBLE);

		// fonts_sizeName=getResources().getStringArray(R.array.Font_Size_px);
		fontSizes = getResources().getStringArray(R.array.Font_Size);

		TextFont_sizeAdapter = new TextFont_Size_ChooseAdapter(
				NoteMainActivity.this, fonts_Name_Display);// fonts_sizeName
		ListViewItems.setAdapter(TextFont_sizeAdapter);
		DataManager.sharedDataManager().setSELECTED_TEXT_OPTION(NOTETYPE.FONT);

		buttonFont.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				DataManager.sharedDataManager().setSELECTED_TEXT_OPTION(
						NOTETYPE.FONT);
				TextFont_sizeAdapter = new TextFont_Size_ChooseAdapter(
						NoteMainActivity.this, fonts_Name_Display);// fonts_sizeName
				ListViewItems.setAdapter(TextFont_sizeAdapter);

				layoutPapers.setVisibility(View.GONE);
				layoutColors1.setVisibility(View.GONE);
				ListViewItems.setVisibility(View.VISIBLE);

				buttonFont.setBackgroundColor(getResources().getColor(
						R.color.eaeaea));
				buttonFont.setTextColor(getResources().getColor(
						R.color.header_bg));

				buttonSize.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonSize
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonColors.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonColors.setTextColor(getResources().getColor(
						R.color.ffffff));

				buttonPaper.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonPaper.setTextColor(getResources()
						.getColor(R.color.ffffff));

				TextFont_sizeAdapter.notifyDataSetChanged();
			}
		});
		buttonSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				DataManager.sharedDataManager().setSELECTED_TEXT_OPTION(
						NOTETYPE.SIZE);

				TextFont_sizeAdapter = new TextFont_Size_ChooseAdapter(
						NoteMainActivity.this, fonts_sizeName);// fonts_sizeName
				ListViewItems.setAdapter(TextFont_sizeAdapter);

				layoutPapers.setVisibility(View.GONE);
				layoutColors1.setVisibility(View.GONE);
				ListViewItems.setVisibility(View.VISIBLE);

				buttonFont.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonFont
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonSize.setBackgroundColor(getResources().getColor(
						R.color.eaeaea));
				buttonSize.setTextColor(getResources().getColor(
						R.color.header_bg));

				buttonColors.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonColors.setTextColor(getResources().getColor(
						R.color.ffffff));

				buttonPaper.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonPaper.setTextColor(getResources()
						.getColor(R.color.ffffff));
				TextFont_sizeAdapter.notifyDataSetChanged();
				
			}
		});
		buttonColors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				layoutPapers.setVisibility(View.GONE);
				layoutColors1.setVisibility(View.VISIBLE);
				ListViewItems.setVisibility(View.GONE);

				buttonFont.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonFont
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonSize.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonSize
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonColors.setBackgroundColor(getResources().getColor(
						R.color.eaeaea));
				buttonColors.setTextColor(getResources().getColor(
						R.color.header_bg));

				buttonPaper.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonPaper.setTextColor(getResources()
						.getColor(R.color.ffffff));
			}
		});

		buttonPaper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				layoutPapers.setVisibility(View.VISIBLE);
				layoutColors1.setVisibility(View.GONE);
				ListViewItems.setVisibility(View.GONE);

				buttonFont.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonFont
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonSize.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonSize
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonColors.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonColors.setTextColor(getResources().getColor(
						R.color.ffffff));

				buttonPaper.setBackgroundColor(getResources().getColor(
						R.color.eaeaea));
				buttonPaper.setTextColor(getResources().getColor(
						R.color.header_bg));

			}
		});

		// Onselected the size and font

		ListViewItems.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (DataManager.sharedDataManager().getSELECTED_TEXT_OPTION() == NOTETYPE.FONT) {

					currentFontTypeface = fonts_Name_Display[arg2].toString();
					Toast.makeText(getApplicationContext(),
							"FONT" + currentFontTypeface, Toast.LENGTH_SHORT)
							.show();

					dialog.dismiss();

				} else if (DataManager.sharedDataManager()
						.getSELECTED_TEXT_OPTION() == NOTETYPE.SIZE) {

					currentFontSize = Integer.parseInt(fontSizes[arg2]);
					Toast.makeText(getApplicationContext(),
							"SIZE" + currentFontSize, Toast.LENGTH_SHORT)
							.show();

					dialog.dismiss();

				}

			}
		});

		// ListViewItems

		// buttonFont
		// buttonSize
		// buttonColors
		// buttonPaper

		ImageButton paper_bg_10 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_10);
		ImageButton paper_bg_9 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_9);
		ImageButton paper_bg_8 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_8);
		ImageButton paper_bg_7 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_7);
		ImageButton paper_bg_6 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_6);
		ImageButton paper_bg_5 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_5);
		ImageButton paper_bg_4 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_4);
		ImageButton paper_bg_3 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_3);
		ImageButton paper_bg_2 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_2);
		ImageButton paper_bg_1 = (ImageButton) dialog
				.findViewById(R.id.paper_bg_1);

		paper_bg_10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});

		ImageButton color_bg_10 = (ImageButton) dialog
				.findViewById(R.id.color_bg_10);
		ImageButton color_bg_9 = (ImageButton) dialog
				.findViewById(R.id.color_bg_9);
		ImageButton color_bg_8 = (ImageButton) dialog
				.findViewById(R.id.color_bg_8);
		ImageButton color_bg_7 = (ImageButton) dialog
				.findViewById(R.id.color_bg_7);
		ImageButton color_bg_6 = (ImageButton) dialog
				.findViewById(R.id.color_bg_6);
		ImageButton color_bg_5 = (ImageButton) dialog
				.findViewById(R.id.color_bg_5);
		ImageButton color_bg_4 = (ImageButton) dialog
				.findViewById(R.id.color_bg_4);
		ImageButton color_bg_3 = (ImageButton) dialog
				.findViewById(R.id.color_bg_3);
		ImageButton color_bg_2 = (ImageButton) dialog
				.findViewById(R.id.color_bg_2);
		ImageButton color_bg_1 = (ImageButton) dialog
				.findViewById(R.id.color_bg_1);
		color_bg_10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});

		color_bg_8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	void showNewDrawingDialog() {

		final Dialog dialog = new Dialog(NoteMainActivity.this);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View contentView = inflater.inflate(R.layout.alert_view, null, false);

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

	}

	void SaveDrawingDialog() {

		final Dialog dialog = new Dialog(NoteMainActivity.this);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View contentView = inflater.inflate(R.layout.alert_view, null, false);

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("SAVE DRAWING");
		textViewTitleAlert.setTextColor(Color.WHITE);
		TextView textViewTitleAlertMessage = (TextView) contentView
				.findViewById(R.id.textViewTitleAlertMessage);
		textViewTitleAlertMessage.setText("Save drawing to device Gallery?");

		Button buttonAlertCancel = (Button) contentView
				.findViewById(R.id.buttonAlertCancel);
		Button buttonAlertOk = (Button) contentView
				.findViewById(R.id.buttonAlertOk);

		buttonAlertCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				drawingControls.setVisibility(View.GONE);
				layOutDrawingView.setVisibility(View.GONE);
				updateButtonUI(-1);

				dialog.dismiss();

			}
		});
		buttonAlertOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				drawingControls.setVisibility(View.GONE);
				layOutDrawingView.setVisibility(View.GONE);
				updateButtonUI(-1);

				// TODO Auto-generated method stub
				drawView.setDrawingCacheEnabled(true);

				String imgSaved = MediaStore.Images.Media.insertImage(
						getContentResolver(), drawView.getDrawingCache(), UUID
								.randomUUID().toString() + ".png", "drawing");
				System.out.println("the string uri:" + imgSaved);

				if (imgSaved != null) {
					Toast.makeText(getApplicationContext(),
							"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
					// savedToast.show();
					drawView.destroyDrawingCache();

					if (DataManager.sharedDataManager().getArrNoteListData() != null) {
						arrNoteListData = DataManager.sharedDataManager()
								.getArrNoteListData();
						NoteListDataModel noteListdatamodel = new NoteListDataModel();
						noteListdatamodel.noteType = NOTETYPE.IMAGEMODE;
						noteListdatamodel.setBitmap(drawView.getBitMapimagae());
						arrNoteListData.add(noteListdatamodel);
						DataManager.sharedDataManager().setArrNoteListData(
								arrNoteListData);

						// arrNoteListData.addAll(DataManager.sharedDataManager().getArrNoteListData());
						adapter.notifyDataSetChanged();

						listviewNotes.smoothScrollToPosition(arrNoteListData
								.size() - 1);
					} else {
						NoteListDataModel noteListdatamodel = new NoteListDataModel();
						noteListdatamodel.noteType = NOTETYPE.IMAGEMODE;
						noteListdatamodel.setBitmap(drawView.getBitMapimagae());
						arrNoteListData.add(noteListdatamodel);
						DataManager.sharedDataManager().setArrNoteListData(
								arrNoteListData);

						// arrNoteListData.addAll(DataManager.sharedDataManager().getArrNoteListData());
						adapter.notifyDataSetChanged();
						listviewNotes.smoothScrollToPosition(arrNoteListData
								.size() - 1);

					}

					drawView.setUserDrawn(false);

				} else {
					Toast.makeText(getApplicationContext(),
							"Oops! Image could not be saved.",
							Toast.LENGTH_SHORT);
				}

				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(contentView);
		dialog.show();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		// super.onBackPressed();

		// if (drawView.getUserDrawn() == true) {
		// SaveDrawingDialog();
		// } else {

		finish();
		DataManager.sharedDataManager().setArrNoteListData(
				new ArrayList<NoteListDataModel>());
		// }

	}

	public void paintClicked(View view) {
		// use chosen color
		if (view != currPaint) {
			// update color
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();

			imgView.setImageDrawable(getResources().getDrawable(
					R.drawable.paint_pressed));
			// currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			// currPaint = (ImageButton) view;
			System.out.println("selected color:" + color);

			int colorCode = Color.parseColor(color);
			dialogColor.dismiss();
			drawView.setDrawColor(colorCode);

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		paintClicked(v);

	}

	public void paperButtonSelected(View view) {
		if (view.getId() == R.id.paper_bg_1) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_1);
		} else if (view.getId() == R.id.paper_bg_2) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_2);
		} else if (view.getId() == R.id.paper_bg_3) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_3);
		} else if (view.getId() == R.id.paper_bg_4) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_4);
		} else if (view.getId() == R.id.paper_bg_5) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_5);
		} else if (view.getId() == R.id.paper_bg_6) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_6);
		} else if (view.getId() == R.id.paper_bg_7) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_7);
		} else if (view.getId() == R.id.paper_bg_8) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_8);
		} else if (view.getId() == R.id.paper_bg_9) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_9);
		} else if (view.getId() == R.id.paper_bg_10) {
			background_bg.setBackgroundResource(R.drawable.paper_bg_10);
		}

	}

	public void colorButtonSelected(View view) {

		currentFontColor = Color.BLACK;
		background_bg.setBackgroundColor(Color.parseColor(view.getTag()
				.toString()));

	}

	/************* Update text Here ************/

	void updatedText(boolean undrLine, boolean bold, boolean italic, int fontSize, int textcolor, String text) {

		spanUpdted = new SpannableString(text);
		if (txtViewer.getText().length() > 0) {
			spanold = new SpannableString(txtViewer.getText());
			txtViewer.setText(TextUtils.concat(spanold, " ", spanUpdted));
		} else {
			txtViewer.setText(TextUtils.concat(spanUpdted));
		}

//		String strFaimly = NoteShareFonts.arial;

		/*if (text.length() > 0) {
			Typeface typeface;
			if (currentFontTypeface.length() > 0)

			{
				typeface = NoteShareFonts.asTypeface(NoteMainActivity.this,
						currentFontTypeface);
				strFaimly = NoteShareFonts
						.asTypefacefaimly(currentFontTypeface);
				if (typeface == null) {
					typeface = NoteShareFonts.asTypeface(NoteMainActivity.this,
							NoteShareFonts.arial);
				}

			} else {
				typeface = NoteShareFonts.asTypeface(NoteMainActivity.this,
						NoteShareFonts.arial);
			}

			typefacae = Typeface.NORMAL;
			if (bold == true && italic == true) {
				typefacae = Typeface.BOLD_ITALIC;

			} else if (bold == false && italic == true) {
				typefacae = Typeface.ITALIC;
			} else if (bold == true && italic == false) {
				typefacae = Typeface.BOLD;
			}


			//FONT SIZE ADDED

			spanUpdted.setSpan(new AbsoluteSizeSpan(fontSize * 2, true), 0,
					spanUpdted.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			//FONT STYLE

			spanUpdted.setSpan(new StyleSpan(typefacae), 0,
					spanUpdted.length(), 0);

			//CUSTOM FONT ADDED

			spanUpdted.setSpan(new CustomTypefaceSpan("", typeface), 0,
					spanUpdted.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

			//UNDERLINE ADDED

			if (undrLine)
			{
				spanUpdted.setSpan(new UnderlineSpan(), 0, spanUpdted.length(),
						0);
			}

			{
				 spanUpdted.setSpan(new BackgroundColorSpan(textbgxcolor), 0,
				 spanUpdted.length(), 0);
			}

			{
				spanUpdted.setSpan(new ForegroundColorSpan(textcolor), 0,
						spanUpdted.length(), 0);
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (txtViewer.getText().length() > 0) {
						spanold = new SpannableString(txtViewer.getText());
						txtViewer.setText(TextUtils.concat(spanold, " ",
								spanUpdted));
					} else {
						txtViewer.setText(TextUtils.concat(spanUpdted));

					}

				}
			});

		}*/

	}

	private String getNextFileName() {
		return Environment.getExternalStorageDirectory() + File.separator
				+ "Record_" + System.currentTimeMillis() + ".mp4";
	}

	private void invalidateButtons() {
		switch (mAudioRecorder.getStatus()) {
		case STATUS_UNKNOWN:
			// mStartButton.setEnabled(false);
			// mPauseButton.setEnabled(false);
			// mPlayButton.setEnabled(false);
			break;
		case STATUS_READY_TO_RECORD:
			// mStartButton.setEnabled(true);
			// mPauseButton.setEnabled(false);
			// mPlayButton.setEnabled(false);
			break;
		case STATUS_RECORDING:
			// mStartButton.setEnabled(false);
			// mPauseButton.setEnabled(true);
			// mPlayButton.setEnabled(false);
			break;
		case STATUS_RECORD_PAUSED:
			// mStartButton.setEnabled(true);
			// mPauseButton.setEnabled(false);
			// mPlayButton.setEnabled(true);
			break;
		default:
			break;
		}
	}

	private void start() {
		mAudioRecorder.start(new AudioRecorder.OnStartListener() {
			@Override
			public void onStarted() {
				invalidateButtons();
			}

			@Override
			public void onException(Exception e) {
				invalidateButtons();
				Toast.makeText(NoteMainActivity.this, "Record error",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void pause() {
		mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
			@Override
			public void onPaused(String activeRecordFileName) {
				mActiveRecordFileName = activeRecordFileName;
				invalidateButtons();
			}

			@Override
			public void onException(Exception e) {
				invalidateButtons();
				Toast.makeText(NoteMainActivity.this, "Pause error",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void play() {
		File file = new File(mActiveRecordFileName);
		if (file.exists()) {

			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "audio/*");
			startActivity(intent);
		}
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
	
	
	
	//TEXT WATCHER
	
	TextWatcher watch = new TextWatcher(){
		 
	    @Override
	    public void afterTextChanged(Editable arg0) {
	        // TODO Auto-generated method stub
	 
	    }
	 
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	            int arg3)
	    {
	        // TODO Auto-generated method stub
	 
	    }
	 
	    @Override
	    public void onTextChanged(CharSequence s, int a, int b, int c) {
	        // TODO Auto-generated method stub
	 
	    	txtViewer.setText(s);
	        
	        
	        
	        if(a == 9){
	            Toast.makeText(getApplicationContext(), "Maximum Limit Reached", Toast.LENGTH_SHORT).show();
	        }
	    }};

}
