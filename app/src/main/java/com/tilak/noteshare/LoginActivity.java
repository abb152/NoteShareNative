package com.tilak.noteshare;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements OnClickListener,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 0;
	private static final String TAG = "MainActivity";
	private static final int PROFILE_PIC_SIZE = 400;

	public Button btnsignUP, btnSignIn;
	public EditText loginEmail, loginPassowrd;

	// Facebook Variables
	private CallbackManager mCallbackManager;
	private AccessToken token;
	private AccessTokenTracker mTracker;
	private ProfileTracker mProfileTracker;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;

	// Google Variables
	private SignInButton btnGoogleSignIn;
	private ImageView imgProfilePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		facebookInitialize();
		setContentView(R.layout.login_activity);
		initlizeUIElement(null);
		loginButton();
		deleteProfilePic();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

	}

	public void initlizeUIElement(View contentview) {
		btnSignIn=(Button) findViewById(R.id.btnloginsignin);
		btnsignUP=(Button) findViewById(R.id.btnloginsignup);
		loginPassowrd = (EditText)findViewById(R.id.loginpassword);
		loginEmail = (EditText)findViewById(R.id.loginemail);
		loginPassowrd.setTypeface(Typeface.DEFAULT);
		loginPassowrd.setTransformationMethod(new PasswordTransformationMethod());
		btnGoogleSignIn = (SignInButton) findViewById(R.id.google_login);

		addlistners();
	}

	public void addlistners() {
		btnSignIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String email = loginEmail.getText().toString();
				String password = loginPassowrd.getText().toString();

				email = email.trim();
				password = password.trim();

				if (email.equals("") || password.equals("")) {
					Toast.makeText(getApplication(), R.string.enter_fields, Toast.LENGTH_LONG).show();
				} else if (!isValidEmail(email)) {
					loginEmail.setError("Invalid Email");
				} else {
					Log.v(TAG, "Email: " + email +
								"/n Password: " + password);
				}
			}
		});
		btnsignUP.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
				finish();
			}
		});
		btnGoogleSignIn.setOnClickListener(this);
	}

	public void deleteProfilePic() {
		File file= new File(android.os.Environment.getExternalStorageDirectory()+ "/NoteShare/Images/profile-picture.jpg");
		if(file.exists()) { file.delete(); }
	}

	// Email Validation
	private boolean isValidEmail(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// Facebook Login Button
	public void facebookInitialize() {
		FacebookSdk.sdkInitialize(getApplicationContext());
		mCallbackManager = CallbackManager.Factory.create();

		mTracker =  new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {}
		};
		mProfileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {}
		};
		mTracker.startTracking();
		mProfileTracker.startTracking();
	}

	public void loginButton() {
		LoginButton button = (LoginButton) findViewById(R.id.fb_login );
		button.setReadPermissions("email");
		button.registerCallback(mCallbackManager, mFacebookCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if(resultCode != RESULT_OK) {
				mSignInClicked = false;
			}
			mIntentInProgress = false;

			if(!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			token = loginResult.getAccessToken();
			Profile profile = Profile.getCurrentProfile();
			//displayProfile(profile);
			String fbId = profile.getId();
			String fbFirstName = profile.getFirstName();
			String fbLastName = profile.getLastName();
			Uri fbProfileUri = profile.getProfilePictureUri(250, 250);
			//final String[] fbEmail = new String[1];

			// Facebook Graph Class
			/*GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
				@Override
				public void onCompleted(JSONObject user, GraphResponse response) {
					try {
						fbEmail[0] = user.getString("email");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).executeAsync();*/

			/*Log.v(TAG, "Facebook Id: " + fbId +
						"/nFacebook First Name: " + fbFirstName +
						"/nFacebbok Last name: " + fbLastName +
						"/nFacebook Email: " + fbEmail[0] +
						"/nFacebook Profile Pic Url: " + fbProfileUri.toString());*/

			Toast.makeText(getApplication(), "Facebook Id: " + fbId +
					", Facebook First Name: " + fbFirstName +
					", Facebbok Last name: " + fbLastName +
					", Facebook Email: " + null +
					", Facebook Profile Pic Url: " + fbProfileUri.toString(), Toast.LENGTH_LONG).show();

		}

		@Override
		public void onCancel() {}

		@Override
		public void onError(FacebookException e) {}
	};

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		mTracker.stopTracking();
		mProfileTracker.stopTracking();
		if (mGoogleApiClient.isConnected()) { mGoogleApiClient.disconnect(); }
	}

	@Override
	public void onConnected(Bundle bundle) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		getProfileInformation();
	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.connect();
		updateUI(false);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.google_login) {
			signInWithGplus();
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	/** Method to resolve any signin errors **/
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) { btnSignIn.setVisibility(View.GONE); }
		else { btnSignIn.setVisibility(View.VISIBLE); }
	}

	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personGoogleId = currentPerson.getId();
				String personFirstName = currentPerson.getName().getGivenName();
				String personLastName = currentPerson.getName().getFamilyName();
				String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String personPhotoUrl = currentPerson.getImage().getUrl();
				//String personName = currentPerson.getDisplayName();
				//String personGooglePlusProfile = currentPerson.getUrl();

				Log.v(TAG, "Google Id: "  + personGoogleId +
							"/nFirst Name: " + personFirstName +
							"/nLast Name: " + personLastName +
							"/nEmail: " + personEmail +
							"/nImage: " + personPhotoUrl);

				//txtName.setText(personName);
				//txtEmail.setText(email);

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Background Async task to load user profile picture from url
	 * */
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

}
