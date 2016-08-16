package com.takee.setupwizard.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.takee.setupwizard.R;
import com.takee.setupwizard.adapter.SpinnerAdapter;
import com.takee.setupwizard.wifi.OnNetworkChangeListener;
import com.takee.setupwizard.wifi.WifiAdmin;
import com.takee.setupwizard.wifi.WifiAdmin.WifiCipherType;

public class WifiCreateDialog extends Dialog {

	private Context mContext;
	private Spinner spinnerSecurity;
	private SpinnerAdapter spinnerSecurityAdapter;

	private EditText edtPassword, edtSsid;
	private CheckBox cbxShowPass;

	private TextView txtBtnCancel, txtBtnSave;
	private WifiAdmin mWifiAdmin;
	private OnNetworkChangeListener mOnNetworkChangeListener;

	public WifiCreateDialog(WifiAdmin wifiAdmin, Context context,
			OnNetworkChangeListener onNetworkChangeListener) {
		super(context, R.style.PopDialog);
		mContext = context;
		mWifiAdmin = wifiAdmin;
		mOnNetworkChangeListener = onNetworkChangeListener;
	}

	public WifiCreateDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_wifi_create);
		setCanceledOnTouchOutside(true);
		initView();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Point size = new Point();
		wm.getDefaultDisplay().getSize(size);

		super.show();
		getWindow().setLayout((int) (size.x * 9 / 10),
				LayoutParams.WRAP_CONTENT);
	}

	private void initView() {

		edtPassword = (EditText) findViewById(R.id.edt_password);
		edtSsid = (EditText) findViewById(R.id.edt_ssid);
		cbxShowPass = (CheckBox) findViewById(R.id.cbx_show_pass);
		txtBtnCancel = (TextView) findViewById(R.id.txt_btn_cancel);
		txtBtnSave = (TextView) findViewById(R.id.txt_btn_save);

		if (edtSsid.getText().toString().trim().length() != 0
				&& edtPassword.getText().toString().trim().length() != 0) {
			txtBtnSave.setEnabled(true);
		} else {
			txtBtnSave.setEnabled(false);
		}
		cbxShowPass.setEnabled(true);

		edtSsid.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				setSaveButtonEnable();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		edtPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				setSaveButtonEnable();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		cbxShowPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// 文本正常显示
					edtPassword
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					Editable etable = edtPassword.getText();
					Selection.setSelection(etable, etable.length());

				} else {
					// 文本以密码形式显示
					edtPassword.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					// 下面两行代码实现: 输入框光标一直在输入文本后面
					Editable etable = edtPassword.getText();
					Selection.setSelection(etable, etable.length());

				}
			}
		});

		spinnerSecurity = (Spinner) findViewById(R.id.spinner_security);

		spinnerSecurityAdapter = new SpinnerAdapter(mContext, new String[] {
				mContext.getResources().getString(R.string.security_none),
				mContext.getResources().getString(R.string.security_wep),
				mContext.getResources().getString(
						R.string.security_wpa_wpa2_psk),
				mContext.getResources().getString(R.string.security_eap),
				mContext.getResources().getString(R.string.security_psk),
				mContext.getResources().getString(R.string.security_wapi) });
		spinnerSecurity.setAdapter(spinnerSecurityAdapter);
		spinnerSecurity.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		txtBtnSave
				.setOnClickListener(new android.widget.CompoundButton.OnClickListener() {
					@Override
					public void onClick(View v) {
						String ssid = edtSsid.getText().toString().trim();
						WifiCipherType securityTpye = WifiCipherType.WIFICIPHER_NOPASS;
						switch (spinnerSecurity.getSelectedItemPosition()) {
						case 0:
							securityTpye = WifiCipherType.WIFICIPHER_NOPASS;
							break;
						case 1:
							securityTpye = WifiCipherType.WIFICIPHER_WEP;
							break;
						case 2:
							securityTpye = WifiCipherType.WIFICIPHER_WPA;
							break;
						case 3:
						case 4:
						case 5:
							securityTpye = WifiCipherType.WIFICIPHER_WPA;
							break;
						default:
							securityTpye = WifiCipherType.WIFICIPHER_NOPASS;
							break;
						}
						String pwd = edtPassword.getText().toString().trim();

						mOnNetworkChangeListener.onNetWorkConnect(ssid);
						mWifiAdmin.connect(ssid, pwd, securityTpye);
						WifiCreateDialog.this.dismiss();
					}
				});
		txtBtnCancel
				.setOnClickListener(new android.widget.CompoundButton.OnClickListener() {

					@Override
					public void onClick(View v) {
						WifiCreateDialog.this.dismiss();
						mOnNetworkChangeListener.onNetWorkDisConnect();
					}
				});
	}

	private void setSaveButtonEnable() {
		if (!edtSsid.getText().toString().trim().equals("")
				&& !edtPassword.getText().toString().trim().equals("")) {
			txtBtnSave.setEnabled(true);
		} else {
			txtBtnSave.setEnabled(false);
		}
	}

}
