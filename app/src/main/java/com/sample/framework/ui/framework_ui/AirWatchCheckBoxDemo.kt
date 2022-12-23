/*
 * Copyright (c) 2018 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui.framework_ui

import android.os.Bundle

import com.sample.airwatchsdk.R
import com.sample.main.AppBaseActivity


class AirWatchCheckBoxDemo : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_box_demo)
    }
}
