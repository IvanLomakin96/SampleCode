/**
 * Copyright (c) 2016 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws in the
 * United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package com.sample.framework.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airwatch.gateway.auth.ApacheNTLMSchemeFactory;
import com.airwatch.gateway.clients.AWAuthInterceptor;
import com.airwatch.gateway.clients.AWCertAuthUtil;
import com.airwatch.gateway.clients.AWHttpClient;
import com.airwatch.gateway.clients.AWOkHttpAuthenticator;
import com.airwatch.gateway.clients.AWOkHttpClient;
import com.airwatch.gateway.clients.AWUrlConnection;
import com.airwatch.gateway.clients.AWWebView;
import com.airwatch.gateway.clients.AWWebViewClient;
import com.airwatch.gateway.clients.NtlmHttpURLConnection;
import com.airwatch.sdk.certificate.CertificateFetchResult;
import com.airwatch.sdk.certificate.PendingRetryDataModel;
import com.airwatch.sdk.certificate.SCEPCertificateFetchListener;
import com.airwatch.sdk.certificate.SCEPCertificateFetcher;
import com.airwatch.sdk.certificate.SCEPContext;
import com.airwatch.sdk.context.SDKContextManager;
import com.airwatch.storage.SDKKeyStore;
import com.airwatch.storage.SDKKeyStoreUtils;
import com.airwatch.util.Logger;
import com.google.android.material.snackbar.Snackbar;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;
import org.koin.java.KoinJavaComponent;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.airwatch.sdk.certificate.CertificateFetchResult.CERT_FETCH_EXCEPTION;
import static com.airwatch.sdk.certificate.CertificateFetchResult.INVALID_SCEP_STATUS;
import static com.airwatch.sdk.certificate.CertificateFetchResult.MAX_RETRY_COUNT_EXCEEDED;
import static com.airwatch.sdk.certificate.CertificateFetchResult.NO_ERROR;
import static com.airwatch.sdk.certificate.CertificateFetchResult.RETRY_TIMEOUT_NOT_ELAPSED_RETRY_SCHEDULED;
import static com.airwatch.sdk.certificate.CertificateFetchResult.SCEP_INSTRUCTIONS_UNAVAILABLE;
import static com.airwatch.storage.SDKSecurePreferencesKeys.IA_CERT_ALIAS;
import static org.apache.http.client.params.AuthPolicy.NTLM;

/**
 * Activity to showcase NTLM and SSL Client Cert Authentication features for various
 * HTTP Clients and the WebView. This class contains code examples for the default HTTP clients
 * and the AW wrapper classes
 */
public class IntegratedAuthActivity extends AppBaseActivity implements View.OnClickListener {

    private static final String TAG = "IntegratedAuthActivity";

    private TextView textView;
    private EditText editView;
    private AWWebView mAwWebView;
    private WebView mWebView;
    Button httpClientBtn, httpUrlConnectionBtn, okhttpClientBtn, webViewBtn;

    LinearLayout rootLayout;

//    private boolean useAWHttpClients;

    private static final String SCEP_SETTINGS_MENU = "SCEP Settings";
    private static final String SCEP_PAUSE_POLLING = "Pause Polling";
    private static final String LOG_CERT_DETAILS = "Log Cert Details";

    private static final String SCEP_MAX_COUNT_KEY = "max_count";
    private static final String SCEP_RETRY_INTERVAL_KEY = "retry_interval";

    private SCEPCertificateFetchListener certStatusListener = new SCEPCertificateFetchListener() {
        @Override
        public void onResult(CertificateFetchResult certificateFetchResult) {
            handleResult(certificateFetchResult);
        }
    };

