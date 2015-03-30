package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadFollowsNewsPostParams;
import me.himi.love.IAppServiceExtend.OnLoadFollowsNewsResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.UserNewsAdapter;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.UserNews;
import me.himi.love.im.util.FaceTextUtils;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.CacheUtils;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 关注用户的留言
 * @ClassName:FollowsNewsActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class FollowsNewsActivity extends BaseActivity {

    me.himi.love.view.list.XListView mNewsListView;

    UserNewsAdapter mUserNewsAdapter;

    View emptyNewsView;// 没有留言展示的view

    ProgressBar pbLoading;// 加载中
    TextView tvLoadRetry;// 重试

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_friends_news);

	init();

    }

    private UserNews mSelectedNews; // 当前被选择的条目

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	tvTopTitle.setText("留言动态");
	tvTopAction.setVisibility(View.VISIBLE);
	tvTopAction.setText("查看我的");
	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(FollowsNewsActivity.this, UserNewsActivity.class);
		Bundle bundle = new Bundle();
		DetailInfoUser user = new DetailInfoUser();
		user.setUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId());
		user.setNickname(MyApplication.getInstance().getCurrentLoginedUser().getNickname());
		bundle.putSerializable("user", user);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	    }
	});

	pbLoading = getViewById(R.id.pb_loading);

	tvLoadRetry = getViewById(R.id.tv_load_retry);
	tvLoadRetry.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		loadUserNews();
	    }
	});

	mNewsListView = (me.himi.love.view.list.XListView) findViewById(R.id.listview_news);

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	mNewsListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

	mUserNewsAdapter = new UserNewsAdapter(this, new ArrayList<UserNews>());

	mNewsListView.setAdapter(mUserNewsAdapter);

	mNewsListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadUserNews();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadUserNews();
	    }
	});

	// 初始显示加载中

	mNewsListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

	    @Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo mInfo = (AdapterContextMenuInfo) menuInfo;

		mSelectedNews = mUserNewsAdapter.getList().get(mInfo.position - 1);

		menu.add(0, 1, 3, "复制文本");

	    }
	});
	//	loadUserNews();
	loadFromCache();
    }

    // 使用本地缓存
    private final String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/follow_users_news_" + MyApplication.getInstance().getCurrentLoginedUser().getUserId();

    private void loadFromCache() {
	// TODO Auto-generated method stub
	List<UserNews> userNews = CacheUtils.loadFromCache(cachePath);
	if (userNews != null) {
	    mUserNewsAdapter.setList(userNews);
	    pbLoading.setVisibility(View.GONE);
	} else {
	    loadUserNews();
	}

    }

    int pageNumber = 1;

    private void loadUserNews() {

	pbLoading.setVisibility(View.VISIBLE);
	tvLoadRetry.setVisibility(View.GONE);

	LoadFollowsNewsPostParams params = new LoadFollowsNewsPostParams();
	params.page = pageNumber;
	params.pageSize = 10;
	params.userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId();

	AppServiceExtendImpl.getInstance().loadFollowsNews(params, new OnLoadFollowsNewsResponseListener() {

	    @Override
	    public void onSuccess(List<UserNews> news) {

		if (news.size() != 0) {
		    if (pageNumber == 1) {
			mUserNewsAdapter.getList().clear();
		    }
		    mUserNewsAdapter.addAll(news);

		    CacheUtils.cacheToLocal(mUserNewsAdapter.getList(), cachePath);
		} else {
		    if (mUserNewsAdapter.getList().size() == 0) {
			showEmptyView();
			ActivityUtil.show(FollowsNewsActivity.this, "暂无留言");
		    } else {
			ToastFactory.getToast(FollowsNewsActivity.this, "暂无更多留言").show();
		    }
		}
		pageNumber++;
		//
		mNewsListView.stopLoadMore();
		mNewsListView.stopRefresh();

		pbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ActivityUtil.show(FollowsNewsActivity.this, errorMsg);

		mNewsListView.stopLoadMore();
		mNewsListView.stopRefresh();

		tvLoadRetry.setVisibility(View.VISIBLE);
	    }
	});
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	// TODO Auto-generated method stub

	switch (item.getItemId()) {

	case 1:
	    // 去掉表情
	    ActivityUtil.copy(FollowsNewsActivity.this, FaceTextUtils.trimAllFace(mSelectedNews.getContent()));
	    showToast("已复制到剪贴板");
	    break;
	}
	return super.onContextItemSelected(item);
    }

    /**
     * 没有留言
     */
    private void showEmptyView() {
	if (null == emptyNewsView) {
	    emptyNewsView = LayoutInflater.from(FollowsNewsActivity.this).inflate(R.layout.empty_visitors, null);
	}
	mNewsListView.setEmptyView(emptyNewsView);
    }

}
