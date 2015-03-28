package me.himi.love.ui;

import me.himi.love.AppServiceBuyImpl;
import me.himi.love.IAppServiceBuy.OnBuyedLoveMoneyPostParams;
import me.himi.love.IAppServiceBuy.OnBuyedLoveMoneyResponseListener;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.Constants;
import me.himi.love.util.StringUtils;
import me.himi.love.util.ToastFactory;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.wanpu.pay.PayConnect;
import com.wanpu.pay.PayResultListener;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class BuyLoveMoneyActivity extends BaseActivity implements OnClickListener {

    DetailInfoUser targetUser;

    TextView mTvGetMoneyFree; // (积分墙)免费获取金币

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_buy_lovemoney);

	init();
	// 初始化万普支付SDK
	initWanpuPay();
    }

    private void initWanpuPay() {
	// TODO Auto-generated method stub
	PayConnect.getInstance("8025cff144f83cf6252bd92eaf0ce588", "WAPS", this);
	// 需要在onDestroy 方法中进行销毁
    }

    private TextView tvMyMoney;
    private TextView tvMoneyInstruction; // 虚拟币说明

    private void init() {

	readAndSetTopBackgroundColor();

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);

	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	tvTopTitle.setText("兑换金币");
	tvTopTitle.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	tvTopAction.setText("帮助"); // help

	findViewById(R.id.layout_buy_50_love_money).setOnClickListener(this);
	findViewById(R.id.layout_buy_100_love_money).setOnClickListener(this);
	findViewById(R.id.layout_buy_300_love_money).setOnClickListener(this);
	findViewById(R.id.layout_buy_600_love_money).setOnClickListener(this);
	findViewById(R.id.layout_buy_1000_love_money).setOnClickListener(this);
	findViewById(R.id.layout_buy_5000_love_money).setOnClickListener(this);
	findViewById(R.id.layout_buy_10000_love_money).setOnClickListener(this);

	// 当前金币数
	tvMyMoney = (TextView) findViewById(R.id.tv_mymoney);

	tvMyMoney.setText(MyApplication.getInstance().getCurrentLoginedUser().getLoveMoney() + "");

	// 虚拟币说明
	tvMoneyInstruction = (TextView) findViewById(R.id.tv_lovemoney_instruction);
	String instruction = "金币说明: \n   1. 可用于开通VIP会员.送礼物;\n   2. <<速配>>栏排序优先: 金币数越多排序越靠前\n(曝光量越大自然交友成功率更高)";

	tvMoneyInstruction.setText(instruction);

	// (积分墙)获取免费金币
	mTvGetMoneyFree = getViewById(R.id.tv_get_lovemoney_free);
	mTvGetMoneyFree.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		//		net.youmi.android.offers.OffersManager.getInstance(BuyLoveMoneyActivity.this).showOffersWallDialog(BuyLoveMoneyActivity.this, new OffersWallDialogListener() {
		//
		//		    @Override
		//		    public void onDialogClose() {
		//			// TODO Auto-generated method stub
		//
		//		    }
		//		});
		// 设定用户ID

		net.youmi.android.offers.OffersManager.getInstance(BuyLoveMoneyActivity.this).showOffersWall();
	    }
	});
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.layout_buy_50_love_money:
	    buyLoveMoney(1000, 1f);
	    break;
	case R.id.layout_buy_100_love_money:
	    buyLoveMoney(10000, 10f);
	    break;
	case R.id.layout_buy_300_love_money:
	    buyLoveMoney(30000, 30); // 
	    break;
	case R.id.layout_buy_600_love_money:
	    buyLoveMoney(60000, 60);
	    break;
	case R.id.layout_buy_1000_love_money:
	    buyLoveMoney(100000, 100);
	    break;
	case R.id.layout_buy_5000_love_money:
	    buyLoveMoney(500000, 500);
	    break;
	case R.id.layout_buy_10000_love_money:
	    buyLoveMoney(1000000, 1000);
	    break;
	}
    }

    /**
     * 购买恋恋币
     * @param i
     * @param price
     */
    private void buyLoveMoney(int i, float price) {
	// TODO Auto-generated method stub
	// 订单ID
	String orderId = "truelove2" + StringUtils.md5(System.currentTimeMillis() + "");
	// 用户标识
	String userId = MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "";
	// 支付商品名称
	String goodsName = "购买" + i + "恋恋币";
	// 支付金额
	//	float price = Math.round(i / 10.0f);
	// 支付时间
	//	String time = new Date().toLocaleString();
	// 支付描述
	String goodsDesc = "购买" + i + "恋恋币";
	// 应用或游戏商服务器端回调接口（无服务器可不填写）
	String notifyUrl = Constants.URL_LOVE_MONEY_NOTIFY;

	PayConnect.getInstance(this).pay(this, orderId, userId, price, goodsName, goodsDesc, notifyUrl, new MyPayResultListener());
    }

    /**
     * 自定义Listener实现PaySuccessListener，用于监听支付成功
     * 
     * @author Administrator
     * 
     */
    private class MyPayResultListener implements PayResultListener {

	@Override
	public void onPayFinish(Context payViewContext, String orderId, int resultCode, String resultString, int payType, float amount, String goodsName) {
	    // 可根据resultCode自行判断
	    if (resultCode == 0) {
		Toast.makeText(getApplicationContext(), resultString + "：" + amount + "元", Toast.LENGTH_LONG).show();
		// 支付成功时关闭当前支付界面
		PayConnect.getInstance(BuyLoveMoneyActivity.this).closePayView(payViewContext);
		// TODO 在客户端处理支付成功的操作
		// 未指定notifyUrl的情况下，交易成功后，必须发送回执
		//		PayConnect.getInstance(BuyLoveMoneyActivity.this).confirm(orderId, payType);

		OnBuyedLoveMoneyPostParams postParams = new OnBuyedLoveMoneyPostParams();
		// 看下orderId
		System.out.println("orderID:" + orderId);
		postParams.orderId = orderId;
		postParams.payType = payType + "";
		postParams.amount = amount;

		AppServiceBuyImpl.getInstance().queryBuyMoneyResult(postParams, new OnBuyedLoveMoneyResponseListener() {

		    @Override
		    public void onSuccess(String result) {
			// TODO Auto-generated method stub
			ToastFactory.getToast(BuyLoveMoneyActivity.this, "购买结果:" + result).show();
		    }

		    @Override
		    public void onFailure(String errorMsg) {
			// TODO Auto-generated method stub
			ToastFactory.getToast(BuyLoveMoneyActivity.this, "购买失败," + errorMsg).show();
		    }
		});
	    } else {
		Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_LONG).show();
	    }
	}
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	PayConnect.getInstance(this).close(); // 以前版本的finalize() 方法作废
	super.onDestroy();
    }
}
