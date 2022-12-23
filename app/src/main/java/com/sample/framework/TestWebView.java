/**
 * Copyright (c) 2014 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in the
 * United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */
package com.sample.framework;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.airwatch.gateway.clients.AWWebView;
import com.airwatch.gateway.clients.AWWebViewClient;
import com.airwatch.signaturevalidation.PackageSignatureCheckUtility;

/**
 * @author Gaurav Arora Sep 5, 2014
 */
public class TestWebView extends AWWebView {

    public static final String USER_SUFFIX = " AirWatch Browser";
    private Context mContext;
    private AWWebViewClient mAWWebViewClient;

	public TestWebView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public TestWebView(Context context, AttributeSet arg1) {
		super(context, arg1);
		mContext = context;
		init();
	}

	public TestWebView(Context context, AttributeSet arg1, int arg2) {
		super(context, arg1, arg2);
		mContext = context;
		init();
	}

	private void init() {
		mAWWebViewClient =new AWWebViewClient(mContext);
		getSettings().setUserAgentString(getSettings().getUserAgentString() + USER_SUFFIX);
		getSettings().setJavaScriptEnabled(true);

		setWebViewClient(mAWWebViewClient);
	}

    public void callHttpAuthHandler(String url){
        if(mAWWebViewClient != null){
            mAWWebViewClient.onReceivedHttpAuthRequest(this,null,url,null);
        }
    }
}
