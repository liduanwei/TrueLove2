package me.himi.love;

import io.rong.imkit.RongIM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.himi.love.IAppService.CheckUpdateParams;
import me.himi.love.IAppService.OnCheckUpdateListener;
import me.himi.love.dao.DBHelper;
import me.himi.love.dao.DBHelper.User;
import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.entity.LoginedUser;
import me.himi.love.ui.CheckUpdateActivity.OnUpdateListener;
import me.himi.love.ui.FirstGuideActivity;
import me.himi.love.ui.SplashActivity;
import me.himi.love.util.ActivityManagerUtils;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.CacheUserManager;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.PreferencesUtil;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * @ClassName:MyApplication
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:08:33 PM
 */
public class MyApplication extends Application {
    public static MyApplication instance;

    private LoginedUser currentLoginedUser;

    static final long CHECK_UPDATE_DELAY = 1000 * 60 * 60 * 24 * 3;

    /** 标识是否需要退出。为true时表示当前的Activity要执行finish()。 */
    private boolean need2Exit;

    @Override
    public void onCreate() {
	super.onCreate();
	instance = this;
	// 标识为 非退出
	need2Exit = false;

	initImageLoader();
	//initBmobIM();
	initRongCloudIM();

	initBaidu(); // 初始化百度地图


	// 创建快捷方式
	if (!"1".equals(PreferencesUtil.read(this, "shortcut_added"))) {
	    ActivityUtil.addShortCut(this, FirstGuideActivity.class);
	    PreferencesUtil.save(this, "shortcut_added", "1");
	}

	// 处理崩溃并重启应用
	//	Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	//
	//	    @Override
	//	    public void uncaughtException(Thread thread, final Throwable ex) {
	//		// TODO Auto-generated method stub
	//		Log.e("FATAL ERRORTrueLove2", ex.getMessage());
	//		//正确重启 注意下面两个 必须 (重启白屏中)
	//		Intent intent = new Intent(); // 无参数实例化(必须)
	//		// setClassName 必须
	//		intent.setClassName("me.himi.love", "me.himi.love.ui.MainActivity");
	//		final PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
	//
	//		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	//		// 3秒后重启
	//		alarmMgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000L, restartIntent);
	//
	//		// 以下代码都无效
	//		//		android.os.Process.killProcess(android.os.Process.myPid());
	//		ActivityManagerUtils.getInstance().removeAllActivity();
	//		//			startActivity(intent);
	//
	//		new Thread() {
	//		    @Override
	//		    public void run() {
	//			String crashFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/crash/crash" + System.currentTimeMillis();
	//			File crashFile = new File(crashFilePath);
	//			if (!crashFile.getParentFile().exists()) {
	//			    crashFile.getParentFile().mkdirs();
	//			}
	//			try {
	//			    PrintStream ps = new PrintStream(crashFile);
	//			    ex.printStackTrace(ps);
	//			    ps.close();
	//			} catch (FileNotFoundException e) {
	//			    // TODO Auto-generated catch block
	//			    e.printStackTrace();
	//			}
	//
	//		    };
	//		}.start();
	//		ex.printStackTrace();
	//
	////		new Thread() {
	////		    public void run() {
	////			Looper.prepare();
	////			Toast.makeText(getApplicationContext(), "程序异常,重启中...", Toast.LENGTH_SHORT).show();
	////			Looper.loop();
	////		    };
	////		}.start();
	//
	//	    }
	//	});
    }

    public static MyApplication getInstance() {
	return instance;
    }

