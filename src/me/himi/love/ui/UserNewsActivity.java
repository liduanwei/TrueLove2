package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.AppServiceImpl;
import me.himi.love.IAppService.OnNewsListener;
import me.himi.love.IAppService.UserNewsParams;
import me.himi.love.IAppServiceExtend.DeleteNewsPostParams;
import me.himi.love.IAppServiceExtend.OnDeleteNewsResponseListener;
import me.himi.love.IAppServiceExtend.OnUpdateNewsResponseListener;
import me.himi.love.IAppServiceExtend.UpdateNewsPostParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.UserNewsAdapter;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.UserNews;
import me.himi.love.im.util.FaceTextUtils;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ToastFactory;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
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
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class UserNewsActivity extends BaseActivity {

    DetailInfoUser targetUser;
    me.himi.love.view.list.XListView mNewsListView;

    UserNewsAdapter mUserNewsAdapter;

    private boolean isMine; // 是当前登录用户的留言

    View emptyNewsView;// 没有留言展示的view

    TextView tvLoading;

    ProgressBar pbLoading;// 加载中
    TextView tvLoadRetry;// 重试

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_user_news);

	init();

    }

    private UserNews mSelectedNews; // 当前被选择的条目

    private void init() {
	Object o = getIntent().getSerializableExtra("user");
	if (o != null) {
	    targetUser = (DetailInfoUser) o;
	    // 判断是否为当前登录用户页面
	    isMine = (targetUser.getUserId() == MyApplication.getInstance().getCurrentLoginedUser().getUserId());
	}

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	if (!isMine) {
	    tvTopTitle.setText("正在查看 " + targetUser.getNickname() + " 的留言");
	} else {

	    tvTopTitle.setText("我的留言");
	    tvTopAction.setVisibility(View.VISIBLE);
	    tvTopAction.setText("+");
	    tvTopAction.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    startActivity(new Intent(UserNewsActivity.this, EditNewsActivity.class));
		    finish();
		}
	    });
	}

	tvLoading = (TextView) findViewById(R.id.tv_loading);

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

		menu.add(0, 1, 0, "复制文本"); // order 0

		// 非当前登录用户不显示修改选项
		if (!(mSelectedNews.getUserId() == MyApplication.getInstance().getCurrentLoginedUser().getUserId())) {
		    return;
		}

		if (mSelectedNews.isPrivate()) {
		    menu.add(0, 2, 1, "设为公开");
		} else {
		    menu.add(0, 2, 1, "设为私有");
		}
		if (mSelectedNews.isAllowComment()) {
		    menu.add(0, 3, 2, "设为不允许评论");
		} else {
		    menu.add(0, 3, 2, "设为允许评论");
		}
		menu.add(0, 4, 3, "删除");
	    }
	});
	loadUserNews();
    }

    int pageNumber = 1;

    private void loadUserNews() {

	pbLoading.setVisibility(View.VISIBLE);
	tvLoadRetry.setVisibility(View.GONE);

	UserNewsParams params = new UserNewsParams();
	params.page = pageNumber;
	params.pageSize = 10;
	params.userId = targetUser.getUserId();

	AppServiceImpl.getInstance().loadNewsList(params, new OnNewsListener() {

	    @Override
	    public void onSuccess(List<UserNews> news) {

		if (news.size() != 0) {
		    if (pageNumber == 1) {
			mUserNewsAdapter.getList().clear();
		    }
		    mUserNewsAdapter.addAll(news);
		    // 隐藏
		    tvLoading.setVisibility(View.GONE);
		} else {

		    if (mUserNewsAdapter.getList().size() == 0) {
			showEmptyView();
			tvLoading.setVisibility(View.VISIBLE);
			tvLoading.setText("暂无");
		    } else {
			ToastFactory.getToast(UserNewsActivity.this, "暂无更多留言").show();
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
		ActivityUtil.show(UserNewsActivity.this, errorMsg);

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
	    ActivityUtil.copy(UserNewsActivity.this, FaceTextUtils.trimAllFace(mSelectedNews.getContent()));
	    showToast("已复制到剪贴板");
	    break;
	case 2: // 设为私有/公开
	    UpdateNewsPostParams postParams = new UpdateNewsPostParams();
	    postParams.newsId = mSelectedNews.getNewsId();
	    postParams.isPrivate = !mSelectedNews.isPrivate();
	    postParams.isAllowComment = mSelectedNews.isAllowComment();
	    updateNews(postParams, new OnSuccessListener() {

		@Override
		public void onSuccess() {
		    // TODO Auto-generated method stub
		    mSelectedNews.setPrivate(!mSelectedNews.isPrivate());
		    mUserNewsAdapter.notifyDataSetChanged();

		}
	    });
	    break;
	case 3: // 设为允许/不允许评论
	    UpdateNewsPostParams postParams2 = new UpdateNewsPostParams();
	    postParams2.newsId = mSelectedNews.getNewsId();
	    postParams2.isPrivate = mSelectedNews.isPrivate();
	    postParams2.isAllowComment = !mSelectedNews.isAllowComment();
	    updateNews(postParams2, new OnSuccessListener() {

		@Override
		public void onSuccess() {
		    // TODO Auto-generated method stub
		    mSelectedNews.setAllowComment(!mSelectedNews.isAllowComment());
		    mUserNewsAdapter.notifyDataSetChanged();
		}
	    });
	    break;
	case 4:
	    DeleteNewsPostParams postParams3 = new DeleteNewsPostParams();
	    postParams3.newsId = mSelectedNews.getNewsId();
	    deleteNews(postParams3, new OnSuccessListener() {

		@Override
		public void onSuccess() {
		    // TODO Auto-generated method stub
		    mUserNewsAdapter.getList().remove(mSelectedNews);
		    mUserNewsAdapter.notifyDataSetChanged();
		}
	    });
	    break;

	}
	return super.onContextItemSelected(item);
    }

    /**
     * 没有留言
     */
    private void showEmptyView() {
	if (null == emptyNewsView) {
	    emptyNewsView = LayoutInflater.from(UserNewsActivity.this).inflate(R.layout.empty_visitors, null);
	}
	mNewsListView.setEmptyView(emptyNewsView);
    }

    /**
     * 修改留言,成功后回调(用于及时修改内存中的操作条目)
     * @param postParams
     * @param listener
     */
    private void updateNews(UpdateNewsPostParams postParams, final OnSuccessListener listener) {
	AppServiceExtendImpl.getInstance().updateNews(postParams, new OnUpdateNewsResponseListener() {

	    @Override
	    public void onSuccess() {
		// TODO Auto-generated method stub
		showToast("修改成功");
		listener.onSuccess();
		//		pageNumber = 1;
		//		loadUserNews();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
	    }
	});
    }

    /**
     * 删除留言
     * @param postParams
     * @param listener
     */
    private void deleteNews(DeleteNewsPostParams postParams, final OnSuccessListener listener) {
	AppServiceExtendImpl.getInstance().deleteNews(postParams, new OnDeleteNewsResponseListener() {

	    @Override
	    public void onSuccess() {
		listener.onSuccess();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		showToast(errorMsg);
	    }
	});
    }

    private interface OnSuccessListener {
	void onSuccess();
    }

}
