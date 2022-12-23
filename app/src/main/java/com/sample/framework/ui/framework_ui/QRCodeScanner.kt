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
import android.util.Log
import com.airwatch.qrcode.ui.QRCodeCaptureActivity
import com.sample.airwatchsdk.R
import com.sample.main.AppBaseActivity
import kotlinx.android.synthetic.main.activity_qrcode_scanner.*

class QRCodeScanner : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode_scanner)
        scanner.setOnClickListener {
            val intent = Intent(applicationContext, QRCodeCaptureActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            var scannedString: String? = intent?.getStringExtra("SCAN_RESULT")
            scannedString?.let {
                if (scannedString.contains("?") && scannedString.contains("&")) {

                    var url = scannedString.substring(scannedString.indexOf('?') + 1, scannedString.indexOf('&'))
                    url?.let {
                        Log.d("SAMPLE APP", "URL : $url")
                        scanned_url.text = url
                    }

                    var gid = scannedString.substring(scannedString.indexOf('&') + 1)
                    gid?.let {
                        Log.d("SAMPLE APP", "GID : $gid")
                        scanned_gid.text = gid
                    }
                    intent?.getStringExtra("SCAN_RESULT")?.let { it1 -> Log.d("SAMPLE APP", it1) }
                } else {
                    scanned_url.text = "URL not in proper format: $scannedString"
                }
            }
        }
    }
}
