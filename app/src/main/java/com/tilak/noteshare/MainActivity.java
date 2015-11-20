package com.tilak.noteshare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tilak.adpters.NoteFolderAdapter;
import com.tilak.adpters.NoteFolderGridAdapter;
import com.tilak.adpters.OurNoteListAdapter;
import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.SideMenuitems;
import com.tilak.db.Config;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum SORTTYPE {
	ALPHABET,
	COLOURS,
	CREATED_TIME,
	MODIFIED_TIME,
	REMINDER_TIME,
	TIME_BOMB
};

enum VIEWTYPE {
	DETAIL,
	LIST,
	GRID
}

public class MainActivity extends DrawerActivity {

	public ImageButton imageButtonHamburg, imageButtoncalander,
			imageButtonsquence;
	public TextView textViewheaderTitle, tvIdHidden;
	public RelativeLayout layoutHeader;
	public EditText editTextsearchNote;
	public ImageButton textViewAdd;
	public ListView notefoleserList;
    //public SwipeListView notefoleserList;

    public GridView notefoleserGridList;
	public ScrollView notefoleserPintrestList;

	public LinearLayout Layout1;
	public LinearLayout Layout2;

	public NoteFolderAdapter adapter;
	public NoteFolderGridAdapter gridAdapter;
	public ArrayList<SideMenuitems> arrDataNote;
	final Context context = this;
	public LinearLayout textNoteSort, textNoteView;
	
	public SORTTYPE sortType;
	public VIEWTYPE viewType;
	public NoteFunctions noteFunctions = new NoteFunctions();
	public Config con = Config.findById(Config.class, 1L);

	private ArrayList<HashMap<String,String>> list;
	public Dialog dialogColor;
	public Dialog move;

	public List<Note> sortallnotes;
	public String setListView;
	public static String folderIdforNotes;

	public ArrayList<String> noteIdList = new ArrayList<String>();

	private static final String TAG = MainActivity.class.getSimpleName();

	public SwipeListView listView;
	final int[] lastItemOpened = {-1};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		Intent intent = this.getIntent();
		folderIdforNotes = intent.getStringExtra("FolderId");
		Log.v("select", "OnCreate" + folderIdforNotes);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater
				.inflate(R.layout.activity_main, null, false);
		mDrawerLayout.addView(contentView, 0);
		//DataManager.sharedDataManager().setSelectedIndex(-1);

