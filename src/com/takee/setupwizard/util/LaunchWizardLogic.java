package com.takee.setupwizard.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.util.Log;

public class LaunchWizardLogic {
	private final String TAG = "LaunchWizardLogic";

	private final String START_WIZARD = "startWizard";
	private final int SETUP_STATE_DEFULT = 0;
	private final int SETUP_STATE_FINISH = 3;// 已设置完成
	private final int SETUP_STATE_IS_RUNNING = 2;// 正在设置
	private final int SETUP_STATE_NEED_TO_RESTART = 1;// 需要重新设置

	 //add by chenminjiang
	 private void finishSetupWizard(Context context) {
	        // Add a persistent setting to allow other apps to know the device has been provisioned.
	        Settings.Global.putInt(context.getContentResolver(), Settings.Global.DEVICE_PROVISIONED , 1);
	        
	        //Settings.Secure.putInt(context.getContentResolver(), Settings.Secure., 1);

	        // remove this activity from the package manager.
	        PackageManager pm = context.getPackageManager();
	        ComponentName name = new ComponentName("com.takee.setupwizard", "com.takee.setupwizard.MainActivity");
	        
	        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
	    }
	
	private void setWizardState(Context context, int state) {
		if (!isFinish(context)) {
			Settings.System.putInt(context.getContentResolver(), START_WIZARD,
					state);
		}
	}

	private int getWizardState(Context context) {
		return Settings.System.getInt(context.getContentResolver(),
				START_WIZARD, SETUP_STATE_DEFULT);
	}

	public boolean isFinish(Context context) {
		return getWizardState(context) == SETUP_STATE_FINISH;
	}

	public void maybeNeedToRestartWizard(Context context) {
		setWizardState(context, SETUP_STATE_NEED_TO_RESTART);
	}

	public void setupWizardIsRunning(Context context) {
		setWizardState(context, SETUP_STATE_IS_RUNNING);
	}

	public void setupWizardFinish(Context context) {
		
		//add by chenminjiang
		finishSetupWizard(context);
		
		if (!isFinish(context)) {
			setWizardState(context, SETUP_STATE_FINISH);
			String packageName = "com.android.launcher3";
			String activityName = "com.android.launcher3.Launcher";
			try {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				ComponentName cn = new ComponentName(packageName, activityName);
				intent.setComponent(cn);
				context.startActivity(intent);
				Log.v(TAG, "ReadyPage startActivity success1");
			} catch (Exception e) {
				e.printStackTrace();
				Log.v(TAG, "ReadyPage startActivity failed1: " + e.toString());
				try {
					PackageInfo pi = context.getPackageManager()
							.getPackageInfo(packageName, 0);
					Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
					resolveIntent.setPackage(pi.packageName);
					PackageManager pManager = context.getPackageManager();
					List<ResolveInfo> apps = pManager.queryIntentActivities(
							resolveIntent, 0);
					ResolveInfo ri = apps.iterator().next();
					if (ri != null) {
						packageName = ri.activityInfo.packageName;
						String className = ri.activityInfo.name;
						Intent intent = new Intent(Intent.ACTION_MAIN);
						ComponentName cn = new ComponentName(packageName,
								className);
						intent.setComponent(cn);
						context.startActivity(intent);
					}
					Log.v(TAG, "ReadyPage startActivity success2");
				} catch (Exception e2) {
					e2.printStackTrace();
					Log.v(TAG,
							"ReadyPage startActivity failed2: " + e2.toString());
				}
			}
		}

	}
}
