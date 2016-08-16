package com.takee.setupwizard.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.takee.setupwizard.base.BasePage;

public class ViewPagerAdapter extends PagerAdapter {

	private List<BasePage> mListView;

	public ViewPagerAdapter(List<BasePage> listView) {
		super();
		this.mListView = listView;
	}

	// 销毁position位置的界面
	public void destroyItem(View arg0, int arg1, Object arg2) {
		BasePage basePage = mListView.get(arg1);
		((ViewGroup) arg0).removeView(basePage.getView());
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	// //获取当前窗体界面数
	public int getCount() {
		return mListView.size();
	}

	// 初始化position位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		BasePage basePage = mListView.get(arg1);
		((ViewGroup) arg0).addView(basePage.getView(), 0);
		return basePage.getView();
	}

	// 判断是否由对象生成界面
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

}
