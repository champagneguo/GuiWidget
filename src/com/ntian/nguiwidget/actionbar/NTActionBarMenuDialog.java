package com.ntian.nguiwidget.actionbar;

import java.lang.reflect.Field;

import com.ntian.nguiwidget.NTMenuAdapter;
import com.ntian.nguiwidget.R;
import com.ntian.nguiwidget.R.id;
import com.ntian.nguiwidget.R.layout;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class NTActionBarMenuDialog extends PopupWindow {

	private LayoutInflater minflater;
	private ViewGroup mMenuView;
	private TextView mTitle;
	private WindowManager mWindowManager;
	private View v;
	private NTMenuAdapter mAdapter;
	private ListView mListView;
	private View mActionBarView;
	//private NTMenuOnClickListener mItemsClick;
	private Context mContext;
	private boolean isShowFinish = false;
	
	//public NTMenuDialog(Activity context,AttributeSet attr,int defStyle,
	//		NTMenuOnClickListener itemsOnClick) {
	public NTActionBarMenuDialog(Context context) {
		super(context);

		mContext = context;
		
	//	mItemsClick = itemsOnClick;
		
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		minflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = (ViewGroup) minflater.inflate(
				R.layout.nt_gui_dialog_bg, null);
		
		//final View mPopupView = (View)getFieldValue(this, "mPopupView");
		 //mWindowManager.updateViewLayout(mPopupView, );
		
		//DisplayMetrics displaymetrics = new DisplayMetrics(); 
		//mWindowManager.getDefaultDisplay().getMetrics(displaymetrics); 
		
		//this.setWidth(LayoutParams.MATCH_PARENT);
		//this.setHeight(LayoutParams.MATCH_PARENT);
		
		//this.getBackground().setBounds(0, 0,0,0);
		//this.setWidth(displaymetrics.widthPixels);
		//this.setHeight(displaymetrics.heightPixels);
		this.setOutsideTouchable(true);
		//this.setClippingEnabled(false);
		this.setBackgroundDrawable(null);
		this.setFocusable(true);
		this.setInputMethodMode(INPUT_METHOD_NOT_NEEDED);
		
		this.setHeight(android.view.WindowManager.LayoutParams.FILL_PARENT);
		this.setWidth(android.view.WindowManager.LayoutParams.FILL_PARENT);
		this.setContentView(mMenuView);
		
		
		//ViewGroup mRootView = (ViewGroup) context.getWindow().getDecorView();

		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

//				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
//				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
	//				if (y < height) {
						dismiss();
		//			}
				}
				return true;
			}
		});
		
		isShowFinish = false;
		getContentView().setFocusableInTouchMode(true);
		getContentView().setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//for( int i = 0; i < 10; ++i ) {
				//	Log.d("MainActivity", " on back ..........................................");
				//}
				if( !isShowFinish ) {
					return false;
				}
				if( event.getAction() == KeyEvent.ACTION_UP ) {
					if( event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
						dismiss();
						return true;
					} else if( event.getKeyCode() == KeyEvent.KEYCODE_MENU ) {
						dismiss();
						return true;
					} else {
						return false;
					}
				}
				
				return false;
			}
		});

	  
		v = minflater.inflate(R.layout.nt_gui_actionbar_menu_dialog, null);
		mMenuView.addView(v);
		mListView = (ListView)v.findViewById(R.id.nt_list);
		mActionBarView = v.findViewById(R.id.bottom_bar_container);
		//mActionBarView = minflater.inflate(R.layout.nt_bottom_menubar, null);
		//mListView.addHeaderView(mActionBarView);
		TranslateAnimation animation = new TranslateAnimation(0, 0, 600, 0);
		// animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(150);
		// animation.setFillAfter(true);
		
		
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				isShowFinish = true;
				//setFocusable(true);
				//update();
			}
		});
		
		v.startAnimation(animation); 
		
		
		mTitle = (TextView)v.findViewById(R.id.nt_title);
	}
	
	public View getActionBar() {
		return mActionBarView;
	}

	public void setTitle(String title) {
		TextView mTitleView = (TextView)minflater.inflate(R.layout.nt_gui_list_view_title,null);
		mTitleView.setText(title);
		//mTitleView.setHeight(180);
		mListView.addHeaderView(mTitleView);
		mListView.invalidate();
		/*
		mTitle.setVisibility(View.VISIBLE);
		mTitle.setBackgroundResource(R.drawable.nt_preference_last_item_bg_pressed);
		mTitle.setText(title);
		mTitle.invalidate();*/
	}
	
	public void setAdapter(NTMenuAdapter adapter) {
		mAdapter = adapter;
		mListView.setAdapter(adapter);
	}
	
	public void setSelection(int selection) {
		
	}
/*
	@Override
	public void dismiss() {
		final NTMenuDialog dialog = NTMenuDialog.this;
		boolean mIsShowing = (Boolean)getFieldValue(this, "mIsShowing"); 
		final View mPopupView = (View)getFieldValue(this, "mPopupView");
		final View mContentView = (View)getFieldValue(this, "mContentView"); 
		final OnDismissListener mOnDismissListener = (OnDismissListener)getFieldValue(this, "mOnDismissListener"); 
		
		if (isShowing() && mPopupView != null) {
			mIsShowing = false;

			 
			final PopupWindow t = (PopupWindow) NTMenuDialog.this;
			TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 600);
			animation.setInterpolator(new LinearInterpolator());
			animation.setDuration(100);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					
					try {
						mWindowManager.removeViewImmediate(mPopupView);
					} finally {
						if (mPopupView != mContentView
								&& mPopupView instanceof ViewGroup) {
							((ViewGroup) mPopupView).removeView(mContentView);
						}
						
						
						if (mOnDismissListener != null) {
							mOnDismissListener.onDismiss();
						}
					}
					
				}
			});
			animation.setFillAfter(true);
			v.startAnimation(animation);
		}
	}
	*/
	
	private static Object getFieldValue(Object aObject, String aFieldName) {
		Field field = getClassField(aObject.getClass(), aFieldName);
																	
																	
																	 
		if (field != null) {
			field.setAccessible(true);
			try {
				return field.get(aObject);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	private static Field getClassField(Class aClazz, String aFieldName) {
		Field[] declaredFields = aClazz.getDeclaredFields();
		for (Field field : declaredFields) {
		 
			if (field.getName().equals(aFieldName)) {
				return field; 
			}
		}

		Class superclass = aClazz.getSuperclass();
		if (superclass != null) { 
			return getClassField(superclass, aFieldName);
		}
		return null;
	}
	
}