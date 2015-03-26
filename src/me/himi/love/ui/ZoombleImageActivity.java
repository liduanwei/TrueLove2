package me.himi.love.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.ZoomableImageView;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ZoombleImageActivity extends BaseActivity {

    me.himi.love.view.ZoomableImageView mZoomableImageView;

    private String imageUrl; // 详细大图
    private String defaultSmallImageUrl;//初始缩略图

    ProgressBar mProgress;

    private Bitmap mBitmap;//当前图

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_zoombleimage);

	init();
    }

    private void init() {

	imageUrl = getIntent().getStringExtra("image_url");
	if (imageUrl == null || imageUrl.length() == 0) {
	    showToast("未指定url");
	    finish();
	    return;
	}

	defaultSmallImageUrl = getIntent().getStringExtra("default_small_image_url");

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("查看大图");

	// 操作
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	final Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 0:
		    showToast("图片已保存成功,路径:" + msg.obj.toString());
		    break;
		}
		super.handleMessage(msg);
	    }
	};

	// 保存
	findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mBitmap != null && !mBitmap.isRecycled()) {

		    final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imageUrl.hashCode() + ".png";

		    new Thread(new Runnable() {
			public void run() {
			    try {
				mBitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(new File(path)));
				handler.obtainMessage(0, path).sendToTarget();
			    } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    }).start();
		} else {
		    showToast("图片未加载!");
		}
	    }
	});

	System.out.println("intent url:" + imageUrl);
	this.mProgress = (ProgressBar) findViewById(R.id.pb_zoomableimage_loading);

	this.mZoomableImageView = (ZoomableImageView) findViewById(R.id.zoomableimageview);

	ImageView ivImage = (ImageView) findViewById(R.id.iv_image);
	ivImage.setVisibility(View.GONE);

	//	ImageLoader.getInstance().displayImage(imageUrl, ivImage);

	// 默认加载小图
	if (defaultSmallImageUrl != null) {
	    loadImage(defaultSmallImageUrl);
	} else {
	    loadImage(imageUrl);
	}
	// 原图

	findViewById(R.id.tv_orginal).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		loadImage(imageUrl);

	    }
	});

	//	Downloader.download(imageUrl, new AsyncResponseHandler() {
	//
	//	    @Override
	//	    public void onSuccess(byte[] response) {
	//		// TODO Auto-generated method stub
	//		Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
	//		mZoomableImageView.setImageBitmap(bitmap);
	//		mTvProgress.setVisibility(View.GONE);
	//	    }
	//
	//	    @Override
	//	    public void onProgress(int currentLength, int contentLength) {
	//
	//		mTvProgress.setText((int) (currentLength / (float) contentLength * 100) + "%");
	//	    }
	//
	//	    @Override
	//	    public void onFailure(String errorMsg) {
	//		// TODO Auto-generated method stub
	//		showToast(errorMsg);
	//
	//	    }
	//	});
    }

    private void loadImage(String imageUrl) {
	// TODO Auto-generated method stub
	// TODO Auto-generated method stub
	// 
	ImageLoader.getInstance().displayImage(imageUrl, mZoomableImageView, ImageLoaderOptions.normalOptions(), new ImageLoadingListener() {

	    @Override
	    public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub
	    }

	    @Override
	    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub
		showToast("图片加载失败");
	    }

	    @Override
	    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// TODO Auto-generated method stub

		mBitmap = arg2;

		mZoomableImageView.setImageBitmap(arg2);

		mProgress.setVisibility(View.GONE);
	    }

	    @Override
	    public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }
	});
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	//	if (mZoomableImageView.getImageBitmap() != null && !mZoomableImageView.getImageBitmap().isRecycled()) {
	//	    mZoomableImageView.getImageBitmap().recycle();
	//	    mZoomableImageView.setImageBitmap(null);
	//	}
	//	if (mBitmap != null && !mBitmap.isRecycled()) {
	//	    mBitmap.recycle();
	//	    mBitmap = null;
	//	}
	super.onDestroy();
    }

    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (mBitmap != null) {
	    if (!mBitmap.isRecycled()) {
		mBitmap.recycle();
		mBitmap = null;
	    }
	}
	super.onBackPressed();
    }
}
