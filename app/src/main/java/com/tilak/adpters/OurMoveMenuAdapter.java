package com.tilak.adpters;

/**
 * Created by Jay on 13-10-2015.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tilak.noteshare.R;
import com.tilak.noteshare.RegularFunctions;

import java.util.ArrayList;
import java.util.HashMap;



public class OurMoveMenuAdapter extends BaseAdapter {
    public ArrayList<HashMap<String,String>> list;
    Activity activity;

    public OurMoveMenuAdapter(Activity activity,ArrayList<HashMap<String,String>> list){
        super();
        this.activity=activity;
        this.list=list;
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
        TextView tvFolderName, tvHiddenFolderId, tvHiddenNoteId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){
            convertView=inflater.inflate(R.layout.move_folder_list,null); //change the name of the layout
            holder=new ViewHolder();

            holder.tvFolderName= (TextView) convertView.findViewById(R.id.tvFolderName); //find the different Views
            holder.tvHiddenFolderId= (TextView) convertView.findViewById(R.id.tvHiddenFolderId); //find the different Views
            holder.tvHiddenNoteId= (TextView) convertView.findViewById(R.id.tvHiddenNoteId); //find the different Views

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        HashMap<String,String> map = list.get(position);
        holder.tvFolderName.setText(map.get("folderName")); //set the hash maps
        holder.tvFolderName.setTypeface(RegularFunctions.getAgendaMediumFont(activity));
        holder.tvHiddenFolderId.setText(map.get("folderId"));
        holder.tvHiddenNoteId.setText(map.get("noteId"));

        return convertView;
    }
}



