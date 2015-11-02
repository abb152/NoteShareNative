package com.tilak.noteshare;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.ak.android.widget.colorpickerseekbar.ColorPickerSeekBar;
import com.tilak.adpters.NotesListAdapter;
import com.tilak.adpters.TextFont_Size_ChooseAdapter;
import com.tilak.datamodels.NoteListDataModel;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

//import android.support.annotation.Keep;

public class NoteMainActivity extends DrawerActivity implements OnClickListener {

	public ImageButton imageButtonHamburg, imageButtoncalander,
			imageButtonsquence, imageButtoncheckbox;
	private float smallBrush, mediumBrush, largeBrush;
	public ImageButton imageButtonTextMode, imageButtonImageMode,
			imageButtonPaintMode, imageButtonAudioMode, imageButtonShareMode,
			imageButtonMoreMode;
	public SpannableString spanUpdted, spanold;
	MediaPlayer mediaPlayer;// = new MediaPlayer();
	public int typefacae = Typeface.NORMAL;

	public TextView progressRecordtext;
	EditText textViewheaderTitle;
	public RelativeLayout layoutHeader;
	public int currentAudioIndex = 0;
	public LinearLayout noteElements;
	List<ImageView> listImage = new ArrayList<ImageView>();

	//Color picker
	int color_selected = -16777216;
	ColorPickerSeekBar colorPickerSeekBar = null;

	EditText edittextEditer, txtViewer;
	Button btnAddText;
	ImageButton buttonPlay;
	ImageButton buttonStop;
	ImageButton buttonRecord, buttonPause, buttonRecordPause;
	ProgressBar progressRecord;
	SeekBar progressRecord1;
	RelativeLayout LayoutAudioRecording;
	private AudioRecorder mAudioRecorder;
	private String audioName;

	private String mActiveRecordFileName;

	public LinearLayout layOutDrawingView, textNoteControls,
			layout_note_more_Info, layout_audio_notechooser , audioElement;

	public TextView textViewAdd, textViewDuration;
	ImageButton bold = null, italic = null, underline = null, h1 = null, h2 = null, h3 = null, h4 = null, h5 = null, h6 = null, align_left = null, align_center = null, align_right = null, redo = null, undo = null;
	/*LayoutInflater inflator = getLayoutInflater();
	View viewText = inflator.inflate(R.layout.note_text, null, false);*/
	View viewText;
	List<RichEditor> allRe = new ArrayList<RichEditor>();
	List<EditText> allCheckboxText = new ArrayList<EditText>();

	final Context context = this;
	public ArrayList<NoteListDataModel> arrNoteListData;
	public ListView listviewNotes;
	public NotesListAdapter adapter;
	private static final int SELECT_PICTURE = 1;
	private static final int REQUEST_CAMERA = 2;
	public DrawingView drawView;
	private ImageButton currPaint;
	public Dialog dialogColor;
	public Dialog move;

	boolean isErase;
	public Dialog brushDialog1;
	private ImageView imageView53;

	boolean isUnderLine = false, isBold = false, isItalic = false;
	public int currentFontSize;
	public String currentFontTypeface;
	public int currentFontColor;
	boolean isRecordingAudio = false;
	private MediaRecorder myAudioRecorder;
	View contentView;
	private String outputFile = null;
	public static String noteIdForDetails;
	SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String currentDateStr = formatter.format(new Date());

	// /Drawing Controls

	public LinearLayout drawingControls;
	public RelativeLayout LayoutTextWritingView;
	public HorizontalScrollView horizontal_scroll_editor;

	public ScrollView scrollView;

	public ImageButton imageButtondrawback, imageButtondrawnew,
            imageButtonbrushdraw, imageButtondrawcolors,
			imageButtonhighlightdraw, imageButtondrawerase,
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
	RelativeLayout background_bg;
	public String[] editortext = new String[1];
	public List<View> textelementid = new ArrayList<View>();
	public String[] noteTitle = new String[1];
	public String[] noteCheckText = new String[1];

	// 8b241b selected bg

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		Intent intent = this.getIntent();
		noteIdForDetails = intent.getStringExtra("NoteId");
		Log.v("select", "onCreate Note Id" + noteIdForDetails);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		contentView = inflater
				.inflate(R.layout.note_activity_main, null, false);
		mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(contentView);
		try {
			fetchNoteElementsFromDb();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		/*List<NoteElement> ne = NoteElement.findWithQuery(NoteElement.class, "SELECT con from NoteElement where  NOTEID= '1' AND TYPE ='image'");
		for(NoteElement n :ne){
			name = n.content;
		}*/

	}

