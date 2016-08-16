package com.takee.setupwizard.base;

import android.view.View;

public abstract class BasePage implements PageControll{
	
	@Override
	public abstract void onResume();

	@Override
	public abstract View getView();
	
}