    /**
     * 
     */
    //    private void checkUpdate() {
    //	CheckUpdateParams params = new CheckUpdateParams();
    //	params.currentVersion = ActivityUtil.getVersionCode(this);
    //
    //	AppServiceImpl.getInstance().checkUpdate(params, new OnCheckUpdateListener() {
    //
    //	    @Override
    //	    public void onSuccess(final CheckUpdateVersion v) {
    //		SharedPreferences pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    //		pref.edit().putLong("last_checkupdate_time", System.currentTimeMillis()).commit();
    //
    //		if (null != v) {
    //		    downloadAndUpdate(v, new OnUpdateListener() {
    //
    //			@Override
    //			public void onSuccess(final String file) {
    //			    // TODO Auto-generated method stub
    //			    final Builder builder = new AlertDialog.Builder(getApplicationContext());
    //			    builder.setTitle("新版本提示");
    //			    builder.setMessage("有新版本可以使用了,版本号:" + v.getVersionName() + ",更新内容如下:\n" + v.getUpdateInstruction());
    //
    //			    builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
    //
    //				@Override
    //				public void onClick(DialogInterface dialog, int which) {
    //				    ActivityUtil.installApk(getApplicationContext(), new File(file));
    //				}
    //			    });
    //
    //			    builder.setNegativeButton("取消", null);
    //			    builder.show();
    //			}
    //
    //			@Override
    //			public void onFailure(String msg) {
    //			    // TODO Auto-generated method stub
    //
    //			}
    //
    //			@Override
    //			public void onDownloading(int contentLength, int currentLength) {
    //			    // TODO Auto-generated method stub
    //
    //			}
    //		    });
    //
    //		    //		    ToastFactory.getToast(instance, "有新版本可以用了").show();
    //
    //		    // 通知更新
    //		    //定义一个PendingIntent，当用户点击通知时，跳转到某个Activity(也可以发送广播等)  
    //		    //		    Intent intent = new Intent(MyApplication.this, CheckUpdateActivity.class);
    //		    //
    //		    //		    Bundle bundle = new Bundle();
    //		    //		    bundle.putSerializable("updateVersion", v);
    //		    //		    intent.putExtras(bundle);
    //		    //		    PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.this, 0, intent, 0);
    //		    //
    //		    //		    //创建一个通知  
    //		    //		    Notification notification = new Notification(R.drawable.icon, "有新版本可以更新了~~", System.currentTimeMillis());
    //		    //		    notification.setLatestEventInfo(MyApplication.this, "点击更新", "点击下载安装包并更新", pendingIntent);
    //		    //
    //		    //		    //用NotificationManager的notify方法通知用户生成标题栏消息通知  
    //		    //		    NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    //		    //		    nManager.notify(103, notification);//id是应用中通知的唯一标识  
    //		    //如果拥有相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。  
    //		}
    //	    }
    //
    //	    @Override
    //	    public void onFailure(String errormsg) {
    //		// TODO Auto-generated method stub
    //
    //	    }
    //
    //	    public void downloadAndUpdate(final CheckUpdateVersion checkUpdateVersion, final OnUpdateListener listener) {
    //		if (checkUpdateVersion == null) {
    //		    return;
    //		}
    //		final Handler handler = new Handler() {
    //		    @Override
    //		    public void handleMessage(Message msg) {
    //			switch (msg.what) {
    //			case 0:
    //			    listener.onDownloading(msg.arg1, msg.arg2);
    //			    break;
    //			case 1:
    //			    listener.onSuccess(msg.obj.toString());
    //			    break;
    //			case 2:
    //			    listener.onFailure(msg.obj.toString());
    //			    break;
    //			}
    //			super.handleMessage(msg);
    //		    }
    //		};
    //
    //		new Thread() {
    //		    @Override
    //		    public void run() {
    //			try {
    //			    HttpURLConnection conn = (HttpURLConnection) new URL(checkUpdateVersion.getDownloadUrl()).openConnection();
    //			    InputStream in = conn.getInputStream();
    //			    int contentLength = conn.getContentLength();
    //
    //			    int currentLength = 0;
    //			    byte[] buffer = new byte[1024 << 1];
    //			    int len = 0;
    //
    //			    String tmpDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/truelove/";
    //			    File file = new File(tmpDir + "me.himi.love" + System.currentTimeMillis() + ".apk");
    //			    if (!file.getParentFile().exists()) {
    //				file.getParentFile().mkdirs();
    //			    }
    //
    //			    FileOutputStream fos = new FileOutputStream(file);
    //
    //			    while ((len = in.read(buffer)) != -1) {
    //				fos.write(buffer, 0, len);
    //				currentLength += len;
    //				handler.obtainMessage(0, contentLength, currentLength).sendToTarget();
    //			    }
    //			    fos.flush();
    //			    fos.close();
    //			    in.close();
    //			    // 下载完成
    //			    handler.obtainMessage(1, file.getAbsolutePath()).sendToTarget();
    //			    return;
    //			} catch (MalformedURLException e) {
    //			    // TODO Auto-generated catch block
    //			    e.printStackTrace();
    //			} catch (IOException e) {
    //			    // TODO Auto-generated catch block
    //			    e.printStackTrace();
    //			}
    //
    //			handler.obtainMessage(2, "下载失败,可能会是网络问题").sendToTarget();
    //		    };
    //		}.start();
    //
    //	    }
    //	});
    //    }

    /**
     * 初始化imageLoader
     */
    public void initImageLoader() {
	//	DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)//图片存本地
	//		.cacheInMemory(true).displayer(new FadeInBitmapDisplayer(50)).bitmapConfig(Bitmap.Config.RGB_565)/*.displayer(new RoundedBitmapDisplayer(5))*/.imageScaleType(ImageScaleType.EXACTLY) // default
	//		.build();

	File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
	ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).memoryCache(new LruMemoryCache(5 * 1024 * 1024)).memoryCacheSize(10 * 1024 * 1024).diskCache(new UnlimitedDiscCache(cacheDir)).diskCacheFileNameGenerator(new HashCodeFileNameGenerator()).defaultDisplayImageOptions(ImageLoaderOptions.normalOptions()).build();

	ImageLoader.getInstance().init(config);

