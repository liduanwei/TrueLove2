package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.GiftChooseAdapter;
import me.himi.love.adapter.GiftChooseAdapter.GiftOnClickListener;
import me.himi.love.entity.Gift;
import me.himi.love.ui.base.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @ClassName:CheckUpdateActivity
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

    List<Gift> gifts = new ArrayList<Gift>() {
	{
	    Gift gift = new Gift();
	    gift.setGiftId("1");
	    gift.setName("吉他");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M03/58/A8/wKhzLFHRS4uNmlxgAAAPRncLB0M396_73-73_8-5.gif");
	    gift.setWithWord("我要用这把吉他给你唱我的故事，你愿听吗？");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("2");
	    gift.setName("戒指");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M04/59/23/wKhzLFHRTmuoUxNkAAAVOP2lJuc304_73-73_8-5.gif");
	    gift.setWithWord("看你的样子很可爱，这枚戒指很衬你~");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("3");
	    gift.setName("套娃");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M03/41/DF/wKhzKlGxxxqvhZBVAAAzsIA56-o969_73-73_8-5.gif");
	    gift.setWithWord("送你一只胖胖的套娃，希望你天天都有好心情！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("4");
	    gift.setName("盆栽");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs02/M00/C2/80/wKhzRlGxxwGfS70-AAAsJYFXs4k658_73-73_8-5.gif");
	    gift.setWithWord("这株盆栽送给你，记得来找我拿花肥！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("5");
	    gift.setName("钻石表");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs02/M01/C2/96/wKhzRlGxyOjuiTGhAAAhqw6pTc8783_73-73_8-5.gif");
	    gift.setWithWord("送一只粉色的腕表，希望我们的故事能从这一刻开始。");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("6");
	    gift.setName("甲壳虫");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs02/M03/93/C5/wKhzR1GxyQvhLJW1AAAQpHM87oM231_73-73_8-5.gif");
	    gift.setWithWord("我想开着这辆小甲壳虫，带你去乡间兜风！你愿意吗女孩？");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("7");
	    gift.setName("草莓蛋糕");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M03/41/AC/wKhzK1GxxmWj8fNUAAAgRyRz6Vk937_73-73_8-5.gif");
	    gift.setWithWord("香甜蛋糕送给你，让它带去我对你所有的祝福。");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("8");
	    gift.setName("泰迪熊");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs03/M01/7D/FA/wKhzGVHRTG72GTHZAAAU0rhMmJ8354_73-73_8-5.gif");
	    gift.setWithWord("我是一只坐在窗边的小熊，虽然不言不语，但是每天等你... ");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("9");
	    gift.setName("冰淇凌");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs03/M01/DA/A3/wKhzGVGxxrzQEWzNAAAfpAOKUWI041_73-73_8-5.gif");
	    gift.setWithWord("大口大口的吃冰淇淋，心中就能开出快活的花来！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("10");
	    gift.setName("包包");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs03/M01/7E/91/wKhzGVHRTsqq0MDqAAAVMf9az2A373_73-73_8-5.gif");
	    gift.setWithWord("送你一只小手包，愿你每天光彩照人！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("11");
	    gift.setName("香水");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M01/59/3C/wKhzLFHRTvOPzBmWAAAUHrE,PDA487_73-73_8-5.gif");
	    gift.setWithWord("你这样的女孩子，看一眼就让人觉得花香浓郁！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId("12");
	    gift.setName("高跟鞋");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M00/59/A5/wKhzK1HRThjEwdhnAAAXzaPqZG0490_73-73_8-5.gif");
	    gift.setWithWord("人说每个女孩都爱小红鞋，这双鞋子你穿上一定很美。");
	    add(gift);
	}
    };

    Gift mSelectedGift;

    private void init() {
	final String userId = getIntent().getStringExtra("user_id");
	final String nickname = getIntent().getStringExtra("nickname");

	// TODO Auto-generated method stub
	com.huewu.pla.lib.MultiColumnPullToRefreshListView gridView = (com.huewu.pla.lib.MultiColumnPullToRefreshListView) findViewById(R.id.multiColumPullToRefreshListView_gift);

	final TextView tvTargetTips = (TextView) findViewById(R.id.tv_select_gift_tips);
	final EditText etWord = (EditText) findViewById(R.id.et_send_word);

	// 赠送
	findViewById(R.id.tv_give_gift_publish).setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent data = new Intent();
		data.putExtra("user_id", userId);
		data.putExtra("gift_id", mSelectedGift.getGiftId() + "");
		data.putExtra("word", etWord.getText().toString());
		setResult(RESULT_OK, data);
		finish();
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

	// 加载可选礼物
	// todo

	tvTargetTips.setText("对 " + nickname + " 说:");

	giftChooseAdapter = new GiftChooseAdapter(this, gifts);
	gridView.setAdapter(giftChooseAdapter);
	giftChooseAdapter.setGiftOnClickListener(new GiftOnClickListener() {

	    @Override
	    public void onClick(Gift gift) {
		// TODO Auto-generated method stub
		etWord.setText(gift.getWithWord());
		mSelectedGift = gift;
	    }
	});

    }
}
