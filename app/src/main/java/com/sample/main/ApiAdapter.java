/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2015 AirWatch. All rights reserved.
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sample.airwatchsdk.R;

import org.w3c.dom.Text;

import java.util.Map;

public class ApiAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] title_array;
    private Map<String, String> map;

    public ApiAdapter(Context context, int array, Map<String, String> map) {

        mInflater = LayoutInflater.from(context);
        mContext = context;
        title_array = mContext.getResources().getStringArray(array);
        this.map = map;
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
            convertView = mInflater.inflate(R.layout.api_list_item, parent,
                    false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView
                    .findViewById(R.id.api_list_item_title);
            holder.refresh = (ImageButton) convertView.
                    findViewById(R.id.api_list_item_refresh_button);
            holder.resultValue = (TextView) convertView.findViewById(R.id.api_list_result_value);
            holder.title.setTag(holder.resultValue);
            holder.refresh.setTag(holder.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(title_array[position]);
        String result = map.get(title_array[position]);
        if (result != null) {
            holder.resultValue.setText(result);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        ImageButton refresh;
        TextView resultValue;
    }
}
