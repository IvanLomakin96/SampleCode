package com.sample.airwatchsdk.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airwatch.sdk.AirWatchSDKException;
import com.airwatch.sdk.SDKManager;
import com.airwatch.util.Logger;
import com.sample.airwatchsdk.R;
import com.sample.main.AppBaseActivity;

import java.util.List;

/**
 * This activity deals with the SDKManager.removeProfileGroup API.
 * API naming may be misleading, it actually removes profiles in action.
 */
public class SDKRemoveProfileGroup extends AppBaseActivity {

	private static final String TAG = "SDKRemoveProfileGroup";

	private ListView mProfileGroupListView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_profile_group);

		mProfileGroupListView = (ListView) findViewById(R.id.remove_profile_group_list);
		TextView emptyList = (TextView) findViewById(R.id.empty_list_view);
		mProfileGroupListView.setEmptyView(emptyList);

		new UpdateProfileGroupList(getApplicationContext(), mProfileGroupListView).execute();
	}
	
	private static class UpdateProfileGroupList extends AsyncTask<String, Void, String> {

		private Context mContext;
		private ListView mPGListView;
		private SDKManager mSDKManager = null;
		private List<String> mProfileGroupList = null;

		public UpdateProfileGroupList(Context context, ListView list) {
			mContext = context;
			mPGListView = list;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				mSDKManager = SDKManager.init(mContext);
				if(mSDKManager != null) {
					try {
						mProfileGroupList = mSDKManager.getAllProfileGroups();
						return "SUCCESS";
					} catch (AirWatchSDKException e) {
						Logger.e(TAG, "AirWatchSDKException has occured");
					}
				}
			} catch (AirWatchSDKException e) {
			}
			
			return null;
		}
		
		@Override
        protected void onPostExecute(String result) {
			if (result == null) {
				Toast.makeText(mContext, "AirWatch SDK Connection Problem.", Toast.LENGTH_LONG).show();
				return;
			}

			if(mProfileGroupList != null && !mProfileGroupList.isEmpty()) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
						android.R.layout.simple_list_item_1, mProfileGroupList);
				mPGListView.setAdapter(adapter);
				mPGListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						try {
							mSDKManager.removeProfileGroup(mProfileGroupList.get(position));
						} catch (AirWatchSDKException e) {
							Logger.e(TAG, "AirWatch SDK Exception has occurred");
						}
						new UpdateProfileGroupList(mContext, mPGListView).execute();
					}
				});
            }
		}
	}
}
