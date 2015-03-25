package me.himi.love.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.tenpay.android.service.DownloadDialog;

import me.himi.love.R;
import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

/**
 * @ClassName:CheckUpdateActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 14, 2014 3:08:53 PM
 */
public class CheckUpdateActivity extends BaseActivity {

    private View mMainView;

    private CheckUpdateVersion updateVersion;

    @Override
    protected void onCreate(Bundle arg0) {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_checkupdate, null);

	setContentView(mMainView);

	java.io.Serializable obj = getIntent().getSerializableExtra("updateVersion");
	if (null != obj) {
	    this.updateVersion = (CheckUpdateVersion) obj;
	    showDownloadDialog();
	}
    }

    private void showDownloadDialog() {
	// TODO Auto-generated method stub
	final Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("新版本提示");
	builder.setMessage("有新版本可以使用了,版本号:" + this.updateVersion.getVersionName() + ",更新内容如下:\n" + this.updateVersion.getUpdateInstruction());

	builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		final AlertDialog downloadingDialog = new AlertDialog.Builder(CheckUpdateActivity.this).create();
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

		final SeekBar bar = new SeekBar(CheckUpdateActivity.this);
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

			ActivityUtil.installApk(CheckUpdateActivity.this, new File(file));
			downloadingDialog.dismiss();
			finish();
		    }

		    @Override
		    public void onFailure(String msg) {
			// TODO Auto-generated method stub
			ActivityUtil.show(CheckUpdateActivity.this, msg);
			downloadingDialog.dismiss();
		    }
		});
	    }
	});

	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	builder.show();
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
		    HttpURLConnection conn = (HttpURLConnection) new URL(updateVersion.getDownloadUrl()).openConnection();
		    InputStream in = conn.getInputStream();
		    //		    int contentLength = conn.getContentLength();
		    int contentLength = updateVersion.getSize();

		    int currentLength = 0;
		    byte[] buffer = new byte[1024 << 1];
		    int len = 0;

		    String tmpDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/";
		    File file = new File(tmpDir + "/me.himi.love" + updateVersion.getDate() + ".apk");
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

    public static interface OnUpdateListener {
	void onDownloading(int contentLength, int currentLength);

	void onSuccess(String file);

	void onFailure(String msg);
    }

}
