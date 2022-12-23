/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui.framework_ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;


public class UIElements extends AppBaseActivity implements OnItemClickListener {

	UIListAdapter uiList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uielements);

		uiList = new UIListAdapter(this);
		ListView listView = (ListView) findViewById(R.id.uilist);
		listView.setAdapter(uiList);
		listView.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = null;

		switch (position) {
			case 0:
				intent = new Intent(getApplicationContext(), AirWatchTextViewDemo.class);
				break;
			case 1:
				intent = new Intent(getApplicationContext(), AirWatchEditTextDemo.class);
				break;
			case 2:
				intent = new Intent(getApplicationContext(), AirWatchAutoCompleteTextViewDemo.class);
				break;
			case 3:
				intent = new Intent(getApplicationContext(), AirWatchButtonDemo.class);
				break;
			case 4:
				intent = new Intent(getApplicationContext(), AirWatchCheckBoxDemo.class);
				break;
		}
		if(intent!=null)
			startActivity(intent);

	}
}
