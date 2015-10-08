package com.tilak.datamodels;

import android.graphics.Bitmap;
import android.text.SpannableString;




public class NoteListDataModel 
{
	
	public NOTETYPE  noteType;
	public Bitmap bitmap;
	public String bitmapPath;
	public String strAudioFilePath;
	public SpannableString stringtext;
	public SpannableString getStringtext() {
		return stringtext;
	}
	public void setStringtext(SpannableString stringtext) {
		this.stringtext = stringtext;
	}
	public String getStrAudioFilePath() {
		return strAudioFilePath;
	}
	public void setStrAudioFilePath(String strAudioFilePath) {
		this.strAudioFilePath = strAudioFilePath;
	}
	public NOTETYPE getNoteType() {
		return noteType;
	}
	public void setNoteType(NOTETYPE noteType) {
		this.noteType = noteType;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getBitmapPath() {
		return bitmapPath;
	}
	public void setBitmapPath(String bitmapPath) {
		this.bitmapPath = bitmapPath;
	}
	public SpannableString getSpannableString() {
		return spannableString;
	}
	public void setSpannableString(SpannableString spannableString) {
		this.spannableString = spannableString;
	}
	public String getAudioPath() {
		return audioPath;
	}
	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}
	public SpannableString spannableString;
	public String audioPath;
	
	
}
