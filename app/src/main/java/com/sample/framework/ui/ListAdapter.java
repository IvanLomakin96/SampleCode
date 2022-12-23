/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 *  This product is protected by copyright and intellectual property laws in  the United Sta tes and other countries as well as by international treaties.
 *  AirWatch products may be covered by one or more patents listed at
 *  http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

/**
 * Created by Umang Chamaria.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sample.airwatchsdk.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends ArrayAdapter<String> {

    private Map<String, ?> mSettingMap;

    private List<String> mSettingKeys;

    public ListAdapter(Context context, List<String> settingKeys) {
        super(context, R.layout.key_value_list_row, settingKeys);
        mSettingKeys = settingKeys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.key_value_list_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.bindView(position);
        return convertView;
    }


    public void addData(Map<String, ?> settingMap) {
        mSettingMap = settingMap;
        mSettingKeys = new ArrayList<>(settingMap.keySet());
    }


    class ViewHolder {

        @BindView(R.id.key)
        TextView preferenceKey;
        @BindView(R.id.value)
        TextView preferenceValue;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void bindView(int position) {
            String key = mSettingKeys.get(position);
            preferenceKey.setText(key);
            preferenceValue.setText("" + mSettingMap.get(key));
        }
    }

}

