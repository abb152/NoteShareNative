package com.tilak.noteshare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tilak.adpters.NoteFolderAdapter;
import com.tilak.adpters.NoteFolderGridAdapter;
import com.tilak.adpters.Test;
import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.SideMenuitems;
import com.tilak.db.Note;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

enum SORTTYPE
{
	ALPHABET,
	COLOURS,
	CREATED_TIME,
	MODIFIED_TIME,
	REMINDER_TIME,
	TIME_BOMB
};

public class MainActivity extends DrawerActivity {

	public ImageButton imageButtonHamburg, imageButtoncalander,
			imageButtonsquence;
	public TextView textViewheaderTitle;
	public RelativeLayout layoutHeader;

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
	public TextView textNoteSort, textNoteView;
	
	public SORTTYPE sortType;



	private ArrayList<HashMap<String,String>> list;




	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater
				.inflate(R.layout.activity_main, null, false);
		mDrawerLayout.addView(contentView, 0);
		DataManager.sharedDataManager().setSelectedIndex(-1);

		try {
			initlizeUIElement(contentView);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//getDeafultNote();
		createDirectory();

	}
	
	public void	 btnCallbacks(Object data)
	{
		System.out.println("the tag us" + data);
		DataManager.sharedDataManager().setSelectedIndex(-1);

		adapter.notifyDataSetChanged();
	}

