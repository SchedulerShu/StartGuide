package com.takee.setupwizard.bean;

import android.net.wifi.ScanResult;

public class MyWifiInfo {
	private String wifiName;
	private String wifiAddress;
	private String wifiSecurity;
	private int wifiSignal;
	private ScanResult scanResult;

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getWifiAddress() {
		return wifiAddress;
	}

	public void setWifiAddress(String wifiAddress) {
		this.wifiAddress = wifiAddress;
	}

	public String getWifiSecurity() {
		return wifiSecurity;
	}

	public void setWifiSecurity(String wifiSecurity) {
		this.wifiSecurity = wifiSecurity;
	}

	public int getWifiSignal() {
		return wifiSignal;
	}

	public void setWifiSignal(int wifiSignal) {
		this.wifiSignal = wifiSignal;
	}

	public ScanResult getScanResult() {
		return scanResult;
	}

	public void setScanResult(ScanResult scanResult) {
		this.scanResult = scanResult;
	}

}
