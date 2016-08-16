package com.takee.setupwizard.page;

import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.takee.setupwizard.MainActivity;
import com.takee.setupwizard.R;
import com.takee.setupwizard.base.BasePage;

public class DatePage extends BasePage {
	private View view;
	private Context context;

	private TextView txtDate, txtTime;
	private LinearLayout dateContainer, timeContainer;
	private CheckBox checkBox;

	private static final int DATE_PICKER_ID = 3;
	private static final int TIME_PICKER_ID = 4;

	public DatePage(MainActivity mainActivity, View page1_data) {
		context = mainActivity;
		view = page1_data;
		initView();
	}

	private void initView() {
		txtDate = (TextView) view.findViewById(R.id.txtDate);
		txtTime = (TextView) view.findViewById(R.id.txtTime);

		dateContainer = (LinearLayout) view.findViewById(R.id.dateContainer);
		timeContainer = (LinearLayout) view.findViewById(R.id.timeContainer);

		checkBox = (CheckBox) view.findViewById(R.id.autoUserNetWorkDate);

		dateContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) context).showDialog(DATE_PICKER_ID);
			}
		});

		timeContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) context).showDialog(TIME_PICKER_ID);
			}
		});

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					dateContainer.setEnabled(false);
					timeContainer.setEnabled(false);
				} else {
					dateContainer.setEnabled(true);
					timeContainer.setEnabled(true);
				}
			}
		});
		setDefultDateAndTime();
	}
	
	private void setDefultDateAndTime() {
		Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
		setDate(dateAndTime.get(Calendar.YEAR),
				(dateAndTime.get(Calendar.MONTH) + 1),
				dateAndTime.get(Calendar.DAY_OF_MONTH));
		setTime(dateAndTime.get(Calendar.HOUR_OF_DAY),
				dateAndTime.get(Calendar.MINUTE));
	}

	public void setDate(int year, int monthOfYear, int dayOfMonth) {
		txtDate.setText(year + "-" + formatDateOrTime(monthOfYear) + "-" + formatDateOrTime(dayOfMonth));
	}

	public void setTime(int hourOfDay, int minute) {
		txtTime.setText(formatDateOrTime(hourOfDay) + ":" + formatDateOrTime(minute));
	}
	
	private String formatDateOrTime(int dateOrTime) {
		String tmp=dateOrTime+"";
		if(dateOrTime<10){
			tmp="0"+dateOrTime;
		}
		return tmp;
	}
	

	@Override
	public void onResume() {

	}

	@Override
	public View getView() {
		return view;
	}

}
