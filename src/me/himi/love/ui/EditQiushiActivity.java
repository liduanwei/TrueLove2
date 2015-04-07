package me.himi.love.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnUploadPhotosListener;
import me.himi.love.IAppService.UploadFileParams;
import me.himi.love.IAppService.UploadPhotosResponse;
import me.himi.love.IAppServiceExtend.OnLoadPublishArticleResponseListener;
import me.himi.love.IAppServiceExtend.PublishArticlePostParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.im.util.PhotoUtil;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 发布新内容(糗事/树洞/
 * @ClassName:EditArticleActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class EditQiushiActivity extends BaseActivity implements OnClickListener {

    me.himi.love.im.ui.customview.EmoticonsEditText mEtContent;

    TextView mTvDeleteImage; // 删除图片

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_edit_article);

	init();

	ActivityUtil.hideSoftInput(this);
    }

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("分享新糗事");

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("发布");
	tvTopAction.setOnClickListener(this);

	mEtContent = (me.himi.love.im.ui.customview.EmoticonsEditText) findViewById(R.id.emoet_news_content);

	findViewById(R.id.iv_photo_fromgallery).setOnClickListener(this);// 相册
	findViewById(R.id.iv_photo_fromcamera).setOnClickListener(this); // 拍照
	findViewById(R.id.iv_photo_url).setOnClickListener(this); // 网络图片url

	findViewById(R.id.layout_imageurl).setVisibility(View.GONE);

	// 删除图片
	mTvDeleteImage = getViewById(R.id.delete_article_image);
	mTvDeleteImage.setVisibility(View.GONE);
	mTvDeleteImage.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 清空待上传图片
		foruploadFiles.clear();
		uploadedFiles.clear();
		//
		ImageView imageView = (ImageView) findViewById(R.id.iv_secret_image);
		imageView.setVisibility(View.GONE);
		mTvDeleteImage.setVisibility(View.GONE);
	    }
	});

    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.tv_top_action:
	    startPublish();
	    break;
	case R.id.iv_photo_fromgallery:
	    selectPictureFromLocal();
	    break;
	case R.id.iv_photo_fromcamera:
	    takePhoto();
	    break;
	case R.id.iv_photo_url:
	    inputImageUrl();
	    break;
	}
    }

    private void inputImageUrl() {
	findViewById(R.id.layout_imageurl).setVisibility(View.VISIBLE);
	final me.himi.love.im.ui.customview.ClearEditText etImageUrl = (me.himi.love.im.ui.customview.ClearEditText) findViewById(R.id.et_imageurl);

	View cancle = findViewById(R.id.tv_cancle);
	cancle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		findViewById(R.id.layout_imageurl).setVisibility(View.GONE);
	    }
	});
	View submit = findViewById(R.id.tv_submit);
	submit.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		final String imgUrl = etImageUrl.getText().toString().trim();
		if (imgUrl.length() == 0) {
		    showToast("请输入图片url");
		    etImageUrl.requestFocus();
		    etImageUrl.setShakeAnimation();
		    return;
		}

		// 显示删除按钮
		mTvDeleteImage.setVisibility(View.VISIBLE);
		// TODO Auto-generated method stub
		ImageView imageView = (ImageView) findViewById(R.id.iv_secret_image);
		imageView.setVisibility(View.VISIBLE);
		ImageLoader.getInstance().displayImage(imgUrl, imageView, ImageLoaderOptions.normalOptions());

		uploadedFiles.clear();
		uploadedFiles.add(imgUrl);
		// 隐藏
		findViewById(R.id.layout_imageurl).setVisibility(View.GONE);
	    }
	});
    }

    private void showPopupPictureDialog() {
	// 

	View contentView = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
	final PopupWindow popupWindow = new PopupWindow(contentView);

	popupWindow.setAnimationStyle(-1);
	popupWindow.update(); // 立即更新使用动画效果

	//禁用触摸外部取消
	popupWindow.setOutsideTouchable(false);
	// 拦截回退按键所需
	popupWindow.setFocusable(true);
	popupWindow.setBackgroundDrawable(new BitmapDrawable());
	// 拦截回退按键所需 ^^

	// 使用相机拍照
	contentView.findViewById(R.id.layout_photo).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		takePhoto();
		popupWindow.dismiss();

	    }
	});

	// 从手机相册选取
	contentView.findViewById(R.id.layout_choose_photo).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		selectPictureFromLocal();
		popupWindow.dismiss();
	    }
	});

	// cancle
	contentView.findViewById(R.id.layout_photo_cancel).setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		popupWindow.dismiss();
	    }
	});
	popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
	popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
	popupWindow.setAnimationStyle(R.anim.grow_from_bottom);
	popupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    private String facePhotoPath;
    static final int GALLERY_PHOTO = 1;
    static final int CAMERA_PHOTO = 2;
    static final int CUT_PHOTO = 3;

    /**
     * 拍照
     */
    private void takePhoto() {
	// TODO Auto-generated method stub
	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
	if (!dir.exists()) {
	    dir.mkdirs();
	}
	// 原图
	File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png");

	facePhotoPath = file.getAbsolutePath();// 获取相片的保存路径
	Uri imageUri = Uri.fromFile(file);

	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	startActivityForResult(intent, CAMERA_PHOTO);
    }

    /**
     * 从相册选取
     */
    private void selectPictureFromLocal() {
	// TODO Auto-generated method stub
	Intent intent = new Intent(Intent.ACTION_PICK, null);
	intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
	startActivityForResult(intent, GALLERY_PHOTO);
    }

    boolean isFromCamera;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	// TODO Auto-generated method stub
	switch (requestCode) {
	case CAMERA_PHOTO:
	    if (resultCode != RESULT_OK) {
		showToast("没有确认照片");
		return;
	    }
	    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		showToast("SD卡不可用");
		return;
	    }

	    Uri uri = Uri.fromFile(new File(facePhotoPath));
	    int width = ActivityUtil.getScreenSize()[0];
	    int height = 300;
	    isFromCamera = true;
	    ActivityUtil.startImageAction(this, uri, width, height, CUT_PHOTO, true);
	    break;
	case GALLERY_PHOTO:
	    if (resultCode != RESULT_OK) {
		showToast("没有选择照片");
		return;
	    }

	    if (null == intent) {
		System.out.println("从相册选择intent 返回了null");
		return;
	    }

	    uri = intent.getData();
	    width = ActivityUtil.getScreenSize()[0];
	    height = 300;
	    isFromCamera = false;
	    ActivityUtil.startImageAction(this, uri, width, height, CUT_PHOTO, true);
	    break;
	case CUT_PHOTO: // 保存已裁剪的图像
	    saveCropedImage(intent);
	    break;
	}
	super.onActivityResult(requestCode, resultCode, intent);
    }

    // 用户已选择的等待上传的图片文件
    List<File> foruploadFiles = new ArrayList<File>();

    private void saveCropedImage(Intent intent) {
	// TODO Auto-generated method stub
	if (null == intent) {
	    showToast("intent is null");
	    return;
	}

	Bundle bundle = intent.getExtras();
	if (bundle == null) {
	    showToast("bundle is null");
	    return;
	}

	Bitmap bitmap = bundle.getParcelable("data");
	if (null != bitmap) {
	    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		showToast("SD卡不可用");
		return;
	    }
	    //	    int degree = PhotoUtil.readPictureDegree(facePhotoPath);
	    //	    if (degree != 0) {
	    //		bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
	    //	    }
	    // 保存图片
	    // 必须有 png后缀名,AsyncHttp 识别为Content-Type
	    String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
	    String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	    String file = PhotoUtil.saveBitmap(savePath, filename, bitmap, false);

	    if (bitmap != null && bitmap.isRecycled()) {
		bitmap.recycle();
	    }

	    foruploadFiles.add(new File(file));
	    // 显示删除按钮
	    mTvDeleteImage.setVisibility(View.VISIBLE);
	    ImageView imageView = (ImageView) findViewById(R.id.iv_secret_image);
	    imageView.setVisibility(View.VISIBLE);
	    ImageLoader.getInstance().displayImage("file://" + file, imageView);
	    showToast("file://" + file);
	} else {
	    showToast("bitmap is null");
	}

    }

    // 已上传成功的图片文件信息
    List<String> uploadedFiles = new ArrayList<String>();

    private void uploadPhotos(final Runnable onUploadCompleteListener) {
	// 先清空上次失败的数据
	uploadedFiles.clear();

	UploadFileParams params = new UploadFileParams();
	// 该参数不能指定上传用户,上传用户由登录的 session 判定
	params.userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	params.filePathes = new ArrayList<String>();
	//添加待上传的文件路径
	for (File f : foruploadFiles) {
	    params.filePathes.add(f.getAbsolutePath());
	}

	final ProgressDialog progress = new ProgressDialog(this);
	progress.setMessage("发布中,请稍候...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	progress.setMessage("发布中,正在上传图片");

	AppServiceImpl.getInstance().uploadPhoto(params, new OnUploadPhotosListener() {

	    @Override
	    public void onSuccess(UploadPhotosResponse uploadFileResponse) {
		progress.dismiss();

		// 上传成功后返回所有的图url
		for (String imgUrl : uploadFileResponse.imageUrls) {
		    uploadedFiles.add(imgUrl);
		}
		// 发布成功回调
		onUploadCompleteListener.run();

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		showToast(errorMsg);
		progress.dismiss();
	    }

	});
    }

    /**
     * 发布秘密
     */
    private void startPublish() {
	if (TextUtils.isEmpty(mEtContent.getText())) {
	    //	    mEtContent.setShakeAnimation();
	    mEtContent.requestFocus();
	    return;
	}

	if (this.foruploadFiles.size() != 0) { // 需要上传图片

	    // 上传所有已选择的图片
	    uploadPhotos(new Runnable() {

		@Override
		public void run() {
		    // TODO Auto-generated method stub
		    publishSecret();
		}
	    });
	} else {
	    publishSecret(); // 不需要上传图片则直接发布
	}

    }

    private void publishSecret() {
	final ProgressDialog progress = new ProgressDialog(this);
	progress.setMessage("发布中,请稍候...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	//发布秘密需要的所有参数
	PublishArticlePostParams postParams = new PublishArticlePostParams();
	postParams.isPublic = true;
	postParams.content = mEtContent.getText().toString();
	if (uploadedFiles.size() != 0) { // 已上传的图片
	    postParams.backgroundImageUrl = uploadedFiles.get(0);
	} else {
	    postParams.backgroundImageUrl = null; // 没有图片
	}
	postParams.tag = "情感";

	postParams.address = MyApplication.getInstance().getAddr();
	postParams.longitude = MyApplication.getInstance().getLongtitude();
	postParams.latitude = MyApplication.getInstance().getLatitude();
	// 发布
	AppServiceExtendImpl.getInstance().publishArticle(postParams, new OnLoadPublishArticleResponseListener() {

	    @Override
	    public void onSuccess() {
		// TODO Auto-generated method stub
		showToast("发布成功");
		progress.dismiss();
		mEtContent.setText("");
		finish();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
		progress.dismiss();
	    }
	});
    }

    @Override
    public void onBackPressed() {
	if (findViewById(R.id.layout_imageurl).getVisibility() == View.VISIBLE) {
	    findViewById(R.id.layout_imageurl).setVisibility(View.GONE);
	    return;
	}

	// 没有内容或者内容长度小于10则直接退出
	if (TextUtils.isEmpty(this.mEtContent.getText()) || this.mEtContent.getText().length() < 10) {
	    super.onBackPressed();
	    return;
	}

	AlertDialog dialog = new AlertDialog.Builder(this).create();
	dialog.setTitle("");
	dialog.setMessage("确定退出编辑吗?");

	dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	    }

	});

	dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "退出", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	dialog.show();

	//super.onBackPressed();
    }
}
