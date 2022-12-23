package com.sample.framework.ui.oemservice.list;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.airwatch.sdk.aidl.oem.IOEMService;
import com.airwatch.sdk.aidl.oem.OEMMethod;
import com.sample.airwatchsdk.R;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ListMethodsActivityFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<OEMMethod>>, AdapterView.OnItemClickListener {

    public static final int LOADER_ID = 0;
    private ArrayAdapter<ListMethodItem> mAdapter;
    private WeakReference<Callback> mCallback;

    public ListMethodsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_methods, container, false);

        final ListView list = (ListView) view.findViewById(R.id.method_list);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        mAdapter.setNotifyOnChange(false);
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);
        view.findViewById(R.id.list_methods_btn).setOnClickListener(this);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.list_methods_btn:
                refresh();
                break;
        }
    }

    public void refresh() {
        final ListMethodsActivity activity = (ListMethodsActivity) getActivity();
        final IOEMService service = activity.service;
        if(service != null) {
            activity.getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<OEMMethod>> onCreateLoader(int id, Bundle args) {
        final OEMMethodLoader loader = new OEMMethodLoader(getActivity(), ((ListMethodsActivity) getActivity()).service);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<OEMMethod>> loader, List<OEMMethod> data) {
        mAdapter.clear();
        for (OEMMethod oemMethod : data) {
            mAdapter.add(new ListMethodItem(oemMethod));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<OEMMethod>> loader) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
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
        void onInvokeMethod(OEMMethod method);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ListMethodItem item = mAdapter.getItem(position);
        mCallback.get().onInvokeMethod(item.method);

    }

}
