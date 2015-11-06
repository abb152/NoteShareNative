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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
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
    /*if (android.os.Build.VERSION.SDK_INT > 9) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }*/
    //getBitmapFromURL("https://lh5.googleusercontent.com/-NcmQ7wanfmo/AAAAAAAAAAI/AAAAAAAALxY/_Vfnvjx6a40/photo.jpg?sz=250");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Config> config = Config.listAll(Config.class);
        Log.e("jay config size: ", String.valueOf(config.size()));
        //Log.e("jay config id: ", String.valueOf(config.get(0).getId()));
        if(config.size() == 0) {
            Config c = new Config("", "", "", "", "", "", 0, "", "", "", "");
            c.save();
            Log.e("jay config id: ", String.valueOf(c.getId()));
        }

        Config configCheck = Config.findById(Config.class,1l);
        if(!configCheck.fbid.isEmpty() || !configCheck.googleid.isEmpty()){
            goToMain();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_activity);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btnFacebookSignin);
        loginButton.setReadPermissions("public_profile, email");
        loginButton.registerCallback(callbackManager, facebookCallback);

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
                mProfileTracker.stopTracking();
                Profile.setCurrentProfile(profile1);
            }
        };
        mProfileTracker.startTracking();

        // Google Plus
        btnSignIn = (SignInButton) findViewById(R.id.btnGoogleSignIn);
        // Button click listeners
        btnSignIn.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    // Facebook Callback
    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            if (BuildConfig.DEBUG) {
                                FacebookSdk.setIsDebugEnabled(true);
                                FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

                                Log.e("Harsh: AccessToken", AccessToken.getCurrentAccessToken().toString());

                                Profile profile = Profile.getCurrentProfile();
                                if (profile != null) {
                                    String firstName = profile.getFirstName();
                                    String lastName = profile.getLastName();
                                    Uri pictureUri = profile.getProfilePictureUri(200, 200);
                                    String email = object.optString("email");
                                    String uid = object.optString("id");
                                    //Toast.makeText(getApplicationContext(), "" + uid + " " + firstName + " " + lastName + " " + email + " " + pictureUri.toString(), Toast.LENGTH_LONG).show();

                                    Config c = Config.findById(Config.class,1l);
                                    c.setFirstname(firstName);
                                    c.setLastname(lastName);
                                    c.setEmail(email);
                                    c.setFbid(uid);
                                    c.setProfilepic(pictureUri.toString());
                                    c.save();
                                    getBitmapFromURL(c.profilepic);
                                    goToMain();
                                    facebookLogout();
                                } else {
                                    //facebookLogout();
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
        }
    };

    public void facebookLogout() {
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

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
        Intent i = new Intent(getApplication(), MainActivity.class);
        i.putExtra("FolderId", "-1");
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
                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/" + profilePicture + ".jpg");
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
                String firstName = currentPerson.getName().getGivenName();
                String lastName = currentPerson.getName().getFamilyName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String personGooglePlusId = currentPerson.getId();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + 200;

                Config c = Config.findById(Config.class,1l);
                c.setFirstname(firstName);
                c.setLastname(lastName);
                c.setEmail(email);
                c.setGoogleid(personGooglePlusId);
                c.setProfilepic(personPhotoUrl);
                c.save();
                getBitmapFromURL(c.profilepic);
                goToMain();

                Log.e(TAG, "Name: " + personName +
                        ", person Id:" + personGooglePlusId +
                        ", plusProfile: " + personGooglePlusProfile +
                        ", email: " + email +
                        ", Image: " + personPhotoUrl);

                /*Toast.makeText(getApplicationContext(), "Name: " + personName +
                        ", person Id:" + personGooglePlusId +
                        ", plusProfile: " + personGooglePlusProfile +
                        ", email: " + email +
                        ", Image: " + personPhotoUrl, Toast.LENGTH_LONG).show();*/

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                /*personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;*/

                //new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

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

}