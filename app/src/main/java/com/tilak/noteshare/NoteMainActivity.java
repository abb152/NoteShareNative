package com.tilak.noteshare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import android.widget.Toast;

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

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAMERA = 2;
    public static String noteIdForDetails;
    static String backgroundColor = "#FFFFFF";
    final Context context = this;
    public ImageButton imageButtonHamburg, imageButtoncalander,
            imageButtonsquence, imageButtoncheckbox;
    public ImageButton imageButtonTextMode, imageButtonImageMode,
            imageButtonPaintMode, imageButtonAudioMode, imageButtonShareMode,
            imageButtonDeleteMode, imageButtonMoreMode;
    public SpannableString spanUpdted, spanold;
    public int typefacae = Typeface.NORMAL;
    public TextView progressRecordtext;
    public RelativeLayout layoutHeader;
    public int currentAudioIndex = 0;
    public LinearLayout noteElements;
    public RelativeLayout noteScribbleElements;
    public ImageButton deleteCheckbox, deleteAudio;
    public LinearLayout layOutDrawingView, textNoteControls,
            layout_note_more_Info, layout_audio_notechooser, audioElement;
    public TextView textViewAdd, textViewDuration;
    public ArrayList<NoteListDataModel> arrNoteListData;
    public ListView listviewNotes;
    public NotesListAdapter adapter;
    public DrawingView drawView;
    public Dialog dialogColor;
    public Dialog move;
    public Dialog brushDialog1;
    public int currentFontSize;
    public String currentFontTypeface;
    public int currentFontColor;
    public LinearLayout drawingControls;
    public RelativeLayout LayoutTextWritingView;
    public HorizontalScrollView horizontal_scroll_editor;
    public ScrollView scrollView;
    public ImageButton imageButtondrawback, imageButtondrawnew,
            imageButtonbrushdraw, imageButtondrawcolors,
            imageButtonhighlightdraw, imageButtondrawerase,
            imageButtondrawMore;
    public ImageButton audioButtondrawback, audioButtondrawnew;
    public boolean isMoreShown = false;
    public boolean isTextmodeSelected = false;
    public boolean isDeleteModeSelected = true;
    public TextFont_Size_ChooseAdapter TextFont_sizeAdapter;
    public String[] fonts_sizeName, fonts_Name_Display, arrStrings;
    public String[] fontSizes;
    public String[] editortext = new String[1];
    public List<View> textelementid = new ArrayList<View>();
    public String[] noteTitle = new String[1];
    public String[] noteCheckText = new String[1];
    MediaPlayer mediaPlayer;// = new MediaPlayer();
    EditText textViewheaderTitle;
    List<ImageView> listImage = new ArrayList<ImageView>();
    //Color picker
    int color_selected = -16777216;
    EditText edittextEditer, txtViewer;
    Button btnAddText;
    ImageButton buttonPlay;
    ImageButton buttonStop;
    ImageButton buttonRecord, buttonPause, buttonRecordPause;
    ProgressBar progressRecord;
    SeekBar progressRecord1;

    public NoteFunctions noteFunctions = new NoteFunctions();

    // /Drawing Controls
    RelativeLayout LayoutAudioRecording;
    ImageButton bold = null, italic = null, underline = null, h1 = null, h2 = null, h3 = null, h4 = null, h5 = null, h6 = null, align_left = null, align_center = null, align_right = null, redo = null, undo = null;
    ImageButton orderedList = null , unorderedList = null;
    View viewText;
    List<RichEditor> allRe = new ArrayList<RichEditor>();
    List<View> allCheckboxText = new ArrayList<View>();
    List<View> allDelete = new ArrayList<View>();
    boolean isErase;
    boolean isPaintMode = false;
    boolean isUnderLine = false, isBold = false, isItalic = false;
    boolean isRecordingAudio = false;
    View contentView;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentDateStr = formatter.format(new Date());
    RelativeLayout background_bg;
    Uri mMediaUri;
    Dialog scribbleDialog;
    View highlightview, brushview, eraserview;
    ImageButton buttonHighlight, buttonBrush, buttonEraser;
    GradientDrawable brushshape;

    // 8b241b selected bg
    int lastBrushSize = 3, lastHighlightSize = 3, lastEraserSize = 3,
            highlightViewSize = (lastHighlightSize * 4 + 20),
            brushViewSize = (lastBrushSize * 4 + 20),
            eraserViewSize = (lastEraserSize * 4 + 20),
            lastBrushColor = 0, count = 0;
    int firstHighlightColor, lastHighlightColor = Color.parseColor("#77FF5B1E");
    TextWatcher watch = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {

            txtViewer.setText(s);

            if (a == 9) {
                Toast.makeText(getApplicationContext(), "Maximum Limit Reached", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private float smallBrush, mediumBrush, largeBrush;
    private AudioRecorder mAudioRecorder;
    private String audioName;
    private String mActiveRecordFileName;
    private ImageButton currPaint;
    private ImageView imageView53;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero!");
        }
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
            //deleteButton();
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
        imageButtonDeleteMode = (ImageButton) contentview
                .findViewById(R.id.imageButtonDeleteMode);

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
                if (!noteTitle[0].equals(updatedText)) {
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
        FileNameGenerator fileNameGenerator = new FileNameGenerator();
        audioName = fileNameGenerator.getFileName("AUDIO");

        outputFile = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/NoteShare/NoteShare Audio/" + audioName;
        ;

        myAudioRecorder = new MediaRecorder();
        //myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        myAudioRecorder.setAudioSamplingRate(44100);
        myAudioRecorder.setAudioEncodingBitRate(320000);
        myAudioRecorder.setAudioChannels(2);

        //myAudioRecorder.setAudioSamplingRate(16000);
        //myAudioRecorder.setAudioChannels(1);

        myAudioRecorder.setOutputFile(outputFile);
    }

    void initlizesTextNoteControls(View contentview) {

        textNoteControls = (LinearLayout) contentview.findViewById(R.id.textNoteControls);
        textNoteControls.setVisibility(View.GONE);

        // bold italic underline ol ul h1 h2 h3 h4 h5 h6 align_left align_center align_right redo undo
        bold = (ImageButton) findViewById(R.id.action_bold);
        italic = (ImageButton) findViewById(R.id.action_italic);
        underline = (ImageButton) findViewById(R.id.action_underline);
        orderedList = (ImageButton) findViewById(R.id.action_ordered);
        unorderedList = (ImageButton) findViewById(R.id.action_unordered);
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

        // redo
        redo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).redo();
            }
        });

        // undo
        undo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).undo();
            }
        });

        // bold
        bold.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setBold();
            }
        });

        // italic
        italic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setItalic();
            }
        });

        // underline
        underline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setUnderline();
            }
        });

        // ordered list
        orderedList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setOrderedList();
            }
        });

        // unordered list
        unorderedList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setUnorderedList();
            }
        });

        // h1
        h1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(1);
            }
        });

        // h2
        h2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(2);
            }
        });

        // h3
        h3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(3);
            }
        });

        // h4
        h4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(4);
            }
        });

        // h5
        h5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(5);
            }
        });

        // h6
        h6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setHeading(6);
            }
        });

        // align left
        align_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setAlignLeft();
            }
        });

        // align center
        align_center.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setAlignCenter();
            }
        });

        // align right
        align_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                allRe.get(Integer.parseInt(v.getTag().toString())).setAlignRight();
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

    /*************
     * moreinfo control Here
     ************/

    Button buttonLock;

    void initlizesMoreInfoView(View contentView) {
        layout_note_more_Info = (LinearLayout) contentView
                .findViewById(R.id.layout_note_more_Info);
        layout_note_more_Info.setVisibility(View.GONE);

	/*Button buttonLock = (Button) layout_note_more_Info
	.findViewById(R.id.buttonLock);
	Button buttonDelete = (Button) layout_note_more_Info
	.findViewById(R.id.buttonDelete);*/
        buttonLock = (Button) layout_note_more_Info.findViewById(R.id.buttonLock);
        Button buttonTimebomb = (Button) layout_note_more_Info.findViewById(R.id.buttonTimebomb);
        Button buttonRemind = (Button) layout_note_more_Info.findViewById(R.id.buttonRemind);
        Button buttonMove = (Button) layout_note_more_Info.findViewById(R.id.buttonMove);
        Button buttonDelete = (Button) layout_note_more_Info.findViewById(R.id.buttonDelete);
        Button buttonShare = (Button) layout_note_more_Info.findViewById(R.id.buttonShare);
    }


    public void move (View v) {
        if (noteIdForDetails == null)
            makeNote();
        noteFunctions.showMoveAlert(this, noteIdForDetails);
    }

    public void timebomb (View v) {
        if (noteIdForDetails == null)
            makeNote();
        noteFunctions.showDate(this, noteIdForDetails, "SET TIMEBOMB", "timebomb");
    }

    public void remindClick(View v) {
        if (noteIdForDetails == null)
            makeNote();
        noteFunctions.showDate(this, noteIdForDetails, "SET REMAINDER", "reminder");
    }

    public void passcode(View v) {
        if (noteIdForDetails == null)
            makeNote();
        noteFunctions.setPasscode(this, noteIdForDetails);
        updateButtonUI(R.id.imageButtonMoreMode);
        if (isMoreShown == false) {
            isMoreShown = true;
            layout_note_more_Info.setVisibility(View.VISIBLE);
        } else {
            isMoreShown = false;
            layout_note_more_Info.setVisibility(View.GONE);
        }
    }

    public void delete(View v) {
        if (noteIdForDetails == null)
            makeNote();
        noteFunctions.showDeleteAlert(this, noteIdForDetails);
        context.startActivity(new Intent(context, MainActivity.class));

    }

    void addlistners() {

        //imageButtoncalander.setVisibility(View.VISIBLE); //changed by Jay

        imageButtoncalander.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                layout_note_more_Info.setVisibility(View.GONE);
                imageButtonsquence.setVisibility(View.VISIBLE);
                isMoreShown = false;
                // Save click
                updateHeaderControls(v.getId());


                if (textelementid.size() > 0) {
                    textelementid.get(0).clearFocus();

                }
                if (allCheckboxText.size() > 0) {
                    allCheckboxText.get(0).clearFocus();
                }

                textNoteControls.setVisibility(View.GONE);
                horizontal_scroll_editor.setVisibility(View.GONE);
                isTextmodeSelected = false;

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                }

                if(isPaintMode){
                    SaveDrawingDialog();
                    isPaintMode = false;
                }
                else{
                    drawView.setVisibility(View.GONE);
                    drawingControls.setVisibility(View.GONE);
                }

                //drawView.startNew();
                /*drawView.setVisibility(View.GONE);
                drawingControls.setVisibility(View.GONE);*/
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
                final RelativeLayout checkbox = (RelativeLayout) viewChecklist.findViewById(R.id.checkbox);
                final ImageView checklist_icon = (ImageView) viewChecklist.findViewById(R.id.checkboxIcon);
                checklist_icon.setTag("0");
                final EditText checklist_text = (EditText) viewChecklist.findViewById(R.id.checkboxText);
                final ImageButton checklistDelete = (ImageButton) viewChecklist.findViewById(R.id.deleteCheckbox);

                allDelete.add(checklistDelete);

                if (allCheckboxText.size() == 0)
                    allCheckboxText.add(checklist_text);

                checklist_text.requestFocus();

                noteElements.addView(checkbox);

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
                                NoteElement ne = new NoteElement(Long.parseLong(noteIdForDetails), 1, "yes", "checkbox", s.toString(), "false", "");
                                ne.save();
                                thisnoteelementid[0] = ne.getId();
                                cb_added[0] = true;
                            }
                            if (cb_added[0]) {
                                NoteElement ne = NoteElement.findById(NoteElement.class, thisnoteelementid[0]);
                                ne.setContent(s.toString());
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
                        if (allCheckboxText.size() > 0)
                            allCheckboxText.remove(0);

                        allCheckboxText.add(v);
                    }
                });

                checklist_icon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = v.getTag().toString();

                        NoteElement ne = NoteElement.findById(NoteElement.class, thisnoteelementid[0]);

                        if (tag.equals("1")) {
                            checklist_icon.setImageResource(R.drawable.checkbox_uncheck_sq);
                            ne.setContentA("false");
                            checklist_text.setPaintFlags(checklist_text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            v.setTag("0");
                        } else {
                            checklist_icon.setImageResource(R.drawable.checkbox_check_sq);
                            ne.setContentA("true");
                            checklist_text.setPaintFlags(checklist_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            v.setTag("1");
                        }
                        ne.save();
                        modifyNoteTime();

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

        imageButtonDeleteMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButton();
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
                final ImageButton audioDelete = (ImageButton) viewAudio.findViewById(R.id.deleteAudio);

                allDelete.add(audioDelete);
                NoteElement ne = null;
                final TextView record_text = (TextView) viewAudio.findViewById(R.id.record_text);

                audioElement.addView(note_audio);

                try {
                    initlizeAudiorecoder();

                    if (noteIdForDetails == null) {
                        makeNote();
                    }
                    if (noteIdForDetails != null) {
                        ne = new NoteElement(Long.parseLong(noteIdForDetails), 1, "yes", "audio", audioName, "false", "");
                        ne.save();
                        modifyNoteTime();
                    }

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

                    //audio_text.setText(myAudioRecorder.get);

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final NoteElement finalNe = ne;
                audio_play.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        audio_play.setImageResource(R.drawable.play_audio);
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        myAudioRecorder = null;
                        record_text.setVisibility(View.GONE);

                        finalNe.contentA = "true";
                        finalNe.save();

                        Toast.makeText(NoteMainActivity.this, "Recording Saved",
                                Toast.LENGTH_SHORT).show();

                        System.out.println("Current Index:" + currentAudioIndex);

                        audioElement.removeAllViews();
                        onResume();
                    }
                });

                //fetchNoteElementsFromDb();
            }
        });
        imageButtonImageMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("image mode");
                updateButtonUI(R.id.imageButtonImageMode);
                // listviewNotes.setScrollContainer(true);
                // startActivity(new
                // Intent(getApplicationContext(),ImageChooserActivity.class));
                isTextmodeSelected = false;
                drawingControls.setVisibility(View.GONE);
                layOutDrawingView.setVisibility(View.GONE);
                //showImageChooserAlertWith("", NoteMainActivity.this);
                //ToDo Image
                Intent intent = new Intent(NoteMainActivity.this, CameraActivity.class);
                if (noteIdForDetails == null)
                    intent.putExtra("isNoteIdNull", true);
                else
                    intent.putExtra("isNoteIdNull", false);

                intent.putExtra("noteid", noteIdForDetails);
                //intent.putExtra("check", 0);
                startActivity(intent);
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
                //scrollView.setVisibility(View.INVISIBLE);

                isPaintMode = true;
                updateButtonUI(R.id.imageButtonPaintMode);
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
                final RelativeLayout textView = (RelativeLayout) viewText.findViewById(R.id.textView);
                final RichEditor editor = (RichEditor) viewText.findViewById(R.id.editor);
                final ImageButton deleteText = (ImageButton) viewText.findViewById(R.id.deleteText);
                editor.setMinimumHeight(40);
                editor.setEditorHeight(40);
                editor.setBackgroundColor(0);
                noteElements.addView(textView);

                allDelete.add(deleteText);
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
                        String plainText = getPlainText(s);

                        if (noteIdForDetails == null)
                            makeNote();

                        if (!ne_added[0]) {
                            NoteElement ne = new NoteElement(Long.parseLong(noteIdForDetails), 1, "yes", "text", s, plainText, "");
                            ne.save();
                            thisnoteid[0] = ne.getId();
                            ne_added[0] = true;
                        }
                        if (ne_added[0]) {
                            NoteElement ne = NoteElement.findById(NoteElement.class, thisnoteid[0]);
                            ne.setContent(s);
                            ne.setContentA(plainText);
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
                    imageButtonMoreMode.setBackgroundColor(getResources().getColor(R.color.A8b241b));
                } else {
                    isMoreShown = false;
                    layout_note_more_Info.setVisibility(View.GONE);
                    imageButtonMoreMode.setBackgroundColor(getResources().getColor(R.color.header_bg));
                }
                imageButtoncalander.setVisibility(View.GONE);
            }
        });

    }

    private void setFeatureTag(String id) {

        bold.setTag(Integer.parseInt(id));
        italic.setTag(Integer.parseInt(id));
        underline.setTag(Integer.parseInt(id));
        orderedList.setTag(Integer.parseInt(id));
        unorderedList.setTag(Integer.parseInt(id));
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
        } else {
            return null;
        }

        Log.d("", "File: " + Uri.fromFile(mediaStorageDir));

        // 5. Return the file's URI
        return Uri.fromFile(mediaStorageDir);
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

    /*************
     * text control Here
     ************/
    void showTextNoteDialog() {

        final Dialog dialog = new Dialog(NoteMainActivity.this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        buttonColors.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                layoutPapers.setVisibility(View.GONE);
                layoutColors1.setVisibility(View.VISIBLE);
                //ListViewItems.setVisibility(View.GONE);

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
        ImageButton highlightbutton6 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton6);
        ImageButton highlightbutton7 = (ImageButton) paintHighlight1
                .findViewById(R.id.highlightbutton7);

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

        final TextView tvHighlightSize = (TextView) layoutHighlight.findViewById(R.id.tvHighlightSize);
        String hsize = String.valueOf(lastHighlightSize);
        tvHighlightSize.setText(hsize);
        SeekBar hightlight_sizeSeekBar = (SeekBar) layoutHighlight.findViewById(R.id.hightlight_sizeSeekBar);
        hightlight_sizeSeekBar.setMax(10);
        hightlight_sizeSeekBar.setProgress(lastHighlightSize);
        highlightview = layoutHighlight.findViewById(R.id.aview);
        LayerDrawable bgDrawable = (LayerDrawable) highlightview.getBackground();
        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.shape_id);
        shape.setColor(lastHighlightColor);
        //aview.setBackgroundColor(lastHighlightColor);

        hightlight_sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lastHighlightSize = progress;
                drawView.setBrushSize((int)(progress * 6.299));
                drawView.setDrawColor(lastHighlightColor);
                String esize = String.valueOf(lastHighlightSize);
                tvHighlightSize.setText(esize);
                //highlightViewSize = (lastHighlightSize * 4 + 20);
                highlightViewSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(progress * 6.299), getResources().getDisplayMetrics());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(highlightViewSize, highlightViewSize);
                params.gravity = Gravity.CENTER;
                highlightview.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Brush
        LinearLayout paint_brush1 = (LinearLayout) layoutBrush.findViewById(R.id.paint_brush1);
        LinearLayout paint_brush2 = (LinearLayout) layoutBrush.findViewById(R.id.paint_brush2);

        ImageButton brushButton1 = (ImageButton) paint_brush1.findViewById(R.id.brushbutton1);
        ImageButton brushButton2 = (ImageButton) paint_brush1.findViewById(R.id.brushbutton2);
        ImageButton brushButton3 = (ImageButton) paint_brush1.findViewById(R.id.brushbutton3);
        ImageButton brushButton4 = (ImageButton) paint_brush1.findViewById(R.id.brushbutton4);
        ImageButton brushButton5 = (ImageButton) paint_brush1.findViewById(R.id.brushbutton5);
        ImageButton brushButton6 = (ImageButton) paint_brush1.findViewById(R.id.brushbutton6);
        ImageButton brushButton7 = (ImageButton) paint_brush2.findViewById(R.id.brushbutton7);
        ImageButton brushButton8 = (ImageButton) paint_brush2.findViewById(R.id.brushbutton8);
        ImageButton brushButton9 = (ImageButton) paint_brush2.findViewById(R.id.brushbutton9);
        ImageButton brushButton10 = (ImageButton) paint_brush2.findViewById(R.id.brushbutton10);
        ImageButton brushButton11 = (ImageButton) paint_brush2.findViewById(R.id.brushbutton11);
        ImageButton brushButton12 = (ImageButton) paint_brush2.findViewById(R.id.brushbutton12);

        brushButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton9.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton10.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton11.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        brushButton12.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                brushClicked(v);
            }
        });

        SeekBar sizeSeekBar = (SeekBar) scribbleDialog.findViewById(R.id.sizeSeekBar);
        sizeSeekBar.setMax(10);
        sizeSeekBar.setProgress(lastBrushSize);

        brushview = layoutBrush.findViewById(R.id.view);
        LayerDrawable brushDrawable = (LayerDrawable) brushview.getBackground();
        brushshape = (GradientDrawable) brushDrawable.findDrawableByLayerId(R.id.shape_id);
        brushshape.setColor(lastBrushColor);
        brushview.setBackground(brushshape);

        if (count == 0) {
            drawView.setBrushSize((int)(lastBrushSize * 6.299));
            drawView.setDrawColor(Color.parseColor("#000000"));
            lastBrushColor = Color.BLACK;
            brushshape.setColor(lastBrushColor);
            brushview.setBackground(brushshape);
        }

        final TextView tvBrushSize = (TextView) scribbleDialog.findViewById(R.id.tvBrushSize);
        String size = String.valueOf(lastBrushSize);
        tvBrushSize.setText(size);
        drawView.setBrushSize((int)(lastBrushSize * 6.299));

        if (count > 0) {
            brushview.setBackgroundColor(color_selected);
        }

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lastBrushSize = progress;
                drawView.setBrushSize((int)(progress * 6.299));
                String tvsize = String.valueOf(lastBrushSize);
                tvBrushSize.setText(tvsize);
                //brushViewSize = (lastBrushSize * 4 + 20);
                brushViewSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(progress * 6.299), getResources().getDisplayMetrics());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(brushViewSize, brushViewSize);
                params.gravity = Gravity.CENTER;
                brushview.setLayoutParams(params);
                //brushshape.setColor(color_selected);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Eraser
        SeekBar sizeEraserSeekBar = (SeekBar) scribbleDialog.findViewById(R.id.eraser_sizeSeekBar);
        sizeEraserSeekBar.setMax(10);
        sizeEraserSeekBar.setProgress(lastEraserSize);

        final TextView tvEraserSize = (TextView) scribbleDialog.findViewById(R.id.tvEraserSize);
        String esize = String.valueOf(lastEraserSize);
        tvEraserSize.setText(esize);

        eraserview = layoutEraser.findViewById(R.id.eraser_view);
        LayerDrawable eraserDrawable = (LayerDrawable) eraserview.getBackground();
        final GradientDrawable erasershape = (GradientDrawable) eraserDrawable.findDrawableByLayerId(R.id.shape_id);
        erasershape.setColor(Color.BLACK);

        sizeEraserSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lastEraserSize = progress;
                drawView.setBrushSize((int)(progress * 6.299));
                drawView.setDrawColor(lastBrushColor);
                String esize = String.valueOf(lastEraserSize);
                tvEraserSize.setText(esize);
                //eraserViewSize = (lastEraserSize * 4 + 20);
                eraserViewSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int)(progress * 6.299), getResources().getDisplayMetrics());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(eraserViewSize, eraserViewSize);
                params.gravity = Gravity.CENTER;
                eraserview.setLayoutParams(params);
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
        if (view != currPaint) {
            String color = view.getTag().toString();
            lastHighlightColor = Color.parseColor(color);
            brushshape.setColor(lastHighlightColor);
            highlightview.setBackground(brushshape);
            drawView.setDrawColor(lastHighlightColor);
        }
    }

    // TODO brush clicked
    public void brushClicked(View view) {
        if (view != currPaint) {
            String color = view.getTag().toString();
            lastBrushColor = Color.parseColor(color);
            drawView.setDrawColor(lastBrushColor);
            drawView.setBrushSize((int)(lastBrushSize * 6.299));
            brushshape.setColor(lastBrushColor);
            brushview.setBackground(brushshape);
            count++;
        }
    }

    // TODO open highlight
    public void openHighlight() {
        drawView.onClickEraser(1);
        drawView.setBrushSize((int) (lastHighlightSize * 6.299));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(highlightViewSize, highlightViewSize);
        params.gravity = Gravity.CENTER;
        highlightview.setLayoutParams(params);
        buttonHighlight.setBackgroundColor(getResources().getColor(R.color.eaeaea));
        buttonHighlight.setImageResource(R.drawable.scrbble_highlight_red);
        buttonBrush.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonBrush.setImageResource(R.drawable.scrbble_draw);
        buttonEraser.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonEraser.setImageResource(R.drawable.scrbble_erase);
        firstHighlightColor = lastHighlightColor;
        drawView.setDrawColor(lastHighlightColor);
    }

    // TODO open brush
    public void openBrush() {
        drawView.onClickEraser(1);
        drawView.setDrawColor(lastBrushColor);
        drawView.setBrushSize((int)(lastBrushSize * 6.299));
        brushview.setBackgroundColor(lastBrushColor);
        brushshape.setColor(lastBrushColor);
        brushview.setBackground(brushshape);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(brushViewSize, brushViewSize);
        params.gravity = Gravity.CENTER;
        brushview.setLayoutParams(params);
        buttonHighlight.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonHighlight.setImageResource(R.drawable.scrbble_highlight);
        buttonBrush.setBackgroundColor(getResources().getColor(R.color.eaeaea));
        buttonBrush.setImageResource(R.drawable.scrbble_draw_red);
        buttonEraser.setBackgroundColor(getResources().getColor(R.color.header_bg));
        buttonEraser.setImageResource(R.drawable.scrbble_erase);
        drawView.setDrawColor(lastBrushColor);
    }

    // TODO open eraser
    public void openEraser() {
        //drawView.setDrawColor(Color.parseColor("#FFFFFF"));
        drawView.setDrawColor(Color.WHITE);
        drawView.onClickEraser(0);
        drawView.setBrushSize((int)(lastEraserSize * 6.299));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(eraserViewSize, eraserViewSize);
        params.gravity = Gravity.CENTER;
        eraserview.setLayoutParams(params);
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

                isPaintMode = false;
                dialog.dismiss();

            }
        });
        buttonAlertOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                drawingControls.setVisibility(View.GONE);
                layOutDrawingView.setVisibility(View.GONE);
                updateButtonUI(-1);

                drawView.setDrawingCacheEnabled(true);

                FileNameGenerator fileNameGenerator = new FileNameGenerator();
                String fileName = fileNameGenerator.getFileName("SCRIBBLE");
                File file = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + fileName);

                try {
                    drawView.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));

                    /*ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png"); // setar isso
                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (file != null) {
                    Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT).show();
                    // savedToast.show();

                    int top = scrollView.getScrollY();

                    NoteElement noteElement = new NoteElement(Long.parseLong(noteIdForDetails),1,"Yes","scribble", fileName,String.valueOf(top),"");
                    noteElement.save();
                    modifyNoteTime();
                    drawView.destroyDrawingCache();
                    drawView.setUserDrawn(false);
                    onRestart();

                } else {
                    Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
                }
                isPaintMode = false;
                dialog.dismiss();
                onRestart();
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
        finish();
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

    public void paperButtonSelected(View view) {
        paperBackground(view.getTag().toString());
        String viewTag = view.getTag().toString();

        if (noteIdForDetails == null) { makeNote(); }

        if (noteIdForDetails != null) {
            Note note = Note.findById(Note.class, Long.parseLong(noteIdForDetails));
            note.setColor(viewTag);
            note.setModifytime(currentDateStr);
            note.save();
        }
    }

    public void paperBackground(String background) {
        if (background.equals("paper_bg_1")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper_bg_1111);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
            background_bg.setBackgroundDrawable(bitmapDrawable);
            //background_bg.setBackgroundResource(R.drawable.paper_bg_1);
        } else if (background.equals("paper_bg_2")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper_bg_1222);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
            background_bg.setBackgroundDrawable(bitmapDrawable);
            //background_bg.setBackgroundResource(R.drawable.paper_bg_2);
        } else if (background.equals("paper_bg_3")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper_bg_1333);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
            background_bg.setBackgroundDrawable(bitmapDrawable);
            //background_bg.setBackgroundResource(R.drawable.paper_bg_3);
        } else if (background.equals("paper_bg_4")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper_bg_1444);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
            background_bg.setBackgroundDrawable(bitmapDrawable);
            //background_bg.setBackgroundResource(R.drawable.paper_bg_4);
        } else if (background.equals("paper_bg_5")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper_bg_1555);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
            background_bg.setBackgroundDrawable(bitmapDrawable);
            //background_bg.setBackgroundResource(R.drawable.paper_bg_5);
        } else if (background.equals("paper_bg_6")) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.paper_bg_1666);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
            background_bg.setBackgroundDrawable(bitmapDrawable);
            //background_bg.setBackgroundResource(R.drawable.paper_bg_6);
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
            note.setModifytime(currentDateStr);
            note.save();
        }

    }

    public void makeNote() {
        Note note = new Note(textViewheaderTitle.getText().toString(), "", backgroundColor, "", 0L , "", "#FFFFFF", currentDateStr, currentDateStr, "", 0);
        note.save();
        noteIdForDetails = note.getId().toString();
        noteTitle[0] = note.getTitle();
    }

    public void modifyNoteTime() {
        Note n = Note.findById(Note.class, Long.parseLong(noteIdForDetails));
        n.setModifytime(currentDateStr);
        n.save();
    }

    public void deleteElements(String tag) {
        NoteElement ne = NoteElement.findById(NoteElement.class, Long.parseLong(tag));
        ne.delete();
        onResume();
    }

    public void fetchNoteElementsFromDb() throws FileNotFoundException {

        if (noteIdForDetails != null) {

            noteElements.removeAllViews();
            List<NoteElement> ne = NoteElement.findWithQuery(NoteElement.class, "SELECT * FROM NOTE_ELEMENT WHERE NOTEID = " + Long.parseLong(noteIdForDetails));

            Note note = Note.findById(Note.class, Long.parseLong(noteIdForDetails));

            if (note.islocked == 1)
                buttonLock.setText("Unlock");
            else
                buttonLock.setText("Lock");

            noteTitle[0] = note.title;

            textViewheaderTitle.setText(note.getTitle());

            String background = note.getColor();

            if (background.contains("#")){
                background_bg.setBackgroundColor(Color.parseColor(background));
            } else {
                paperBackground(background);
            }

            for (final NoteElement n : ne) {
                if (n.type.equals("text")) {
                    String s = n.content;
                    noteElements = (LinearLayout) findViewById(R.id.noteElements);
                    LayoutInflater inflator = getLayoutInflater();
                    View viewText = inflator.inflate(R.layout.note_text, null, false);
                    final RelativeLayout textView = (RelativeLayout) viewText.findViewById(R.id.textView);
                    final RichEditor editor = (RichEditor) viewText.findViewById(R.id.editor);
                    final ImageButton deleteText = (ImageButton) viewText.findViewById(R.id.deleteText);
                    deleteText.setTag(n.getId());

                    allDelete.add(deleteText);
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
                            if (textelementid.size() > 0)
                                textelementid.remove(0);

                            textelementid.add(v);
                        }
                    });

                    editor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
                        @Override
                        public void onTextChange(String s) {
                            n.setContent(s);
                            n.setContentA(getPlainText(s));
                            n.save();
                            modifyNoteTime();
                        }
                    });

                    deleteText.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDeleteAlert(v.getTag().toString(), NoteMainActivity.this);
                        }
                    });

                    noteElements.addView(textView);
                } else if (n.type.equals("image")) {
                    // add image layout
                    noteElements = (LinearLayout) findViewById(R.id.noteElements);
                    LayoutInflater inflator = LayoutInflater.from(getApplicationContext());
                    View viewImage = inflator.inflate(R.layout.note_image, null, false);
                    RelativeLayout note_image = (RelativeLayout) viewImage.findViewById(R.id.note_image);
                    ImageView note_imageview = (ImageView) note_image.findViewById(R.id.note_imageview);
                    final ImageButton imageDelete = (ImageButton) viewImage.findViewById(R.id.deleteImage);
                    allDelete.add(imageDelete);
                    imageDelete.setTag(n.getId());

                    imageDelete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getApplicationContext(), v.getTag().toString(), Toast.LENGTH_LONG).show();
                            //deleteElements(v.getTag().toString());
                            showDeleteAlert(v.getTag().toString(), NoteMainActivity.this);
                        }
                    });
                    String name = n.content;
                    File f = new File(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Images/" + name);
                    //int deviceWidth = getWindowManager().getDefaultDisplay().getWidth();
                    //int deviceHeight = getWindowManager().getDefaultDisplay().getHeight();
                    Bitmap b = BitmapFactory.decodeFile(String.valueOf(f));

                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

                    DisplayMetrics dm = new DisplayMetrics();
                    this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    float height = dm.heightPixels;
                    float width = dm.widthPixels;


                    /*DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    float dpHeight = displayMetrics.heightPixels;// / displayMetrics.density;
                    float dpWidth = displayMetrics.widthPixels;// / displayMetrics.density;
                    Log.e("********","");
                    Log.e("jay dpheight", String.valueOf(dpHeight));
                    Log.e("jay dpwidth", String.valueOf(dpWidth));
                    Log.e("jay densityDpi", String.valueOf(displayMetrics.densityDpi));
                    Log.e("jay density", String.valueOf(displayMetrics.density));
                    Log.e("jay bitmap pxheight", String.valueOf(b.getHeight() ));
                    Log.e("jay bitmap pxwidth", String.valueOf(b.getWidth() ));

                    Configuration configuration = this.getResources().getConfiguration();
                    int screenWidthDp = configuration.screenWidthDp;
                    int screenHeightDp = configuration.screenHeightDp;

                    Log.e("jay screenWidthdp", String.valueOf(screenWidthDp));
                    Log.e("jay screenHeightdp", String.valueOf(screenHeightDp));

                    Log.e("jay h", String.valueOf((height / 1.5) - 40));
                    Log.e("jay w", String.valueOf(width));*/

                    //Toast.makeText(getApplication(), "Width: " + deviceWidth + ", Height: " + deviceHeight, Toast.LENGTH_LONG).show();
					/*BitmapFactory.Options op = new BitmapFactory.Options();
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
					}*/
                    note_imageview.setImageBitmap(b);
                    note_imageview.setMaxHeight((int) (height / 1.5));
                    noteElements.addView(note_image);
                } else if (n.type.equals("scribble")) {
					noteScribbleElements = (RelativeLayout) findViewById(R.id.scribbleRelative);
					LayoutInflater inflator = LayoutInflater.from(getApplicationContext());
					View viewImage = inflator.inflate(R.layout.note_image, null, false);
					RelativeLayout note_image = (RelativeLayout) viewImage.findViewById(R.id.note_image);
					ImageView note_imageview = (ImageView) note_image.findViewById(R.id.note_imageview);
					String name = n.content;
					File f = new File(Environment.getExternalStorageDirectory() + "/NoteShare/NoteShare Images/" + name);
					Bitmap b = BitmapFactory.decodeFile(String.valueOf(f));
					note_imageview.setImageBitmap(b);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, Integer.parseInt(n.getContentA()), 0, 0);
                    note_imageview.setLayoutParams(params);
                    noteScribbleElements.addView(note_image);

                } else if (n.type.equals("audio")) {
                    // add audio layout
                    String name = null;
                    String status;

                    name = n.content;
                    status = n.contentA;

                    //addAudio(name);
                    noteElements = (LinearLayout) findViewById(R.id.noteElements);
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View viewAudio = inflater.inflate(R.layout.note_audio, null, false);
                    LinearLayout note_audio = (LinearLayout) viewAudio.findViewById(R.id.note_audio);

                    final MediaPlayer mp = new MediaPlayer();
                    final ImageView audio_play = (ImageView) viewAudio.findViewById(R.id.audio_play);
                    final SeekBar audio_seek = (SeekBar) viewAudio.findViewById(R.id.audio_seek);
                    final TextView audio_text = (TextView) viewAudio.findViewById(R.id.audio_text);
                    final ImageButton audioDelete = (ImageButton) viewAudio.findViewById(R.id.deleteAudio);
                    allDelete.add(audioDelete);
                    audioDelete.setTag(n.getId());

                    audioDelete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDeleteAlert(v.getTag().toString(), NoteMainActivity.this);
                        }
                    });

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
                    if (status.equals("true"))
                        noteElements.addView(note_audio);

                } else if (n.type.equals("checkbox")) {
                    Boolean status = Boolean.valueOf(n.contentA);
                    String text = n.content;

                    noteElements = (LinearLayout) findViewById(R.id.noteElements);
                    LayoutInflater inflator = getLayoutInflater();
                    View viewChecklist = inflator.inflate(R.layout.note_checklist, null, false);
                    final RelativeLayout checkbox = (RelativeLayout) viewChecklist.findViewById(R.id.checkbox);
                    final ImageView checklist_icon = (ImageView) viewChecklist.findViewById(R.id.checkboxIcon);
                    final ImageButton checklistDelete = (ImageButton) viewChecklist.findViewById(R.id.deleteCheckbox);
                    allDelete.add(checklistDelete);
                    checklistDelete.setTag(n.getId());
                    if(status)
                        checklist_icon.setTag(1);
                    else
                        checklist_icon.setTag(0);

                    final EditText checklist_text = (EditText) viewChecklist.findViewById(R.id.checkboxText);

                    checklist_text.setText(text);
                    //checklist_text.setTag(allCheckboxText.size()-1);
                    noteElements.addView(checkbox);

                    if (status) {
                        checklist_icon.setImageResource(R.drawable.checkbox_check_sq);
                        checklist_text.setPaintFlags(checklist_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else {
                        checklist_icon.setImageResource(R.drawable.checkbox_uncheck_sq);
                        checklist_text.setPaintFlags(checklist_text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    checklistDelete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getApplicationContext(), v.getTag().toString(), Toast.LENGTH_LONG).show();
                            //deleteElements(v.getTag().toString());
                            showDeleteAlert(v.getTag().toString(), NoteMainActivity.this);
                        }
                    });

                    checklist_text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            n.setContent(s.toString());
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
                                checklist_icon.setImageResource(R.drawable.checkbox_uncheck_sq);
                                n.setContentA("false");
                                checklist_text.setPaintFlags(checklist_text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                v.setTag("0");
                            } else {
                                checklist_icon.setImageResource(R.drawable.checkbox_check_sq);
                                n.setContentA("true");
                                checklist_text.setPaintFlags(checklist_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                v.setTag("1");
                            }
                            n.save();
                            modifyNoteTime();
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

                            if (allCheckboxText.size() > 0)
                                allCheckboxText.remove(0);

                            allCheckboxText.add((EditText) v);
                        }
                    });

                }
            }
        }
    }

    void showDeleteAlert(final String tag, Context context) {

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
        textViewTitleAlertMessage.setText("ARE YOU SURE WANT TO DELETE THIS ITEM?");

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
                //dialog.dismiss();
                deleteElements(tag);
                dialog.dismiss();
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(contentView);
        dialog.show();
    }

    public void deleteButton() {
        if (!isDeleteModeSelected) {
            imageButtonDeleteMode.setBackgroundColor(getResources().getColor(R.color.A8b241b));
            for (int i = 0; i < allDelete.size(); i++) {
                allDelete.get(i).setVisibility(View.VISIBLE);
            }
            isDeleteModeSelected = true;
        } else {
            imageButtonDeleteMode.setBackgroundColor(getResources().getColor(R.color.header_bg));
            for (int i = 0; i < allDelete.size(); i++) {
                allDelete.get(i).setVisibility(View.GONE);
            }
            isDeleteModeSelected = false;
        }
    }

    public String getPlainText(String htmlText){
        String plainText = htmlText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
        return plainText.replace("&nbsp;","");
    }

}