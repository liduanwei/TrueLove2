package me.himi.love.ui.fragment;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadRecentFansPostParams;
import me.himi.love.IAppServiceExtend.LoadRecentFollowsPostParams;
import me.himi.love.IAppServiceExtend.LoadUserNewsRecentPostParams;
import me.himi.love.IAppServiceExtend.LoadVisitorsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadRecentFansResponseListener;
import me.himi.love.IAppServiceExtend.OnLoadRecentFollowsResponseListener;
import me.himi.love.IAppServiceExtend.OnLoadUserNewsResponseListener;
import me.himi.love.IAppServiceExtend.OnLoadVisitorsResponseListener;
import me.himi.love.IAppServiceExtend.UserIdAndFace;
import me.himi.love.IAppServiceExtend.UserIdAndFaceInfos;
import me.himi.love.IAppServiceExtend.UserNewsInfos;
import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.ui.UserFansActivity;
import me.himi.love.ui.UserFollowsActivity;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.UserNewsActivity;
import me.himi.love.ui.UserVisitorsActivity;
import me.himi.love.ui.VisitorsAndVisitwhoActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.ImageLoaderOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class UserInfoPage2Fragment extends BaseFragment implements OnClickListener {

    View mContainerView;

    TextView tvLastLoginTime, tvRegisterTime, tvLastAddress;

    LinearLayout llTopUserNews;// 用户最近的留言布局容器

    TextView tvVisitorCount; //访客数
    TextView tvFansCount; //粉丝数
    TextView tvFollowsCount; //粉丝数

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	mContainerView = inflater.inflate(R.layout.userinfo_page2, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    private void init() {
	// 最近留言
	llTopUserNews = (LinearLayout) mContainerView.findViewById(R.id.ll_user_news);
	llTopUserNews.setOnClickListener(this);

	// 访客数
	tvVisitorCount = (TextView) mContainerView.findViewById(R.id.tv_visitors_count);
	// 粉丝数
	tvFansCount = (TextView) mContainerView.findViewById(R.id.tv_fans_count);
	// 关注数
	tvFollowsCount = (TextView) mContainerView.findViewById(R.id.tv_follows_count);

	// 最后登录
	tvLastLoginTime = (TextView) mContainerView.findViewById(R.id.tv_user_lastlogin_time);
	// 注册时间
	tvRegisterTime = (TextView) mContainerView.findViewById(R.id.tv_user_register_time);
	// 最后位置
	tvLastAddress = (TextView) mContainerView.findViewById(R.id.tv_user_lastaddr);

    }

    private DetailInfoUser mTargetUser;

    public void setInfos(final DetailInfoUser user) {
	mTargetUser = user;

	// 最后登录时间
	tvLastLoginTime.setText(user.getLastLogintime());
	// 注册时间
	tvRegisterTime.setText(user.getRegisterTime());
	// 最后位置
	tvLastAddress.setText(user.getLastAddress());

	// 加载最近留言
	LoadUserNewsRecentPostParams postParams = new LoadUserNewsRecentPostParams();
	postParams.userId = user.getUserId();

	AppServiceExtendImpl.getInstance().loadUserNewsCount(postParams, new OnLoadUserNewsResponseListener() {

	    @Override
	    public void onSuccess(UserNewsInfos infos) {
		// TODO Auto-generated method stub
		if (infos != null) {
		    TextView tvCount = (TextView) mContainerView.findViewById(R.id.tv_news_count);
		    tvCount.setText(infos.count + "");

		    ImageView iv = (ImageView) mContainerView.findViewById(R.id.iv_news_image);
		    if (infos.imageUrls.size() != 0) {
			iv.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(infos.imageUrls.get(0).getSmallImageUrl(), iv);
		    } else {
			iv.setVisibility(View.GONE);
		    }

		    TextView tvContent = (TextView) mContainerView.findViewById(R.id.tv_news_content);
		    tvContent.setText(infos.content);
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);

	    }
	});

	// 查看所有访客
	mContainerView.findViewById(R.id.tv_visitor_all).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		//		Intent intent = new Intent(getActivity(), UserVisitorsActivity.class);
		//		intent.putExtra("target_user_id", user.getUserId());
		//		intent.putExtra("user_nickname", user.getNickname());
		Intent intent = new Intent(getActivity(), VisitorsAndVisitwhoActivity.class);
		intent.putExtra("user_id", user.getUserId());
		intent.putExtra(VisitorsAndVisitwhoActivity.USER_GENDER, user.getGender());
		startActivity(intent);

	    }
	});

	// 加载最近访客
	LoadVisitorsPostParams postParams2 = new LoadVisitorsPostParams();
	postParams2.userId = user.getUserId();
	AppServiceExtendImpl.getInstance().loadRecentVisitors(postParams2, new OnLoadVisitorsResponseListener() {

	    @Override
	    public void onSuccess(UserIdAndFaceInfos infos) {
		// TODO Auto-generated method stub

		if (infos != null) {
		    if (infos.count != 0) {
			tvVisitorCount.setText(infos.count + "");
			//showToast("最近7天访客数量:" + infos.count);
			ImageView iv01 = (ImageView) mContainerView.findViewById(R.id.iv_visitor01_face);
			ImageView iv02 = (ImageView) mContainerView.findViewById(R.id.iv_visitor02_face);
			ImageView iv03 = (ImageView) mContainerView.findViewById(R.id.iv_visitor03_face);
			ImageView iv04 = (ImageView) mContainerView.findViewById(R.id.iv_visitor04_face);
			ImageView iv05 = (ImageView) mContainerView.findViewById(R.id.iv_visitor05_face);

			ImageView[] ivs = { iv01, iv02, iv03, iv04, iv05 };

			int size = infos.userFaces.size() <= ivs.length ? infos.userFaces.size() : ivs.length;
			for (int i = 0; i < size; ++i) {
			    ivs[i].setVisibility(View.VISIBLE);
			    final UserIdAndFace visitorFace = infos.userFaces.get(i);
			    ImageLoader.getInstance().displayImage(visitorFace.face.getSmallImageUrl(), ivs[i], ImageLoaderOptions.circleOptions());

			    // 点击头像查看资料
			    ivs[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				    // TODO Auto-generated method stub
				    Intent intent = new Intent(getActivity(), UserInfoTextActivity.class);
				    intent.putExtra("user_id", visitorFace.userId);
				    intent.putExtra("user_nickname", visitorFace.nickname);
				    intent.putExtra("user_face_url", visitorFace.face.getSmallImageUrl());
				    getActivity().startActivity(intent);
				    // 结束掉,防止回退栈太深
				    getActivity().finish();
				}
			    });
			}
		    } else {
			//showToast("还没有访客");
		    }
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
	    }
	});

	// 查看所有粉丝
	mContainerView.findViewById(R.id.tv_fans_all).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), UserFansActivity.class);
		intent.putExtra("target_user_id", user.getUserId());
		intent.putExtra("user_nickname", user.getNickname());
		startActivity(intent);
	    }
	});

	//  加载最新的几位粉丝
	LoadRecentFansPostParams postParams3 = new LoadRecentFansPostParams();
	postParams3.userId = user.getUserId();
	AppServiceExtendImpl.getInstance().loadRecentFans(postParams3, new OnLoadRecentFansResponseListener() {

	    @Override
	    public void onSuccess(UserIdAndFaceInfos infos) {
		// TODO Auto-generated method stub

		if (infos != null) {
		    if (infos.count != 0) {
			tvFansCount.setText(infos.count + "");
			ImageView iv01 = (ImageView) mContainerView.findViewById(R.id.iv_fans01_face);
			ImageView iv02 = (ImageView) mContainerView.findViewById(R.id.iv_fans02_face);
			ImageView iv03 = (ImageView) mContainerView.findViewById(R.id.iv_fans03_face);
			ImageView iv04 = (ImageView) mContainerView.findViewById(R.id.iv_fans04_face);
			ImageView iv05 = (ImageView) mContainerView.findViewById(R.id.iv_fans05_face);

			ImageView[] ivs = { iv01, iv02, iv03, iv04, iv05 };

			int size = infos.userFaces.size() <= ivs.length ? infos.userFaces.size() : ivs.length;
			for (int i = 0; i < size; ++i) {
			    ivs[i].setVisibility(View.VISIBLE);
			    final UserIdAndFace visitorFace = infos.userFaces.get(i);
			    ImageLoader.getInstance().displayImage(visitorFace.face.getSmallImageUrl(), ivs[i], ImageLoaderOptions.circleOptions());

			    // 点击头像查看资料
			    ivs[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				    // TODO Auto-generated method stub
				    Intent intent = new Intent(getActivity(), UserInfoTextActivity.class);
				    intent.putExtra("user_id", visitorFace.userId);
				    intent.putExtra("user_nickname", visitorFace.nickname);
				    intent.putExtra("user_face_url", visitorFace.face.getSmallImageUrl());
				    getActivity().startActivity(intent);
				    // 结束掉,防止回退栈太深
				    getActivity().finish();
				}
			    });
			}
		    } else {
			//showToast("还没有访客");
		    }
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
	    }
	});

	// 查看所有关注用户
	mContainerView.findViewById(R.id.tv_follows_all).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), UserFollowsActivity.class);
		intent.putExtra("target_user_id", user.getUserId());
		intent.putExtra("user_nickname", user.getNickname());
		startActivity(intent);
	    }
	});

	//  加载最新的几位关注用户
	LoadRecentFollowsPostParams postParams4 = new LoadRecentFollowsPostParams();
	postParams4.userId = user.getUserId();
	AppServiceExtendImpl.getInstance().loadRecentFollows(postParams4, new OnLoadRecentFollowsResponseListener() {

	    @Override
	    public void onSuccess(UserIdAndFaceInfos infos) {
		// TODO Auto-generated method stub

		if (infos != null) {
		    if (infos.count != 0) {
			tvFollowsCount.setText(infos.count + "");
			ImageView iv01 = (ImageView) mContainerView.findViewById(R.id.iv_follow01_face);
			ImageView iv02 = (ImageView) mContainerView.findViewById(R.id.iv_follow02_face);
			ImageView iv03 = (ImageView) mContainerView.findViewById(R.id.iv_follow03_face);
			ImageView iv04 = (ImageView) mContainerView.findViewById(R.id.iv_follow04_face);
			ImageView iv05 = (ImageView) mContainerView.findViewById(R.id.iv_follow05_face);

			ImageView[] ivs = { iv01, iv02, iv03, iv04, iv05 };

			int size = infos.userFaces.size() <= ivs.length ? infos.userFaces.size() : ivs.length;
			for (int i = 0; i < size; ++i) {
			    ivs[i].setVisibility(View.VISIBLE);
			    final UserIdAndFace visitorFace = infos.userFaces.get(i);
			    ImageLoader.getInstance().displayImage(visitorFace.face.getSmallImageUrl(), ivs[i], ImageLoaderOptions.circleOptions());

			    // 点击头像查看资料
			    ivs[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				    // TODO Auto-generated method stub
				    Intent intent = new Intent(getActivity(), UserInfoTextActivity.class);
				    intent.putExtra("user_id", visitorFace.userId);
				    intent.putExtra("user_nickname", visitorFace.nickname);
				    intent.putExtra("user_face_url", visitorFace.face.getSmallImageUrl());
				    getActivity().startActivity(intent);
				    // 结束掉,防止回退栈太深
				    getActivity().finish();
				}
			    });
			}
		    } else {
			//showToast("还没有访客");
		    }
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
	    }
	});

    }

    public int getViewHeight() {
	return this.mContainerView.getHeight();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

	super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {
	if (v == llTopUserNews) { // 用户最近留言
	    if (mTargetUser != null) {
		Intent intent = new Intent(getActivity(), UserNewsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", mTargetUser);
		intent.putExtras(bundle);
		startActivity(intent);
	    }
	}
    }

    @Override
    public void onDestroy() {

	HttpUtil.getClient().cancelAllRequests(true);
	super.onDestroy();
    }
}
