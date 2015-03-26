package me.himi.love.ui;

import io.rong.imlib.RongIMClient.ConnectCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.himi.love.AppServiceImpl;
import me.himi.love.AppServiceRongCloudIMImpl;
import me.himi.love.IAppService.CheckUpdateParams;
import me.himi.love.IAppService.OnCheckUpdateListener;
import me.himi.love.IAppServiceRongCloudIM.OnTokenResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.boost.androidservice.FloatShareService;
import me.himi.love.boost.androidservice.MessagePollService;
import me.himi.love.dao.DBHelper;
import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.entity.PrivateMessage;
import me.himi.love.entity.PrivateMessage.MessageType;
import me.himi.love.ui.CheckUpdateActivity.OnUpdateListener;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.ui.fragment.CommunityFragment;
import me.himi.love.ui.fragment.NearbyFragment;
import me.himi.love.ui.fragment.PersonalFragment;
import me.himi.love.ui.fragment.StrangeNewsAndIntriguingStoryFragment;
import me.himi.love.ui.fragment.UserWallFragment;
import me.himi.love.ui.sound.SoundPlayer;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.StringUtils;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.MarqueeTextView;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsEarnNotify;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends BaseActivity {

    Fragment[] fragments;

    Button[] mTabs;

    TextView mTvTopTitle, mTvTopAction;
    TextView tvMessageTips;

    //    ConversationFragment conversationsFragment;

    CommunityFragment shareFragment;
    UserWallFragment userWallFragment; //

    //    MessagesFragment messagesFragment;
    PersonalFragment personalFragment;
    NearbyFragment nearPeopleFragment;

    StrangeNewsAndIntriguingStoryFragment strangenewsFragment;

    static final long CHECK_UPDATE_DELAY = 1000 * 60 * 60 * 24 * 1; // 相隔1天检查一次新版本

    public static final String ACTION_IM_RECEIVE_MESSAGE = "receive_message";

    RelativeLayout mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	mContentView = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
	setContentView(mContentView);

	initTabs();

	initPopupView();

	initGifWindow();

	initServices();
	initBroadcastReceiver();

	// 系统公告
	MarqueeTextView tvNotice = getViewById(R.id.tv_system_notice);
	tvNotice.setTextColor(getResources().getColor(R.color.text_white));
	tvNotice.setText("[系统公告] ID211163用户成功开通了12个月VIP会员");

	//检查更新
	SharedPreferences pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	long lastCheckTime = pref.getLong("last_checkupdate_time", 0);
	if (System.currentTimeMillis() - lastCheckTime >= CHECK_UPDATE_DELAY) {
	    if (ActivityUtil.hasNetwork(this)) {
		checkAndUpdate();
	    }
	}

	// 测试更新
	//	checkAndUpdate();

	// 初始化广告
	initAd();

	// 连接IM
	connectRongCloudIM(MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");
    }

    /**
     * 广告初始化
     */
    private void initAd() {
	// TODO Auto-generated method stub
	initAdYoumi();
    }

    /**
     * 有米
     */

    PointsEarnNotify pointsEarnNotify = new PointsEarnNotify() {

	@Override
	public void onPointEarn(Context arg0, EarnPointsOrderList arg1) {
	    // TODO Auto-generated method stub
	    showToast(arg1.get(0).getPoints() + "积分");
	}
    };

    // 积分变动通知
    PointsChangeNotify pointsChangeNotify = new PointsChangeNotify() {

	@Override
	public void onPointBalanceChange(int arg0) {
	    // TODO Auto-generated method stub
	    //
	    showToast("当前积分:" + arg0);
	}
    };

    /**
     * 有米初始化
     */
    private void initAdYoumi() {
	// TODO Auto-generated method stub
	String appId = "767bdba8ddf43d30";
	String appSecret = "1ab2a0364b1ad5ec";

	net.youmi.android.AdManager.getInstance(this).init(appId, appSecret, false);

	//积分墙
	net.youmi.android.offers.OffersManager.getInstance(this).onAppLaunch();
	// 设置用户标识
	net.youmi.android.offers.OffersManager.getInstance(this).setCustomUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");
	// 开启用户数据统计
	net.youmi.android.AdManager.getInstance(this).setUserDataCollect(true);
	// 开启积分墙通知提醒
	net.youmi.android.offers.PointsManager.setEnableEarnPointsNotification(true);
	//开启积分到账悬浮框提示功能
	net.youmi.android.offers.PointsManager.setEnableEarnPointsToastTips(true);
	// 让UI刷新积分(需要在onDestroy 方法中销毁
	net.youmi.android.offers.PointsManager.getInstance(this).registerNotify(pointsChangeNotify);

	// 设置积分墙样式
	net.youmi.android.offers.OffersBrowserConfig.setBrowserTitleText("免费赚取金币");
	net.youmi.android.offers.OffersBrowserConfig.setBrowserTitleBackgroundColor(readTopBackgroundColor());

	//	showToast("积分:" + net.youmi.android.offers.PointsManager.getInstance(this).queryPoints() + "");

	//	showToast("配置状态:"+net.youmi.android.offers.OffersManager.getInstance(this).checkOffersAdConfig());

	//在onDestroy时 调用exit
	//	net.youmi.android.offers.OffersManager.getInstance(this).onAppExit();

	// 注册积分更新监听(在onDestroy中解除注册)
	net.youmi.android.offers.PointsManager.getInstance(this).registerPointsEarnNotify(pointsEarnNotify);

	// 使用服务器通知
	net.youmi.android.offers.OffersManager.setUsingServerCallBack(true);
    }

    private void checkAndUpdate() {
	CheckUpdateParams params = new CheckUpdateParams();
	params.currentVersion = ActivityUtil.getVersionCode(this);

	AppServiceImpl.getInstance().checkUpdate(params, new OnCheckUpdateListener() {

	    @Override
	    public void onSuccess(final CheckUpdateVersion checkUpdateVersion) {

		if (null != checkUpdateVersion) {

		    // 先检查安装文件是否已存在且MD5值一致
		    String tmpDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/";

		    final File file = new File(tmpDir + "me.himi.love" + checkUpdateVersion.getDate() + ".apk");
		    System.out.println("file md5:" + checkUpdateVersion.getMd5() + ",md5:" + StringUtils.md5(file));
		    if (file.exists() && checkUpdateVersion.getMd5().equals(StringUtils.md5(file).toLowerCase())) { // 文件已存在并且md5值一致
			//存在此版本安装包则进入安装
			showInstallDialog(checkUpdateVersion, file.getAbsolutePath());
			return;
		    }

		    downloadAndUpdate(checkUpdateVersion.getDownloadUrl(), checkUpdateVersion.getSize(), file, new OnUpdateListener() {

			@Override
			public void onSuccess(final String file) {
			    // TODO Auto-generated method stub
			    showInstallDialog(checkUpdateVersion, file);
			}

			@Override
			public void onFailure(String msg) {
			    // TODO Auto-generated method stub

			}

			@Override
			public void onDownloading(int contentLength, int currentLength) {
			    // TODO Auto-generated method stub
			}
		    });

		    //		    // 提示新版本
		    //		    final Dialog dialog = new Dialog(MainActivity.this);
		    //		    View view = getLayoutInflater().inflate(R.layout.layout_newversion_tips, null);
		    //		    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    //		    dialog.setContentView(view);
		    //
		    //		    final TextView tvTips = (TextView) view.findViewById(R.id.tv_newversion_tips);
		    //		    final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		    //		    progressBar.setVisibility(View.GONE);
		    //
		    //		    tvTips.setText("发现新版本!\n版本号:" + checkUpdateVersion.getVersionName() + ",\n更新内容如下:\n" + checkUpdateVersion.getUpdateInstruction());
		    //		    final Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
		    //		    final Button btnUpdateCancel = (Button) view.findViewById(R.id.btn_update_cancel);
		    //
		    //		    btnUpdate.setOnClickListener(new View.OnClickListener() {
		    //
		    //			@Override
		    //			public void onClick(View v) {
		    //			    // 有新版本则先提示下载然后提示安装
		    //			    showToast("新版本后台下载中,请不要退出应用...");
		    //			    dialog.dismiss();
		    //			    
		    //			}
		    //		    });
		    //
		    //		    btnUpdateCancel.setOnClickListener(new View.OnClickListener() {
		    //
		    //			@Override
		    //			public void onClick(View v) {
		    //			    // TODO Auto-generated method stub
		    //			    dialog.dismiss();
		    //			}
		    //		    });
		    //		    dialog.show();

		    // 通知更新
		    //定义一个PendingIntent，当用户点击通知时，跳转到某个Activity(也可以发送广播等)  
		    //		    Intent intent = new Intent(MyApplication.this, CheckUpdateActivity.class);
		    //
		    //		    Bundle bundle = new Bundle();
		    //		    bundle.putSerializable("updateVersion", v);
		    //		    intent.putExtras(bundle);
		    //		    PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.this, 0, intent, 0);
		    //
		    //		    //创建一个通知  
		    //		    Notification notification = new Notification(R.drawable.icon, "有新版本可以更新了~~", System.currentTimeMillis());
		    //		    notification.setLatestEventInfo(MyApplication.this, "点击更新", "点击下载安装包并更新", pendingIntent);
		    //
		    //		    //用NotificationManager的notify方法通知用户生成标题栏消息通知  
		    //		    NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    //		    nManager.notify(103, notification);//id是应用中通知的唯一标识  
		    //如果拥有相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。  
		}
	    }

	    @Override
	    public void onFailure(String errormsg) {
		// TODO Auto-generated method stub

	    }

	    public void downloadAndUpdate(final String downUrl, final int size, final File downPath, final OnUpdateListener listener) {

		final Handler handler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
			    listener.onDownloading(msg.arg1, msg.arg2);
			    break;
			case 1:
			    listener.onSuccess(msg.obj.toString());
			    break;
			case 2:
			    listener.onFailure(msg.obj.toString());
			    break;
			}
			super.handleMessage(msg);
		    }
		};

		ExecutorService executorService = Executors.newFixedThreadPool(3);
		executorService.execute(new Runnable() {

		    @Override
		    public void run() {
			// TODO Auto-generated method stub
			try {
			    HttpURLConnection conn = (HttpURLConnection) new URL(downUrl).openConnection();
			    InputStream in = conn.getInputStream();
			    int contentLength = size;/*conn.getContentLength();*/

			    int currentLength = 0;
			    byte[] buffer = new byte[1024 << 1];
			    int len = 0;

			    if (!downPath.getParentFile().exists()) {// 如果父目录不存在则创建
				downPath.getParentFile().mkdirs(); // 
			    }

			    FileOutputStream fos = new FileOutputStream(downPath);

			    while ((len = in.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				currentLength += len;
				handler.obtainMessage(0, contentLength, currentLength).sendToTarget();
			    }
			    fos.flush();
			    fos.close();
			    in.close();
			    // 下载完成
			    handler.obtainMessage(1, downPath.getAbsolutePath()).sendToTarget();
			    return;
			} catch (MalformedURLException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

			handler.obtainMessage(2, "下载失败,可能会是网络问题").sendToTarget();
		    };
		});

		//		new Thread() {
		//		    @Override
		//		    public void run() {
		//		    }
		//		}.start();

	    }

	    private void showInstallDialog(final CheckUpdateVersion v, final String file) {
		final Builder builder = new AlertDialog.Builder(MyApplication.getInstance().getTopActivity());

		builder.setTitle("新版本提示");

		builder.setMessage("有新版本可以使用了,\n版本号:" + v.getVersionName() + "\n当前版本号:" + ActivityUtil.getVersionName(MainActivity.this) + ",\n更新内容说明如下:\n" + v.getUpdateInstruction());

		builder.setPositiveButton("安装", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			// 确定安装后保存最后更新时间
			SharedPreferences pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
			pref.edit().putLong("last_checkupdate_time", System.currentTimeMillis()).commit();
			ActivityUtil.installApk(getApplicationContext(), new File(file));
		    }
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.dismiss();
		    }
		});
		final AlertDialog dialog = builder.setCancelable(false).create();
		dialog.show();
	    }
	});
    }

    Intent floatButtonService;//
    Intent msgPollService;// 轮询service

    private void initServices() {
	// 启动 消息 轮询service
	msgPollService = new Intent(this, MessagePollService.class);
	startService(msgPollService);

	//	// 启动 悬浮按钮
	floatButtonService = new Intent(this, FloatShareService.class);
	startService(floatButtonService);
    }

    /**
     * 停止 service
     */
    private void stopServices() {
	stopService(msgPollService);
	//	stopService(floatButtonService);

    }

    // 私信 broadcaseReceiver
    BroadcastReceiver privateMessageReceiver;
    // 新 IM 消息
    BroadcastReceiver newIMMessageReceiver;

    private void initBroadcastReceiver() {
	tvMessageTips = (TextView) findViewById(R.id.tv_message_tips);

	// 私信 广播接收器,收集收到的私信消息数量, 令消息提示可见
	privateMessageReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		// 私信消息
		if (MessagePollService.ACTION_PRIVATE_MESSAGE.equals(action)) {
		    /// 根据消息的类型来处理
		    int msgType = intent.getIntExtra("message_type", -1);
		    if (msgType == MessageType.VISITOR.ordinal()) { // 新访客消息
			findViewById(R.id.tv_vistors_message_tips).setVisibility(View.VISIBLE);
		    } else if (msgType == MessageType.SAYHI.ordinal()) { // 新打招呼
			findViewById(R.id.tv_sayhi_message_tips).setVisibility(View.VISIBLE);
		    } else if (msgType == MessageType.FOLLOW.ordinal()) { //新关注消息
			findViewById(R.id.tv_newfans_message_tips).setVisibility(View.VISIBLE);
		    }

		    // 声音提示
		    SoundPlayer.getInstance(MainActivity.this).playNotify();

		    queryPrivateMessages(); // 收到通知即查询数据库
		    // 被隐藏则显示消息提示
		    //		    if (messageFragment.isHidden()) {
		    //			tvMessageTips.setVisibility(View.VISIBLE);
		    //		    }
		}

		// 系统消息
		else if (MessagePollService.ACTION_SYSTEM_MESSAGE.equals(action)) {

		}

		else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 屏幕关闭后 降低刷新频率(增大间隔时间)
		}
	    }
	};

	IntentFilter intentFilter = new IntentFilter();
	intentFilter.addAction(MessagePollService.ACTION_PRIVATE_MESSAGE);
	intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
	intentFilter.setPriority(2147483647); // 设为最高优先级
	registerReceiver(privateMessageReceiver, intentFilter);

	// 消息接受通知
	IntentFilter iFilter = new IntentFilter();
	iFilter.addAction(ACTION_IM_RECEIVE_MESSAGE);
	iFilter.setPriority(2147483647);
	newIMMessageReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		showToast(intent.getStringExtra("content"));
		//		RongIM.getInstance().startConversationList(MainActivity.this);
	    }
	};

	// 注册
	registerReceiver(newIMMessageReceiver, iFilter);

	// 默认先查一次数据库
	queryPrivateMessages();

    }

    private void initTabs() {

	mTvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	mTvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	mTabs = new Button[5];
	mTabs[0] = (Button) findViewById(R.id.btn_set); // 个人
	//	mTabs[1] = (Button) findViewById(R.id.btn_contact); // 联系人
	mTabs[1] = (Button) findViewById(R.id.btn_share); // 社区
	//	mTabs[3] = (Button) findViewById(R.id.btn_message); // 消息
	mTabs[2] = (Button) findViewById(R.id.btn_nearby); // 发现
	//	mTabs[3] = (Button) findViewById(R.id.btn_message); // 消息
	mTabs[3] = (Button) findViewById(R.id.btn_userwall); // 推荐vip用户墙
	mTabs[4] = (Button) findViewById(R.id.btn_strangenews); // 

	personalFragment = new PersonalFragment();
	//	conversationsFragment = new ConversationFragment();
	shareFragment = new CommunityFragment(); // 分享
	//	messageFragment = new PrivateMessageFragment();
	nearPeopleFragment = new NearbyFragment();

	// VIP用户墙
	userWallFragment = new UserWallFragment();

	strangenewsFragment = new StrangeNewsAndIntriguingStoryFragment();

	fragments = new Fragment[] { personalFragment, /*conversationsFragment,*/shareFragment, /*messageFragment,*/nearPeopleFragment, userWallFragment, strangenewsFragment };

	mTabs[0].setSelected(true);
	mTvTopTitle.setText("我");
	mTvTopAction.setVisibility(View.GONE);

	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

	// 启动时只添加首页的 fragement, 其他页面需要时再add
	transaction.add(R.id.fragment_container, personalFragment);
	// 为了接收消息 所以得先创建
	//	transaction.add(R.id.fragment_container, messageFragment);
	transaction.add(R.id.fragment_container, shareFragment);
	transaction.add(R.id.fragment_container, userWallFragment);
	transaction.add(R.id.fragment_container, nearPeopleFragment);
	transaction.add(R.id.fragment_container, strangenewsFragment);

	transaction.show(personalFragment);
	transaction.hide(userWallFragment);
	transaction.hide(shareFragment);
	transaction.hide(nearPeopleFragment);
	transaction.hide(strangenewsFragment);

	transaction.commit();
    }

    private int currentIndex;

    public void onTabSelect(View v) {
	int index = 0;
	switch (v.getId()) {
	case R.id.btn_set:
	    index = 0;
	    mTvTopTitle.setText(R.string.main_tab_set);
	    mTvTopAction.setVisibility(View.GONE);
	    break;

	case R.id.btn_share:
	    index = 1;
	    mTvTopTitle.setText(R.string.main_tab_share);
	    //	    mTvTopAction.setText("++");
	    mTvTopAction.setVisibility(View.VISIBLE);
	    break;

	case R.id.btn_nearby:
	    index = 2;
	    mTvTopTitle.setText(R.string.main_tab_nearby);
	    mTvTopAction.setText("筛选");
	    mTvTopAction.setVisibility(View.VISIBLE);
	    break;
	case R.id.btn_userwall: // vip会员墙 ,速配
	    index = 3;
	    mTvTopTitle.setText(R.string.main_tab_userwall);
	    mTvTopAction.setVisibility(View.GONE);
	    break;
	case R.id.btn_strangenews: // 奇闻
	    index = 4;
	    mTvTopTitle.setText(R.string.main_tab_strangenews);
	    mTvTopAction.setVisibility(View.GONE);
	    break;
	}

	if (currentIndex != index) {
	    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
	    trans.hide(fragments[currentIndex]);
	    if (!fragments[index].isAdded()) {
		trans.add(R.id.fragment_container, fragments[index]);
	    }
	    trans.show(fragments[index]).commit();
	}

	mTabs[currentIndex].setSelected(false);
	currentIndex = index;
	mTabs[currentIndex].setSelected(true);
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (firstTime + 2000 > System.currentTimeMillis()) {
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    //	    super.onBackPressed();
	} else {
	    showToast("再按一次返回HOME");
	}
	firstTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
	unregisterReceiver(privateMessageReceiver);
	unregisterReceiver(newIMMessageReceiver);

	// 停止service
	stopServices();

	// 销毁 广告
	net.youmi.android.offers.OffersManager.getInstance(this).onAppExit();

	net.youmi.android.offers.PointsManager.getInstance(this).unRegisterPointsEarnNotify(pointsEarnNotify);

	net.youmi.android.offers.PointsManager.getInstance(this).unRegisterNotify(pointsChangeNotify);

	super.onDestroy();
    }

    View popupView;
    TextView tvSayhiTitle, tvSayhiContent, tvSayhiClose;
    ImageView ivSayhiUserFace;

    /**
     * 收到招呼时的弹窗
     */
    private void initPopupView() {
	// 弹窗 view
	popupView = findViewById(R.id.popup_sayhi);
	ivSayhiUserFace = (ImageView) popupView.findViewById(R.id.iv_user_face);
	tvSayhiTitle = (TextView) popupView.findViewById(R.id.tv_sayhi_title);
	tvSayhiContent = (TextView) popupView.findViewById(R.id.tv_sayhi_content);
	tvSayhiClose = (TextView) popupView.findViewById(R.id.tv_sayhi_close);
	tvSayhiClose.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		hidePopupSayhi();
	    }
	});
    }

    /**
     * 展示弹窗
     */
    private void showPopupSayhi(final int id, final String icon, String content, final String userId, final String nickname) {
	// 显示弹窗
	tvSayhiContent.setText(content);
	tvSayhiTitle.setText(nickname);
	ImageLoader.getInstance().displayImage(icon, ivSayhiUserFace, ImageLoaderOptions.normalOptions());

	this.popupView.setVisibility(View.VISIBLE);
	// 渐入动画
	AnimationSet animationSet = new AnimationSet(false);
	TranslateAnimation transAnimation = new TranslateAnimation(0, 0, 200, 0);
	transAnimation.setDuration(300);
	transAnimation.setInterpolator(new AccelerateInterpolator());
	animationSet.addAnimation(transAnimation);
	popupView.startAnimation(animationSet);

	// 渐显动画结束后自动隐藏
	animationSet.setAnimationListener(new AnimationListener() {

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
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {

		    @Override
		    public void run() {
			// TODO Auto-generated method stub
			if (popupView.getVisibility() == View.VISIBLE) {
			    hidePopupSayhi();
			}

		    }
		}, 1000 * 10);
	    }
	});

	this.popupView.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 隐藏弹窗
		hidePopupSayhi(); //

		// 设为已读
		DBHelper dbHelper = DBHelper.getInstance(MainActivity.this, DBHelper.DB_NAME, null, 1);
		dbHelper.updateMessageToReadedById(id);

		// 查看该用户的资料
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, UserInfoTextActivity.class);
		intent.putExtra("user_id", Integer.parseInt(userId)); // ID
		intent.putExtra("user_nickname", nickname); // 昵称
		intent.putExtra("user_face_url", icon); //用户头像

		startActivity(intent); // 启动 activity
	    }
	});

    }

    /**
     * 隐藏弹窗
     */
    private void hidePopupSayhi() {
	this.popupView.setVisibility(View.GONE);
	// 渐入动画
	AnimationSet animationSet = new AnimationSet(false);
	TranslateAnimation transAnimation = new TranslateAnimation(0, 0, 0, 500);
	transAnimation.setDuration(500);
	transAnimation.setInterpolator(new AccelerateInterpolator());
	AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.8f);
	alphaAnimation.setDuration(500);

	animationSet.addAnimation(transAnimation);
	animationSet.addAnimation(alphaAnimation);
	popupView.startAnimation(animationSet);

	//	animationSet.setAnimationListener(new AnimationListener() {
	//	    
	//	    @Override
	//	    public void onAnimationStart(Animation animation) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	    
	//	    @Override
	//	    public void onAnimationRepeat(Animation animation) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	    
	//	    @Override
	//	    public void onAnimationEnd(Animation animation) {
	//		// TODO Auto-generated method stub
	//		popupView.setVisibility(View.GONE);
	//	    }
	//	});
    }

    /**
     * 查询新消息
     */
    private void queryPrivateMessages() {

	// 查询本地数据库
	DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);
	List<PrivateMessage> msges = dbHelper.selectAllMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	//	List<PrivateMessage> msges = dbHelper.selectUnreadMessages(MyApplication.getInstance().getCurrentLoginedUser().getUserId());

	for (PrivateMessage msg : msges) {

	    if (msg.getType() == MessageType.SAYHI) {
		//findViewById(R.id.tv_sayhi_message_tips).setVisibility(View.VISIBLE);
		// 展示 打招呼 弹窗
		if (msg.getTitle() == null || !msg.getTitle().contains("=_=")) {
		    //		showToast("null" + msg.getTitle());
		    System.out.println("hi title:" + msg.getTitle());
		} else {
		    String[] nameAndId = msg.getTitle().split("=_=");
		    if (!msg.isReaded()) { // 未读则展示
			showPopupSayhi(msg.getId(), msg.getIcon(), msg.getContent(), nameAndId[1], nameAndId[0]);
		    }
		}

		// 测试全屏动画
		try { // 显示 动画
		    showGif(R.raw.gif01);
		} catch (Throwable th) {
		    th.printStackTrace();
		}

	    }
	}
    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	readAndSetTopBackgroundColor();
	int bottomColor = readTopBackgroundColor();

	findViewById(R.id.layout_main_bottom).setBackgroundColor(bottomColor);
	findViewById(R.id.btn_set).setBackgroundColor(bottomColor);
	findViewById(R.id.btn_share).setBackgroundColor(bottomColor);
	findViewById(R.id.btn_nearby).setBackgroundColor(bottomColor);
	findViewById(R.id.btn_userwall).setBackgroundColor(bottomColor);
	findViewById(R.id.btn_strangenews).setBackgroundColor(bottomColor);
	
	
	super.onResume();
    }

    /**
     * 初始化 gif 窗口
     */
    private View mFloatView;
    private GifView mGifView;

    private void initGifWindow() {
	WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	params.height = -1;
	params.width = -1;
	params.format = 1;
	params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
	params.type = WindowManager.LayoutParams.TYPE_PHONE;

	mFloatView = getLayoutInflater().inflate(R.layout.layout_gif, null);
	wm.addView(mFloatView, params);
	mFloatView.setVisibility(View.GONE);//初始状态为隐藏

	mGifView = (GifView) mFloatView.findViewById(R.id.gifview);
	mGifView.setGifImageType(GifImageType.COVER);
	//	mGifView.setShowDimension(ActivityUtil.getScreenSize()[0] / 2, ActivityUtil.getScreenSize()[1] / 2);
	// 点击隐藏
	mFloatView.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		mFloatView.setVisibility(View.GONE);
	    }
	});

    }

    private Handler mHandler = new Handler();
    private Runnable hideRunnable = new Runnable() {

	@Override
	public void run() {
	    // TODO Auto-generated method stub
	    mFloatView.setVisibility(View.GONE);
	}
    };

    /**
     * show gif
     * @param resId
     */
    private void showGif(int resId) {
	mGifView.setGifImage(resId);
	mFloatView.setVisibility(View.VISIBLE);
	// 延时隐藏
	//	mHandler.removeCallbacks(hideRunnable);
	//	mHandler.postDelayed(hideRunnable, 5000);
    }

    private int tryReconnectCount = 3; // 最大失败尝试次数

    View mLoadingView;

    /**
     * 连接到融云IM
     * @param userId
     */
    private void connectRongCloudIM(final String userId) {
	//	final ProgressDialog progress = new ProgressDialog(MainActivity.this);
	//	progress.setMessage("连接IM...");
	//	progress.setTitle("");
	//	progress.show();
	if (mLoadingView == null) {
	    mLoadingView = getLayoutInflater().inflate(R.layout.layout_loading_retry, null);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_IN_PARENT);
	    mContentView.addView(mLoadingView, params);

	    // 重试
	    mLoadingView.findViewById(R.id.tv_load_retry).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    tryReconnectCount = 3;
		    connectRongCloudIM(userId);

		}
	    });
	}

	// 可见
	mLoadingView.setVisibility(View.VISIBLE);
	// 重试按钮隐藏
	mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.GONE);

	AppServiceRongCloudIMImpl.getInstance().getTokenFromServer(userId, new OnTokenResponseListener() {

	    @Override
	    public void onSuccess(String token) {
		// TODO Auto-generated method stub
		AppServiceRongCloudIMImpl.getInstance().connect(MainActivity.this, token, new ConnectCallback() {

		    @Override
		    public void onError(ErrorCode arg0) {
			// TODO Auto-generated method stub

			switch (arg0) {
			case TOKEN_INCORRECT:
			    if (tryReconnectCount-- >= 0) {
				connectRongCloudIM(userId);
				ToastFactory.getToast(MainActivity.this, "token 不正确,重试中(" + tryReconnectCount + ")").show();
			    } else {
				ToastFactory.getToast(MainActivity.this, "token 错误").show();
				// 登录失败则进入 登录页面
				// 隐藏
				mLoadingView.setVisibility(View.GONE);
			    }
			    break;
			case TIMEOUT:
			    if (tryReconnectCount-- >= 0) {
				connectRongCloudIM(userId);
				ToastFactory.getToast(MainActivity.this, "登录IM超时,重试中(" + tryReconnectCount + ")").show();
			    } else {
				// 登录失败则进入 登录页面
				// 隐藏
				mLoadingView.setVisibility(View.GONE);
			    }
			    break;
			case APP_KEY_UNAVAILABLE:
			    showToast("IM appKey不可用");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case PACKAGE_BROKEN:
			    showToast("IM package Broken");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case DATABASE_ERROR:
			    showToast("数据库出错");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case SERVER_UNAVAILABLE:
			    showToast("IM服务器不可用!");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case UNKNOWN:
			    showToast("IM 未知错误");
			    mLoadingView.setVisibility(View.GONE);
			    break;
			}
		    }

		    @Override
		    public void onSuccess(String arg0) {
			// TODO Auto-generated method stub
			// 刷新用户资料
			// 隐藏
			mLoadingView.setVisibility(View.GONE);
			AppServiceRongCloudIMImpl.getInstance().refreshUserInfo();

		    }
		});
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(MainActivity.this, "获取token失败,请重试").show();
		// 重试按钮可见
		mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
	    }
	});
    }
}
