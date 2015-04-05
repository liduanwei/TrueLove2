package me.himi.love.ui.fragment;

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
import java.util.Timer;
import java.util.TimerTask;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.LoadArticlesPostParams;
import me.himi.love.IAppServiceExtend.OnLoadArticlesResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.ArticleAdapter;
import me.himi.love.entity.Article;
import me.himi.love.ui.ArticleCommentsActivity;
import me.himi.love.ui.EditArticleActivity;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:SecretFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class ArticleFragment extends BaseFragment implements OnItemClickListener {

    View mContainerView;

    XListView mListView;

    ArticleAdapter mAdapter;

    TextView tvTopAction;

    List<Article> datas = new ArrayList<Article>();

    View mLayoutNewArticlesTips; // 新贴提示

    private static final String cacheArticlesPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/articles";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	mContainerView = inflater.inflate(R.layout.fragment_article, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	//	init();
	super.onActivityCreated(savedInstanceState);

    }

    private void init() {
	tvTopAction = (TextView) getActivity().findViewById(R.id.tv_top_action);
	tvTopAction.setText("发帖");

	mListView = (XListView) mContainerView.findViewById(R.id.listview_article);
	mAdapter = new ArticleAdapter(getActivity(), datas);
	mListView.setAdapter(mAdapter);

	// 使用代理 设置ImageLoader  在 ListView 滚动时不加载图片
	//mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
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
	mContainerView.findViewById(R.id.tv_start_publish_article).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), EditArticleActivity.class);
		startActivity(intent);
	    }
	});

	// 顶部提示新帖数量
	mLayoutNewArticlesTips = mContainerView.findViewById(R.id.layout_top_unload_articles);
	// 点击加载新帖
	mLayoutNewArticlesTips.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View arg0) {
		// TODO Auto-generated method stub
		loadArticles(); //  
	    }
	});

	//loadArticles();
	loadFromCache();
	// 广告初始化
	initAds();

    }

    private void initAds() {
	// TODO Auto-generated method stub
	initDomobAd();
    }

    /**
     * 广告 初始化
     */
    private void initDomobAd() {
	cn.domob.android.ads.AdView adview = new cn.domob.android.ads.AdView(MyApplication.getInstance().getTopActivity(), "56OJzoHYuN5N9Wvxuc", "16TLmU5aApZM2NUOnjMjOyei");
	// // 广告 container
	RelativeLayout adContainer = (RelativeLayout) mContainerView.findViewById(R.id.layout_adcontainer1);
	//	adview320x50.setGravity(Gravity.CENTER);
	//	adview.setKeyword("game");
	//	adview.setUserGender("male");
	//	adview.setUserBirthdayStr("2000-08-08");
	//	adview.setUserPostcode("123456");
	//	adview.setAdEventListener(null);

	adContainer.addView(adview);
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

    private Handler mHandler = new Handler() {
	public void handleMessage(android.os.Message msg) {
	    switch (msg.what) {
	    case 0:
		loadNewestCount();
		break;
	    }
	}

	private void loadNewestCount() {
	    // TODO Auto-generated method stub
	    String url = Constants.URL_NEWARTICLE_COUNT;
	    RequestParams params = new RequestParams();
	    int lastTime = 0;
	    if (mAdapter.getList().size() != 0) {
		lastTime = mAdapter.getList().get(0).getCreateTime(); // 获取当前第一个帖子的时间
	    }
	    params.put("last_time", lastTime); // 当前最新帖子时间, 获取该时间之后的帖子
	    AsyncHttpResponseHandler resHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		    // TODO Auto-generated method stub
		    String data = new String(arg2);
		    try {
			JSONObject jsonObj = new JSONObject(data);
			int msgCount = jsonObj.getInt("count");
			//			if (msgCount == 0) {
			//			    return;
			//			}

			if (mLayoutNewArticlesTips.getVisibility() == View.GONE) {
			    mLayoutNewArticlesTips.setVisibility(View.VISIBLE);
			    TranslateAnimation transAnim = new TranslateAnimation(0, 0, -100, 0);
			    transAnim.setDuration(200L);
			    mLayoutNewArticlesTips.startAnimation(transAnim);
			}

			TextView tvCounts = (TextView) mLayoutNewArticlesTips.findViewById(R.id.tv_unload_articles_count);
			tvCounts.setText("有" + msgCount + "篇新帖子,下拉获取");
		    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		    mHandler.sendEmptyMessageDelayed(0, 1000 * 20);// 20s 后再次加载
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		    // TODO Auto-generated method stub
		    mHandler.sendEmptyMessageDelayed(0, 1000 * 20);// 20s 后再次加载
		}
	    };
	    HttpUtil.post(url, params, resHandler);
	};
    };

    private void loadArticles() {
	if (isRefreshing) {
	    return;
	}

	isRefreshing = true;

	LoadArticlesPostParams postParams = new LoadArticlesPostParams();
	postParams.page = pageNumber;
	postParams.pageSize = 20;
	postParams.order = "create_time DESC";

	AppServiceExtendImpl.getInstance().loadArticles(postParams, new OnLoadArticlesResponseListener() {

	    @Override
	    public void onSuccess(List<Article> secrets) {
		// TODO Auto-generated method stub
		mLayoutNewArticlesTips.setVisibility(View.GONE); // 隐藏新帖提示

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

		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 1000 * 20);// 开始轮询最新帖子的数量
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

    /**
     * 发贴
     */
    private View.OnClickListener editArticleOnClickListener = new View.OnClickListener() {

	@Override
	public void onClick(View v) {
	    // TODO Auto-generated method stub
	    Intent intent = new Intent(getActivity(), EditArticleActivity.class);
	    startActivity(intent);
	}
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
	if (!hidden) {
	    if (tvTopAction != null) {
		tvTopAction.setVisibility(View.VISIBLE);
		tvTopAction.setText("发贴");
		tvTopAction.setOnClickListener(editArticleOnClickListener);
	    }
	}
	super.onHiddenChanged(hidden);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	position = position - 1;//mListView.getHeaderViewsCount();

	if (position <= -1 || position >= mAdapter.getList().size()) {
	    return;
	}
	Article article = mAdapter.getList().get(position);

	Intent intent = new Intent(this.getActivity(), ArticleCommentsActivity.class);
	Bundle bundle = new Bundle();
	bundle.putSerializable("article", article);
	intent.putExtras(bundle);
	startActivity(intent);

    }

}
