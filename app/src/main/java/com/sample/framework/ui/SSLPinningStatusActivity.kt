package com.sample.framework.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.airwatch.certpinning.SSLPinningStatus
import com.airwatch.ui.fragments.SSLPinningStatusFragment
import com.sample.airwatchsdk.R
import kotlinx.android.synthetic.main.activity_ssl_pinning_status.*
import kotlinx.android.synthetic.main.content_ssl_pinning_status.*

class SSLPinningStatusActivity : AppCompatActivity(), SSLPinningStatusFragment.Callback {

    companion object {
        @JvmStatic var isActive: Boolean = false
    }

    override fun onPostResume() {
        super.onPostResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun onDataChange(context: SSLPinningStatusFragment, data: MutableList<SSLPinningStatus>) {
        refresh.show()
        progress.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ssl_pinning_status)
        setSupportActionBar(toolbar)

        refresh.setOnClickListener {
            refresh.hide()
            progress.visibility = View.VISIBLE
            (sslPinningStatusFragment as SSLPinningStatusFragment).refresh()
        }
    }

}
