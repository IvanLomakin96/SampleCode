/*
 * Copyright (c) 2018 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui.framework_ui


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import com.airwatch.core.AWConstants
import com.airwatch.ui.activity.AboutActivity
import com.airwatch.ui.activity.OpenSourceLicenseActivity
import com.sample.airwatchsdk.R
import com.sample.main.AppBaseActivity
import com.sample.main.MainListAdapter
import kotlinx.android.synthetic.main.activity_main.*


class FrameWorkUIMainActivity : AppBaseActivity(), OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var adaptor = MainListAdapter(this, R.array.ui_main_list_items)
        with(mainlist){
            adapter = adaptor
            onItemClickListener = this@FrameWorkUIMainActivity
        }
    }

    override fun onItemClick(listView: AdapterView<*>, arg1: View, position: Int, arg3: Long) {
        var intent: Intent? = null
        when (position) {
            0 -> intent = Intent(applicationContext, UIElements::class.java)
            1 -> intent = Intent(applicationContext, OpenSourceLicenseActivity::class.java)
            2 -> {
                intent = Intent(applicationContext, AboutActivity::class.java)
                intent.putExtra(AWConstants.OPEN_SOURCE_FILE_URL, "http://www.air-watch.com/downloads/open_source_license_Android_Framework_15.06_GA.txt")
                intent.putExtra(AWConstants.PRIVACY_POLICY_FILE_URL, "http://www.air-watch.com/downloads/airwatch_privacy_policy.txt")
            }
        }
        intent?.run{startActivity(this)}
    }
}