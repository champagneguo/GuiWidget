package com.ntian.nguiwidget.actionbar;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.view.menu.MenuBuilder;
import com.ntian.nguiwidget.NTMenuAdapter;
import com.ntian.nguiwidget.NTMenuItem;
import com.ntian.nguiwidget.NTMenuOnClickListener;
import com.ntian.nguiwidget.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class NTActionBarSplitManager {
    private static NTActionBarSplitManager mNTActionBarSplitManager;
    private Context mContext;
    private Menu mMenu;
    private static int mMaxDisplayCount = 2;
    private boolean mHardKeyMenu = true;
    Button mBtnMore;

    public static NTActionBarSplitManager getInstance(Context context) {
    	if (mNTActionBarSplitManager == null) {
    		mNTActionBarSplitManager = new NTActionBarSplitManager(context);
    	}
    	return mNTActionBarSplitManager;
    }

    NTActionBarSplitManager(Context context) {
    	mContext = context;
    }

    public interface NTActionBarMenuInterface {
        public void onCreateOptionsMenu(Menu menu);
        public void onPrepareOptionsMenu(Menu menu);
        public boolean onOptionsItemSelected(MenuItem item);
        public LayoutInflater getLayoutInflater();
        public ViewGroup getParentContainer();
        public Window getParentWindow();
    }

	class NTActionBarMenuProxy<T> implements NTActionBarMenuInterface {
		T remote;
		MenuInflater menuInflater;
		LayoutInflater layoutInflater;
		ViewGroup parentContainer;
		Window parentWindow;

		NTActionBarMenuProxy(T t, Window window, ViewGroup parent) {
			parentWindow = window; 
			parentContainer = parent;
			remote = t;
			Activity a = null;
			if (remote instanceof Fragment) {
			    a = ((Fragment)remote).getActivity();
			} else if (remote instanceof Activity) {
				a = (Activity)remote;
			}
			if (a != null) {
				menuInflater = a.getMenuInflater();
				layoutInflater = a.getLayoutInflater();
			}
			parentWindow.getDecorView().setTag(this);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu) {
			if (remote instanceof Fragment) {
			    ((Fragment)remote).onCreateOptionsMenu(menu, menuInflater);
			} else if (remote instanceof Activity) {
				((Activity)remote).onCreateOptionsMenu(menu);
			}
		}

		@Override
		public void onPrepareOptionsMenu(Menu menu) {
			if (remote instanceof Fragment) {
			    ((Fragment)remote).onPrepareOptionsMenu(menu);
			} else if (remote instanceof Activity) {
				((Activity)remote).onPrepareOptionsMenu(menu);
			}
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (remote instanceof Fragment) {
			    return ((Fragment)remote).onOptionsItemSelected(item);
			} else if (remote instanceof Activity) {
				return ((Activity)remote).onOptionsItemSelected(item);
			}
			return false;
		}

		@Override
		public LayoutInflater getLayoutInflater() {
			return layoutInflater;
		}

		@Override
		public ViewGroup getParentContainer() {
			return parentContainer;
		}

		@Override
		public Window getParentWindow() {
			return parentWindow;
		}
	}

	//if count is 0, bottom menu bar will be hide, and should use menu key show all the menu
	public void setMaxDisplayCount(int count) {
    	mMaxDisplayCount = count;
    	if (mMaxDisplayCount < 1) mMaxDisplayCount = 0;
    }

    public void reset() {
    	mBtnMore = null;
    	mMenu = null;
    }

    public void onMenuKeyToggled(Context context) {
    	onMenuKeyToggled(context, ((Activity)context).getWindow());
    }

    public void invalidateMenu(Context context) {
    	invalidateMenu(context, ((Activity)context).getWindow());
    }

    public void onMenuKeyToggled(Context context, Window window) {
    	mContext = context;
    	NTActionBarMenuInterface menuProxy = (NTActionBarMenuInterface)window.getDecorView().getTag();
    	if (menuProxy == null || (mBtnMore == null && mMaxDisplayCount > 0) || mMenu == null || mMenu.size() == 0 || !mHardKeyMenu) return;
    	buildLevelTwoMenu(menuProxy);
    }

    public void invalidateMenu(Context context, Window window) {
    	mContext = context;
    	NTActionBarMenuInterface menuProxy = (NTActionBarMenuInterface)window.getDecorView().getTag();
    	if (menuProxy == null) return;
    	buildMenu(context, menuProxy);
    	reLayoutHeight(menuProxy.getParentContainer(), -1);
    }

    public void buildMenu(Context context, final Activity activity) {
    	ViewGroup parentContainerView = (ViewGroup)activity.getWindow().getDecorView();
    	if (parentContainerView == null) return;
        /*ActionBar bar = activity.getActionBar();
        if (bar != null) {
        	//bar.setDisplayOptions(options);
        }*/

    	NTActionBarMenuInterface menuProxy = new NTActionBarMenuProxy<Activity>(activity, activity.getWindow(), parentContainerView);
    	buildMenu(context, menuProxy);
    	reLayoutHeight(menuProxy.getParentContainer(), -1);
    }

    void reLayoutHeight(ViewGroup parentContainerView, int height) {
    	ViewGroup contentView = (ViewGroup)parentContainerView.findViewById(com.android.internal.R.id.decor_content_parent);
    	if (contentView == null) {
    	    contentView = (ViewGroup)parentContainerView.findViewById(android.R.id.content);
    	    if (contentView != null) contentView = (ViewGroup)contentView.getParent();
    	}
        if (contentView instanceof com.android.internal.widget.ActionBarOverlayLayout) {
        	FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)contentView.getLayoutParams();
        	View actionBar = parentContainerView.findViewById(R.id.bottom_bar_container);
        	if (actionBar != null && actionBar.getVisibility() == View.VISIBLE) {
        		//contentView.setPaddingRelative(0, 0, 0, height < 1? (int)mContext.getResources().getDimension(R.dimen.nt_bottom_menubar_height):height);
        		lp.bottomMargin = height < 1? (int)mContext.getResources().getDimension(R.dimen.nt_bottom_menubar_height):height;
        	} else {
        		//contentView.setPaddingRelative(0, 0, 0, 0);
        		lp.bottomMargin = 0;
        	}
        	contentView.setLayoutParams(lp);
        }
    }

    public void buildMenu(Context context, final Fragment fragment, int height, ViewGroup parentContainerView) {
    	if (parentContainerView == null) parentContainerView = (ViewGroup)fragment.getActivity().getWindow().getDecorView();
    	if (parentContainerView == null) return;
    	fragment.setHasOptionsMenu(false);
    	NTActionBarMenuInterface menuProxy = new NTActionBarMenuProxy<Fragment>(fragment, fragment.getActivity().getWindow(), parentContainerView);
    	buildMenu(context, menuProxy);
    	reLayoutHeight(menuProxy.getParentContainer(), height);
    }
    
    public void buildMenuForClock(Context context, final Fragment fragment, int height, ViewGroup parentContainerView,boolean isTransParent) {
    	if (parentContainerView == null) parentContainerView = (ViewGroup)fragment.getActivity().getWindow().getDecorView();
    	if (parentContainerView == null) return;
    	fragment.setHasOptionsMenu(false);
    	NTActionBarMenuInterface menuProxy = new NTActionBarMenuProxy<Fragment>(fragment, fragment.getActivity().getWindow(), parentContainerView);
    	buildMenuForClock(context, menuProxy ,isTransParent);
    	reLayoutHeight(menuProxy.getParentContainer(), height);
    }
    
    protected void buildMenuForClock(Context context, final NTActionBarMenuInterface menuProxy, boolean isTransParent) {
    	
    	mContext = context;
    	mBtnMore = null;
    	mMenu = new MenuBuilder(mContext);
    	menuProxy.onCreateOptionsMenu(mMenu);
    	menuProxy.onPrepareOptionsMenu(mMenu);
    	int size = mMenu.size();
        ViewGroup parentContainer = menuProxy.getParentContainer();

    	View actionBar = parentContainer.findViewById(R.id.bottom_bar_container);
    	
    	if (size == 0) {
	    	if (actionBar != null) {
	    		actionBar.setVisibility(View.GONE);
	    	}
	    	return;
    	}
    	if (actionBar == null) {
    	    actionBar = menuProxy.getLayoutInflater().inflate(R.layout.nt_bottom_menubar, parentContainer);
    	    actionBar = actionBar.findViewById(R.id.bottom_bar_container);
    	} else {
    		actionBar.setVisibility(View.VISIBLE);
    		actionBar.findViewById(R.id.bottom_btn_1).setVisibility(View.GONE);
    		actionBar.findViewById(R.id.bottom_btn_2).setVisibility(View.GONE);
    		actionBar.findViewById(R.id.bottom_btn_3).setVisibility(View.GONE);
    		ViewGroup vg = (ViewGroup)actionBar.findViewById(R.id.button_container);
    		if (vg.getChildCount() > 3) {
    			vg.removeViews(3, vg.getChildCount() - 3);
    		}
    	}
    	
    	if (isTransParent && actionBar != null) {
			View v = actionBar.findViewById(R.id.bottom_bar_divider);
			v.setBackgroundColor(Color.TRANSPARENT);
		}

    	View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			MenuItem _menuItem = (MenuItem)v.getTag();
    			if (_menuItem != null) {
    				menuProxy.onOptionsItemSelected(_menuItem);
    			} else {
    				buildLevelTwoMenu(menuProxy);
    			}
    		}
        };

    	buildLevelOneMenu(actionBar, mBtnOnClickListener, false);
    }
    

    public void buildMenu(Context context, final Fragment fragment, int height) {
    	ViewGroup parentContainerView = (ViewGroup)fragment.getActivity().getWindow().getDecorView();
    	if (parentContainerView == null) return;
    	fragment.setHasOptionsMenu(false);
    	NTActionBarMenuInterface menuProxy = new NTActionBarMenuProxy<Fragment>(fragment, fragment.getActivity().getWindow(), parentContainerView);
    	buildMenu(context, menuProxy);
    	reLayoutHeight(menuProxy.getParentContainer(), height);
    }

    public void buildMenu(Context context, final Fragment fragment) {
    	ViewGroup parentContainerView = (ViewGroup)fragment.getActivity().getWindow().getDecorView();
    	if (parentContainerView == null) return;
    	fragment.setHasOptionsMenu(false);
    	NTActionBarMenuInterface menuProxy = new NTActionBarMenuProxy<Fragment>(fragment, fragment.getActivity().getWindow(), parentContainerView);
    	buildMenu(context, menuProxy);
    	reLayoutHeight(menuProxy.getParentContainer(), -1);
    }

    protected void buildMenu(Context context, final NTActionBarMenuInterface menuProxy) {
    	mContext = context;
    	mBtnMore = null;
    	mMenu = new MenuBuilder(mContext);
    	menuProxy.onCreateOptionsMenu(mMenu);
    	menuProxy.onPrepareOptionsMenu(mMenu);
    	int size = mMenu.size();
        ViewGroup parentContainer = menuProxy.getParentContainer();

    	View actionBar = parentContainer.findViewById(R.id.bottom_bar_container);
    	if (size == 0) {
	    	if (actionBar != null) {
	    		actionBar.setVisibility(View.GONE);
	    	}
	    	return;
    	}
    	if (actionBar == null) {
    	    actionBar = menuProxy.getLayoutInflater().inflate(R.layout.nt_bottom_menubar, parentContainer);
    	    actionBar = actionBar.findViewById(R.id.bottom_bar_container);
    	} else {
    		actionBar.setVisibility(View.VISIBLE);
    		actionBar.findViewById(R.id.bottom_btn_1).setVisibility(View.GONE);
    		actionBar.findViewById(R.id.bottom_btn_2).setVisibility(View.GONE);
    		actionBar.findViewById(R.id.bottom_btn_3).setVisibility(View.GONE);
    		ViewGroup vg = (ViewGroup)actionBar.findViewById(R.id.button_container);
    		if (vg.getChildCount() > 3) {
    			vg.removeViews(3, vg.getChildCount() - 3);
    		}
    	}

    	View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			MenuItem _menuItem = (MenuItem)v.getTag();
    			if (_menuItem != null) {
    				menuProxy.onOptionsItemSelected(_menuItem);
    			} else {
    				buildLevelTwoMenu(menuProxy);
    			}
    		}
        };

    	buildLevelOneMenu(actionBar, mBtnOnClickListener, false);
    }

    int findFirstVisible(int start) {
    	MenuItem menuItem = null;
    	int ret = -1;
    	for (int i=start; i<mMenu.size(); i++) {
    		menuItem = mMenu.getItem(i);
    		if (menuItem.isVisible() && !TextUtils.isEmpty(menuItem.getTitle())) return i;
    	}
    	return ret;
    }

    public void buildLevelOneMenu(final View actionBar, View.OnClickListener btnOnClickListener, boolean isFromDialog) {
    	int size = mMenu.size();
    	if (size == 0) return;
    	int pos = 0;
    	int vpos = findFirstVisible(pos);
    	if (vpos < 0) {actionBar.setVisibility(View.GONE); return;}
    	size -= (vpos - pos);
    	if (size < 1) {actionBar.setVisibility(View.GONE); return;}
    	if (mMaxDisplayCount == 0) {
    		actionBar.setVisibility(View.GONE);
    		return;
    	}
    	pos = vpos;
    	MenuItem menuItem = null;
    	Button btnmenu = null;
    	Button btnMore = null;
    	Drawable icon = null;
    	Drawable defIcon = mContext.getResources().getDrawable(R.drawable.nt_widget_menubar_unknow);
    	if (size > 0) {
    		menuItem = mMenu.getItem(pos);
    		btnmenu = (Button)actionBar.findViewById(R.id.bottom_btn_1);
    		btnmenu.setTag(menuItem);
    		btnmenu.setEnabled(menuItem.isEnabled());
  			btnmenu.setBackgroundResource((menuItem.isChecked())?R.drawable.nt_widget_menubar_bg_checked:R.drawable.nt_widget_menubar_bg);
    		btnmenu.setOnClickListener(btnOnClickListener);
    		btnmenu.setVisibility(View.VISIBLE);
    		btnmenu.setText(menuItem.getTitle());
    		icon = menuItem.getIcon();
    		if (icon == null) icon = defIcon;
    		btnmenu.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null);
    		if (size > 1) btnMore = (Button)actionBar.findViewById(R.id.bottom_btn_2);
    	}
    	pos++;
    	vpos = findFirstVisible(pos);
    	if (vpos < 0) return;
    	size -= (vpos - pos);
    	if (size < 1) return;
    	pos = vpos;
    	if (size > 1) {
    		if (mMaxDisplayCount == 1) {
    			btnMore = (Button)actionBar.findViewById(R.id.bottom_btn_2);
    		} else {
	    		menuItem = mMenu.getItem(pos);
	    		btnmenu = (Button)actionBar.findViewById(R.id.bottom_btn_2);
	    		btnmenu.setTag(menuItem);
	    		btnmenu.setEnabled(menuItem.isEnabled());
	  			btnmenu.setBackgroundResource((menuItem.isChecked())?R.drawable.nt_widget_menubar_bg_checked:R.drawable.nt_widget_menubar_bg);
	    		btnmenu.setOnClickListener(btnOnClickListener);
	    		btnmenu.setVisibility(View.VISIBLE);
	    		btnmenu.setText(menuItem.getTitle());
	    		icon = menuItem.getIcon();
	    		if (icon == null) icon = defIcon;
	    		btnmenu.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null);
	    		btnMore = null;
	    		if (size > 2) btnMore = (Button)actionBar.findViewById(R.id.bottom_btn_3);
    		}
    	}
    	pos++;
    	vpos = findFirstVisible(pos);
    	if (vpos < 0) return;
    	size -= (vpos - pos);
    	if (size < 1) return;
    	pos = vpos;
    	if (size > 2) {
    		if (mMaxDisplayCount == 2) {
    			btnMore = (Button)actionBar.findViewById(R.id.bottom_btn_3);
    		} else if (mMaxDisplayCount > 2) {
	    		menuItem = mMenu.getItem(pos);
	    		btnmenu = (Button)actionBar.findViewById(R.id.bottom_btn_3);
	    		btnmenu.setTag(menuItem);
	    		btnmenu.setEnabled(menuItem.isEnabled());
	  			btnmenu.setBackgroundResource((menuItem.isChecked())?R.drawable.nt_widget_menubar_bg_checked:R.drawable.nt_widget_menubar_bg);
	    		btnmenu.setOnClickListener(btnOnClickListener);
	    		btnmenu.setVisibility(View.VISIBLE);
	    		btnmenu.setText(menuItem.getTitle());
	    		icon = menuItem.getIcon();
	    		if (icon == null) icon = defIcon;
	    		btnmenu.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null);
	    		LinearLayout buttonContainer = (LinearLayout)actionBar.findViewById(R.id.button_container);
	    		int otherDisplayCnt = mMaxDisplayCount - 3;
    	    	pos++;
	    		for(int i=0; i < otherDisplayCnt; i++) {
	    			menuItem = mMenu.getItem(pos + i);
	    			if (menuItem.isVisible() == false || TextUtils.isEmpty(menuItem.getTitle())) continue;
	    			btnmenu = new Button(mContext);
	    			btnmenu.setTag(menuItem);
	        		btnmenu.setEnabled(menuItem.isEnabled());
	      			btnmenu.setBackgroundResource((menuItem.isChecked())?R.drawable.nt_widget_menubar_bg_checked:R.drawable.nt_widget_menubar_bg);
	    			btnmenu.setOnClickListener(btnOnClickListener);
	    			btnmenu.setBackground(null);
	    			btnmenu.setTextColor(mContext.getResources().getColor(R.color.nt_text_second_color));
	    			btnmenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)mContext.getResources().getDimensionPixelSize(R.dimen.nt_title_small));
	    			btnmenu.setCompoundDrawablePadding((int)mContext.getResources().getDimension(R.dimen.nt_button_drawpaddingtop));
		    		btnmenu.setText(menuItem.getTitle());
		    		icon = menuItem.getIcon();
		    		if (icon == null) icon = defIcon;
		    		btnmenu.setCompoundDrawablesRelativeWithIntrinsicBounds(null, icon, null, null);
	    			buttonContainer.addView(btnmenu);
	    		}
	    		btnMore = new Button(mContext);
	    		btnMore.setBackground(null);
	    		btnMore.setTextColor(mContext.getResources().getColor(R.color.nt_text_second_color));
	    		btnMore.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)mContext.getResources().getDimensionPixelSize(R.dimen.nt_title_small));
    			buttonContainer.addView(btnMore);
    		}
    	}
    	if (btnMore != null) {
    		btnMore.setTag(null);
    		btnMore.setBackgroundResource(R.drawable.nt_widget_menubar_bg);
    		btnMore.setOnClickListener(btnOnClickListener);
	    	btnMore.setVisibility(mHardKeyMenu?View.GONE:View.VISIBLE);
	    	btnMore.setText(R.string.nt_widget_more);
	    	btnMore.setCompoundDrawablesRelativeWithIntrinsicBounds(null, mContext.getResources().getDrawable(
	    			isFromDialog?R.drawable.nt_widget_menubar_more_c:R.drawable.nt_widget_menubar_more), null, null);
    		mBtnMore = btnMore;
    	}
    }

    public void buildLevelTwoMenu(final NTActionBarMenuInterface menuProxy) {
    	toggleNTMenu(menuProxy);
    }

    NTActionBarMenuDialog mMenuPopup;

	private NTMenuAdapter prepareMenuAdapter(final NTActionBarMenuInterface menuProxy) {
    	int size = mMenu.size();
        int subSize = size - mMaxDisplayCount;
    	MenuItem menuItem = null;
		List<NTMenuItem> items = new ArrayList<NTMenuItem>();
		NTMenuItem ntMenu = null;
        for(int i=0; i < subSize; i++) {
			menuItem = mMenu.getItem(mMaxDisplayCount + i);
			if (!menuItem.isVisible() || TextUtils.isEmpty(menuItem.getTitle())) continue;
			ntMenu = new NTMenuItem(menuItem.getItemId(), menuItem.getTitle().toString());
			ntMenu.isEnable = menuItem.isEnabled();
			ntMenu.isSelected = menuItem.isChecked();
			ntMenu.setTag(menuItem);
			items.add(ntMenu);
        }

        if (items.size() == 0) return null;

    	NTMenuOnClickListener itemsOnClick = new NTMenuOnClickListener() {
    		public void onClick(NTMenuItem v, int position) {
    			mMenuPopup.dismiss();
    			MenuItem _menuItem = (MenuItem)v.getTag();
    			if (_menuItem != null) {
    				menuProxy.onOptionsItemSelected(_menuItem);
    			}
    		}
    	};

		NTMenuAdapter mMenuadapter = new NTMenuAdapter(mContext, items, itemsOnClick);

		return mMenuadapter;
	}

	private void toggleNTMenu(final NTActionBarMenuInterface menuProxy) {

		mMenuPopup = new NTActionBarMenuDialog(mContext);
		View actionBarView = mMenuPopup.getActionBar();

    	View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
    		@Override
    		public void onClick(View v) {
    			mMenuPopup.dismiss();
    			MenuItem _menuItem = (MenuItem)v.getTag();
    			if (_menuItem != null) {
    				menuProxy.onOptionsItemSelected(_menuItem);
    			}
    		}
        };

		if (!mHardKeyMenu) {
			actionBarView.setVisibility(View.VISIBLE);
			buildLevelOneMenu(actionBarView, mBtnOnClickListener, true);
		} else {
			actionBarView.setVisibility(View.GONE);
		}

		NTMenuAdapter adapter = prepareMenuAdapter(menuProxy);
		if (adapter == null) {
			mMenuPopup = null;
			return;
		}

		mMenuPopup.setAdapter(adapter);
		ViewGroup rootView = (ViewGroup) menuProxy.getParentWindow().getDecorView();
		mMenuPopup.showAtLocation(rootView, Gravity.FILL | Gravity.CENTER, 0, 0);
		mMenuPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				mMenuPopup = null;
			}
		});
	}
}
