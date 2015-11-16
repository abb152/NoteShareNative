package com.tilak.noteshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tilak.adpters.OurMoveMenuAdapter;
import com.tilak.db.Config;
import com.tilak.db.Folder;
import com.tilak.db.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoteFunctions {

    // LOCK / PASS CODE
    public void setPasscode(Context context, String id) {
        Note n = Note.findById(Note.class, Long.parseLong(id));
        if(n.islocked == 1){
            passcode(context, id, 3);
        } else {
            Config con = Config.findById(Config.class, 1L);
            Log.e("jay con.passcode", String.valueOf(con.getPasscode()));
            if (con.getPasscode() == 0)
                Toast.makeText(context, "Please set passcode in Setting page", Toast.LENGTH_LONG).show();
            else {
                n.islocked = 1;
                n.save();
                //passcode(context, id, 1);
            }
        }
    }

    public void passcode(Context context, String id, int i){
        Intent intent = new Intent(context, PasscodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("FileId", id);
        if (i == 1) {
            intent.putExtra("Check", "1");
        } else if (i == 3) {
            intent.putExtra("Check", "3");
        }
        context.startActivity(intent);
    }

    // TIME BOMB & REMINDER
    Dialog move;
    public void showDate(final Context context, final String noteid, String title, final String type){
        move = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.datetime, null, false);

        LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.layoutAlertbox);
        TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText(title);
        textViewTitleAlert.setTextColor(Color.WHITE);

        DatePicker dp = (DatePicker) contentView.findViewById(R.id.dp);
        TimePicker tp = (TimePicker) contentView.findViewById(R.id.tp);


        //final int[1] hour;/* = tp.getCurrentHour();*/
        final int[] time = new int[2];
        time[0] = tp.getCurrentHour();
        time[1] = tp.getCurrentMinute();

        final int[] date = new int[3];
        date[0] = dp.getDayOfMonth();
        date[1] = dp.getMonth() +1;
        date[2] = dp.getYear();


        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time[0] = hourOfDay;
                time[1] = minute;
            }
        });

        Button buttonAlertOk = (Button) contentView.findViewById(R.id.buttonAlertOk);
        Button buttonAlertCancel = (Button) contentView.findViewById(R.id.buttonAlertCancel);

        dp.setMinDate(System.currentTimeMillis() - (60 * 48 * 1000));

        dp.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //Log.d("tag", "finally found the listener, the date is: year " + year + ", month " + month + ", dayOfMonth " + dayOfMonth);
                date[0] = dayOfMonth;
                date[1] = month + 1;
                date[2] = year;
            }
        });

        buttonAlertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(),"Day: " + date[0] + ", Month: " + date[1] + ", Year: " + date[2] ,Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplication(),"Hour: "+ time[0] + "Minute" + time[1],Toast.LENGTH_LONG).show();

                String timebombTime = check(date[2]) + "-" + check(date[1]) + "-" + check(date[0]) + " " + check(time[0]) + ":" + check(time[1]) + ":00";

                if (type.equals("timebomb")) {
                    Note n = Note.findById(Note.class, Long.valueOf(noteid));
                    n.timebomb = timebombTime;
                    n.save();
                    Toast.makeText(context, "Timebomb Set: " + timebombTime, Toast.LENGTH_LONG).show();
                } else if (type.equals("reminder")) {
                    Note n = Note.findById(Note.class, Long.valueOf(noteid));
                    n.remindertime = timebombTime;
                    n.save();
                    Toast.makeText(context, "Reminder Set: " + timebombTime, Toast.LENGTH_LONG).show();
                }

                move.dismiss();
            }
        });

        buttonAlertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move.dismiss();
            }
        });

        move.requestWindowFeature(Window.FEATURE_NO_TITLE);
        move.setCancelable(true);
        move.setContentView(contentView);
        move.show();
    }

    public String check(int value){
        String newvalue;
        if (value < 10) // minute
            newvalue = "0" + String.valueOf(value);
        else
            newvalue =  String.valueOf(value);
        return newvalue;
    }

    // MOVE
    public void showMenuAlert(final Context context, String noteid, Activity activity) {
        move = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.movefiletofolder, null, false);

        LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.layoutAlertbox);
        TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText("Move to");
        textViewTitleAlert.setTextColor(Color.WHITE);

        ListView lvFolder = (ListView) contentView.findViewById(R.id.lvFolder);
        TextView empty = (TextView) contentView.findViewById(R.id.empty);
        lvFolder.setEmptyView(empty);

        ArrayList<HashMap<String,String>> folderList = new ArrayList<HashMap<String, String>>();

        List<Folder> folder = Folder.findWithQuery(Folder.class, "Select * from Folder ORDER BY ID DESC");
        for(Folder folderloop : folder){
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("folderName", folderloop.getName());
            map.put("folderId", String.valueOf(folderloop.getId()));
            map.put("noteId",noteid);
            folderList.add(map);
        }

        OurMoveMenuAdapter moveMenuAdapter = new OurMoveMenuAdapter(activity, folderList);
        lvFolder.setAdapter(moveMenuAdapter);

        lvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                int itemPosition = position;
                String folderid = null, foldername = null;
                Long noteid;

                HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

                folderid = map.get("folderId");
                foldername = map.get("folderName");
                noteid = Long.parseLong(map.get("noteId"));

                Note n = Note.findById(Note.class,noteid);
                n.folder = folderid;
                n.save();
                move.dismiss();
                Toast.makeText(context, "Note "+ n.getTitle() + " moved to " + foldername +" folder.", Toast.LENGTH_SHORT).show();
            }
        });

        move.requestWindowFeature(Window.FEATURE_NO_TITLE);
        move.setCancelable(true);
        move.setContentView(contentView);
        move.show();
    }

    // DELETE
    public void delete() {}

    // SHARE
    public void share() {}

}
