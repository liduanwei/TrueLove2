package me.himi.love.ui;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnLoadDetailUserListener;
import me.himi.love.IAppService.OnSimpleListener;
import me.himi.love.IAppService.OnUploadFaceListener;
import me.himi.love.IAppService.UploadFaceResponse;
import me.himi.love.IAppService.UploadFileParams;
import me.himi.love.IAppService.UserInfo;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.LoginedUser;
import me.himi.love.im.util.PhotoUtil;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.ListMenuDialog;
import me.himi.love.view.ListMenuDialog.MenuItem;
import me.himi.love.view.SelectDatePopupWindow;
import me.himi.love.view.SelectDatePopupWindow.OnSubmitListener;
import me.himi.love.view.SelectProvinceCityPopupWindow;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
* @ClassName:EditMyInfoActivity
* @author sparklee liduanwei_911@163.com
* @date Nov 5, 2014 3:50:36 PM
*/
public class EditMyInfoActivity extends BaseActivity implements OnClickListener {
    Button[] mTabs;

    View mMainView;

    TextView mTvTopTitle;
    TextView mTvTopAction;

    TextView tvUserId, tvMaritalStatus, tvHomeplace, tvAddress, tvBirthday;
    EditText etEdu;// 学历
    EditText etHeight, etWeight;
    TextView tvCharmBody, tvHouse, tvMonthSalary, tvDistanceLove, tvOppositeSexType, tvPremartialSex, tvLiveWithParents, tvWantBaby, tvInterests, tvPersonlity;
    // 征友设置
    TextView tvWantedHomeplace, tvWantedAddress, tvWantedAge, tvWantedHeight, tvWantedEducation, tvWantedSalary, tvWantedExtends;

    TextView tvEmployment;

    EditText etInstruction;
    EditText etOftenAddress;

    EditText etMonologue, etMyNickname;

    TextView tvConstellation;

    View layoutBackground; // 背景图
    View layoutFace; // 头像
    ImageView ivMyFace; //用户头像

    // 表示当前用户资料已加载完成
    DetailInfoUser mCurrentInfoUser;

    private String facePhotoPath;//头像路径
    private boolean isFromCamera;// 头像是否来自拍摄
    private static final int CAMERA_PHOTO = 1; // 拍照
    private static final int GALLERY_PHOTO = 2; // 从相册选择
    private static final int CUT_PHOTO = 3;

