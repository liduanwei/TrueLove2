package me.himi.love.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.himi.love.AppServiceImpl;
import me.himi.love.AppServiceRongCloudIMImpl;
import me.himi.love.IAppService.OnPerfectCompleteResponseListener;
import me.himi.love.IAppService.OnUploadFaceListener;
import me.himi.love.IAppService.PerfectCompleetePostParams;
import me.himi.love.IAppService.UploadFaceResponse;
import me.himi.love.IAppService.UploadFileParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.im.util.PhotoUtil;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.SelectDatePopupWindow;
import me.himi.love.view.SelectDatePopupWindow.OnSubmitListener;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserRegisterCompleteActivity extends BaseActivity {

    View mMainView;

    int mYear, mMonth, mDay;

    RadioButton rbMale, rbFemale;

    EditText mEtNickname;
    EditText mTvBirthday;

    ImageView ivUserFace;
    TextView tvUploadRetry;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_user_register_perfect_complete, null);
	setContentView(mMainView);

	init();

    }

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("完善交友资料");

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	ivUserFace = (ImageView) findViewById(R.id.iv_register_user_face);
	ivUserFace.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		selectPhoto();
	    }
	});

	// 重试上传
	tvUploadRetry = (TextView) findViewById(R.id.tv_uploadface_retry);
	tvUploadRetry.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		tvUploadRetry.setVisibility(View.GONE);
		uploadFace();
	    }
	});

	rbMale = (RadioButton) findViewById(R.id.rb_register_male);
	rbFemale = (RadioButton) findViewById(R.id.rb_register_female);

	// birthday
	mTvBirthday = ((EditText) findViewById(R.id.et_input_birthday));
	mTvBirthday.setText("");

	mEtNickname = ((EditText) findViewById(R.id.et_input_nickname));

	findViewById(R.id.button_update_perfect_complete).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(mEtNickname.getText())) {
		    mEtNickname.requestFocus();
		    showToast("请填写昵称");
		    return;
		}

		if (TextUtils.isEmpty(mTvBirthday.getText())) {
		    ActivityUtil.show(UserRegisterCompleteActivity.this, "请选择你的出生日期");
		    return;
		}

		if (!rbMale.isChecked() && !rbFemale.isChecked()) {
		    ActivityUtil.show(UserRegisterCompleteActivity.this, "请选择你的性别");
		    return;
		}
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserRegisterCompleteActivity.this);
		dialogBuilder.create();
		dialogBuilder.setTitle("提示");
		dialogBuilder.setMessage("性别一经设置无法更改,请确认当前选择");

		dialogBuilder.setNegativeButton("取消", null);

		dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
			if (!ActivityUtil.hasNetwork(UserRegisterCompleteActivity.this)) {
			    ToastFactory.getToast(UserRegisterCompleteActivity.this, "请开启网络").show();
			    return;
			}

			perfectComplete();
		    }
		});
		dialogBuilder.show();

	    }
	});

	mYear = 1988;
	mMonth = 9;
	mDay = 11;
    }

    public void inputBirthdayOnClick(View v) {
	SelectDatePopupWindow birth = new SelectDatePopupWindow(this, mYear + "-" + mMonth + "-" + mDay);

	birth.setOutsideTouchable(false);

	birth.showAtLocation(this.mMainView, Gravity.CENTER, 0, 0);

	birth.setOnSubmitListener(new OnSubmitListener() {

	    @Override
	    public void onSelected(int year, int month, int day) {
		mYear = year;
		mMonth = month;
		mDay = day;
		((EditText) findViewById(R.id.et_input_birthday)).setText(year + "-" + month + "-" + day);
	    }
	});
    }

    private void perfectComplete() {

	final ProgressDialog progress = new ProgressDialog(this);
	progress.setMessage("提交中...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	// 完善资料需要的字段
	PerfectCompleetePostParams postParams = new PerfectCompleetePostParams();
	postParams.nickname = mEtNickname.getText().toString().trim();
	// 月份 -1 , 因为是从0表示1月
	postParams.brithday = (int) (ActivityUtil.getTimemillis(mYear, mMonth - 1, mDay) / 1000L);
	postParams.gender = rbMale.isChecked() ? 1 : 0;

	AppServiceImpl.getInstance().registerPerfectComplete(postParams, new OnPerfectCompleteResponseListener() {

	    @Override
	    public void onSuccess() {
		progress.dismiss();

		AppServiceRongCloudIMImpl.getInstance().refreshUserInfo();
		//进入主页, 清除所有Activity,避免回退
		MyApplication.getInstance().exit();
		// 进入启动页 尝试自动登录
		startActivity(new Intent(UserRegisterCompleteActivity.this, SplashActivity.class));
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		progress.dismiss();
		showToast("上传失败," + errorMsg);
		tvUploadRetry.setVisibility(View.VISIBLE);

	    }
	});
    }

    private String facePhotoPath;//头像路径
    private boolean isFromCamera;// 头像是否来自拍摄
    private static final int CAMERA_PHOTO = 1; // 拍照
    private static final int GALLERY_PHOTO = 2; // 从相册选择
    private static final int CUT_PHOTO = 3;

    private String cropedFilePath; // 已保存的已裁剪的头像路径

    private void selectPhoto() {

	View contentView = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
	final PopupWindow popupWindow = new PopupWindow(contentView);

	popupWindow.setAnimationStyle(-1);
	popupWindow.update(); // 立即更新使用动画效果
	popupWindow.setOutsideTouchable(false);
	// 拦截回退按键所需
	popupWindow.setFocusable(true);
	popupWindow.setBackgroundDrawable(new BitmapDrawable());
	// 拦截回退按键所需 ^^

	popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
	popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
	popupWindow.setAnimationStyle(R.anim.grow_from_bottom);
	popupWindow.showAtLocation(this.mMainView, Gravity.BOTTOM, 0, 0);

	contentView.findViewById(R.id.layout_photo).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		popupWindow.dismiss();
		// TODO Auto-generated method stub
		File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		if (!dir.exists()) {
		    dir.mkdirs();
		}
		// 原图
		File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
		facePhotoPath = file.getAbsolutePath();// 获取相片的保存路径
		Uri imageUri = Uri.fromFile(file);

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, CAMERA_PHOTO);
	    }
	});

	contentView.findViewById(R.id.layout_choose_photo).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		popupWindow.dismiss();
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(intent, GALLERY_PHOTO);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

	if (requestCode == CAMERA_PHOTO) {
	    if (resultCode != RESULT_OK) {
		showToast("没有确认照片");
		return;
	    }
	    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		showToast("SD卡不可用");
		return;
	    }
	    Uri uri = Uri.fromFile(new File(facePhotoPath));
	    int width = 300;
	    int height = 300;
	    isFromCamera = true;
	    ActivityUtil.startImageAction(this, uri, width, height, CUT_PHOTO, true);
	} else if (requestCode == GALLERY_PHOTO) {
	    if (resultCode != RESULT_OK) {
		showToast("没有选择照片");
		return;
	    }

	    if (null == intent) {
		System.out.println("从相册选择intent 返回了null");
		return;
	    }
	    Uri uri = intent.getData();
	    isFromCamera = false;
	    int width = 300;
	    int height = 300;
	    ActivityUtil.startImageAction(this, uri, width, height, CUT_PHOTO, true);
	}

	else if (requestCode == CUT_PHOTO) {
	    saveCropAvator(intent);
	}
	super.onActivityResult(requestCode, resultCode, intent);
    }

    private void saveCropAvator(Intent intent) {
	// TODO Auto-generated method stub
	if (null == intent)
	    return;
	Bundle bundle = intent.getExtras();
	if (bundle == null) {
	    return;
	}

	Bitmap bitmap = bundle.getParcelable("data");
	if (null != bitmap) {
	    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		showToast("SD卡不可用");
		return;
	    }
	    bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
	    //	    int degree = PhotoUtil.readPictureDegree(facePhotoPath);
	    //
	    //	    if (isFromCamera && degree != 0) {
	    //		bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
	    //	    }
	    // 保存图片
	    // 必须有 png后缀名,AsyncHttp 识别为Content-Type
	    String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
	    String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/";

	    String file = PhotoUtil.saveBitmap(savePath, filename, bitmap, false);
	    this.cropedFilePath = file;
	    ImageLoader.getInstance().displayImage("file://" + file, ivUserFace);

	    if (bitmap != null && bitmap.isRecycled()) {
		bitmap.recycle();
	    }
	    // 上传头像
	    uploadFace();
	}

    }

    private void uploadFace() {

	final ProgressDialog progress = new ProgressDialog(this);
	progress.setCancelable(false);
	progress.setMessage("头像上传中...");
	progress.show();

	// TODO Auto-generated method stub
	UploadFileParams params = new UploadFileParams();
	params.userId = 2;
	params.filePathes = new ArrayList<String>();
	params.filePathes.add(this.cropedFilePath);

	AppServiceImpl.getInstance().uploadFace(params, new OnUploadFaceListener() {

	    @Override
	    public void onSuccess(UploadFaceResponse uploadFileResponse) {
		progress.dismiss();
		showToast("头像上传成功");

		//		String imageUrl = Constants.HOST + uploadFileResponse.imageUrl.substring(1);
		//		ActivityUtil.openBrowser(UserRegisterPerfectCompleteActivity.this, imageUrl);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		tvUploadRetry.setVisibility(View.VISIBLE);
		progress.dismiss();
		showToast(errorMsg);
	    }
	});
    }

    private static long firstTime;

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (firstTime + 2000 > System.currentTimeMillis()) {
	    //	    MyApplication.getInstance().exit();
	    startActivity(new Intent(this, MainActivity.class));
	    finish();
	    super.onBackPressed();
	} else {
	    ActivityUtil.show(this, "请完善交友资料, 强制退出将导致性别资料不可修改\n再按一次返回键确认关闭本页面");
	}
	firstTime = System.currentTimeMillis();
    }

}
