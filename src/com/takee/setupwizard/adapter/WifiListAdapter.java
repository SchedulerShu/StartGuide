package com.takee.setupwizard.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.takee.setupwizard.R;
import com.takee.setupwizard.bean.MyWifiInfo;

public class WifiListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<MyWifiInfo> mWifiInfos;
	private Context mContext;

	public WifiListAdapter(Context context, List<MyWifiInfo> wifiInfos) {
		this.mWifiInfos = wifiInfos;
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setList(List<MyWifiInfo> wifiInfos) {
		this.mWifiInfos = wifiInfos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mWifiInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private class ViewHold {
		TextView wifiName;
		TextView wifiSecurity;
		ImageView wifiSignal;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold = null;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = mInflater.inflate(R.layout.wifi_item, null);
			hold.wifiName = (TextView) convertView.findViewById(R.id.wifiName);
			hold.wifiSecurity = (TextView) convertView
					.findViewById(R.id.wifiSecurity);
			hold.wifiSignal = (ImageView) convertView
					.findViewById(R.id.wifiSignal);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}

		MyWifiInfo wifiInfo = mWifiInfos.get(position);
		hold.wifiName.setText(wifiInfo.getWifiName());
		hold.wifiSecurity.setText(wifiInfo.getWifiSecurity());
		hold.wifiSignal.setBackgroundResource(wifiInfo.getWifiSignal());
		return convertView;
	}
}