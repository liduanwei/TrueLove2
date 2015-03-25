package me.himi.love.ui;

import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.ShareSMSConstants;
import me.himi.love.util.StringUtils;
import me.himi.love.util.ToastFactory;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 修改密码
 * @ClassName:ModifyPasswordActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ModifyPasswordActivity extends BaseActivity implements OnClickListener {

    PullToRefreshWebView mWebView;

    EditText etPassword, etPassword2;

    private String phoneNumber;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_modify_password);

	phoneNumber = getIntent().getStringExtra("phone");
	init();

    }

    EditText etSMSVerifyCode;
    private TextView tvTopTitle, tvTopAction;

    private void init() {

	tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopTitle.setText("重置密码");
	tvTopTitle.setOnClickListener(this);

	tvTopAction.setVisibility(View.GONE);
	//	tvTopAction.setText("提交");
	//	tvTopAction.setOnClickListener(this);

	etPassword = (EditText) findViewById(R.id.et_modify_password);
	etPassword2 = (EditText) findViewById(R.id.et_modify_password2);

	findViewById(R.id.btn_submit).setOnClickListener(this);

	// 输入短信验证码
	etSMSVerifyCode = (EditText) findViewById(R.id.et_resetpwd_verifycode);

	// 发送手机验证短信
	findViewById(R.id.tv_resetpwd_send_verifycode).setOnClickListener(this);
	// 提交验证
	findViewById(R.id.tv_resetpwd_submit_verifycode).setOnClickListener(this);

	hideModify();

	// 初始化SMS
	initSMSSDK();

    }

    private void initSMSSDK() {
	SMSSDK.initSDK(this, ShareSMSConstants.SHARESDK_SMS_APPKEY, ShareSMSConstants.SHARESDK_SMS_APPSECRET);

	final Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
		int event = msg.arg1;
		int result = msg.arg2;
		Object data = msg.obj;
		Log.e("event", "event=" + event);
		if (result == SMSSDK.RESULT_COMPLETE) {
		    //短信注册成功后，返回MainActivity,然后提示新好友
		    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
			Toast.makeText(getApplicationContext(), "验证成功", Toast.LENGTH_SHORT).show();
			//显示修改密码

			// 显示修改输入框
			showModify();

		    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
			Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
			//			textView2.setText("验证码已经发送");
			etSMSVerifyCode.requestFocus();//定位输入验证码
			etSMSVerifyCode.setText(""); // 清空内容
		    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
			Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
			//			countryTextView.setText(data.toString());
		    }
		} else { // 验证码错误
		    ((Throwable) data).printStackTrace();
		    showToast("验证码错误");
		}
	    }
	};

	EventHandler eh = new EventHandler() {

	    @Override
	    public void afterEvent(int event, int result, Object data) {

		Message msg = new Message();
		msg.arg1 = event;
		msg.arg2 = result;
		msg.obj = data;
		handler.sendMessage(msg);
	    }

	};
	SMSSDK.registerEventHandler(eh);
    }

    private void submit() {
	if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
	    etPassword.requestFocus();
	    ToastFactory.getToast(ModifyPasswordActivity.this, "请设置密码").show();
	    return;
	}

	if (etPassword.getText().toString().trim().length() < 6) {
	    etPassword.requestFocus();
	    ToastFactory.getToast(ModifyPasswordActivity.this, "密码长度6~10位").show();
	    return;
	}

	if (TextUtils.isEmpty(etPassword2.getText().toString().trim())) {
	    etPassword2.requestFocus();
	    ToastFactory.getToast(ModifyPasswordActivity.this, "请输入确认密码").show();
	    return;
	}

	if (!etPassword.getText().toString().equals(etPassword2.getText().toString().trim())) {
	    etPassword2.requestFocus();
	    ToastFactory.getToast(ModifyPasswordActivity.this, "两次密码不一致,请重新输入").show();
	    return;
	}

	if (!ActivityUtil.hasNetwork(this)) {
	    ToastFactory.getToast(ModifyPasswordActivity.this, "网络不可用,请检查网络").show();
	    return;
	}

	// 提交 修改密码
	RequestParams params = new RequestParams();
	params.put("password", etPassword.getText().toString().trim());
	params.put("user_id", MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");

	HttpUtil.post(Constants.URL_RESET_PWD, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		showToast("连接超时");
	    }
	});
    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.tv_top_title:
	    finish();
	    break;
	case R.id.tv_top_action:
	case R.id.btn_submit:
	    submit();
	    break;
	case R.id.tv_resetpwd_send_verifycode:
	    sendVerifyCodeMsg();
	    break;
	case R.id.tv_resetpwd_submit_verifycode:
	    submitVerifyCode();
	    break;
	}
    }

    private String currentPhoneNumber;

    // 发送验证短信
    private void sendVerifyCodeMsg() {
	this.currentPhoneNumber = phoneNumber;
	SMSSDK.getVerificationCode("86", currentPhoneNumber);
    }

    private void submitVerifyCode() {
	//	isVerified = true;
	String code = etSMSVerifyCode.getText().toString().trim();
	if (code.length() == 0) {
	    showToast("请输入验证码");
	    etSMSVerifyCode.requestFocus();
	    return;
	}
	SMSSDK.submitVerificationCode("86", phoneNumber, code);
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	SMSSDK.unregisterAllEventHandler();
	super.onDestroy();
    }

    private void showModify() {
	findViewById(R.id.rl_modify_top).setEnabled(true);
	findViewById(R.id.rl_modify_code).setEnabled(true);
	findViewById(R.id.rl_modify_submit).setEnabled(true);
    }

    private void hideModify() {
	findViewById(R.id.rl_modify_top).setEnabled(false);
	findViewById(R.id.rl_modify_code).setEnabled(false);
	findViewById(R.id.rl_modify_submit).setEnabled(false);
    }
}
