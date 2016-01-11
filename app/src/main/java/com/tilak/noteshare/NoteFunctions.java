package com.tilak.noteshare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tilak.adpters.OurMoveMenuAdapter;
import com.tilak.db.Config;
import com.tilak.db.Folder;
import com.tilak.db.Note;
import com.tilak.db.NoteElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class NoteFunctions {

    //MainActivity mainActivity = new MainActivity();
    //public static String SERVER_URL = "http://104.197.122.116/";
    //public static String SERVER_URL = "http://192.168.0.125:1337/";
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

        List<Folder> folder = Folder.findWithQuery(Folder.class, "Select * from Folder where CREATIONTIME != '0' ORDER BY ID DESC");
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

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateStr = formatter.format(new Date());
                try {
                    Note n = Note.findById(Note.class, noteid);
                    n.folder = folderid;
                    n.setModifytime(currentDateStr);
                    n.setMtime(RegularFunctions.stringToDate(currentDateStr));
                    n.save();
                    move.dismiss();
                    Toast.makeText(context, "Note " + n.getTitle() + " moved to " + foldername + " folder.", Toast.LENGTH_SHORT).show();
                } catch (ParseException pe) {

                }
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
                    Note n = Note.findById(Note.class, Long.parseLong(id));
                    if (n.getIslocked() == 1) {
                        Intent intent = new Intent(context, PasscodeActivity.class);
                        intent.putExtra("FileId", id);
                        intent.putExtra("Check", "6");
                        context.startActivity(intent);
                    } else{
                        delete(id);
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                } else {

                    dialog.dismiss();
                    Note n = Note.findById(Note.class, Long.parseLong(id));
                    if (n.getIslocked() == 1) {
                        Intent intent = new Intent(context, PasscodeActivity.class);
                        intent.putExtra("FileId", id);
                        intent.putExtra("Check", "6");
                        context.startActivity(intent);
                    } else {
                        delete(id);
                        context.startActivity(new Intent(context, MainActivity.class));
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
        n.setCtime(0l);
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
        ivOptionLock.setImageResource(R.drawable.ic_note_lock_dark);
        tvOptionLock.setText("Lock");

        optionLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPasscode(context, id);
                dialog.dismiss();
            }
        });

        LinearLayout optionTimebomb = (LinearLayout) dialog.findViewById(R.id.optionTimebomb);
        TextView tvOptionTimebomb = (TextView) optionTimebomb.findViewById(R.id.textViewSlideMenuName);
        ImageView ivOptionTimebomb = (ImageView) optionTimebomb.findViewById(R.id.imageViewSlidemenu);
        ivOptionTimebomb.setImageResource(R.drawable.ic_note_timebomb_dark);
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
        ivOptionReminder.setImageResource(R.drawable.ic_note_remainder_dark);
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
        ivOptionMove.setImageResource(R.drawable.ic_note_move_dark);
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
        ivOptionDelete.setImageResource(R.drawable.ic_note_delete_dark);
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
        ivOptionShare.setImageResource(R.drawable.ic_note_share_dark);
        ivOptionShare.setPadding(2, 2, 2, 2);
        tvOptionShare.setText("Share");
        View layoutsepreter = optionShare.findViewById(R.id.layoutsepreter);
        layoutsepreter.setVisibility(View.GONE);

        optionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Share
                //noteshareShare(context, id);
                share(context,id, true);
                dialog.dismiss();
            }
        });

        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        //dialog.setContentView(contentView);
        dialog.show();
    }

    // SHARE
    public void share(final Context context, final String id , final boolean outsideNote) {
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
        tvWhatsapp.setText("Link");
        shareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkShare(context, id);
                shareDialog.dismiss();
            }
        });

        LinearLayout shareEmail = (LinearLayout) shareDialog.findViewById(R.id.shareEmail);
        TextView tvEmail = (TextView) shareEmail.findViewById(R.id.textViewSlideMenuName);
        tvEmail.setText("NoteShare");
        shareEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteshareShare(context, id);
                shareDialog.dismiss();
            }
        });

        LinearLayout shareMessage = (LinearLayout) shareDialog.findViewById(R.id.shareMessage);
        TextView tvMessage = (TextView) shareMessage.findViewById(R.id.textViewSlideMenuName);
        tvMessage.setText("Screenshot");
        shareMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textShare(context, id);
                screenshotFromMain(context,id);
                shareDialog.dismiss();
            }
        });

        LinearLayout shareFacebook = (LinearLayout) shareDialog.findViewById(R.id.shareFacebook);
        TextView tvFacebook = (TextView) shareFacebook.findViewById(R.id.textViewSlideMenuName);
        tvFacebook.setText("Text");
        shareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textShare(context, id);
                /*if(outsideNote) {
                    MainActivity mainActivity = new MainActivity();
                    context.screenshot(v.getTag().toString());
                }
                else{
                    NoteMainActivity noteMainActivity = new NoteMainActivity();
                    noteMainActivity.screenshot();
                }*/

                shareDialog.dismiss();
            }
        });

        /*LinearLayout shareTwitter = (LinearLayout) shareDialog.findViewById(R.id.shareTwitter);
        TextView tvTwitter = (TextView) shareTwitter.findViewById(R.id.textViewSlideMenuName);
        tvTwitter.setText("Twitter");*/

        shareDialog.show();
    }

    //text sharing
    public void textShare(final Context context, final String id){

        String noteDesc = "";

        List<NoteElement> noteElements = NoteElement.find(NoteElement.class, "(type = ? OR type = ?) AND noteid = ?", "text", "checkbox", id);

        if(noteElements.size() != 0 && noteElements.get(0).getContentA() != null){

            for(int i=0; i <noteElements.size(); i++){

                Log.e("jay og", noteElements.get(i).getContent());
                String j = noteElements.get(i).getContent();

                j = j.replace("</li>","</li><br />");
                j = j.replace("<ol>","<br /><ol>");
                j = j.replace("<ul>","<br /><ul>");

                String abc = Html.fromHtml(j).toString();

                noteDesc = noteDesc + abc;

                if(i != noteElements.size() -1 ){
                    noteDesc = noteDesc + "\n";
                }
            }

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, noteDesc);
            context.startActivity(Intent.createChooser(shareIntent, "Send Text to"));


            /*Source source=new Source(noteDesc);
            String renderedText=source.getRenderer().toString();
            System.out.println("\nSimple rendering of the HTML document: jay\n");
            System.out.println("jay" +renderedText);
            Log.e("jay", renderedText);*/
        }else{
            noteDesc = "";
        }

    }

    public void screenshotFromMain(Context context,String noteid){
        Note note = Note.findById(Note.class, Long.parseLong(noteid));
        if (note.islocked == 0) {
            Intent i = new Intent(context, NoteMainActivity.class);
            i.putExtra("NoteId", noteid);
            i.putExtra("Outside", true);
            context.startActivity(i);
            /*SearchLayout.setVisibility(View.GONE);
            textViewheaderTitle.setText("");
            searchLayoutOpen = false;*/
            //editTextsearchNote.setText("");
        } else {
            Intent intent = new Intent(context, PasscodeActivity.class);
            intent.putExtra("FileId", noteid);
            intent.putExtra("Check", "4");
            context.startActivity(intent);
            //editTextsearchNote.setText("");
        }
    }

    public void screenshot(View v, final Context context, final String background, final String noteId) {

        ScrollView scroll = (ScrollView) v;

        int width = scroll.getChildAt(0).getWidth();
        int height = scroll.getChildAt(0).getHeight();

        int blankSpace = RegularFunctions.pxFromDp(context, 1500);

        Log.e("jay sw", String.valueOf(width));
        Log.e("jay sh", String.valueOf(height - blankSpace));

        int screenShotHeight = height - blankSpace;

        double j = ((double) screenShotHeight) / 200;
        int timesLoopShouldRun = (int) Math.ceil(j);


        Canvas bitmapCanvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(width, screenShotHeight, Bitmap.Config.ARGB_8888);

        bitmapCanvas.setBitmap(bitmap);
        bitmapCanvas.drawColor(Color.parseColor(background));
        //bitmapCanvas.scale(1.0f, 3.0f);
        scroll.draw(bitmapCanvas);

        Random randomGenerator = new Random();
        String randomNumber = String.valueOf(randomGenerator.nextInt(10000));

        String fileName = noteId + ".png";
        File file = new File(Environment.getExternalStorageDirectory(), "/NoteShare/NoteShare Images/" + fileName);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Toast.makeText(context, "Screenshot taken", Toast.LENGTH_SHORT).show();
        Log.e("jay ss", "generated");

    }

    //link sharing
    public void linkShare(final Context context, final String id){

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Generating link...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

        //final String email = emailTo.getText().toString();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                if (Looper.myLooper() == null)
                {
                    Looper.prepare();
                }

                if(RegularFunctions.getServerNoteId(id).equals("0")){
                    progressDialog.setMessage("Please wait while we Sync the Note...");
                    RegularFunctions.syncNow();
                }

                /*String shareMessage = RegularFunctions.getUserName() + " has shared \'"+ RegularFunctions.getNoteName(id) + "\' note with you.\n\n"
                        +"http://www.noteshare.com/"+RegularFunctions.getUserId() +"/"+RegularFunctions.getServerNoteId(id)+".html"
                        +"\n\n-via NoteShare";*/

                String shareMessage = RegularFunctions.getUserName() + " has shared \'"+ RegularFunctions.getNoteName(id) + "\' note with you.\n\n"
                        +"http://104.197.47.172/note/get#/app/note/"+RegularFunctions.getServerNoteId(id)
                        +"\n\n-via NoteShare";

                Log.e("jay", shareMessage);
                Toast.makeText(context, shareMessage, Toast.LENGTH_SHORT).show();
                //progressDialog.dismiss();

                //Uri uri = Uri.parse("android.resource://com.tilak.noteshare/drawable/ic_launcher");
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                //share.putExtra(Intent.EXTRA_STREAM, uri);
                share.putExtra(Intent.EXTRA_TEXT, shareMessage);
                context.startActivity(Intent.createChooser(share, "Share note"));
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }.execute(null,null,null);

    }

    //noteshare to noteshare share // email
    public void noteshareShare(final Context context, final String id) {
        final Dialog shareDialog = new Dialog(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.share_noteshare_email, null, false);
        TextView tvShareTitleAlert = (TextView) contentView.findViewById(R.id.tvEmailShareTitle);
        tvShareTitleAlert.setText("SHARE NOTE");
        tvShareTitleAlert.setTextColor(Color.WHITE);

        final EditText emailTo = (EditText) contentView.findViewById(R.id.textViewTitleAlertMessage);

        Button buttonShareCancel = (Button) contentView.findViewById(R.id.buttonAlertCancel);
        Button buttonShareOk = (Button) contentView.findViewById(R.id.buttonAlertOk);

        buttonShareCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });

        buttonShareOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailTo.getText().toString().toLowerCase().trim();
                if (email.equals("")) {
                    emailTo.setError("Enter Email id.");
                } else if (!RegularFunctions.isValidEmail(email)) {
                    emailTo.setError("Invalid Email");
                } else {
                    Log.e("jay text", emailTo.getText().toString().toLowerCase());
                    Log.e("jay id", id);
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    //final String email = emailTo.getText().toString();

                    new AsyncTask<Void, Void, String>() {

                        boolean shared = false;

                        @Override
                        protected String doInBackground(Void... params) {
                            RegularFunctions.syncNow();
                            try
                            {
                                String shareEmailJson = shareJson(id, email).toString();
                                Log.e("jay sharejson", shareEmailJson);

                                String response = RegularFunctions.post(RegularFunctions.SERVER_URL + "share/save", shareEmailJson);
                                Log.e("jay response", response);

                                JSONObject jsonObject = new JSONObject(response);

                                String value = jsonObject.optString("value");
                                if (value.equals("true")) {
                                    shareDialog.dismiss();
                                    progressDialog.dismiss();
                                    shared = true;
                                } else {
                                    shareDialog.dismiss();
                                    progressDialog.dismiss();
                                    shared = false;
                                }
                            }
                            catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                            catch(IOException io)
                            {
                                io.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if(shared)
                                Toast.makeText(context, "Note shared successfully!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(context, "Oops, Something went wrong!", Toast.LENGTH_LONG).show();

                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }.execute(null,null,null);
                }
            }
        });

        shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareDialog.setCancelable(false);
        shareDialog.setContentView(contentView);
        shareDialog.setCanceledOnTouchOutside(true);

        shareDialog.show();
    }

    public JSONObject shareJson(String id, String email){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("userfrom",RegularFunctions.getUserId());
            jsonObject.put("email",email);
            jsonObject.put("note", RegularFunctions.getServerNoteId(id).trim());
        }catch(JSONException je){
        }
        return jsonObject;
    }
}
