package com.tilak.dataAccess;

import java.util.ArrayList;

import com.tilak.datamodels.NOTETYPE;
import com.tilak.datamodels.NoteListDataModel;

import android.R.integer;
import android.graphics.Bitmap;





public class DataManager 
{
	public static DataManager manager;
	public Bitmap userImageBitMap;
	public boolean typeofListView;
	public NOTETYPE SELECTED_TEXT_OPTION;
	
	 int selectedIndex;
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public NOTETYPE getSELECTED_TEXT_OPTION() 
	{
		return SELECTED_TEXT_OPTION;
	}

	public void setSELECTED_TEXT_OPTION(NOTETYPE sELECTED_TEXT_OPTION) {
		SELECTED_TEXT_OPTION = sELECTED_TEXT_OPTION;
	}

	public ArrayList<NoteListDataModel> arrNoteListData;
	
	
	NoteListDataModel notelistData;
	

	
	public NoteListDataModel getNotelistData() {
		return notelistData;
	}

	public void setNotelistData(NoteListDataModel notelistData) {
		this.notelistData = notelistData;
	}

	public static DataManager sharedDataManager()
	{
		if(manager==null)
		{
			manager=new DataManager();
		}
		
		return manager;
	}
	
	public void printname()
	{
		System.out.println("DataManager.printname()");
	}
	
	public Bitmap getUserImageBitMap() 
	{
		return userImageBitMap;
	}

	public void setUserImageBitMap(Bitmap userImageBitMap) 
	{
		this.userImageBitMap = userImageBitMap;
	}
	
	public boolean isTypeofListView() {
		return typeofListView;
	}

	public void setTypeofListView(boolean typeofListView) {
		this.typeofListView = typeofListView;
	}
	
	public ArrayList<NoteListDataModel> getArrNoteListData() {
		return arrNoteListData;
	}

	public void setArrNoteListData(ArrayList<NoteListDataModel> arrNoteListData) {
		this.arrNoteListData = arrNoteListData;
	}

}
