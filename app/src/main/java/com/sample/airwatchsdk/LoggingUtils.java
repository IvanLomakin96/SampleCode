package com.sample.airwatchsdk;

import com.airwatch.sdk.LoggingUtility;

public class LoggingUtils extends LoggingUtility {

	protected LoggingUtils() {
		super(AirWatchSDKSampleApp.getAppContext());
	}

	@Override
	public String appDataForDiagnosticLogs() {
		StringBuilder str = new StringBuilder();
		str.append(" Test logs from the SDK Sample App.");
		return str.toString();
	}
}