package com.tilak.noteshare;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tilak.db.Config;

import java.util.List;

public class PasscodeActivity extends DrawerActivity {
    EditText et1,et2,et3,et4;
    AlertDialog confirmPassword=null;
    String passcode="";
    String confirm_passcode="";
    boolean confirm =false;
    TextView t=null;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);
        et4 = (EditText) findViewById(R.id.et4);
        t=(TextView) findViewById(R.id.textView);
        List<Config> config = Config.listAll(Config.class);
        getActionBar().hide();
        if(config.size() == 0) {
            Config c = new Config("Noteshare", "", "", "", "", "", 0, "", "username", "1", "1");
            c.save();
        }
    }
    public void keyPressed(View v){
        TextView tv = (TextView) v;
        i++;
        if(i<=4) {
            passcode += tv.getText().toString();
            if (passcode.length() == 4) {
                Log.d("//////////////", passcode);
                et4.setText("*");
                t.setText("Confirm Password");
                if(confirm==false){
                    confirm_passcode=passcode;
                    passcode="";
                    i=0;
                    Log.d("//////////////",passcode);
                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                    et4.setText("");
                    confirm=true;
                }
                else{
                    if (passcode.equals(confirm_passcode)) {
                        Config c = Config.findById(Config.class, Long.valueOf(1));
                        c.setPasscode(Integer.parseInt(confirm_passcode));
                        c.save();
                        Toast.makeText(PasscodeActivity.this, "Passcode Saved", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(PasscodeActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                        passcode = "";
                        et1.setText("");
                        et2.setText("");
                        et3.setText("");
                        et4.setText("");
                        confirm=true;
                        i=0;
                    }
                }
                //Check for passcode
            } else {
                if (i == 1) {
                    Log.d("//////////////", passcode);
                    et1.setText("*");
                } else if (i == 2) {
                    Log.d("//////////////", passcode);
                    et2.setText("*");
                } else if (i == 3) {
                    Log.d("//////////////", passcode);
                    et3.setText("*");
                }
            }
        }
    }
    public void clearPressed(View v){

    }
    public void backPressed(View v){
        passcode="";
        i=0;
        Log.d("//////////////",passcode);
        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");
    }
}
