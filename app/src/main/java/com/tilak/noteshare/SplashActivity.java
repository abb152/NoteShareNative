package com.tilak.noteshare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity
{
	
	// Splash screen timer
  public ImageView imageviewSplashLogo;
	
	private static int SPLASH_TIME_OUT = 4000;
 
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        
     // Do what you need for this SDK
     		/*if (Build.VERSION.SDK_INT >19) {
     			Window window = this.getWindow();
     			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
     			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//     			window.setStatusBarColor(this.getResources().getColor(R.color.header_bg));
     			window.setStatusBarColor(Color.GREEN);
     			
     			
     		}*/
     		
     		
     		
        imageviewSplashLogo=(ImageView) findViewById(R.id.imgsplashLogo);
        
        Animation hyperspaceJump = AnimationUtils.loadAnimation(this, R.anim.fadeout);
       // imageviewSplashLogo.startAnimation(hyperspaceJump);
 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() 
            {
                // This method will be executed once the timer is over
                // Start your app main activity
            	SharedPreferences preferences = SplashActivity.this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE); 
            	boolean finish = preferences.getBoolean("FINISHED", false);  
            	
            	if (finish==true)
            	{
            		Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
				}else
				{
					
					//Intent i = new Intent(SplashActivity.this, InteroductionActivity.class);
					Intent i = new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(i);
				}
                
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }}