		try {
			initlizeUIElement(contentView);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		editTextsearchNote = (EditText)findViewById(R.id.editTextsearchNote);
		editTextsearchNote.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.e("jay *****", "****");
				List<Long> idList = new ArrayList<Long>();

				if (folderIdforNotes == null || folderIdforNotes.equals("-1")) {

					//find note
					sortallnotes = Note.findWithQuery(Note.class, "Select * from Note where creationtime != 0 AND title LIKE ?", "%" + editTextsearchNote.getText().toString() + "%");
				}
				else {
					sortallnotes = Note.findWithQuery(Note.class, "Select * from Note where creationtime != 0 AND title LIKE ? AND folder = " + folderIdforNotes, "%" + editTextsearchNote.getText().toString() + "%");

				}
					//Log.e("jay sortall size", String.valueOf(sortallnotes.size()));

					for(int i=0; i < sortallnotes.size(); i++){
						idList.add(sortallnotes.get(i).getId());
						//Log.e("jay sortall id", String.valueOf(sortallnotes.get(i).getId()));
					}

					// find elements
					List<NoteElement> ne = NoteElement.findWithQuery(NoteElement.class, "Select DISTINCT NOTEID from NOTE_ELEMENT where content_A LIKE ?", "%" + editTextsearchNote.getText().toString() + "%" );
					//Log.e("jay ne size", String.valueOf(ne.size()));
					for(int i=0; i < ne.size(); i++){
						idList.add(ne.get(i).getNoteid());
						//Log.e("jay ne id", String.valueOf(ne.get(i).getNoteid()));
					}

					//get only unique ids using set
					Set<Long> set = new HashSet<Long>(idList);
					//Log.e("jay set size", String.valueOf(set.size()));
					sortallnotes.clear();

					//convert set to list and add notes into sortallnotes
					List<Long> list;// = new ArrayList<Long>();

					if(folderIdforNotes == null || folderIdforNotes.equals("-1")){
						list = new ArrayList<Long>(set);
					}else{
						List<Note> notes = Note.findWithQuery(Note.class,"Select * from Note where creationtime != 0 AND folder = " + folderIdforNotes);
						List<Long> folderId = new ArrayList<Long>();
						for(int i =0; i < notes.size(); i++){
							folderId.add(notes.get(i).getId());
						}
						Set<Long> folderSet = new HashSet<Long>();
						folderSet.addAll(folderId);
						set.retainAll(folderSet);
						list = new ArrayList<Long>(set);
					}

				//List<Long> list = new ArrayList<Long>(set);

				for(int i=0; i < list.size(); i++){
					sortallnotes.add(Note.findById(Note.class, list.get(i).longValue()));
					Log.e("jay list id", String.valueOf(list.get(i).longValue()));
				}

				String strCout = "(" + sortallnotes.size() + ")";
				textViewheaderTitle.setText("NOTE " + strCout);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (sortallnotes.toString() == "[]") {
					putInList();
					swipeListView();
				} else {
					putInList();
					swipeListView();
				}
			}
		});

		createDirectory();

	}

	@Override
	protected void onRestart() {
		super.onRestart();

		//editTextsearchNote.setText("");
		try {
			checkTimeClicked();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//sortType = SORTTYPE.MODIFIED_TIME;
		Config con = Config.findById(Config.class, 1L);
		sortType = SORTTYPE.valueOf(con.getSort());
		viewType = VIEWTYPE.valueOf(con.getView());
		populate();
		sortingArray();
		//swipeListView();
	}

	public void	 btnCallbacks(Object data)
	{
		System.out.println("the tag us" + data);
		DataManager.sharedDataManager().setSelectedIndex(-1);

		adapter.notifyDataSetChanged();
	}

	void initlizeUIElement(View contentview) throws ParseException{
		//DataManager.sharedDataManager().setTypeofListView(false);

		layoutHeader = (RelativeLayout) contentview
				.findViewById(R.id.mainHeadermenue);
		textViewheaderTitle = (TextView) layoutHeader
				.findViewById(R.id.textViewheaderTitle);

		imageButtonHamburg = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtonHamburg);


		textNoteSort = (LinearLayout) findViewById(R.id.textNoteSort);
		textNoteView = (LinearLayout) findViewById(R.id.textNoteView);

		textViewAdd = (ImageButton) findViewById(R.id.textViewAdd);

		arrDataNote = new ArrayList<SideMenuitems>();

		notefoleserGridList = (GridView) findViewById(R.id.notefoleserGridList);
		notefoleserPintrestList = (ScrollView) findViewById(R.id.notefoleserPintrestList);
		Layout1 = (LinearLayout) findViewById(R.id.Layout1);
		Layout2 = (LinearLayout) findViewById(R.id.Layout2);

		addlistners();
		// getDeafultNote();
		Config con = Config.findById(Config.class, 1L);
		sortType = SORTTYPE.valueOf(con.getSort());
		viewType = VIEWTYPE.valueOf(con.getView());

		checkTimeClicked();
		populate();
		sortingArray();
		swipeListView();
	}

	void updatePintrestView() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		sortingArray();
		Layout1.removeAllViews();
		Layout2.removeAllViews();
		

		for (int i = 0; i < arrDataNote.size(); i++) {
			
			View contentView = inflater.inflate(R.layout.notefoldepintrestrow,
					null, false);
			
			//LinearLayout contentView=(LinearLayout) findViewById(R.id.PintrestViews);
			//LinearLayout contentViewSub=(LinearLayout) findViewById(R.id.PintrestSubViews);
			

			TextView textViewSlideMenuName = (TextView) contentView
					.findViewById(R.id.textViewSlideMenuName);
			TextView textViewSlideMenuNameSubTitle = (TextView) contentView
					.findViewById(R.id.textViewSlideMenuNameSubTitle);
			View layoutsepreter = (View) contentView
					.findViewById(R.id.layoutsepreter);
			layoutsepreter.setVisibility(View.VISIBLE);

			SideMenuitems model = arrDataNote.get(i);
			textViewSlideMenuName.setText(model.getMenuName());
			textViewSlideMenuNameSubTitle.setText(model.getMenuNameDetail());

			if (i % 2 == 0) {
				Layout1.addView(contentView);
				contentView.setBackgroundColor(Color
						.parseColor(model.getColours()));
			} else {
				Layout2.addView(contentView);
				contentView.setBackgroundColor(Color
						.parseColor(model.getColours()));
			}

		}

	}

	void sortingArray()
	{
		switch (sortType) {
		case ALPHABET:
		{
			Collections.sort(sortallnotes, new TitleComparator());
			putInList();
			swipeListView();
		}
			break;
		case COLOURS:
		{
			Collections.sort(sortallnotes, new colorComparator());
			putInList();
			swipeListView();
		}
			break;
		case CREATED_TIME:
		{
			Collections.sort(sortallnotes, new creationTimeComparator());
			putInList();
			swipeListView();
		}
			break;
		case MODIFIED_TIME:
		{
			Collections.sort(sortallnotes, new modifiedTimeComparator());
			putInList();
			swipeListView();
		}
			break;
		case REMINDER_TIME:
		{
			
		}
			break;
		case TIME_BOMB:
		{
			Collections.sort(sortallnotes, new timebombComparator());
			putInList();
			swipeListView();
		}
			break;

		default:
			break;
		}
	}

	void addlistners() {

		textNoteSort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showActionSheet_sort(arg0);

			}
		});
		textNoteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showActionSheet(v);
			}
		});

		/*imageButtoncalander.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {}
		});*/
		imageButtonHamburg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openSlideMenu();
			}
		});
		/*imageButtonsquence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});*/
		textViewAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, NoteMainActivity.class));
			}
		});
	}



	// showActionSheet
	// ---------------------------------------------------------------------------------------

	public void showActionSheet(View v) {

		final Dialog myDialog = new Dialog(MainActivity.this,
				R.style.CustomTheme);

		myDialog.setContentView(R.layout.actionsheet);
		Button buttonDissmiss = (Button) myDialog
				.findViewById(R.id.buttonDissmiss);

		LinearLayout layoutList = (LinearLayout) myDialog
				.findViewById(R.id.layoutList);
		TextView layoutListTextView = (TextView) layoutList
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutListImageView = (ImageView) layoutList
				.findViewById(R.id.imageViewSlidemenu);
		layoutListImageView.setImageResource(R.drawable.list_view_logo);
		layoutListTextView.setText("List");

		LinearLayout layoutDetail = (LinearLayout) myDialog
				.findViewById(R.id.layoutDetail);
		TextView layoutDetailTextView = (TextView) layoutDetail
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutDetailImageView = (ImageView) layoutDetail
				.findViewById(R.id.imageViewSlidemenu);
		layoutDetailImageView.setImageResource(R.drawable.detail_view_logo);
		layoutDetailTextView.setText("Details");

		LinearLayout layoutPintrest = (LinearLayout) myDialog
				.findViewById(R.id.layoutPintrest);
		TextView layoutPintrestTextView = (TextView) layoutPintrest
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutPintrestImageView = (ImageView) layoutPintrest
				.findViewById(R.id.imageViewSlidemenu);
		layoutPintrestImageView.setImageResource(R.drawable.pintrest_view_logo);
		layoutPintrestTextView.setText("Shuffle");
		layoutPintrest.setVisibility(LinearLayout.GONE);

		LinearLayout layoutGrid = (LinearLayout) myDialog
				.findViewById(R.id.layoutGrid);
		TextView layoutGridTextView = (TextView) layoutGrid
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutGridImageView = (ImageView) layoutGrid
				.findViewById(R.id.imageViewSlidemenu);
		layoutGridImageView.setImageResource(R.drawable.grid_view_logo);
		layoutGridTextView.setText("Tiles");

		layoutPintrestTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				myDialog.dismiss();
			}
		});

		layoutGridTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SwipeListView listView = (SwipeListView) findViewById(R.id.notefoleserList);
				listView.setVisibility(View.GONE);
				notefoleserGridList.setVisibility(View.VISIBLE);
				con.setView(VIEWTYPE.GRID.name());
				con.save();
				viewType = VIEWTYPE.GRID;
				swipeListView();
				myDialog.dismiss();
			}
		});

		layoutDetailTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				notefoleserGridList.setVisibility(View.GONE);
				SwipeListView listView = (SwipeListView) findViewById(R.id.notefoleserList);
				listView.setVisibility(View.VISIBLE);
				con.setView(VIEWTYPE.DETAIL.name());
				con.save();
				viewType = VIEWTYPE.DETAIL;
				swipeListView();
				myDialog.dismiss();
			}
		});

		layoutListTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				notefoleserGridList.setVisibility(View.GONE);
				SwipeListView listView = (SwipeListView) findViewById(R.id.notefoleserList);
				listView.setVisibility(View.VISIBLE);
				con.setView(VIEWTYPE.LIST.name());
				con.save();
				viewType = VIEWTYPE.LIST;
				swipeListView();
				myDialog.dismiss();

			}
		});

		buttonDissmiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});

		myDialog.getWindow().getAttributes().windowAnimations = R.anim.slide_up_1;
		myDialog.show();

		myDialog.getWindow().setGravity(Gravity.BOTTOM);

	}

    /******* bottom sorting menu start *******/

	public void showActionSheet_sort(View v) {

		final Dialog myDialog = new Dialog(MainActivity.this,
				R.style.CustomTheme);

		// layoutReminderTime
		// layouttimebomb

		myDialog.setContentView(R.layout.actionsheet_sort);
		Button buttonDissmiss = (Button) myDialog
				.findViewById(R.id.buttonDissmiss);

		LinearLayout layoutList = (LinearLayout) myDialog
				.findViewById(R.id.layoutList);
		TextView layoutListTextView = (TextView) layoutList
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutListImageView = (ImageView) layoutList
				.findViewById(R.id.imageViewSlidemenu);
		layoutListImageView.setImageResource(R.drawable.alphabet_sort_view);
		
		layoutListTextView.setText("A-Z alphabetical");

		LinearLayout layoutDetail = (LinearLayout) myDialog
				.findViewById(R.id.layoutDetail);
		TextView layoutDetailTextView = (TextView) layoutDetail
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutDetailImageView = (ImageView) layoutDetail
				.findViewById(R.id.imageViewSlidemenu);
		layoutDetailImageView.setImageResource(R.drawable.color_sort_view);
		layoutDetailTextView.setText("Colours");

		LinearLayout layoutPintrest = (LinearLayout) myDialog
				.findViewById(R.id.layoutPintrest);
		TextView layoutPintrestTextView = (TextView) layoutPintrest
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutPintrestImageView = (ImageView) layoutPintrest
				.findViewById(R.id.imageViewSlidemenu);
		layoutPintrestImageView.setImageResource(R.drawable.modifiedtime_sort_view);
		layoutPintrestTextView.setText("Modified Time");

		LinearLayout layoutGrid = (LinearLayout) myDialog
				.findViewById(R.id.layoutGrid);
		TextView layoutGridTextView = (TextView) layoutGrid
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutGridImageView = (ImageView) layoutGrid
				.findViewById(R.id.imageViewSlidemenu);
		layoutGridImageView.setImageResource(R.drawable.createdtime_sort_view);
		layoutGridTextView.setText("Created Time");

		LinearLayout layoutListReminderTime = (LinearLayout) myDialog
				.findViewById(R.id.layoutReminderTime);
		TextView layoutListTextViewReminderTime = (TextView) layoutListReminderTime
				.findViewById(R.id.textViewSlideMenuName);
		ImageView layoutListImageViewReminderTime = (ImageView) layoutListReminderTime
				.findViewById(R.id.imageViewSlidemenu);
		layoutListImageViewReminderTime.setImageResource(R.drawable.reminder_sort_view);
		layoutListTextViewReminderTime.setText("Reminder Time");

		LinearLayout layoutListTimeBomb = (LinearLayout) myDialog
				.findViewById(R.id.layouttimebomb);
		TextView layoutListTextViewTimeBomb = (TextView) layoutListTimeBomb
				.findViewById(R.id.textViewSlideMenuName);
		layoutListTimeBomb
				.findViewById(R.id.imageViewSlidemenu);
		ImageView layoutListImageViewTimeBomb = (ImageView) layoutListTimeBomb
				.findViewById(R.id.imageViewSlidemenu);
		layoutListImageViewTimeBomb.setImageResource(R.drawable.timebomb_sort_view);
		layoutListTextViewTimeBomb.setText("Time Bomb");

		layoutGridTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "Created Time",
						Toast.LENGTH_SHORT).show();
				sortType = SORTTYPE.CREATED_TIME;
				con.setSort(SORTTYPE.CREATED_TIME.name());
				con.save();
				sortingArray();
				myDialog.dismiss();
			}
		});

		layoutPintrestTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "Modified Time",
						Toast.LENGTH_SHORT).show();
				sortType=SORTTYPE.MODIFIED_TIME;
				con.setSort(SORTTYPE.MODIFIED_TIME.name());
				con.save();
				sortingArray();
				myDialog.dismiss();
			}
		});

		layoutDetailTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Toast.makeText(getApplicationContext(), "Colours",
						Toast.LENGTH_SHORT).show();
				sortType=SORTTYPE.COLOURS;
				con.setSort(SORTTYPE.COLOURS.name());
				con.save();
				sortingArray();
				myDialog.dismiss();
			}
		});

		layoutListTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//
				sortType=SORTTYPE.ALPHABET;
				con.setSort(SORTTYPE.ALPHABET.name());
				con.save();
				sortingArray();
				Toast.makeText(getApplicationContext(), "Alphabetical",
						Toast.LENGTH_SHORT).show();
				myDialog.dismiss();
			}
		});

		layoutListTextViewTimeBomb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sortType=SORTTYPE.TIME_BOMB;
				con.setSort(SORTTYPE.TIME_BOMB.name());
				con.save();
				sortingArray();
				Toast.makeText(getApplicationContext(), "Time Bomb",
						Toast.LENGTH_SHORT).show();
				myDialog.dismiss();
			}
		});

		layoutListTextViewReminderTime
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(getApplicationContext(),
								"Reminder Time", Toast.LENGTH_SHORT).show();
						myDialog.dismiss();
					}
				});

		buttonDissmiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});

		myDialog.getWindow().getAttributes().windowAnimations = R.anim.slide_up_1;

		myDialog.show();

		myDialog.getWindow().setGravity(Gravity.BOTTOM);

	}

    /******* bottom sorting menu end *******/


    /******* onBackPressed start *******/

    @Override
    public void onBackPressed() {
        showAlertWith(this);
    }

    void showAlertWith(Context context) {

        final Dialog dialog = new Dialog(context);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.alert_view, null, false);

        TextView textViewTitleAlert = (TextView) contentView
                .findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText("QUIT THE APP");
        textViewTitleAlert.setTextColor(Color.WHITE);
        TextView textViewTitleAlertMessage = (TextView) contentView
                .findViewById(R.id.textViewTitleAlertMessage);
        textViewTitleAlertMessage.setText("Are you sure, Do you want to quit the app?");

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
				System.exit(0);
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(contentView);
        dialog.show();

    }

    /******* onBackPressed end *******/



    /******* create directory start *******/

    // create directory NoteShare in internal memory
    // and Images and Audio folder inside NoteShare folder for images, profile picture and audio notes

	public void createDirectory() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		if (isExternalStorageAvailable()) {
			// get the URI

			// 1. Get the external storage directory
			String appName = MainActivity.this.getString(R.string.app_name);
			String imgDir = "/NoteShare/NoteShare Images";
			String audioDir = "/NoteShare/NoteShare Audio";
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), appName);

			// 2. Create our subdirectory
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) { Log.e(TAG, "Failed to create NoteShare directory."); }
			}

			// 3. Creating Image Directory in NoteShare Directory
			File imgDirectory = new File(Environment.getExternalStorageDirectory(), imgDir);
			if (!imgDirectory.exists()) {
				if (!imgDirectory.mkdirs()) { Log.e(TAG, "Failed to create Image directory."); }
			}

			// 4. Creating Audio Directory in NoteShare Directory
			File audioDirectory = new File(Environment.getExternalStorageDirectory(), audioDir);
			if (!audioDirectory.exists()) {
				if (!audioDirectory.mkdirs()) { Log.e(TAG, "Failed to create Audio directory."); }
			}
		}
	}

	private boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();

		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		else {
			return false;
		}
	}

    /******* create directory end *******/



	void populate() {
		list = new ArrayList<HashMap<String, String>>();
		List<Note> allnotes;
		if(folderIdforNotes == null || folderIdforNotes == "-1")
			allnotes = Note.findWithQuery(Note.class, "Select * from Note WHERE creationtime != 0 ORDER BY ID DESC");
		else
			allnotes = Note.findWithQuery(Note.class, "Select * from Note WHERE creationtime != 0 AND folder = " + folderIdforNotes + " ORDER BY ID DESC");

		sortallnotes = allnotes;
		putInList();

		String strCout = "(" + list.size() + ")";
		textViewheaderTitle.setText("NOTE " + strCout);

	}

	void putInList(){
		if(list.size()>0) {
			list.clear();
		}
		noteIdList.clear();
		int i = 0;
		for(Note currentnote : sortallnotes){

			noteIdList.add(currentnote.getId().toString());
			String noteDesc = "";

			/*List<NoteElement> noteElements = NoteElement.findWithQuery(NoteElement.class, "SELECT DISTINCT TYPE FROM NOTE_ELEMENT WHERE NOTEID = " + currentnote.getId());
			for (NoteElement currentNoteElement : noteElements){
				noteDesc += currentNoteElement.getType().toUpperCase() + " ";
			}*/
			//List<NoteElement> noteElements = NoteElement.findWithQuery(NoteElement.class, "SELECT DISTINCT TYPE FROM NOTE_ELEMENT WHERE NOTEID = " + currentnote.getId() + " AND ");
			List<NoteElement> noteElements = NoteElement.find(NoteElement.class,"type = ? and noteid = ?", "text", currentnote.getId().toString());

			if(noteElements.size() != 0 && noteElements.get(0).getContentA() != null){
				noteDesc = noteElements.get(0).getContentA();
			}else{
				noteDesc = "";
			}

			HashMap<String,String> map = new HashMap<String,String>();
			map.put("noteName", currentnote.getTitle());
			//map.put("noteDesc", noteDesc); // change this later
			map.put("noteDesc", noteDesc);
			map.put("noteDate", currentnote.getModifytime());
			map.put("noteId", currentnote.getId().toString());
			map.put("noteBgColor",currentnote.getBackground());
			map.put("noteLock", String.valueOf(currentnote.getIslocked()));
			map.put("noteNum", String.valueOf(i));
			//map.put("noteLock", String.valueOf(currentnote.getIslocked()));
			list.add(map);
			i++;
		}
	}

	public void checkTimeClicked() throws ParseException {

		List<Note> allnotes = Note.findWithQuery(Note.class, "Select * from Note WHERE timebomb IS NOT NULL AND timebomb != ''");

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String currentDateStr = formatter.format(new Date());

		Date currentDate = formatter.parse(currentDateStr);

		for (Note currentnote : allnotes) {
			String timebomb_time = currentnote.getTimebomb();

			Date timebomb = formatter.parse(timebomb_time);
			if (currentDate.compareTo(timebomb) >= 0) {
				currentnote.setCreationtime("0");
				currentnote.save();
			}
		}
	}

	private void showColorAlert(Context context,final int nid) {
		dialogColor = new Dialog(context);
		LayoutInflater inflater = getLayoutInflater();
		View contentView = inflater.inflate(R.layout.paintcolor, null, false);
		LinearLayout paintLayout = (LinearLayout) contentView.findViewById(R.id.paint_colors);
		LinearLayout paintLayout1 = (LinearLayout) contentView.findViewById(R.id.paint_colors1);

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
		ImageButton colorbutton6 = (ImageButton) paintLayout1
				.findViewById(R.id.colorbutton6);
		ImageButton colorbutton7 = (ImageButton) paintLayout1
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
				paintClicked(v, nid);

			}
		});
		colorbutton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);

			}
		});
		colorbutton3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);

			}
		});
		colorbutton4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});
		colorbutton5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});
		colorbutton6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});
		colorbutton7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});

		colorbutton8.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});

		colorbutton9.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});
		colorbutton10.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				paintClicked(v, nid);
			}
		});

		TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText("SELECT COLOR");
		textViewTitleAlert.setTextColor(Color.WHITE);

		dialogColor.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogColor.setCancelable(true);
		dialogColor.setContentView(contentView);
		dialogColor.show();
	}

	public void paintClicked(View view,int nid) {
		// use chosen color
		ImageButton currPaint = null;
		if (view != currPaint) {
			// update color
			ImageButton imgView = (ImageButton) view;
			String color = view.getTag().toString();

			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

			Note n = Note.findById(Note.class,(long) nid);
			n.background = color;
			n.save();
			onRestart();
			dialogColor.dismiss();
		}
	}

	public void swipeListView(){
		listView = (SwipeListView) findViewById(R.id.notefoleserList);
		GridView listGridView = (GridView) findViewById(R.id.notefoleserGridList);

		OurNoteListAdapter noteAdapter = new OurNoteListAdapter(this,list, viewType.name());

		if(viewType != VIEWTYPE.GRID){
			listView.setOffsetLeft(130L);

			listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					//do your stuff here

					HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

					int nid = Integer.parseInt(map.get("noteId"));
					showColorAlert(MainActivity.this, nid);
					return true;
				}
			});



			listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
				@Override
				public void onClickFrontView(int position) {

					int itemPosition = position;
					String noteid = null;

					noteid = noteIdList.get(position);


					try {
						// TODO if passcode == null
						Note note = Note.findById(Note.class, Long.parseLong(noteid));
						if (note.islocked == 0) {
							Intent i = new Intent(MainActivity.this, NoteMainActivity.class);
							i.putExtra("NoteId", noteid);
							startActivity(i);
							//editTextsearchNote.setText("");
						} else {
							Intent intent = new Intent(MainActivity.this, PasscodeActivity.class);
							intent.putExtra("FileId", noteid);
							intent.putExtra("Check", "2");
							startActivity(intent);
							//editTextsearchNote.setText("");
						}
						// TODO if passcode != null
					} catch (Exception e) {

					}
				}

				@Override
				public void onOpened(int position, boolean toRight) {
					super.onOpened(position, toRight);
					if (lastItemOpened[0] != -1 && lastItemOpened[0] != position)
						listView.closeAnimate(lastItemOpened[0]);
					lastItemOpened[0] = position;
				}

			});

			listView.setAdapter(noteAdapter);
			notefoleserGridList.setVisibility(View.GONE);
			listView.setAnimationTime(200);

			}if(viewType == VIEWTYPE.GRID){
				listGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						int itemPosition = position;
						String noteid = null;

						HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

						noteid = map.get("noteId");

						try {
							Intent i = new Intent(MainActivity.this, NoteMainActivity.class);
							i.putExtra("NoteId", noteid);
							startActivity(i);
						} catch (Exception e) {

						}
					}
				});

				listGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						//do your stuff here

						HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

						int nid = Integer.parseInt(map.get("noteId"));
						showColorAlert(MainActivity.this, nid);
						return true;
					}
				});

				listGridView.setAdapter(noteAdapter);
				notefoleserGridList.setVisibility(View.VISIBLE);
			}

	}
	class TitleComparator implements Comparator<Note> {

		public int compare(Note c1, Note c2) {
			return c1.getTitle().compareTo(c2.getTitle());
		}
	}
	class colorComparator implements Comparator<Note> {

		public int compare(Note c1, Note c2) {
			return c1.getBackground().compareTo(c2.getBackground());
		}
	}
	class remindComparator implements Comparator<Note> {

		public int compare(Note c1, Note c2) {
			return c1.getRemindertime().compareTo(c2.getRemindertime());
		}
	}
	class creationTimeComparator implements Comparator<Note> {

		public int compare(Note c1, Note c2) {
			try{
				SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String creationtime1 = c1.getCreationtime();
				Date creation1 = formatter.parse(creationtime1);
				String creationtime2 = c2.getCreationtime();
				Date creation2 = formatter.parse(creationtime2);
				return creationtime2.compareTo(creationtime1);
			}catch (Exception e) {
			}
			finally {
				return c1.getCreationtime().compareTo(c2.getCreationtime());
			}
		}
	}
	class modifiedTimeComparator implements Comparator<Note> {

		public int compare(Note c1, Note c2) {
			try{
				SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String modifiedtime1 = c1.getModifytime();
				Date modification1 = formatter.parse(modifiedtime1);
				String modifiedtime2 = c2.getCreationtime();
				Date modification2 = formatter.parse(modifiedtime2);
				return modification1.compareTo(modification2);
			}
			catch (Exception e) {
			}
			finally {
				return c2.getModifytime().compareTo(c1.getModifytime());
			}
		}
	}
	class timebombComparator implements Comparator<Note> {

		public int compare(Note c1, Note c2) {
			try{
				SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timebomb1 = c1.getModifytime();
				Date timebombdate1 = formatter.parse(timebomb1);
				String timebomb2 = c2.getCreationtime();
				Date timebombdate2 = formatter.parse(timebomb2);
				return timebombdate1.compareTo(timebombdate2);
			}catch (Exception e) {

			}
			finally {
				return c2.getTimebomb().compareTo(c1.getTimebomb());
			}
		}
	}

	public void move(View v){
		listView.closeAnimate(lastItemOpened[0]);
		String noteid = v.getTag().toString();
		noteFunctions.showMoveAlert(this, noteid);
	}

	public void timeBomb(View v){
		listView.closeAnimate(lastItemOpened[0]);
		String id = v.getTag().toString();
		noteFunctions.showDate(this, id, "SET TIMEBOMB", "timebomb");
	}

	public void remind(View v) {
		listView.closeAnimate(lastItemOpened[0]);
		String id = v.getTag().toString();
		noteFunctions.showDate(this, id, "SET REMINDER", "reminder");
	}

	public void deleteNote(View v){
		listView.closeAnimate(lastItemOpened[0]);
		String id = v.getTag().toString();
		noteFunctions.showDeleteAlert(this, id);
		onRestart();
	}

	public void passCode(View v){
		listView.closeAnimate(lastItemOpened[0]);
		String id = v.getTag().toString();
		noteFunctions.setPasscode(getApplicationContext(), id);
	}
}
