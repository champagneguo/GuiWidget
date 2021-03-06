package com.ntian.nguiwidget;

import java.lang.reflect.Field;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class NTEditMenuDialog extends PopupWindow {

	private LayoutInflater minflater;
	private ViewGroup mMenuView;
	private TextView mTitle;
	private EditText mEdit;
	private Button negButton;
	private Button posButton;
	private WindowManager mWindowManager;
	private View v;
	
	//private NTMenuOnClickListener mItemsClick;
	private Context mContext;
	private boolean isShowFinish = false;
	
	public NTEditMenuDialogListener mNegButtonListener;
	public NTEditMenuDialogListener mPosButtonListener;
	
	public interface NTEditMenuDialogListener {
		public void onClick(View v, String textContent);
	};
	
	
	//public NTMenuDialog(Activity context,AttributeSet attr,int defStyle,
	//		NTMenuOnClickListener itemsOnClick) {
	public NTEditMenuDialog(Context context) {
		super(context);

		mContext = context  ;
		
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		minflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = (ViewGroup) minflater.inflate(
				R.layout.nt_gui_dialog_bg, null);
		
		
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(null);
		this.setFocusable(true);
	//	this.setInputMethodMode(PopupWindow.INPUT_METHOD_FROM_FOCUSABLE);
		this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		this.setHeight(android.view.WindowManager.LayoutParams.FILL_PARENT);
		this.setWidth(android.view.WindowManager.LayoutParams.FILL_PARENT);
		this.setContentView(mMenuView);
		//this.update();
		
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
		//getContentView().setFocusableInTouchMode(true);
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

	  
		v = minflater.inflate(R.layout.nt_gui_edit_dialog, null);
		mMenuView.addView(v);
		
	 
		TranslateAnimation animation = new TranslateAnimation(0, 0, 600, 0);
		// animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
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
				setFocusable(true);
				update();
			}
		});
		
		v.startAnimation(animation); 
		 
		
		mTitle = (TextView)v.findViewById(R.id.nt_title);
		mEdit = (EditText)v.findViewById(R.id.nt_edit);
		mEdit.requestFocus();
		negButton = (Button)v.findViewById(R.id.nt_neg_button);
		posButton = (Button)v.findViewById(R.id.nt_pos_button);
		
		if( mEdit.getText().toString().equals("") ||
			mEdit.getText().toString().length() == 0 ) {
			posButton.setEnabled(false);
		}
		
		 
		
		mEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if( mEdit.getText().toString().equals("") ||
					mEdit.getText().toString().length() == 0 ) {
					posButton.setEnabled(false);
				} else {
					posButton.setEnabled(true);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				 
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				 
				
			}
		});
		
		
		
		negButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = mEdit.getText().toString();
				dismiss();
				
				if( mNegButtonListener != null ) {
					mNegButtonListener.onClick(v,text);
				}
			}
		});
		
		
		posButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = mEdit.getText().toString();
				dismiss();
				
				if( mPosButtonListener != null ) {
					mPosButtonListener.onClick(v,text);
				}
				
				
			}
		});
	}

	
	
	public void setTitle(String title) {
		//TextView mTitleView = (TextView)minflater.inflate(R.layout.nt_gui_list_view_title,null);
		mTitle.setText(title);
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		NTPopupDialogManager.getInstance(mContext).setPopupWindow(this);
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		super.showAsDropDown(anchor, xoff, yoff, gravity);
		NTPopupDialogManager.getInstance(mContext).setPopupWindow(this);
	}
	
	public void setPosButtonListener(NTEditMenuDialogListener p) {
		mPosButtonListener = p;
	}
	
	public void setNegButtonListener(NTEditMenuDialogListener p) {
		mNegButtonListener = p;
	}

	@Override
	public void dismiss() {
		NTPopupDialogManager.getInstance(mContext).clear();
		super.dismiss();
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