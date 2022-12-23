/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui.framework_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sample.airwatchsdk.R;


public class UIListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private String[] title_array; 
	
	public UIListAdapter(Context context) {
		
		mInflater = LayoutInflater.from(context);
		mContext = context;
		title_array = mContext.getResources().getStringArray(R.array.ui_list_items);
	}

	@Override
	public int getCount() {
		return title_array.length;
	}

	@Override
	public Object getItem(int position) {
		return title_array[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.ui_list_item, parent,
					false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.ui_list_item_text);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(title_array[position]);

		return convertView;
	}

	static class ViewHolder {
        TextView title;
    }

}
