package com.takee.setupwizard.wifi;

import android.net.wifi.ScanResult;

public interface OnNetworkChangeListener {
	void onNetWorkDisConnect();

	void onNetWorkConnect(String connectingSSID);
	
}
