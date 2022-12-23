/**
 * ****************************************************************************
 * Copyright (C) 2014 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at http://www.vmware.com/go/patents.
 * *****************************************************************************
 */
package com.sample.framework.logs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggerService extends Service {

    public static final String TAG = LoggerService.class.getSimpleName();
    protected static final String LS_LINE_SEPARATOR = "\n";

    protected LoggerServiceThread mLSThread;
    protected String mLogcatCmd;
    private boolean isServiceRunning;

    //Initializing member variables
    {
        mLogcatCmd = "logcat AirWatch_:D AirWatch_Proxy:D AirWatch_ClientVerifierImpl:D *:S";
        mLSThread = null;
    }

    protected class LoggerServiceThread extends Thread {
        protected FileWriter mLogFileWriter;
        protected BufferedWriter mLogBufWriter;
        protected File mLogCurFile;
        protected int miFileCount;
        protected Boolean mRunThread;


        //Initializing the variables
        {
            mLogFileWriter = null;
            mLogBufWriter = null;
            mLogCurFile = null;
            miFileCount = 0;
            mRunThread = false;
        }

        public void setRunState(boolean status) {
            mRunThread = status;
        }

        @Override
        public void run() {
            String line = null;
            Process process = null;
            BufferedReader bufRdr = null;
            try {
                process = Runtime.getRuntime().exec(mLogcatCmd);
                bufRdr = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = bufRdr.readLine()) != null && mRunThread) {
                    if (isServiceRunning) {
                        LogsContainer.getInstance().addLogs(line);
                        if (LogsContainer.getInstance().isLoggingStopped())
                            break;
                    } else {
                        stopThread();
                    }
                }
                LogsContainer.getInstance().addLogs(line);
            } catch (IOException e) {
                Log.e(TAG, "run: Got an IO Exception");
            } finally {
                //Close all the streams
                if (bufRdr != null) {
                    try {
                        bufRdr.close();

                    } catch (IOException e) {
                        Log.e(TAG, "run: Got an IO Exception");
                    } finally {
                        bufRdr = null;
                    }
                }
                if (process != null) {
                    process.destroy();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;

    }

    private void stopThread() {
        //Stop the thread if present
        if (mLSThread != null) {
            mLSThread.setRunState(false);
            //We need to log some information as the thread will be blocking on the read line
            Log.i(TAG, "Logging for stopping the thread.");
            mLSThread = null;
        } else {
            Toast.makeText(getApplicationContext(), "Logger not running", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Start the thread here, before starting check whether the
        isServiceRunning = true;
        if (mLSThread != null) {
            Toast.makeText(getApplicationContext(), "Logger already started", Toast.LENGTH_SHORT).show();
        } else {
            mLSThread = new LoggerServiceThread();
            mLSThread.setRunState(true);
            mLSThread.start();
            Toast.makeText(getApplicationContext(), "Logger Started", Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    public static boolean startService(Context cntxt) {
        try {
            //Start the service
            Intent srvcIntnet = new Intent(cntxt, LoggerService.class);
            cntxt.startService(srvcIntnet);
        } catch (Exception e) {
            Log.e(TAG, "Not able to start the logger service: " + e, e);
            return false;
        }

        return true;
    }

    public static boolean stopService(Context cntxt) {
        try {
            //Start the service
            Intent srvcIntnet = new Intent(cntxt, LoggerService.class);
            cntxt.stopService(srvcIntnet);
            Toast.makeText(cntxt, "Logger Service Stopped", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Not able to stop the logger service: " + e, e);
            return false;
        }
        return true;
    }
}
