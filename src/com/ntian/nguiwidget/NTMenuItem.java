package com.ntian.nguiwidget;

import android.content.Context;


public class NTMenuItem {
	public int id;
	public String text;
	public boolean isEnable;
	public boolean isSelected;
	public Object tag;
	
	public NTMenuItem(int _id , String _text) {
		id = _id;
		text = _text;
		isEnable = true;
		isSelected = false;
	}
	
	public NTMenuItem(int _id , String _text,boolean _isEnable) {
		id = _id;
		text = _text;
		isEnable = _isEnable;
		isSelected = false;
	}
	
	public NTMenuItem(Context context,int resourid) {
		id = resourid;
		text = context.getResources().getString(resourid);
		isEnable = true;
		isSelected = false;
	}
	
	public NTMenuItem(Context context,int resourid,boolean _isEnable) {
		id = resourid;
		text = context.getResources().getString(resourid);
		isEnable = _isEnable;
		isSelected = false;
	}
	
	public void setTag(Object _tag) {
		tag = _tag;
	}

	public Object getTag() {
		return tag;
	}
}
