package com.ntian.nguiwidget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	 
	 
	NTMenuDialog mPopup;
	NTEditMenuDialog mEditPopup;
	NTAlertDialog mAlertDialog;
	//boolean isShow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nt_gui_activity_main);
	
		
		mPopup = null;
		//isShow = false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		toggleNTMenu3();
		return false;
	}

	private void toggleNTMenu() {		
		if( mPopup == null  ) {
			 
			List<NTMenuItem> items = new ArrayList<NTMenuItem>();
			NTMenuItem item1 = new NTMenuItem(0 , getResources().getString(R.string.nt_gui_item1));
			NTMenuItem item2 = new NTMenuItem(1 , getResources().getString(R.string.nt_gui_item2));
			item1.isSelected = true;
			item2.isEnable = false;
			items.add(item1);
			items.add(item2);
			
		 
			
			NTMenuAdapter adapter = new NTMenuAdapter(this,items,itemsOnClick);
			
			ViewGroup mRootView = (ViewGroup) getWindow().getDecorView();
			 
			mPopup = new NTMenuDialog(MainActivity.this);
			 
			
			mPopup.setAdapter(adapter);
			
			 
			mPopup.showAtLocation(mRootView, Gravity.FILL | Gravity.CENTER, 0,
					0);
			 
			mPopup.setTitle("12");
			mPopup.update();
		} else {
			 
			
			mPopup.dismiss();
			mPopup = null;
			
		} 
	}
	
	private void toggleNTMenu2() {		
		if( mEditPopup == null  ) {
			 
			 
			ViewGroup mRootView = (ViewGroup) getWindow().getDecorView();
			 
			mEditPopup = new NTEditMenuDialog(MainActivity.this);
			 
			
			mEditPopup.showAtLocation(mRootView, Gravity.FILL | Gravity.CENTER, 0,
					0);
			 
			//mEditPopup.setTitle("12");
			//mEditPopup.update();
		} else {
			 
			
			mEditPopup.dismiss();
			mEditPopup = null;
			
		} 
	}
	
	private void toggleNTMenu3() {		
		if( mEditPopup == null  ) {
			 
			 
			ViewGroup mRootView = (ViewGroup) getWindow().getDecorView();
			 
			mAlertDialog = new NTAlertDialog(MainActivity.this);
			 
			
			mAlertDialog.showAtLocation(mRootView, Gravity.FILL | Gravity.CENTER, 0,
					0);
			 
			mAlertDialog.setTitle("Export to SIM card");
			mAlertDialog.setText("246 phone numbers can be imported to SIM card, continue?");
			//mEditPopup.setTitle("12");
			//mEditPopup.update();
		} else {
			 
			
			mAlertDialog.dismiss();
			mAlertDialog = null;
			
		} 
	}
	
	/*
	@Override
	public void onBackPressed() {
	 
		
		if( mPopup != null  ) {
			mPopup.dismiss();
			mPopup = null;
		} else {
			super.onBackPressed();
		}
	}
	*/
	private NTMenuOnClickListener  itemsOnClick = new NTMenuOnClickListener(){  
		public void onClick(NTMenuItem v, int position) {  
			Log.d("MainActivity" , "v .................................." + v.id);
		}
	};

	
}
  