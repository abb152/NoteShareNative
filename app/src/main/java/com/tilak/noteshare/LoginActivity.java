package com.tilak.noteshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

    }

    public void main(View v){
        Intent i = new Intent(getApplication(), MainActivity.class);
        startActivity(i);

    }

}