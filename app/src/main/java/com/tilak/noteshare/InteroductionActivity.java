package com.tilak.noteshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class InteroductionActivity extends Activity {
    /** Called when the activity is first created. */
	public int finish=0;
	private float lastX;
	Button btnNext;
	ViewFlipper page;
	LinearLayout layoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interoduction_activity);

		Bundle b = getIntent().getExtras();
		final String fname = b.getString("fname");
		String hide = b.getString("hide");
        
        page = (ViewFlipper)findViewById(R.id.flipper);
        btnNext = (Button)findViewById(R.id.next);
		layoutButton = (LinearLayout) findViewById(R.id.layoutButton);

        btnNext.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("finish");
				Intent i = new Intent(InteroductionActivity.this, UserProfileActivity.class);
				i.putExtra("fname", fname);
				i.putExtra("hide", "hide");
				startActivity(i);
				// close this activity
				finish();

				//--SAVE Data
				SharedPreferences preferences = InteroductionActivity.this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("FINISHED", true);
				//editor.putBoolean("FINISHED", false);
				editor.commit();
			}
		});

		layoutButton.setVisibility(View.GONE);
    }

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
	}


	// Using the following method, we will handle all screen swaps.
	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {


			case MotionEvent.ACTION_DOWN:
				lastX = touchevent.getX();
				break;
			case MotionEvent.ACTION_UP:
				float currentX = touchevent.getX();


				// Handling left to right screen swap.
				if (lastX < currentX) {


					// If there aren't any other children, just break.
					if (page.getDisplayedChild() == 0)
						break;


					// Next screen comes in from left.
					page.setInAnimation(this, R.anim.slide_right_out);
					// Current screen goes out from right.
					page.setOutAnimation(this, R.anim.slide_right_in);


					// Display next screen.
					page.showNext();

					finish--;

					layoutButton.setVisibility(View.GONE);
				}


				// Handling right to left screen swap.
				if (lastX > currentX) {


					// If there is a child (to the left), kust break.
					if (page.getDisplayedChild() == 1)
						break;


					// Next screen comes in from right.
					page.setInAnimation(this, R.anim.slide_left_in);
					// Current screen goes out from left.
					page.setOutAnimation(this, R.anim.slide_left_out);


					// Display previous screen.
					page.showPrevious();

					finish++;
					if(finish == 3)
						layoutButton.setVisibility(View.VISIBLE);
				}
				break;
		}
		return false;
	}

}