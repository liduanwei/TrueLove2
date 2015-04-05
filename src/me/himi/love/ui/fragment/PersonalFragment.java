package me.himi.love.ui.fragment;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ConnectCallback.ErrorCode;
import me.himi.love.AppServiceExtendImpl;
import me.himi.love.AppServiceImpl;
import me.himi.love.AppServiceRongCloudIMImpl;
import me.himi.love.RongIMEvent;
import me.himi.love.IAppService.OnLoadDetailUserListener;
import me.himi.love.IAppServiceExtend.HomeInfo;
import me.himi.love.IAppServiceExtend.LoadHomeInfoPostParams;
import me.himi.love.IAppServiceExtend.OnLoadHomeInfoResponseListener;
import me.himi.love.IAppServiceExtend.OnSigninResponseListener;
import me.himi.love.IAppServiceExtend.SigninPostParams;
import me.himi.love.IAppServiceRongCloudIM.OnTokenResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.LoginedUser;
import me.himi.love.entity.loader.IUserDetailLoader;
import me.himi.love.entity.loader.impl.UserDetailLoaderImpl;
import me.himi.love.ui.BuyLoveMoneyActivity;
import me.himi.love.ui.BuyVIPActivity;
import me.himi.love.ui.EditNewsActivity;
import me.himi.love.ui.FollowsNewsActivity;
import me.himi.love.ui.MainActivity;
import me.himi.love.ui.MyArticlesActivity;
import me.himi.love.ui.MyFansActivity;
import me.himi.love.ui.MyFollowsActivity;
import me.himi.love.ui.MyFriendsActivity;
import me.himi.love.ui.MyReceivedSayhiActivity;
import me.himi.love.ui.SettingsActivity;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.VisitorsAndVisitwhoActivity;
import me.himi.love.ui.WaitingForReviewArticlesActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.Share;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.EmojiTextView;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName:PersonalFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class PersonalFragment extends BaseFragment implements OnClickListener {

    RelativeLayout mContainerView;
    ImageView ivMyFace;
    EmojiTextView tvMyNickname;
    TextView tvMyId;

    TextView tvSignin; // 签到
    TextView tvLoveMoney; //

    TextView tvFriendsCount, tvFollowsCount, tvFansCount, tvMyGiftsCount, tvMyArticlesCount; //  好友,关注,粉丝,礼物,帖子

    TextView tvMyVip, tvVipExpireTime;

    TextView tvVisitorsTips, tvFansTips, tvSayhiTips, tvConversationTips, tvGiftsTips, tvArticlesTips; // 访客提醒,粉丝提醒,打招呼提醒,会话消息提醒,礼物提醒,新帖子(审核通过,新评论)提醒

    // 首次启动从服务器加载当前登录用户的一些基本资料
    IUserDetailLoader userLoader = new UserDetailLoaderImpl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.e(getClass().getSimpleName(), "->onCreateView");
	mContainerView = (RelativeLayout) inflater.inflate(R.layout.fragment_personal, container, false);
	init();
	return mContainerView;
    }

    //    private BroadcastReceiver rcNewMsgBroadcastReceiver; // im聊天消息
    private BroadcastReceiver privateMsgBroadcastReceiver; // 私信消息

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

	//	init();

    }

    private void init() {

	ivMyFace = (ImageView) mContainerView.findViewById(R.id.iv_my_face);
	tvMyId = ((TextView) mContainerView.findViewById(R.id.tv_my_id));
	tvMyNickname = ((EmojiTextView) mContainerView.findViewById(R.id.tv_my_nickname));

	//	mContainerView.findViewById(R.id.ll_my_news).setOnClickListener(this); // 个人动态

	mContainerView.findViewById(R.id.ll_follows_news).setOnClickListener(this); // 关注用户动态

	mContainerView.findViewById(R.id.layout_share).setOnClickListener(this); // 分享

	mContainerView.findViewById(R.id.layout_sayhi_messages).setOnClickListener(this); // 粉丝/打招呼等消息

	mContainerView.findViewById(R.id.layout_conversations).setOnClickListener(this); // 会话

	mContainerView.findViewById(R.id.ll_visitors).setOnClickListener(this); // 看过谁,谁看过

	mContainerView.findViewById(R.id.layout_start_settings).setOnClickListener(this); // 设置

	mContainerView.findViewById(R.id.layout_writen_news).setOnClickListener(this); // 发布心情
	mContainerView.findViewById(R.id.tv_start_publish_news).setOnClickListener(this); // 发布心情

	if (MyApplication.getInstance().getCurrentLoginedUser().getUserId() == 211111) {
	    mContainerView.findViewById(R.id.layout_review_articles).setVisibility(View.VISIBLE);
	    mContainerView.findViewById(R.id.layout_review_articles).setOnClickListener(this); // 审核帖子
	} else {
	    mContainerView.findViewById(R.id.layout_review_articles).setVisibility(View.GONE);
	}
	//	mContainerView.findViewById(R.id.layout_my_vistors).setOnClickListener(this); // 查看访客

	mContainerView.findViewById(R.id.layout_buy_lovemoney).setOnClickListener(this); // 兑换恋恋币

	mContainerView.findViewById(R.id.layout_vip_center).setOnClickListener(this); // 会员中心

	mContainerView.findViewById(R.id.layout_to_myfriends).setOnClickListener(this); // 查看我的好友
	mContainerView.findViewById(R.id.layout_to_myfollows).setOnClickListener(this); // 查看我的关注
	mContainerView.findViewById(R.id.layout_to_myfans).setOnClickListener(this); // 查看我的粉丝
	mContainerView.findViewById(R.id.layout_to_mygifts).setOnClickListener(this); // 查看我的礼物
	mContainerView.findViewById(R.id.layout_to_myarticles).setOnClickListener(this); // 查看我的帖子

	tvFriendsCount = (TextView) (mContainerView.findViewById(R.id.tv_my_friends_no)); // 好友
	tvFollowsCount = (TextView) (mContainerView.findViewById(R.id.tv_my_follows_no));//关注
	tvFansCount = (TextView) (mContainerView.findViewById(R.id.tv_my_fans_no)); //粉丝
	tvMyGiftsCount = (TextView) (mContainerView.findViewById(R.id.tv_my_gifts_no)); //礼物
	tvMyArticlesCount = (TextView) (mContainerView.findViewById(R.id.tv_my_articles_no)); //帖子

	LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
	tvFriendsCount.setText(loginedUser.getFriendsCount() + "");
	tvFollowsCount.setText(loginedUser.getFollowsCount() + "");
	tvFansCount.setText(loginedUser.getFansCount() + "");

	tvSignin = (TextView) mContainerView.findViewById(R.id.tv_signin); // 签到
	tvSignin.setOnClickListener(this);

	// 金币
	tvLoveMoney = (TextView) mContainerView.findViewById(R.id.tv_love_money);

	//	String url = "http://";
	//	ImageLoader.getInstance().displayImage(url, ivMyFace, ImageLoaderOptions.rounderOptions());

	// 查询记录, 有新访客就提示
	mContainerView.findViewById(R.id.tv_vistors_message_tips).setVisibility(View.VISIBLE);

	// 查看我的详细资料
	mContainerView.findViewById(R.id.layout_top).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), UserInfoTextActivity.class);
		intent.putExtra("user_id", MyApplication.getInstance().getCurrentLoginedUser().getUserId());
		intent.putExtra("is_vip", MyApplication.getInstance().getCurrentLoginedUser().getIsVip() == 1);
		intent.putExtra("user_nickname", MyApplication.getInstance().getCurrentLoginedUser().getNickname());
		intent.putExtra("user_face_url", MyApplication.getInstance().getCurrentLoginedUser().getFaceUrl());
		startActivity(intent);
	    }
	});

	tvMyVip = (TextView) mContainerView.findViewById(R.id.tv_my_vip);
	// 过期时间
	tvVipExpireTime = (TextView) mContainerView.findViewById(R.id.tv_vip_expire_time);

	// 会话消息提示
	tvConversationTips = (TextView) mContainerView.findViewById(R.id.tv_conversation_message_tips);
	// 访客消息提示
	tvVisitorsTips = (TextView) mContainerView.findViewById(R.id.tv_vistors_message_tips);
	// 粉丝消息提示
	tvFansTips = (TextView) mContainerView.findViewById(R.id.tv_newfans_message_tips);

	// 打招呼消息提示
	tvSayhiTips = (TextView) mContainerView.findViewById(R.id.tv_sayhi_message_tips);
	// 礼物消息 提示
	tvGiftsTips = (TextView) mContainerView.findViewById(R.id.tv_newgifts_message_tips);
	// 新帖子消息 提示
	tvArticlesTips = (TextView) mContainerView.findViewById(R.id.tv_newarticles_message_tips);

	//  默认提示圆点都是隐藏的
	tvVisitorsTips.setVisibility(View.GONE);
	tvFansTips.setVisibility(View.GONE);
	tvSayhiTips.setVisibility(View.GONE);
	tvGiftsTips.setVisibility(View.GONE);
	tvArticlesTips.setVisibility(View.GONE);
	//
	// 加载是否存在未读聊天消息
	if (RongIM.getInstance() != null) {
	    tvConversationTips.setVisibility(RongIM.getInstance().getTotalUnreadCount() != 0 ? View.VISIBLE : View.GONE);
	}
	// 加载是否存在未读消息

	// 首次创建时即加载我的资料
	loadMyInfo();

	// 收到新会话消息时显示提示
	// 注册新消息广播接收器
	registerBroadcastReceivers();

	// 
	//	RongIMEvent imEvent = RongIMEvent.getInstance(getActivity());
	//	if (imEvent != null) {
	//	    imEvent.setOtherListener();
	//	}
	// 连接IM
	connectRongCloudIM(MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");
	// 广告
	initAds();
    }

    private void registerBroadcastReceivers() {
	// TODO Auto-generated method stub
	//	IntentFilter filter = new IntentFilter();
	//	filter.addAction(MainActivity.ACTION_IM_RECEIVE_MESSAGE);
	//
	//	rcNewMsgBroadcastReceiver = new BroadcastReceiver() {
	//
	//	    @Override
	//	    public void onReceive(Context context, Intent intent) {
	//		tvConversationTips.setVisibility(View.VISIBLE);
	//		showToast(intent.getStringExtra("content"));
	//	    }
	//	};
	//
	//	// z
	//	getActivity().registerReceiver(rcNewMsgBroadcastReceiver, filter);

	//	filter = new IntentFilter();
	//	filter.addAction(MessagePollService.ACTION_PRIVATE_MESSAGE);
	//	privateMsgBroadcastReceiver = new BroadcastReceiver() {
	//	    
	//	    @Override
	//	    public void onReceive(Context context, Intent intent) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	};
    }

    private void initAds() {
	initDomobAd();
    }

    /**
     * 广告 初始化
     */
    private void initDomobAd() {
	cn.domob.android.ads.AdView adview = new cn.domob.android.ads.AdView(MyApplication.getInstance().getTopActivity(), "56OJzoHYuN5N9Wvxuc", "16TLmU5aApZM2NUOnDJjsmyk");
	// // 广告 container
	RelativeLayout adContainer = (RelativeLayout) mContainerView.findViewById(R.id.adContainer);
	adview.setGravity(Gravity.CENTER);
	//	adview.setKeyword("game");
	//	adview.setUserGender("male");
	//	adview.setUserBirthdayStr("2000-08-08");
	//	adview.setUserPostcode("123456");
	//	adview.setAdEventListener(null);

	adContainer.addView(adview);
    }

    private void loadMyInfo() {

	int userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();

	AppServiceImpl.getInstance().loadUserInfo(userId, new OnLoadDetailUserListener() {

	    @Override
	    public void onSuccess(DetailInfoUser user) {
		// TODO Auto-generated method stub
		if (user == null) {
		    ToastFactory.getToast(MyApplication.getInstance().getApplicationContext(), "出错了").show();
		    return;
		}

		tvMyId.setText("ID: " + user.getUserId());
		tvMyNickname.setEmojiText(user.getNickname());

		// 更新当前内存中的 已登录的 user的资料
		LoginedUser loginedUser = MyApplication.getInstance().getCurrentLoginedUser();
		loginedUser.setFaceUrl(user.getFaceUrl());
		loginedUser.setNickname(user.getNickname());
		tvLoveMoney.setText(loginedUser.getLoveMoney() + "币");
		// 保存详细资料
		loginedUser.setDetailInfoUser(user);

		MyApplication.getInstance().setCurrentLoginedUser(loginedUser);
		String url = user.getFaceUrl();
		refreshMyFace(url);

		// 获取首页数据
		updateHomeInfo();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(getActivity(), "获取资料失败,连接超时");

	    }
	});
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	//	case R.id.ll_my_news: // 查看我的留言
	//	    Intent intent2 = new Intent(getActivity(), UserNewsActivity.class);
	//	    Bundle extras = new Bundle();
	//	    DetailInfoUser user = new DetailInfoUser();
	//	    user.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	//	    user.setNickname(MyApplication.getInstance().getCurrentLoginedUser().getNickname());
	//	    extras.putSerializable("user", user);
	//	    intent2.putExtras(extras);
	//	    startActivity(intent2);
	//	    break;
	case R.id.ll_follows_news: // 关注用户的留言
	    Intent intent1 = new Intent(getActivity(), FollowsNewsActivity.class);
	    startActivity(intent1);
	    break;
	case R.id.layout_share: // 分享
	    Share.share(getActivity(), "我正在使用" + getResources().getString(R.string.app_name) + "征婚交友APP,帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\""); // 测试分享
	    break;
	case R.id.layout_conversations: // 查看会话
	    if (RongIM.getInstance() != null) {
		RongIM.getInstance().startConversationList(getActivity());
		tvConversationTips.setVisibility(View.GONE);//点击后隐藏提示
	    } else {
		// IM连接已销毁, 需重新登录
		//		重新连接
		showToast("IM连接已断开, 正在重新连接");
		connectRongCloudIM(MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");
	    }
	    break;
	case R.id.layout_sayhi_messages: // 查看打招呼消息
	    getActivity().startActivity(new Intent(getActivity(), MyReceivedSayhiActivity.class));
	    // 已读消息提醒,隐藏提示
	    getActivity().findViewById(R.id.tv_sayhi_message_tips).setVisibility(View.GONE);
	    break;
	case R.id.ll_visitors: // 谁看过,看过谁
	    Intent intent3 = new Intent(getActivity(), VisitorsAndVisitwhoActivity.class);
	    intent3.putExtra("user_id", MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	    startActivity(intent3);
	    // 隐藏提示
	    mContainerView.findViewById(R.id.tv_vistors_message_tips).setVisibility(View.GONE);
	    break;
	case R.id.layout_start_settings: // 设置
	    startActivity(new Intent(this.getActivity(), SettingsActivity.class));
	    break;
	case R.id.layout_writen_news: // 开始发布留言
	case R.id.tv_start_publish_news:
	    startActivity(new Intent(getActivity(), EditNewsActivity.class));
	    break;
	//	case R.id.layout_my_vistors: // 我的访客
	//	    Intent intent = new Intent(getActivity(), UserVisitorsActivity.class);
	//	    intent.putExtra("target_user_id", MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	//	    intent.putExtra("user_nickname", MyApplication.getInstance().getCurrentLoginedUser().getNickname());
	//	    startActivity(intent);
	//	    break;
	case R.id.layout_buy_lovemoney:
	    Intent intent5 = new Intent(getActivity(), BuyLoveMoneyActivity.class);
	    startActivity(intent5);
	    break;
	case R.id.layout_vip_center: // 会员中心
	    Intent intent4 = new Intent(getActivity(), BuyVIPActivity.class);
	    startActivity(intent4);
	    break;
	case R.id.tv_signin://签到
	    signin();
	    break;
	case R.id.layout_to_myfriends:// 我的好友
	    Intent intent6 = new Intent(getActivity(), MyFriendsActivity.class);
	    startActivity(intent6);
	    break;
	case R.id.layout_to_myfollows:// 我的关注
	    Intent intent7 = new Intent(getActivity(), MyFollowsActivity.class);
	    startActivity(intent7);
	    break;
	case R.id.layout_to_myfans:// 我的粉丝
	    Intent intent8 = new Intent(getActivity(), MyFansActivity.class);

	    // 隐藏提示
	    mContainerView.findViewById(R.id.tv_newfans_message_tips).setVisibility(View.GONE);
	    // 制造异常
	    //	    System.out.println(1/0);
	    startActivity(intent8);
	    break;
	case R.id.layout_to_mygifts: // 我的礼物
	    mContainerView.findViewById(R.id.tv_newgifts_message_tips).setVisibility(View.GONE);
	    break;
	case R.id.layout_to_myarticles: // 我的帖子
	    mContainerView.findViewById(R.id.tv_newarticles_message_tips).setVisibility(View.GONE);
	    startActivity(new Intent(getActivity(), MyArticlesActivity.class));
	    break;

	case R.id.layout_review_articles:// 审核帖子
	    startActivity(new Intent(getActivity(), WaitingForReviewArticlesActivity.class));
	    break;
	}
    }

    /**
     * 签到
     */
    ProgressDialog progress;

    private void signin() {

	if (progress == null) {
	    progress = new ProgressDialog(getActivity());
	}
	progress.setMessage("签到...");
	progress.setCanceledOnTouchOutside(false);
	progress.show();

	SigninPostParams postParams = new SigninPostParams();

	AppServiceExtendImpl.getInstance().sigin(postParams, new OnSigninResponseListener() {

	    @Override
	    public void onSuccess(int reward, int money) {
		// TODO Auto-generated method stub
		// 签到成功
		showToast("签到成功!\n奖励" + reward + "币");
		tvLoveMoney.setText(money + "币");

		tvSignin.setEnabled(false);
		tvSignin.setText("已签到");
		progress.dismiss();
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
    public void onHiddenChanged(boolean hidden) {
	if (!hidden) {
	    updateHomeInfo();
	}
	super.onHiddenChanged(hidden);
    }

    /**
     * 更新 用户 资料
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
	// TODO Auto-generated method stub
	if (isVisibleToUser) {
	    updateHomeInfo();
	}
	super.setUserVisibleHint(isVisibleToUser);
    }

    //    setUser

    //    @Override
    //    public void onResume() { // activity 切换时被调用
    //	// 刷新显示头像
    //	// TODO Auto-generated method stub
    //	// 更新资料
    //	updateHomeInfo();
    //	
    //	
    //	LoginedUser user = MyApplication.getInstance().getCurrentLoginedUser();
    //	tvMyId.setText("ID: " + user.getUserId());
    //	tvMyNickname.setEmojiText(user.getNickname());
    //	tvLoveMoney.setText(user.getLoveMoney() + "币");
    //	String url = user.getFaceUrl();// "http://t11.baidu.com/it/u=751031812,971358817&fm=56" : "http://img1.imgtn.bdimg.com/it/u=1983880049,3609856836&fm=116&gp=0.jpg";
    //	refreshMyFace(url);
    //	super.onResume();
    //    }

    private void refreshMyFace(String faceUrl) {

	ImageLoader.getInstance().displayImage(faceUrl, ivMyFace, ImageLoaderOptions.rounderOptions(), new ImageLoadingListener() {

	    @Override
	    public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		// TODO Auto-generated method stub
		// 不能及时刷新显示的问题
		ivMyFace.setImageBitmap(arg2); // 重新设置图像
		ivMyFace.invalidate();
	    }

	    @Override
	    public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }
	});
    }

    /**
     * 重新获取首页信息
     */
    private void updateHomeInfo() {
	//显示状态改变时 尝试更新为当前内存中保存的资料
	final LoginedUser user = MyApplication.getInstance().getCurrentLoginedUser();

	LoadHomeInfoPostParams postParams = new LoadHomeInfoPostParams();

	AppServiceExtendImpl.getInstance().loadHomeInfo(postParams, new OnLoadHomeInfoResponseListener() {

	    @Override
	    public void onSuccess(HomeInfo homeInfo) {
		// TODO Auto-generated method stub

		tvFansCount.setText(homeInfo.fans + "");
		tvFriendsCount.setText(homeInfo.friends + "");
		tvFollowsCount.setText(homeInfo.follows + "");

		tvMyGiftsCount.setText(homeInfo.gifts + "");

		tvMyArticlesCount.setText(homeInfo.articles + "");

		// 更新 loginedUser中的数据
		user.setFansCount(homeInfo.fans);
		user.setFollowsCount(homeInfo.follows);
		user.setFriendsCount(homeInfo.friends);

		user.setLoveMoney(homeInfo.loveMoney);
		user.setNickname(homeInfo.nickname);

		user.setIsVip(homeInfo.isVip ? 1 : 0);

		user.setVipExpireTime(homeInfo.vipExpireTime);

		//更新view

		tvMyId.setText("ID:" + user.getUserId());
		tvMyNickname.setEmojiText(user.getNickname());
		tvLoveMoney.setText(user.getLoveMoney() + "币");
		if (homeInfo.signed) {
		    tvSignin.setText("已签到");
		    tvSignin.setEnabled(false);
		} else {
		    tvSignin.setText("签到");
		    tvSignin.setEnabled(true);
		}

		if (user.getIsVip() == 1) {
		    String dateStr = ActivityUtil.getDateStr(user.getVipExpireTime(), "yyyy/MM/dd HH:mm:ss");
		    tvVipExpireTime.setText("VIP会员有效期至:" + dateStr);
		    tvMyVip.setBackgroundResource(R.drawable.vip_sign);
		} else {
		    tvMyVip.setBackgroundResource(R.drawable.vip_sign_not);
		}
		String url = user.getFaceUrl();// "http://t11.baidu.com/it/u=751031812,971358817&fm=56" : "http://img1.imgtn.bdimg.com/it/u=1983880049,3609856836&fm=116&gp=0.jpg";
		refreshMyFace(url);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub

	    }
	});
    }

    //    private void connectRongCloudIM(final String userId) {
    //	final ProgressDialog progress = new ProgressDialog(getActivity());
    //	progress.setMessage("登录IM...");
    //	progress.show();
    //
    //	AppServiceRongCloudIMImpl.getInstance().getTokenFromServer(userId, new OnTokenResponseListener() {
    //
    //	    @Override
    //	    public void onSuccess(String token) {
    //		// TODO Auto-generated method stub
    //		AppServiceRongCloudIMImpl.getInstance().connect(getActivity(), token, new ConnectCallback() {
    //
    //		    @Override
    //		    public void onError(ErrorCode arg0) {
    //			//			progress.dismiss();
    //			switch (arg0) {
    //			case TOKEN_INCORRECT:
    //			    connectRongCloudIM(userId);
    //			    break;
    //			case TIMEOUT:
    //			    connectRongCloudIM(userId);
    //			    break;
    //			case APP_KEY_UNAVAILABLE:
    //			    break;
    //			case PACKAGE_BROKEN:
    //			    break;
    //			case DATABASE_ERROR:
    //			    break;
    //			case SERVER_UNAVAILABLE:
    //			    break;
    //			case UNKNOWN:
    //			    break;
    //			}
    //		    }
    //
    //		    @Override
    //		    public void onSuccess(String arg0) {
    //			// 刷新用户资料
    //			AppServiceRongCloudIMImpl.getInstance().refreshUserInfo();
    //			progress.dismiss();
    //			//进入主页, 清除所有Activity,避免回退
    //		    }
    //		});
    //	    }
    //
    //	    @Override
    //	    public void onFailure(String errorMsg) {
    //		// TODO Auto-generated method stub
    //		ToastFactory.getToast(getActivity(), "获取token失败,连接超时,请重试").show();
    //		progress.dismiss();
    //	    }
    //	});
    //    }

    private int tryReconnectCount = 3; // 最大失败尝试次数

    View mLoadingView;

    /**
     * 连接到融云IM
     * @param userId
     */
    private void connectRongCloudIM(final String userId) {
	//	final ProgressDialog progress = new ProgressDialog(MainActivity.this);
	//	progress.setMessage("连接IM...");
	//	progress.setTitle("");
	//	progress.show();
	if (mLoadingView == null) {
	    mLoadingView = getActivity().getLayoutInflater().inflate(R.layout.layout_loading_retry, null);
	    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_IN_PARENT);
	    mContainerView.addView(mLoadingView, params);

	    // 重试
	    mLoadingView.findViewById(R.id.tv_load_retry).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    tryReconnectCount = 3;
		    connectRongCloudIM(userId);

		}
	    });
	}

	// 可见
	mLoadingView.setVisibility(View.VISIBLE);
	// 重试按钮隐藏
	mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.GONE);

	AppServiceRongCloudIMImpl.getInstance().getTokenFromServer(userId, new OnTokenResponseListener() {

	    @Override
	    public void onSuccess(String token) {
		// TODO Auto-generated method stub
		AppServiceRongCloudIMImpl.getInstance().connect(getActivity(), token, new ConnectCallback() {

		    @Override
		    public void onError(ErrorCode arg0) {
			// TODO Auto-generated method stub

			switch (arg0) {
			case TOKEN_INCORRECT:
			    if (tryReconnectCount-- >= 1) {
				connectRongCloudIM(userId);
				ToastFactory.getToast(getActivity(), "token 不正确,重试中(" + tryReconnectCount + ")").show();
			    } else {
				ToastFactory.getToast(getActivity(), "token 错误").show();
				// 登录失败则进入 登录页面
				// 隐藏
				mLoadingView.setVisibility(View.GONE);
			    }
			    break;
			case TIMEOUT:
			    if (tryReconnectCount-- >= 0) {
				connectRongCloudIM(userId);
				ToastFactory.getToast(getActivity(), "登录IM超时,重试中(" + tryReconnectCount + ")").show();
			    } else {
				// 登录失败则进入 登录页面
				// 隐藏
				mLoadingView.setVisibility(View.GONE);
			    }
			    break;
			case APP_KEY_UNAVAILABLE:
			    showToast("IM appKey不可用");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case PACKAGE_BROKEN:
			    showToast("IM package Broken");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case DATABASE_ERROR:
			    showToast("数据库出错");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case SERVER_UNAVAILABLE:
			    showToast("IM服务器不可用!");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			case UNKNOWN:
			    showToast("IM 未知错误");
			    // 重试按钮可用
			    mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
			    break;
			}
		    }

		    @Override
		    public void onSuccess(String arg0) {
			// TODO Auto-generated method stub
			// 刷新用户资料
			// 隐藏
			mLoadingView.setVisibility(View.GONE);
			AppServiceRongCloudIMImpl.getInstance().refreshUserInfo();

			// 注册新消息监听器
			RongIMEvent.getInstance(getActivity()).setOtherListener();

		    }
		});
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(getActivity(), "获取token失败,请重试").show();
		// 重试按钮可见
		mLoadingView.findViewById(R.id.tv_load_retry).setVisibility(View.VISIBLE);
	    }
	});
    }

    @Override
    public void onDestroy() {
	// TODO Auto-generated method stub
	unregisterBroadcastReceivers();
	super.onDestroy();
    }

    private void unregisterBroadcastReceivers() {
	// TODO Auto-generated method stub

	//	getActivity().unregisterReceiver(rcNewMsgBroadcastReceiver);
    }
}
