package com.ntian.nguiwidget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NTMenuAdapter extends ArrayAdapter<NTMenuItem> {

	protected LayoutInflater mInflater;
	private static final int mLayout = R.layout.nt_gui_list_view_item;
	private NTMenuOnClickListener mOnClick;
	private int mSelection = -1;
	private Context mContext;
	
	public NTMenuAdapter(Context context, List<NTMenuItem> items,NTMenuOnClickListener click) {

		super(context, mLayout, items);
		
		mContext = context;
		
		mOnClick = click;
		
		
		mInflater = LayoutInflater.from(context);
	}
	
	public void setSelection( int selection ) {
		mSelection = selection;
	}
	
	public int getSelection() {
		return mSelection;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int p = position;
		RelativeLayout result = (RelativeLayout) mInflater.inflate(
				R.layout.nt_gui_list_view_item, parent, false);
		
		final NTMenuItem item = getItem(position);
		
		TextView test = (TextView) result.findViewById(R.id.text);
		test.setText(item.text);
		
		if( item.isEnable ) {
			result.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mOnClick.onClick( item , p);
					
				}
			});
		} else {
			test.setTextColor(mContext.getResources().getColor(R.color.nt_gui_disable_color));
		}
		
		if( item.isSelected ) {
			ImageView arrow = (ImageView)result.findViewById(R.id.arrow);
			arrow.setVisibility(View.VISIBLE);
			test.setTextColor(mContext.getResources().getColor(R.color.nt_gui_select_color));
		}

		return result;
	}
}
