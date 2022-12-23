package com.sample.framework.ui.oemservice.invoke;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.airwatch.sdk.aidl.oem.OEMMethod;
import com.sample.airwatchsdk.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class InvokeMethodActivityFragment extends Fragment implements View.OnClickListener {

    private WeakReference<Callback> mCallback;
    private ListView mList;

    private HashMap<Integer, String> mArgsMap = new HashMap<>();


    public InvokeMethodActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_invoke_method, container, false);

        mList = (ListView) view.findViewById(R.id.method_param_list);

        mList.setAdapter(new ParameterAdapter(getActivity(), mCallback.get().getMethod().parameterNames, mArgsMap));
        mList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        ((TextView)view.findViewById(R.id.method_name_text)).setText(mCallback.get().getMethod().name);
        view.findViewById(R.id.invoke_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.invoke_button:
                onInvoke();
                break;
        }
    }

    private void onInvoke() {
        final ListAdapter adapter = mList.getAdapter();
        final int count = adapter.getCount();
        final ArrayList<String> args = new ArrayList<>(count);
        for(int i=0; i<count; i++){
            final String argVal = mArgsMap.get(i);
            args.add(argVal == null ? "" : argVal);
        }
        mCallback.get().onInvoke(args);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(!(activity instanceof Callback)){
            throw new IllegalArgumentException("activity "+activity+" must implement "+Callback.class);
        }
        mCallback = new WeakReference<>((Callback) activity);
    }

    interface Callback{
        OEMMethod getMethod();
        void onInvoke(List<String> args);
    }
}
