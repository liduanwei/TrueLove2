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
import me.himi.love.util.ShareSMSConstants;
import me.himi.love.util.StringUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @ClassName:UserRegisterActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserRegisterActivity extends BaseActivity implements OnClickListener {

    View mMainView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_user_register, null);

	setContentView(mMainView);

	init();

    }

    EditText etUsername;

    TextView tvSendVerifyCode; // 发送验证码,提交验证码
    EditText etVerifyCode; // 验证码

    private boolean isVerified; // 手机号是否已验证通过
    private String currentPhoneNumber;// 当前输入待验证的手机号码

    CheckBox mCbAgreeLicense; // 同意协议

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("注册");
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("完成");
	tvTopAction.setOnClickListener(this);

	findViewById(R.id.button_register).setOnClickListener(this);

	// 同意协议
	mCbAgreeLicense = (CheckBox) findViewById(R.id.cb_agree_license);

	etUsername = (EditText) findViewById(R.id.et_username);

	// 使用协议
	TextView tvLisence = ((TextView) findViewById(R.id.tv_register_lisence));
	//	tvLisence.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	tvLisence.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		showToast("查看使用协议");
	    }
	});

	etUsername.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		isVerified = false;// 有任何输入改变就设置为未验证

		//如果输入的是手机号码则显示验证码输入框
		if (StringUtils.isPhoneNumber(etUsername.getText().toString().trim())) {
		    tvSendVerifyCode.setVisibility(View.VISIBLE);
		    findViewById(R.id.rl_phone_number_verifycode).setVisibility(View.VISIBLE);
		} else {
		    tvSendVerifyCode.setVisibility(View.GONE);
		    findViewById(R.id.rl_phone_number_verifycode).setVisibility(View.GONE);
		}
	    }
	});

	initSMSSDK();

	// 发送验证码
	tvSendVerifyCode = (TextView) findViewById(R.id.tv_send_phonenumber_verifycode);
	tvSendVerifyCode.setOnClickListener(this);

	// 验证码 输入
	etVerifyCode = (EditText) findViewById(R.id.et_phone_number_verifycode);

	tvSendVerifyCode.setVisibility(View.GONE);
	findViewById(R.id.rl_phone_number_verifycode).setVisibility(View.GONE);

	// 密码,密码确认
	etPassword = (EditText) findViewById(R.id.et_password);
	etPasswordConfirm = (EditText) findViewById(R.id.et_password_confirm);

    }

    private void initSMSSDK() {
	// TODO Auto-generated method stub
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
			// 关闭提示进度框
			if (verifyDialog != null) {
			    verifyDialog.dismiss();
			}

			Toast.makeText(getApplicationContext(), "验证成功", Toast.LENGTH_SHORT).show();

			tvSendVerifyCode.setVisibility(View.GONE);
			findViewById(R.id.rl_phone_number_verifycode).setVisibility(View.GONE);
			//			System.out.println(data.toString());
			isVerified = true;// 验证通过
			//			textView2.setText("提交验证码成功");

			String number = StringUtils.findPhoneNumber(data.toString());
			if (number != null) {
			    register(number); // 提交注册
			} else {
			    showToast("响应内容错误");
			}

		    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
			// 关闭提示进度框
			if (verifyDialog != null) {
			    verifyDialog.dismiss();
			}

			Toast.makeText(getApplicationContext(), "验证码将以短信发送,请注意查收", Toast.LENGTH_SHORT).show();
			//			textView2.setText("验证码已经发送");
			etVerifyCode.requestFocus();//定位输入验证码
			etVerifyCode.setText(""); // 清空内容

		    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表

			Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
			//			countryTextView.setText(data.toString());
		    }
		} else { // 验证码错误
		    ((Throwable) data).printStackTrace();
		    // 关闭提示进度框
		    if (verifyDialog != null) {
			verifyDialog.dismiss();
		    }
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

    EditText etPassword, etPasswordConfirm;

    /**
     * 注册前客户端进行必要检查
     */
    private void registerBefore() {
	String username = etUsername.getText().toString().trim();

	if (!mCbAgreeLicense.isChecked()) {
	    showToast("您必须先同意使用协议才能注册");
	    return;
	}

	if (TextUtils.isEmpty(username)) {
	    etUsername.requestFocus();
	    showToast("请填写手机号/邮箱");
	    return;
	}

	// 非正确邮箱或手机号
	if (!StringUtils.isEmail(username) && !StringUtils.isPhoneNumber(username)) {
	    etUsername.requestFocus();
	    showToast("邮箱格式/手机号码不正确");
	    //	    showToast("手机号码格式不正确");
	    return;
	}

	// 检查为手机号情况下
	if (StringUtils.isPhoneNumber(username)) {
	    String code = etVerifyCode.getText().toString().trim();
	    if (code.length() == 0) {
		showToast("请输入验证码");
		etVerifyCode.requestFocus();
		return;
	    }
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

	// 检查为手机号情况下
	if (StringUtils.isPhoneNumber(username)) {
	    if (!isVerified) { // 还未验证就先进行验证
		submitPhoneVerifyCode(); // 先检查验证码	
		return;
	    }
	}

	// 邮箱情况下直接注册
	register(username);

    }

    private void nextStep(String username) {
	// TODO Auto-generated method stub
	Intent intent = new Intent(UserRegisterActivity.this, UserRegisterStep2Activity.class);
	intent.putExtra("username", username);
	startActivity(intent);
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (firstTime + 2000 > System.currentTimeMillis()) {
	    MyApplication.getInstance().exit();
	    super.onBackPressed();
	} else {
	    ActivityUtil.show(this, "再按一次退出应用");
	}
	firstTime = System.currentTimeMillis();
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
	    registerBefore(); //
	    break;
	case R.id.tv_send_phonenumber_verifycode:
	    // 发送验证码
	    sendVerifyCode();
	    break;
	}
    }

    // 发送验证码
    private void sendVerifyCode() {
	if (verifyDialog == null) {
	    verifyDialog = new ProgressDialog(this);
	}

	verifyDialog.setMessage("验证码发送中...");
	verifyDialog.show();

	this.currentPhoneNumber = etUsername.getText().toString().trim();
	SMSSDK.getVerificationCode("86", currentPhoneNumber);

    }

    ProgressDialog verifyDialog;

    // 提交验证码
    private void submitPhoneVerifyCode() {
	//	isVerified = true;
	String code = etVerifyCode.getText().toString().trim();
	if (code.length() == 0) {
	    showToast("请输入验证码");
	    etVerifyCode.requestFocus();
	    return;
	}

	if (verifyDialog == null) {
	    verifyDialog = new ProgressDialog(this);
	}
	verifyDialog.setMessage("验证码验证中...");
	verifyDialog.show();

	SMSSDK.submitVerificationCode("86", etUsername.getText().toString().trim(), etVerifyCode.getText().toString().trim());
    }

    private void register(String username) {

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
	// 设备型号
	user.setModel(ActivityUtil.getModel());

	AppServiceImpl.getInstance().register(user, new OnRegisterListener() {

	    @Override
	    public void onSuccess(final RegisteredUser registeredUser) {
		progress.dismiss();
		if (null == registeredUser) {
		    ActivityUtil.show(UserRegisterActivity.this, "注册失败!\n可能原因: 该邮箱/手机号已注册");

		    return;
		}

		// 保存用户ID和密码到本地数据库
		showToast("恭喜,注册成功!");

		DBHelper dbHelper = DBHelper.getInstance(UserRegisterActivity.this, DBHelper.DB_NAME, null, 1);
		// 清空所有数据
		//dbHelper.truncateAllTables();调试时不执行
		//		dbHelper.deleteMessageTable(); // 清空消息

		dbHelper.insertUser(registeredUser.getUserId(), registeredUser.getUserPassword(), registeredUser.getUserId() + "");

		// 同时注册 IM 聊天帐号
		//		registerIM(registeredUser);

		// 进入完善资料
		startActivity(new Intent(UserRegisterActivity.this, UserRegisterCompleteActivity.class));
		finish();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		progress.dismiss();
		showToast("注册失败," + errorMsg);

	    }
	});
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	SMSSDK.unregisterAllEventHandler(); // 销毁smsSDK
	super.onDestroy();

    }

}
