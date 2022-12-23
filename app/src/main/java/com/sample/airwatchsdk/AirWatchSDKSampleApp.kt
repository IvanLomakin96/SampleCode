/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2018 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
 */

package com.sample.airwatchsdk

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.airwatch.app.AWApplication
import com.airwatch.gateway.ui.GatewaySplashActivity
import com.airwatch.notification.PushNotificationManager
import com.airwatch.sdk.AppInfoReader
import com.airwatch.sdk.configuration.AppSettingFlags
import com.airwatch.sdk.context.SDKContext
import com.airwatch.sdk.context.SDKContextException
import com.airwatch.sdk.context.SDKContextManager
import com.airwatch.sdk.logger.AWLog
import com.airwatch.util.Logger
import com.crashlytics.android.Crashlytics
import com.sample.framework.ui.SSLPinningStatusActivity
import com.sample.main.MainLandingActivity
import com.sample.notification.FCMMessagingService
import io.fabric.sdk.android.Fabric
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.security.cert.X509Certificate

class AirWatchSDKSampleApp : AWApplication() {

    companion object {
        lateinit var context: Context
        @JvmStatic  fun getAppContext(): Context? = context

        private const val REQ_SSL_PINNING_STATUS = 999
        private const val META_DATA_KEY_ENABLE_LEAK_CANARY = "com.airwatch.sample.enable.leakcanary"

        const val TAG = "TestAirWatchSDK"
        const val CLEAR_APP_DATA_BROADCAST = "com.sample.airwatchsdk.clearappdata.BROADCAST"
        const val APP_CONFIG_CHANGE_BROADCAST = "com.sample.airwatchsdk.app.config.change.BROADCAST"
        const val APP_CONFIG_CHANGE_BROADCAST_EXTRA_CONFIG = "configuration"
    }

    private val sdkStatusListener = SDKContext.State.StateChangeListener { newState ->
        if(newState == SDKContext.State.IDLE)
            return@StateChangeListener

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        preferences.getString(FCMMessagingService.FCM_PENDING_REGISTRATION_TOKEN, "").let { pendingToken ->
            if(TextUtils.isEmpty(pendingToken))
                return@StateChangeListener
            try {
                PushNotificationManager.getInstance(context).registerToken(pendingToken)
                preferences.edit().remove(FCMMessagingService.FCM_PENDING_REGISTRATION_TOKEN).apply()
            }catch (ex: SDKContextException){
                Logger.w(TAG, "Exception registering the FCM token.")
            }
        }
    }

    override fun onCreate(application: Application) {
        super.onCreate(application)
        SDKContextManager.getSDKContext().stateManager.registerListener(sdkStatusListener)
    }

    override fun onPostCreate() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        context = applicationContext
        val metaData = AppInfoReader.getAppMetaData(applicationContext)

        Fabric.with(this, Crashlytics())
        AWLog.setLoggerTag("SDKSampleApp")
        Logger.setLoggerLevel(Log.VERBOSE)
        loadKoinModules(appModule)

    }

    override fun getMainLauncherIntent(): Intent? {
        return Intent(applicationContext, GatewaySplashActivity::class.java)
    }

    override fun isInputLogoBrandable(): Boolean {
        return true
    }

    override fun getMagCertificateEnable(): Boolean {
        return true
    }

    override fun getMainActivityIntent(): Intent {
        return Intent(applicationContext, MainLandingActivity::class.java)
    }

    override fun getIsStandAloneAllowed(): Boolean {
        return true
    }

    override fun getNotificationActivityIntent(): Intent {
        return mainLauncherIntent!!
    }

    override fun getAppTextEula(): String {
        return "http://google.com"
    }

    override fun onSSLPinningValidationFailure(host: String, serverCACert: X509Certificate?) {
        Toast.makeText(this, "Invalid certificate for endpoint $host", Toast.LENGTH_SHORT).show()
    }

    override fun getScheduleSdkFetchTime() = 120_000

    override fun getSettingsFetchThresholdTimeInMillis() = 120_000

    private fun showSSLPinningStatusNotification() {
        SSLPinningStatusActivity.isActive
        val intent = Intent(this, SSLPinningStatusActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, REQ_SSL_PINNING_STATUS, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent?.let{
            val builder = NotificationCompat.Builder(this, "ssl_pinning_status")
            builder.setContentText("Click to view connectivity status")
                    .setContentTitle("AirWatch Connectivity")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.awmdm)

            val notifMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifMgr.notify(REQ_SSL_PINNING_STATUS, builder.build())
        }
    }

    override fun onSSLPinningRequestFailure(host: String, serverCACert: X509Certificate?) {
        showSSLPinningStatusNotification()
    }

    override fun onTLSCertificateRotationRequired(alias: String, certificateState: Int, certExpTime: Long) {
        Toast.makeText(this, "Certificate Rotation Required $alias", Toast.LENGTH_SHORT).show()
        Logger.e(TAG, "on Certificate Rotation Required with cert state: $certificateState; for alias: $alias")
    }
    override fun getNotificationIcon(): Int {
        return R.drawable.awsdk_ws1_logo
    }

    override fun getFlags(): AppSettingFlags? {
        return super.getFlags()?.apply {
            set(AppSettingFlags.ALLOW_SECURE_CHANNEL_V3, true)
            set(AppSettingFlags.ENABLE_INACTIVE_COMPLIANCE, true)
            set(AppSettingFlags.IS_FEATURE_MODULE_ENABLED, false)
            set(AppSettingFlags.OPERATIONAL_DATA, true)
        }
    }

}

val appModule = module {
    factory { SDKContextManager.getSDKContext().keyManager }
}
