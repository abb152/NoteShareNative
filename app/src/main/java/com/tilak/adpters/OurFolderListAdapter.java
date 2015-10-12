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

//import com.tilak.foldershare.R;

public class OurFolderListAdapter extends BaseAdapter {
    public ArrayList<HashMap<String,String>> list;
    Activity activity;

    public OurFolderListAdapter(Activity activity,ArrayList<HashMap<String,String>> list){
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
        TextView txtFolderName,txtFolderDesc,txtFolderDate,tvIdHidden;
        ImageButton button;
        LinearLayout linearLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){
            convertView=inflater.inflate(R.layout.our_folder_list,null); //change the name of the layout
            holder=new ViewHolder();

            holder.linearLayout= (LinearLayout) convertView.findViewById(R.id.folderfront);
            holder.txtFolderName= (TextView) convertView.findViewById(R.id.tvFolderName); //find the different Views
            holder.txtFolderDesc = (TextView) convertView.findViewById(R.id.tvFolderDesc);
            holder.txtFolderDate= (TextView) convertView.findViewById(R.id.tvFolderDate);
            holder.tvIdHidden = (TextView) convertView.findViewById(R.id.tvIdFolderHidden);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        HashMap<String,String> map =list.get(position);
        holder.txtFolderName.setText(map.get("folderName")); //set the hash maps
        holder.txtFolderDesc.setText(map.get("folderDesc"));
        holder.txtFolderDate.setText(map.get("folderDate"));
        holder.tvIdHidden.setText(map.get("folderId"));
        holder.linearLayout.setBackgroundColor(Color.parseColor(map.get("folderBgColor")));

        return convertView;
    }
}
