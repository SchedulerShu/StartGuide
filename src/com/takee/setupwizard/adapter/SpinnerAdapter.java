package com.takee.setupwizard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.takee.setupwizard.R;

public class SpinnerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private String[] mValues;

	public SpinnerAdapter(Context context, String[] values) {
		this.mValues = values;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mValues.length;
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
		TextView text;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold = null;
		if (convertView == null) {
			hold = new ViewHold();
			convertView = mInflater.inflate(R.layout.spinner_item, null);
			hold.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(hold);
		} else {
			hold = (ViewHold) convertView.getTag();
		}
		hold.text.setText(mValues[position]);
		return convertView;
	}
}