	@Override
	protected void onResume() {
		super.onResume();
		initlizeUIElement(contentView);
		try {
			fetchNoteElementsFromDb();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	void initlizeUIElement(View contentview) {

		scrollView = (ScrollView) contentview.findViewById(R.id.scrollView);

		noteElements = (LinearLayout) findViewById(R.id.noteElements);


	/* Default Initlization */
		currentFontSize = 8;
		currentFontTypeface = NoteShareFonts.arial;
		currentFontColor = Color.BLACK;

		background_bg = (RelativeLayout) contentview
				.findViewById(R.id.background_bg);
		layoutHeader = (RelativeLayout) contentview
				.findViewById(R.id.mainHeadermenue);
		imageButtoncalander = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtoncalander);
		imageButtoncheckbox = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtoncheckbox);

		textViewheaderTitle = (EditText) layoutHeader
				.findViewById(R.id.textViewheaderTitle);
		imageButtonHamburg = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtonHamburg);
		imageButtonsquence = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtonsquence);

		imageButtonsquence.setImageResource(R.drawable.color_header_icon);
		imageButtonHamburg.setImageResource(R.drawable.back_icon_1);
		imageButtoncalander.setImageResource(R.drawable.done_icon);

		horizontal_scroll_editor = (HorizontalScrollView) contentview.findViewById(R.id.horizontal_scroll_editor);
		horizontal_scroll_editor.setVisibility(View.GONE);

		//imageView53 = (ImageView) findViewById(R.id.imageView53);

		// textViewAdd = (im) findViewById(R.id.textViewAdd);

		// audio controls
		//audioRecording(contentView);
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

        /*if(noteIdForDetails == null)
            textViewheaderTitle.setText("NOTE");*/

        textViewheaderTitle.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (noteIdForDetails == null) {
					makeNote();
				}

				String updatedText = s.toString();
				if(!noteTitle[0].equals(updatedText)){
					Note note = Note.findById(Note.class, Long.parseLong(noteIdForDetails));
					note.title = updatedText;
					//note.modificationtime = currentDateStr;
					note.save();
					modifyNoteTime();
					noteTitle[0] = updatedText;
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});



		/** Layout Audio Recording **/

		addlistners();
		addScribbleControlListners();
		addTextNoteControlsListners();
		addAudioNoteListners();

		updateHeaderControls(R.id.imageButtonHamburg);
		imageButtonsquence.setVisibility(View.VISIBLE);
		//imageButtoncalander.setVisibility(View.VISIBLE);

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
				updateButtonUI(-1);
			}
		});

		/*imageView53.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = null;
				NoteElement ne = NoteElement.findById(NoteElement.class, 1L);
				name = ne.content;

				*//*Bitmap b = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Images/" + name);
				imageView53.setImageBitmap(b);*//*
				Toast.makeText(getApplication(), name, Toast.LENGTH_LONG).show();
				try {
					MediaPlayer mp = new MediaPlayer();
					mp.setDataSource(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Audio/" + name);
					mp.prepare();
					mp.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});*/

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
				updateAudioNoteUI(arg0.getId());
				layout_audio_notechooser.setVisibility(View.GONE);
			}
		});

		audioButtondrawnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

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
		//SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHH_mm_ss");
		String timestamp = String.valueOf(System.currentTimeMillis());
		audioName = "AUD-" + timestamp + ".3gp";

		outputFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/NoteShare/NoteShare Audio/" + audioName;
		;

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	/*void audioRecording(View contentview) {

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
				buttonRecord.setVisibility(View.VISIBLE);
				buttonRecordPause.setVisibility(View.GONE);
				myAudioRecorder.stop();
				progressRecordtext.setText("");

			}
		});

		buttonPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				buttonPause.setVisibility(View.VISIBLE);
				buttonPlay.setVisibility(View.VISIBLE);

				buttonPlay.setEnabled(true);
				buttonPause.setEnabled(false);
				buttonStop.setEnabled(false);
				progressRecordtext.setVisibility(View.GONE);

				buttonRecord.setVisibility(View.GONE);
				buttonStop.setVisibility(View.GONE);

				try {

					mediaPlayer.pause();
					Toast.makeText(NoteMainActivity.this, "Recording pause",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
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
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					mediaPlayer.prepare();

				} catch (IOException e) {

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

				*//*NoteListDataModel noteListdatamodel = new NoteListDataModel();
				noteListdatamodel.noteType = NOTETYPE.AUDIOMODE;
				noteListdatamodel.setStrAudioFilePath(outputFile);
				//arrNoteListData.add(currentAudioIndex, noteListdatamodel);*//*

				//adapter.notifyDataSetChanged();
				//listviewNotes.smoothScrollToPosition(currentAudioIndex);

				LayoutAudioRecording.setVisibility(View.GONE);
				progressRecordtext.setText("");

				NoteElement ne = new NoteElement(1, 1, "yes", "audio", audioName);
				ne.save();

			}
		});
		buttonRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				isRecordingAudio = true;
				progressRecordtext.setVisibility(View.VISIBLE);

				try {

					myAudioRecorder.prepare();
					myAudioRecorder.start();

				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
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

				*//*if (arrNoteListData.size() > 0) {
					currentAudioIndex = arrNoteListData.size();
				} else {
					currentAudioIndex = 0;
				}*//*

			}
		});
	}*/

	void initlizesTextNoteControls(View contentview) {
		//

		/*textNoteControls = (LinearLayout) contentview
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
				isTextmodeSelected = false;
			}
		});*/
		textNoteControls = (LinearLayout) contentview.findViewById(R.id.textNoteControls);
		textNoteControls.setVisibility(View.GONE);

		// bold italic underline h1 h2 h3 h4 h5 h6 align_left align_center align_right redo undo
		bold = (ImageButton) findViewById(R.id.action_bold);
		italic = (ImageButton) findViewById(R.id.action_italic);
		underline = (ImageButton) findViewById(R.id.action_underline);
		h1 = (ImageButton) findViewById(R.id.action_heading1);
		h2 = (ImageButton) findViewById(R.id.action_heading2);
		h3 = (ImageButton) findViewById(R.id.action_heading3);
		h4 = (ImageButton) findViewById(R.id.action_heading4);
		h5 = (ImageButton) findViewById(R.id.action_heading5);
		h6 = (ImageButton) findViewById(R.id.action_heading6);
		align_left = (ImageButton) findViewById(R.id.action_align_left);
		align_center = (ImageButton) findViewById(R.id.action_align_center);
		align_right = (ImageButton) findViewById(R.id.action_align_right);
		redo = (ImageButton) findViewById(R.id.action_redo);
		undo = (ImageButton) findViewById(R.id.action_undo);

	}

	void initlizesScibbleNoteControles(View contentview) {

		layOutDrawingView = (LinearLayout) contentview
				.findViewById(R.id.layOutDrawingView);
		drawView = (DrawingView) layOutDrawingView
				.findViewById(R.id.viewScibble);

		layOutDrawingView.setVisibility(View.GONE);

		/*smallBrush = getResources().getInteger(R.integer.small_size);
		mediumBrush = getResources().getInteger(R.integer.medium_size);
		largeBrush = getResources().getInteger(R.integer.large_size);

		drawView.setBrushSize(smallBrush);*/

		drawingControls = (LinearLayout) contentview
				.findViewById(R.id.drawingControls);
		drawingControls.setVisibility(View.GONE);

		imageButtondrawback = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawback);
		imageButtondrawnew = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawnew);
        imageButtonbrushdraw = (ImageButton) drawingControls
				.findViewById(R.id.imageButtonbrushdraw);
		imageButtondrawcolors = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawcolors);
        imageButtonhighlightdraw = (ImageButton) drawingControls
				.findViewById(R.id.imageButtonhighlightdraw);
		imageButtondrawerase = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawerase);
		imageButtondrawMore = (ImageButton) drawingControls
				.findViewById(R.id.imageButtondrawMore);

	}

	void addTextNoteControlsListners() {

		/*textButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// BACK MODE
				updateTextNoteControlListners(arg0.getId());
				textNoteControls.setVisibility(View.GONE);
				System.out.println("BACK MODE");
				updateButtonUI(-1);
				imageButtonsquence.setVisibility(View.VISIBLE);


			}
		});
		textButtondrawnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// BULLET MODE
				updateTextNoteControlListners(arg0.getId());
				System.out.println("BULLET  MODE");

			}
		});
		textButtondrawdraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// NUMBERING MODE
				updateTextNoteControlListners(arg0.getId());
				System.out.println("NUMBERING  MODE");
			}
		});
		textButtondrawcolors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

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
				// ITIELIC MODE
				//
				 updateTextNoteControlListners(arg0.getId());
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
				// MORE MODE
				updateTextNoteControlListners(arg0.getId());
				System.out.println("MORE  MODE");
			}
		});*/

		/*boolean change = true;
		if (change) {
			// onclick
			// dark color
		}
		// false
		if (!change) {
			// remove onclick
			// same color
		}
		// true*/

		// redo
		redo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).redo();
				//updateTextNoteControlListners(R.id.action_redo);
			}
		});

		// undo
		undo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).undo();
				//updateTextNoteControlListners(R.id.action_undo);
			}
		});

		// bold
		bold.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 allRe.get(Integer.parseInt(v.getTag().toString())).setBold();
				//updateTextNoteControlListners(R.id.action_bold);
			}
		});

		// italic
		italic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setItalic();
				//updateTextNoteControlListners(R.id.action_italic);
			}
		});

		// underline
		underline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setUnderline();
				//updateTextNoteControlListners(R.id.action_underline);
			}
		});

		// h1
		h1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(1);
				//updateTextNoteControlListners(R.id.action_heading1);
			}
		});

		// h2
		h2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(2);
				//updateTextNoteControlListners(R.id.action_heading2);
			}
		});

		// h3
		h3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(3);
				//updateTextNoteControlListners(R.id.action_heading3);
			}
		});

		// h4
		h4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(4);
				//updateTextNoteControlListners(R.id.action_heading4);
			}
		});

		// h5
		h5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(5);
				//updateTextNoteControlListners(R.id.action_heading5);
			}
		});

		// h6
		h6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(6);
				//updateTextNoteControlListners(R.id.action_heading6);
			}
		});

		// align left
		align_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setAlignLeft();
				//updateTextNoteControlListners(R.id.action_align_left);
			}
		});

		// align center
		align_center.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setAlignCenter();
				//updateTextNoteControlListners(R.id.action_align_center);
			}
		});

		// align right
		align_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				allRe.get(Integer.parseInt(v.getTag().toString())).setAlignRight();
				//updateTextNoteControlListners(R.id.action_align_right);
			}
		});

	}

	void addScribbleControlListners() {
		imageButtondrawback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
				updateScribbleControlListners(v.getId());
				showNewDrawingDialog();

			}
		});
		imageButtonbrushdraw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateScribbleControlListners(v.getId());
				//drawView.setErase(false);
				//drawView.setBrushSize(drawView.getLastBrushSize());
				//onCreateBrushDialog();
				showScribbleDialog("brush");
				openBrush();
				updateScribbleButtonColor("brush");
			}
		});
		imageButtondrawcolors.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateScribbleControlListners(v.getId());
				//showColorAlert("", NoteMainActivity.this);
			}
		});
		imageButtonhighlightdraw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateScribbleControlListners(v.getId());
				//showBrushSizeDialog(false);
				//showHighLightAlert("", NoteMainActivity.this);
				showScribbleDialog("highlight");
				openHighlight();
				updateScribbleButtonColor("highlight");
			}
		});
		imageButtondrawerase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateScribbleControlListners(v.getId());
				// showEraserDialog();
				//showBrushSizeDialog(true);
				//onCreateEraserDialog();
				showScribbleDialog("eraser");
				openEraser();
				updateScribbleButtonColor("eraser");
			}
		});
		imageButtondrawMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateScribbleControlListners(v.getId());

			}
		});
	}

	void updateTextNoteControlListners(int elementId) {

		bold.setBackgroundColor(getResources().getColor(R.color.header_bg));
		italic.setBackgroundColor(getResources().getColor(R.color.header_bg));
		underline.setBackgroundColor(getResources().getColor(R.color.header_bg));
		h1.setBackgroundColor(getResources().getColor(R.color.header_bg));
		h2.setBackgroundColor(getResources().getColor(R.color.header_bg));
		h3.setBackgroundColor(getResources().getColor(R.color.header_bg));
		h4.setBackgroundColor(getResources().getColor(R.color.header_bg));
		h5.setBackgroundColor(getResources().getColor(R.color.header_bg));
		h6.setBackgroundColor(getResources().getColor(R.color.header_bg));
		align_left.setBackgroundColor(getResources().getColor(R.color.header_bg));
		align_center.setBackgroundColor(getResources().getColor(R.color.header_bg));
		align_right.setBackgroundColor(getResources().getColor(R.color.header_bg));
		redo.setBackgroundColor(getResources().getColor(R.color.header_bg));
		undo.setBackgroundColor(getResources().getColor(R.color.header_bg));

		switch (elementId) {
			case R.id.action_bold:
				bold.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_italic:
				italic.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_underline:
				underline.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_heading1:
				h1.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_heading2:
				h2.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_heading3:
				h3.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_heading4:
				h4.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_heading5:
				h5.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_heading6:
				h6.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_align_left:
				align_left.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_align_center:
				align_center.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_align_right:
				align_right.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_redo:
				redo.setBackgroundColor(getResources().getColor(R.color.A8b241b));
				break;
			case R.id.action_undo:
				undo.setBackgroundColor(getResources().getColor(R.color.A8b241b));
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
		imageButtonbrushdraw.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtondrawcolors.setBackgroundColor(getResources().getColor(
				R.color.header_bg));
		imageButtonhighlightdraw.setBackgroundColor(getResources().getColor(
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
                imageButtonbrushdraw.setBackgroundColor(getResources().getColor(
						R.color.A8b241b));
				break;
			case R.id.imageButtondrawcolors:
				imageButtondrawcolors.setBackgroundColor(getResources().getColor(
						R.color.A8b241b));
				break;
			case R.id.imageButtonhighlightdraw:
                imageButtonhighlightdraw.setBackgroundColor(getResources()
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

	/*Button buttonLock = (Button) layout_note_more_Info
	.findViewById(R.id.buttonLock);
	Button buttonDelete = (Button) layout_note_more_Info
	.findViewById(R.id.buttonDelete);*/
		Button buttonRemind = (Button) layout_note_more_Info
				.findViewById(R.id.btnRemind);
	/*Button buttonTimeBomb = (Button) layout_note_more_Info
	.findViewById(R.id.buttonTimeBomb);
	Button buttonAttach = (Button) layout_note_more_Info
	.findViewById(R.id.buttonAttach);*/

	/*buttonLock.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View arg0) {
	System.out.println("button lock");
	}
	});
	buttonDelete.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View arg0) {
	System.out.println("button delete");
	}
	});*/
	/*buttonRemind.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View arg0) {
	finish();
	System.out.println("button remind");
	}
	});*/
	/*buttonTimeBomb.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View arg0) {
	System.out.println("button time bomb");
	}
	});
	buttonAttach.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View arg0) {
	System.out.println("button attached");
	}
	});
*/
	}

	/************* main list control Here ************/
	void addlistners() {

		//imageButtoncalander.setVisibility(View.VISIBLE); //changed by Jay

		// #NOTE ITEM CLCIK

	/*listviewNotes.setOnItemClickListener(new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	long arg3) {
	NoteListDataModel notelistitem = arrNoteListData.get(arg2);
	Toast.makeText(getApplicationContext(), "clicke",
	Toast.LENGTH_SHORT).show();
	Log.v("GET ITEM", "seletecd item" + arg2 + "TYPE:"
	+ notelistitem.noteType);

	switch (notelistitem.noteType) {
	case TEXTMODE: {
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
	});*/

		// #BUTTON CHECKED CLICK TEXTSAVE

		imageButtoncalander.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				layout_note_more_Info.setVisibility(View.GONE);
				imageButtonsquence.setVisibility(View.VISIBLE);
				isMoreShown = false;
				// Save click
				updateHeaderControls(v.getId());

				if (textelementid.size() > 0)
					textelementid.get(0).clearFocus();

				textNoteControls.setVisibility(View.GONE);
				horizontal_scroll_editor.setVisibility(View.GONE);
				isTextmodeSelected=false;

				try  {
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				} catch (Exception e) {}

				//drawView.startNew();
				drawView.setVisibility(View.GONE);
				drawingControls.setVisibility(View.GONE);
				imageButtonHamburg.setVisibility(View.VISIBLE);
				imageButtoncalander.setVisibility(View.GONE);
				imageButtonsquence.setVisibility(View.VISIBLE);
				imageButtoncheckbox.setVisibility(View.VISIBLE);
			}
		});

		imageButtonHamburg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// openSlideMenu();
				isTextmodeSelected = false;
				layout_note_more_Info.setVisibility(View.GONE);
				imageButtonsquence.setVisibility(View.VISIBLE);
				layout_audio_notechooser.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.GONE);
				isMoreShown = false;
				finish();

			}
		});

		imageButtoncheckbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageButtoncalander.setVisibility(View.VISIBLE);
				noteElements = (LinearLayout) findViewById(R.id.noteElements);
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				final View viewChecklist = inflater.inflate(R.layout.note_checklist, null, false);
				LinearLayout checkbox = (LinearLayout) viewChecklist.findViewById(R.id.checkbox);
				final ImageView checklist_icon = (ImageView) viewChecklist.findViewById(R.id.checkboxIcon);
				checklist_icon.setTag("1");
				final EditText checklist_text = (EditText) viewChecklist.findViewById(R.id.checkboxText);
				allCheckboxText.add(checklist_text);
				noteElements.addView(checkbox);

				final boolean[] cb_added = {false};
				final long[] thisnoteelementid = new long[1];

				checklist_text.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (noteIdForDetails == null) {
							makeNote();
						}
						if (noteIdForDetails != null) {
							String updatedText = s.toString();

							if (!cb_added[0]) {
								NoteElement ne = new NoteElement(Long.parseLong(noteIdForDetails), 1, "yes", "checkbox", updatedText);
								ne.save();
								thisnoteelementid[0] = ne.getId();
								cb_added[0] = true;
							}
							if (cb_added[0]) {
								NoteElement ne = NoteElement.findById(NoteElement.class, thisnoteelementid[0]);
								ne.content = s.toString();
								ne.save();
								modifyNoteTime();
							}
						}
					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});

				checklist_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(checklist_text, InputMethodManager.SHOW_IMPLICIT);
					}
				});

				checklist_icon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String tag = v.getTag().toString();
						if (tag.equals("1")) {
							checklist_icon.setImageResource(R.drawable.checkbox_check_sq);
							v.setTag("0");
						} else {
							checklist_icon.setImageResource(R.drawable.checkbox_uncheck_sq);
							v.setTag("1");
						}
					}
				});

			}
		});

		imageButtonsquence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

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
				drawingControls.setVisibility(View.GONE);
				updateButtonUI(R.id.imageButtonAudioMode);
				System.out.println("audio mode");

				audioElement = (LinearLayout) findViewById(R.id.audioRecording);
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				final View viewAudio = inflater.inflate(R.layout.note_audio, null, false);
				LinearLayout note_audio = (LinearLayout) viewAudio.findViewById(R.id.note_audio);
				final ImageView audio_play = (ImageView) viewAudio.findViewById(R.id.audio_play);
				final TextView audio_text = (TextView) viewAudio.findViewById(R.id.audio_text);
				audio_play.setImageResource(R.drawable.stop_audio);

				final TextView record_text = (TextView) viewAudio.findViewById(R.id.record_text);

				audioElement.addView(note_audio);

				try {
					initlizeAudiorecoder();
					myAudioRecorder.prepare();
					myAudioRecorder.start();

					audio_play.setTag(1);

					final Long startTime = System.currentTimeMillis();
					final Date startTimeDate = new Date(startTime);


					//audio_seek.setMax(mp.getDuration() / 1000);
					final Handler mHandler = new Handler();
					// Make sure you update Seekbar on UI thread
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String tag = audio_play.getTag().toString();
							if (myAudioRecorder != null && tag.equals("1")) {
								final Long currentTime = System.currentTimeMillis();
								Date currentTimeDate = new Date(currentTime);

								Long timeDifference = currentTimeDate.getTime() - startTimeDate.getTime();

								long diffSeconds = timeDifference / 1000 % 60;
								long diffMinutes = timeDifference / (60 * 1000) % 60;
								long diffHours = timeDifference / (60 * 60 * 1000) % 24;

								//String duration = "0" + diffHours +":"+ diffMinutes +":"+diffSeconds;
								String duration = String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);

								record_text.setText(String.valueOf(duration));
								record_text.setVisibility(View.VISIBLE);
							}
							mHandler.postDelayed(this, 1000);
							//mHandler.removeCallbacksAndMessages(this);


						}
					});

				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				audio_play.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						audio_play.setImageResource(R.drawable.play_audio);
						myAudioRecorder.stop();
						myAudioRecorder.release();
						myAudioRecorder = null;
						record_text.setVisibility(View.GONE);

						if (noteIdForDetails == null) {
							makeNote();
						}
						if (noteIdForDetails != null) {
							NoteElement ne = new NoteElement(Long.parseLong(noteIdForDetails), 1, "yes", "audio", audioName);
							ne.save();
                            modifyNoteTime();
						}


						Toast.makeText(NoteMainActivity.this, "Recording Saved",
								Toast.LENGTH_SHORT).show();

						System.out.println("Current Index:" + currentAudioIndex);

						audioElement.removeAllViews();
						onResume();

						/*final MediaPlayer mp = new MediaPlayer();
						//final ImageView audio_play = (ImageView) viewAudio.findViewById(R.id.audio_play);
						final SeekBar audio_seek = (SeekBar) viewAudio.findViewById(R.id.audio_seek);
						final TextView audio_text = (TextView) viewAudio.findViewById(R.id.audio_text);
						final ImageView audio_play = (ImageView) viewAudio.findViewById(R.id.audio_play);
						audio_play.setTag(0);

						final File f = new File(outputFile);
						try {
							mp.setDataSource(f.getAbsolutePath());
							mp.prepare();
						} catch (IOException e) {
							e.printStackTrace();
						}

						// Audio Play
						audio_play.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (mp.isPlaying()) {
									mp.pause();
									audio_play.setImageResource(R.drawable.play_audio);
								} else {
									audio_play.setImageResource(R.drawable.pause_audio);
									mp.start();
									audio_seek.setMax(mp.getDuration() / 1000);
									final Handler mHandler1 = new Handler();
									// Make sure you update Seekbar on UI thread
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											if (mp != null) {
												int mCurrentPosition = mp.getCurrentPosition() / 1000;
												String currentduration = getDurationBreakdown(mp.getCurrentPosition());
												String currentduration1 = getDurationBreakdown(mp.getDuration());
												if (mCurrentPosition <= mp.getDuration() / 1000) {
													System.out.println("CurrentDuration:" + currentduration);
													audio_seek.setProgress(mCurrentPosition);
													audio_text.setVisibility(View.VISIBLE);
													audio_text.setText(currentduration + "/" + currentduration1);
												}
											}
											mHandler1.postDelayed(this, 1000);
										}
									});
									mp.setOnCompletionListener(new OnCompletionListener() {
										@Override
										public void onCompletion(MediaPlayer mp) {
											audio_play.setImageResource(R.drawable.play_audio);
										}
									});
								}
							}
						});*/


					}
				});

				//fetchNoteElementsFromDb();
			}
		});
		imageButtonImageMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("image  mode");
				updateButtonUI(R.id.imageButtonImageMode);
				// listviewNotes.setScrollContainer(true);
				// startActivity(new
				// Intent(getApplicationContext(),ImageChooserActivity.class));
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
				System.out.println("paint mode");
				//scrollView.setVisibility(View.INVISIBLE);

				updateButtonUI(R.id.imageButtonPaintMode);

				// startActivity(new
				// Intent(getApplicationContext(),DrawingActivity.class));
				// updatenoteList(NOTETYPE.SCRIBBLEMODE);
				// listviewNotes.setScrollContainer(false);

				// drawView.clear();

				layOutDrawingView.setVisibility(View.VISIBLE);
				drawingControls.setVisibility(View.VISIBLE);

				isTextmodeSelected = false;
				updateScribbleControlListners(R.id.imageButtondrawback);
				imageButtonsquence.setVisibility(View.GONE);
				imageButtoncheckbox.setVisibility(View.GONE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;
				layout_audio_notechooser.setVisibility(View.GONE);
				imageButtonHamburg.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.VISIBLE);
				drawView.setVisibility(View.VISIBLE);
				//drawView.setDrawColor(getResources().getColor(R.color.black));
				drawView.setDrawColor(color_selected);
				drawView.setBrushSize(16);
			}
		});
		imageButtonShareMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawingControls.setVisibility(View.GONE);
				updateButtonUI(R.id.imageButtonShareMode);
				System.out.println("share mode");
				layOutDrawingView.setVisibility(View.GONE);
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
				updateButtonUI(R.id.imageButtonTextMode);
				System.out.println("text mode");
				//layOutDrawingView.setVisibility(View.GONE);
				// startActivity(new
				// Intent(getApplicationContext(),TextChooserActivity.class));

				// updatenoteList(NOTETYPE.TEXTMODE);
				// listviewNotes.setScrollContainer(true);
				//LayoutTextWritingView.setVisibility(View.VISIBLE);
				//isTextmodeSelected = true;

				drawingControls.setVisibility(View.GONE);
				//textNoteControls.setVisibility(View.VISIBLE);
				//imageButtonsquence.setVisibility(View.VISIBLE);
				layout_note_more_Info.setVisibility(View.GONE);
				isMoreShown = false;
				layout_audio_notechooser.setVisibility(View.GONE);
				horizontal_scroll_editor.setVisibility(View.VISIBLE);

				imageButtonHamburg.setVisibility(View.GONE);
				imageButtoncalander.setVisibility(View.VISIBLE);
				imageButtonsquence.setVisibility(View.GONE);
				imageButtoncheckbox.setVisibility(View.GONE);

				noteElements = (LinearLayout) findViewById(R.id.noteElements);
				LayoutInflater inflator = getLayoutInflater();
				viewText = inflator.inflate(R.layout.note_text, null, false);
				final RichEditor editor = (RichEditor) viewText.findViewById(R.id.editor);
				editor.setMinimumHeight(40);
				editor.setEditorHeight(40);
				editor.setBackgroundColor(0);
				noteElements.addView(editor);

				allRe.add(editor);
				editor.setTag(allRe.size() - 1);
				setFeatureTag(String.valueOf(allRe.size() - 1));

				final boolean[] ne_added = {false};
				final long[] thisnoteid = new long[1];
				//editor.setHtml(s);

				//editor.requestFocus();
				if (textelementid.size() == 0)
					textelementid.add(editor);


				editor.focusEditor();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editor, InputMethodManager.SHOW_IMPLICIT);

				// TODO editor up
				editor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						//mEditor.setEditorHeight(400);
						String id = v.getTag().toString();

						setFeatureTag(id);
						drawingControls.setVisibility(View.GONE);
						layout_note_more_Info.setVisibility(View.GONE);
						isMoreShown = false;
						layout_audio_notechooser.setVisibility(View.GONE);
						horizontal_scroll_editor.setVisibility(View.VISIBLE);

						imageButtonHamburg.setVisibility(View.GONE);
						imageButtoncalander.setVisibility(View.VISIBLE);
						imageButtonsquence.setVisibility(View.GONE);
						imageButtoncheckbox.setVisibility(View.GONE);

						if (textelementid.size() > 0)
							textelementid.remove(0);

						textelementid.add(v);

					}
				});

				editor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
					@Override
					public void onTextChange(String s) {
						//editortext[0] = s;
						//String tag = (String) editor.getTag();

						if(noteIdForDetails == null)
							makeNote();

						if (!ne_added[0]) {
							NoteElement ne = new NoteElement(Long.parseLong(noteIdForDetails), 1, "yes", "text", s);
							ne.save();
							thisnoteid[0] = ne.getId();
							ne_added[0] = true;
						}
						if (ne_added[0]) {
							NoteElement ne = NoteElement.findById(NoteElement.class, thisnoteid[0]);
							ne.content = s;
							ne.save();
							modifyNoteTime();
						}
					}
				});
			}
		});


		imageButtonMoreMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				imageButtonsquence.setVisibility(View.VISIBLE);
				drawingControls.setVisibility(View.GONE);
				System.out.println("more mode");
				layOutDrawingView.setVisibility(View.GONE);
				updateButtonUI(R.id.imageButtonMoreMode);
				layout_audio_notechooser.setVisibility(View.GONE);

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

	private void setFeatureTag(String id){

		bold.setTag(Integer.parseInt(id));
		italic.setTag(Integer.parseInt(id));
		underline.setTag(Integer.parseInt(id));
		h1.setTag(Integer.parseInt(id));
		h2.setTag(Integer.parseInt(id));
		h3.setTag(Integer.parseInt(id));
		h4.setTag(Integer.parseInt(id));
		h5.setTag(Integer.parseInt(id));
		h6.setTag(Integer.parseInt(id));
		align_left.setTag(Integer.parseInt(id));
		align_center.setTag(Integer.parseInt(id));
		align_right.setTag(Integer.parseInt(id));
		redo.setTag(Integer.parseInt(id));
		undo.setTag(Integer.parseInt(id));
	}

	/*void updatenoteList(NOTETYPE notetype) {
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
	}*/

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

	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri getOutputMediaFileUri(int mediaType) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		// 3. Create a file name
		// 4. Create the file
		String imgDir = "NoteShare Images";
		String appName = "NoteShare";
		appName = "../" + appName;
		String p = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString().trim();
		File mediaStorageDir = new File(p, appName);
		String path = mediaStorageDir.getPath() + File.separator + imgDir + File.separator;
		String timestamp = String.valueOf(System.currentTimeMillis());
		if (mediaType == MEDIA_TYPE_IMAGE) {
			mediaStorageDir = new File(path + "IMG-" + timestamp + ".jpg");
		}
		else {
			return null;
		}

		Log.d("", "File: " + Uri.fromFile(mediaStorageDir));

		// 5. Return the file's URI
		return Uri.fromFile(mediaStorageDir);
	}

	Uri mMediaUri;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		if (resultCode == RESULT_OK) {
			if (requestCode == TAKE_PHOTO_REQUEST) {

				/*int rotate = 0;
				int orientation = 0;
				String timestamp = String.valueOf(System.currentTimeMillis());
				String imagePath = Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Images/IMG-" + timestamp + ".jpg";
				try {
					getContentResolver().notifyChange(mMediaUri, null);
					File imageFile = new File(imagePath);
					ExifInterface exif = new ExifInterface(
							imageFile.getAbsolutePath());
					orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);
					switch (orientation) {
						case ExifInterface.ORIENTATION_ROTATE_270:
							rotate = 270;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							rotate = 180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_90:
							rotate = 90;
							break;
					}
					Log.v("", "Exif orientation: " + orientation);
				} catch (Exception e) {
					e.printStackTrace();
				}

				*//****** Image rotation ****//*
				Matrix matrix = new Matrix();
				matrix.postRotate(orientation);*/
				//Bitmap cropped = Bitmap.createBitmap(scaled, x, y, width, height, matrix, true);

				//finish();
				Intent cameraIntent = new Intent(NoteMainActivity.this, CameraImage.class);
				cameraIntent.putExtra("image", mMediaUri.toString());
				//send noteid
				if(noteIdForDetails == null)
					cameraIntent.putExtra("a", 0);
				else
					cameraIntent.putExtra("a", 1);

				cameraIntent.putExtra("noteid", noteIdForDetails);
				cameraIntent.putExtra("check", 0);
				//cameraIntent.putExtra()
				startActivity(cameraIntent);

				/*noteElements = (LinearLayout) findViewById(R.id.noteElements);
				LayoutInflater inflator = LayoutInflater.from(getApplicationContext());
				View viewImage = inflator.inflate(R.layout.note_image, null, false);
				LinearLayout note_image = (LinearLayout) viewImage.findViewById(R.id.note_image);
				ImageView note_imageview = (ImageView) note_image.findViewById(R.id.note_imageview);
				noteElements.addView(note_imageview);*/

	/*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	String imgDir = "../NoteShare/NoteShare Images/";
	String timestamp = String.valueOf(System.currentTimeMillis());
	File file = new File(Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.getDataDirectory())),
	imgDir + "IMG-" + timestamp + ".jpg");
	File storageDir = Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.getDataDirectory()));

	try {
	//thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
	*//*File image = File.createTempFile(imgDir + "IMG-" + timestamp, ".jpg", storageDir);
	thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(image));*//*
	ContentValues values = new ContentValues();
	values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
	values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar isso
	getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

	Intent i = new Intent(this, CameraImage.class);
	startActivity(i);
	//camera_image.setImageBitmap(thumbnail);
	} catch (Exception e) {
	e.printStackTrace();
	}*/
			} else if (requestCode == SELECT_PICTURE) {
				Uri imagePath = data.getData();
				Intent selectIntent = new Intent(NoteMainActivity.this, CameraImage.class);
				selectIntent.putExtra("select_image", imagePath.toString());
				selectIntent.putExtra("noteid", noteIdForDetails);

				if(noteIdForDetails == null)
					selectIntent.putExtra("a", 0);
				else
					selectIntent.putExtra("a", 1);

				selectIntent.putExtra("check", 1);
				Log.e("select pic", imagePath.toString());
				//cameraIntent.putExtra()
				startActivity(selectIntent);
			}
		}

	/*if (resultCode == RESULT_OK) {
	imageButtoncalander.setVisibility(View.VISIBLE);

	if (requestCode == REQUEST_CAMERA) {
	Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

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
	}*/

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

		colorbutton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});

		colorbutton8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});

		colorbutton9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});
		colorbutton10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paintClicked(v);
			}
		});

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("SELECT COLOR");
		textViewTitleAlert.setTextColor(Color.WHITE);

		dialogColor.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogColor.setContentView(contentView);
		dialogColor.show();
	}

    void showHighLightAlert(String message, Context context) {

        dialogColor = new Dialog(context);
        dialogColor.setCanceledOnTouchOutside(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.highlightcolor, null, false);
        LinearLayout paintHighlight1 = (LinearLayout) contentView.findViewById(R.id.paint_highlight1);
        LinearLayout paintHighlight2 = (LinearLayout) contentView.findViewById(R.id.paint_highlight2);
        // currPaint = (ImageButton) paintLayout.getChildAt(0);
        // currPaint.setImageDrawable(getResources().getDrawable(
        // R.drawable.paint_pressed));

        ImageButton highlightbutton1 = (ImageButton) paintHighlight1
                .findViewById(R.id.hightlightbutton1);
        ImageButton highlightbutton2 = (ImageButton) paintHighlight1
                .findViewById(R.id.hightlightbutton2);
        ImageButton highlightbutton3 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton3);
        ImageButton highlightbutton4 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton4);
        ImageButton highlightbutton5 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton5);
        ImageButton highlightbutton6 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton6);
        ImageButton highlightbutton7 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton7);
        ImageButton highlightbutton8 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton8);
        ImageButton highlightbutton9 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton9);
        ImageButton highlightbutton10 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton10);

        highlightbutton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });

        highlightbutton8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });

        highlightbutton9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });
        highlightbutton10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                paintClicked(v);
            }
        });

        TextView textViewTitleAlert = (TextView) contentView
                .findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText("SELECT COLOR");
        textViewTitleAlert.setTextColor(Color.WHITE);

        dialogColor.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
				dialog.dismiss();

			}
		});
		textViewTitleTakePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// System.exit(0);
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

				//captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				if (mMediaUri == null) {
					// display an error
					Toast.makeText(NoteMainActivity.this, "", Toast.LENGTH_LONG).show();
				}
				else {
					takePhotoIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mMediaUri);
					startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
				}
	/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	startActivityForResult(intent, REQUEST_CAMERA);*/
				dialog.dismiss();
			}
		});

		textViewTitlechosserfromgallary
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
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
		dialog.setCancelable(true);
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
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(contentView);
		dialog.show();

	}

	/************* show brush control Here ************/
	void showBrushSizeDialog(boolean iserase) {

		brushDialog1 = new Dialog(NoteMainActivity.this);
		isErase = iserase;
		brushDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		brushDialog1.setCancelable(true);
		brushDialog1.setContentView(R.layout.brush_chooser);
		TextView textViewSizesHeader = (TextView) brushDialog1
				.findViewById(R.id.textViewSizesHeader);
		if (iserase == true) {
			textViewSizesHeader.setText("ERASE SIZES");
		} else {
			textViewSizesHeader.setText("BRUSH SIZES");
		}

	/*ImageButton smallBtn = (ImageButton) brushDialog1
	.findViewById(R.id.small_brush);
	ImageButton smallBtn_12 = (ImageButton) brushDialog1
	.findViewById(R.id.small_brush_12);
	ImageButton smallBtn_14 = (ImageButton) brushDialog1
	.findViewById(R.id.small_brush_14);
	ImageButton smallBtn_16 = (ImageButton) brushDialog1
	.findViewById(R.id.small_brush_16);
	ImageButton smallBtn_18 = (ImageButton) brushDialog1
	.findViewById(R.id.small_brush_18);*/

	/*smallBtn.setOnClickListener(new OnClickListener() {

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
	});*/

		brushDialog1.show();

	}

	/************* Erase control Here ************/
	/*void showEraserDialog() {

	final Dialog brushDialog1 = new Dialog(NoteMainActivity.this);

	brushDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
	brushDialog1.setCancelable(true);
	brushDialog1.setContentView(R.layout.brush_chooser);

	TextView textViewSizesHeader = (TextView) brushDialog1
	.findViewById(R.id.textViewSizesHeader);

	textViewSizesHeader.setText("ERASER SIZES");

	*//*ImageButton smallBtn = (ImageButton) brushDialog1
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
	});*//*
	brushDialog1.show();

	}*/


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
		//final ListView ListViewItems = (ListView) dialog
				//.findViewById(R.id.ListViewItems);

		//final Button buttonFont = (Button) dialog.findViewById(R.id.buttonFont);
		//final Button buttonSize = (Button) dialog.findViewById(R.id.buttonSize);
		final Button buttonColors = (Button) dialog.findViewById(R.id.buttonColors);
		final Button buttonPaper = (Button) dialog.findViewById(R.id.buttonPaper);

		layoutPapers.setVisibility(View.GONE);
		layoutColors1.setVisibility(View.VISIBLE);

		buttonColors.setBackgroundColor(getResources().getColor(
				R.color.eaeaea));
		buttonColors.setTextColor(getResources().getColor(
				R.color.header_bg));

		//ListViewItems.setVisibility(View.VISIBLE);

		// fonts_sizeName=getResources().getStringArray(R.array.Font_Size_px);
		//fontSizes = getResources().getStringArray(R.array.Font_Size);

		/*TextFont_sizeAdapter = new TextFont_Size_ChooseAdapter(
				NoteMainActivity.this, fonts_Name_Display);// fonts_sizeName
		ListViewItems.setAdapter(TextFont_sizeAdapter);
		DataManager.sharedDataManager().setSELECTED_TEXT_OPTION(NOTETYPE.FONT);*/

		/*buttonFont.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
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
		});*/
		buttonColors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				layoutPapers.setVisibility(View.GONE);
				layoutColors1.setVisibility(View.VISIBLE);
				//ListViewItems.setVisibility(View.GONE);

				/*buttonFont.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonFont
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonSize.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonSize
						.setTextColor(getResources().getColor(R.color.ffffff));*/

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
				layoutPapers.setVisibility(View.VISIBLE);
				layoutColors1.setVisibility(View.GONE);
				//ListViewItems.setVisibility(View.GONE);

				/*buttonFont.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonFont
						.setTextColor(getResources().getColor(R.color.ffffff));

				buttonSize.setBackgroundColor(getResources().getColor(
						R.color.header_bg));
				buttonSize
						.setTextColor(getResources().getColor(R.color.ffffff));*/

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

		/*ListViewItems.setOnItemClickListener(new OnItemClickListener() {

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
		});*/

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
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				paperButtonSelected(arg0);
				dialog.dismiss();
			}
		});
		paper_bg_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
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
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});

		color_bg_8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});
		color_bg_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorButtonSelected(v);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

    Dialog scribbleDialog;
    ImageButton buttonHighlight, buttonBrush, buttonEraser;

    // TODO highlight + brush + eraser dialog
    public void showScribbleDialog(String name) {
        scribbleDialog = new Dialog(NoteMainActivity.this);
        scribbleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        scribbleDialog.setCanceledOnTouchOutside(true);
        scribbleDialog.setContentView(R.layout.scribble_dialog);

        final LinearLayout layoutHighlight = (LinearLayout) scribbleDialog.findViewById(R.id.highlightView);
        final LinearLayout layoutBrush = (LinearLayout) scribbleDialog.findViewById(R.id.brushView);
        final LinearLayout layoutEraser = (LinearLayout) scribbleDialog.findViewById(R.id.eraserView);

        buttonHighlight = (ImageButton) scribbleDialog.findViewById(R.id.buttonHighlight);
        buttonBrush = (ImageButton) scribbleDialog.findViewById(R.id.buttonBrush);
        buttonEraser = (ImageButton) scribbleDialog.findViewById(R.id.buttonEraser);

        layoutHighlight.setVisibility(View.GONE);
        layoutBrush.setVisibility(View.GONE);
        layoutEraser.setVisibility(View.GONE);

        if (name.equals("highlight"))
            layoutHighlight.setVisibility(View.VISIBLE);
        else if (name.equals("brush"))
            layoutBrush.setVisibility(View.VISIBLE);
        else if (name.equals("eraser"))
            layoutEraser.setVisibility(View.VISIBLE);

        buttonHighlight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                layoutHighlight.setVisibility(View.VISIBLE);
                layoutBrush.setVisibility(View.GONE);
                layoutEraser.setVisibility(View.GONE);
                openHighlight();
				updateScribbleButtonColor("highlight");
            }
        });

        buttonBrush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                layoutHighlight.setVisibility(View.GONE);
                layoutBrush.setVisibility(View.VISIBLE);
                layoutEraser.setVisibility(View.GONE);
                openBrush();
				updateScribbleButtonColor("brush");
            }
        });

        buttonEraser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutHighlight.setVisibility(View.GONE);
                layoutBrush.setVisibility(View.GONE);
                layoutEraser.setVisibility(View.VISIBLE);
                openEraser();
				updateScribbleButtonColor("eraser");
            }
        });

        // Highlight
        LinearLayout paintHighlight1 = (LinearLayout) layoutHighlight.findViewById(R.id.paint_highlight1);
        LinearLayout paintHighlight2 = (LinearLayout) layoutHighlight.findViewById(R.id.paint_highlight2);

        ImageButton highlightbutton1 = (ImageButton) paintHighlight1
                .findViewById(R.id.hightlightbutton1);
        ImageButton highlightbutton2 = (ImageButton) paintHighlight1
                .findViewById(R.id.hightlightbutton2);
        ImageButton highlightbutton3 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton3);
        ImageButton highlightbutton4 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton4);
        ImageButton highlightbutton5 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton5);
        ImageButton highlightbutton6 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton6);
        ImageButton highlightbutton7 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton7);
        ImageButton highlightbutton8 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton8);
        ImageButton highlightbutton9 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton9);
        ImageButton highlightbutton10 = (ImageButton) paintHighlight2
                .findViewById(R.id.highlightbutton10);

        highlightbutton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });
        highlightbutton10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightClicked(v);
            }
        });

        // Brush
        colorPickerSeekBar = (ColorPickerSeekBar) scribbleDialog.findViewById(R.id.colorpicker);

        final View v = scribbleDialog.findViewById(R.id.view);
        SeekBar sizeSeekBar = (SeekBar) scribbleDialog.findViewById(R.id.sizeSeekBar);
        sizeSeekBar.setMax(14);
        sizeSeekBar.setProgress(lastBrushSize);

		if(count == 0) {
			drawView.setBrushSize(10 + (lastBrushSize * 2));
			drawView.setDrawColor(Color.parseColor("#000000"));
			colorPickerSeekBar.setProgress(0);
		}

        final TextView tvBrushSize = (TextView) scribbleDialog.findViewById(R.id.tvBrushSize);
        String size = String.valueOf(10 + (lastBrushSize * 2));
        tvBrushSize.setText(size);

        drawView.setBrushSize(10 + (lastBrushSize * 2));

        if (count > 0) {
            v.setBackgroundColor(color_selected);
        }
        colorPickerSeekBar.setProgress(lastBrushColor);

		//int currentColor = drawView.getCurrentPaintColor();
		//drawView.setDrawColor();

        colorPickerSeekBar.setOnColorSeekbarChangeListener(new ColorPickerSeekBar.OnColorSeekBarChangeListener() {
            @Override
            public void onColorChanged(SeekBar seekBar, int color, boolean b) {
                v.setBackgroundColor(color);
				color_selected = color;


				for(int i=0; i <20;i++)
					Log.e("ColorSelected: ", String.valueOf(color_selected));


				drawView.setDrawColor(color);
                lastBrushColor = seekBar.getProgress();
                count++;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                lastBrushSize = progress;
                drawView.setBrushSize(10 + (progress * 2));
                String size = String.valueOf(10 + (lastBrushSize * 2));
                tvBrushSize.setText(size);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Eraser
        SeekBar sizeEraserSeekBar = (SeekBar) scribbleDialog.findViewById(R.id.eraser_sizeSeekBar);
        sizeEraserSeekBar.setMax(14);
        sizeEraserSeekBar.setProgress(lastBrushSize);

        final TextView tvEraserSize = (TextView) scribbleDialog.findViewById(R.id.tvEraserSize);
        String esize = String.valueOf(10 + (lastBrushSize * 2));
        tvEraserSize.setText(esize);

		/*drawView.setBrushSize(10 + (lastBrushSize * 2));
		drawView.setDrawColor(Color.parseColor("#FFFFFF"));*/

        sizeEraserSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				lastBrushSize = progress;
				drawView.setBrushSize(10 + (progress * 2));
				String esize = String.valueOf(10 + (lastBrushSize * 2));
				tvEraserSize.setText(esize);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

        scribbleDialog.show();
    }

    // TODO highlighter clicked
    public void highlightClicked(View view) {
        // use chosen color
        if (view != currPaint) {
            // update color
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();

            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            // currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            // currPaint = (ImageButton) view;
			System.out.println("selected color:" + color);

            int colorCode = Color.parseColor(color);
			scribbleDialog.dismiss();
            drawView.setDrawColor(colorCode);
        }

    }
    // TODO open highlight
    public void openHighlight() {
		drawView.onClickEraser(1);
		//drawView.setDrawColor(Color.parseColor("#00"));
        buttonHighlight.setBackgroundColor(getResources().getColor(R.color.eaeaea));
        buttonHighlight.setImageResource(R.drawable.scrbble_highlight_red);
        buttonBrush.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonBrush.setImageResource(R.drawable.scrbble_draw);
        buttonEraser.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonEraser.setImageResource(R.drawable.scrbble_erase);
    }
    // TODO open brush
    public void openBrush() {
		drawView.setDrawColor(color_selected);
		drawView.onClickEraser(1);
		//drawView.setBrushSize(lastBrushSize);
        buttonHighlight.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonHighlight.setImageResource(R.drawable.scrbble_highlight);
        buttonBrush.setBackgroundColor(getResources().getColor(R.color.eaeaea));
        buttonBrush.setImageResource(R.drawable.scrbble_draw_red);
        buttonEraser.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonEraser.setImageResource(R.drawable.scrbble_erase);
    }
    // TODO open eraser
    public void openEraser() {
		//drawView.setDrawColor(Color.parseColor("#FFFFFF"));
		drawView.setDrawColor(Color.TRANSPARENT);
		drawView.onClickEraser(0);
        buttonHighlight.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonHighlight.setImageResource(R.drawable.scrbble_highlight);
        buttonBrush.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonBrush.setImageResource(R.drawable.scrbble_draw);
        buttonEraser.setBackgroundColor(getResources().getColor(R.color.eaeaea));
        buttonEraser.setImageResource(R.drawable.scrbble_erase_red);
    }
	// TODO updateScribbleButtonColor
	public void updateScribbleButtonColor(String name) {
		if (name.equals("highlight")) {
			imageButtonhighlightdraw.setBackgroundColor(getResources().getColor(R.color.A8b241b));
			imageButtonbrushdraw.setBackgroundColor(getResources().getColor(R.color.header_bg));
			imageButtondrawerase.setBackgroundColor(getResources().getColor(R.color.header_bg));
		}
		if (name.equals("brush")) {
			imageButtonhighlightdraw.setBackgroundColor(getResources().getColor(R.color.header_bg));
			imageButtonbrushdraw.setBackgroundColor(getResources().getColor(R.color.A8b241b));
			imageButtondrawerase.setBackgroundColor(getResources().getColor(R.color.header_bg));
		}
		if (name.equals("eraser")) {
			imageButtonhighlightdraw.setBackgroundColor(getResources().getColor(R.color.header_bg));
			imageButtonbrushdraw.setBackgroundColor(getResources().getColor(R.color.header_bg));
			imageButtondrawerase.setBackgroundColor(getResources().getColor(R.color.A8b241b));
		}
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
				drawView.startNew();
				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
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

				Date date = new Date();

				drawingControls.setVisibility(View.GONE);
				layOutDrawingView.setVisibility(View.GONE);
				updateButtonUI(-1);

				drawView.setDrawingCacheEnabled(true);

	/*String imgSaved = MediaStore.Images.Media.insertImage(
	getContentResolver(), drawView.getDrawingCache(), *//*UUID.randomUUID().toString()*//*
	"IMG" + new Timestamp(date.getTime()), "drawing");
	System.out.println("the string uri:" + imgSaved);*/

				String timestamp = String.valueOf(System.currentTimeMillis());
				File file = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + "IMG-" + timestamp + ".jpg");
				File file2 = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + "IMG-" + timestamp + ".png");

				try {
					drawView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
					drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file2));

					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
					values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar isso
					getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

					ContentValues values2 = new ContentValues();
					values2.put(MediaStore.Images.Media.DATA, file2.getAbsolutePath());
					values2.put(MediaStore.Images.Media.MIME_TYPE, "image/png"); // setar isso
					getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values2);


				} catch (Exception e) {
					e.printStackTrace();
				}

	/*ContentResolver cr = getContentResolver();

	private void addImageGallery(File f) {
	ContentValues values = new ContentValues();
	values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
	values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar isso
	getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}
*/

				if (file != null) {
					Toast.makeText(getApplicationContext(),
							"Drawing saved to Gallery!", Toast.LENGTH_SHORT).show();
					// savedToast.show();
					drawView.destroyDrawingCache();

	/*if (DataManager.sharedDataManager().getArrNoteListData() != null) {
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

	}*/

					drawView.setUserDrawn(false);

				} else {
					Toast.makeText(getApplicationContext(),
							"Oops! Image could not be saved.",
							Toast.LENGTH_SHORT).show();
				}

				dialog.dismiss();
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(contentView);
		dialog.show();

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		// if (drawView.getUserDrawn() == true) {
		// SaveDrawingDialog();
		// } else {

		finish();

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
		paintClicked(v);
	}

	static String backgroundColor = "#FFFFFF";

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
		backgroundColor = view.getTag().toString();

		if (noteIdForDetails == null) {
			makeNote();
		}

		if (noteIdForDetails != null) {
			Note note = Note.findById(Note.class, Long.parseLong(noteIdForDetails));
			note.setColor(backgroundColor);
            note.setModificationtime(currentDateStr);
			note.save();
		}

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

