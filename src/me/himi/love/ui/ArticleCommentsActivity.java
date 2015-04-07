package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadArticleCommentsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadArticleCommentsResponseListener;
import me.himi.love.IAppServiceExtend.OnPublishArticleCommentResponseListener;
import me.himi.love.IAppServiceExtend.OnVoteArticleLoveResponseListener;
import me.himi.love.IAppServiceExtend.PublishArticleCommentPostParams;
import me.himi.love.IAppServiceExtend.VoteArticleLovePostParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.ArticleCommentsAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.Article;
import me.himi.love.entity.ArticleComment;
import me.himi.love.im.adapter.EmoViewPagerAdapter;
import me.himi.love.im.adapter.EmoteAdapter;
import me.himi.love.im.entity.FaceText;
import me.himi.love.im.ui.customview.EmoticonsEditText;
import me.himi.love.im.util.FaceTextUtils;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 文章评论
 * @ClassName:ArticleCommentActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class ArticleCommentsActivity extends BaseActivity implements OnItemClickListener {

    Article targetArticle;

    me.himi.love.view.list.XListView mListView;

    ArticleCommentsAdapter mCommentsAdapter;
    EmoticonsEditText etCommentContent;
    TextView tvPublishComment;

    View layoutReply; //
    TextView tvReply, tvReplyCancle;
    ArticleComment mCurrentReplyComment; // 当前想要回复的评论

    ProgressBar pbLoading; // 头部底部的加载中
    TextView tvLoading; // 同上

    TextView tvShowEmo; // 插入 表情按钮

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_article_comments);

	init();
    }

    private void init() {

	Object o = getIntent().getSerializableExtra("article");
	if (o != null) {
	    targetArticle = (Article) o;
	} else {
	    ActivityUtil.show(this, "没有指定帖子");
	    finish();
	}

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopTitle.setText("查看帖子");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	if (targetArticle.getUserId() == MyApplication.getInstance().getCurrentLoginedUser().getUserId()) {
	    tvTopAction.setVisibility(View.VISIBLE);
	    tvTopAction.setText("");
	} else {
	    tvTopAction.setVisibility(View.GONE);
	    tvTopAction.setText("");
	}

	// 输入框上面的回复谁布局容器
	layoutReply = findViewById(R.id.layout_article_comment_reply);
	layoutReply.setVisibility(View.GONE);
	// 回复 谁:
	tvReply = (TextView) findViewById(R.id.tv_article_comment_reply);
	// 取消回复 按钮
	tvReplyCancle = (TextView) findViewById(R.id.tv_article_comment_reply_cancle);
	tvReplyCancle.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		mCurrentReplyComment = null;
		layoutReply.setVisibility(View.GONE);
		tvReply.setText("");
	    }

	});

	tvShowEmo = (TextView) findViewById(R.id.tv_article_comment_emo);
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
		    ActivityUtil.closeSoftInput(ArticleCommentsActivity.this, etCommentContent);
		}
	    }
	});

	etCommentContent = (EmoticonsEditText) findViewById(R.id.et_article_comment_content);
	etCommentContent.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		View view = findViewById(R.id.layout_emo);
		view.setVisibility(View.GONE);
		ActivityUtil.showSoftInputView(ArticleCommentsActivity.this, etCommentContent);
	    }
	});

	// 发布评论
	tvPublishComment = (TextView) findViewById(R.id.tv_article_comment_publish);

	tvPublishComment.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		publishComment();
	    }

	});

	mListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview_article_comments);
	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	//	mNewsCommentListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	// 顶部的帖子布局容器

	RelativeLayout layoutFooterLoading = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.user_news_comment_loading, null);

	pbLoading = (ProgressBar) layoutFooterLoading.findViewById(R.id.pb_user_news_comment_loading);
	tvLoading = (TextView) layoutFooterLoading.findViewById(R.id.tv_user_news_comment_loading);

	// 底部提示加载中
	mListView.addFooterView(layoutFooterLoading);

	// 设置无数据时显示的view
	//	mNewsCommentListView.setEmptyView(v);

	mCommentsAdapter = new ArticleCommentsAdapter(this, new ArrayList<ArticleComment>());

	mListView.setAdapter(mCommentsAdapter);

	//顶部帖子详细
	View v = createArticleView();
	mListView.addHeaderView(v);

	mListView.setPullLoadEnable(true);
	mListView.setPullRefreshEnable(false); // 因按评论时间升序,所以不提供刷新,只能加载更多
	mListView.pullRefreshing();

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadArticleComments();
		tvLoading.setVisibility(View.GONE);
		pbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadArticleComments();
	    }
	});

	loadArticleComments();

	// 
	mListView.setOnItemClickListener(this);

	// 默认先隐藏软键盘
	ActivityUtil.hideSoftInput(this);

	// 表情view
	initEmoView();
    }

    private View createArticleView() {

	View convertView = LayoutInflater.from(this).inflate(R.layout.article_item, null);

	final Article article = this.targetArticle;

	// 文章配图
	ImageView ivBackground = ViewHolder.get(convertView, R.id.iv_article_image);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);

	TextView tvNickname = ViewHolder.get(convertView, R.id.tv_user_nickname);

	TextView tvContent = ViewHolder.get(convertView, R.id.tv_secret_content); // 内容

	TextView tvTime = ViewHolder.get(convertView, R.id.tv_time); // 时间

	ImageView ivVip = ViewHolder.get(convertView, R.id.iv_vip);

	final ImageView ivLoves = ViewHolder.get(convertView, R.id.iv_likes); //赞图标
	final TextView tvLoves = ViewHolder.get(convertView, R.id.tv_likes); // 赞次数
	TextView tvComments = ViewHolder.get(convertView, R.id.tv_comments); // 评论数

	tvLoves.setText(article.getLove() + "");
	tvComments.setText(article.getComments() + "");

	tvTime.setText(ActivityUtil.convertTimeToString(article.getCreateTime() * 1000L));

	// vip
	ivVip.setImageResource(article.getVip() != 0 ? R.drawable.vip : R.drawable.vipnot);
	tvNickname.setTextColor(getResources().getColor(article.getVip() != 0 ? R.color.c_vip : R.color.c_not_vip));
	//	ivVip.setVisibility(user.getVip() != 0 ? View.VISIBLE : View.GONE);

	if (article.getContentImageUrl().getSmallImageUrl() == null || article.getContentImageUrl().getSmallImageUrl().trim().length() == 0) {
	    ivBackground.setVisibility(View.GONE);
	} else {
	    ivBackground.setVisibility(View.VISIBLE);
	    String imageUrl = article.getContentImageUrl().getSmallImageUrl();
	    // 文章配图
	    ImageLoader.getInstance().displayImage(imageUrl, ivBackground, ImageLoaderOptions.normalOptions(), new ImageLoadingListener() {

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
		    mCommentsAdapter.notifyDataSetChanged();
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
		    // TODO Auto-generated method stub

		}
	    });
	}

	// displayImage 会有刷新闪烁问题
	ImageLoader.getInstance().displayImage(article.getFaceUrl().getSmallImageUrl(), ivFace, ImageLoaderOptions.circleOptions(), new ImageLoadingListener() {

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
		mCommentsAdapter.notifyDataSetChanged();
	    }

	    @Override
	    public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }
	});

	tvNickname.setText(article.getNickname());

	tvContent.setText(article.getContent()); // 秘密的内容

	View love = ViewHolder.get(convertView, R.id.layout_likes);
	love.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		VoteArticleLovePostParams postParams = new VoteArticleLovePostParams();
		postParams.articleId = targetArticle.getId();
		AppServiceExtendImpl.getInstance().loveArticle(postParams, new OnVoteArticleLoveResponseListener() {

		    @Override
		    public void onSuccess() {
			// TODO Auto-generated method stub
			tvLoves.setText(Integer.parseInt(tvLoves.getText().toString().trim()) + 1 + "");
			ivLoves.setBackgroundResource(R.drawable.player_collection_pressed);
		    }

		    @Override
		    public void onFailure(String errorMsg) {
			// TODO Auto-generated method stub
			showToast(errorMsg);
		    }
		});
	    }
	});
	// 分享
	View v = ViewHolder.get(convertView, R.id.iv_article_more);
	v.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		testShare();
	    }
	});
	return convertView;
    }

    private void testShare() {
	ShareSDK.initSDK(this);
	OnekeyShare oks = new OnekeyShare();
	//关闭sso授权
	oks.disableSSOWhenAuthorize();

	// 分享时Notification的图标和文字
	oks.setNotification(R.drawable.icon, getString(R.string.app_name));
	// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	oks.setTitle(getString(R.string.share));
	// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	oks.setTitleUrl("http://sharesdk.cn");
	// text是分享文本，所有平台都需要这个字段
	oks.setText(getResources().getString(R.string.app_name) + "征婚交友App,助依然守候的你在茫茫人海中找到相守一生对的那个Ta");
	// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	oks.setImagePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.png");//确保SDcard下面存在此张图片
	// url仅在微信（包括好友和朋友圈）中使用
	oks.setUrl("http://himi.me");
	// comment是我对这条分享的评论，仅在人人网和QQ空间使用
	oks.setComment(getResources().getString(R.string.app_name) + ",助依然守候的你在茫茫人海中找到相守一生对的那个Ta");
	// site是分享此内容的网站名称，仅在QQ空间使用
	oks.setSite(getString(R.string.app_name));
	// siteUrl是分享此内容的网站地址，仅在QQ空间使用
	oks.setSiteUrl("http://himi.me");

	// 启动分享GUI
	oks.show(this);
    }

    int pageNumber = 1;

    private boolean isRefreshing;// 刷新中

    /**
     * 加载评论
     */
    private void loadArticleComments() {

	if (isRefreshing) {
	    return;
	}

	isRefreshing = true;

	LoadArticleCommentsPostParams postParams = new LoadArticleCommentsPostParams();
	postParams.articleId = targetArticle.getId();
	postParams.page = pageNumber;
	postParams.pageSize = 20;

	AppServiceExtendImpl.getInstance().loadArticleComments(postParams, new OnLoadArticleCommentsResponseListener() {

	    @Override
	    public void onSuccess(List<ArticleComment> comments) {

		pbLoading.setVisibility(View.GONE);

		if (comments.size() != 0) {
		    if (pageNumber == 1) { // 是刷新
			mCommentsAdapter.getList().clear();
		    }
		    tvLoading.setVisibility(View.GONE);

		    mCommentsAdapter.addAll(comments);
		} else {
		    tvLoading.setVisibility(View.VISIBLE);
		    if (pageNumber == 1) {
			if (mCommentsAdapter.getList().size() == 0) {
			    tvLoading.setText("暂无评论");
			} else {
			    tvLoading.setText("暂无更多评论");
			}
		    } else {
			tvLoading.setText("暂无更多评论");
		    }
		}

		pageNumber++;

		isRefreshing = false;

		mListView.stopRefresh();
		mListView.stopLoadMore();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(ArticleCommentsActivity.this, errorMsg);

		pbLoading.setVisibility(View.GONE);
		tvLoading.setText("网络超时,请重试");

		mListView.stopRefresh();
		mListView.stopLoadMore();
		
		isRefreshing = false;

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
	PublishArticleCommentPostParams postParams = new PublishArticleCommentPostParams();
	postParams.articleId = targetArticle.getId();
	if (mCurrentReplyComment != null) {
	    postParams.parentCommentId = mCurrentReplyComment.getId();
	}
	postParams.content = etCommentContent.getText().toString();

	AppServiceExtendImpl.getInstance().publishArticleComment(postParams, new OnPublishArticleCommentResponseListener() {

	    @Override
	    public void onSuccess(ArticleComment comment) {
		// TODO Auto-generated method stub

		// 发布成功, 恢复原状 
		etCommentContent.setText("");
		mCurrentReplyComment = null;
		layoutReply.setVisibility(View.GONE);
		// 隐藏表情 view
		findViewById(R.id.layout_emo).setVisibility(View.GONE);
		// 软键盘
		ActivityUtil.hideSoftInput(ArticleCommentsActivity.this);

		isPosting = false;
		mCommentsAdapter.add(comment);

		// 隐藏头部提示加载的textview 
		tvLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(ArticleCommentsActivity.this, errorMsg);
		isPosting = false;
	    }
	});

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// 这里是什么问题呢?
	position = position - mListView.getHeaderViewsCount();
	if (position < 0 || position >= this.mCommentsAdapter.getList().size()) {
	    return;
	}

	ArticleComment comment = this.mCommentsAdapter.getList().get(position);
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
