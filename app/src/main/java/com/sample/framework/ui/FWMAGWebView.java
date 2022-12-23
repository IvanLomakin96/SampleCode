/*
 * Copyright (c) 2015 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sample.airwatchsdk.R;

public class FWMAGWebView extends ProxyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwmagweb_view);
    }

    @Override
    protected void showUI() {

    }
}
