package me.himi.love.ui;

import io.rong.imlib.RongIMClient.ConnectCallback;

import java.util.Date;

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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:SplashActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class SplashActivity extends BaseActivity {
    Button[] mTabs;

    View mMainView;

    TextView tvLoginTips;

    ImageView mIvFace; // 头像

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_splash, null);
	//	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_first_guide, null);
	setContentView(mMainView);

	//	ViewFlipper flipper = (ViewFlipper) mMainView.findViewById(R.id.viewFlipper);
	//	flipper.startFlipping();
	//	
	//	
	//	flipper.setOnTouchListener(new OnTouchListener() {
	//	    
	//	    @Override
	//	    public boolean onTouch(View v, MotionEvent event) {
	//		// TODO Auto-generated method stub
	//		
	//		return false;
	//	    }
	//	});

	init();
    }

    private void init() {

	// 开启过渡动画

	tvLoginTips = (TextView) findViewById(R.id.tv_login_tips); // 自动登录提示信息
	//	mMainView.setBackgroundDrawable(getResources().getDrawable(R.drawable.splash2));

	AnimationSet animalSet = new AnimationSet(false);

	AlphaAnimation animal = new AlphaAnimation(1f, 0f);
	animal.setDuration(3000);

	animal.setRepeatCount(0);

	//	animal.setInterpolator(new BounceInterpolator());

	animalSet.addAnimation(animal);

	//	animal = new AlphaAnimation(1f, 0f);
	//	animal.setDuration(2000);
	//	animalSet.addAnimation(animal);

	mMainView.startAnimation(animalSet);

	// 最后登录用户的头像
	mIvFace = getViewById(R.id.iv_last_login_user);
	mIvFace.setVisibility(View.GONE);

	animalSet.setAnimationListener(new AnimationListener() {

	    @Override
	    public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onAnimationEnd(Animation animation) {
		// 检查本地是否有登录数据
		// 没有就进入 注册
		DBHelper dbHelper = DBHelper.getInstance(SplashActivity.this, DBHelper.DB_NAME, null, 1);
		User user = dbHelper.queryLastLoginUser();
		dbHelper.close();

		if (user == null) {// 本地数据库没有注册用户记录
		    //进入注册页面
		    startActivity(new Intent(SplashActivity.this, UserRegisterOrLoginActivity.class));
		    finish();
		    return;
		}

		if (!ActivityUtil.hasNetwork(SplashActivity.this)) { // 没有网络
		    ToastFactory.getToast(SplashActivity.this, "请开启网络").show();
		    // 进入登录页
		    startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
		    finish();
		    return;
		}
		// 检查是否设置了自动登录
		boolean isAutoLogin = !"0".equals(PreferencesUtil.read(SplashActivity.this, "auto_login"));
		if (isAutoLogin && user.pwd.trim().length() != 0) { //设置了自动登录和存在密码
		    login(user);
		} else {
		    // 进入登录页
		    startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
		    finish();
		}
	    }
	});

    }

    private void login(User user) {

	final int userId = user.userId;
	final String password = user.pwd;

	final String faceUrl = user.faceUrl;

	// 显示用户头像
	mIvFace.setVisibility(View.VISIBLE);

	AnimationSet animSet = new AnimationSet(false);
	TranslateAnimation translateAnim = new TranslateAnimation(0, 0, 600, 100);
	translateAnim.setDuration(300L);
	translateAnim.setInterpolator(new AccelerateInterpolator());
	translateAnim.setFillAfter(true); // 停留在最后一帧的位置
	animSet.addAnimation(translateAnim);

	AlphaAnimation alphaAnim = new AlphaAnimation(0.1f, 1.0f);
	alphaAnim.setDuration(300L);
	animSet.addAnimation(alphaAnim);

	mIvFace.startAnimation(animSet);

	ImageLoader.getInstance().displayImage(faceUrl, mIvFace, ImageLoaderOptions.circleOptions());

	findViewById(R.id.layout_login_loading).setVisibility(View.VISIBLE);

	// 经纬度
	String longtitude = MyApplication.getInstance().getLongtitude();
	String latitude = MyApplication.getInstance().getLatitude();
	String address = MyApplication.getInstance().getAddr();

	AppServiceImpl.getInstance().login(userId + "", password, longtitude, latitude, address, new OnLoginListener() {

	    @Override
	    public void onSuccess(LoginedUser loginedUser) {
		// TODO Auto-generated method stub

		// 查看登录是否成功
		// 成功则跳转到 主页
		// 失败 则进入登录页手动登录
		if (loginedUser == null) {

		    ActivityUtil.show(SplashActivity.this, "登录失败,用户不存在");

		    // 登录失败则进入 登录页面
		    startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
		    finish();
		    return;
		}

		// 内存记录 当前登录的 用户
		MyApplication.getInstance().setCurrentLoginedUser(loginedUser);

		//tvLoginTips.setText("登录IM...");
		//		ToastFactory.getToast(SplashActivity.this, "正在登录IM...").show();

		//		finish();

		DBHelper dbHelper = DBHelper.getInstance(SplashActivity.this, DBHelper.DB_NAME, null, 1);
		// 更新本地数据库记录
		dbHelper.updateUser(loginedUser.getUserId(), password, loginedUser.getNickname(), (int) (new Date().getTime() / 1000L), loginedUser.getFaceUrl(), /*loginedUser.getImUserName()*/"", /*loginedUser.getImPassword()*/"");
		dbHelper.close();

		// 同步登录 Bmob 的IM即时聊天服务帐号
		//		IMServiceImpl.getInstance(SplashActivity.this).login(loginedUser.getImUserName(), loginedUser.getImPassword(), new SaveListener() {
		//
		//		    @Override
		//		    public void onSuccess() {
		//			showToast("即时聊天账户登录成功");
		//			// 更新用户位置信息
		//			IMServiceImpl.getInstance(SplashActivity.this).updateUserInfos();
		//		    }
		//
		//		    @Override
		//		    public void onFailure(int arg0, String arg1) {
		//			// TODO Auto-generated method stub
		//			showToast("即时聊天账户登录失败," + arg1);
		//			// 重新注册IM账号
		//		    }
		//		});

		// 同步登录 融云的IM账户 (修改为进入主页面再登录IM)
		//connectRongCloudIM(userId + "");
		
		
		finish();

		ToastFactory.getToast(SplashActivity.this, "欢迎回来").show();
		//进入主页, 清除所有Activity,避免回退
		ActivityManagerUtils.getInstance().removeAllActivity();

		startActivity(new Intent(SplashActivity.this, MainActivity.class));

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		ActivityUtil.show(SplashActivity.this, "自动登录失败,连接超时,请重试");
		// 登录失败则进入 登录页面
		startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
		finish();
	    }
	});
    }

    private int tryReconnectCount = 3; // 最大失败尝试次数

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
		AppServiceRongCloudIMImpl.getInstance().connect(SplashActivity.this, token, new ConnectCallback() {

		    @Override
		    public void onError(ErrorCode arg0) {
			// TODO Auto-generated method stub
			//			progress.dismiss();

			switch (arg0) {
			case TOKEN_INCORRECT:
			    if (tryReconnectCount-- >= 0) {
				connectRongCloudIM(userId);
				ToastFactory.getToast(SplashActivity.this, "IM token 不正确,重试中...").show();
			    } else {
				ToastFactory.getToast(SplashActivity.this, "IM token 不正确").show();
				// 登录失败则进入 登录页面
				startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
				finish();
			    }
			    break;
			case TIMEOUT:
			    if (tryReconnectCount-- >= 0) {
				connectRongCloudIM(userId);
				ToastFactory.getToast(SplashActivity.this, "登录IM超时,重试中...").show();
			    } else {
				// 登录失败则进入 登录页面
				startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
				finish();
			    }
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

			ToastFactory.getToast(SplashActivity.this, "欢迎回来").show();
			//进入主页, 清除所有Activity,避免回退
			ActivityManagerUtils.getInstance().removeAllActivity();

			startActivity(new Intent(SplashActivity.this, MainActivity.class));
		    }
		});
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(SplashActivity.this, "获取token失败,请重试").show();
		// 登录失败则进入 登录页面
		startActivity(new Intent(SplashActivity.this, UserLoginActivity.class));
		finish();
	    }
	});
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
	// 如果自动登录中则退出自动登录
	if (findViewById(R.id.layout_login_loading).getVisibility() == View.VISIBLE) {
	    HttpUtil.getClient().cancelAllRequests(true); // 中止所有请求

	    startActivity(new Intent(this, UserLoginActivity.class));
	    finish();

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
}
