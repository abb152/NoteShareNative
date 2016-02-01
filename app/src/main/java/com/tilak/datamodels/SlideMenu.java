package com.tilak.datamodels;

import org.json.*;
import java.util.ArrayList;

public class SlideMenu {
	
    private ArrayList<SideMenuitems> sideMenuitems;
    
    
	public SlideMenu () {
		
	}	
        
    public SlideMenu (JSONObject json) {
    

        this.sideMenuitems = new ArrayList<SideMenuitems>();
        JSONArray arraySideMenuitems = json.optJSONArray("sideMenuitems");
        if (null != arraySideMenuitems) {
            int sideMenuitemsLength = arraySideMenuitems.length();
            for (int i = 0; i < sideMenuitemsLength; i++) {
                JSONObject item = arraySideMenuitems.optJSONObject(i);
                if (null != item) {
                    this.sideMenuitems.add(new SideMenuitems(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("sideMenuitems");
            if (null != item) {
                this.sideMenuitems.add(new SideMenuitems(item));
            }
        }


    }
    
    public ArrayList<SideMenuitems> getSideMenuitems() {
        return this.sideMenuitems;
    }

    public void setSideMenuitems(ArrayList<SideMenuitems> sideMenuitems) {
        this.sideMenuitems = sideMenuitems;
    }


    
}
