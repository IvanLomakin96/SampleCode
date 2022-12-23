/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2013 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
 */

package com.sample.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sample.airwatchsdk.R;

public class MainListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private String[] title_array; 
	
	public MainListAdapter(Context context, int array) {
		
		mInflater = LayoutInflater.from(context);
		mContext = context;
		title_array = mContext.getResources().getStringArray(array);
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
			convertView = mInflater.inflate(R.layout.main_list_item, parent,
					false);

			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.main_list_item_text);

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
