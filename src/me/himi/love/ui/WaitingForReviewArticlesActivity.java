package me.himi.love.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadWaitingForReviewArticlesPostParams;
import me.himi.love.IAppServiceExtend.OnLoadArticlesResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.ArticleAdapter;
import me.himi.love.entity.Article;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.HttpUtil;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract.Constants;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 等待审核的帖子
 * @ClassName:WaitingForReviewArticlesActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class WaitingForReviewArticlesActivity extends BaseActivity implements OnItemClickListener {
    XListView mListView;

    ArticleAdapter mAdapter;

    TextView tvTopAction;

    List<Article> datas = new ArrayList<Article>();

    private static final String cacheArticlesPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/watingforreview_articles_";

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_waitingforreview_articles);
	init();
    }

    private void init() {
	final TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");
	tvTopTitle.setText("审贴");
	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WaitingForReviewArticlesActivity.this, EditArticleActivity.class);
		startActivity(intent);
	    }
	});

	mListView = (XListView) findViewById(R.id.listview_article);
	mAdapter = new ArticleAdapter(this, datas);
	mListView.setAdapter(mAdapter);

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	//	mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
	// 首先允许加载更多
	mListView.setPullLoadEnable(true);
	// 允许下拉
	mListView.setPullRefreshEnable(true);
	// 设置监听器
	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		loadArticles();
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		// 如果当前处于刷新状态则不加载更多
		loadArticles();
	    }
	});
	mListView.pullRefreshing();
	mListView.setDividerHeight(0);

	mListView.setOnItemClickListener(this);

	// 进入发布新内容
	findViewById(R.id.tv_start_publish_article).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(WaitingForReviewArticlesActivity.this, EditArticleActivity.class);
		startActivity(intent);
	    }
	});

	//loadArticles();
	loadFromCache();
    }

    private void loadFromCache() {
	// TODO Auto-generated method stub
	File f = new File(cacheArticlesPath);
	if (!f.exists()) {
	    loadArticles();
	    return;
	}

	try {
	    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
	    Object obj = ois.readObject();
	    List<Article> articles = (List<Article>) obj;
	    mAdapter.getList().clear();
	    mAdapter.addAll(articles);
	    ois.close();
	} catch (StreamCorruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private int pageNumber = 1;

    private boolean isRefreshing;// 刷新中...

    private void loadArticles() {
	if (isRefreshing) {
	    return;
	}

	isRefreshing = true;

	LoadWaitingForReviewArticlesPostParams postParams = new LoadWaitingForReviewArticlesPostParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;
	postParams.order = "create_time DESC";
	postParams.reviewStatus = 0; // 未审核状态

	AppServiceExtendImpl.getInstance().loadWaitingForReviewArticles(postParams, new OnLoadArticlesResponseListener() {

	    @Override
	    public void onSuccess(List<Article> secrets) {
		// TODO Auto-generated method stub
		if (secrets.size() != 0) {
		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(secrets);

		    // 缓存到本地
		    cacheToLocal(secrets);
		} else {
		    if (pageNumber == 1) { // 刷新没有数据
			mAdapter.getList().clear();
		    }
		    showToast(mAdapter.getList().size() != 0 ? "暂无更多" : "暂无数据");
		}

		isRefreshing = false;

		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		pageNumber++;
	    }

	    private void cacheToLocal(List<Article> secrets) {
		// TODO Auto-generated method stub
		File f = new File(cacheArticlesPath);
		if (!f.getParentFile().exists()) {
		    f.getParentFile().mkdirs();
		}
		try {
		    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		    oos.writeObject(secrets);
		    oos.close();
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	    @Override
	    public void onFailure(String errorMsg) {

		isRefreshing = false;
		// TODO Auto-generated method stub
		if (mListView.getPullLoading()) {
		    mListView.stopLoadMore();
		}
		if (mListView.getPullRefreshing()) {
		    mListView.stopRefresh();
		}
		showToast(errorMsg);
	    }
	});

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	// TODO Auto-generated method stub
	position = position - 1;//mListView.getHeaderViewsCount();

	if (position <= -1 || position >= mAdapter.getList().size()) {
	    return;
	}
	// 弹出操作菜单
	showMenu(mAdapter.getList().get(position), position, arg1);
    }

    private View menuView;
    private PopupWindow pwMenuWin;

    private void showMenu(final Article article, int pos, View v) {
	// TODO Auto-generated method stub
	if (null == pwMenuWin) {
	    menuView = getLayoutInflater().inflate(R.layout.layout_review_menu, null);
	    pwMenuWin = new PopupWindow(menuView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

	    pwMenuWin.setContentView(menuView);
	}

	//	pwMenuWin.showAtLocation(v, Gravity.TOP, 0, 0);
	//	pwMenuWin.showAsDropDown(mListView.getChildAt(pos), 0, 0, Gravity.CENTER);
	pwMenuWin.showAsDropDown(v);

	final String url = me.himi.love.util.Constants.URL_ARTICLE_REVIEW;
	final RequestParams params = new RequestParams();
	params.put("article_id", article.getId());

	final AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		showToast("操作成功");
		pwMenuWin.dismiss();
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		showToast("操作失败");
		pwMenuWin.dismiss();
	    }
	};

	// 允许通过
	menuView.findViewById(R.id.btn_review_allowpass).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		params.put("status", 1);
		HttpUtil.post(url, params, responseHandler);
	    }
	});

	// 禁止通过
	menuView.findViewById(R.id.btn_review_notallowpass).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		params.put("status", 2);
		HttpUtil.post(url, params, responseHandler);
	    }
	});

	// 保留
	menuView.findViewById(R.id.btn_review_cancle).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		//		pwMenuWin.dismiss();
		params.put("status", 0);
		HttpUtil.post(url, params, responseHandler);
	    }
	});

	final TextView tvContent = (TextView) menuView.findViewById(R.id.tv_article_content);
	tvContent.setText(article.getContent());
    }

    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	if (pwMenuWin != null && pwMenuWin.isShowing()) {
	    pwMenuWin.dismiss();
	    return;
	}
	super.onBackPressed();
    }

}
