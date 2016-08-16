package com.takee.setupwizard.page;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.takee.setupwizard.MainActivity;
import com.takee.setupwizard.R;
import com.takee.setupwizard.adapter.WifiListAdapter;
import com.takee.setupwizard.base.BasePage;
import com.takee.setupwizard.bean.MyWifiInfo;
import com.takee.setupwizard.view.WifiConnDialog;
import com.takee.setupwizard.view.WifiCreateDialog;
import com.takee.setupwizard.view.WifiForgetDialog;
import com.takee.setupwizard.wifi.OnNetworkChangeListener;
import com.takee.setupwizard.wifi.WifiAdmin;

public class WifiPage extends BasePage {

	private static final String TAG = "WifiPage";

	private View rootView;
	private Switch wifiSwitch;
	private Context mContext;
	private ListView listView;

	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	private String mConnectingSSID;
	private ConnectivityManager connectivityManager;

	private WifiAdmin mWifiAdmin;

	private List<MyWifiInfo> wifiInfos;
	private WifiListAdapter listAdapter;

	private TextView noDataSpaceTextView;

	public WifiPage(MainActivity mainActivity, View page2_wifi) {
		mContext = mainActivity;
		rootView = page2_wifi;
		initWifi();
		initView();
		getWifiList();
		setWifiSwitchState();
		registWifiReceiver();
	}

	@Override
	public void onResume() {
		getWifiList();
		if (wifiInfos != null) {
			listAdapter.setList(wifiInfos);
			listAdapter.notifyDataSetChanged();
		}
		if (wifiSwitch != null) {
			setWifiSwitchState();
		}
		showWifiMessageView();
	}

