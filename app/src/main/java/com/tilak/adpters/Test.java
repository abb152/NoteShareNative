package com.tilak.adpters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tilak.noteshare.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jay on 11-10-2015.
 */
public class Test extends BaseAdapter {

    public ArrayList<HashMap<String,String>> list;
    Activity activity;

    public Test(Activity activity,ArrayList<HashMap<String,String>> list){
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
        TextView txtDesc,txtTime;
        ImageButton button;
        RelativeLayout relativeLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){
            convertView=inflater.inflate(R.layout.test,null); //change the name of the layout
            holder=new ViewHolder();

            holder.relativeLayout= (RelativeLayout) convertView.findViewById(R.id.front);
            holder.txtDesc= (TextView) convertView.findViewById(R.id.tv1); //find the different Views
            holder.txtTime= (TextView) convertView.findViewById(R.id.tv2);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        HashMap<String,String> map =list.get(position);
        holder.txtDesc.setText(map.get("tvTitle")); //set the hash maps
        holder.txtTime.setText(map.get("tvCreate"));

        return convertView;
    }
}
