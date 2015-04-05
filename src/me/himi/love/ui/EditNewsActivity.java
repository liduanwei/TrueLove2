package me.himi.love.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnSimpleListener;
import me.himi.love.IAppService.OnUploadPhotosListener;
import me.himi.love.IAppService.PublishNewsParams;
import me.himi.love.IAppService.UploadFileParams;
import me.himi.love.IAppService.UploadPhotosResponse;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.loader.impl.UserNewsLoaderImpl;
import me.himi.love.im.adapter.EmoViewPagerAdapter;
import me.himi.love.im.adapter.EmoteAdapter;
import me.himi.love.im.entity.FaceText;
import me.himi.love.im.util.FaceTextUtils;
import me.himi.love.im.util.PhotoUtil;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.PreferencesUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 发布留言
 * @ClassName:EditNewsActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class EditNewsActivity extends BaseActivity implements OnClickListener {

    me.himi.love.im.ui.customview.EmoticonsEditText mEtContent;

    TextView[] tvDels;

    ImageView[] ivPics;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_edit_news);

	init();

	ActivityUtil.hideSoftInput(this);
    }

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setText("发表新留言");

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopAction.setText("发表");
	tvTopAction.setOnClickListener(this);

	findViewById(R.id.tv_publish_news).setOnClickListener(this);

	mEtContent = (me.himi.love.im.ui.customview.EmoticonsEditText) findViewById(R.id.emoet_news_content);

	String content = PreferencesUtil.read(this, "last_news_content");

	mEtContent.setText(content);

	findViewById(R.id.layout_news_picture1).setOnClickListener(this);
	findViewById(R.id.layout_news_picture2).setOnClickListener(this);
	findViewById(R.id.layout_news_picture3).setOnClickListener(this);

	// 
	ivPics = new ImageView[] { (ImageView) findViewById(R.id.iv_news_picture1), (ImageView) findViewById(R.id.iv_news_picture2), (ImageView) findViewById(R.id.iv_news_picture3) };
	// 初始时只显示第一个添加按钮,插入图片之后再显示后面一个
	for (int i = 1, n = ivPics.length; i < n; ++i) {
	    ivPics[i].setVisibility(View.GONE);
	}

	tvDels = new TextView[] { (TextView) findViewById(R.id.tv_remove_news_picture1), (TextView) findViewById(R.id.tv_remove_news_picture2), (TextView) findViewById(R.id.tv_remove_news_picture3) };
	for (int i = 0, n = tvDels.length; i < n; ++i) {
	    tvDels[i].setVisibility(View.GONE);
	    tvDels[i].setOnClickListener(new DelOnClickListener(i));
	}

	initEmoView();
    }

    private class DelOnClickListener implements View.OnClickListener {
	int index;

	public DelOnClickListener(int index) {
	    this.index = index;
	}

	@Override
	public void onClick(View v) {
	    if (foruploadFiles.size() == 1) {
		tvDels[0].setVisibility(View.GONE);
		foruploadFiles.remove(0);
		ivPics[1].setVisibility(View.GONE);
		ivPics[0].setImageBitmap(null);
		ivPics[0].setBackgroundResource(R.drawable.fml);
		return;
	    }

	    // 
	    tvDels[foruploadFiles.size() - 1].setVisibility(View.GONE);
	    ivPics[foruploadFiles.size() - 1].setImageBitmap(null);
	    ivPics[foruploadFiles.size() - 1].setBackgroundResource(R.drawable.fml);

	    foruploadFiles.remove(index);
	    ivPics[index].setImageBitmap(null);

	    int i = 0, n = foruploadFiles.size();
	    for (; i < n; ++i) {
		ImageLoader.getInstance().displayImage("file://" + foruploadFiles.get(i).getAbsolutePath(), ivPics[i], ImageLoaderOptions.normalOptions());
	    }

	    i += 1;

	    for (; i < ivPics.length; ++i) {
		ivPics[i].setVisibility(View.GONE);
	    }

	}
    }

    /**
     * 初始化表情布局
     */
    private void initEmoView() {
	findViewById(R.id.tv_news_emo).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		// 显示或隐藏表情视图
		View view = findViewById(R.id.layout_emo);
		if (view.getVisibility() == View.VISIBLE) {
		    view.setVisibility(View.GONE);
		    //		    ActivityUtil.showSoftInputView(UserNewsCommentActivity.this, etCommentContent);

		} else {
		    view.setVisibility(View.VISIBLE);
		    ActivityUtil.closeSoftInput(EditNewsActivity.this, mEtContent);
		}
	    }

	});

	ViewPager viewpager = (ViewPager) findViewById(R.id.pager_emo);
	List<View> views = new ArrayList<View>();
	List<FaceText> emos01 = FaceTextUtils.faceTexts;
	views.add(getGridView(emos01));
	//	List<FaceText> emos02 = FaceTextUtils.faceTexts2;
	//	views.add(getGridView(emos02));
	viewpager.setAdapter(new EmoViewPagerAdapter(views));

    }

    /**
     * 初始化 gridview
     * @param emos
     * @return
     */
    private View getGridView(List<FaceText> emos) {
	View v = LayoutInflater.from(this).inflate(R.layout.emo_gridview, null);
	GridView gridview = (GridView) v.findViewById(R.id.gv_emo);

	List<FaceText> list = new ArrayList<FaceText>();
	list.addAll(emos);
	final EmoteAdapter gridAdapter = new EmoteAdapter(this, list);
	gridview.setAdapter(gridAdapter);
	gridview.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		FaceText name = (FaceText) gridAdapter.getItem(position);
		String key = name.text.toString();

		System.out.println("key:" + key + ",FaceText:" + name.text);
		try {
		    if (mEtContent != null && !TextUtils.isEmpty(key)) {
			int start = mEtContent.getSelectionStart();
			CharSequence content = mEtContent.getText().insert(start, key);
			mEtContent.setText(content);
			// 定位光标位置
			CharSequence info = mEtContent.getText();
			if (info instanceof Spannable) {
			    Spannable spanText = (Spannable) info;
			    Selection.setSelection(spanText, start + key.length());
			}
		    }
		} catch (Exception e) {

		}
	    }
	});
	return gridview;
    }

    private int currentPictureIndex; // 当前选择插入的位置

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.tv_publish_news:
	case R.id.tv_top_action:
	    startPublish();
	    break;
	case R.id.layout_news_picture1:
	    currentPictureIndex = 0;
	    showPopupPictureDialog();
	    break;
	case R.id.layout_news_picture2:
	    currentPictureIndex = 1;
	    showPopupPictureDialog();
	    break;
	case R.id.layout_news_picture3:
	    currentPictureIndex = 2;
	    showPopupPictureDialog();
	    break;
	}
    }

    private void showPopupPictureDialog() {
	// 原先是可以重新选择图片替换,更改为:
	// 检查如果当前选择的位置存在图片则进入查看该图片的大图

	if (!foruploadFiles.isEmpty()) {
	    if (currentPictureIndex < foruploadFiles.size()) {
		String imgUrlIfExists = "file://" + foruploadFiles.get(currentPictureIndex).getAbsolutePath();
		Intent intent = new Intent(this, ZoombleImageActivity.class);
		intent.putExtra("image_url", imgUrlIfExists);
		//	    intent.putExtra("default_small_image_url", null);
		startActivity(intent);
		return;
	    }
	}

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

    private void selectPictureFromLocal() {
	// TODO Auto-generated method stub
	Intent intent = new Intent(Intent.ACTION_PICK, null);
	intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
	startActivityForResult(intent, GALLERY_PHOTO);
    }

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

	    File file = new File(facePhotoPath);

	    // 需要压缩处理下图片
	    Bitmap compressedBitmap = PhotoUtil.getImageThumbnail(facePhotoPath, 400, 400);
	    String newPhotoPath = PhotoUtil.saveBitmap(file.getParentFile().getAbsolutePath(), file.getName(), compressedBitmap, true);

	    // 删除旧图
	    if (this.foruploadFiles.size() >= currentPictureIndex + 1) {
		this.foruploadFiles.remove(currentPictureIndex);
	    }
	    this.foruploadFiles.add(currentPictureIndex, new File(newPhotoPath));

	    // 显示待上传图片
	    ImageLoader.getInstance().displayImage("file://" + newPhotoPath, ivPics[currentPictureIndex], ImageLoaderOptions.normalOptions());
	    // 删除 可见
	    tvDels[currentPictureIndex].setVisibility(View.VISIBLE);
	    // 下个添加按钮可见
	    if (this.foruploadFiles.size() < ivPics.length) {
		ivPics[this.foruploadFiles.size()].setVisibility(View.VISIBLE);
	    }
	    //	    try {
	    //		// decodeBitmap中使用 option 中的 inSampleSize 来压缩处理大图
	    //		//		String newPhotoPath = PhotoUtil.compressBitmap(facePhotoPath, 6, 100);
	    //		// 加入待上传 list
	    //		this.foruploadFiles.add(new File(newPhotoPath));
	    //
	    //		ImageLoader.getInstance().displayImage("file://" + newPhotoPath, ivPics[this.foruploadFiles.size() - 1], ImageLoaderOptions.normalOptions());
	    //
	    //	    } catch (FileNotFoundException e) {
	    //		// TODO Auto-generated catch block
	    //		e.printStackTrace();
	    //	    }
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

	    Uri uri2 = intent.getData();
	    String filepath = ActivityUtil.getRealFilePath(EditNewsActivity.this, uri2);
	    File file2 = new File(filepath);

	    Bitmap compressedBitmap2 = PhotoUtil.getImageThumbnail(filepath, 400, 400);
	    String newPhotoPath2 = PhotoUtil.saveBitmap(file2.getParentFile().getAbsolutePath(), file2.getName(), compressedBitmap2, true);

	    // 加入待上传 list
	    // 删除旧图
	    if (this.foruploadFiles.size() >= currentPictureIndex + 1) {
		this.foruploadFiles.remove(currentPictureIndex);
	    }
	    this.foruploadFiles.add(currentPictureIndex, new File(newPhotoPath2));

	    ImageLoader.getInstance().displayImage("file://" + newPhotoPath2, ivPics[this.foruploadFiles.size() - 1], ImageLoaderOptions.normalOptions());
	    // 删除 可见
	    tvDels[currentPictureIndex].setVisibility(View.VISIBLE);
	    // 下个添加按钮可见
	    if (this.foruploadFiles.size() < ivPics.length) {
		ivPics[this.foruploadFiles.size()].setVisibility(View.VISIBLE);
	    }
	    //	    ivPics[this.foruploadFiles.size() - 1].setImageBitmap(BitmapFactory.decodeFile(filepath));
	    break;
	case CUT_PHOTO: // 裁剪图像
	    saveCropAvator(intent);
	    break;
	}
	super.onActivityResult(requestCode, resultCode, intent);
    }

    // 用户已选择的等待上传的图片文件
    List<File> foruploadFiles = new ArrayList<File>();

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
	    int degree = PhotoUtil.readPictureDegree(facePhotoPath);

	    if (degree != 0) {
		bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
	    }
	    // 保存图片
	    // 必须有 png后缀名,AsyncHttp 识别为Content-Type
	    String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
	    String savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	    String file = PhotoUtil.saveBitmap(savePath, filename, bitmap, false);

	    if (bitmap != null && bitmap.isRecycled()) {
		bitmap.recycle();
	    }
	    foruploadFiles.add(new File(file));
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
     * 发布留言
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
		    publishNews();
		}
	    });
	} else {
	    publishNews(); // 不需要上传图片则直接发布
	}

    }

    private void publishNews() {
	final ProgressDialog progress = new ProgressDialog(this);
	progress.setMessage("发布中,请稍候...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	//发布留言的所有参数
	PublishNewsParams newsParams = new PublishNewsParams();
	newsParams.title = "";
	newsParams.content = mEtContent.getText().toString();
	newsParams.latitude = MyApplication.getInstance().getLatitude();
	newsParams.longitude = MyApplication.getInstance().getLongtitude();
	newsParams.address = MyApplication.getInstance().getAddr();
	newsParams.isPrivate = ((CheckBox) findViewById(R.id.cb_publish_news_isprivate)).isChecked() ? 1 : 0; // 是否公开
	newsParams.isAllowComment = ((CheckBox) findViewById(R.id.cb_publish_news_isallowcomment)).isChecked() ? 0 : 1;
	newsParams.imageUrls = new ArrayList<String>() {
	    {
		for (String imageUrl : uploadedFiles) {
		    add(imageUrl);
		}
	    }
	};

	AppServiceImpl.getInstance().publishNews(newsParams, new OnSimpleListener() {

	    @Override
	    public void onSuccess() {
		progress.dismiss();

		// 发布成功后及时清空已保存的草稿内容
		PreferencesUtil.save(EditNewsActivity.this, "last_news_content", "");

		ActivityUtil.show(EditNewsActivity.this, "发布成功");
		Intent intent = new Intent(EditNewsActivity.this, UserNewsActivity.class);
		Bundle extras = new Bundle();

		DetailInfoUser user = new DetailInfoUser();
		user.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());

		user.setNickname(MyApplication.getInstance().getCurrentLoginedUser().getNickname());
		extras.putSerializable("user", user);
		intent.putExtras(extras);
		startActivity(intent);

		// 
		finish();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		progress.dismiss();
		ActivityUtil.show(EditNewsActivity.this, errorMsg);
	    }
	});
    }

    @Override
    public void onBackPressed() {
	// 没有内容或者内容长度小于10则直接退出
	if (TextUtils.isEmpty(this.mEtContent.getText()) || this.mEtContent.getText().length() < 10) {
	    super.onBackPressed();
	    return;
	}

	AlertDialog dialog = new AlertDialog.Builder(this).create();
	dialog.setTitle("");
	dialog.setMessage("不想发布可临时保存到本地,确定退出编辑吗?");

	// 如果上次已有保存该内容本次点击确定则直接删除
	dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		PreferencesUtil.save(EditNewsActivity.this, "last_news_content", "");
		finish();
	    }

	});
	dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	    }

	});

	dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "保存后退出", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		PreferencesUtil.save(EditNewsActivity.this, "last_news_content", mEtContent.getText().toString());
		finish();
	    }
	});
	dialog.show();

	//super.onBackPressed();
    }
}
