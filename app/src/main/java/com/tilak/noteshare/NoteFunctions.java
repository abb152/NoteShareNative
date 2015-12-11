package com.tilak.noteshare;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tilak.adpters.OurMoveMenuAdapter;
import com.tilak.db.Config;
import com.tilak.db.Folder;
import com.tilak.db.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NoteFunctions {

    //MainActivity mainActivity = new MainActivity();

    // LOCK / PASS CODE
    public void setPasscode(Context context, String id) {
        Note n = Note.findById(Note.class, Long.parseLong(id));
        if(n.islocked == 1){
            passcode(context, id, 3);
        } else {
            Config con = Config.findById(Config.class, 1L);
            //Log.e("jay con.passcode", String.valueOf(con.getPasscode()));
            if (con.getPasscode() == 0){
                Toast.makeText(context, "Please set passcode first.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, PasscodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("FileId", id);
                intent.putExtra("Check", "5");
                context.startActivity(intent);
            }
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

        if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.HONEYCOMB) {
            dp.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                    date[0] = dayOfMonth;
                    date[1] = month + 1;
                    date[2] = year;
                }
            });
        }

        buttonAlertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedTime = check(date[2]) + "-" + check(date[1]) + "-" + check(date[0]) + " " + check(time[0]) + ":" + check(time[1]) + ":00";

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = df.parse(selectedTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long epochTime = date.getTime();

                if (type.equals("timebomb")) {
                    Note n = Note.findById(Note.class, Long.valueOf(noteid));
                    n.timebomb = selectedTime;
                    n.save();
                    Toast.makeText(context, "Timebomb Set: " + selectedTime, Toast.LENGTH_LONG).show();
                } else if (type.equals("reminder")) {
                    Note n = Note.findById(Note.class, Long.valueOf(noteid));
                    n.remindertime = epochTime;
                    n.save();
                    setReminder(context, noteid, epochTime);
                    Toast.makeText(context, "Reminder Set: " + selectedTime, Toast.LENGTH_LONG).show();
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

    public void setReminder(Context context, String noteid, long startTime) {

        Note n = Note.findById(Note.class, Long.valueOf(noteid));

        CalendarEvent evt = new CalendarEvent();
        //evt.setDescr("this is desc");
        evt.setTitle(n.getTitle());
        //evt.setLocation("Mumbai");
        evt.setStartTime(startTime);
        evt.setEndTime(startTime + 3600000);
        evt.setIdCalendar("1");

        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, CalendarEvent.toICSContentValues(evt));

        long eventID = Long.parseLong(uri.getLastPathSegment());

        ContentResolver crReminder = context.getContentResolver();
        Uri uriReminder = crReminder.insert(CalendarContract.Reminders.CONTENT_URI, CalendarEvent.setReminder(eventID));
        //System.out.println("Event URI ["+uri+"]");
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
    public void showMoveAlert(final Context context, String noteid) {
        move = new Dialog(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.movefiletofolder, null, false);

        LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.layoutAlertbox);
        TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText("MOVE TO");
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
            map.put("noteId", noteid);
            folderList.add(map);
        }

        OurMoveMenuAdapter moveMenuAdapter = new OurMoveMenuAdapter((Activity) context, folderList);
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

                Note n = Note.findById(Note.class, noteid);
                n.folder = folderid;
                n.save();
                move.dismiss();
                Toast.makeText(context, "Note " + n.getTitle() + " moved to " + foldername + " folder.", Toast.LENGTH_SHORT).show();
            }
        });

        move.requestWindowFeature(Window.FEATURE_NO_TITLE);
        move.setCancelable(true);
        move.setContentView(contentView);
        move.show();
    }

    // DELETE
    public void showDeleteAlert(final Context context, final String id, final boolean insideNote) {

        final Dialog dialog = new Dialog(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.alert_view, null, false);

        TextView textViewTitleAlert = (TextView) contentView.findViewById(R.id.textViewTitleAlert);
        textViewTitleAlert.setText("DELETE NOTE");
        textViewTitleAlert.setTextColor(Color.WHITE);
        TextView textViewTitleAlertMessage = (TextView) contentView.findViewById(R.id.textViewTitleAlertMessage);
        textViewTitleAlertMessage.setText("Are you sure you want to Delete \n this Note?");

        Button buttonAlertCancel = (Button) contentView.findViewById(R.id.buttonAlertCancel);
        Button buttonAlertOk = (Button) contentView.findViewById(R.id.buttonAlertOk);

        buttonAlertCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        buttonAlertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //delete(id);
                if (insideNote) {

                    dialog.dismiss();
                    Note n =  Note.findById(Note.class, Long.parseLong(id));
                    if(n.getIslocked() == 1 ){
                        Intent intent = new Intent(context, PasscodeActivity.class);
                        intent.putExtra("FileId", id);
                        intent.putExtra("Check", "6");
                        context.startActivity(intent);
                    }
                    else
                        delete(id);

                    //context.startActivity(new Intent(context, MainActivity.class));
                } else {

                    //MainActivity mainActivity = new MainActivity();
                    //mainActivity.delete(id);

                    dialog.dismiss();
                    Note n =  Note.findById(Note.class, Long.parseLong(id));
                    if(n.getIslocked() == 1 ){
                        Intent intent = new Intent(context, PasscodeActivity.class);
                        intent.putExtra("FileId", id);
                        intent.putExtra("Check", "6");
                        context.startActivity(intent);
                    }
                    else {
                        delete(id);
                        context.startActivity(new Intent(context, MainActivity.class));
                        //MainActivity mainActivity = new MainActivity();
                        //mainActivity.delete(id);
                    }
                }
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(contentView);
        dialog.show();
    }

    public void delete(String id){
        Note n = Note.findById(Note.class, Long.parseLong(id));
        n.setCreationtime("0");
        n.save();
    }

    public void showOptionAlert(final Context context, final String id) {
        final Dialog dialog = new Dialog(context);

        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View contentView = inflater.inflate(R.layout.alert_option_view, null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_option_view);

        TextView tvOptionTitleAlert = (TextView) dialog.findViewById(R.id.tvOptionTitleAlert);
        tvOptionTitleAlert.setText("OPTIONS");
        tvOptionTitleAlert.setTextColor(Color.WHITE);

        LinearLayout optionLock = (LinearLayout) dialog.findViewById(R.id.optionLock);
        TextView tvOptionLock = (TextView) optionLock.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionLock = (ImageView) optionLock.findViewById(R.id.imageViewSlidemenu);
        ivOptionLock.setImageResource(R.drawable.ic_option_lock);
        tvOptionLock.setText("Lock");

        optionLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note n = Note.findById(Note.class, Long.parseLong(id));
                if (n.getIslocked() == 0){
                    n.islocked = 1;
                    n.save();
                }
                else
                    setPasscode(context, id);
                dialog.dismiss();
            }
        });

        LinearLayout optionTimebomb = (LinearLayout) dialog.findViewById(R.id.optionTimebomb);
        TextView tvOptionTimebomb = (TextView) optionTimebomb.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionTimebomb = (ImageView) optionTimebomb.findViewById(R.id.imageViewSlidemenu);
        ivOptionTimebomb.setImageResource(R.drawable.ic_option_timebomb);
        tvOptionTimebomb.setText("Timebomb");

        optionTimebomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(context, id, "SET TIMEBOMB", "timebomb");
                dialog.dismiss();
            }
        });

        LinearLayout optionReminder = (LinearLayout) dialog.findViewById(R.id.optionReminder);
        TextView tvOptionReminder = (TextView) optionReminder.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionReminder = (ImageView) optionReminder.findViewById(R.id.imageViewSlidemenu);
        ivOptionReminder.setImageResource(R.drawable.ic_option_reminder);
        tvOptionReminder.setText("Reminder");

        optionReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(context, id, "SET REMAINDER", "reminder");
                dialog.dismiss();
            }
        });

        LinearLayout optionMove = (LinearLayout) dialog.findViewById(R.id.optionMove);
        TextView tvOptionMove = (TextView) optionMove.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionMove = (ImageView) optionMove.findViewById(R.id.imageViewSlidemenu);
        ivOptionMove.setImageResource(R.drawable.ic_option_move);
        tvOptionMove.setText("Move");

        optionMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoveAlert(context, id);
                dialog.dismiss();
            }
        });

        LinearLayout optionDelete = (LinearLayout) dialog.findViewById(R.id.optionDelete);
        TextView tvOptionDelete = (TextView) optionDelete.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionDelete = (ImageView) optionDelete.findViewById(R.id.imageViewSlidemenu);
        ivOptionDelete.setImageResource(R.drawable.ic_option_delete);
        tvOptionDelete.setText("Delete");

        optionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlert(context, id, false);
                dialog.dismiss();
            }
        });

        LinearLayout optionShare = (LinearLayout) dialog.findViewById(R.id.optionShare);
        TextView tvOptionShare = (TextView) optionShare.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionShare = (ImageView) optionShare.findViewById(R.id.imageViewSlidemenu);
        ivOptionShare.setImageResource(R.drawable.ic_option_share);
        ivOptionShare.setPadding(2, 2, 2, 2);
        tvOptionShare.setText("Share");
        View layoutsepreter = optionShare.findViewById(R.id.layoutsepreter);
        layoutsepreter.setVisibility(View.GONE);

        optionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Share
                dialog.dismiss();
            }
        });

        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        //dialog.setContentView(contentView);
        dialog.show();
    }

    // SHARE
    public void share(Context context) {
        final Dialog shareDialog = new Dialog(context);
        shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareDialog.setCancelable(false);
        shareDialog.setContentView(R.layout.alert_share_view);
        shareDialog.setCanceledOnTouchOutside(true);

        TextView tvShareTitleAlert = (TextView) shareDialog.findViewById(R.id.tvShareTitleAlert);
        tvShareTitleAlert.setText("SHARE NOTE VIA");
        tvShareTitleAlert.setTextColor(Color.WHITE);

        LinearLayout shareWhatsapp = (LinearLayout) shareDialog.findViewById(R.id.shareWhatsapp);
        TextView tvWhatsapp = (TextView) shareWhatsapp.findViewById(R.id.textViewSlideMenuName);
        ImageView ivWhatsapp = (ImageView) shareWhatsapp.findViewById(R.id.imageViewSlidemenu);
        ivWhatsapp.setImageResource(R.drawable.ic_option_delete);
        tvWhatsapp.setText("Whatsapp");

        LinearLayout shareEmail = (LinearLayout) shareDialog.findViewById(R.id.shareEmail);
        TextView tvEmail = (TextView) shareEmail.findViewById(R.id.textViewSlideMenuName);
        ImageView ivEmail = (ImageView) shareEmail.findViewById(R.id.imageViewSlidemenu);
        ivEmail.setImageResource(R.drawable.ic_option_delete);
        tvEmail.setText("Email");

        LinearLayout shareMessage = (LinearLayout) shareDialog.findViewById(R.id.shareMessage);
        TextView tvMessage = (TextView) shareMessage.findViewById(R.id.textViewSlideMenuName);
        ImageView ivMessage = (ImageView) shareMessage.findViewById(R.id.imageViewSlidemenu);
        ivMessage.setImageResource(R.drawable.ic_option_delete);
        tvMessage.setText("Message");

        LinearLayout shareFacebook = (LinearLayout) shareDialog.findViewById(R.id.shareFacebook);
        TextView tvFacebook = (TextView) shareFacebook.findViewById(R.id.textViewSlideMenuName);
        ImageView ivFacebook = (ImageView) shareFacebook.findViewById(R.id.imageViewSlidemenu);
        ivFacebook.setImageResource(R.drawable.ic_option_delete);
        tvFacebook.setText("Facebook");

        LinearLayout shareTwitter = (LinearLayout) shareDialog.findViewById(R.id.shareTwitter);
        TextView tvTwitter = (TextView) shareTwitter.findViewById(R.id.textViewSlideMenuName);
        ImageView ivTwitter = (ImageView) shareTwitter.findViewById(R.id.imageViewSlidemenu);
        ivTwitter.setImageResource(R.drawable.ic_option_delete);
        tvTwitter.setText("Twitter");

        Button buttonShareCancel = (Button) shareDialog.findViewById(R.id.buttonShareCancel);
        Button buttonShareOk = (Button) shareDialog.findViewById(R.id.buttonShareOk);

        buttonShareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });

        buttonShareOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });

        shareDialog.show();
    }


}
