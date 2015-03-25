package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnNewsCommentListener;
import me.himi.love.IAppService.OnPublishNewsCommentListener;
import me.himi.love.IAppService.PublishNewsCommentParams;
import me.himi.love.IAppService.UserNewsCommentParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.UserNewsCommentAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.LoadUserNewsComments;
import me.himi.love.entity.UserNews;
import me.himi.love.entity.UserNewsComment;
import me.himi.love.im.adapter.EmoViewPagerAdapter;
import me.himi.love.im.adapter.EmoteAdapter;
import me.himi.love.im.entity.FaceText;
import me.himi.love.im.ui.customview.EmoticonsEditText;
import me.himi.love.im.util.FaceTextUtils;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserNewsCommentActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserNewsCommentActivity extends BaseActivity implements OnItemClickListener {

    UserNews targetNews;

    me.himi.love.view.list.XListView mNewsCommentListView;

    UserNewsCommentAdapter mUserNewsCommentAdapter;
    EmoticonsEditText etCommentContent;
    TextView tvPublishComment;

    View layoutReply; //
    TextView tvReply, tvReplyCancle;
    UserNewsComment mCurrentReplyComment; // 当前想要回复的评论

    TextView tvHeaderCommentCount, tvNewsPosttime; // 头部显示的评论数量,用户留言发布时间
    ProgressBar pbLoading; // 头部底部的加载中
    TextView tvLoading; // 同上

    TextView tvShowEmo; // 插入 表情按钮

    private boolean isMine; // 是当前登录用户的留言

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_user_news_comments);

	init();

    }

    private void init() {

	Object o = getIntent().getSerializableExtra("news");
	if (o != null) {
	    targetNews = (UserNews) o;
	} else {
	    ActivityUtil.show(this, "没有指定留言");
	    finish();
	}

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	tvTopTitle.setText("留言评论");
	if (targetNews.getUserId() == MyApplication.getInstance().getCurrentLoginedUser().getUserId()) {
	    tvTopAction.setVisibility(View.VISIBLE);

	    tvTopAction.setText("操作");
	} else {
	    tvTopAction.setVisibility(View.GONE);
	    tvTopAction.setText("");
	}

	// 输入框上面的回复谁布局容器
	layoutReply = findViewById(R.id.layout_news_comment_reply);
	layoutReply.setVisibility(View.GONE);
	// 回复 谁:
	tvReply = (TextView) findViewById(R.id.tv_news_comment_reply);
	// 取消回复 按钮
	tvReplyCancle = (TextView) findViewById(R.id.tv_news_comment_reply_cancle);
	tvReplyCancle.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		mCurrentReplyComment = null;
		layoutReply.setVisibility(View.GONE);
		tvReply.setText("");
	    }

	});

	tvShowEmo = (TextView) findViewById(R.id.tv_news_comment_emo);
	tvShowEmo.setOnClickListener(new View.OnClickListener() {

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
		    ActivityUtil.closeSoftInput(UserNewsCommentActivity.this, etCommentContent);
		}
	    }

	});

	etCommentContent = (EmoticonsEditText) findViewById(R.id.et_news_comment_content);
	etCommentContent.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		View view = findViewById(R.id.layout_emo);
		view.setVisibility(View.GONE);
		ActivityUtil.showSoftInputView(UserNewsCommentActivity.this, etCommentContent);
	    }
	});

	// 发布评论
	tvPublishComment = (TextView) findViewById(R.id.tv_news_comment_publish);

	tvPublishComment.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		publishComment();
	    }

	});

	mNewsCommentListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview_news_comments);
	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	//	mNewsCommentListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	//顶部留言
	View v = createUserNewsView();
	mNewsCommentListView.addHeaderView(v);

	// 头部的用户留言布局容器
	tvHeaderCommentCount = (TextView) v.findViewById(R.id.tv_news_comments);
	tvNewsPosttime = (TextView) v.findViewById(R.id.tv_news_post_time);

	RelativeLayout layoutFooterLoading = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_news_comment_loading, null);

	pbLoading = (ProgressBar) layoutFooterLoading.findViewById(R.id.pb_user_news_comment_loading);
	tvLoading = (TextView) layoutFooterLoading.findViewById(R.id.tv_user_news_comment_loading);

	// 底部提示加载中
	mNewsCommentListView.addFooterView(layoutFooterLoading);

	// 设置无数据时显示的view
	//	mNewsCommentListView.setEmptyView(v);

	mUserNewsCommentAdapter = new UserNewsCommentAdapter(this, new ArrayList<UserNewsComment>());

	mNewsCommentListView.setAdapter(mUserNewsCommentAdapter);

	mNewsCommentListView.setPullLoadEnable(true);
	mNewsCommentListView.setPullRefreshEnable(true);
	mNewsCommentListView.pullRefreshing();

	mNewsCommentListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadUserNewsComment();
		tvLoading.setVisibility(View.GONE);
		pbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadUserNewsComment();
	    }
	});

	loadUserNewsComment();

	// 
	mNewsCommentListView.setOnItemClickListener(this);

	// 默认先隐藏软键盘
	ActivityUtil.hideSoftInput(this);

	// 表情view
	initEmoView();
    }

    private View createUserNewsView() {

	View convertView = LayoutInflater.from(this).inflate(R.layout.user_news_comment_top, null);
	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);
	final TextView tvNickname = ViewHolder.get(convertView, R.id.tv_news_user_nickname);
	final TextView tvAge = ViewHolder.get(convertView, R.id.tv_news_user_age);

	final View mainContentView = ViewHolder.get(convertView, R.id.layout_news_main_content);

	final TextView tvContent = ViewHolder.get(convertView, R.id.tv_news_content);
	final TextView tvPosttime = ViewHolder.get(convertView, R.id.tv_news_post_time);
	final TextView tvComments = ViewHolder.get(convertView, R.id.tv_news_comments);
	final TextView tvPostLocation = ViewHolder.get(convertView, R.id.tv_news_post_location);

	// 测试图片内容
	final ImageView ivImageRow1Content01 = ViewHolder.get(convertView, R.id.iv_news_row1_image1_content);
	final ImageView ivImageRow1Content02 = ViewHolder.get(convertView, R.id.iv_news_row1_image2_content);
	final ImageView ivImageRow1Content03 = ViewHolder.get(convertView, R.id.iv_news_row1_image3_content);

	final ImageView ivImageRow2Content01 = ViewHolder.get(convertView, R.id.iv_news_row2_image1_content);
	final ImageView ivImageRow2Content02 = ViewHolder.get(convertView, R.id.iv_news_row2_image2_content);
	final ImageView ivImageRow2Content03 = ViewHolder.get(convertView, R.id.iv_news_row2_image3_content);

	int backRes = 0;
	Drawable genderDrawable = null;
	if (targetNews.getGender() == 1) {
	    backRes = R.drawable.shape_gender_age_male;
	    genderDrawable = getResources().getDrawable(R.drawable.ic_user_male2);
	} else {
	    backRes = R.drawable.shape_gender_age_female;
	    genderDrawable = getResources().getDrawable(R.drawable.ic_user_famale2);
	}
	tvAge.setText(targetNews.getAge() + "");
	tvAge.setBackgroundResource(backRes);
	genderDrawable.setBounds(0, 0, genderDrawable.getMinimumWidth(), genderDrawable.getMinimumHeight());
	tvAge.setCompoundDrawables(genderDrawable, null, null, null);

	tvNickname.setText(targetNews.getNickname());
	tvContent.setText(targetNews.getContent());
	tvPosttime.setText(targetNews.getPostTime());
	tvComments.setText(targetNews.getCommentsCount() + "");

	// 需要计算经纬度对应的位置与当前用户的位置的距离
	tvPostLocation.setText(/*news.getPostLongitude() + "," + news.getPostLatitude() + "," +*/targetNews.getAddress());

	// display 会有刷新闪烁问题

	int size = targetNews.getImageUrls().size();

	ImageView[] ivs = { ivImageRow1Content01, ivImageRow1Content02, ivImageRow1Content03, ivImageRow2Content01, ivImageRow2Content02, ivImageRow2Content03 };

	int t = Math.min(ivs.length, size);//size >= 3 ? 3 : size;

	if (size != 0) {
	    int i = 0;
	    for (; i < t; ++i) {
		// 图片错位问题, 每次都需要设置 可视与不可视
		ivs[i].setVisibility(View.VISIBLE);
		// 默认使用小图
		final BSImageUrl imgUrl = targetNews.getImageUrls().get(i);
		ImageLoader.getInstance().displayImage(imgUrl.getSmallImageUrl(), ivs[i], ImageLoaderOptions.normalOptions());
		ivs[i].setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
			// TODO Auto-generated method stub
			//  链接查看大图
			Intent intent = new Intent(UserNewsCommentActivity.this, ZoombleImageActivity.class);
			intent.putExtra("image_url", imgUrl.getBigImageUrl());
			startActivity(intent);

		    }
		});
	    }
	    for (; i < ivs.length; ++i) {
		ivs[i].setVisibility(View.GONE);
	    }
	} else {
	    for (int i = 0; i < ivs.length; ++i) {
		ivs[i].setVisibility(View.GONE);
	    }
	}
	ImageLoader.getInstance().displayImage(targetNews.getFaceUrl(), ivFace, ImageLoaderOptions.rounderOptions());

	ivFace.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(UserNewsCommentActivity.this, UserInfoTextActivity.class);
		System.out.println("newsUserID" + targetNews.getUserId());
		intent.putExtra("user_id", targetNews.getUserId());
		intent.putExtra("user_nickname", targetNews.getNickname());
		intent.putExtra("user_face_url", targetNews.getFaceUrl());
		startActivity(intent);
	    }
	});
	return convertView;
    }

    int pageNumber = 1;

    /**
     * 还需要重新加载 头部的 评论数,,,
     */
    private void loadUserNewsComment() {

	UserNewsCommentParams params = new UserNewsCommentParams();
	params.page = pageNumber;
	params.pageSize = 10;
	params.userId = targetNews.getUserId();
	params.newsId = targetNews.getNewsId();

	AppServiceImpl.getInstance().loadNewsComments(params, new OnNewsCommentListener() {

	    @Override
	    public void onSuccess(LoadUserNewsComments comments) {

		pbLoading.setVisibility(View.GONE);
		// 留言总评论数
		tvHeaderCommentCount.setText(comments.getTotalCommentCount() + "");
		tvNewsPosttime.setText(comments.getNewsPostTime());

		if (comments.getComments().size() != 0) {
		    if (pageNumber == 1) { // 是刷新
			mUserNewsCommentAdapter.getList().clear();
		    }
		    tvLoading.setVisibility(View.GONE);

		    mUserNewsCommentAdapter.addAll(comments.getComments());
		} else {
		    tvLoading.setVisibility(View.VISIBLE);
		    if (pageNumber == 1) {
			if (mUserNewsCommentAdapter.getList().size() == 0) {
			    tvLoading.setText("暂无评论");
			} else {
			    tvLoading.setText("暂无更多评论");
			}
		    } else {
			tvLoading.setText("暂无更多评论");
		    }
		}

		pageNumber++;

		mNewsCommentListView.stopRefresh();
		mNewsCommentListView.stopLoadMore();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(UserNewsCommentActivity.this, errorMsg);

		pbLoading.setVisibility(View.GONE);
		tvLoading.setText("连接超时,请重试");

		mNewsCommentListView.stopRefresh();
		mNewsCommentListView.stopLoadMore();
	    }
	});
    }

    private boolean isPosting; // 发布中...

    private void publishComment() {
	if (isPosting) {
	    return;
	}

	if (TextUtils.isEmpty(etCommentContent.getText())) {
//	    etCommentContent.setShakeAnimation();
	    etCommentContent.requestFocus();
	    return;
	}

	isPosting = true;
	PublishNewsCommentParams publishParams = new PublishNewsCommentParams();
	publishParams.content = etCommentContent.getText().toString();
	publishParams.newsId = targetNews.getNewsId();
	publishParams.newsUserId = targetNews.getUserId();
	publishParams.replyCommentId = mCurrentReplyComment != null ? mCurrentReplyComment.getId() : -1;

	// TODO Auto-generated method stub
	AppServiceImpl.getInstance().publishNewsComment(publishParams, new OnPublishNewsCommentListener() {

	    @Override
	    public void onSuccess(UserNewsComment comment) {

		// 发布成功, 恢复原状 
		etCommentContent.setText("");
		mCurrentReplyComment = null;
		layoutReply.setVisibility(View.GONE);
		// 隐藏表情 view
		findViewById(R.id.layout_emo).setVisibility(View.GONE);
		// 软键盘
		ActivityUtil.hideSoftInput(UserNewsCommentActivity.this);

		isPosting = false;
		mUserNewsCommentAdapter.add(comment);
		// 临时更新数量,这数量可能会不对,只是在listview 中加载了该条评论, 并未刷新所有数据
		targetNews.setCommentsCount(targetNews.getCommentsCount() + 1);
		tvHeaderCommentCount.setText(targetNews.getCommentsCount() + "");

		// 隐藏头部提示加载的textview 
		tvLoading.setVisibility(View.GONE);

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(UserNewsCommentActivity.this, errorMsg);
		isPosting = false;
	    }
	});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// 这里是什么问题呢?
	position = position - mNewsCommentListView.getHeaderViewsCount();
	if (position < 0 || position >= this.mUserNewsCommentAdapter.getList().size()) {
	    return;
	}

	UserNewsComment comment = this.mUserNewsCommentAdapter.getList().get(position);
	//	// 不能回复自己
	//	if (comment.getUserId() == MyApplication.getInstance().getCurrentLoginedUser().getUserId()) {
	//	    return;
	//	}
	mCurrentReplyComment = comment;
	layoutReply.setVisibility(View.VISIBLE);
	tvReply.setText("回复 " + comment.getNickname());
    }

    private void initEmoView() {
	ViewPager viewpager = (ViewPager) findViewById(R.id.pager_emo);
	List<View> views = new ArrayList<View>();
	List<FaceText> emos01 = FaceTextUtils.faceTexts;
	views.add(getGridView(emos01));
//	List<FaceText> emos02 = FaceTextUtils.faceTexts2;
//	views.add(getGridView(emos02));
	viewpager.setAdapter(new EmoViewPagerAdapter(views));

    }

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
		try {
		    if (etCommentContent != null && !TextUtils.isEmpty(key)) {
			int start = etCommentContent.getSelectionStart();
			CharSequence content = etCommentContent.getText().insert(start, key);
			etCommentContent.setText(content);
			// 定位光标位置
			CharSequence info = etCommentContent.getText();
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

}
