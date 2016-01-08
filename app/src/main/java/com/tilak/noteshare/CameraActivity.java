
package com.tilak.noteshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraActivity extends Activity {

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;

    private static final int ROTATE_NINETY_DEGREES = 90;

    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";

    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";

    private static final int ON_TOUCH = 1;

    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;

    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    private static boolean IMAGE_SET = false;

    Bitmap croppedImage, bitmap;

    String noteid;
    boolean isNoteIdNull;
    NoteMainActivity noteMainActivity;
    CropImageView cropImageView;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);

        Bundle bundle = getIntent().getExtras();
        noteid = bundle.getString("noteid");
        isNoteIdNull = bundle.getBoolean("isNoteIdNull");

        // Initialize components of the app
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        //Sets the rotate button
        findViewById(R.id.Button_rotate).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(IMAGE_SET) {
                    cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
                    croppedImage = rotateBitmap(croppedImage, 90);
                }else{
                    Toast.makeText(getApplicationContext(), "Please choose an Image First.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Sets initial aspect ratio to 10/10, for demonstration purposes
        //cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        //cropImageView.setAspectRatio(1, 1);
        //cropImageView.setScaleType();
        //cropImageView.setFixedAspectRatio(false);
        findViewById(R.id.Button_done).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (IMAGE_SET) {
                    showImageChooserAlertWith("SELECT IMAGE SIZE", CameraActivity.this);
                }else{
                    Toast.makeText(getApplicationContext(), "Please choose an Image First.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.Button_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.Button_load).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        startActivityForResult(getPickImageChooserIntent(), 200);

    }

    public Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void done() {
        finish();

        try {
            // Saving Image file
            FileNameGenerator fileNameGenerator = new FileNameGenerator();
            String filename = fileNameGenerator.getFileName("IMAGE");

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + filename);

            int originalHeight = croppedImage.getHeight();
            int originalWidth = croppedImage.getWidth();

            Bitmap scaledImage = createScaledBitmap(croppedImage, originalHeight, originalWidth);

            scaledImage.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(mediaStorageDir)); //87


            // Refreshing Gallery to view Image in Gallery
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, mediaStorageDir.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Saving to database
            if (isNoteIdNull) { makeNote(); }

            if (!isNoteIdNull) {
                NoteElement noteElement = new NoteElement(Long.parseLong(noteid), getNoteElementOrderNumber(), "yes", "image", filename ,"","");
                noteElement.save();
                modifyNoteTime();
            }

        } catch (FileNotFoundException e) {}
    }

    public Bitmap createScaledBitmap(Bitmap originalBitmap, int originalHeight, int originalWidth){

        if(originalWidth > 800){
            float originalRatio = originalWidth/originalHeight;
            int newWidth = 800;
            float widthRatio = originalWidth / newWidth;

            int newHeight = Math.round(originalHeight / widthRatio);

            return Bitmap.createScaledBitmap(originalBitmap,newWidth,newHeight,false);

        }
        return originalBitmap;
    }

    public void makeNote() {
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = formatter.format(new Date());
        try {
            Note note = new Note("NOTE", "", "#FFFFFF", "0", 0L, "0", "#FFFFFF", currentDateStr, currentDateStr, "0", 0, stringToDate(currentDateStr), stringToDate(currentDateStr));
            note.save();
            noteMainActivity.noteIdForDetails = note.getId().toString();
            noteid = noteMainActivity.noteIdForDetails;
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        isNoteIdNull = false;
    }

    public long stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
    }//

    public void modifyNoteTime() {
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = formatter.format(new Date());
        try{
        Note n = Note.findById(Note.class, Long.parseLong(noteid));
        n.setModifytime(currentDateStr);
        n.setMtime(stringToDate(currentDateStr));
        n.save();
        }catch(ParseException pe){
            pe.printStackTrace();
        }
    }

    public void cancel(View v){
        finish();
        Toast.makeText(getApplication(), "Photo Discarded", Toast.LENGTH_LONG).show();
    }

    public int getNoteElementOrderNumber(){
        int lastNumber=0;
        List<NoteElement> ne = NoteElement.findWithQuery(NoteElement.class, "SELECT ORDERNUMBER FROM NOTE_ELEMENT WHERE NOTEID = " + Long.parseLong(noteid));
        if(ne.size() > 0)
            return lastNumber = (ne.get(ne.size()-1).getOrderNumber()) + 1;
        else
            return 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            ((CropImageView) findViewById(R.id.CropImageView)).setImageUri(imageUri);
            IMAGE_SET = true;
            try {
                croppedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }


    void showImageChooserAlertWith(String message, Context context) {

        final Dialog dialog = new Dialog(context);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.chooseimagealertview,
                null, false);

        TextView textViewTitleAlert = (TextView) contentView
                .findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText(message);
        textViewTitleAlert.setTextColor(Color.WHITE);

        TextView tvFullSize = (TextView) contentView
                .findViewById(R.id.tvFullSize);

        TextView tvCropSize = (TextView) contentView
                .findViewById(R.id.tvCropSize);

        TextView tvCancel = (TextView) contentView
                .findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        tvFullSize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                done();
                dialog.dismiss();
            }
        });

        tvCropSize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // System.exit(0);
                croppedImage = cropImageView.getCroppedImage();
                done();
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(contentView);
        dialog.show();

    }
}
