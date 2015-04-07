package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @ClassName:PhoneNumberRegisterActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class PhoneNumberVerifyRegisterActivity extends BaseActivity implements OnClickListener {

    EditText mEtPhoneNumberVerify;
    TextView mTvReloadVerify;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_register_phonenumber_verify);

	init();

    }

    private void init() {
	TextView tvTopTitle = getViewById(R.id.tv_top_title);
	TextView tvTopAction = getViewById(R.id.tv_top_action);
	tvTopTitle.setText("验证手机号");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setText("");

	// 手机号输入
	mEtPhoneNumberVerify = getViewById(R.id.et_phonenumber_verify);

	CheckBox cbAgreeLicense = getViewById(R.id.cb_agree_license);
	cbAgreeLicense.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		findViewById(R.id.tv_register_next_step).setEnabled(isChecked);
	    }
	});

	// 重新获取验证码
	mTvReloadVerify = getViewById(R.id.tv_reload_verify);

	findViewById(R.id.tv_register_previsous_step).setOnClickListener(this);
	findViewById(R.id.tv_register_next_step).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_register_previsous_step:
	    finish();
	    break;
	case R.id.tv_register_next_step:

	    break;
	}

    }

}
