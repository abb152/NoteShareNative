package com.tilak.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tilak.db.Note;
import com.tilak.noteshare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Jay on 14-10-2015.
 */
public class CustomDateFormatter {

    public String dbToAdapterDate(String date){

        SimpleDateFormat originalDateFormat  = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss");
        SimpleDateFormat requiredDateFormat = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss");
        //SimpleDateFormat requiredDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

        try {
            date = requiredDateFormat.format(originalDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public void jay(String a){
        Log.e("jay test", a);
    }


    Dialog move;
    public void showDate(final Context context, final String noteid ){
        move = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.datetime, null, false);

        LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.layoutAlertbox);
        TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText("Date Time");
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

                Note n = Note.findById(Note.class, Long.valueOf(noteid));
                n.timebomb = timebombTime;
                n.save();

                move.dismiss();
                Toast.makeText(context, "Timebomb Set: " + timebombTime, Toast.LENGTH_LONG).show();
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
}
