package com.sample.framework.ui.oemservice.list;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;
import android.util.Log;

import com.airwatch.sdk.aidl.oem.IOEMService;
import com.airwatch.sdk.aidl.oem.OEMMethod;
import com.sample.framework.ui.oemservice.Constants;

import java.util.Collections;
import java.util.List;

/**
 * Created by STurner on 5/15/15.
 *
 * @author Stephen Turner <stephenturner265@air-watch.com>
 */
class OEMMethodLoader extends AsyncTaskLoader<List<OEMMethod>> {

    private final IOEMService mService;

    OEMMethodLoader(Context context, IOEMService service) {
        super(context);
        mService = service;
    }

    @Override
    public List<OEMMethod> loadInBackground() {
        List<OEMMethod> methods = Collections.emptyList();
        try {
            if (mService != null) {
                methods = mService.listMethods();
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "could not get oem methods", e);
        }
        return methods;
    }
}