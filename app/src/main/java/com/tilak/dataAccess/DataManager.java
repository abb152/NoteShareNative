package com.tilak.dataAccess;

import android.graphics.Bitmap;





public class DataManager 
{
	public static DataManager manager;
	public Bitmap userImageBitMap;
	public boolean typeofListView;

	 int selectedIndex;
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public static DataManager sharedDataManager()
	{
		if(manager==null)
		{
			manager=new DataManager();
		}
		
		return manager;
	}

	public Bitmap getUserImageBitMap() 
	{
		return userImageBitMap;
	}

	public void setUserImageBitMap(Bitmap userImageBitMap) 
	{
		this.userImageBitMap = userImageBitMap;
	}

	public void setTypeofListView(boolean typeofListView) {
		this.typeofListView = typeofListView;
	}

}
