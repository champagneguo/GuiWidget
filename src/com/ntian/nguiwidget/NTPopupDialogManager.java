package com.ntian.nguiwidget;

import android.content.Context;
import android.widget.PopupWindow;

public class NTPopupDialogManager {
    private static NTPopupDialogManager mNTPopupDialogManager;
    private PopupWindow mPopupWindow;
    public static NTPopupDialogManager getInstance(Context context) {
    	if (mNTPopupDialogManager == null) {
    		mNTPopupDialogManager = new NTPopupDialogManager(context);
    	}
    	return mNTPopupDialogManager;
    }

    NTPopupDialogManager(Context context) {
    }

    public void setPopupWindow(PopupWindow popupWindow) {
    	mPopupWindow = popupWindow;
    }

    public void reset() {
        if (mPopupWindow != null) {
        	mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    protected void clear() {
    	mPopupWindow = null;
    }
}
