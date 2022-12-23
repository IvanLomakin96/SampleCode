package com.sample.airwatchsdk;

import android.os.AsyncTask;
import android.util.Pair;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;

import java.util.concurrent.Executor;

/**
 *
 */
public class AsyncSDKManager {

    public <T> AsyncTask<Invoker<T>, Void, Pair<Invoker<T>, Pair<T, AirWatchSDKException>>> invoke(Invoker<T> invoker, Executor executor){
        return new AsyncSDKManagerTask<T>().executeOnExecutor(executor, invoker);
    }
    public <T> AsyncTask<Invoker<T>, Void, Pair<Invoker<T>, Pair<T, AirWatchSDKException>>> invoke(Invoker<T> invoker) {
        return invoke(invoker, AsyncTask.SERIAL_EXECUTOR);
    }
    public interface Invoker<T>{
        T invoke(SDKManager sdk) throws AirWatchSDKException;
        void onResult(T result, AirWatchSDKException exception);
    }

    static class AsyncSDKManagerTask<T> extends AsyncTask<Invoker<T>, Void, Pair<Invoker<T>, Pair<T, AirWatchSDKException>>>{

        @Override
        protected Pair<Invoker<T>, Pair<T, AirWatchSDKException>> doInBackground(Invoker<T>... params) {
            Pair<Invoker<T>, Pair<T, AirWatchSDKException>> response;
            final Invoker<T> invoker = params[0];
            T result;
            try {
                final SDKManager sdk = SDKManager.init(AirWatchSDKSampleApp.getAppContext());
                result = invoker.invoke(sdk);
                response = new Pair<>(invoker, new Pair<T, AirWatchSDKException>(result, null));
            } catch (AirWatchSDKException e) {
                response = new Pair<>(invoker, new Pair<T, AirWatchSDKException>(null, e));
            }
            return response;
        }

        @Override
        protected void onPostExecute(Pair<Invoker<T>, Pair<T, AirWatchSDKException>> response) {
            final Invoker<T> invoker = response.first;
            final AirWatchSDKException exception = response.second.second;
            final T result = response.second.first;
            if(exception == null){
                invoker.onResult(result, null);
            }else{
                invoker.onResult(null, exception);
            }
        }
    }
}
