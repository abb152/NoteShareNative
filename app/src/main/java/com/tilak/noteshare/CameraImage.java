package com.tilak.noteshare;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tilak.db.NoteElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CameraImage extends Activity {

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_image);

        // Retrieving image Uri from Bundle
        Bundle b = getIntent().getExtras();
        Uri mediaUri = Uri.parse(b.getString("image"));
        ImageView image = (ImageView)findViewById(R.id.camera_image);

        if(getIntent().hasExtra("image")) {
            try {
                // Converting Uri to Bitmap
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mediaUri);
                // Setting bitmap to ImageView
                image.setImageBitmap(bitmap);
            } catch (Exception e) {}
        }
    }

    public void done(View v) {
        finish();

        try {
            // Saving Image file
            String timestamp = String.valueOf(System.currentTimeMillis());
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + "IMG-" + timestamp + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mediaStorageDir));

            // Refreshing Gallery to view Image in Gallery
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, mediaStorageDir.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Toast to display image path
            Toast.makeText(getApplication(), mediaStorageDir.toString(), Toast.LENGTH_LONG).show();

            // Saving to database
            NoteElement noteElement = new NoteElement(1L, 1, "yes", "image", "IMG-" + timestamp + ".jpg");
            noteElement.save();
        } catch (FileNotFoundException e) {}
    }

    public void crop(View v){}

    public void cancel(View v){
        finish();
        Toast.makeText(getApplication(), "Photo Discarded", Toast.LENGTH_LONG).show();
    }
}
