/*
 * Copyright (c) 2019. AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in
 * the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.notification

/**
 * Created by mnovoa on 1/8/19
 */
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.airwatch.notification.PushNotificationManager
import com.airwatch.sdk.AirWatchSDKException
import com.airwatch.sdk.context.SDKContextManager
import com.airwatch.util.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMMessagingService : FirebaseMessagingService() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        val context = SDKContextManager.getSDKContext().context
        PushNotificationManager.getInstance(context).processMessage(message?.notification?.title?: "",
                message?.notification?.body ?: "You got a message", null)
    }

    override fun onNewToken(token: String?) {
        Logger.d(TAG, "onNewToken() called with $token")
        super.onNewToken(token)

        val context = SDKContextManager.getSDKContext().context
        if (token != null) {
            try {
                PushNotificationManager.getInstance(context).registerToken(token)
            } catch (e: AirWatchSDKException) {
                Logger.e(TAG, "Registering FCM token failed", e)
                sharedPreferences.edit().putString(FCM_PENDING_REGISTRATION_TOKEN, token).apply()
            }
        }
    }

    companion object {
        const val FCM_PENDING_REGISTRATION_TOKEN = "com.sample.FCM_PENDING_REGISTRATION_TOKEN"
        private const val TAG = "FCMMessagingService"
    }
}