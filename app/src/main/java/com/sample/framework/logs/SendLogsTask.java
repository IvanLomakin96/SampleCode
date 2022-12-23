package com.sample.framework.logs;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.airwatch.core.task.AbstractSDKTask;
import com.airwatch.core.task.TaskResult;
import com.airwatch.login.SDKAppAuthenticator;
import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.sdk.context.SDKContext;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.storage.SDKSecurePreferencesKeys;

/**
 * Created by josephben on 11/2/2015.
 */
public class SendLogsTask extends AbstractSDKTask {
    private Context context;
    private static final String TAG=SendLogsTask.class.getSimpleName();
    public static final String ACTION_SEND_LOGS="com.sample.framework.ACTION_SEND_LOGS";
    public static int LOGS_NOT_PRESENT=0;
    public static int LOGS_CANNOT_SEND_IN_STANDALONE=1;
    public static int LOGS_SENT=2;



    public SendLogsTask(Context context){
        super(context);
        this.context=context;
    }
    @Override
    public TaskResult execute() {
        mTaskResult.setStatus(LOGS_NOT_PRESENT);
        try {
            SDKContext sdkContext = SDKContextManager.getSDKContext();
            SharedPreferences securePreferences = sdkContext.getSDKSecurePreferences();
            //if anchor app not present, log files are not sent.
            if (securePreferences.getString(SDKSecurePreferencesKeys.SSO_MODE,
                    SDKAppAuthenticator.AUTHENTICATION_MODE_SSO)
                    .equals(SDKAppAuthenticator.AUTHENTICATION_MODE_STANDALONE)) {
                Log.i(TAG, "Send logs unavailable in standalone mode");
                mTaskResult.setStatus(LOGS_CANNOT_SEND_IN_STANDALONE);
            } else {
                if (SDKManager.init(context).uploadApplicationLogs() == true) {
                    mTaskResult.setStatus(LOGS_SENT);
                    mTaskResult.setIsSuccess(true);
                    Log.i(TAG, "Logs sent!");
                } else {
                    mTaskResult.setStatus(LOGS_NOT_PRESENT);
                    Log.i(TAG,"Log file empty!");
                }
            }
        } catch (AirWatchSDKException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception while trying to send the application logs");
        }
        finally {
         return mTaskResult;
        }
    }

    @Override
    public String getTaskAction() {
        return ACTION_SEND_LOGS;
    }
}
