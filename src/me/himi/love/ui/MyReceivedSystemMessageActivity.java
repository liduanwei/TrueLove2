package me.himi.love.ui;

import me.himi.love.R;
import me.himi.love.entity.SystemMessage;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.Constants;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

/**
 * 用户收到系统消息
 * @ClassName:MyReceivedSystemMessageActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class MyReceivedSystemMessageActivity extends BaseActivity {

    private static final String TAG = MyReceivedSystemMessageActivity.class.getSimpleName();
    TextView tvTopTitle, tvTopAction;
    PullToRefreshWebView mWebView;

    SystemMessage msg;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_system_message);

	init();

    }

    private void init() {

	java.io.Serializable o = getIntent().getSerializableExtra("msg");
	if (null != o) {
	    msg = (SystemMessage) o;
	}

	if (msg == null) {
	    //
	    Log.i(TAG, "msg is null");
	    //	    finish();
	}

	tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
	tvTopAction = (TextView) findViewById(R.id.tv_top_action);
	tvTopAction.setText("");

	tvTopAction.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		
	    }
	});

	this.mWebView = (PullToRefreshWebView) findViewById(R.id.pullWebView);

	this.mWebView.getRefreshableView().getSettings().setJavaScriptEnabled(true);

	//	this.mWebView.getRefreshableView().loadUrl(msg.getLinkUrl());
	this.mWebView.getRefreshableView().loadUrl(Constants.URL_CAT_GAME);

	this.mWebView.setMode(Mode.BOTH);

	this.mWebView.setOnRefreshListener(new OnRefreshListener<WebView>() {

	    @Override
	    public void onRefresh(PullToRefreshBase<WebView> refreshView) {
		// TODO Auto-generated method stub
		mWebView.setState(State.RESET);
	    }
	});

    }

}
