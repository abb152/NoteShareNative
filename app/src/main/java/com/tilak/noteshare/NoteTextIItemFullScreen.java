package com.tilak.noteshare;

import com.tilak.dataAccess.DataManager;
import com.tilak.datamodels.NoteListDataModel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NoteTextIItemFullScreen extends Activity {

	public ImageView imageviewfullscreenmode;
	 Button imageClose ;
	 EditText textViewfullscreenmode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notetextlist_fullscreen_activity);
		
		imageviewfullscreenmode = (ImageView) findViewById(R.id.imageviewfullscreenmode);
		
		

		textViewfullscreenmode = (EditText) findViewById(R.id.textViewfullscreenmode);
		
		NoteListDataModel notelistitem = DataManager.sharedDataManager()
				.getNotelistData();
		//imageviewfullscreenmode.setImageBitmap(notelistitem.getBitmap());
		
		textViewfullscreenmode.setText(notelistitem.getSpannableString());
		
		imageClose=(Button) findViewById(R.id.imageClose);
		imageClose.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	});
	}
}
