package me.himi.love.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.CheckUpdateParams;
import me.himi.love.IAppService.OnCheckUpdateListener;
import me.himi.love.IAppService.OnLogoutListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.RongIMEvent;
import me.himi.love.boost.androidservice.FloatShareService;
import me.himi.love.boost.androidservice.MessagePollService;
import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityManagerUtils;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.StringUtils;
import me.himi.love.view.city.FileUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wanpu.pay.PayConnect;
import com.wanpu.pay.PayResultListener;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class SettingsActivity extends BaseActivity implements OnClickListener {

    TextView mTvCacheSize; // 缓存大小

    public static final String USE_VIBRATOR_WHEN_NEWMSG = "use_vibrator_when_newmsg";

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_settings);

	init();

    }

    private void init() {

	readAndSetTopBackgroundColor();

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("设置");

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("");
	tvTopAction.setVisibility(View.GONE);

	TextView tvCurrentVersion = getViewById(R.id.tv_current_version); // 当前版本
	tvCurrentVersion.setText("v" + ActivityUtil.getVersionName(this));

	// 立即检查更新
	getViewById(R.id.layout_checkupdate).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		checkUpdate();
	    }

	    private void checkUpdate() {
		// TODO Auto-generated method stub
		final AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this).create();
		dialog.setMessage("");
		dialog.setTitle("检查新版本中,请稍候...");
		dialog.show();

		CheckUpdateParams params = new CheckUpdateParams();
		params.currentVersion = ActivityUtil.getVersionCode(SettingsActivity.this);

		AppServiceImpl.getInstance().checkUpdate(params, new OnCheckUpdateListener() {

		    @Override
		    public void onSuccess(CheckUpdateVersion v) {
			// TODO Auto-generated method stub
			dialog.dismiss();

			if (v != null) {
			    //			    Intent intent = new Intent(SettingsActivity.this, CheckUpdateActivity.class);
			    //			    Bundle bundle = new Bundle();
			    //			    bundle.putSerializable("updateVersion", v);
			    //			    intent.putExtras(bundle);
			    //			    startActivity(intent);
			    mUpdateVersion = v;
			    showDownloadDialog();
			} else {
			    showToast("当前已是最新版本");
			}
		    }

		    @Override
		    public void onFailure(String errormsg) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			showToast("连接超时");
		    }
		});
	    };
	});

	// 配置颜色
	findViewById(R.id.layout_settings_color).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SettingsActivity.this, ColorSettingsActivity.class);
		startActivity(intent);
	    }
	});
	final SharedPreferences sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
	final CheckBox cbVibrator = (CheckBox) findViewById(R.id.cb_vibrator_switch);
	cbVibrator.setChecked(sp.getBoolean(USE_VIBRATOR_WHEN_NEWMSG, true));
	// 新提醒时震动
	findViewById(R.id.layout_settings_vibrator).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		cbVibrator.setChecked(!cbVibrator.isChecked());
		sp.edit().putBoolean(USE_VIBRATOR_WHEN_NEWMSG, cbVibrator.isChecked()).commit();
	    }
	});
	

	// 捐助我
	findViewById(R.id.layout_offerme).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		//		Intent intent = new Intent(SettingsActivity.this, OffermeActivity.class);
		//		startActivity(intent);
		View view = findViewById(R.id.layout_offerme_container);

		if (view.getVisibility() == View.VISIBLE) {
		    view.setVisibility(View.GONE);
		    findViewById(R.id.tv_offerme_label).setVisibility(View.VISIBLE);
		} else {
		    view.setVisibility(View.VISIBLE);
		    findViewById(R.id.tv_offerme_label).setVisibility(View.GONE);
		}
	    }
	});

	// 默认隐藏支付输入框
	findViewById(R.id.layout_offerme_container).setVisibility(View.GONE);

	// 输入捐助金额
	final EditText etOffermeMoney = getViewById(R.id.et_offerme_money);
	// 进入支付
	final TextView tvEnterPay = getViewById(R.id.tv_enter_offerme);
	// 取消支付
	final TextView tvEnterPayCancle = getViewById(R.id.tv_enter_offerme_cancle);
	tvEnterPayCancle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		findViewById(R.id.layout_offerme_container).setVisibility(View.GONE);
		findViewById(R.id.tv_offerme_label).setVisibility(View.VISIBLE);
	    }
	});

	etOffermeMoney.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (etOffermeMoney.getText().toString().trim().length() != 0) {
		    tvEnterPay.setVisibility(View.VISIBLE);
		} else {
		    tvEnterPay.setVisibility(View.GONE);
		}
	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	    }
	});

	tvEnterPay.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (etOffermeMoney.getText().toString().trim().length() == 0) {
		    showToast("请输入捐助金额");
		    return;
		}

		// TODO Auto-generated method stub
		PayConnect.getInstance("8025cff144f83cf6252bd92eaf0ce588", "WAPS", SettingsActivity.this);
		String orderId = "truelove2" + StringUtils.md5(System.currentTimeMillis() + "");
		// 用户标识
		String userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "";
		// 支付商品名称
		String goodsName = "捐助 恋恋";
		// 支付金额
		float price = Float.parseFloat(etOffermeMoney.getText().toString()) * 1.0f;
		// 支付时间
		String time = "";
		// 支付描述
		String goodsDesc = "捐助 恋恋";
		// 应用或游戏商服务器端回调接口（无服务器可不填写）
		String notifyUrl = Constants.URL_VIP_NOTIFY;

		PayConnect.getInstance(SettingsActivity.this).pay(SettingsActivity.this, orderId, userId, price, goodsName, goodsDesc, notifyUrl, new PayResultListener() {

		    @Override
		    public void onPayFinish(Context payViewContext, String orderId, int resultCode, String resultString, int payType, float amount, String goodsName) {
			// TODO Auto-generated method stub
			//			showToast(resultString);
			if (resultCode == 0) {
			    showToast("捐助成功,感谢您的大力支持!");
			} else {
			    showToast("支付失败," + resultString + "请再试一次");
			}
		    }
		});
	    }
	});

	// 帮助说明
	findViewById(R.id.layout_help_instructions).setOnClickListener(this);

	// 清除缓存
	findViewById(R.id.layout_cache_clear).setOnClickListener(this);

	// 缓存文件size
	mTvCacheSize = getViewById(R.id.tv_cache_size);

	mTvCacheSize.setText(String.format("%.2f", (FileUtil.getDirSize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2") / 1024.0f / 1024)) + "mb");

	// 退出登录
	Button btnLogout = (Button) findViewById(R.id.btn_logout);
	btnLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.btn_logout: // 退出登录
	    logout();
	    break;
	case R.id.layout_help_instructions: // 帮助说明
	    Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
	    startActivity(intent);
	    break;

	case R.id.layout_cache_clear:
	    boolean isDeleted = FileUtil.deleteDirFromSD(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2");
	    if (isDeleted) {
		showToast("缓存已清除");
		//	    mTvCacheSize.setText((int) (FileUtil.getDirSize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2") / 1024.0f) + "kb");
		mTvCacheSize.setText(String.format("%.2f", (FileUtil.getDirSize(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2") / 1024.0f / 1024)) + "mb");
	    }
	    break;
	}
    }

    private void logout() {

	//	if (!ActivityUtil.hasNetwork(this)) {
	//	    ToastFactory.getToast(this, "请开启网络").show();
	//	    return;
	//	}

	//	final ProgressDialog progress = new ProgressDialog(this);
	//	progress.setMessage("正在登出...");
	//	progress.setCanceledOnTouchOutside(false);
	//	progress.show();
	//
	//	progress.cancel();

	// 结束所有回退栈中的Activity
	ActivityManagerUtils.getInstance().removeAllActivity();

	// 销毁实例
	RongIMEvent.getInstance(SettingsActivity.this).logout();

	// 结束所有service
	stopAllService();

	// 进入登录页
	startActivity(new Intent(SettingsActivity.this, UserLoginActivity.class));

	// 销毁所有内存对象缓存
	MyApplication.getInstance().logout();

	AppServiceImpl.getInstance().logout(new OnLogoutListener() {

	    @Override
	    public void onSuccess() {
		// TODO Auto-generated method stub
		//		progress.cancel();
		//
		//		// 结束所有回退栈中的Activity
		//		ActivityManagerUtils.getInstance().removeAllActivity();
		//
		//		// 进入登录页
		//		Intent intent = new Intent();
		//		intent.setClass(SettingsActivity.this, UserLoginActivity.class);
		//		startActivity(intent);
		//
		//		// 销毁所有内存对象缓存
		//		MyApplication.getInstance().logout();
		//
		//		// 退出 IM, 销毁内存
		//		MyApplication.getInstance().imLogout();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		//		ToastFactory.getToast(SettingsActivity.this, "退出失败了," + errorMsg).show();
		//		progress.cancel();
	    }
	});
    }

    private void stopAllService() {
	// TODO Auto-generated method stub
	// 消息轮询
	stopService(new Intent(this, MessagePollService.class));
	// 悬浮按钮
	stopService(new Intent(this, FloatShareService.class));
    }

    private CheckUpdateVersion mUpdateVersion;

    private void showDownloadDialog() {
	// TODO Auto-generated method stub
	final Builder builder = new AlertDialog.Builder(this);

	builder.setTitle("新版本提示");
	builder.setMessage("发现新版本!\n版本号:" + this.mUpdateVersion.getVersionName() + ",\n更新内容如下:\n" + this.mUpdateVersion.getUpdateInstruction());

	builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		final AlertDialog downloadingDialog = new AlertDialog.Builder(SettingsActivity.this).create();
		downloadingDialog.setMessage("");
		downloadingDialog.setTitle("新版本下载中,稍候会提示安装...");

		downloadingDialog.setCancelable(false);
		downloadingDialog.setCanceledOnTouchOutside(false);

		// 后台下载
		downloadingDialog.setButton(AlertDialog.BUTTON_POSITIVE, "后台下载", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			downloadingDialog.dismiss();
			showToast("后台下载中");
			finish();
		    }
		});

		// 取消下载
		downloadingDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消下载", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			downloadingDialog.dismiss();
			isDownloading = false;
			showToast("下载已取消");
			finish();
		    }
		});

		downloadingDialog.show();

		final SeekBar bar = new SeekBar(SettingsActivity.this);
		bar.setMax(100);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.bottomMargin = 2;
		downloadingDialog.addContentView(bar, params);
		downloadAndUpdate(new OnUpdateListener() {

		    @Override
		    public void onDownloading(int contentLength, int currentLength) {
			// TODO Auto-generated method stub
			int progress = (int) (currentLength / (float) contentLength * 100);
			bar.setProgress(progress);
			downloadingDialog.setMessage("更新包下载中..." + progress + "%");
		    }

		    @Override
		    public void onSuccess(String file) {
			// TODO Auto-generated method stub
			// 保存更新时间
			// 确定安装后保存最后更新时间
			SharedPreferences pref = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
			pref.edit().putLong("last_checkupdate_time", System.currentTimeMillis()).commit();

			ActivityUtil.installApk(SettingsActivity.this, new File(file));
			downloadingDialog.dismiss();
			//finish();
		    }

		    @Override
		    public void onFailure(String msg) {
			// TODO Auto-generated method stub
			ActivityUtil.show(SettingsActivity.this, msg);
			downloadingDialog.dismiss();
		    }
		});
	    }
	});

	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		//finish();
	    }
	});
	final AlertDialog dialog = builder.create();
	dialog.show();
    }

    private boolean isDownloading = true;// 下载中...

    public void downloadAndUpdate(final OnUpdateListener listener) {

	isDownloading = true;

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

	new Thread() {
	    @Override
	    public void run() {
		try {
		    HttpURLConnection conn = (HttpURLConnection) new URL(mUpdateVersion.getDownloadUrl()).openConnection();
		    InputStream in = conn.getInputStream();
		    //		    int contentLength = conn.getContentLength();
		    int contentLength = mUpdateVersion.getSize();

		    int currentLength = 0;
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;

		    String tmpDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/";
		    File file = new File(tmpDir + "/me.himi.love" + mUpdateVersion.getDate() + ".apk");
		    if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		    }

		    FileOutputStream fos = new FileOutputStream(file);

		    while (isDownloading && (len = in.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			currentLength += len;
			handler.obtainMessage(0, contentLength, currentLength).sendToTarget();
		    }
		    fos.flush();
		    fos.close();
		    in.close();
		    // 下载完成
		    if (isDownloading) {
			handler.obtainMessage(1, file.getAbsolutePath()).sendToTarget();
		    }
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
	}.start();

    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	readAndSetTopBackgroundColor();

	System.out.println("topColor:" + readTopBackgroundColor());
	super.onResume();
    }

    public static interface OnUpdateListener {
	void onDownloading(int contentLength, int currentLength);

	void onSuccess(String file);

	void onFailure(String msg);
    }

    public static boolean getVibrator(Context ctx) {
	SharedPreferences sp = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
	return sp.getBoolean(USE_VIBRATOR_WHEN_NEWMSG, true);
    }
}
