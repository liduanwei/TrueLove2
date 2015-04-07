package me.himi.love.ui;

import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnRegisterListener;
import me.himi.love.IAppService.RegisterUser;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.RegisteredUser;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityManagerUtils;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.StringUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @ClassName:UserRegisterStep2Activity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserRegisterStep2Activity extends BaseActivity implements OnClickListener {

    View mMainView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_user_register_step2, null);
	setContentView(mMainView);

	init();

    }

    EditText etPassword, etPasswordConfirm;

    private void init() {

	//	String username = getIntent().getStringExtra("username");

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("注册-设置密码");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("完成");
	tvTopAction.setOnClickListener(this);
	findViewById(R.id.button_register).setOnClickListener(this);

	etPassword = (EditText) findViewById(R.id.et_password);
	etPasswordConfirm = (EditText) findViewById(R.id.et_password_confirm);

    }

    private void register() {

	String username = getIntent().getStringExtra("username").trim();
	if (!StringUtils.isPhoneNumber(username)) {
	    showToast("非手机号");
	    return;
	}

	if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
	    etPassword.requestFocus();
	    showToast("请填写密码");
	    return;
	}

	if (etPassword.getText().toString().trim().length() < 6 || etPassword.getText().toString().trim().length() > 10) {
	    etPassword.requestFocus();
	    showToast("密码长度为6-10位");
	    return;
	}

	if (TextUtils.isEmpty(etPasswordConfirm.getText().toString().trim())) {
	    etPasswordConfirm.requestFocus();
	    showToast("请填写确认密码");
	    return;
	}

	if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
	    showToast("两次密码不一致");
	    return;
	}

	final ProgressDialog progress = new ProgressDialog(this);
	progress.setMessage("正在注册...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	// 注册需要提交的字段
	RegisterUser user = new RegisterUser();

	user.setUsername(username);
	user.setPassword(etPassword.getText().toString().trim());

	// 月份 -1 , 因为是从0表示1月
	user.setImei(ActivityUtil.getImei(this));
	user.setPhoneNumber(ActivityUtil.getPhoneNumber(this));
	// 注册时的经纬度
	user.setLongtitude(MyApplication.getInstance().getLongtitude());
	user.setLatitude(MyApplication.getInstance().getLatitude());

	AppServiceImpl.getInstance().register(user, new OnRegisterListener() {

	    @Override
	    public void onSuccess(final RegisteredUser registeredUser) {
		progress.dismiss();
		if (null == registeredUser) {
		    ActivityUtil.show(UserRegisterStep2Activity.this, "注册失败!\n可能原因: 该手机号已注册");
		    return;
		}

		// 保存用户ID和密码到本地数据库
		showToast("恭喜,注册成功!");

		DBHelper dbHelper = DBHelper.getInstance(UserRegisterStep2Activity.this, DBHelper.DB_NAME, null, 1);
		// 清空所有数据
		//dbHelper.truncateAllTables();调试时不执行
		//		dbHelper.deleteMessageTable(); // 清空消息

		dbHelper.insertUser(registeredUser.getUserId(), registeredUser.getUserPassword(), registeredUser.getUserId() + "");

		// 同时注册 IM 聊天帐号
		//		registerIM(registeredUser);

		// 进入完善资料
		startActivity(new Intent(UserRegisterStep2Activity.this, UserRegisterCompleteActivity.class));
		finish();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		progress.dismiss();
		showToast("注册失败," + errorMsg);

	    }
	});
    }

    /**
     * 进入登录页
     * @param v
     */
    public void btnStartLoginOnClick(View v) {
	ActivityManagerUtils.getInstance().removeAllActivity();
	startActivity(new Intent(this, UserLoginActivity.class));
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_top_action:
	case R.id.button_register:
	    register();
	    break;

	}
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();

    }

}
