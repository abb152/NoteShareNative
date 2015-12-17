package com.tilak.adpters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tilak.noteshare.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jay on 16-12-2015.
 */
public class OurNotificationListAdapter extends BaseAdapter {
    public ArrayList<HashMap<String,String>> list;
    Activity activity;

    public OurNotificationListAdapter(Activity activity,ArrayList<HashMap<String,String>> list){
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
        TextView tvNotiHeader,tvDesc;
        ImageButton ibAccept;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){
            convertView=inflater.inflate(R.layout.our_notification_list,null); //change the name of the layout
            holder=new ViewHolder();


            holder.tvNotiHeader= (TextView) convertView.findViewById(R.id.tvNotiHeader); //find the different Views
            holder.tvDesc= (TextView) convertView.findViewById(R.id.tvDesc);
            holder.ibAccept= (ImageButton) convertView.findViewById(R.id.ibAccept);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        HashMap<String,String> map = list.get(position);
        String note = map.get("note");

        String notename = map.get("notename");
        String username = map.get("username");
        holder.tvDesc.setText((Html.fromHtml("<b>"+username +"</b> has shared <b>"+ notename +"</b> note with you."))); //set the hash maps
        holder.ibAccept.setTag(note);

        return convertView;
    }
}
