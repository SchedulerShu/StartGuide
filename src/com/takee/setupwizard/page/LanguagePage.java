package com.takee.setupwizard.page;

import java.lang.reflect.Method;
import java.util.Locale;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.takee.setupwizard.MainActivity;
import com.takee.setupwizard.R;
import com.takee.setupwizard.adapter.SpinnerAdapter;
import com.takee.setupwizard.base.BasePage;

public class LanguagePage extends BasePage {
	private View view;
	private Context mContext;

	private Spinner languageSpinner;
	private SpinnerAdapter spinnerAdapter;

	private Configuration config;

	public LanguagePage(MainActivity mainActivity, View page1_data) {
		mContext = mainActivity;
		view = page1_data;
		initView();
	}

	private void initView() {
		if (languageSpinner == null) {
			languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);
			
			spinnerAdapter=new SpinnerAdapter(mContext, new String[] {
							mContext.getResources().getString(
									R.string.language_chinese),
							mContext.getResources().getString(
									R.string.language_fanti),
							mContext.getResources().getString(
									R.string.language_en) });
			languageSpinner.setAdapter(spinnerAdapter);
			config = mContext.getResources().getConfiguration();
			if (config.locale == Locale.CHINA) {
				languageSpinner.setSelection(0, true);
			} else if (config.locale == Locale.TAIWAN) {
				languageSpinner.setSelection(1, true);
			} else if (config.locale == Locale.ENGLISH) {
				languageSpinner.setSelection(2, true);
			}

			languageSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							Locale locale = config.locale;
							switch (position) {
							case 0:
								config.locale = Locale.CHINA;
								break;
							case 1:
								config.locale = Locale.TAIWAN;
								break;
							case 2:
								config.locale = Locale.ENGLISH;
								break;
							default:
								break;
							}

							DisplayMetrics dm = mContext.getResources()
									.getDisplayMetrics();
							if (config.locale != locale) {
								mContext.getResources().updateConfiguration(
										config, dm);
								updateLanguage(config.locale);
								((MainActivity) mContext).updateView();
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {

						}
					});
		}
	}

	@Override
	public void onResume() {

	}

	@Override
	public View getView() {
		return view;
	}

	private void updateLanguage(Locale locale) {
		try {
			Object objIActMag;
			Class clzIActMag = Class.forName("android.app.IActivityManager");
			Class clzActMagNative = Class
					.forName("android.app.ActivityManagerNative");
			Method mtdActMagNative$getDefault = clzActMagNative
					.getDeclaredMethod("getDefault");
			objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
			Method mtdIActMag$getConfiguration = clzIActMag
					.getDeclaredMethod("getConfiguration");
			Configuration config = (Configuration) mtdIActMag$getConfiguration
					.invoke(objIActMag);// need system permission:
										// android.permission.CHANGE_CONFIGURATION
			config.locale = locale;
			Class[] clzParams = { Configuration.class };
			Method mtdIActMag$updateConfiguration = clzIActMag
					.getDeclaredMethod("updateConfiguration", clzParams);
			mtdIActMag$updateConfiguration.invoke(objIActMag, config);

			BackupManager.dataChanged("com.android.providers.settings");
			Class clzBackupManager = Class
					.forName("android.app.backup.BackupManager");
			Class[] clzString = { String.class };
			Method mtdDataChanged = clzBackupManager.getDeclaredMethod(
					"dataChanged", clzString);
			mtdDataChanged.invoke(clzBackupManager,
					"com.android.providers.settings");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
