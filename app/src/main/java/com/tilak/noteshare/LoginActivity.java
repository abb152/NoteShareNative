package com.tilak.noteshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
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
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.tilak.db.Config;
import com.tilak.db.Sync;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String SENDER_ID = "1766986306";
    GoogleCloudMessaging gcm;
    String msg;
    String regid;
    public String socialid, fullname, useremail, profilePicture, loginType;

    String responseFbId = "", responseGpId ="";
    static String responseServerId = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //List<Note> notes = Note.findWithQuery(Note.class,"Select * from NOTE INNER JOIN NOTE_ELEMENT on NOTE.id = NOTE_ELEMENT.noteid where NOTE_ELEMENT.type = 'checkbox'");
        //Log.e("jay size",String.valueOf(notes.size()));

        List<Config> config = Config.listAll(Config.class);
        if(config.size() == 0) {
            Config c = new Config("", "", "", "", "", "", 0, "", "", "", "", "MODIFIED_TIME", "DETAIL",0);
            c.save();

            long currentTime = RegularFunctions.getCurrentTimeLong();

            Sync sync = new Sync( currentTime, currentTime, currentTime, currentTime, 0l, 1);
            sync.save();
        }

        Config configCheck = Config.findById(Config.class,1l);
        if(!configCheck.fbid.isEmpty() || !configCheck.googleid.isEmpty()){
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String> (){

            boolean gcmIdReceived = false;

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    gcmIdReceived = true;
                    Log.e("jay regid", regid);

                } catch (IOException ex) {
                    gcmIdReceived = false;
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                if(gcmIdReceived){
                    try {
                        sendLogin();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"Oops! Something went wrong!",Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null,null,null);
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
        public void onSuccess(final LoginResult loginResult) {


            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            if (BuildConfig.DEBUG) {
                                FacebookSdk.setIsDebugEnabled(true);
                                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);


                                socialid = object.optString("id");
                                useremail = object.optString("email");
                                fullname = object.optString("name");
                                loginType = "fb";

                                Uri.Builder builder = new Uri.Builder();
                                builder.scheme("https")
                                        .authority("graph.facebook.com")
                                        .appendPath(socialid)
                                        .appendPath("picture")
                                        .appendQueryParameter("width", "200")
                                        .appendQueryParameter("height", "200");

                                Uri pictureUri = builder.build();

                                profilePicture = pictureUri.toString();

                                getGcmId();
                                facebookLogout();

                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email");
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
        Intent i = new Intent(getApplication(), InteroductionActivity.class);
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
                fullname = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                socialid = currentPerson.getId();
                useremail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                loginType = "gp";
                profilePicture = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + 200;
                getGcmId();

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

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public void getGcmId(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            /*regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }*/
            Log.e("jay in", "gcmId");

            registerInBackground();

        } else {
            Log.e("Jay", "No valid Google Play Services APK found.");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    boolean once = false;

    public void sendLogin() throws JSONException, ClientProtocolException, IOException {

        if(!once) {
            once = true;
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            if (!isFinishing()) {
                progressDialog.show();
            }

            new AsyncTask<Void, Void, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(Void... params) {

                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                    try {
                        String loginjson = loginJson(loginType, socialid, fullname, useremail, profilePicture, regid).toString();
                        String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "user/sociallogin1", loginjson);
                        Log.e("jay response", response);

                        String responseName = null;
                        String responseEmail = null;
                        String responseProfilePic = null;

                        try {
                            JSONObject responseJson = new JSONObject(response);

                            responseServerId = responseJson.get("_id").toString();
                            responseName = responseJson.get("name").toString();
                            responseEmail = responseJson.get("email").toString();
                            responseProfilePic = responseJson.get("profilepic").toString();

                            createDirectory();

                            if (loginType.equals("fb"))
                                responseFbId = responseJson.get("fbid").toString();
                            else if (loginType.equals("gp"))
                                responseGpId = responseJson.get("googleid").toString();
                        } catch (JSONException je) {

                        }
                        if (responseServerId.isEmpty()) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
                        } else {
                            Config c = Config.findById(Config.class, 1l);
                            c.setFirstname(responseName);
                            c.setEmail(responseEmail);
                            c.setFbid(responseFbId);
                            c.setGoogleid(responseGpId);
                            c.setProfilepic(responseProfilePic);
                            c.setServerid(responseServerId);
                            c.setAppversion(getAppVersion(LoginActivity.this));
                            c.setDeviceid(regid);
                            c.save();

                            Long currentTimeLong = RegularFunctions.getCurrentTimeLong();
                            Long initialTimeLong = 1420113600000l;

                            Sync s = Sync.findById(Sync.class, 1l);
                            s.setFolderLocalToServer(initialTimeLong);
                            s.setFolderServerToLocal(initialTimeLong);
                            s.setNoteLocalToServer(initialTimeLong);
                            s.setNoteServerToLocal(initialTimeLong);
                            s.setLastSyncTime(0l);
                            s.setSyncType(1);
                            s.save();

                            getBitmapFromURL(c.profilepic);

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            goToMain();
                            finish();
                        }
                    } catch (JSONException je) {

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    finish();
                }
            }.execute(null, null, null);
        }
    }

    public JSONObject loginJson(String loginType, String socialid, String fullname, String useremail , String profilePicture, String regid) throws JSONException {

        JSONObject login = new JSONObject();
        if (loginType.equals("fb")) {
            login.put("fbid", socialid);
        } else if (loginType.equals("gp")) {
            login.put("googleid", socialid);
        }
        login.put("name", fullname);
        login.put("email", useremail);
        login.put("profilepic", profilePicture);
        login.put("deviceid", regid);
        return login;
    }

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
    @Override
    public void onBackPressed() {

    }
}