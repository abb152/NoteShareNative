package com.noteshareapp.noteshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.noteshareapp.adpters.MenuOpenInterface;
import com.noteshareapp.adpters.SlideMenuAdapter;
import com.noteshareapp.datamodels.SideMenuitems;
import com.noteshareapp.datamodels.SlideMenu;
import com.noteshareapp.db.Config;

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
					items.setResourceId(R.drawable.ic_note);
					break;
				case 2:
					items.setResourceId(R.drawable.ic_folder);
					break;
				case 3:
					items.setResourceId(R.drawable.ic_action_notification);
					break;
				case 4:
					items.setResourceId(R.drawable.ic_rate);
					break;
				case 5:
					items.setResourceId(R.drawable.ic_like);
					break;
				case 6:
					items.setResourceId(R.drawable.ic_feedback);
					break;
				case 7:
					items.setResourceId(R.drawable.ic_action_invite);
					break;
				case 8:
					items.setResourceId(R.drawable.ic_action_setting);
					break;
				case 9:
					items.setResourceId(R.drawable.ic_logout);
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
		//Intent i = new Intent(getApplication(), MainActivity.class);
		//i.putExtra("FolderId","-1");
		//startActivity(i);
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
			finish();
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
					//finish();
				}
				break;
				case 3: {
					System.out.println("notification center");
					startActivity(new Intent(this, NotificationCenterActivity.class));
					finish();
				}
				break;
				case 4:{
					Uri uri = Uri.parse("market://details?id=" + "com.noteshareapp.noteshare");
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					// To count with Play market backstack, After pressing back button,
					// to taken back to our application, we need to add following flags to intent.
					goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
					try {
						startActivity(goToMarket);
					} catch (ActivityNotFoundException e) {
						//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + "com.wohlig.stakes")));
					}
				}
				break;
				case 5:{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/")));
					//finish();
				}
				break;
				case 6:{
					startActivity(new Intent(this, SendFeedbackActivity.class));
					finish();
				}
				break;
				case 7:{
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							System.out.println("invite friends");
							Intent share = new Intent(Intent.ACTION_SEND);
							//share.setType("image/*");
							share.setType("text/plain");
							//share.putExtra(Intent.EXTRA_STREAM, uri);
							share.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_friends_text));
							startActivity(Intent.createChooser(share, "Invite friends"));
						}
					}, 300);
					//finish();
				}
				break;
				/*case 5: {
					System.out.println("about");
					startActivity(new Intent(this, AboutNoteShareActivity.class));
					finish();
				}*/
				case 8: {
					System.out.println("setting");
					startActivity(new Intent(this, SettingActivity.class));
					finish();
				}
				break;
				/*case 9: {
					System.out.println("logout");
					showAlertWith("LOGOUT", "Are you sure you want to Log Out?", DrawerActivity.this);
				}
				break;*/
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

}
