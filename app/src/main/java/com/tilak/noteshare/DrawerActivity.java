package com.tilak.noteshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tilak.adpters.MenuOpenInterface;
import com.tilak.adpters.SlideMenuAdapter;
import com.tilak.datamodels.SideMenuitems;
import com.tilak.datamodels.SlideMenu;
import com.tilak.db.Config;
import com.tilak.db.Folder;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DrawerActivity extends Activity implements MenuOpenInterface {
	// private static String TAG = MainActivity.class.getSimpleName();
	public String[] mNavigationDrawerItemTitles;
	public ArrayList<SideMenuitems> arrMenuTitle;
	public DrawerLayout mDrawerLayout;
	public ListView mDrawerList;
	ArrayList<SideMenuitems> arrMenu;

	// ArrayAdapter<String> adapter;

	public SlideMenuAdapter adapter;

	private static boolean isLaunch = true;

	// private String mActivityTitle;

	public TextView mTitleTextView;
	protected static int position;
	public SlideMenu menu;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sliding_activity);

		// DataManager.sharedDataManager().printname();

		// Loading menu file

		// Do what you need for this SDK
		/*if (Build.VERSION.SDK_INT >= 21) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.header_bg));
		}*/

		String strresponse = loadJSONFromAsset();
		try {
			JSONObject jsonObject = new JSONObject(strresponse);
			menu = new SlideMenu(jsonObject);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mNavigationDrawerItemTitles = getResources().getStringArray(
				R.array.navigation_drawer_items_array);

		arrMenuTitle = menu.getSideMenuitems();
		arrMenu = new ArrayList<SideMenuitems>();

		for (int i = 0; i < arrMenuTitle.size(); i++) {
			SideMenuitems items = arrMenuTitle.get(i);
			int menuid = Integer.parseInt(items.getMenuid());
			switch (menuid) {
				case 1:
					items.setResourceId(R.drawable.note_menu_icon);
					break;
				case 2:
					items.setResourceId(R.drawable.folder_icon);
					break;
				/*case 3:
					items.setResourceId(R.drawable.folder_icon);
					break;*/
				/*case 4:
					items.setResourceId(R.drawable.about_us_icon);
					break;
				case 5:
					items.setResourceId(R.drawable.termsandcondition_icon);
					break;*/
				case 3:
					items.setResourceId(R.drawable.notification_icon);
					break;
				/*case 7:
					items.setResourceId(R.drawable.rate_us_icon);
					break;
				case 8:
					items.setResourceId(R.drawable.likeus_on_icon);
					break;
				case 9:
					items.setResourceId(R.drawable.send_feedback_icon);
					break;
				case 10:
					items.setResourceId(R.drawable.invite_icon);
					break;*/
				case 4:
					items.setResourceId(R.drawable.invite_icon);
					break;
				case 5:
					items.setResourceId(R.drawable.about_us_icon);
					break;
				case 6:
					items.setResourceId(R.drawable.setting_icon);
					break;
				case 7:
					items.setResourceId(R.drawable.logout_icon);
					break;
				default:
					break;
			}
			arrMenu.add(items);

		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// adapter = new
		// ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mNavigationDrawerItemTitles);
		adapter = new SlideMenuAdapter(this, mNavigationDrawerItemTitles,
				arrMenu);
		mDrawerList.setAdapter(adapter);

		mDrawerList.setSelector(android.R.color.transparent);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				mDrawerLayout.closeDrawers();
				// mTitleTextView.setText(mNavigationDrawerItemTitles[position]);
				openActivity(position);
				/*
				 * Bundle args = new Bundle(); args.putString("Menu",
				 * mNavigationDrawerItemTitles[position]); DetailFragment detail
				 * = new DetailFragment(); detail.setArguments(args);
				 * FragmentManager fragmentManager = getFragmentManager();
				 * fragmentManager
				 * .beginTransaction().replace(R.id.content_frame,
				 * detail).commit();
				 */

			}

		});

		if (isLaunch) {
			isLaunch = false;
			openinitilaActivity(0);
		}

	}

	protected void openinitilaActivity(int position1) {
		System.out.println("initail launch");
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	protected void openActivity(int position1) {
		position = position1;
		if (position1 == 0) {
			// Profile setting
			Config c = Config.findById(Config.class, 1L);
			String fname = c.getFirstname();
			Intent i = new Intent(this, UserProfileActivity.class);
			i.putExtra("fname", fname);
			i.putExtra("hide", "hide");
			startActivity(i);
			System.out.println("profile setting");
			//finish();
		} else {
			SideMenuitems modeldata = arrMenu.get(position1 - 1);

			int menuid = Integer.parseInt(modeldata.getMenuid());

			switch (menuid) {
				case 1: {
					System.out.println("notes");
					openinitilaActivity(0);
				}
				break;
				case 2: {
					// openinitilaActivity(0);
					System.out.println("folder");
					startActivity(new Intent(this, NewFolderMainActivity.class));
					// finish();
				}
				break;
				case 3: {
					System.out.println("notification center");
					startActivity(new Intent(this, NotificationCenterActivity.class));
					//finish();
				}
				break;
				case 4: {
					System.out.println("invite friends");
					Uri uri = Uri.parse("android.resource://com.tilak.noteshare/drawable/ic_launcher");
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("image/*");
					share.putExtra(Intent.EXTRA_STREAM, uri);
					share.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_friends_text));
					startActivity(Intent.createChooser(share, "Invite friends"));
					//finish();
				}
				break;
				case 5: {
					System.out.println("about");
					startActivity(new Intent(this, AboutNoteShareActivity.class));
					//finish();
				}
				break;
				case 6: {
					System.out.println("setting");
					startActivity(new Intent(this, SettingActivity.class));
					//finish();
				}
				break;
				case 7: {
					System.out.println("logout");
					showAlertWith("ARE YOU SURE?", "Are you sure you want to Log Out?", DrawerActivity.this);
				}
				break;
				default: {}
				break;
			}
		}

	}

	public void addListners() {}

	public String loadJSONFromAsset() {
		String json = null;
		try {
			InputStream is = getAssets().open("sidemenu.json");
			// InputStream is=getResources().openRawResource(R.raw.sidemenu);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	@Override
	public void openSlideMenu() {
		// TODO Auto-generated method stub
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			mDrawerLayout.openDrawer(mDrawerList);
		}
		//Toast.makeText(getApplicationContext(), "menu Clicked!",
				//Toast.LENGTH_LONG).show();
	}

	/*
	 * ActionBar mActionBar = getActionBar();
	 * mActionBar.setDisplayShowHomeEnabled(false);
	 * mActionBar.setDisplayShowTitleEnabled(false); LayoutInflater mInflater =
	 * LayoutInflater.from(this);
	 * 
	 * View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
	 * mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
	 * mTitleTextView.setText("My Own Title");
	 * 
	 * ImageButton imageButton = (ImageButton) mCustomView
	 * .findViewById(R.id.imageButton); imageButton.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View view) {
	 * Toast.makeText(getApplicationContext(), "Refresh Clicked!",
	 * Toast.LENGTH_LONG).show(); } });
	 * 
	 * ImageButton btnMenu = (ImageButton) mCustomView
	 * .findViewById(R.id.imageButtonMenu); btnMenu.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View view) {
	 * 
	 * if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
	 * mDrawerLayout.closeDrawer(mDrawerList); } else {
	 * mDrawerLayout.openDrawer(mDrawerList); }
	 * 
	 * Toast.makeText(getApplicationContext(), "menu Clicked!",
	 * Toast.LENGTH_LONG).show(); } });
	 * 
	 * mActionBar.setCustomView(mCustomView);
	 * mActionBar.setDisplayShowCustomEnabled(true);
	 */

	void showAlertWith(String title, String message, Context context) {

		final Dialog dialog = new Dialog(context);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater.inflate(R.layout.alert_view, null, false);

		TextView textViewTitleAlert = (TextView) contentView
				.findViewById(R.id.textViewTitleAlert);
		textViewTitleAlert.setText(title);
		textViewTitleAlert.setTextColor(Color.WHITE);
		TextView textViewTitleAlertMessage = (TextView) contentView
				.findViewById(R.id.textViewTitleAlertMessage);
		textViewTitleAlertMessage.setText(message);

		Button buttonAlertCancel = (Button) contentView
				.findViewById(R.id.buttonAlertCancel);
		Button buttonAlertOk = (Button) contentView
				.findViewById(R.id.buttonAlertOk);
		buttonAlertCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		buttonAlertOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				flushDatabase();
				finish();
				startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
			}
		});

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(contentView);
		dialog.show();

	}

	public void flushDatabase(){

		//flush config values
		Config c = Config.findById(Config.class, 1l);
		c.setFirstname("");
		c.setLastname("");
		c.setEmail("");
		c.setPassword("");
		c.setFbid("");
		c.setGoogleid("");
		c.setPasscode(0);
		c.setProfilepic("");
		c.setUsername("");
		c.setDeviceid("");
		c.setServerid("");
		c.save();

		//Config.deleteAll(Config.class);

		//delete all folders
		Folder.deleteAll(Folder.class);

		//delete all notes
		Note.deleteAll(Note.class);

		//delete all noteElements
		NoteElement.deleteAll(NoteElement.class);

	}

}
