package me.himi.love.ui;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.GetUserBlacklistCallback;
import io.rong.imkit.RongIM.OperationCallback;
import io.rong.imlib.RongIMClient.BlacklistStatus;
import me.himi.love.AppServiceExtendImpl;
import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnLoadDetailUserListener;
import me.himi.love.IAppServiceExtend.FollowParams;
import me.himi.love.IAppServiceExtend.OnFollowResponseListener;
import me.himi.love.IAppServiceExtend.OnSayHiResponseListener;
import me.himi.love.IAppServiceExtend.SayHiParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.dao.DBHelper;
import me.himi.love.dao.DBHelper.UserFollow;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.LoginedUser;
import me.himi.love.entity.loader.IUserDetailLoader;
import me.himi.love.entity.loader.impl.UserDetailLoaderImpl;
import me.himi.love.entity.loader.impl.UserNewsLoaderImpl;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.ui.fragment.UserInfoPage1Fragment;
import me.himi.love.ui.fragment.UserInfoPage2Fragment;
import me.himi.love.ui.fragment.UserInfoPage3Fragment;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.CacheUserManager;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.EmojiTextView;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserInfoTextActivity extends BaseActivity implements OnClickListener {
    int mUserId; // 目标用户ID

    DetailInfoUser mTargetUser; // 目标用户

    IUserDetailLoader userLoader = new UserDetailLoaderImpl();

    View layoutUserBackgournd; // 用户设置的顶部背景图

    ImageView ivUserFace; // 用户头像
    ImageView ivVipTop; // vip皇冠

    String mIntentUserFaceUrl;// 传递过来的 用户头像 url
    boolean mIsVip; // 是否为vip

    EmojiTextView tvTopTitle, tvTopAction;

    View layoutBottomMenu, layoutBottomMenuEdit; // 底部 菜单的布局容器, 其他用户的资料页菜单, 当前登录用户自己的资料页菜单

    //
    TextView[] tvTabs;
    TextView tvTabPage1, tvTabPage2, tvTabPage3;

    View mContentView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mContentView = getLayoutInflater().inflate(R.layout.activity_userinfo_text, null);

	setContentView(mContentView);

	init();
    }

    private UserInfoPage1Fragment page1Fragment;
    private UserInfoPage2Fragment page2Fragment;
    private UserInfoPage3Fragment page3Fragment;

    private Fragment[] mFragments;

    private int mCurrentPageIndex;
    private int lineWidth;
    private View mCursorLine;

    private void init() {

	page1Fragment = new UserInfoPage1Fragment();
	page2Fragment = new UserInfoPage2Fragment();
	page3Fragment = new UserInfoPage3Fragment();

	mFragments = new Fragment[] { page1Fragment, page2Fragment, page3Fragment };

	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	transaction.add(R.id.fl_container, page1Fragment);
	transaction.add(R.id.fl_container, page2Fragment);
	transaction.add(R.id.fl_container, page3Fragment);
	transaction.show(page1Fragment);
	transaction.hide(page2Fragment);
	transaction.hide(page3Fragment);
	transaction.commit();

	mCursorLine = findViewById(R.id.iv_nearby_bottom_line);

	ViewGroup.LayoutParams params = mCursorLine.getLayoutParams();
	params.width = ActivityUtil.getScreenSize()[0] / 3; // 3 个页面
	lineWidth = params.width;
	mCursorLine.setLayoutParams(params); // 设置光标线宽度

	tvTabPage1 = (TextView) findViewById(R.id.tv_tab_page1);
	tvTabPage2 = (TextView) findViewById(R.id.tv_tab_page2);
	tvTabPage3 = (TextView) findViewById(R.id.tv_tab_page3);
	tvTabPage1.setOnClickListener(this);
	tvTabPage2.setOnClickListener(this);
	tvTabPage3.setOnClickListener(this);

	tvTabs = new TextView[] { tvTabPage1, tvTabPage2, tvTabPage3 };
	tvTabs[0].setTextColor(getResources().getColor(R.color.cursor_line));

	// intent 传递过来的值
	mUserId = getIntent().getIntExtra("user_id", 0);
	// 昵称
	String userNickname = getIntent().getStringExtra("user_nickname");
	//头像
	mIntentUserFaceUrl = getIntent().getStringExtra("user_face_url");
	// vip
	mIsVip = getIntent().getBooleanExtra("is_vip", false);

	// 顶部标题操作栏
	tvTopTitle = (EmojiTextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);

	tvTopAction = (EmojiTextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	// 底部菜单
	layoutBottomMenu = findViewById(R.id.layout_bottom_menu);
	// 底部菜单(当前用户的资料)
	layoutBottomMenuEdit = findViewById(R.id.layout_bottom_menu_self);

	// 关注
	final TextView btnFollow = (TextView) findViewById(R.id.btn_follow);
	// 查询本地数据库中是否存在关注
	final DBHelper db = DBHelper.getInstance(UserInfoTextActivity.this, DBHelper.DB_NAME, null, 1);
	final UserFollow follow = db.queryFollow(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), mUserId);

	btnFollow.setText(follow != null ? "取消关注" : "关注");
	btnFollow.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// 查询本地数据库中是否存在关注
		if ("取消关注".equals(btnFollow.getText())) {
		    showToast("取消关注处理中...");
		    FollowParams postParams = new FollowParams();
		    postParams.followUserId = mUserId;
		    AppServiceExtendImpl.getInstance().unfollow(postParams, new OnFollowResponseListener() {

			@Override
			public void onSuccess(int status) {
			    // TODO Auto-generated method stub
			    if (status == -1) { // 没有关注该用户
				btnFollow.setText("关注");
				return;
			    }
			    db.deleteFollow(MyApplication.getInstance().getCurrentLoginedUser().getUserId(), mUserId);
			    db.close();
			    btnFollow.setText("关注");
			}

			@Override
			public void onFailure(String errorMsg) {
			    // TODO Auto-generated method stub
			    showToast(errorMsg);
			}
		    });
		} else {
		    FollowParams postParams = new FollowParams();
		    postParams.followUserId = mUserId;

		    showToast("关注处理中...");
		    AppServiceExtendImpl.getInstance().follow(postParams, new OnFollowResponseListener() {

			@Override
			public void onSuccess(int status) {
			    // TODO Auto-generated method stub
			    if (status == -1) { // 已经关注该用户
				btnFollow.setText("取消关注");
				return;
			    }
			    UserFollow follow = new UserFollow();
			    follow.userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
			    follow.followUserId = mUserId;
			    db.insertFollow(follow);
			    db.close();
			    btnFollow.setText("取消关注");
			}

			@Override
			public void onFailure(String errorMsg) {
			    // TODO Auto-generated method stub
			    showToast(errorMsg);
			}
		    });
		}

	    }
	});

	// 举报/拉黑
	TextView btnReport = (TextView) findViewById(R.id.btn_userinfo_more);
	btnReport.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mTargetUser == null) {
		    showToast("加载当前用户资料未完成");
		    return;
		}
		showReportDialog();
	    }
	});

	// 打招呼
	TextView btnSayhi = (TextView) findViewById(R.id.btn_start_sayhi);
	btnSayhi.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

		if (mTargetUser != null) {
		    // todo
		    showSayhiDialog();
		}
	    }
	});

	// 聊天
	TextView btnStartIM = (TextView) findViewById(R.id.btn_start_im);
	btnStartIM.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

		if (mTargetUser != null) {
		    // todo
		    // 启动聊天界面
		    RongIM.getInstance().startPrivateChat(UserInfoTextActivity.this, mTargetUser.getUserId() + "", mTargetUser.getNickname());
		}
	    }
	});

	// 送礼物
	TextView btnShare = (TextView) findViewById(R.id.btn_start_giveGift);
	btnShare.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {

		if (mTargetUser != null) {
		    // todo
		    //		    Share.share(UserInfoTextActivity.this, "我正在使用" + getResources().getString(R.string.app_name) + "征婚交友APP,这是" + mUserId + "的个人主页,帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\"");

		    Intent intent = new Intent(UserInfoTextActivity.this, GiftChooseActivity.class);
		    intent.putExtra("user_id", mTargetUser.getUserId() + "");
		    intent.putExtra("nickname", mTargetUser.getNickname());
		    intent.putExtra("gender", mTargetUser.getGender());
		    int requestCode = 1;
		    startActivityForResult(intent, requestCode);
		}
	    }
	});

	// 编辑资料
	layoutBottomMenuEdit.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// todo
		//		    Share.share(UserInfoTextActivity.this, "我正在使用" + getResources().getString(R.string.app_name) + "征婚交友APP,这是" + mUserId + "的个人主页,帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\"");
		Intent intent = new Intent(UserInfoTextActivity.this, EditMyInfoActivity.class);
		// 已有数据则直接传递过去,否则直接跳转让其自己加载
		startActivityForResult(intent, EditMyInfoActivity.EDIT_MY_INFO_RESULT_CODE);
	    }
	});

	// 是当前登录用户则区分绘制不同的底部菜单
	if (isCurrentLoginedUser()) {
	    tvTopTitle.setEmojiText("我");
	    layoutBottomMenu.setVisibility(View.GONE);
	    layoutBottomMenuEdit.setVisibility(View.VISIBLE);

	    tvTopAction.setVisibility(View.VISIBLE);
	    tvTopAction.setText("编辑");

	    tvTopAction.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    Intent intent = new Intent(UserInfoTextActivity.this, EditMyInfoActivity.class);
		    // 已有数据则直接传递过去,否则直接跳转让其自己加载
		    startActivityForResult(intent, EditMyInfoActivity.EDIT_MY_INFO_RESULT_CODE);
		}
	    });
	} else {
	    tvTopTitle.setEmojiText("正在查看 " + userNickname + " 的资料");
	    layoutBottomMenu.setVisibility(View.VISIBLE);
	    layoutBottomMenuEdit.setVisibility(View.GONE);
	    tvTopAction.setVisibility(View.GONE);
	}

	if (mUserId == 0) {
	    ActivityUtil.show(this, "没有指定用户ID");
	    finish();
	}
	int height = getIntent().getIntExtra("user_height", 0);
	int weight = getIntent().getIntExtra("user_weight", 0);

	// 用户设置的背景和头像
	layoutUserBackgournd = findViewById(R.id.layout_user_background);
	// 头像
	ivUserFace = (ImageView) findViewById(R.id.iv_user_face);

	// vip皇冠图标
	ivVipTop = getViewById(R.id.iv_user_face_top);
	ivVipTop.setVisibility(mIsVip ? View.VISIBLE : View.GONE);

	// 点击头像进入大图浏览
	ivUserFace.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(UserInfoTextActivity.this, ZoombleImageActivity.class);
		intent.putExtra("image_url", mIntentUserFaceUrl);
		intent.putExtra("default_small_image_url", UserNewsLoaderImpl.getSmallImageUrl(mIntentUserFaceUrl));
		startActivity(intent);
	    }
	});

	// 加载并显示用户的头像
	ImageLoader.getInstance().displayImage(mIntentUserFaceUrl, ivUserFace, ImageLoaderOptions.rounderOptions());

	// 用户昵称
	tvTopTitle = (EmojiTextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	tvTopTitle.setEmojiText(userNickname);

	loadTargetUserInfo();

    }

    private void loadTargetUserInfo() {

	final ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
	pbLoading.setVisibility(View.VISIBLE);

	final TextView tvRetry = (TextView) findViewById(R.id.tv_load_retry); // 加载失败时点击重试
	tvRetry.setVisibility(View.GONE);

	AppServiceImpl.getInstance().loadUserInfo(mUserId, new OnLoadDetailUserListener() {

	    @Override
	    public void onSuccess(DetailInfoUser detailInfoUser) {

		pbLoading.setVisibility(View.GONE);

		if (detailInfoUser == null) {
		    ActivityUtil.show(UserInfoTextActivity.this, "!-_-加载出错了!\n\n请重新注册/登录");
		    return;
		}

		// 加入缓存,过期时长为 60秒
		CacheUserManager.getInstance().addUser(mUserId + "", detailInfoUser, 1000 * 10);

		mTargetUser = detailInfoUser;

		// 加载其他页面数据
		setInfos(detailInfoUser);

		ImageLoader.getInstance().displayImage(detailInfoUser.getFaceUrl(), ivUserFace, ImageLoaderOptions.rounderOptions());

		// 如果加载的是当前登录的用户的资料则更新当前内存中的登录用户loginedUser
		if (isCurrentLoginedUser()) {
		    updateCurrentLoginedUser(detailInfoUser);
		}

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		pbLoading.setVisibility(View.GONE);

		tvRetry.setVisibility(View.VISIBLE);
		tvRetry.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
			// TODO Auto-generated method stub
			loadTargetUserInfo();
		    }
		});
		ActivityUtil.show(UserInfoTextActivity.this, errorMsg);
	    }
	});

    }

    /**
     * 
     * @param user
     */
    private void setInfos(DetailInfoUser user) {
	if (this.page1Fragment.isDetached() || this.page2Fragment.isDetached()) {
	    return;
	}

	// 容易报错...
	try {
	    this.page1Fragment.setInfos(user);
	    this.page2Fragment.setInfos(user);
	    this.page3Fragment.load(user);
	} catch (Throwable th) {

	}

    }

    /**
     * 简单判断当前是否为本机登录的用户
     * @return
     */
    private boolean isCurrentLoginedUser() {
	return this.mUserId == MyApplication.getInstance().getCurrentLoginedUser().getUserId();
    }

    /**
     * 更新当前用户的资料
     * @param user
     */
    private void updateCurrentLoginedUser(DetailInfoUser user) {
	if (user == null) {
	    return;
	}
	LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
	loginedUser.setNickname(user.getNickname());
	loginedUser.setFaceUrl(user.getFaceUrl());
	MyApplication.getInstance().setCurrentLoginedUser(loginedUser);
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	TranslateAnimation anim = null;
	int index = 0;
	switch (v.getId()) {
	case R.id.tv_tab_page1:
	    index = 0;
	    anim = new TranslateAnimation(mCurrentPageIndex * lineWidth, 0, 0, 0);
	    break;
	case R.id.tv_tab_page2:
	    index = 1;
	    anim = new TranslateAnimation(mCurrentPageIndex * lineWidth, lineWidth, 0, 0);
	    break;
	case R.id.tv_tab_page3:
	    index = 2;
	    anim = new TranslateAnimation(mCurrentPageIndex * lineWidth, lineWidth * 2, 0, 0);
	    break;
	}

	if (mCurrentPageIndex == index) {
	    return;
	}

	tvTabs[mCurrentPageIndex].setTextColor(getResources().getColor(R.color.base_color_text_black));

	// animation interpolator
	//		anim.setInterpolator(new CycleInterpolator(0.2f));
	anim.setDuration(100);
	anim.setFillAfter(true); // 停留在最后一帧
	mCursorLine.startAnimation(anim);

	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	transaction.show(mFragments[index]);
	transaction.hide(mFragments[mCurrentPageIndex]);
	transaction.commit();

	mCurrentPageIndex = index;

	tvTabs[mCurrentPageIndex].setTextColor(getResources().getColor(R.color.cursor_line));

    }

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();

	// 如果当前目标用户ID为当前登录用户ID则更新数据(编辑资料后返回)
	if (mTargetUser != null && (loginedUser.getUserId() == mTargetUser.getUserId())) {
	    DetailInfoUser infoUser = loginedUser.getDetailInfoUser();
	    if (null != infoUser) {
		setInfos(infoUser);
	    }
	}
	super.onResume();
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	HttpUtil.getClient().cancelAllRequests(true);

	super.onDestroy();
    }

    /**
     * 举报/拉黑
     */
    private void showReportDialog() {

	View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_menu_report, null);
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
	popupWindow.showAtLocation(this.mContentView, Gravity.BOTTOM, 0, 0);

	// 加入黑名单 或 移出黑名单
	final TextView tvBlackList = (TextView) contentView.findViewById(R.id.tv_blacklist);

	// 取消
	contentView.findViewById(R.id.layout_report_cancel).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		popupWindow.dismiss();
	    }
	});

	RongIM.getInstance().getBlacklistStatus(mUserId + "", new GetUserBlacklistCallback() {

	    @Override
	    public void onSuccess(BlacklistStatus arg0) {
		// TODO Auto-generated method stub
		if (arg0 == BlacklistStatus.NOT_EXIT_BLACK_LIST) {
		    tvBlackList.setText("加入黑名单");
		} else {
		    tvBlackList.setText("解除黑名单");
		}
	    }

	    @Override
	    public void onError(io.rong.imkit.RongIM.GetUserBlacklistCallback.ErrorCode arg0) {
		// TODO Auto-generated method stub

	    }
	});

	//	// 从黑名单删除
	//	RongIM.getInstance().removeFromBlacklist(mUserId + "", null);

	// 加入黑名单
	contentView.findViewById(R.id.layout_addtoblacklist).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		popupWindow.dismiss();

		RongIM.getInstance().getBlacklistStatus(mUserId + "", new GetUserBlacklistCallback() {

		    @Override
		    public void onSuccess(BlacklistStatus arg0) {
			// TODO Auto-generated method stub
			// 还未加入黑名单
			if (arg0 == BlacklistStatus.NOT_EXIT_BLACK_LIST) {
			    // 加入黑名单
			    RongIM.getInstance().addToBlacklist(mUserId + "", new OperationCallback() {

				@Override
				public void onSuccess() {
				    // TODO Auto-generated method stub
				    showToast("加入黑名单成功");
				}

				@Override
				public void onError(ErrorCode arg0) {
				    // TODO Auto-generated method stub
				    showToast("加入黑名单失败");
				}
			    });
			} else {
			    RongIM.getInstance().removeFromBlacklist(mUserId + "", new OperationCallback() {

				@Override
				public void onError(ErrorCode arg0) {
				    // TODO Auto-generated method stub
				    showToast("解除黑名单失败");
				}

				@Override
				public void onSuccess() {
				    // TODO Auto-generated method stub
				    showToast("已解除黑名单");
				}

			    });
			}
		    }

		    @Override
		    public void onError(ErrorCode arg0) {
			// TODO Auto-generated method stub

		    }
		});

	    }
	});

	// 举报并加入黑名单
	contentView.findViewById(R.id.layout_report).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub

		// 举报 
		// todo:

		popupWindow.dismiss();

		// 举报成功
		showToast("举报成功");

		RongIM.getInstance().getBlacklistStatus(mUserId + "", new GetUserBlacklistCallback() {

		    @Override
		    public void onSuccess(BlacklistStatus arg0) {
			// TODO Auto-generated method stub
			// 还未加入黑名单
			if (arg0 == BlacklistStatus.NOT_EXIT_BLACK_LIST) {
			    // 加入黑名单
			    RongIM.getInstance().addToBlacklist(mUserId + "", new OperationCallback() {

				@Override
				public void onSuccess() {
				    // TODO Auto-generated method stub
				    showToast("加入黑名单成功");
				}

				@Override
				public void onError(ErrorCode arg0) {
				    // TODO Auto-generated method stub
				    showToast("加入黑名单失败");
				}
			    });
			} else {
			    showToast("加入黑名单成功");
			    //			    RongIM.getInstance().removeFromBlacklist(mUserId + "", new OperationCallback() {
			    //
			    //				@Override
			    //				public void onError(ErrorCode arg0) {
			    //				    // TODO Auto-generated method stub
			    //				    showToast("解除黑名单失败");
			    //				}
			    //
			    //				@Override
			    //				public void onSuccess() {
			    //				    // TODO Auto-generated method stub
			    //				    showToast("已解除黑名单");
			    //				}
			    //
			    //			    });
			}
		    }

		    @Override
		    public void onError(ErrorCode arg0) {
			// TODO Auto-generated method stub

		    }
		});
	    }
	});

    }

    private Dialog sayhiDialog;
    private View sayhiView;

    private void showSayhiDialog() {
	if (null == sayhiDialog) {
	    sayhiView = LayoutInflater.from(this).inflate(R.layout.dialog_sayhi, null);

	    final EditText etContent = (EditText) sayhiView.findViewById(R.id.et_sayhi);
	    // 可以从服务器上加载推荐打招呼的内容
	    String[] contents = { "你好,看了你的资料发现你就是我一直在等待的那个人,能认识下么? 期待你的回复!", "恳请关注 ! ! !, 仰慕你很久了" };
	    etContent.setText(contents[(int) (Math.random() * 100) % contents.length]);

	    sayhiDialog = new Dialog(this);
	    sayhiDialog.setTitle("向 " + mTargetUser.getNickname() + " 打招呼");

	    sayhiView.findViewById(R.id.tv_send_sayhi).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    if (TextUtils.isEmpty(etContent.getText())) {
			ToastFactory.getToast(UserInfoTextActivity.this, "内容不能为空").show();
			return;
		    }
		    sendSayhi(etContent.getText().toString());
		    sayhiDialog.dismiss();
		}
	    });

	    sayhiView.findViewById(R.id.tv_cancel_send_sayhi).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    sayhiDialog.dismiss();
		}
	    });

	    sayhiDialog.show();

	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    sayhiDialog.addContentView(sayhiView, params);
	}
	if (!sayhiDialog.isShowing()) {

	    sayhiDialog.setTitle("向 " + mTargetUser.getNickname() + " 打招呼");

	    sayhiDialog.show();
	}
    }

    ProgressDialog progressDialog;

    private void sendSayhi(String msg) {
	if (null == progressDialog) {
	    progressDialog = new ProgressDialog(this);
	}
	progressDialog.setMessage("打招呼发送中...");
	progressDialog.show();
	SayHiParams params = new SayHiParams();
	params.fromUserId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	params.toUserId = mTargetUser.getUserId();
	params.content = msg;
	AppServiceExtendImpl.getInstance().sayhi(params, new OnSayHiResponseListener() {

	    @Override
	    public void onSuccess(int money, String msg) {
		progressDialog.dismiss();

		ToastFactory.getToast(UserInfoTextActivity.this, msg).show();
		LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
		loginedUser.setLoveMoney(money);
	    }

	    @Override
	    public void onFailure(String msg) {
		progressDialog.dismiss();
		// TODO Auto-generated method stub
		ToastFactory.getToast(UserInfoTextActivity.this, msg).show();
	    }
	});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
	// TODO Auto-generated method stub
	if (requestCode == 1) {
	    if (resultCode == RESULT_OK) { // 选中礼物确认提交
		//		final ProgressDialog dialog = new ProgressDialog(this);
		//		dialog.setMessage("赠送中...");
		//		dialog.show();
		//
		//		String targetUserId = arg2.getStringExtra("user_id");
		//		String targetGiftId = arg2.getStringExtra("gift_id");
		//		String word = arg2.getStringExtra("word"); // 赠言
		//
		//		GiveGiftPostParams postParams = new GiveGiftPostParams();
		//		postParams.giftId = targetGiftId;
		//		postParams.toUserId = targetUserId;
		//		postParams.word = word;
		//
		//		AppServiceExtendImpl.getInstance().giveGift(postParams, new OnGiveGiftResponseListener() {
		//
		//		    @Override
		//		    public void onSuccess(UserGift userGift) {
		//			// TODO Auto-generated method stub
		//			showToast("已赠送!");
		//			dialog.dismiss();
		//		    }
		//
		//		    @Override
		//		    public void onFailure(String errorMsg) {
		//			// TODO Auto-generated method stub
		//			showToast(errorMsg);
		//			dialog.dismiss();
		//		    }
		//		});
	    }
	}

	super.onActivityResult(requestCode, resultCode, arg2);
    }
}
