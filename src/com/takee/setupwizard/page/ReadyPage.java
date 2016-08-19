package com.takee.setupwizard.page;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.takee.setupwizard.MainActivity;
import com.takee.setupwizard.R;
import com.takee.setupwizard.base.BasePage;

public class ReadyPage extends BasePage {
	
	private final String TAG = "ReadyPage";
	private View mView;
	private Context mContext;

	public ReadyPage(MainActivity mainActivity, View page5_ready) {
		mContext = mainActivity;
		mView = page5_ready;

		Log.v(TAG, "ReadyPage enter");
		((Button) mView.findViewById(R.id.done))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mContext instanceof MainActivity) {
							((MainActivity) mContext).mLaunchWizardLogic
									.setupWizardFinish(mContext);
							((MainActivity) mContext).finish();
						}
					}
				});
	}

	@Override
	public void onResume() {
		Log.v(TAG, "onResume enter");
	}

	@Override
	public View getView() {
		return mView;
	}

}