    public static final int EDIT_MY_INFO_RESULT_CODE = 2;// 编辑我的资料后返回 OnActivityResult

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_edit_myinfo, null);
	setContentView(mMainView);

	init();

	// 默认先隐藏软键盘
	ActivityUtil.hideSoftInput(this);
    }

    private void init() {

	mTvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	mTvTopTitle.setText("我");
	mTvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	mTvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	mTvTopAction.setText("保存");

	mTvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (mCurrentInfoUser == null) {
		    ActivityUtil.show(EditMyInfoActivity.this, "当前资料同步失败,请重新获取后再修改");
		    return;
		}

		saveMyInfo();
	    }
	});

	// 背景图
	layoutBackground = findViewById(R.id.layout_user_background);
	layoutBackground.setOnClickListener(this);
	//头像
	layoutFace = findViewById(R.id.layout_user_face);
	layoutFace.setOnClickListener(this);

	ivMyFace = (ImageView) findViewById(R.id.iv_user_face);

	// 用户ID
	tvUserId = (TextView) findViewById(R.id.tv_user_id);

	// 身高
	etHeight = (EditText) findViewById(R.id.et_user_height);

	// 体重
	etWeight = (EditText) findViewById(R.id.et_user_weight);

	// 真爱独白
	etMonologue = (EditText) findViewById(R.id.et_monologue);
	// 昵称
	etMyNickname = (EditText) findViewById(R.id.et_my_nickname);

	// 生日
	tvBirthday = (TextView) findViewById(R.id.tv_user_birthday);
	tvBirthday.setOnClickListener(this);

	//职业
	tvEmployment = (TextView) findViewById(R.id.tv_user_employment);
	tvEmployment.setOnClickListener(this);

	// 学历
	etEdu = (EditText) findViewById(R.id.et_user_edu);
	etEdu.setOnClickListener(this);

	// 情感状况
	tvMaritalStatus = (TextView) findViewById(R.id.tv_user_marital_status);
	tvMaritalStatus.setOnClickListener(this);

	// 家乡
	tvHomeplace = (TextView) findViewById(R.id.tv_user_homeplace);
	tvHomeplace.setOnClickListener(this);

	// 现居地
	tvAddress = (TextView) findViewById(R.id.tv_user_address);
	tvAddress.setOnClickListener(this);

	//星座
	tvConstellation = (TextView) findViewById(R.id.tv_user_constellation);

	//  空间背景
	findViewById(R.id.layout_user_background).setOnClickListener(this);

	tvCharmBody = (TextView) findViewById(R.id.tv_user_charm_body);
	tvCharmBody.setOnClickListener(this);

	tvHouse = (TextView) findViewById(R.id.tv_user_house);
	tvHouse.setOnClickListener(this);

	// 月薪
	tvMonthSalary = getViewById(R.id.tv_monthly_salary);
	tvMonthSalary.setOnClickListener(this);

	tvDistanceLove = (TextView) findViewById(R.id.tv_user_distance_love);
	tvDistanceLove.setOnClickListener(this);

	// 喜欢的异性类型
	tvOppositeSexType = (TextView) findViewById(R.id.tv_user_opposite_sex_type);
	tvOppositeSexType.setOnClickListener(this);

	// 婚前性行为
	tvPremartialSex = (TextView) findViewById(R.id.tv_user_premartial_sex);
	tvPremartialSex.setOnClickListener(this);

	// 接收和对方父母同住
	tvLiveWithParents = (TextView) findViewById(R.id.tv_user_live_with_parents);
	tvLiveWithParents.setOnClickListener(this);

	// 是否想要小孩
	tvWantBaby = (TextView) findViewById(R.id.tv_user_want_baby);
	tvWantBaby.setOnClickListener(this);

	// 兴趣
	tvInterests = (TextView) findViewById(R.id.tv_user_interests);
	tvInterests.setOnClickListener(this);

	// 个性特征
	tvPersonlity = (TextView) findViewById(R.id.tv_user_personality);
	tvPersonlity.setOnClickListener(this);

	// 个人说明
	etInstruction = (EditText) findViewById(R.id.et_personal_instruction);

	// 常出没地
	etOftenAddress = (EditText) findViewById(R.id.et_often_address);

	// 征友设置

	tvWantedHomeplace = (TextView) findViewById(R.id.tv_wanted_homeplace);
	tvWantedAddress = (TextView) findViewById(R.id.tv_wanted_address);
	tvWantedAge = (TextView) findViewById(R.id.tv_wanted_age);
	tvWantedHeight = (TextView) findViewById(R.id.tv_wanted_height);
	tvWantedEducation = (TextView) findViewById(R.id.tv_wanted_education);
	tvWantedSalary = (TextView) findViewById(R.id.tv_wanted_monthly_salary);
	tvWantedExtends = (TextView) findViewById(R.id.tv_wanted_extends);

	tvWantedHomeplace.setText("湖北");
	tvWantedAddress.setText("不限");
	tvWantedAge.setText("20-26岁");
	tvWantedHeight.setText("不限");
	tvWantedEducation.setText("不限");
	tvWantedSalary.setText("不限");
	tvWantedExtends.setText("只要真心相爱,能相濡以沫,待华发满头之时仍能陪我看每一个日出日落这就够了");

	tvWantedHomeplace.setOnClickListener(this);
	tvWantedAddress.setOnClickListener(this);
	tvWantedAge.setOnClickListener(this);
	tvWantedHeight.setOnClickListener(this);
	tvWantedEducation.setOnClickListener(this);
	tvWantedSalary.setOnClickListener(this);
	tvWantedExtends.setOnClickListener(this);

	// 已有数据则直接取,没有则重新加载
	Serializable obj = getIntent().getSerializableExtra("DetailInfoUser");

	if (null != obj) {

	    DetailInfoUser user = (DetailInfoUser) obj;

	    // 表示当前用户资料已加载完成
	    mCurrentInfoUser = user;

	    setInfos(user);

	} else {
	    loadMyInfo();
	}
    }

    private View progressBar;

    private void loadMyInfo() {
	if (!ActivityUtil.hasNetwork(this)) {
	    ActivityUtil.show(this, "请开启网络");
	    return;
	}

	if (null == progressBar) {
	    progressBar = new ProgressBar(this);
	    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
	    params.gravity = Gravity.CENTER;
	    getWindow().addContentView(progressBar, params);
	}

	int userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	AppServiceImpl.getInstance().loadUserInfo(userId, new OnLoadDetailUserListener() {

	    @Override
	    public void onSuccess(DetailInfoUser user) {
		// TODO Auto-generated method stub
		progressBar.setVisibility(View.GONE);

		if (user == null) {
		    ActivityUtil.show(EditMyInfoActivity.this, "!-_-加载数据出错");
		    return;
		}

		// 表示当前用户资料已加载完成
		mCurrentInfoUser = user;
		setInfos(user);

		// 更新内存中的user
		LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
		loginedUser.setDetailInfoUser(user);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(EditMyInfoActivity.this, "获取用户资料失败,网络超时").show();
		//		progress.dismiss();
		progressBar.setVisibility(View.GONE);

	    }
	});
    }

    private void setInfos(DetailInfoUser user) {
	// 加载头像
	System.out.println("user face:" + user.getFaceUrl());
	ImageLoader.getInstance().displayImage(user.getFaceUrl(), ivMyFace, ImageLoaderOptions.rounderOptions());

	tvMonthSalary.setText(user.getMonthlySalary());

	tvEmployment.setText(user.getEmployment());
	tvUserId.setText(user.getUserId() + "");
	etMonologue.setText(user.getMonologue());
	etHeight.setText(user.getHeight() + "cm");
	etWeight.setText(user.getWeight() + "kg");
	tvHomeplace.setText(user.getHomeplace());
	tvAddress.setText(user.getAddress());
	tvMaritalStatus.setText(user.getMartialStatus());

	tvBirthday.setText(user.getBirthdayYear() + "-" + user.getBirthdayMonth() + "-" + user.getBirthdayDay());

	tvCharmBody.setText(user.getCharmBody());
	tvHouse.setText(user.getHouse());
	tvDistanceLove.setText(user.getDistanceLove());

	tvOppositeSexType.setText(user.getOppositeSexType());
	tvPremartialSex.setText(user.getPremartialSex());
	tvLiveWithParents.setText(user.getLiveWithParents());
	tvWantBaby.setText(user.getWantBaby());
	tvInterests.setText(user.getInterests());
	tvPersonlity.setText(user.getPersonalities());

	etInstruction.setText(user.getInstruction());
	etOftenAddress.setText(user.getOftenAddress());

	tvConstellation.setText(user.getConstellation() + "座");

	etMyNickname.setText(user.getNickname());

	etEdu.setText(user.getEducation());//学历

	tvWantedHomeplace.setText(user.getWantHomeplace());
	tvWantedAddress.setText(user.getWantAddress());
	tvWantedAge.setText(user.getWantAge());
	tvWantedHeight.setText(user.getWantHeight());
	tvWantedEducation.setText(user.getWantEducation());
	tvWantedSalary.setText(user.getWantMonthSalary());
	tvWantedExtends.setText(user.getWantAddtional());
    }

    /**
     * 保存我的资料
     */
    private void saveMyInfo() {
	if (!ActivityUtil.hasNetwork(this)) {
	    ActivityUtil.show(EditMyInfoActivity.this, "请开启网络");
	    return;
	}
	final ProgressDialog progress = new ProgressDialog(this);
	progress.setMessage("正在保存...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	mCurrentInfoUser.setMonologue(this.etMonologue.getText().toString());
	mCurrentInfoUser.setNickname(this.etMyNickname.getText().toString());

	final UserInfo user = new UserInfo();
	// 修改资料需要同步到 内存中的 user 对象上
	user.setMonologue(mCurrentInfoUser.getMonologue());
	user.setNickname(mCurrentInfoUser.getNickname());
	int year = mCurrentInfoUser.getBirthdayYear();
	int month = mCurrentInfoUser.getBirthdayMonth();
	int day = mCurrentInfoUser.getBirthdayDay();

	user.setBirthday((int) (ActivityUtil.getTimemillis(year, month - 1, day) / 1000L));

	user.setDeviceId(ActivityUtil.getImei(this));
	user.setAppId(""); // 应用 密钥

	user.setHeight(Integer.parseInt(etHeight.getText().toString().toLowerCase().replaceAll("c", "").replaceAll("m", "")));
	user.setWeight(Integer.parseInt(etWeight.getText().toString().toLowerCase().replace("k", "").replaceAll("g", "")));

	user.setMartialStatus(tvMaritalStatus.getText().toString());
	user.setBlood("O");
	user.setHomeplace(tvHomeplace.getText().toString());
	user.setAddress(tvAddress.getText().toString());
	user.setCharmBody(tvCharmBody.getText().toString());
	user.setDistanceLove(tvDistanceLove.getText().toString());
	user.setHouse(tvHouse.getText().toString());
	user.setEducation(etEdu.getText().toString().trim());
	user.setEmployment(tvEmployment.getText().toString());
	user.setWantBaby(tvWantBaby.getText().toString());
	user.setPremartialSex(tvPremartialSex.getText().toString());
	user.setOppositeSexType(tvOppositeSexType.getText().toString());
	user.setMonthlySalary(tvMonthSalary.getText().toString());
	user.setInterests(tvInterests.getText().toString());
	user.setPersonalities(tvPersonlity.getText().toString());

	user.setInstruction(etInstruction.getText().toString());
	user.setOftenAddress(etOftenAddress.getText().toString());

	user.setWantHomeplace(tvWantedHomeplace.getText().toString());
	user.setWantAddress(tvWantedAddress.getText().toString());
	user.setWantAge(tvWantedAge.getText().toString());
	user.setWantHeight(tvWantedHeight.getText().toString());
	user.setWantEducation(tvWantedEducation.getText().toString());
	user.setWantMonthSalary(tvWantedSalary.getText().toString());
	user.setWantAddtional(tvWantedExtends.getText().toString());

	user.setMonthlySalary(tvMonthSalary.getText().toString());

	AppServiceImpl.getInstance().update(user, new OnSimpleListener() {

	    @Override
	    public void onSuccess() {
		// TODO Auto-generated method stub
		progress.cancel();
		ActivityUtil.show(EditMyInfoActivity.this, "保存成功");
		//setResult(UserInfoActivity.EDIT_MY_INFO_RESULT_CODE);

		LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
		DetailInfoUser infoUser = loginedUser.getDetailInfoUser();
		if (infoUser != null) { // 更新内存中的信息
		    resetDetailInfoUser(infoUser, user);
		}

		//finish();
		finish();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(EditMyInfoActivity.this, "保存失败,网络超时");
		progress.cancel();
	    }
	});
    }

    /**
     * 
     * userInfo
     * @param user
     */
    private void resetDetailInfoUser(DetailInfoUser infoUser, UserInfo user) {
	// TODO Auto-generated method stub
	infoUser.setBirthdayDay(mCurrentInfoUser.getBirthdayDay());
	infoUser.setBirthdayMonth(mCurrentInfoUser.getBirthdayMonth());
	infoUser.setBirthdayYear(mCurrentInfoUser.getBirthdayYear());

	infoUser.setHeight(user.getHeight());
	infoUser.setWeight(user.getWeight());

	infoUser.setMartialStatus(user.getMartialStatus());
	infoUser.setBlood(user.getBlood());
	infoUser.setHomeplace(user.getHomeplace());
	infoUser.setAddress(user.getAddress());
	infoUser.setCharmBody(user.getCharmBody());
	infoUser.setDistanceLove(user.getDistanceLove());
	infoUser.setHouse(user.getHouse());
	infoUser.setEducation(user.getEducation());
	infoUser.setEmployment(user.getEmployment());
	infoUser.setWantBaby(user.getWantBaby());
	infoUser.setPremartialSex(user.getPremartialSex());
	infoUser.setOppositeSexType(user.getOppositeSexType());
	infoUser.setMonthlySalary(user.getMonthlySalary());
	infoUser.setInterests(user.getInterests());
	infoUser.setPersonalities(user.getPersonalities());

	infoUser.setInstruction(user.getInstruction());
	infoUser.setOftenAddress(user.getOftenAddress());

	infoUser.setWantHomeplace(user.getWantHomeplace());
	infoUser.setWantAddress(user.getWantAddress());
	infoUser.setWantAge(user.getWantAge());
	infoUser.setWantHeight(user.getWantHeight());
	infoUser.setWantEducation(user.getWantEducation());
	infoUser.setWantMonthSalary(user.getWantMonthSalary());
	infoUser.setWantAddtional(user.getWantAddtional());

	user.setMonthlySalary(user.getMonthlySalary());

    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.tv_user_birthday: // 
	    inputMyBirthday();
	    break;
	case R.id.tv_user_homeplace:
	    selectHomeplace();
	    break;
	case R.id.tv_user_address:
	    selectAddress();
	    break;
	case R.id.tv_user_marital_status:
	    selectMaritalStatus();
	    break;
	case R.id.layout_user_face:
	    selectAvatarPhoto();
	    break;
	case R.id.layout_user_background:
	    selectSpaceBackground();
	    //...
	    break;
	case R.id.tv_user_charm_body:
	    selectCharmBody();
	    break;
	case R.id.tv_user_house:
	    selectHouse();
	    break;
	case R.id.tv_monthly_salary:
	    selectSalary();
	    break;
	case R.id.tv_user_employment:
	    selectEmployment();
	    break;
	case R.id.et_user_edu: // 学历
	    selectEdu();
	    break;
	case R.id.tv_user_distance_love:
	    selectDistanceLove();
	    break;
	case R.id.tv_user_premartial_sex:
	    selectPremartialSex();
	    break;
	case R.id.tv_user_opposite_sex_type:
	    selectOppositeSexType();
	    break;
	case R.id.tv_user_live_with_parents:
	    selectLiveWithParents();
	    break;
	case R.id.tv_user_interests:
	    selectInterests();
	    break;
	case R.id.tv_user_personality:
	    selectPersonality();
	    break;
	case R.id.tv_user_want_baby:
	    selectWantBaby();
	    break;
	case R.id.tv_wanted_homeplace:
	    selectWantedHomeplace();
	    break;
	case R.id.tv_wanted_address:
	    selectWantedAddress();
	    break;
	case R.id.tv_wanted_age:
	    selectWantedAge();
	    break;
	case R.id.tv_wanted_height:
	    selectWantedHeight();
	    break;
	case R.id.tv_wanted_education:
	    selectWantedEducation();
	    break;
	case R.id.tv_wanted_monthly_salary:
	    selectWantedSalary();
	    break;
	case R.id.tv_wanted_extends:
	    inputWantedExtends();
	    break;
	}
    }

    private void selectSalary() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("月薪");

	dialog.addMenuItem(dialog.new MenuItem("低于800"));
	dialog.addMenuItem(dialog.new MenuItem("低于2000"));
	dialog.addMenuItem(dialog.new MenuItem("2000-5000"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("5000-10000"));
	dialog.addMenuItem(dialog.new MenuItem("10000-20000"));
	dialog.addMenuItem(dialog.new MenuItem("20000-30000"));
	dialog.addMenuItem(dialog.new MenuItem("30000以上"));

	dialog.setChecked(tvMonthSalary.getText().toString());
	dialog.setInputContent(tvMonthSalary.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvMonthSalary.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvMonthSalary.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 附加说明
     */
    private void inputWantedExtends() {
	final Dialog dialog = new Dialog(this);
	dialog.setTitle("附加要求说明:");
	dialog.show();

	View contentView = LayoutInflater.from(this).inflate(R.layout.input_wanted_extends, null);
	dialog.setContentView(contentView);

	final EditText etContent = (EditText) contentView.findViewById(R.id.et_wanted_extends);
	etContent.setText(tvWantedExtends.getText());

	contentView.findViewById(R.id.tv_wanted_extends_cancle).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		dialog.dismiss();
	    }
	});
	contentView.findViewById(R.id.tv_wanted_extends_submit).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		tvWantedExtends.setText(etContent.getText().toString());
	    }
	});
    }

    private void selectOppositeSexType() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("喜欢的异性类型");

	int currentGender = MyApplication.getInstance().getCurrentLoginedUser().getGender();

	if (currentGender == 1) {
	    dialog.addMenuItem(dialog.new MenuItem("成熟魅力"));
	    dialog.addMenuItem(dialog.new MenuItem("落落大方"));// 
	    dialog.addMenuItem(dialog.new MenuItem("温柔善良"));
	    dialog.addMenuItem(dialog.new MenuItem("眉清目秀"));
	    dialog.addMenuItem(dialog.new MenuItem("性感妩媚"));
	    dialog.addMenuItem(dialog.new MenuItem("善解人意"));
	    dialog.addMenuItem(dialog.new MenuItem("雍容华贵"));
	    dialog.addMenuItem(dialog.new MenuItem("活泼开朗"));
	    dialog.addMenuItem(dialog.new MenuItem("娇小可爱"));
	    dialog.addMenuItem(dialog.new MenuItem("温柔体贴"));

	    dialog.setChecked(tvOppositeSexType.getText().toString());//只有添加的第一个是选中状态   
	    dialog.setInputContent(tvOppositeSexType.getText().toString());
	} else {
	    dialog.addMenuItem(dialog.new MenuItem("温柔体贴"));
	    dialog.addMenuItem(dialog.new MenuItem("落落大方"));// 
	    dialog.addMenuItem(dialog.new MenuItem("稳重内敛"));
	    dialog.addMenuItem(dialog.new MenuItem("外表帅气"));
	    dialog.addMenuItem(dialog.new MenuItem("活泼开朗"));
	    dialog.addMenuItem(dialog.new MenuItem("幽默风趣"));
	    dialog.addMenuItem(dialog.new MenuItem("本分善良"));
	    dialog.addMenuItem(dialog.new MenuItem("性格可爱"));
	    dialog.addMenuItem(dialog.new MenuItem("运动阳光"));
	    dialog.addMenuItem(dialog.new MenuItem("成熟魅力"));

	    dialog.setChecked(tvOppositeSexType.getText().toString());//只有添加的第一个是选中状态
	    dialog.setInputContent(tvOppositeSexType.getText().toString());

	}

	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {

		if (TextUtils.isEmpty(input)) {
		    if (checkedItems.size() != 0) {
			tvOppositeSexType.setText(checkedItems.get(0).getName());
		    }
		} else {
		    tvOppositeSexType.setText(input);
		}
	    }

	});
    }

    private void selectWantBaby() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("个性特征");

	dialog.addMenuItem(dialog.new MenuItem("想要"));
	dialog.addMenuItem(dialog.new MenuItem("暂时不想要"));// 
	dialog.addMenuItem(dialog.new MenuItem("看情况"));

	dialog.setChecked(tvWantBaby.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvWantBaby.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (TextUtils.isEmpty(input)) {
		    if (checkedItems.size() != 0) {
			tvWantBaby.setText(checkedItems.get(0).getName());
		    }
		} else {
		    tvWantBaby.setText(input);
		}
	    }
	});
    }

    private void selectPersonality() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("个性特征");

	dialog.addMenuItem(dialog.new MenuItem("憨厚老实"));
	dialog.addMenuItem(dialog.new MenuItem("善良纯真"));// 
	dialog.addMenuItem(dialog.new MenuItem("大大咧咧"));

	dialog.setChecked(tvPersonlity.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvPersonlity.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (TextUtils.isEmpty(input)) {
		    if (checkedItems.size() != 0) {
			tvPersonlity.setText(checkedItems.get(0).getName());
		    }
		} else {
		    tvPersonlity.setText(input);
		}
	    }
	});
    }

    private void selectInterests() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, true); // 多选模式
	dialog.setTitle("兴趣爱好");

	dialog.addMenuItem(dialog.new MenuItem("阅读"));
	dialog.addMenuItem(dialog.new MenuItem("听音乐"));// 
	dialog.addMenuItem(dialog.new MenuItem("看电影"));
	dialog.addMenuItem(dialog.new MenuItem("上网"));
	dialog.addMenuItem(dialog.new MenuItem("码代码"));

	dialog.setChecked(tvInterests.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvInterests.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvInterests.setText(input);
		} else {

		    System.out.println("size:" + checkedItems.size());

		    if (checkedItems.size() != 0) {

			String s = "";
			for (int i = 0, n = checkedItems.size(); i < n; ++i) {
			    MenuItem item = checkedItems.get(i);
			    s += item.getName();
			    if (i == n - 1) {
				break;
			    }

			    s += ",";
			}
			tvInterests.setText(s);
		    }
		}
	    }
	});
    }

    private void selectLiveWithParents() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("是否接受婚后和对方父母同住");

	dialog.addMenuItem(dialog.new MenuItem("无法接受"));
	dialog.addMenuItem(dialog.new MenuItem("看情况"));// 
	dialog.addMenuItem(dialog.new MenuItem("可以接受"));

	dialog.setChecked(tvLiveWithParents.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvLiveWithParents.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {

		if (!TextUtils.isEmpty(input)) {
		    tvLiveWithParents.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvLiveWithParents.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    private void selectPremartialSex() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("是否接受婚前性行为");

	dialog.addMenuItem(dialog.new MenuItem("无法接受"));
	dialog.addMenuItem(dialog.new MenuItem("看情况"));// 
	dialog.addMenuItem(dialog.new MenuItem("可以接受"));

	dialog.setChecked(tvPremartialSex.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvPremartialSex.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvPremartialSex.setText(input);
		} else {

		    if (checkedItems.size() != 0) {
			tvPremartialSex.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    private void selectDistanceLove() {
	// TODO Auto-generated method stub
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("是否接受异地恋");

	dialog.addMenuItem(dialog.new MenuItem("可以接受"));
	dialog.addMenuItem(dialog.new MenuItem("看情况"));// 
	dialog.addMenuItem(dialog.new MenuItem("不能接受"));

	dialog.setChecked(tvDistanceLove.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvDistanceLove.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvDistanceLove.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvDistanceLove.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    private void selectEmployment() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("职业");

	dialog.addMenuItem(dialog.new MenuItem("计算机/互联网/通信"));
	dialog.addMenuItem(dialog.new MenuItem("生产/工艺/制造"));// 
	dialog.addMenuItem(dialog.new MenuItem("医疗/护理/制药"));
	dialog.addMenuItem(dialog.new MenuItem("金融/银行/投资/保险"));
	dialog.addMenuItem(dialog.new MenuItem("商业/服务业/个体经营"));
	dialog.addMenuItem(dialog.new MenuItem("文化/广告/传媒"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("娱乐/艺术/表演"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("律师/法务"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("教育/培训"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("公务员/行政/事业单位"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("模特"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("空姐"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("学生"));// 只有添加的第一个是选中状态

	dialog.setChecked(tvEmployment.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(tvEmployment.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvEmployment.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvEmployment.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    /**
     * 学历
     */
    private void selectEdu() {
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("学历");

	dialog.addMenuItem(dialog.new MenuItem("小学"));
	dialog.addMenuItem(dialog.new MenuItem("初中"));
	dialog.addMenuItem(dialog.new MenuItem("中专"));
	dialog.addMenuItem(dialog.new MenuItem("大专"));// 
	dialog.addMenuItem(dialog.new MenuItem("本科"));
	dialog.addMenuItem(dialog.new MenuItem("双学士"));
	dialog.addMenuItem(dialog.new MenuItem("硕士"));
	dialog.addMenuItem(dialog.new MenuItem("博士"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("博士后"));

	dialog.setChecked(etEdu.getText().toString());//只有添加的第一个是选中状态
	dialog.setInputContent(etEdu.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    etEdu.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			etEdu.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    /**
     * 
     */
    private void selectHouse() {
	// TODO Auto-generated method stub
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("住房");

	dialog.addMenuItem(dialog.new MenuItem("已购房"));
	dialog.addMenuItem(dialog.new MenuItem("与父母同住"));
	dialog.addMenuItem(dialog.new MenuItem("租房"));// 只有添加的第一个是选中状态

	dialog.setChecked(tvHouse.getText().toString());
	dialog.setInputContent(tvHouse.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvHouse.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvHouse.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    private void selectCharmBody() {
	// TODO Auto-generated method stub

	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("魅力部位");
	dialog.addMenuItem(dialog.new MenuItem("笑容"));
	dialog.addMenuItem(dialog.new MenuItem("眉毛"));
	dialog.addMenuItem(dialog.new MenuItem("眼睛"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("头发"));
	dialog.addMenuItem(dialog.new MenuItem("鼻梁"));
	dialog.addMenuItem(dialog.new MenuItem("嘴唇"));
	dialog.addMenuItem(dialog.new MenuItem("牙齿"));
	dialog.addMenuItem(dialog.new MenuItem("颈部"));
	dialog.addMenuItem(dialog.new MenuItem("耳朵"));
	dialog.addMenuItem(dialog.new MenuItem("手"));
	dialog.addMenuItem(dialog.new MenuItem("胳膊"));
	dialog.addMenuItem(dialog.new MenuItem("胸部"));
	dialog.addMenuItem(dialog.new MenuItem("腰部"));
	dialog.addMenuItem(dialog.new MenuItem("脚"));
	dialog.addMenuItem(dialog.new MenuItem("腿"));
	dialog.addMenuItem(dialog.new MenuItem("臀部"));

	dialog.setChecked(tvCharmBody.getText().toString());
	dialog.setInputContent(tvCharmBody.getText().toString());

	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvCharmBody.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvCharmBody.setText(checkedItems.get(0).getName() + input);
		    }
		}
	    }
	});
    }

    /**
     * 选择头像上传
     */
    private void selectAvatarPhoto() {

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

    /**
     * 选择空间背景图上传
     */
    private void selectSpaceBackground() {
	showToast("上传封面图");
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

	    if (bitmap != null && bitmap.isRecycled()) {
		bitmap.recycle();
	    }
	    // 上传头像
	    uploadFace(file);
	}

    }

    private void uploadFace(String file) {
	// TODO Auto-generated method stub
	final ProgressDialog progress = new ProgressDialog(this);
	progress.setCancelable(false);
	progress.setMessage("头像上传中...");
	progress.show();

	UploadFileParams params = new UploadFileParams();
	params.userId = 2;
	params.filePathes = new ArrayList<String>();
	params.filePathes.add(file);

	AppServiceImpl.getInstance().uploadFace(params, new OnUploadFaceListener() {

	    @Override
	    public void onSuccess(UploadFaceResponse uploadFileResponse) {

		progress.dismiss();
		showToast("上传成功!");
		String imageUrl = Constants.HOST + uploadFileResponse.imageUrl.substring(1);
		//		ActivityUtil.openBrowser(EditMyInfoActivity.this, imageUrl);
		System.out.println(imageUrl);

		// 修改内存中的资料
		LoginedUser user = MyApplication.getInstance().getCurrentLoginedUser();
		user.setFaceUrl(imageUrl);

		ImageLoader.getInstance().displayImage(imageUrl, ivMyFace);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		showToast(errorMsg);
		progress.dismiss();
	    }
	});
    }

    /**
     *情感状况
     */
    private void selectMaritalStatus() {

	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("情感状态");

	dialog.addMenuItem(dialog.new MenuItem("保密"));
	dialog.addMenuItem(dialog.new MenuItem("单身"));
	dialog.addMenuItem(dialog.new MenuItem("恋爱中"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("已婚"));
	dialog.addMenuItem(dialog.new MenuItem("离异"));

	dialog.setChecked(tvMaritalStatus.getText().toString());
	dialog.setInputContent(tvMaritalStatus.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvMaritalStatus.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvMaritalStatus.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});

    }

    /**
     * 输入出生日期
     */
    private void inputMyBirthday() {

	SelectDatePopupWindow birth = new SelectDatePopupWindow(this, this.tvBirthday.getText().toString());

	birth.setOutsideTouchable(false);

	birth.showAtLocation(this.mMainView, Gravity.CENTER, 0, 0);

	birth.setOnSubmitListener(new OnSubmitListener() {

	    @Override
	    public void onSelected(int year, int month, int day) {

		mCurrentInfoUser.setBirthdayYear(year);
		mCurrentInfoUser.setBirthdayMonth(month);
		mCurrentInfoUser.setBirthdayDay(day);

		tvBirthday.setText(year + "-" + month + "-" + day);
	    }
	});
    }

    SelectProvinceCityPopupWindow cityPopupWindow;

    private void selectHomeplace() {

	//	
	if (null == cityPopupWindow) {
	    cityPopupWindow = new SelectProvinceCityPopupWindow(this, this.tvBirthday.getText().toString());

	    cityPopupWindow.setOutsideTouchable(false);

	    cityPopupWindow.setFocusable(true);
	    cityPopupWindow.setBackgroundDrawable(new BitmapDrawable());

	    cityPopupWindow.setWidth(ActivityUtil.getScreenSize()[0]);

	    cityPopupWindow.setHeight(ActivityUtil.getScreenSize()[1]);
	    cityPopupWindow.setOnSubmitListener(new SelectProvinceCityPopupWindow.OnSubmitListener() {

		@Override
		public void onSubmit(String selectedCity) {
		    // TODO Auto-generated method stub
		    tvHomeplace.setText(selectedCity);
		}
	    });
	}
	if (!cityPopupWindow.isShowing()) {
	    cityPopupWindow.showAtLocation(this.mMainView, Gravity.CENTER, 0, 0);
	}
    }

    /**
     * 
     */
    SelectProvinceCityPopupWindow cityPopupWindow2;

    private void selectAddress() {
	if (null == cityPopupWindow2) {
	    cityPopupWindow2 = new SelectProvinceCityPopupWindow(this, this.tvBirthday.getText().toString());

	    cityPopupWindow2.setOutsideTouchable(false);

	    cityPopupWindow2.setFocusable(true);
	    cityPopupWindow2.setBackgroundDrawable(new BitmapDrawable());

	    cityPopupWindow2.setWidth(ActivityUtil.getScreenSize()[0]);

	    cityPopupWindow2.setHeight(ActivityUtil.getScreenSize()[1]);
	    cityPopupWindow2.setOnSubmitListener(new SelectProvinceCityPopupWindow.OnSubmitListener() {

		@Override
		public void onSubmit(String selectedCity) {
		    // TODO Auto-generated method stub
		    tvAddress.setText(selectedCity);
		}
	    });
	}
	if (!cityPopupWindow2.isShowing()) {
	    cityPopupWindow2.showAtLocation(this.mMainView, Gravity.CENTER, 0, 0);

	}
    }

    SelectProvinceCityPopupWindow cityPopupWindow3;

    /**
     * 选择征友要求家乡
     */
    private void selectWantedHomeplace() {
	if (null == cityPopupWindow3) {
	    cityPopupWindow3 = new SelectProvinceCityPopupWindow(this, this.tvBirthday.getText().toString());

	    cityPopupWindow3.setOutsideTouchable(false);

	    cityPopupWindow3.setFocusable(true);
	    cityPopupWindow3.setBackgroundDrawable(new BitmapDrawable());

	    cityPopupWindow3.setWidth(ActivityUtil.getScreenSize()[0]);

	    cityPopupWindow3.setHeight(ActivityUtil.getScreenSize()[1]);
	    cityPopupWindow3.setOnSubmitListener(new SelectProvinceCityPopupWindow.OnSubmitListener() {

		@Override
		public void onSubmit(String selectedCity) {
		    // TODO Auto-generated method stub
		    tvWantedHomeplace.setText(selectedCity);
		}
	    });
	}
	if (!cityPopupWindow3.isShowing()) {
	    cityPopupWindow3.showAtLocation(this.mMainView, Gravity.CENTER, 0, 0);

	}
    }

    SelectProvinceCityPopupWindow cityPopupWindow4;

    /**
     * 选择征友要求现居地
     */
    private void selectWantedAddress() {
	if (null == cityPopupWindow4) {
	    cityPopupWindow4 = new SelectProvinceCityPopupWindow(this, this.tvBirthday.getText().toString());

	    cityPopupWindow4.setOutsideTouchable(false);

	    cityPopupWindow4.setFocusable(true);
	    cityPopupWindow4.setBackgroundDrawable(new BitmapDrawable());

	    cityPopupWindow4.setWidth(ActivityUtil.getScreenSize()[0]);

	    cityPopupWindow4.setHeight(ActivityUtil.getScreenSize()[1]);
	    cityPopupWindow4.setOnSubmitListener(new SelectProvinceCityPopupWindow.OnSubmitListener() {

		@Override
		public void onSubmit(String selectedCity) {
		    // TODO Auto-generated method stub
		    tvWantedAddress.setText(selectedCity);
		}
	    });
	}
	if (!cityPopupWindow4.isShowing()) {
	    cityPopupWindow4.showAtLocation(this.mMainView, Gravity.CENTER, 0, 0);

	}
    }

    /**
     * 年龄要求
     */
    private void selectWantedAge() {
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("年龄要求");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("20-23"));
	dialog.addMenuItem(dialog.new MenuItem("24-26"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("27-29"));
	dialog.addMenuItem(dialog.new MenuItem("30-32"));
	dialog.addMenuItem(dialog.new MenuItem("33-35"));
	dialog.addMenuItem(dialog.new MenuItem("36-40"));
	dialog.addMenuItem(dialog.new MenuItem("41-45"));
	dialog.addMenuItem(dialog.new MenuItem("46-50"));
	dialog.addMenuItem(dialog.new MenuItem("51-60"));

	dialog.setChecked(tvWantedAge.getText().toString());
	dialog.setInputContent(tvWantedAge.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvWantedAge.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvWantedAge.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 身高要求
     */
    private void selectWantedHeight() {
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("年龄要求");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("150-165"));
	dialog.addMenuItem(dialog.new MenuItem("165-170"));
	dialog.addMenuItem(dialog.new MenuItem("170-175"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("175-180"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("180-190"));
	dialog.addMenuItem(dialog.new MenuItem("190以上"));

	dialog.setChecked(tvWantedHeight.getText().toString());
	dialog.setInputContent(tvWantedHeight.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvWantedHeight.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvWantedHeight.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 学历要求
     */
    private void selectWantedEducation() {
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("学历要求");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("小学"));
	dialog.addMenuItem(dialog.new MenuItem("初中"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("高中及中专"));
	dialog.addMenuItem(dialog.new MenuItem("大专"));
	dialog.addMenuItem(dialog.new MenuItem("本科"));
	dialog.addMenuItem(dialog.new MenuItem("硕士及以上"));

	dialog.setChecked(tvWantedEducation.getText().toString());
	dialog.setInputContent(tvWantedEducation.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvWantedEducation.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvWantedEducation.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    /**
     * 最低收入
     */
    private void selectWantedSalary() {
	ListMenuDialog dialog = new ListMenuDialog(this, false); // 单选模式
	dialog.setTitle("最低收入");

	dialog.addMenuItem(dialog.new MenuItem("不限"));
	dialog.addMenuItem(dialog.new MenuItem("2000以内"));
	dialog.addMenuItem(dialog.new MenuItem("2000-5000"));// 只有添加的第一个是选中状态
	dialog.addMenuItem(dialog.new MenuItem("5000-10000"));
	dialog.addMenuItem(dialog.new MenuItem("10000-20000"));
	dialog.addMenuItem(dialog.new MenuItem("20000-30000"));
	dialog.addMenuItem(dialog.new MenuItem("30000以上"));

	dialog.setChecked(tvWantedSalary.getText().toString());
	dialog.setInputContent(tvWantedSalary.getText().toString());
	dialog.show();
	dialog.setOnSubmitListener(new ListMenuDialog.OnSubmitListener() {

	    @Override
	    public void onSubmit(List<MenuItem> checkedItems, String input) {
		if (!TextUtils.isEmpty(input)) {
		    tvWantedSalary.setText(input);
		} else {
		    if (checkedItems.size() != 0) {
			tvWantedSalary.setText(checkedItems.get(0).getName());
		    }
		}
	    }
	});
    }

    private boolean isChanged;

    @Override
    public void onBackPressed() {

	isChanged = true;

	// TODO Auto-generated method stub
	if (isChanged) {

	    AlertDialog dialog = new AlertDialog.Builder(this).create();
	    dialog.setTitle("提示");
	    dialog.setMessage("是否保存");
	    dialog.setButton(AlertDialog.BUTTON_POSITIVE, "保存", new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    // TODO Auto-generated method stub
		    saveMyInfo();
		}
	    });

	    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "不保存", new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    // TODO Auto-generated method stub
		    finish();
		}
	    });
	    dialog.show();

	} else {
	    super.onBackPressed();
	}
    }
}
