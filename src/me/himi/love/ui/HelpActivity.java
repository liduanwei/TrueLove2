package me.himi.love.ui;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;

import me.himi.love.R;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.Constants;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * @ClassName:OffermeActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class HelpActivity extends BaseActivity implements OnClickListener {
    PullToRefreshWebView mWebView;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_help);

	init();

    }

    private void init() {
	TextView tvTopTitle = getViewById(R.id.tv_top_title);
	TextView tvTopAction = getViewById(R.id.tv_top_action);

	tvTopTitle.setText("帮助说明");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});
	tvTopAction.setText("");

	// 说明文字
	this.mWebView = (PullToRefreshWebView) findViewById(R.id.pullWebView);

//	this.mWebView.getRefreshableView().getSettings().setJavaScriptEnabled(true);
	this.mWebView.getRefreshableView().getSettings().setBuiltInZoomControls(true);
	//	this.mWebView.getRefreshableView().loadUrl(msg.getLinkUrl());

	String data = "<div class=\"dataTables_wrapper\" id=\"itemsss_wrapper\"><table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\" frame=\"hsides\" rules=\"rows\" id=\"itemsss\">\n" + " <thead>\n" + "    <tr class=\"sdkbar\" style=\"font-weight:bold;\"><th width=\"13%\" class=\"sorting_desc\" rowspan=\"1\" colspan=\"1\">\n" + "            提交时间\n" + "        </th><th width=\"9%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            应用名称\n" + "        </th><th class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "           用户ID\n" + "        </th><th class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            订单号\n" + "        </th><th width=\"5%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            渠道号\n"
		+ "        </th><th width=\"9%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            商品名称\n" + "        </th><th width=\"5%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            订单金额\n" + "        </th><th width=\"5%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            支付方式\n" + "        </th><th width=\"5%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            实付金额\n" + "        </th><th width=\"5%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            交易状态\n" + "        </th><th width=\"5%\" class=\"sorting\" rowspan=\"1\" colspan=\"1\">\n" + "            回调状态\n" + "        </th></tr>\n" + "     </thead>\n" + "                    \n" + "      \n" + " \n" + "<tbody><tr title=\"支付成功\" class=\"odd\">\n"
		+ "     <td align=\"center\" height=\"28px\" class=\" sorting_1\">2015-03-13 22:47:54</td> \n" + "        <td align=\"center\">\n" + "         \n" + "            恋恋\n" + "       \n" + "\n" + "        </td>\n" + "        <td align=\"center\">\n" + "            211111\n" + "        </td>\n" + "	<td align=\"center\">\n" + "            truelove26a78606de92a782d262df9128931675d  <br>\n" + "	    <font style=\"font-size: 10px;\" color=\"grey\">20150313224757679927743</font>\n" + "        </td>\n" + "	<td align=\"center\">\n" + "            WAPS\n" + "        </td>\n" + "	 \n" + "	<td align=\"center\">\n" + "            购买100恋恋币\n" + "        </td>\n" + "	<td align=\"center\">\n" + "            ￥10.00\n" + "        </td>\n" + "	<td align=\"center\">\n" + "            支付宝\n" + "        </td>\n" + "\n" + "	   \n"
		+ "	<td align=\"center\" style=\"font-weight:800 ;\">\n" + "	￥10.00\n" + "        </td>\n" + "\n" + "	<td align=\"center\">\n" + "	\n" + "\n" + "	 <font color=\"green\">\n" + "            支付成功\n" + "	     </font>\n" + "            \n" + "     \n" + "	</td><td align=\"center\">\n" + "	 \n" + " 成功\n" + "	\n" + "	\n" + "	</td>\n" + "	 \n"
		+ "    </tr></tbody></table><div class=\"dataTables_paginate paging_full_numbers\" id=\"itemsss_paginate\"><span class=\"first paginate_button paginate_button_disabled\" id=\"itemsss_first\">第一页</span><span class=\"previous paginate_button paginate_button_disabled\" id=\"itemsss_previous\"> 上一页 </span><span><span class=\"paginate_active\">1</span></span><span class=\"next paginate_button paginate_button_disabled\" id=\"itemsss_next\"> 下一页 </span><span class=\"last paginate_button paginate_button_disabled\" id=\"itemsss_last\"> 最后一页 </span></div></div>";

	this.mWebView.getRefreshableView().loadDataWithBaseURL(null, data, "text/html", "utf8", null);

	this.mWebView.setMode(Mode.BOTH);

	this.mWebView.setOnRefreshListener(new OnRefreshListener<WebView>() {

	    @Override
	    public void onRefresh(PullToRefreshBase<WebView> refreshView) {
		// TODO Auto-generated method stub
		mWebView.setState(State.RESET);
	    }
	});

    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub

    }

}
