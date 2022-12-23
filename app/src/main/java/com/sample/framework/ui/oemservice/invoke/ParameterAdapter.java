package com.sample.framework.ui.oemservice.invoke;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


import com.sample.airwatchsdk.R;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by STurner on 5/15/15.
 *
 * @author Stephen Turner <stephenturner265@air-watch.com>
 */
class ParameterAdapter extends BaseAdapter {

    private final String[] mParameterNames;
    private final Map<Integer, String> mArgs;
    private final WeakReference<Activity> mContext;

    ParameterAdapter(Activity context, String[] parameterNames, Map<Integer, String> args) {
        mParameterNames = parameterNames;
        mArgs = args;
        mContext = new WeakReference<>(context);
    }


    @Override
    public int getCount() {
        return mParameterNames.length;
    }

    @Override
    public Object getItem(int position) {
        return mParameterNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext.get()).inflate(R.layout.method_parameter_item, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder(mArgs);
            holder.nameText = (TextView) convertView.findViewById(R.id.name_text);
            final EditText valueEditText = (EditText) convertView.findViewById(R.id.value_edit_text);
            holder.setValueEditText(valueEditText);
            convertView.setTag(holder);
        }
        holder.position = position;
        final String paramName = (String) getItem(position);
        holder.nameText.setText(paramName);

        final String argValue = mArgs.get(position);
        holder.getValueEditText().setText(argValue == null ? "" : argValue);

        return convertView;
    }
}