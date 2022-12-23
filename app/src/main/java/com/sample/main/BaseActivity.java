package com.sample.main;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Subham Tyagi,
 * on 08/Sep/2015,
 * 11:00 AM
 * TODO:Add class comment.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BaseActivity extends AppCompatActivity {


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }
}
