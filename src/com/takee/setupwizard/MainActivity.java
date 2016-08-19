package com.takee.setupwizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.takee.setupwizard.adapter.ViewPagerAdapter;
import com.takee.setupwizard.base.BasePage;
import com.takee.setupwizard.page.DatePage;
import com.takee.setupwizard.page.LanguagePage;
import com.takee.setupwizard.page.ReadyPage;
import com.takee.setupwizard.page.WifiPage;
import com.takee.setupwizard.util.LaunchWizardLogic;
import com.takee.setupwizard.util.SystemDateTime;

public class MainActivity extends Activity {

	private ViewPager mViewPager;
	private List<BasePage> viewList = null;
	private LinearLayout circleLayout;
	private int totalpage = 4;

	private final int PAGE0_LANGUAGE = 0, PAGE1_DATA = 1, PAGE2_WIFI = 2,
			PAGE3_READY = 3;
	private BasePage languagePage, datePage, wifiPage, readyPage;
	private TextView title;

	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	public static LaunchWizardLogic mLaunchWizardLogic = new LaunchWizardLogic();

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,
				FLAG_HOMEKEY_DISPATCHED);
		setContentView(R.layout.activity_main);
		initViewPage();
		initCircleIndicator();
		setOnViewPageScorll();
	}

	public void updateView() {
		initViewPage();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			viewList.get(mViewPager.getCurrentItem()).onResume();
			mLaunchWizardLogic.setupWizardIsRunning(this);
		} else {
			mLaunchWizardLogic.maybeNeedToRestartWizard(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		mLaunchWizardLogic.setupWizardIsRunning(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLaunchWizardLogic.maybeNeedToRestartWizard(this);
	}

	/** 初始化滑动视图的集合 */
	private void initViewPage() {
		title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getString(R.string.page2_title));

		viewList = new ArrayList<BasePage>();
		LayoutInflater inflater = getLayoutInflater().from(this);
		
		//delete by chenminjiang
		//View page0_language = inflater.inflate(R.layout.page0_language, null);
		//View page1_data = inflater.inflate(R.layout.page1_date, null);
		View page2_wifi = inflater.inflate(R.layout.page2_wifi, null);
		View page5_ready = inflater.inflate(R.layout.page3_ready, null);

		//delete by chenminjiang
		//languagePage = new LanguagePage(this, page0_language);
		//datePage = new DatePage(this, page1_data);
		wifiPage = new WifiPage(this, page2_wifi);
		readyPage = new ReadyPage(this, page5_ready);

		//delete by chenminjiang
		//viewList.add(languagePage);
		//viewList.add(datePage);
		viewList.add(wifiPage);
		viewList.add(readyPage);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(new ViewPagerAdapter(viewList));
		totalpage = viewList.size();
		mViewPager.setCurrentItem(0);
	}

	// 日期修改-start--------------------------------------------

	private static final int DATE_PICKER_ID = 3;
	private static final int TIME_PICKER_ID = 4;

	private Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:
			return new DatePickerDialog(this, d,
					dateAndTime.get(Calendar.YEAR),
					dateAndTime.get(Calendar.MONTH),
					dateAndTime.get(Calendar.DAY_OF_MONTH));
		case TIME_PICKER_ID:
			return new TimePickerDialog(MainActivity.this, t,
					dateAndTime.get(Calendar.HOUR_OF_DAY),
					dateAndTime.get(Calendar.MINUTE), true);
		default:
			break;
		}
		return null;
	}

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			((DatePage) datePage).setDate(year, (monthOfYear + 1), dayOfMonth);
			try {
				SystemDateTime.setDate(year, monthOfYear, dayOfMonth);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	};

	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateAndTime.set(Calendar.MINUTE, minute);

			((DatePage) datePage).setTime(hourOfDay, minute);
			try {
				SystemDateTime.setTime(hourOfDay, minute);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	// 日期修改-end--------------------------------------------

	// --底部的圆点-start---------------------------------------------------------------------------------
	private void initCircleIndicator() {
		circleLayout = (LinearLayout) findViewById(R.id.circle);
		circleLayout.removeAllViews();

		for (int i = 0; i < totalpage; i++) {
			LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
			TextView textView = new TextView(this);
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			if (i == mViewPager.getCurrentItem()) {
				textView.setBackgroundResource(R.drawable.round_dark_deep);
			} else {
				textView.setBackgroundResource(R.drawable.round_dark);
			}
			textView.setLayoutParams(layoutParams);
			circleLayout.addView(textView);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	private void setOnViewPageScorll() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				setCircleBackgroudColor(arg0);
				Switch switchButton = (Switch) findViewById(R.id.wifiSwitch);

				if (arg0 == PAGE2_WIFI) {
					switchButton.setVisibility(View.VISIBLE);
				} else if (switchButton.getVisibility() == View.VISIBLE) {
					switchButton.setVisibility(View.GONE);
				}

				switch (arg0) {
				case PAGE0_LANGUAGE:
					title.setText(getResources()
							.getString(R.string.page2_title));
					break;
				case PAGE1_DATA:
					title.setText(getResources()
							.getString(R.string.page5_title));
					break;
				case PAGE2_WIFI:
					title.setText(getResources()
							.getString(R.string.page2_title));
					break;
				case PAGE3_READY:
					title.setText(getResources()
							.getString(R.string.page5_title));
					break;
				default:
					break;
				}
				viewList.get(arg0).onResume();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void setCircleBackgroudColor(int index) {
		for (int i = 0; i < circleLayout.getChildCount(); i++) {
			if (i == index) {
				((TextView) circleLayout.getChildAt(i))
						.setBackgroundResource(R.drawable.round_dark_deep);
			} else {
				((TextView) circleLayout.getChildAt(i))
						.setBackgroundResource(R.drawable.round_dark);
			}
		}
	}

	// --底部的圆点-end---------------------------------------------------------------------------------

	private static final int ADD_NETWORK_ITEM_ID = 1;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		if (mViewPager.getCurrentItem() == PAGE2_WIFI) {
			menu.add(1, ADD_NETWORK_ITEM_ID, 1, R.string.add_network);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == ADD_NETWORK_ITEM_ID) {
			((WifiPage) wifiPage).addNewNetwork();
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		if(mLaunchWizardLogic.isFinish(this)){
			super.onBackPressed();
		}
	}
}
