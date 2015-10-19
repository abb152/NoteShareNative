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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class CameraImage extends Activity {

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_image);

        Bundle b = getIntent().getExtras();
        Uri mediaUri = Uri.parse(b.getString("image"));
        ImageView image = (ImageView)findViewById(R.id.camera_image);

        if(getIntent().hasExtra("image")) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mediaUri);
                image.setImageBitmap(bitmap);
                //Toast.makeText(getApplication(), bitmap.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {}
        } else {
            //Toast.makeText(getApplication(), "Cannot find any extra from NoteMainActivity", Toast.LENGTH_LONG).show();
        }
    }

    public void done(View v) {
        finish();

        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + "IMG-" + timestamp + ".jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mediaStorageDir));

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, mediaStorageDir.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar isso
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Toast.makeText(getApplication(), mediaStorageDir.toString(), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {}
    }

    public void crop(View v){}

    public void cancel(View v){
        finish();
        Toast.makeText(getApplication(), "Photo Discarded", Toast.LENGTH_LONG).show();
    }
}