	// AsyncHttpClient 使用 cookie ,存储到 应用 Perference 目录下的xml文件中
	//	PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
	HttpUtil.setCookieStore(this);
    }

    public Activity getTopActivity() {
	return ActivityManagerUtils.getInstance().getTopActivity();
    }

    public void addActivity(Activity ac) {
	ActivityManagerUtils.getInstance().addActivity(ac);
    }

    public void finish(Activity ac) {
	ActivityManagerUtils.getInstance().removeActivity(ac);
    }

    public void exit() {
	ActivityManagerUtils.getInstance().removeAllActivity();
	//	android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 初始化融云IM
     */
    private void initRongCloudIM() {

	//List<Conversation> conversations = RongIM.getInstance().getConversationList();
	RongIM.init(this, "6tnym1brn8ys7", R.drawable.icon);
    }

    LocationClient mLocationClient;
    MyLocationListener mMyLocationListener;
    public static double[] lastPoint = new double[2];// 上一次定位到的经纬度

    /**
     * 初始化百度相关sdk initBaidumap
     * @Title: initBaidumap
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initBaidu() {
	// 初始化地图Sdk
	SDKInitializer.initialize(this);
	// 初始化定位sdk
	initBaiduLocClient();
    }

    /**
     * 初始化百度定位sdk
     * @Title: initBaiduLocClient
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initBaiduLocClient() {
	mLocationClient = new LocationClient(this);
	mMyLocationListener = new MyLocationListener();

	LocationClientOption option = new LocationClientOption();
	option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
	option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
	option.setIsNeedAddress(true);//返回的定位结果包含地址信息
	option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
	mLocationClient.setLocOption(option);

	mLocationClient.registerLocationListener(mMyLocationListener);

	//开始定位SDK
	mLocationClient.start();
    }

    public final String PREF_LONGTITUDE = "longtitude";// 经度
    private String longtitude = "";

    /**
     * 获取经度
     * 
     * @return
     */
    public String getLongtitude() {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	longtitude = preferences.getString(PREF_LONGTITUDE, "");
	return longtitude;
    }

    /**
     * 设置经度
     * 
     * @param pwd
     */
    public void setLongtitude(String lon) {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor editor = preferences.edit();
	if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
	    longtitude = lon;
	}
    }

    public final String PREF_LATITUDE = "latitude";// 经度
    private String latitude = "";

    /**
     * 获取纬度
     * 
     * @return
     */
    public String getLatitude() {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	latitude = preferences.getString(PREF_LATITUDE, "");
	return latitude;
    }

    /**
     * 设置纬度
     * 
     * @param pwd
     */
    public void setLatitude(String lat) {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor editor = preferences.edit();
	if (editor.putString(PREF_LATITUDE, lat).commit()) {
	    latitude = lat;
	}
    }

    public final String PREF_ADDR = "addr"; //实际地址

    /**
     * 保存地址
     * @param addr
     */
    public void setAddr(String addr) {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor editor = preferences.edit();
	editor.putString(PREF_ADDR, addr).commit();
    }

    /**
     * 获取地址
     * @return
     */
    public String getAddr() {
	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	return preferences.getString(PREF_ADDR, "");
    }

    /**
     * 实现定位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

	@Override
	public void onReceiveLocation(BDLocation location) {
	    // Receive Location
	    double latitude = location.getLatitude();
	    double longtitude = location.getLongitude();

	    System.out.println("接收到的经度:" + longtitude + ",纬度:" + latitude);
	    //	    if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
	    setAddr(location.getAddrStr()); // 保存
	    //ToastFactory.getToast(MyApplication.this, "addr:" + location.getAddrStr() + "lat:" + latitude + ",lng:" + longtitude).show();
	    //	    }

	    if (lastPoint != null) {
		if (lastPoint[0] == location.getLatitude() && lastPoint[1] == location.getLongitude()) {
		    // 若两次请求获取到的地理位置坐标是相同的则不再定位
		    // 停止
		    mLocationClient.stop();
		    return;
		}
	    }

	    //	    if ("".equals(getLongtitude()) || "".equals(getLatitude())) {
	    //		// 保存到配置中
	    setLongtitude(longtitude + "");
	    setLatitude(latitude + "");
	    //	    }

	    lastPoint[0] = latitude;
	    lastPoint[1] = longtitude;
	}
    }

    //IM所需  ---end;

    public LoginedUser getCurrentLoginedUser() {
	if (currentLoginedUser == null) {
	    currentLoginedUser = new LoginedUser();
	    DBHelper dbHelper = DBHelper.getInstance(this, DBHelper.DB_NAME, null, 1);
	    User user = dbHelper.queryLastLoginUser();
	    currentLoginedUser.setUserId(user.userId);
	}
	return this.currentLoginedUser;
    }

    public void setCurrentLoginedUser(LoginedUser user) {
	this.currentLoginedUser = user;
    }

    /**
     * 退出登录,情况缓存数据
     */
    public void logout() {
	this.currentLoginedUser = null;
	CacheUserManager.getInstance().clearAllCache();
	// 退出im
	if (RongIM.getInstance() != null) {
	    RongIM.getInstance().disconnect(false);
	}
    }

}
