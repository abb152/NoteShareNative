package com.tilak.noteshare;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tilak.db.Config;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LoginButton loginButton;
    CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;

    // Google Plus
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "LoginActivity";
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private SignInButton btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //List<Note> notes = Note.findWithQuery(Note.class,"Select * from NOTE INNER JOIN NOTE_ELEMENT on NOTE.id = NOTE_ELEMENT.noteid where NOTE_ELEMENT.type = 'checkbox'");
        //Log.e("jay size",String.valueOf(notes.size()));

        List<Config> config = Config.listAll(Config.class);
        if(config.size() == 0) {
            Config c = new Config("", "", "", "", "", "", 0, "", "", "", "", "MODIFIED_TIME", "DETAIL");
            c.save();
        }

        Config configCheck = Config.findById(Config.class,1l);
        if(!configCheck.fbid.isEmpty() || !configCheck.googleid.isEmpty()){
            //goToMain();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_activity);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btnFacebookSignin);
        loginButton.setReadPermissions("public_profile, email");
        loginButton.registerCallback(callbackManager, facebookCallback);

        /*mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
                mProfileTracker.stopTracking();
                Profile.setCurrentProfile(profile1);
            }
        };
        mProfileTracker.startTracking();*/

        // Google Plus
        btnSignIn = (SignInButton) findViewById(R.id.btnGoogleSignIn);
        setGooglePlusButtonText(btnSignIn,"Google+");
        // Button click listeners
        btnSignIn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setPadding(-1,0,0,0);
                tv.setText(buttonText);
                return;
            }
        }
    }


    // Facebook Callback
    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            /*ProfileTracker profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    this.stopTracking();
                    Profile.setCurrentProfile(currentProfile);

                }
            };
            profileTracker.startTracking();*/


            if(Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        Log.v("facebook - profile", profile2.getFirstName());
                        mProfileTracker.stopTracking();
                    }
                };
                mProfileTracker.startTracking();
            }
            else {
                Profile profile = Profile.getCurrentProfile();
                Log.v("facebook - profile", profile.getFirstName());
            }
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            if (BuildConfig.DEBUG) {
                                FacebookSdk.setIsDebugEnabled(true);
                                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

                                Profile profile = Profile.getCurrentProfile();
                                if (profile != null) {
                                    //String firstName = profile.getFirstName();
                                    //String lastName = profile.getLastName();
                                    String name = profile.getName();
                                    Uri pictureUri = profile.getProfilePictureUri(200, 200);
                                    String email = object.optString("email");
                                    String uid = object.optString("id");
                                    try {
                                        sendLogin(uid, name, email, pictureUri.toString(), "fb");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    facebookLogout();
                                } else {
                                    facebookLogout();
                                    Toast.makeText(getApplication(), "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getApplication(), "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
        }
    };

    public void facebookLogout() {
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK)
                mSignInClicked = false;

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void main(View v){
        goToMain();
    }

    public void goToMain(){
        createDirectory();
        Config c = Config.findById(Config.class, 1l);
        String fname = c.firstname;
        Intent i = new Intent(getApplication(), UserProfileActivity.class);
        //i.putExtra("FolderId","-1");
        i.putExtra("fname", fname);
        i.putExtra("hide", "hide");
        startActivity(i);
        finish();
    }

    public void getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            try {
                // Saving Image file
                String profilePicture = String.valueOf("profile");
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/.NoteShare/" + profilePicture + ".jpg");
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mediaStorageDir));

                // Refreshing Gallery to view Image in Gallery
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, mediaStorageDir.getAbsolutePath());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


            } catch (FileNotFoundException e) {}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        //Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        getProfileInformation();
        signOutFromGplus();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoogleSignIn:
                // Signin button clicked
                signInWithGplus();
                break;
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            GooglePlayServicesUtil.showErrorDialogFragment(mConnectionResult.getErrorCode(),this,RC_SIGN_IN);

        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                //String firstName = currentPerson.getName().getGivenName();
                //String lastName = currentPerson.getName().getFamilyName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String personGooglePlusId = currentPerson.getId();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + 200;

                try {
                    sendLogin(personGooglePlusId, personName, email, personPhotoUrl, "gp");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void sendLogin(String id, String name, String email, String profilePic, String type) throws JSONException, ClientProtocolException, IOException {


        ArrayList<String> stringData = new ArrayList<String>();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> resonseHandler = new BasicResponseHandler();
        HttpPost postMethod = new HttpPost("http://104.197.122.116/user/sociallogin");

        JSONObject json = new JSONObject();
        if (type.equals("fb")) {
            json.put("fbid", id);
        } else if (type.equals("gp")) {
            json.put("googleid", id);
        }
        json.put("name", name);
        json.put("email", email);
        json.put("profilepic", profilePic);
        //postMethod.setHeader("Content-Type", "application/json" );
        postMethod.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
        String response = httpClient.execute(postMethod,resonseHandler);
        JSONObject responseJson = new JSONObject(response);

        responseServerId = responseJson.get("_id").toString();
        String responseName = responseJson.get("name").toString();
        String responseEmail = responseJson.get("email").toString();
        String responseProfilePic = responseJson.get("profilepic").toString();


        createDirectory();


        if (type.equals("fb"))
            responseFbId = responseJson.get("fbid").toString();
        else if (type.equals("gp"))
            responseGpId = responseJson.get("googleid").toString();

        if (responseServerId.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No response from Server", Toast.LENGTH_LONG).show();
        } else {
            Config c = Config.findById(Config.class, 1l);
            c.setFirstname(responseName);
            c.setEmail(responseEmail);
            c.setFbid(responseFbId);
            c.setGoogleid(responseGpId);
            c.setProfilepic(responseProfilePic);
            c.setServerid(responseServerId);
            c.save();
            getBitmapFromURL(c.profilepic);
            goToMain();
        }
    }

    String responseFbId = "", responseGpId ="";
    static String responseServerId = "";

    /******* create directory start *******/

    // create directory NoteShare in internal memory
    // and Images and Audio folder inside NoteShare folder for images, profile picture and audio notes

    public void createDirectory() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            // get the URI

            // 1. Get the external storage directory
            String appName = LoginActivity.this.getString(R.string.app_name);
            String imgDir = "/NoteShare/NoteShare Images";
            String audioDir = "/NoteShare/NoteShare Audio";
            String extraDir = "/NoteShare/.NoteShare";

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

            // 4. Creating Audio Directory in NoteShare Directory
            File extraDirectory = new File(Environment.getExternalStorageDirectory(), extraDir);
            if (!extraDirectory.exists()) {
                if (!extraDirectory.mkdirs()) { Log.e(TAG, "Failed to create Extra directory."); }
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



}