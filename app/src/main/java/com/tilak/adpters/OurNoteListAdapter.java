package com.tilak.adpters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tilak.noteshare.R;

import java.util.ArrayList;
import java.util.HashMap;

public class OurNoteListAdapter extends BaseAdapter {
    public ArrayList<HashMap<String,String>> list;
    String listview = "";
    Activity activity;
    TextView noteDesc;

    public OurNoteListAdapter(Activity activity,ArrayList<HashMap<String,String>> list, String listwiew){
        super();
        this.activity=activity;
        this.list=list;
        this.listview = listwiew;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        //all the fields in layout specified
        TextView txtNoteName,txtNoteDesc,txtNoteDate, tvIdHidden;
        ImageButton btnLock, btnTimebomb, btnMove, btnDelete ,btnShare;
        LinearLayout linearLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.our_note_list,null); //change the name of the layout
            if(listview == "grid"){
                convertView = inflater.inflate(R.layout.our_note_list,null); //change the name of the layout
            }

            if(listview != "grid") {
                holder.tvIdHidden = (TextView) convertView.findViewById(R.id.tvIdHidden);
            }
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.front);
            holder.txtNoteName = (TextView) convertView.findViewById(R.id.tvNoteName); //find the different Views
            holder.txtNoteDesc = (TextView) convertView.findViewById(R.id.tvNoteDesc);
            holder.txtNoteDate = (TextView) convertView.findViewById(R.id.tvNoteDate);


            if(listview == "list") {
                holder.txtNoteName.setTextSize(18F);
                holder.txtNoteDesc.setVisibility(View.GONE);
                holder.txtNoteDate.setVisibility(View.GONE);
                holder.linearLayout.setPadding(20,30,20,20);
            }

            if(listview != "grid") {
                holder.btnLock = (ImageButton) convertView.findViewById(R.id.btnlock);
                holder.btnTimebomb = (ImageButton) convertView.findViewById(R.id.btntimebomb);
                holder.btnMove = (ImageButton) convertView.findViewById(R.id.btnmove);
                holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btndelete);
                holder.btnShare = (ImageButton) convertView.findViewById(R.id.btnshare);
            }
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        HashMap<String,String> map = list.get(position);
        holder.txtNoteName.setText(map.get("noteName")); //set the hash maps
        holder.txtNoteDesc.setText(map.get("noteDesc"));
        holder.txtNoteDate.setText(map.get("noteDate"));
        holder.linearLayout.setBackgroundColor(Color.parseColor(map.get("noteBgColor")));
        if(listview != "grid") {
            holder.tvIdHidden.setText(map.get("noteId"));
        }

        if(map.get("noteLock").equalsIgnoreCase("1") && listview!="grid"){
            holder.btnLock.setImageResource(R.drawable.image_option_lock2);
        }

        if(listview != "grid") {
            holder.btnLock.setTag(map.get("noteId"));
            holder.btnTimebomb.setTag(map.get("noteId"));
            holder.btnMove.setTag(map.get("noteId"));
            holder.btnDelete.setTag(map.get("noteId"));
            holder.btnShare.setTag(map.get("noteId"));
        }

        return convertView;
    }
}