	public void setWifiEnable() {
		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			wifiManager.startScan();
			getWifiList();
		}

		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			wifiSwitch.setChecked(true);
		} else {
			wifiSwitch.setChecked(false);
		}

		if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			wifiSwitch.setEnabled(true);
			setWifiInfoToNull();
		} else {
			wifiSwitch.setEnabled(false);
		}
		showWifiMessageView();
	}

	private void initWifi() {
		wifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
		mWifiAdmin = new WifiAdmin(mContext);
	}

	private void initView() {
		listView = (ListView) rootView.findViewById(R.id.wifiList);
		noDataSpaceTextView = (TextView) rootView
				.findViewById(R.id.no_data_space_textview);
		if (wifiInfos == null) {
			wifiInfos = new ArrayList<MyWifiInfo>();
		}
		listAdapter = new WifiListAdapter(mContext, wifiInfos);
		listView.setAdapter(listAdapter);
		wifiSwitch = (Switch) ((MainActivity) mContext)
				.findViewById(R.id.wifiSwitch);

		wifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (wifiManager != null) {
					if (isChecked && !wifiManager.isWifiEnabled()) {
						wifiManager.setWifiEnabled(true);
						wifiSwitch.setEnabled(false);
						showWifiMessageView();
					} else if (!isChecked && wifiManager.isWifiEnabled()) {
						wifiManager.setWifiEnabled(false);
						wifiSwitch.setEnabled(false);

						wifiInfos = new ArrayList<MyWifiInfo>();
						listAdapter.setList(wifiInfos);
						listAdapter.notifyDataSetChanged();

						showWifiMessageView();
					}
				}
			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == wifiInfos.size()-1) {
					addNewNetwork();
					return;
				}

				ScanResult scanResult2 = wifiInfos.get(position)
						.getScanResult();
				// 是否已连接
				if (isConnected(scanResult2)) {
					WifiForgetDialog mStatusDialog = new WifiForgetDialog(
							mContext, R.style.PopDialog, scanResult2,
							mOnNetworkChangeListener);
					mStatusDialog.show();
					return;
				}

				if (isNoSecurity(scanResult2.capabilities)) {//没有密码保护
					mWifiAdmin.connectSpecificAP(scanResult2);
				} else {// 有密码保护
					WifiConnDialog mDialog = new WifiConnDialog(mContext,
							R.style.PopDialog, scanResult2,
							mOnNetworkChangeListener);
					mDialog.show();
				}
			}

			/** 判断是否有密码保护 */
			private boolean isNoSecurity(String cap) {
				String desc = "";
				if (cap.toUpperCase().contains("WPA2-EAP")) {
					desc = "802.1x";
				}
				if (cap.toUpperCase().contains("WPA-PSK")) {
					desc = "WPA";
				}
				if (cap.toUpperCase().contains("WPA2-PSK")) {
					desc = "WPA2";
				}
				if (cap.toUpperCase().contains("WPA-PSK")
						&& cap.toUpperCase().contains("WPA2-PSK")) {
					desc = "WPA/WPA2";
				}
				if (desc.equals("")) {
					return true;
				}
				return false;
			}

		});

	}

	private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

		@Override
		public void onNetWorkDisConnect() {
			getWifiList();
		}

		@Override
		public void onNetWorkConnect(String connectingSSID) {
			getWifiList();
			setWifiInfoToNull();
			mConnectingSSID = connectingSSID;
		}
	};

	/** 获取WiFi列表并刷新界面 */
	private void getWifiList() {
		List<ScanResult> results = wifiManager.getScanResults();
		Log.v(TAG, "results.size(): " + results.size());
		if (results != null && results.size() != 0) {
			packageToMyWifiList(results);
			if (wifiInfos == null) {
				wifiInfos = new ArrayList<MyWifiInfo>();
			}
			if (listAdapter != null) {
				listAdapter.setList(wifiInfos);
				listAdapter.notifyDataSetChanged();
			}
		} else {
			if (wifiManager.isWifiEnabled()) {
				wifiManager.startScan();
				mHandler.sendEmptyMessageDelayed(MSG_GET_WIFI_LIST, 1000);
			}
		}
	}

	private void packageToMyWifiList(List<ScanResult> results) {
		if (wifiInfos == null) {
			wifiInfos = new ArrayList<MyWifiInfo>();
		} else {
			wifiInfos.clear();
		}
		for (ScanResult scan : results) {
			MyWifiInfo info = new MyWifiInfo();
			info.setScanResult(scan);
			info.setWifiName(scan.SSID);
			info.setWifiAddress(scan.BSSID);

			String cap = scan.capabilities;
			String wifiSecurity = "";

			if (cap.toUpperCase().contains("WPA2-EAP")) {
				wifiSecurity = "802.1x";
			}
			if (cap.toUpperCase().contains("WPA-PSK")) {
				wifiSecurity = "WPA";
			}
			if (cap.toUpperCase().contains("WPA2-PSK")) {
				wifiSecurity = "WPA2";
			}
			if (cap.toUpperCase().contains("WPA-PSK")
					&& cap.toUpperCase().contains("WPA2-PSK")) {
				wifiSecurity = "WPA/WPA2";
			}

			if (TextUtils.isEmpty(wifiSecurity)) {
				wifiSecurity = mContext.getResources().getString(
						R.string.no_protect);
			} else {
				wifiSecurity = String.format(
						mContext.getResources().getString(R.string.protect_by),
						wifiSecurity);
			}
			if (mConnectingSSID != null) {
				if (scan.SSID.equals(mConnectingSSID)) {
					wifiSecurity = mContext.getResources().getString(
							R.string.is_conncting);
				}
			}

			if (isConnected(scan)) {
				wifiSecurity = mContext.getResources().getString(
						R.string.connected);
			}

			info.setWifiSecurity(wifiSecurity);

			// 网络信号强度
			int level = scan.level;
			int wifiSignal = 0;
			if (Math.abs(level) > 100) {
				wifiSignal = 0;
			} else if (Math.abs(level) > 80) {
				wifiSignal = R.drawable.wifi04;
			} else if (Math.abs(level) > 70) {
				wifiSignal = R.drawable.wifi04;
			} else if (Math.abs(level) > 60) {
				wifiSignal = R.drawable.wifi03;
			} else if (Math.abs(level) > 50) {
				wifiSignal = R.drawable.wifi02;
			} else {
				wifiSignal = R.drawable.wifi01;
			}
			info.setWifiSignal(wifiSignal);
			if (info.getWifiName() != null
					&& !info.getWifiName().trim().equals("")
					&& !hasSameSSID(info.getWifiName())) {
				wifiInfos.add(info);
			}
		}
		//添加网络
		MyWifiInfo info = new MyWifiInfo();
		info.setScanResult(null);
		info.setWifiName(mContext.getResources().getString(R.string.add_network));
		info.setWifiSecurity("");
		info.setWifiSignal(0);
		wifiInfos.add(info);
		 
		sortWifiList(wifiInfos);
	}

	private boolean hasSameSSID(String ssid) {
		for (MyWifiInfo info : wifiInfos) {
			if (info.getWifiName().equals(ssid)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasSameAddress(String address) {
		for (MyWifiInfo info : wifiInfos) {
			if (info.getWifiAddress().equals(address)) {
				return true;
			}
		}
		return false;
	}

	private boolean isConnected(ScanResult scanResult) {
		if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState() == NetworkInfo.State.CONNECTED) {
			if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID())
					&& !TextUtils.isEmpty(scanResult.SSID)
					&& wifiInfo.getSSID().equals(scanResult.SSID)) {
				return true;
			} else if (wifiInfo != null
					&& !TextUtils.isEmpty(wifiInfo.getBSSID())
					&& !TextUtils.isEmpty(scanResult.BSSID)
					&& wifiInfo.getBSSID().equals(scanResult.BSSID)) {
				return true;
			}
		}
		return false;
	}

	private void sortWifiList(List<MyWifiInfo> list) {
		for (int i = 1; i < list.size(); i++) {
			for (int j = list.size() - i - 1; j >= 0; j--) {
				int lastIndex = j - 1;
				if (lastIndex >= 0) {
					if (list.get(j).getWifiSignal() < list.get(lastIndex)
							.getWifiSignal()) {
						MyWifiInfo tmpInfo = list.get(lastIndex);
						list.set(lastIndex, list.get(j));
						list.set(j, tmpInfo);
					}
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i)
					.getWifiSecurity()
					.equals(mContext.getResources().getString(
							R.string.connected))
					|| list.get(i)
							.getWifiSecurity()
							.equals(mContext.getResources().getString(
									R.string.is_conncting))) {
				if (i == 0)
					return;
				MyWifiInfo firstWifiInfo = list.get(0);
				list.set(0, list.get(i));
				list.set(i, firstWifiInfo);
				for (int ii = 1; ii < list.size(); ii++) {
					for (int j = list.size() - ii-1; j >= 2; j--) {
						int lastIndex = j - 1;
						if (lastIndex >= 0) {
							if (list.get(j).getWifiSignal() < list.get(lastIndex)
									.getWifiSignal()) {
								MyWifiInfo tmpInfo = list.get(lastIndex);
								list.set(lastIndex, list.get(j));
								list.set(j, tmpInfo);
							}
						}
					}
				}
				
				return;
			}
		}
	}

	private void setWifiSwitchState() {
		if (wifiManager.isWifiEnabled() && !wifiSwitch.isChecked()) {
			wifiSwitch.setChecked(true);
		} else if (!wifiManager.isWifiEnabled() && wifiSwitch.isChecked()) {
			wifiSwitch.setChecked(false);
		}
		wifiSwitch.setEnabled(true);
	}

	@Override
	public View getView() {
		return rootView;
	}

	// 监听WiFi改变
	private boolean isRegist = false;

	private void registWifiReceiver() {
		if (!isRegist) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);// 信号强度改变
			intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);// 状态改变（多次调用）
			intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);// WiFi是否可用
			intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 它应该咨询看到什么样的连接事件发生。
			intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			mContext.registerReceiver(wifiChangeReceiver, intentFilter);
			isRegist = true;
		}
	}

	public void unRegistReceiver() {
		if (isRegist) {
			isRegist = false;
			mContext.unregisterReceiver(wifiChangeReceiver);
		}
	}

	BroadcastReceiver wifiChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.v(TAG, "action: " + action);
			if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				int wifistate = intent.getIntExtra(
						WifiManager.EXTRA_WIFI_STATE,
						WifiManager.WIFI_STATE_DISABLED);
				if (wifistate == WifiManager.WIFI_STATE_DISABLED) {// WiFi不可用
					mHandler.sendEmptyMessage(MSG_WIFI_DISABLE);
				} else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {// WiFi可用
					mHandler.removeMessages(MSG_WIFI_ENABLE);
					mHandler.sendEmptyMessageDelayed(MSG_WIFI_ENABLE, 100);
				}
				setWifiEnable();
			} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				NetworkInfo info = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// WiFi未连接
					mHandler.sendEmptyMessage(MSG_GET_WIFI_LIST);
				} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {// WiFi已连接
					mConnectingSSID = null;
					mHandler.sendEmptyMessage(MSG_CONNECTED);
					wifiInfo = wifiManager.getConnectionInfo();
				}
			} else {
				mHandler.removeMessages(MSG_GET_WIFI_LIST);
				// mHandler.sendEmptyMessageDelayed(MSG_GET_WIFI_LIST, 500);
				mHandler.sendEmptyMessage(MSG_GET_WIFI_LIST);
			}
		}
	};

	private final int MSG_GET_WIFI_LIST = 1;
	private final int MSG_REFERSH_WIFI_LIST = 2;
	private final int MSG_CONNECTED = 3;
	private final int MSG_WIFI_ENABLE = 4;
	private final int MSG_WIFI_DISABLE = 5;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_WIFI_LIST:
			case MSG_REFERSH_WIFI_LIST:
			case MSG_CONNECTED:
			case MSG_WIFI_ENABLE:
				getWifiList();
				break;
			case MSG_WIFI_DISABLE:
				wifiInfos = new ArrayList<MyWifiInfo>();
				listAdapter.setList(wifiInfos);
				listAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	public void addNewNetwork() {
		WifiCreateDialog wifiCreateDialog = new WifiCreateDialog(mWifiAdmin,
				mContext, mOnNetworkChangeListener);
		wifiCreateDialog.show();
	}

	public void setWifiInfoToNull() {
		wifiInfo = null;
	}

	private void showWifiMessageView() {
		switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			noDataSpaceTextView.setText(mContext.getResources().getString(R.string.wifi_disable));
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			noDataSpaceTextView.setText(mContext.getResources().getString(R.string.wifi_disabling));
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			noDataSpaceTextView.setText(mContext.getResources().getString(R.string.wifi_enabling));
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			noDataSpaceTextView.setText("");
			break;
		default:
			noDataSpaceTextView.setText("");
			break;
		}
	}

}