//	String strFaimly = NoteShareFonts.arial;

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

	/*private String getNextFileName() {
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
			intent.setDataAndType(Uri.fromFile(file), "audio*//*");
			startActivity(intent);
		}
	}*/

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
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3)
		{}

		@Override
		public void onTextChanged(CharSequence s, int a, int b, int c) {

			txtViewer.setText(s);

			if(a == 9){
				Toast.makeText(getApplicationContext(), "Maximum Limit Reached", Toast.LENGTH_SHORT).show();
			}
		}};

	public void onCreateBrushDialog() {

		Dialog brushDialog = new Dialog(NoteMainActivity.this);
		brushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		brushDialog.setCancelable(true);
		brushDialog.setContentView(R.layout.brush_chooser);

		final View v = brushDialog.findViewById(R.id.view);

		TextView textViewSizesHeader = (TextView) brushDialog.findViewById(R.id.textViewSizesHeader);
		textViewSizesHeader.setText("BRUSH SIZES");
		colorPickerSeekBar = (ColorPickerSeekBar) brushDialog.findViewById(R.id.colorpicker);

		SeekBar sizeSeekBar = (SeekBar) brushDialog.findViewById(R.id.sizeSeekBar);
		sizeSeekBar.setMax(14);
		sizeSeekBar.setProgress(lastBrushSize);

		final TextView tvBrushSize = (TextView) brushDialog.findViewById(R.id.tvBrushSize);
		String size = String.valueOf(10 + (lastBrushSize * 2));
		tvBrushSize.setText(size);

		drawView.setBrushSize(10 + (lastBrushSize * 2));

		if (count > 0) {
			v.setBackgroundColor(color_selected);
		}
		colorPickerSeekBar.setProgress(lastBrushColor);

		colorPickerSeekBar.setOnColorSeekbarChangeListener(new ColorPickerSeekBar.OnColorSeekBarChangeListener() {
			@Override
			public void onColorChanged(SeekBar seekBar, int color, boolean b) {
				v.setBackgroundColor(color);
				color_selected = color;
				drawView.setDrawColor(color);
				lastBrushColor = seekBar.getProgress();
				count++;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				lastBrushSize = progress;
				drawView.setBrushSize(10 + (progress * 2));
				String size = String.valueOf(10 + (lastBrushSize * 2));
				tvBrushSize.setText(size);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		brushDialog.show();
	}
	int lastBrushColor = 0,count = 0, lastBrushSize = 3 ;

	/*public void onCreateEraserDialog() {
		Dialog brushDialog = new Dialog(NoteMainActivity.this);
		brushDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		brushDialog.setCancelable(true);
		brushDialog.setContentView(R.layout.eraser_chooser);
		final View v = brushDialog.findViewById(R.id.view);
		TextView textViewSizesHeader = (TextView) brushDialog.findViewById(R.id.textViewSizesHeader);
		textViewSizesHeader.setText("ERASER SIZES");

		SeekBar sizeSeekBar = (SeekBar) brushDialog.findViewById(R.id.sizeSeekBar);
		sizeSeekBar.setMax(14);
		sizeSeekBar.setProgress(lastBrushSize);

		final TextView tvBrushSize = (TextView) brushDialog.findViewById(R.id.tvBrushSize);
		String size = String.valueOf(10 + (lastBrushSize * 2));
		tvBrushSize.setText(size);
		drawView.setBrushSize(10 + (lastBrushSize * 2));
		drawView.setDrawColor(Color.parseColor("#FFFFFF"));

		sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				lastBrushSize = progress;
				drawView.setBrushSize(10 + (progress * 2));
				String size = String.valueOf(10 + (lastBrushSize * 2));
				tvBrushSize.setText(size);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		brushDialog.show();
	}*/

	public void remindClick(View v) {
		//String id = v.getTag().toString();
		//setDateTime.showDate(this, id);
		layout_note_more_Info.setVisibility(View.GONE);
		showDate(this);
		//layout_note_more_Info.setVisibility(View.GONE);
	}


	//public void showDate(Context context, final String noteid ){
	public void showDate(Context context){

		move = new Dialog(context);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.datetime, null, false);

		LinearLayout ll = (LinearLayout) findViewById(R.id.layoutAlertbox);
		TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("Date Time");
		textViewTitleAlert.setTextColor(Color.WHITE);

		DatePicker dp = (DatePicker) contentView.findViewById(R.id.dp);
		TimePicker tp = (TimePicker) contentView.findViewById(R.id.tp);


		//final int[1] hour;/* = tp.getCurrentHour();*/
		final int[] time = new int[2];
		time[0] = tp.getCurrentHour();
		time[1] = tp.getCurrentMinute();

		final int[] date = new int[3];
		date[0] = dp.getDayOfMonth();
		date[1] = dp.getMonth() +1;
		date[2] = dp.getYear();


		tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				time[0] = hourOfDay;
				time[1] = minute;
			}
		});

		Button buttonAlertOk = (Button) contentView.findViewById(R.id.buttonAlertOk);
		Button buttonAlertCancel = (Button) contentView.findViewById(R.id.buttonAlertCancel);

		dp.setMinDate(System.currentTimeMillis() - (60 * 48 * 1000));

		dp.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
				//Log.d("tag", "finally found the listener, the date is: year " + year + ", month " + month + ", dayOfMonth " + dayOfMonth);
				date[0] = dayOfMonth;
				date[1] = month + 1;
				date[2] = year;
			}
		});

		buttonAlertOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplication(),"Day: " + date[0] + ", Month: " + date[1] + ", Year: " + date[2] ,Toast.LENGTH_LONG).show();
				//Toast.makeText(getApplication(),"Hour: "+ time[0] + "Minute" + time[1],Toast.LENGTH_LONG).show();

				String reminderTime = check(date[2]) + "-" + check(date[1]) + "-" + check(date[0]) + " " + check(time[0]) + ":" + check(time[1]) + ":00";

