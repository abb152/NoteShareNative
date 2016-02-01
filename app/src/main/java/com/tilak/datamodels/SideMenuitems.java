package com.tilak.datamodels;

import java.util.Date;

import org.json.*;


public class SideMenuitems {
	
    private String menuid;
    private String menuName;
    private String menuNameDetail;
    public int resourceId;
    public Date CreatedTime,ModifiedTime,ReminderTime,TimeBomb;
    public String colours;
    
    
    
	public Date getCreatedTime() {
		return CreatedTime;
	}

	public String getColours() {
		return colours;
	}

	public void setColours(String colours) {
		this.colours = colours;
	}

	public void setCreatedTime(Date createdTime) {
		CreatedTime = createdTime;
	}

	public Date getModifiedTime() {
		return ModifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		ModifiedTime = modifiedTime;
	}

	public Date getReminderTime() {
		return ReminderTime;
	}

	public void setReminderTime(Date reminderTime) {
		ReminderTime = reminderTime;
	}

	public Date getTimeBomb() {
		return TimeBomb;
	}

	public void setTimeBomb(Date timeBomb) {
		TimeBomb = timeBomb;
	}

	public int getResourceId() {
		return resourceId;
	}

	public String getMenuNameDetail() {
		return menuNameDetail;
	}

	public void setMenuNameDetail(String menuNameDetail) {
		this.menuNameDetail = menuNameDetail;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public SideMenuitems () {
		
	}	
        
    public SideMenuitems (JSONObject json) {
    
        this.menuid = json.optString("menuid");
        this.menuName = json.optString("menuName");
        //this.resourceId=json.optInt("resourceid");

    }
    
    public String getMenuid() {
        return this.menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }


    
}
