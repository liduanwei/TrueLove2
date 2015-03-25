package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.ZoomableImageView;
import android.graphics.Bitmap;
import android.os.Bundle;
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

	defaultSmallImageUrl = getIntent().getStringExtra("default_small_image_url");

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("查看大图");

	System.out.println("intent url:" + imageUrl);
	this.mProgress = (ProgressBar) findViewById(R.id.pb_zoomableimage_loading);

	this.mZoomableImageView = (ZoomableImageView) findViewById(R.id.zoomableimageview);

	ImageView ivImage = (ImageView) findViewById(R.id.iv_image);
	ivImage.setVisibility(View.GONE);

	//	ImageLoader.getInstance().displayImage(imageUrl, ivImage);

	if (defaultSmallImageUrl != null) {
	    ImageLoader.getInstance().displayImage(defaultSmallImageUrl, mZoomableImageView);
	}
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

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	//	if (mZoomableImageView.getImageBitmap() != null && !mZoomableImageView.getImageBitmap().isRecycled()) {
	//	    mZoomableImageView.getImageBitmap().recycle();
	//	    mZoomableImageView.setImageBitmap(null);
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