	void initlizeUIElement(View contentview) throws ParseException{
		DataManager.sharedDataManager().setTypeofListView(false);

		layoutHeader = (RelativeLayout) contentview
				.findViewById(R.id.mainHeadermenue);
		textViewheaderTitle = (TextView) layoutHeader
				.findViewById(R.id.textViewheaderTitle);

		imageButtonHamburg = (ImageButton) layoutHeader
				.findViewById(R.id.imageButtonHamburg);


		textNoteSort = (TextView) findViewById(R.id.textNoteSort);
		textNoteView = (TextView) findViewById(R.id.textNoteView);

		textViewAdd = (ImageButton) findViewById(R.id.textViewAdd);

		arrDataNote = new ArrayList<SideMenuitems>();

		notefoleserGridList = (GridView) findViewById(R.id.notefoleserGridList);
		notefoleserPintrestList = (ScrollView) findViewById(R.id.notefoleserPintrestList);
		Layout1 = (LinearLayout) findViewById(R.id.Layout1);
		Layout2 = (LinearLayout) findViewById(R.id.Layout2);

		// Grid adapter

		//gridAdapter = new NoteFolderGridAdapter(this, arrDataNote);
		//notefoleserGridList.setAdapter(gridAdapter);

		// list adapter

		//adapter = new NoteFolderAdapter(this, arrDataNote);
		//notefoleserList.setAdapter(adapter);


		addlistners();
		// getDeafultNote();

		checkTimeClicked();
		populate();

		SwipeListView listView = (SwipeListView) findViewById(R.id.notefoleserList);

		Test testAdapter = new Test(this,list);
		listView.setOffsetLeft(200L);
		listView.setOffsetRight(100L);
		listView.setAdapter(testAdapter);

		listView.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
				//do your stuff here
				Intent intent = new Intent(MainActivity.this,LoginActivity.class);
				startActivity(intent);
				return true;
			}
		});

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
			Collections.sort(arrDataNote, new Comparator<SideMenuitems>() {

				@Override
				public int compare(SideMenuitems lhs, SideMenuitems rhs) {
					// TODO Auto-generated method stub
				return lhs.getMenuName().compareToIgnoreCase(rhs.getMenuName());
				}
			});
			
		}
			break;
		case COLOURS:
		{
			Collections.sort(arrDataNote, new Comparator<SideMenuitems>() {

				@Override
				public int compare(SideMenuitems lhs, SideMenuitems rhs) {
					// TODO Auto-generated method stub
				return lhs.getColours().compareToIgnoreCase(rhs.getColours());
				}
			});
		}
			break;
		case CREATED_TIME:
		{
			
		}
			break;
		case MODIFIED_TIME:
		{
			
		}
			break;
		case REMINDER_TIME:
		{
			
		}
			break;
		case TIME_BOMB:
		{
			
		}
			break;

		default:
			break;
		}
	}
	
	void updateGridView() {

		gridAdapter.notifyDataSetChanged();
	}


	void addlistners() {

		textNoteSort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showActionSheet_sort(arg0);

			}
		});
		textNoteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
                // TODO Auto-generated method stub
                openSlideMenu();
            }
        });
		/*imageButtonsquence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});*/
		textViewAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, NoteMainActivity.class));
            }
        });

		/*notefoleserList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                startActivity(new Intent(context, NoteMainActivity.class));
            }
        });
		
		notefoleserList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (DataManager.sharedDataManager().getSelectedIndex() == arg2) {
                    DataManager.sharedDataManager().setSelectedIndex(-1);
                } else {
                    DataManager.sharedDataManager().setSelectedIndex(arg2);
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });*/


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

		layoutGridTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				notefoleserGridList.setVisibility(View.VISIBLE);
				notefoleserList.setVisibility(View.GONE);
				//notefoleserPintrestList.setVisibility(View.GONE);
				myDialog.dismiss();
			}
		});

		layoutPintrestTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				notefoleserGridList.setVisibility(View.GONE);
				notefoleserList.setVisibility(View.GONE);
				notefoleserPintrestList.setVisibility(View.VISIBLE);
				
				//updatePintrestView();
				myDialog.dismiss();

			}
		});

		layoutDetailTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				DataManager.sharedDataManager().setTypeofListView(false);
				adapter.notifyDataSetChanged();
				// TODO Auto-generated method stub
				notefoleserGridList.setVisibility(View.GONE);
				notefoleserList.setVisibility(View.VISIBLE);
				//notefoleserPintrestList.setVisibility(View.GONE);
				
				DataManager.sharedDataManager().setSelectedIndex(-1);
				adapter.notifyDataSetChanged();
				myDialog.dismiss();

			}
		});

		layoutListTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DataManager.sharedDataManager().setTypeofListView(true);
				adapter.notifyDataSetChanged();
				notefoleserGridList.setVisibility(View.GONE);
				notefoleserList.setVisibility(View.VISIBLE);
				//notefoleserPintrestList.setVisibility(View.GONE);
				DataManager.sharedDataManager().setSelectedIndex(-1);
				adapter.notifyDataSetChanged();
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
		layoutListTextViewTimeBomb.setText("Time Bomb");

		layoutGridTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), "Created Time",
						Toast.LENGTH_SHORT).show();
				sortType=SORTTYPE.CREATED_TIME;
				sortingArray();

				myDialog.dismiss();

			}
		});

		layoutPintrestTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Modified Time",
						Toast.LENGTH_SHORT).show();
				sortType=SORTTYPE.MODIFIED_TIME;
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
				sortingArray();
				
				myDialog.dismiss();

			}
		});

		layoutListTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			
				adapter.notifyDataSetChanged();
				
				
				sortType=SORTTYPE.ALPHABET;
				sortingArray();

				Toast.makeText(getApplicationContext(), "Alphabetical",
						Toast.LENGTH_SHORT).show();

				myDialog.dismiss();

			}
		});

		layoutListTextViewTimeBomb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

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
        showAlertWith("Are you sure,Do you want to quit the app?",
                MainActivity.this);
    }

    void showAlertWith(String message, Context context) {

        final Dialog dialog = new Dialog(context);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
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
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        });
        buttonAlertOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
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
			String imgDir = "../NoteShare/Images";
			String audioDir = "../NoteShare/Audio";
			appName = "../" + appName;
			File mediaStorageDir = new File(
					Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.getDataDirectory())), appName);
			//Environment.getExternalStorageDirectory(Environment.getRootDirectory(), appName);

			// 2. Create our subdirectory
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) { Log.e(TAG, "Failed to create NoteShare directory."); }
			}

			// 3. Creating Image Directory in NoteShare Directory
			File imgDirectory = new File(
					Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.getDataDirectory())), imgDir);
			if (!imgDirectory.exists()) {
				if (!imgDirectory.mkdirs()) { Log.e(TAG, "Failed to create Image directory."); }
			}

			// 4. Creating Audio Directory in NoteShare Directory
			File audioDirectory = new File(
				Environment.getExternalStoragePublicDirectory(String.valueOf(Environment.getDataDirectory())), audioDir);
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

		list=new ArrayList<HashMap<String, String>>();
		List<Note> allnotes = Note.findWithQuery(Note.class, "Select * from Note WHERE shownote = '1'");
		for(Note currentnote : allnotes){

			HashMap<String,String> map = new HashMap<String,String>();
			map.put("tvTitle", currentnote.getTitle());
			map.put("tvCreate", currentnote.getCreationtime());
			list.add(map);

		}

		//adapter.notifyDataSetChanged();
		String strCout = "(" + list.size() + ")";
		textViewheaderTitle.setText("NOTE " + strCout);
		//sortType=SORTTYPE.ALPHABET;
		//updateGridView();
		//updatePintrestView();

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
				currentnote.setShownote("0");
				currentnote.save();
			} else {
				currentnote.setShownote("1");
				currentnote.save();
			}

		}
	}
}