/*	Note n = Note.findById(Note.class, Long.valueOf(noteid));
	n.remindertime = reminderTime;
	n.save();*/

				move.dismiss();
				Toast.makeText(getApplication(),"Remind Set: " + reminderTime, Toast.LENGTH_LONG).show();
			}
		});

		buttonAlertCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				move.dismiss();
			}
		});


		move.requestWindowFeature(Window.FEATURE_NO_TITLE);
		move.setCancelable(true);

		move.setContentView(contentView);
		move.show();
	}


	public String check(int value){
		String newvalue;
		if (value < 10) // minute
			newvalue = "0" + String.valueOf(value);
		else
			newvalue =  String.valueOf(value);
		return newvalue;
	}

	public void makeNote() {
		Note note = new Note(textViewheaderTitle.getText().toString(), "", backgroundColor, "", "", "", "#FFFFFF", currentDateStr, currentDateStr, "", "1", 0);
		note.save();
		noteIdForDetails = note.getId().toString();

		Log.e("jay notetitle:", note.getTitle());

		noteTitle[0] = note.getTitle();
	}

    public void modifyNoteTime() {
        Note n = Note.findById(Note.class, Long.parseLong(noteIdForDetails));
        n.modificationtime = currentDateStr;
        n.save();
    }



    public void fetchNoteElementsFromDb() throws FileNotFoundException {

		if(noteIdForDetails != null) {

			noteElements.removeAllViews();
			List<NoteElement> ne = NoteElement.findWithQuery(NoteElement.class, "SELECT * FROM NOTE_ELEMENT WHERE NOTEID = " + Long.parseLong(noteIdForDetails));

			Note note = Note.findById(Note.class, Long.parseLong(noteIdForDetails));

			noteTitle[0] = note.title;

			textViewheaderTitle.setText(note.getTitle());

			String background = note.getColor();
			background_bg.setBackgroundColor(Color.parseColor(background));

			for (final NoteElement n : ne) {
				if (n.type.equals("text")) {
					String s = n.content;
					noteElements = (LinearLayout) findViewById(R.id.noteElements);
					LayoutInflater inflator = getLayoutInflater();
					View viewText = inflator.inflate(R.layout.note_text, null, false);
					final RichEditor editor = (RichEditor) viewText.findViewById(R.id.editor);
					allRe.add(editor);
					editor.setTag(allRe.size() - 1);
					editor.setHtml(s);
					editor.setBackgroundColor(0);
					editor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							String id = v.getTag().toString();
							setFeatureTag(id);
							drawingControls.setVisibility(View.GONE);
							layout_note_more_Info.setVisibility(View.GONE);
							isMoreShown = false;
							layout_audio_notechooser.setVisibility(View.GONE);
							horizontal_scroll_editor.setVisibility(View.VISIBLE);
							imageButtoncalander.setVisibility(View.VISIBLE);
							imageButtonHamburg.setVisibility(View.GONE);

							// TODO hide
							imageButtoncheckbox.setVisibility(View.GONE);
							imageButtonsquence.setVisibility(View.GONE);
							if (textelementid.size()>0)
								textelementid.remove(0);

							textelementid.add(v);
						}
					});

					editor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
						@Override
						public void onTextChange(String s) {
							//editortext[0] = s;
							//String tag = (String) editor.getTag();
							/*NoteElement ne = NoteElement.findById(NoteElement.class, Long.parseLong(n.getId()));
							ne.content = s;
							ne.save();*/
							n.content = s;
							n.save();
                            modifyNoteTime();
						}
					});

					noteElements.addView(editor);
				} else if (n.type.equals("image")) {
					// add image layout
					noteElements = (LinearLayout) findViewById(R.id.noteElements);
					LayoutInflater inflator = LayoutInflater.from(getApplicationContext());
					View viewImage = inflator.inflate(R.layout.note_image, null, false);
					LinearLayout note_image = (LinearLayout) viewImage.findViewById(R.id.note_image);
					ImageView note_imageview = (ImageView) note_image.findViewById(R.id.note_imageview);
					String name = n.content;
					File f = new File(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Images/" + name);
					int deviceWidth = getWindowManager().getDefaultDisplay().getWidth();
					int deviceHeight = getWindowManager().getDefaultDisplay().getHeight();
					Bitmap b = BitmapFactory.decodeFile(String.valueOf(f));
					//Toast.makeText(getApplication(), "Width: " + deviceWidth + ", Height: " + deviceHeight, Toast.LENGTH_LONG).show();
					BitmapFactory.Options op = new BitmapFactory.Options();
					op.inJustDecodeBounds = true;
					int imageWidth = b.getWidth();
					int imageHeight = b.getHeight();
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					//Bitmap scale;
					Bitmap scale = b.createScaledBitmap(b, deviceWidth, deviceHeight, false); // portrait
					//Bitmap scale = b.createScaledBitmap(b, deviceHeight, deviceWidth, false); //landscape
					if (imageWidth > imageHeight) { // landscape
						scale = b.createScaledBitmap(b, deviceHeight, deviceWidth, false);
					} else if (imageWidth < imageHeight) { // portrait
						scale = b.createScaledBitmap(b, deviceWidth, deviceHeight, false);
					}
					note_imageview.setImageBitmap(scale);
					noteElements.addView(note_image);
				} else if (n.type.equals("audio")) {
					// add audio layout
					final String name = n.content;
                    addAudio(name);
				} else if (n.type.equals("checkbox")) {
					String s = n.content;
					noteElements = (LinearLayout) findViewById(R.id.noteElements);
					LayoutInflater inflator = getLayoutInflater();
					View viewChecklist = inflator.inflate(R.layout.note_checklist, null, false);
					LinearLayout checkbox = (LinearLayout) viewChecklist.findViewById(R.id.checkbox);
					final ImageView checklist_icon = (ImageView) viewChecklist.findViewById(R.id.checkboxIcon);
					checklist_icon.setTag("1");
					final EditText checklist_text = (EditText) viewChecklist.findViewById(R.id.checkboxText);
					allCheckboxText.add(checklist_text);
					checklist_text.setText(s);
					//checklist_text.setTag(allCheckboxText.size()-1);
 					noteElements.addView(checkbox);

					checklist_text.addTextChangedListener(new TextWatcher() {
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						}

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							n.content = s.toString();
							n.save();
							modifyNoteTime();
						}

						@Override
						public void afterTextChanged(Editable s) {
						}
					});

					checklist_icon.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String tag = v.getTag().toString();
							if (tag.equals("1")) {
								checklist_icon.setImageResource(R.drawable.checkbox_check_sq);
								v.setTag("0");
							} else {
								checklist_icon.setImageResource(R.drawable.checkbox_uncheck_sq);
								v.setTag("1");
							}
						}
					});

					checklist_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							//String id = v.getTag().toString();
							drawingControls.setVisibility(View.GONE);
							layout_note_more_Info.setVisibility(View.GONE);
							isMoreShown = false;
							layout_audio_notechooser.setVisibility(View.GONE);
							imageButtoncalander.setVisibility(View.VISIBLE);
							imageButtonHamburg.setVisibility(View.GONE);
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.showSoftInput(checklist_text, InputMethodManager.SHOW_IMPLICIT);

							// TODO hide
							imageButtoncheckbox.setVisibility(View.GONE);
							imageButtonsquence.setVisibility(View.GONE);
							if (textelementid.size() > 0)
								textelementid.remove(0);

							textelementid.add(v);
						}
					});
				}
			}
		}
	}

	public void addAudio(String name) {
		noteElements = (LinearLayout) findViewById(R.id.noteElements);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View viewAudio = inflater.inflate(R.layout.note_audio, null, false);
		LinearLayout note_audio = (LinearLayout) viewAudio.findViewById(R.id.note_audio);

		final MediaPlayer mp = new MediaPlayer();
		final ImageView audio_play = (ImageView) viewAudio.findViewById(R.id.audio_play);
		final SeekBar audio_seek = (SeekBar) viewAudio.findViewById(R.id.audio_seek);
		final TextView audio_text = (TextView) viewAudio.findViewById(R.id.audio_text);

		final File f = new File(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Audio/" + name);
		try {
			mp.setDataSource(f.getAbsolutePath());
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Audio Play
		audio_play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mp.isPlaying()) {
					mp.pause();
					audio_play.setImageResource(R.drawable.play_audio);
				} else {
					audio_play.setImageResource(R.drawable.pause_audio);
					mp.start();
					audio_seek.setMax(mp.getDuration() / 1000);
					final Handler mHandler = new Handler();
					// Make sure you update Seekbar on UI thread
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (mp != null) {
								int mCurrentPosition = mp.getCurrentPosition() / 1000;
								String currentduration = getDurationBreakdown(mp.getCurrentPosition());
								String currentduration1 = getDurationBreakdown(mp.getDuration());
								if (mCurrentPosition <= mp.getDuration() / 1000) {
									System.out.println("CurrentDuration:" + currentduration);
									audio_seek.setProgress(mCurrentPosition);
									audio_text.setVisibility(View.VISIBLE);
									audio_text.setText(currentduration + "/" + currentduration1);
								}
							}
							mHandler.postDelayed(this, 1000);
						}
					});
					mp.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							audio_play.setImageResource(R.drawable.play_audio);
						}
					});
				}
			}
		});
		noteElements.addView(note_audio);
	}
}