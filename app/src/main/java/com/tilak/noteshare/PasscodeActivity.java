package com.tilak.noteshare;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tilak.db.Config;
import com.tilak.db.Note;

import java.util.List;

public class PasscodeActivity extends DrawerActivity {
    EditText et1,et2,et3,et4;
    AlertDialog confirmPassword=null;
    String passcode="";
    String confirm_passcode="";
    boolean confirm =false;
    TextView t=null;
    int i=0;
    String check, fileId;
    int dbPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        Intent intent = this.getIntent();
        check = intent.getStringExtra("Check");
        fileId = intent.getStringExtra("FileId");

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
        if(check.equals("1")){
            t.setText("Confirm Password");
            Config con = Config.findById(Config.class, 1L);
            dbPass = con.passcode;
        }
    }
    public void keyPressed(View v){
        TextView tv = (TextView) v;
        i++;
        if(check.equals("1")){
            passcode += tv.getText().toString();
            if(passcode.length() == 4){
                et4.setText("*");
                if(Integer.parseInt(passcode) == dbPass){
                    Note n = Note.findById(Note.class,Long.parseLong(fileId));
                    n.islocked = 1;
                    n.save();
                    finish();
                }else{
                    passcode="";
                    i=0;
                    Log.d("//////////////",passcode);
                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                    et4.setText("");
                    t.setText("Enter Valid Password to Open file.");
                    Toast.makeText(PasscodeActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                }
            }else{
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
        else {
            if (i <= 4) {
                passcode += tv.getText().toString();
                if (passcode.length() == 4) {
                    Log.d("//////////////", passcode);
                    et4.setText("*");
                    t.setText("Confirm Password");
                    if (confirm == false) {
                        confirm_passcode = passcode;
                        passcode = "";
                        i = 0;
                        Log.d("//////////////", passcode);
                        et1.setText("");
                        et2.setText("");
                        et3.setText("");
                        et4.setText("");
                        confirm = true;
                    } else {
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
                            confirm = true;
                            i = 0;
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
    }
    public void clearPressed(View v){
        finish();
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
