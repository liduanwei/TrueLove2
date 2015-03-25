package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.StringUtils;
import me.himi.love.util.ToastFactory;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.smssdk.SMSSDK;

import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ResetPasswordActivity extends BaseActivity implements OnClickListener {

    PullToRefreshWebView mWebView;

    EditText etUsername, etVerifyCode;

    ImageView ivVerifyCode;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_reset_password);

	init();

    }

    private TextView tvTopTitle, tvTopAction;

    private void init() {

	tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopTitle.setText("重置密码");
	tvTopTitle.setOnClickListener(this);
	tvTopAction.setText("提交");
	tvTopAction.setOnClickListener(this);

	etUsername = (EditText) findViewById(R.id.et_resetpwd_username);
	etVerifyCode = (EditText) findViewById(R.id.et_verifycode);
	ivVerifyCode = (ImageView) findViewById(R.id.iv_verifycode);

	loadVerifyCode();

	ivVerifyCode.setOnClickListener(this);

	findViewById(R.id.btn_submit).setOnClickListener(this);

    }

    private void loadVerifyCode() {
	if (!ActivityUtil.hasNetwork(this)) {
	    ToastFactory.getToast(ResetPasswordActivity.this, "网络错误,请检查网络").show();
	    return;
	}
	HttpUtil.get(Constants.URL_RESET_PWD_VERIFY_CODE, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		Bitmap bitmap = BitmapFactory.decodeByteArray(arg2, 0, arg2.length);
		ivVerifyCode.setImageBitmap(bitmap);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(ResetPasswordActivity.this, "加载验证码失败").show();
	    }
	});
    }

    private void submit() {
	if (TextUtils.isEmpty(etUsername.getText().toString().trim())) {
	    etUsername.requestFocus();
	    ToastFactory.getToast(ResetPasswordActivity.this, "请输入注册邮箱/手机号").show();
	    return;
	}
	if (!StringUtils.isEmail(etUsername.getText().toString().trim()) && !StringUtils.isPhoneNumber(etUsername.getText().toString().trim())) {
	    etUsername.requestFocus();
	    ToastFactory.getToast(ResetPasswordActivity.this, "邮箱/手机号格式不正确").show();
	    return;
	}

	if (TextUtils.isEmpty(etVerifyCode.getText())) {
	    etVerifyCode.requestFocus();
	    ToastFactory.getToast(ResetPasswordActivity.this, "请输入验证码").show();
	    return;
	}

	if (etVerifyCode.getText().toString().trim().length() < 4) {
	    etVerifyCode.requestFocus();
	    ToastFactory.getToast(ResetPasswordActivity.this, "验证码长度错误").show();
	    return;
	}

	if (!ActivityUtil.hasNetwork(this)) {
	    ToastFactory.getToast(ResetPasswordActivity.this, "网络不可用,请检查网络").show();
	    return;
	}

	RequestParams params = new RequestParams();

	final String username = etUsername.getText().toString().trim();
	params.put("username", username);
	params.put("code", etVerifyCode.getText().toString());

	HttpUtil.post(Constants.URL_RESET_PWD, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String res = new String(arg2);
		System.out.println("重置密码:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			showToast(msg);
			return;
		    }

		    // 判断当前输入的是邮箱还是手机号
		    // 如果是手机号则进入短信验证页
		    if (StringUtils.isPhoneNumber(username)) {
			finish(); // 结束当前activity
			// 进入密码修改页
			Intent intent = new Intent(ResetPasswordActivity.this, ModifyPasswordActivity.class);
			intent.putExtra("phone", username);
			startActivity(intent);
		    }
		    String msg = jsonObj.getString("msg");
		    showToast(msg);

		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    showToast("参数错误");
		}
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
	case R.id.iv_verifycode:
	    loadVerifyCode();
	    break;
	case R.id.tv_top_title:
	    finish();
	    break;
	case R.id.tv_top_action:
	case R.id.btn_submit:
	    submit();
	    break;

	}
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub

	super.onDestroy();
    }
}
