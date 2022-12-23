/*
 * AirWatch Android Software Development Kit
 * Copyright (c) 2013 AirWatch. All rights reserved.
 *
 * Unless required by applicable law, this Software Development Kit and sample
 * code is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. AirWatch expressly disclaims any and all
 * liability resulting from User's use of this Software Development Kit. Any
 * modification made to the SDK must have written approval by AirWatch.
 */

package com.sample.airwatchsdk.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.airwatch.core.task.OnTaskCompleteListener;
import com.airwatch.core.task.TaskProcessor;
import com.airwatch.core.task.TaskResult;
import com.sample.airwatchsdk.AirWatchSDKSampleApp;
import com.sample.airwatchsdk.R;
import com.sample.framework.logs.SendLogsTask;
import com.sample.main.AppBaseActivity;
import com.sample.main.MainListAdapter;

public class MainActivity extends AppBaseActivity implements OnItemClickListener {
    MainListAdapter mainList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainList = new MainListAdapter(this, R.array.main_list_items);
        ListView listView = (ListView) findViewById(R.id.mainlist);
        listView.setAdapter(mainList);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TaskProcessor taskProcessor = new TaskProcessor();
        switch (item.getItemId()) {
            case R.id.menu_sendApplicationLogs:
                Log.i(AirWatchSDKSampleApp.TAG, "SendApplicationLogs menu option selected");
                SendLogsTask sendLogsTask = new SendLogsTask(getApplicationContext());
                taskProcessor.execute(sendLogsTask);
                taskProcessor.setOnTaskCompleteListener(new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(final String action, final TaskResult taskResult) {
                        if (action.equals(SendLogsTask.ACTION_SEND_LOGS)) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (taskResult.isSuccess()) {
                                        Toast.makeText(getApplicationContext(), R.string.toast_msg_send_log_success, Toast
                                                .LENGTH_SHORT).show();
                                    } else {
                                        if (taskResult.getStatus() == SendLogsTask.LOGS_NOT_PRESENT) {
                                            Toast.makeText(getApplicationContext(), R.string.toast_msg_log_file_empty, Toast
                                                    .LENGTH_SHORT).show();
                                        } else if (taskResult.getStatus() == SendLogsTask.LOGS_CANNOT_SEND_IN_STANDALONE) {
                                            Toast.makeText(getApplicationContext(), R.string
                                                    .toast_msg_send_log_unavailable, Toast
                                                    .LENGTH_SHORT).show();
                                        }

                                    }

                                }
                            });
                        }
                    }
                });
                break;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> listView, View arg1, int position,
                            long arg3) {
        Intent intent = null;

        switch (position) {
            case 0:
                intent = new Intent(getApplicationContext(), SDKManagerAPIs.class);
                break;
            case 1:
                intent = new Intent(getApplicationContext(), SDKSecureAppInfo.class);
                break;
            case 2:
                intent = new Intent(getApplicationContext(), SDKAuthentication.class);
                break;
            case 3:
                intent = new Intent(getApplicationContext(), SDKPasscode.class);
                break;
            case 4:
                intent = new Intent(getApplicationContext(), SDKCertificateManagement.class);
                break;
            case 5:
                intent = new Intent(getApplicationContext(), SDKAnalytics.class);
                break;
            case 6:
                intent = new Intent(getApplicationContext(), SDKClearAppData.class);
                break;
            case 7:
                intent = new Intent(getApplicationContext(), SDKSSOSessionInfo.class);
                break;
            case 8:
                intent = new Intent(getApplicationContext(), SDKProfileUpdates.class);
                break;
            case 9:
                intent = new Intent(getApplicationContext(), SDKFactoryReset.class);
                break;
            case 10:
                intent = new Intent(getApplicationContext(), SDKRemoveProfileGroup.class);
                break;
            case 11:
                intent = new Intent(getApplicationContext(), SDKRebootDevice.class);
                break;
            case 12:
                intent = new Intent(getApplicationContext(), SDKLogging.class);
                break;
            case 13:
                intent = new Intent(getApplicationContext(), SDKGetAppKeys.class);
                break;
			case 14:
				intent = new Intent(getApplicationContext(), SDKAppConfigActivity.class);
				break;
            case 15:
                intent = new Intent(getApplicationContext(), SDKAutoEnroll.class);
                break;
        }
        if (intent != null)
            startActivity(intent);
    }
}
