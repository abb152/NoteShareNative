package com.tilak.noteshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tilak.db.Config;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SendFeedbackActivity extends DrawerActivity {
	public RelativeLayout layoutHeder;
	public ImageButton btnheaderMenu, btnsequence, btncalander;
	public TextView textheadertitle, textViewSubHeaderTitle;
	public EditText textViewFeedbackText;
	public LinearLayout layoutTitleHeaderview;
	public Button btSubmit;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate your activity layout here!
		View contentView = inflater
				.inflate(R.layout.sendfeedback_activity, null, false);
		mDrawerLayout.addView(contentView, 0);
		initlizeUIElement(contentView);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	void initlizeUIElement(View contentView) {
		//mainHeadermenue
		layoutHeder = (RelativeLayout) contentView.findViewById(R.id.mainHeadermenue);
		btnheaderMenu = (ImageButton) layoutHeder.findViewById(R.id.imageButtonHamburg);

		btnsequence = (ImageButton) layoutHeder.findViewById(R.id.imageButtonsquence);
		btncalander = (ImageButton) layoutHeder.findViewById(R.id.imageButtoncalander);
		btncalander.setVisibility(View.GONE);
		btnsequence.setVisibility(View.GONE);

		textViewFeedbackText = (EditText) findViewById(R.id.etFeedback);


		///textheadertitle=(TextView) layoutHeder.findViewById(R.id.textViewheaderTitle);
		//textheadertitle.setText("");


		layoutTitleHeaderview = (LinearLayout) contentView.findViewById(R.id.titleHeaderview1);
		textViewSubHeaderTitle = (TextView) layoutTitleHeaderview.findViewById(R.id.textViewHeaderTitle1);
		textViewSubHeaderTitle.setText("Send Feedback");

		btSubmit = (Button) findViewById(R.id.btSubmit);

		addListners();
	}

	@Override
	public void addListners() {
		// TODO Auto-generated method stub
		super.addListners();
		btnheaderMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openSlideMenu();
			}
		});
		btSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					sendFeedback();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void sendFeedback() throws JSONException, ClientProtocolException, IOException {

		Config con = Config.findById(Config.class, 1L);
		//String email = con.email;
		String message = textViewFeedbackText.getText().toString();
		String serverId = LoginActivity.responseServerId;

		Log.e("jay in getServerData","");
		ArrayList<String> stringData = new ArrayList<String>();
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ResponseHandler<String> resonseHandler = new BasicResponseHandler();
		HttpPost postMethod = new HttpPost("http://104.197.122.116/feed/save");

		JSONObject json = new JSONObject();
		//json.put("user", "56120af8a89c4c8f043a0285");
		json.put("user", serverId);
		//json.put("email", email);
		json.put("text", message);
		//postMethod.setHeader("Content-Type", "application/json" );
		postMethod.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
		String response = httpClient.execute(postMethod,resonseHandler);
		Log.e("jay response :", response);
		JSONObject responseJson = new JSONObject(response);
		String value = responseJson.get("value").toString();
		//String message = responseJson.get("message").toString();

		if (value.equals("true")) {
			Toast.makeText(getApplicationContext(), "Thank you for your feedback", Toast.LENGTH_LONG).show();
			finish();
			Intent in = new Intent(this, MainActivity.class);
			startActivity(in);
		} else if (value.equals("false")) {
			Toast.makeText(getApplicationContext(), "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
		}

	}
}