    private SCEPCertificateFetcher certificateFetcher = SCEPContext.getInstance().getSCEPCertificateFetcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntlm_and_basic_auth);

        httpClientBtn = (Button) findViewById(R.id.httpclient_btn);
        webViewBtn = (Button) findViewById(R.id.webview_btn);
        httpUrlConnectionBtn = (Button) findViewById(R.id.httpurlconnection_btn);
        okhttpClientBtn = (Button) findViewById(R.id.okhttp_btn);

        rootLayout = (LinearLayout) findViewById(R.id.root_view);

        httpClientBtn.setOnClickListener(this);
        httpUrlConnectionBtn.setOnClickListener(this);
        webViewBtn.setOnClickListener(this);
        okhttpClientBtn.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.response_code);
        editView = (EditText) findViewById(R.id.edit_text);
        mAwWebView = (AWWebView) findViewById(R.id.web_view);

        mAwWebView.setWebViewClient(new AWWebViewClient(this) {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        mWebView = new WebView(this);
        mWebView.setTag("webview");

        mWebView.setWebViewClient(new AWWebViewClient(this) {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        if (rootLayout.findViewById(R.id.web_view) == null) {
            rootLayout.addView(mAwWebView);
        }

        certificateFetcher.registerFetchListener(certStatusListener);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int maxCount = pref.getInt(SCEP_MAX_COUNT_KEY, SCEPCertificateFetcher.DEFAULT_MAX_RETRY_COUNT);
        int retryInterval = pref.getInt(SCEP_RETRY_INTERVAL_KEY, SCEPCertificateFetcher.DEFAULT_RETRY_INTERVAL_SECONDS);
        certificateFetcher.setMaxRetryCount(maxCount);
        certificateFetcher.setRetryIntervalSeconds(retryInterval);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (certificateFetcher.isSCEPCertificatePending()) {
            certificateFetcher.triggerPolling();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseSCEPCertificatePolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        certificateFetcher.unregisterFetchListener(certStatusListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(SCEP_SETTINGS_MENU);
        menu.add(SCEP_PAUSE_POLLING);
        menu.add(LOG_CERT_DETAILS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getTitle().equals(SCEP_SETTINGS_MENU)) {
            showSCEPSettings();
            return true;
        } else if (item.getTitle().equals(SCEP_PAUSE_POLLING)) {
            pauseSCEPCertificatePolling();
            return true;
        } else if (item.getTitle().equals(LOG_CERT_DETAILS)) {
            logCertDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logCertDetails() {
/*
        try {
            SDKKeyStore sdkKeyStore = SDKContextManager.getSDKContext().getKeyStore();
            KeyStore keyStore = KoinJavaComponent.get(SDKKeyStoreUtils.class).
                    getPKCS12(SDKContextManager.getSDKContext().getSDKSecurePreferences().getString(IA_CERT_ALIAS, ""));

            X509Certificate[] certificates = null;
            StringBuilder sb = new StringBuilder();

            Enumeration<String> e = keyStore.aliases();
            while (e.hasMoreElements()) {
                String alias = e.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry
                            (alias, null);
                    certificates = (X509Certificate[]) entry.getCertificateChain();
                    break;
                }
            }

            if (certificates != null) {
                sb.append("Subject: ").append(certificates[0].getSubjectDN()).append('\n');
                sb.append("Issuer: ").append(certificates[0].getIssuerDN()).append('\n');

                if (certificates[0].getSubjectAlternativeNames() == null) {
                    sb.append("SAN: null").append('\n');
                } else {

                    sb.append("SAN: not null").append('\n');

                    for (List<?> san : certificates[0].getSubjectAlternativeNames()) {
                        sb.append("SAN entry: ").append(san).append('\n');

                        for (Object item : san) {
                            sb.append("SAN entry component: ").append(item).append('\n');

                            if (item instanceof byte[]) {
                                sb.append("SAN entry byte[] component: ").append(new String((byte[]) item)).append('\n');
                            }
                        }
                    }
                }
            } else {
                sb.append("No certs found");
            }

            Logger.d(TAG, "Cert details: " + sb.toString());
            showSnackbar("Cert details: " + sb.toString());

        } catch (Exception ex) {
            Logger.e(TAG, "Error retrieving cert details", ex);
            showSnackbar("Error retrieving cert details: " + ex.getMessage());
        }*/
    }

    private void showSCEPSettings() {
        final AlertDialog.Builder scepDialog = new AlertDialog.Builder(this);

        scepDialog.setTitle(SCEP_SETTINGS_MENU);
        scepDialog.setCancelable(true);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int maxCount = pref.getInt(SCEP_MAX_COUNT_KEY, SCEPCertificateFetcher.DEFAULT_MAX_RETRY_COUNT);
        int retryInterval = pref.getInt(SCEP_RETRY_INTERVAL_KEY, SCEPCertificateFetcher.DEFAULT_RETRY_INTERVAL_SECONDS);

        final TextView tvMaxCount = new TextView(this);
        tvMaxCount.setText("Max Retry Count:");

        final EditText etMaxCount = new EditText(this);
        etMaxCount.setHint("Max Retry Count");
        etMaxCount.setText(Integer.toString(maxCount));

        final TextView tvRetryInterval = new TextView(this);
        tvRetryInterval.setText("Retry Interval:");

        final EditText etRetryInterval = new EditText(this);
        etRetryInterval.setHint("Retry Interval");
        etRetryInterval.setText(Integer.toString(retryInterval));

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.addView(tvMaxCount);
        dialogLayout.addView(etMaxCount);
        dialogLayout.addView(tvRetryInterval);
        dialogLayout.addView(etRetryInterval);
        scepDialog.setView(dialogLayout);

        scepDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int changedMaxCount = Integer.parseInt(etMaxCount.getText().toString().trim());
                int changedRetryInterval = Integer.parseInt(etRetryInterval.getText().toString().trim());

                pref.edit().putInt(SCEP_MAX_COUNT_KEY, changedMaxCount)
                        .putInt(SCEP_RETRY_INTERVAL_KEY, changedRetryInterval)
                        .commit();
            }
        });

        scepDialog.show();
    }

    private void pauseSCEPCertificatePolling() {
        boolean cancelled = certificateFetcher.pausePolling();

        if (cancelled) {
            showSnackbar("Paused SCEP Certificate polling");
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        textView.setText("");

        //Clear the WebViews
        mAwWebView.loadUrl("about:blank");
        mWebView.loadUrl("about:blank");

        switch (v.getId()) {
            case R.id.httpurlconnection_btn:
                invokeAWHttpUrlConnecton();
                break;
            case R.id.httpclient_btn:
                invokeAWHttpClient();
                break;
            case R.id.webview_btn:
                invokeAWWebView();
                break;
            case R.id.okhttp_btn:
                invokeAWOkHttpClient();
                break;
        }
    }

    /*
     * AWUrlConnection
     */
    private void invokeAWHttpUrlConnecton() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "awHttpUrlConnectionHandler ");
                awHttpUrlConnectionHandler();
            }
        }).start();
    }

    private void awHttpUrlConnectionHandler() {
        NtlmHttpURLConnection ntlmHttpURLConnection = null;
        HttpURLConnection httpURLConnection = null;
        try {
            // Create a trust manager that does not validate certificate chains
            allowSelfSigned();
            URL url = new URL(editView.getText().toString());

            CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);

            httpURLConnection = (HttpURLConnection) AWUrlConnection.openConnection(url);

            if (url.getProtocol().equalsIgnoreCase("https")) {
                SSLContext sslContext = SSLContext.getInstance("TLS");

                //Get KeyManagers from AWCertAuthUtil.getCertAuthKeyManagers() for SSL Client Cert IA
                sslContext.init(
                        AWCertAuthUtil.getCertAuthKeyManagers(),
                        new TrustManager[]{trustAllTrustManager},
                        new SecureRandom());
                ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sslContext.getSocketFactory());
            }

            //Decorate any HttpUrlConnection with NtlmHttpUrlConnection to get Basic and NTLM auth.
            ntlmHttpURLConnection = new NtlmHttpURLConnection(httpURLConnection);

            ntlmHttpURLConnection.setUseCaches(false);

            ntlmHttpURLConnection.setConnectTimeout(15000);
            ntlmHttpURLConnection.connect();

            final int responseCode = ntlmHttpURLConnection.getResponseCode();

            showResultOnTextView(Integer.toString(responseCode));

        } catch (Exception e) {
            showResultOnTextView("Error");
            Logger.e(TAG, e);
        }

        try {
            // If any response body is returned with a 400 status code, URLConnection provides this
            // body in the getErrorStream() method instead of getInputStream().
            if (ntlmHttpURLConnection.getResponseCode() >= 400) {
                logResponse(ntlmHttpURLConnection.getErrorStream());
            } else {
                logResponse(ntlmHttpURLConnection.getInputStream());
            }
        } catch (Exception e) {
            Logger.e(TAG, e);
        } finally {
            ntlmHttpURLConnection.disconnect();
        }
    }

    private void logResponse(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();

        byte[] buf = new byte[128];

        while (inputStream.read(buf) != -1) {
            sb.append(new String(buf));
        }

        Logger.i("AwHttpUrlConnection response: " + sb.toString());
    }

    /*
     * Default HTTP Client and AWHttpClient
     */
    private void invokeAWHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                awHttpClientHandler();
            }
        }).start();
    }

    private void invokeHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                httpClientHandler();
            }
        }).start();
    }

    private void awHttpClientHandler() {
        //The KeyManagers for SSL Client Cert IA are added in the AWTrustAllHttpClient implementation
        AWHttpClient httpclient = new AWTrustAllHttpClient(editView.getText().toString());
        httpclient.setCookieStore(new BasicCookieStore());
        try {
            HttpGet httpPost = new HttpGet(editView.getText().toString());
            final HttpResponse response = httpclient.execute(httpPost);
            final String responseCode = Integer.toString(response.getStatusLine().getStatusCode());
            showResultOnTextView(responseCode);
        } catch (Exception e) {
            Logger.e(TAG, e);
            showResultOnTextView("Error");
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    private void httpClientHandler() {
        //The KeyManagers for SSL Client Cert IA are added in the TrustAllHttpClient implementation
        DefaultHttpClient httpclient = new TrustAllHttpClient(editView.getText().toString());
        httpclient.setCookieStore(new BasicCookieStore());

        //Register the NTLMScheme for NTLM IA support
        httpclient.getAuthSchemes().register(NTLM, new ApacheNTLMSchemeFactory());
        httpclient.addRequestInterceptor(new AWAuthInterceptor(false), 0);

        try {
            HttpGet httpPost = new HttpGet(editView.getText().toString());
            final HttpResponse response = httpclient.execute(httpPost);
            final String responseCode = Integer.toString(response.getStatusLine().getStatusCode());
            showResultOnTextView(responseCode);
        } catch (Exception e) {
            Logger.e(TAG, e);
            showResultOnTextView("Error");
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    /*
     * Default OkHttpClient and AWOkHttClient
     */
    private void invokeAWOkHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                awOkHttpClientHandler();
            }
        }).start();
    }

    private void invokeOkHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                okHttpClientHandler();
            }
        }).start();
    }

    private CookieJar okhttpCookieJar = new CookieJar() {

        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    };

    private void awOkHttpClientHandler() {
        try {

            OkHttpClient clientOrg = new OkHttpClient.Builder()
                    .cookieJar(okhttpCookieJar)
                    .build();

            OkHttpClient client = AWOkHttpClient.copyWithDefaults(clientOrg, trustAllTrustManager);

            Request request = new Request.Builder()
                    .url(editView.getText().toString())
                    .build();

            final Response response = client.newCall(request).execute();
            final String responseCode = Integer.toString(response.code());
            Logger.i("Status code AwOkHttpClient :" + responseCode);
            final String message = response.body().string();
            Logger.i("Default AWOkHttpClient response: " + message);
            response.body().close();
            showResultOnTextView(responseCode);
        } catch (Exception e) {
            Logger.e("Unable to send request AWOkHttpClient", e);
            showResultOnTextView("Error");
        }
    }

    private void okHttpClientHandler() {
        try {

            Authenticator awAuthenticator = new AWOkHttpAuthenticator(false);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            // Create an SSLContext instance using the KeyManagers from
            // AWCertAuthUtil.getCertAuthKeyManagers() SSL Cert IA.
            sslContext.init(AWCertAuthUtil.getCertAuthKeyManagers(),
                    new TrustManager[]{trustAllTrustManager}, null);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(okhttpCookieJar)
                    .sslSocketFactory(sslContext.getSocketFactory(), trustAllTrustManager)
                    .authenticator(awAuthenticator).build(); //Set awAuthenticator for NTLM IA.

            Request request = new Request.Builder()
                    .url(editView.getText().toString())
                    .build();

            final Response response = client.newCall(request).execute();
            final String responseCode = Integer.toString(response.code());
            Logger.i("Status code OkHttpClient :" + responseCode);
            final String message = response.body().string();
            Logger.i("Default OkHttpClient response: " + message);
            response.body().close();
            showResultOnTextView(responseCode);
        } catch (Exception e) {
            Logger.e("Unable to send request OkHttpClient", e);
            showResultOnTextView("Error");
        }
    }

    /*
     * Default WebView and AWWebView
     */
    private void invokeAWWebView() {
        mAwWebView.loadUrl(editView.getText().toString());
    }

    private void invokeWebView() {
        mWebView.loadUrl(editView.getText().toString());
    }

    /*
     * Private utility functions
     */
    private void showResultOnTextView(final String responseCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(responseCode);
            }
        });
    }

    private static X509TrustManager trustAllTrustManager = new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    };

    private static void allowSelfSigned() {
        // Install the all-trusting trust manager
        try {

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{trustAllTrustManager}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            Logger.e("error in sllcontext init");
        }
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    private class AWTrustAllHttpClient extends AWHttpClient {

        private String url;

        public AWTrustAllHttpClient(String url) {
            this.url = url;
        }

        @Override
        protected ClientConnectionManager createClientConnectionManager() {
            return getClientConnectionManager(getParams(), url);
        }
    }

    private class TrustAllHttpClient extends DefaultHttpClient {

        private String url;

        public TrustAllHttpClient(String url) {
            this.url = url;
        }

        @Override
        protected ClientConnectionManager createClientConnectionManager() {
            return getClientConnectionManager(getParams(), url);
        }
    }

    private ClientConnectionManager getClientConnectionManager(HttpParams params, String url) {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", getTrustAllSslSocketFactory(), 443));

        if (url.trim().startsWith("https")) {
            try {
                URL urlObj = new URL(url.trim());
                int customPort = urlObj.getPort();

                if (customPort != 443 && customPort != -1) {
                    registry.register(new Scheme("https", getTrustAllSslSocketFactory(), customPort));
                }

            } catch (MalformedURLException e) {
            } //Ignore
        }

        return new SingleClientConnManager(params, registry);
    }

    private SSLSocketFactory getTrustAllSslSocketFactory() {

        try {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(null, null);

            TrustAllSslSocketFactory sslfactory = new TrustAllSslSocketFactory(trustStore);
            sslfactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sslfactory;
        } catch (Exception e) {
            Logger.e("Could not create All Trust SSL Socket Factory.", e);
            return null;
        }
    }

    /**
     * This is just an example to show the implementation of SSLSocketFactory.
     *
     */
    private class TrustAllSslSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public TrustAllSslSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {

            super(truststore);

            KeyManager[] clientCertAuthKeyManagers = AWCertAuthUtil.getCertAuthKeyManagers();

            sslContext.init(clientCertAuthKeyManagers, new TrustManager[]{trustAllTrustManager}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private void handleResult(CertificateFetchResult result) {
        switch (result.getStatus()) {
            case SUCCESS:
                showSnackbar("SCEP certificate fetch succeeded");
                break;
            case FAILURE:
                String errorString = getErrorString(result.getErrorCode());
                showSnackbar("SCEP certificate fetch failed. " + errorString);
                break;
            case PENDING:
                String messageString = getErrorString(result.getErrorCode());
                PendingRetryDataModel retryDataModel = result.getPendingRetryDataModel();
                String retryMessage = "Attempts remaining: " + retryDataModel.getRetryAttemptsRemaining() +
                        ". Time remaining for next retry: " + retryDataModel.getTimeRemainingForNextRetryAttempt();
                showSnackbar(messageString + " " + retryMessage);
                break;
        }
    }

    private String getErrorString(int code) {
        switch (code) {
            case SCEP_INSTRUCTIONS_UNAVAILABLE:
                return "SCEP instructions unavailable.";
            case CERT_FETCH_EXCEPTION:
                return "Exception occurred while fetching SCEP certificate.";
            case INVALID_SCEP_STATUS:
                return "Invalid SCEP status.";
            case MAX_RETRY_COUNT_EXCEEDED:
                return "Max retry count exceeded.";
            case RETRY_TIMEOUT_NOT_ELAPSED_RETRY_SCHEDULED:
                return "Retry timeout not elapsed yet. Retry has been scheduled.";
            case NO_ERROR:
                return "Server returned the status as 'Pending'.";
            default:
                return "Unknown error.";
        }
    }
}