package me.himi.love.ui;

import io.rong.imlib.RongIMClient.ConnectCallback;

import java.util.List;

import me.himi.love.AppServiceImpl;
import me.himi.love.AppServiceRongCloudIMImpl;
import me.himi.love.IAppService.OnLoginListener;
import me.himi.love.IAppServiceRongCloudIM.OnTokenResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.dao.DBHelper;
import me.himi.love.dao.DBHelper.User;
import me.himi.love.entity.LoginedUser;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityManagerUtils;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.PreferencesUtil;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.EmojiTextView;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserLoginActivity extends BaseActivity implements View.OnClickListener {
    EditText mEtUserId;
    EditText mEtPassword;

    CheckBox mCbRememberPwd;
    CheckBox mCbAutoLogin;

    TextView mTvForgotPassword;

    ImageView ivFace; // 头像

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_user_login);

	init();
    }

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("登录");

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("");
	tvTopAction.setOnClickListener(this);
	tvTopAction.setVisibility(View.GONE);

	// 如果选择了记住密码则再次登录时自动给EditText 设值,否则不设置
	ivFace = (ImageView) findViewById(R.id.iv_selected_login_user);

	// 忘记密码
	mTvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
	mTvForgotPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	mTvForgotPassword.setOnClickListener(this);

	mEtUserId = (EditText) findViewById(R.id.et_login_id);
	mEtPassword = (EditText) findViewById(R.id.et_login_password);

	mCbRememberPwd = (CheckBox) findViewById(R.id.cb_remember_password);
	// 非0即选中, 默认null为选中
	mCbRememberPwd.setChecked(!"0".equals(PreferencesUtil.read(this, "remember_password")));
	mCbRememberPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		PreferencesUtil.save(UserLoginActivity.this, "remember_password", mCbRememberPwd.isChecked() ? "1" : "0");
	    }
	});
	// 
	mCbAutoLogin = (CheckBox) findViewById(R.id.cb_remember_auto_login);
	mCbAutoLogin.setChecked(!"0".equals(PreferencesUtil.read(this, "auto_login")));
	mCbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

	    @Override
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		PreferencesUtil.save(UserLoginActivity.this, "auto_login", mCbAutoLogin.isChecked() ? "1" : "0");
	    }
	});

	// 查询本地数据库中已存在的注册的用户记录
	DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);

	List<User> users = dbHelper.queryAllUsers();

	if (users == null || users.size() == 0 || users.size() == 1) { // 只有一个用户或无用户则不显示下拉按钮
	    findViewById(R.id.tv_user_login_show_alluser).setVisibility(View.GONE);
	} else {
	    findViewById(R.id.tv_user_login_show_alluser).setVisibility(View.VISIBLE);
	    findViewById(R.id.tv_user_login_show_alluser).setOnClickListener(this);
	}

	// debug

	// 读取本地记录的最后登录用户的id及密码
	User user = dbHelper.queryLastLoginUser();
	dbHelper.close();

	//MyApplication.currentLoginedUser.getUserId();

	if (user != null) {
	    mEtUserId.setText(user.userId + "");
	    if (mCbRememberPwd.isChecked()) {
		mEtPassword.setText(user.pwd);
	    }
	    if (user.faceUrl != null) {
		ivFace.setVisibility(user.faceUrl.trim().length() != 0 ? View.VISIBLE : View.GONE);
		ImageLoader.getInstance().displayImage(user.faceUrl, ivFace, ImageLoaderOptions.circleOptions());
	    }

	} else {
	    ivFace.setVisibility(View.GONE);
	}

    }

    public void btnLoginOnClick(View v) {

	login();
    }

    /**
     * 进入注册页
     * @param v
     */
    public void btnStartRegisterOnClick(View v) {
	// 清除回退栈
	ActivityManagerUtils.getInstance().removeAllActivity();
	Intent intent = new Intent();
	intent.setClass(this, UserRegisterActivity.class);
	startActivity(intent);
    }

    private boolean isCancledLogin;// 标记是否点按取消了登录

    private ProgressDialog progress;

    private void login() {

	isCancledLogin = false;

	if (TextUtils.isEmpty(mEtUserId.getText())) {
	    showToast("用户ID不能为空");
	    mEtUserId.requestFocus();
	    return;
	}

	if (TextUtils.isEmpty(mEtPassword.getText())) {
	    showToast("密码不能为空");
	    mEtPassword.requestFocus();
	    return;
	}

	if (!ActivityUtil.hasNetwork(this)) {
	    ToastFactory.getToast(this, "请开启网络").show();
	    return;
	}

	final String userId = mEtUserId.getText().toString();
	final String password = mEtPassword.getText().toString();

	if (null == progress) {
	    progress = new ProgressDialog(this);
	}
	progress.setMessage("正在登录...");
	progress.setOnDismissListener(new OnDismissListener() {

	    @Override
	    public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		isCancledLogin = true;
		HttpUtil.getClient().cancelAllRequests(true);// 中止网络请求,不起作用?
	    }
	});
	progress.setOnCancelListener(new OnCancelListener() {

	    @Override
	    public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		isCancledLogin = true;
		HttpUtil.getClient().cancelAllRequests(true);// 中止网络请求,不起作用?
	    }
	});
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	// 经纬度
	String longtitude = MyApplication.getInstance().getLongtitude();
	String latitude = MyApplication.getInstance().getLatitude();
	String address = MyApplication.getInstance().getAddr();

	AppServiceImpl.getInstance().login(userId, password, longtitude, latitude, address, new OnLoginListener() {

	    @Override
	    public void onSuccess(LoginedUser loginedUser) {
		//		progress.dismiss();
		if (isCancledLogin) {
		    return;
		}
		// TODO Auto-generated method stub
		if (loginedUser == null) {
		    ActivityUtil.show(UserLoginActivity.this, "登录失败,帐户名/密码错误");
		    progress.dismiss();
		    return;
		}

		// 内存记录 当前登录的 用户
		MyApplication.getInstance().setCurrentLoginedUser(loginedUser);

		//		ToastFactory.getToast(UserLoginActivity.this, "登录成功").show();
		//progress.setMessage("登录IM..."); // 登录 IM

		// 存储用户的资料到本地数据库
		DBHelper dbHelper = DBHelper.getInstance(UserLoginActivity.this, DBHelper.DB_NAME, null, 1);

		// 如果未勾选记住密码则不保存密码
		String pwd = "";
		if (mCbRememberPwd.isChecked()) {
		    pwd = password;
		}
		if (!dbHelper.findUser(loginedUser.getUserId())) { // 本地不存在则添加
		    dbHelper.insertUser(loginedUser.getUserId(), password, loginedUser.getNickname());
		}
		dbHelper.updateUser(loginedUser.getUserId(), pwd, loginedUser.getNickname(), (int) (System.currentTimeMillis() / 1000L), loginedUser.getFaceUrl(), /*loginedUser.getImUserName()*/"", /*loginedUser.getImPassword()*/"");
		dbHelper.close();

		// 同步登录 Bmob 的IM即时聊天服务帐号
		//		final IMServiceImpl serviceImpl = IMServiceImpl.getInstance(UserLoginActivity.this);
		//		serviceImpl.login(loginedUser.getImUserName(), loginedUser.getImPassword(), new SaveListener() {
		//
		//		    @Override
		//		    public void onSuccess() {
		//			showToast("IM聊天账户登录成功");
		//			serviceImpl.updateUserInfos();
		//		    }
		//
		//		    @Override
		//		    public void onFailure(int arg0, String arg1) {
		//			// TODO Auto-generated method stub
		//			showToast("即时聊天账户登录失败," + arg1);
		//		    }
		//		});

		// 同步登录 融云的IM账户
		//connectRongCloudIM(userId + "");

		//		//进入主页, 清除所有Activity,避免回退
		//		ActivityManagerUtils.getInstance().removeAllActivity();
		//
		//		startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
		finish();

		ToastFactory.getToast(UserLoginActivity.this, "欢迎回来").show();
		//进入主页, 清除所有Activity,避免回退
		ActivityManagerUtils.getInstance().removeAllActivity();
		startActivity(new Intent(UserLoginActivity.this, MainActivity.class));

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		if (isCancledLogin) {
		    return;
		}
		ActivityUtil.show(UserLoginActivity.this, "连接超时,请重试");
		progress.dismiss();
	    }
	});
    }

    /**
     * 连接到融云IM
     * @param userId
     */
    private void connectRongCloudIM(final String userId) {
	//	final ProgressDialog progress = new ProgressDialog(SplashActivity.this);
	//	progress.setMessage("正在登录IM");
	//	progress.setTitle("");
	//	progress.show();

	AppServiceRongCloudIMImpl.getInstance().getTokenFromServer(userId, new OnTokenResponseListener() {

	    @Override
	    public void onSuccess(String token) {
		// TODO Auto-generated method stub
		AppServiceRongCloudIMImpl.getInstance().connect(UserLoginActivity.this, token, new ConnectCallback() {

		    @Override
		    public void onError(ErrorCode arg0) {
			// TODO Auto-generated method stub
			//			progress.dismiss();
			switch (arg0) {
			case TOKEN_INCORRECT:
			    connectRongCloudIM(userId);
			    break;
			case TIMEOUT:
			    connectRongCloudIM(userId);
			    break;
			case APP_KEY_UNAVAILABLE:
			    break;
			case PACKAGE_BROKEN:
			    break;
			case DATABASE_ERROR:
			    break;
			case SERVER_UNAVAILABLE:
			    break;
			case UNKNOWN:
			    break;
			}
		    }

		    @Override
		    public void onSuccess(String arg0) {
			// TODO Auto-generated method stub
			//			progress.dismiss();
			finish();
			// 刷新用户资料
			AppServiceRongCloudIMImpl.getInstance().refreshUserInfo();
			ToastFactory.getToast(UserLoginActivity.this, "欢迎回来").show();
			//进入主页, 清除所有Activity,避免回退
			ActivityManagerUtils.getInstance().removeAllActivity();
			startActivity(new Intent(UserLoginActivity.this, MainActivity.class));
		    }
		});
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(UserLoginActivity.this, "获取token失败,连接超时,请重试").show();
		progress.dismiss();
	    }
	});
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_user_login_show_alluser:
	    showUsersPopupWindow();
	    break;
	case R.id.tv_forgot_password:
	    startActivity(new Intent(this, ResetPasswordActivity.class));
	    break;
	case R.id.tv_top_action:
	    login();
	    break;
	}
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
	if (historyUsersPopupWindow != null && historyUsersPopupWindow.isShowing()) {
	    historyUsersPopupWindow.dismiss();
	    return;
	}

	// TODO Auto-generated method stub
	if (firstTime + 2000 > System.currentTimeMillis()) {
	    MyApplication.getInstance().exit();
	    super.onBackPressed();
	} else {
	    ActivityUtil.show(this, "再按一次退出应用");
	}
	firstTime = System.currentTimeMillis();
    }

    PopupWindow historyUsersPopupWindow;

    private void showUsersPopupWindow() {

	// 如果显示则隐藏
	if (historyUsersPopupWindow != null && historyUsersPopupWindow.isShowing()) {
	    historyUsersPopupWindow.dismiss();
	    return;
	}

	// 查询本地数据库中已存在的注册的用户记录
	DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);
	List<User> users = dbHelper.queryAllUsers();

	// TODO Auto-generated method stub
	if (historyUsersPopupWindow != null) {
	    historyUsersPopupWindow.dismiss();
	    historyUsersPopupWindow = null;
	}
	historyUsersPopupWindow = new PopupWindow();
	ScrollView scrollView = new ScrollView(this);
	LinearLayout view = new LinearLayout(this);
	view.setOrientation(LinearLayout.VERTICAL);
	view.setBackgroundResource(R.color.base_color_text_gray);

	//	historyUsersPopupWindow.setAnimationStyle(-1);// 作为 dropdown 时使用默认动画效果
	//	historyUsersPopupWindow.update(); // 立即更新,使用动画,

	ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

	for (int i = 0, n = users.size(); i < n; ++i) {
	    final User user = users.get(i);
	    EmojiTextView tvUserId = new EmojiTextView(this);
	    tvUserId.setPadding(15, 15, 15, 15);
	    tvUserId.setClickable(true);
	    tvUserId.setLayoutParams(params);
	    tvUserId.setGravity(Gravity.CENTER_HORIZONTAL);

	    tvUserId.setBackgroundResource(R.drawable.selector_login_user_item);
	    tvUserId.setEmojiText(user.nickname + " - " + user.userId);
	    view.addView(tvUserId);
	    tvUserId.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    mEtUserId.setText(user.userId + "");
		    mEtPassword.setText(user.pwd + "");
		    historyUsersPopupWindow.dismiss();

		    System.out.println("faceUrl:" + user.faceUrl);
		    if (user.faceUrl != null) {
			ivFace.setVisibility(user.faceUrl.trim().length() != 0 ? View.VISIBLE : View.GONE);
			ImageLoader.getInstance().displayImage(user.faceUrl, ivFace, ImageLoaderOptions.circleOptions());
		    }
		}
	    });
	}

	scrollView.addView(view);
	historyUsersPopupWindow.setContentView(scrollView);
	historyUsersPopupWindow.setWidth(mEtUserId.getWidth());
	historyUsersPopupWindow.setOutsideTouchable(false);
	//	popupWindow.setHeight(mEtUserId.getHeight() * users.size());
	historyUsersPopupWindow.setHeight(300);
	historyUsersPopupWindow.showAsDropDown(mEtUserId);
    }
}
