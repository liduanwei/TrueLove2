package me.himi.love.ui;

import me.himi.love.AppServiceBuyImpl;
import me.himi.love.IAppServiceBuy.OnBuyedVipPostParams;
import me.himi.love.IAppServiceBuy.OnBuyedVipResponseListener;
import me.himi.love.IAppServiceExtend.HomeInfo;
import me.himi.love.IAppServiceExtend.LoadHomeInfoPostParams;
import me.himi.love.IAppServiceExtend.OnLoadHomeInfoResponseListener;
import me.himi.love.AppServiceExtendImpl;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.LoginedUser;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/**
 * @ClassName:UserInfoActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 5, 2014 3:50:36 PM
 */
public class BuyVIPActivity extends BaseActivity implements OnClickListener {

    DetailInfoUser targetUser;

    @Override
    protected void onCreate(Bundle arg0) {
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_buy_vip);

	// 
	init();
    }

    private TextView tvVipExpireTime; // 会员到期时间
    private TextView mTvGetMoneyFree; // 免费获取金币
    private TextView mTvMyMoney; // 当前金币数
    private TextView tvMoneyInstruction; // 金币简要说明

    private void init() {

	TextView tvTopTitle = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_title);
	TextView tvTopAction = (TextView) findViewById(R.id.layout_nav_top).findViewById(R.id.tv_top_action);

	tvTopTitle.setText("开通VIP会员");

	tvTopAction.setText("");

	findViewById(R.id.layout_vip_1_month).setOnClickListener(this);
	findViewById(R.id.layout_vip_3_month).setOnClickListener(this);
	findViewById(R.id.layout_vip_6_month).setOnClickListener(this);
	findViewById(R.id.layout_vip_12_month).setOnClickListener(this);

	// 当前金币
	mTvMyMoney = getViewById(R.id.tv_mymoney);
	mTvMyMoney.setText(MyApplication.getInstance().getCurrentLoginedUser().getLoveMoney() + "");

	// 虚拟币说明
	tvMoneyInstruction = (TextView) findViewById(R.id.tv_lovemoney_instruction);
	String instruction = "金币说明: \n   1. 可用于开通/续时VIP会员.送礼物.\n   2. <<速配>>栏排序优先: 金币数越多排序越靠前\n(曝光量越大自然交友成功率更高)";

	tvMoneyInstruction.setText(instruction);

	// 获取金币
	mTvGetMoneyFree = getViewById(R.id.tv_get_lovemoney_free);
	mTvGetMoneyFree.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub

		net.youmi.android.offers.OffersManager.getInstance(BuyVIPActivity.this).showOffersWall();
	    }
	});

	tvVipExpireTime = (TextView) findViewById(R.id.tv_vip_expire_time);

	//会员到期时间

	if (MyApplication.getInstance().getCurrentLoginedUser().getIsVip() == 1) {
	    String dateStr = ActivityUtil.getDateStr(MyApplication.getInstance().getCurrentLoginedUser().getVipExpireTime(), "yyyy/MM/dd HH:mm:ss");
	    tvVipExpireTime.setText(dateStr);
	} else { // 非会员
	    tvVipExpireTime.setText("您目前还没有开通VIP会员");
	}

    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.layout_vip_1_month:
	    showConfirmDialog(0); // 1个月
	    break;
	case R.id.layout_vip_3_month:
	    showConfirmDialog(1); // 3个月
	    break;
	case R.id.layout_vip_6_month:
	    showConfirmDialog(2);//6个月
	    break;
	case R.id.layout_vip_12_month:
	    showConfirmDialog(3); // 12个月
	    break;

	}
    }

    private void showConfirmDialog(final int monthType) {
	// TODO Auto-generated method stub
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	int[] month = { 1, 3, 6, 12 };
	int[] money = { 11000, 31000, 61000, 110000 };

	builder.setMessage("开通" + month[monthType] + "个月会员,需花费" + money[monthType] + "金币");
	builder.setPositiveButton("开通", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		buyVIP(monthType);
	    }
	});
	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	    }
	});
	AlertDialog confDialog = builder.create();
	confDialog.show();
    }

    ProgressDialog progress;

    private void buyVIP(int monthType) {

	if (progress == null) {
	    progress = new ProgressDialog(this);
	}
	progress.setMessage("提交订单中...");
	progress.show();

	OnBuyedVipPostParams postParams = new OnBuyedVipPostParams();

	postParams.vipType = monthType; // 根据支付金额判断购买的会员时长类型

	AppServiceBuyImpl.getInstance().buyedVip(postParams, new OnBuyedVipResponseListener() {

	    @Override
	    public void onSuccess(String result) {
		// TODO Auto-generated method stub
		progress.dismiss();
		try {
		    JSONObject jsonObj = new JSONObject(result);
		    int status = jsonObj.getInt("status");
		    if (-1 == status) {
			// 转到金币中心
			String msg = jsonObj.getString("msg");

			// 您当前的金币不足
			ToastFactory.getToast(BuyVIPActivity.this, msg + "\n正在转到金币获取页").show();
			//
			//			startActivity(new Intent(BuyVIPActivity.this, BuyLoveMoneyActivity.class));

			net.youmi.android.offers.OffersManager.getInstance(BuyVIPActivity.this).showOffersWall();

		    } else if (1 == status) {
			String msg = jsonObj.getString("msg");
			ToastFactory.getToast(BuyVIPActivity.this, msg).show();
		    }
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		ToastFactory.getToast(BuyVIPActivity.this, "购买失败," + errorMsg).show();
		progress.dismiss();
	    }
	});
    }

    /**
     * 购买成功后刷新显示
     */
    @Override
    protected void onResume() {

	updateHomeInfo();

	// TODO Auto-generated method stub
	//	if (MyApplication.getInstance().getCurrentLoginedUser().getIsVip() == 1) {
	//	    String dateStr = ActivityUtil.getDateStr(MyApplication.getInstance().getCurrentLoginedUser().getVipExpireTime(), "yyyy/MM/dd HH:mm:ss");
	//	    tvVipExpireTime.setText("VIP会员到期时间:" + dateStr);
	//	} else { // 非会员
	//	    tvVipExpireTime.setText("您目前还没有购买会员");
	//	}
	super.onResume();
    }

    /**
     * 重新获取首页信息
     */
    private void updateHomeInfo() {
	//显示状态改变时 尝试更新为当前内存中保存的资料
	final LoginedUser user = MyApplication.getInstance().getCurrentLoginedUser();

	LoadHomeInfoPostParams postParams = new LoadHomeInfoPostParams();

	AppServiceExtendImpl.getInstance().loadHomeInfo(postParams, new OnLoadHomeInfoResponseListener() {

	    @Override
	    public void onSuccess(HomeInfo homeInfo) {
		// TODO Auto-generated method stub

		//		tvFansCount.setText(homeInfo.fans + "");
		//		tvFriendsCount.setText(homeInfo.friends + "");
		//		tvFollowsCount.setText(homeInfo.follows + "");

		// 更新 loginedUser中的数据
		user.setFansCount(homeInfo.fans);
		user.setFollowsCount(homeInfo.follows);
		user.setFriendsCount(homeInfo.friends);

		user.setLoveMoney(homeInfo.loveMoney);
		user.setNickname(homeInfo.nickname);

		user.setIsVip(homeInfo.isVip ? 1 : 0);

		user.setVipExpireTime(homeInfo.vipExpireTime);

		//更新view

		mTvMyMoney.setText(user.getLoveMoney() + "");
		if (user.getIsVip() == 1) {
		    String dateStr = ActivityUtil.getDateStr(user.getVipExpireTime(), "yyyy/MM/dd HH:mm:ss");
		    tvVipExpireTime.setText(dateStr);
		} else { // 非会员
		    tvVipExpireTime.setText("您目前还没有开通VIP会员");
		}

		//		tvMyId.setText("ID:" + user.getUserId());
		//		tvMyNickname.setEmojiText(user.getNickname());
		//		tvLoveMoney.setText(user.getLoveMoney() + "币");
		//		if (homeInfo.signed) {
		//		    tvSignin.setText("已签到");
		//		    tvSignin.setEnabled(false);
		//		}
		//
		//		if (user.getIsVip() == 1) {
		//		    tvMyVip.setBackgroundResource(R.drawable.vip_sign);
		//		} else {
		//		    tvMyVip.setBackgroundResource(R.drawable.vip_sign_not);
		//		}
		//		String url = user.getFaceUrl();// "http://t11.baidu.com/it/u=751031812,971358817&fm=56" : "http://img1.imgtn.bdimg.com/it/u=1983880049,3609856836&fm=116&gp=0.jpg";
		//		refreshMyFace(url);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub

	    }
	});
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
    }

}
