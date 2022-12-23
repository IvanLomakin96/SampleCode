package com.sample.framework.ui.oemservice.invoke;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by STurner on 5/15/15.
 *
 * @author Stephen Turner <stephenturner265@air-watch.com>
 */
class ViewHolder implements TextWatcher {
    public TextView nameText;
    private EditText mValueEditText;
    public int position;

    private Map<Integer, String> mArgsMap;

    public ViewHolder(Map<Integer, String> argsMap) {
        mArgsMap = argsMap;
    }

    public EditText getValueEditText() {
        return mValueEditText;
    }

    public void setValueEditText(EditText valueEditText) {
        if(this.mValueEditText != null){
            this.mValueEditText.removeTextChangedListener(this);
        }
        this.mValueEditText = valueEditText;
        this.mValueEditText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mArgsMap.put(position, s.toString());
    }
}
