package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.entity.StrangeNews;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;
import me.himi.love.util.Share;

import org.apache.http.Header;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 文章评论
 * @ClassName:ArticleCommentActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class StrangenewsAndStoryActivity extends BaseActivity {

    StrangeNews targetArticle;

    String data;

    com.handmark.pulltorefresh.library.PullToRefreshWebView mWebView;

    ProgressBar pbLoading;// 加载中
    TextView tvLoadRetry;// 重试

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_strangenews_detail);

	init();

    }

    private void init() {

	Object o = getIntent().getSerializableExtra("strangenews");
	if (o != null) {
	    targetArticle = (StrangeNews) o;
	} else {
	    ActivityUtil.show(this, "没有指定帖子");
	    finish();
	}

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);
	tvTopTitle.setText("查看正文");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	tvTopAction.setText("分享");
	tvTopAction.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Share.share(StrangenewsAndStoryActivity.this, targetArticle);
	    }
	});

	TextView tvTitle = getViewById(R.id.tv_title);
	tvTitle.setText(targetArticle.getTitle());

	mWebView = (PullToRefreshWebView) findViewById(R.id.pullWebView);
	// 单列图片适应屏幕
	mWebView.getRefreshableView().getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	// 
	mWebView.setOnRefreshListener(new OnRefreshListener<WebView>() {

	    @Override
	    public void onRefresh(PullToRefreshBase<WebView> refreshView) {
		// TODO Auto-generated method stub
		loadData();

	    }
	});

	pbLoading = getViewById(R.id.pb_loading);

	tvLoadRetry = getViewById(R.id.tv_load_retry);
	tvLoadRetry.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		loadData();
	    }
	});

	data = targetArticle.getSummary();

	loadFromCache();

    }

    private void loadFromCache() {
	// TODO Auto-generated method stub
	loadData();
    }

    private void loadData() {

	pbLoading.setVisibility(View.VISIBLE);
	tvLoadRetry.setVisibility(View.GONE);

	// TODO Auto-generated method stub
	mWebView.getRefreshableView().loadDataWithBaseURL(null, data, "text/html", "utf8", null);
	String url = Constants.URL_STRANGENEWS_DETAIL;
	RequestParams params = new RequestParams();
	params.put("id", targetArticle.getId());

	mWebView.setMode(Mode.BOTH);

	HttpUtil.post(url, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String res = new String(arg2);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(res);
		sb.append("</html>");
		mWebView.getRefreshableView().loadDataWithBaseURL(null, sb.toString(), "text/html", "utf8", null);
		mWebView.onRefreshComplete();
		pbLoading.setVisibility(View.GONE);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		showToast("加载失败");
		mWebView.onRefreshComplete();

		tvLoadRetry.setVisibility(View.VISIBLE);
	    }
	});

    }
}
