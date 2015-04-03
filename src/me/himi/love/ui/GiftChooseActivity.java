package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.GiveGiftPostParams;
import me.himi.love.IAppServiceExtend.LoadGiftPostParams;
import me.himi.love.IAppServiceExtend.OnGiveGiftResponseListener;
import me.himi.love.IAppServiceExtend.OnLoadGiftResponseListener;
import me.himi.love.R;
import me.himi.love.adapter.GiftChooseAdapter;
import me.himi.love.adapter.GiftChooseAdapter.GiftOnClickListener;
import me.himi.love.entity.Gift;
import me.himi.love.entity.UserGift;
import me.himi.love.ui.base.BaseActivity;
import me.himi.love.util.CacheUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;

/**
 * @ClassName:GiftChooseActivity
 * @author sparklee liduanwei_911@163.com
 * @date Nov 14, 2014 3:08:53 PM
 */
public class GiftChooseActivity extends BaseActivity {

    private View mMainView;

    GiftChooseAdapter giftChooseAdapter;

    String userId, userNickname;

    public static final int SELECTED = 1;

    @Override
    protected void onCreate(Bundle arg0) {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_gift_choose, null);

	setContentView(mMainView);

	WindowManager m = getWindowManager();
	Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
	android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
	p.height = (int) (d.getHeight() * 0.75); // 高度设置为屏幕的0.8
	p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.7
	getWindow().setAttributes(p);

	init();
    }

    List<Gift> gifts = new ArrayList<Gift>();

    Gift mSelectedGift;

    com.huewu.pla.lib.MultiColumnPullToRefreshListView mGridView;

    private void init() {
	final String userId = getIntent().getStringExtra("user_id");
	final String nickname = getIntent().getStringExtra("nickname");
	final int gender = getIntent().getIntExtra("gender", 0); // 限制性别的礼物

	// TODO Auto-generated method stub
	mGridView = (com.huewu.pla.lib.MultiColumnPullToRefreshListView) findViewById(R.id.multiColumPullToRefreshListView_gift);

	//
	mGridView.setOnRefreshListener(new OnRefreshListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub

		loadGifts(gender);

	    }
	});

	final TextView tvTargetTips = (TextView) findViewById(R.id.tv_select_gift_tips);
	final EditText etWord = (EditText) findViewById(R.id.et_send_word);

	// 默认按钮不可用
	findViewById(R.id.tv_give_gift_publish).setEnabled(false);
	// 赠送
	findViewById(R.id.tv_give_gift_publish).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mSelectedGift != null) {
		    //		    Intent data = new Intent();
		    //		    data.putExtra("user_id", userId);
		    //		    data.putExtra("gift_id", mSelectedGift.getGiftId() + "");
		    //		    data.putExtra("word", etWord.getText().toString());
		    //		    setResult(RESULT_OK, data);
		    publishGift(userId + "", mSelectedGift.getGiftId() + "", etWord.getText().toString());
		}
	    }
	});

	// 取消
	findViewById(R.id.tv_give_gift_cancle).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	    }
	});

	// 充值/获取金币
	findViewById(R.id.tv_buy_money).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
//		startActivity(new Intent(GiftChooseActivity.this, BuyLoveMoneyActivity.class));
		net.youmi.android.offers.OffersManager.getInstance(GiftChooseActivity.this).showOffersWall();
	    }
	});

	tvTargetTips.setText("对 " + nickname + " 说:");

	giftChooseAdapter = new GiftChooseAdapter(this, gifts);
	mGridView.setAdapter(giftChooseAdapter);
	giftChooseAdapter.setGiftOnClickListener(new GiftOnClickListener() {

	    @Override
	    public void onClick(Gift gift) {
		// TODO Auto-generated method stub
		etWord.setText(gift.getWithWord());
		tvTargetTips.setText("送" + gift.getName() + ",对 " + nickname + " 说:");
		mSelectedGift = gift;

		findViewById(R.id.tv_give_gift_publish).setEnabled(true);
	    }
	});

	// 加载可选礼物
	// todo
	loadFromCache(gender); // 优先从缓存中加载
    }

    private boolean isRefreshing;

    private void loadGifts(final int gender) {

	if (isRefreshing)
	    return;
	isRefreshing = true;
	// TODO Auto-generated method stub
	LoadGiftPostParams postParams = new LoadGiftPostParams();
	postParams.page = 1;
	postParams.pageSize = 100;
	postParams.targetUserGender = gender;

	AppServiceExtendImpl.getInstance().loadGift(postParams, new OnLoadGiftResponseListener() {

	    @Override
	    public void onSuccess(List<Gift> gifts) {
		// TODO Auto-generated method stub
		giftChooseAdapter.setList(gifts);
		giftChooseAdapter.notifyDataSetChanged();

		mGridView.onRefreshComplete();

		isRefreshing = false;

		// 缓存到本地
		cacheToLocal(giftChooseAdapter.getList(), gender);
	    }

	    /**
	     * 
	     * @param users
	     */
	    private void cacheToLocal(List<Gift> gifts, int gender) {
		// TODO Auto-generated method stub
		String cacheGiftsPath = null;
		if (1 == gender) {
		    cacheGiftsPath = cacheGiftsMalePath;
		} else {
		    cacheGiftsPath = cacheGiftsFemalePath;
		}
		CacheUtils.cacheToLocal(gifts, cacheGiftsPath);
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);

		mGridView.onRefreshComplete();

		isRefreshing = false;

	    }
	});
    }

    // 使用本地缓存
    private final static String cacheGiftsMalePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/gifts_male";
    private final static String cacheGiftsFemalePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove2/gifts_female";

    private void loadFromCache(int gender) {
	// TODO Auto-generated method stub
	String cacheGiftsPath = null;
	if (gender == 1) {
	    cacheGiftsPath = cacheGiftsMalePath; // 
	} else {
	    cacheGiftsPath = cacheGiftsFemalePath;
	}

	List<Gift> gifts = CacheUtils.loadFromCache(cacheGiftsPath);
	if (gifts != null) {
	    giftChooseAdapter.setList(gifts);
	} else {
	    loadGifts(gender);
	}
    }

    /**
     * 赠送礼物
     * @param targetUserId
     * @param targetGiftId
     * @param word
     */
    private void publishGift(String targetUserId, String targetGiftId, String word) {
	final ProgressDialog dialog = new ProgressDialog(this);
	dialog.setMessage("赠送中...");
	dialog.show();

	GiveGiftPostParams postParams = new GiveGiftPostParams();
	postParams.giftId = targetGiftId;
	postParams.toUserId = targetUserId;
	postParams.word = word;

	AppServiceExtendImpl.getInstance().giveGift(postParams, new OnGiveGiftResponseListener() {

	    @Override
	    public void onSuccess(UserGift userGift) {
		// TODO Auto-generated method stub
		showToast("已赠送!");
		dialog.dismiss();
		finish();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
		dialog.dismiss();

	    }
	});
    }
}
