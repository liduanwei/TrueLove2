package me.himi.love.ui;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.GiftChooseAdapter;
import me.himi.love.adapter.GiftChooseAdapter.GiftOnClickListener;
import me.himi.love.entity.Gift;
import me.himi.love.ui.base.BaseActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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

    @Override
    protected void onCreate(Bundle arg0) {
	// TODO Auto-generated method stub
	super.onCreate(arg0);
	requestWindowFeature(Window.FEATURE_NO_TITLE);

	mMainView = LayoutInflater.from(this).inflate(R.layout.activity_gift_choose, null);

	setContentView(mMainView);

	init();
    }

    List<Gift> gifts = new ArrayList<Gift>() {
	{
	    Gift gift = new Gift();
	    gift.setGiftId(1);
	    gift.setName("吉他");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M03/58/A8/wKhzLFHRS4uNmlxgAAAPRncLB0M396_73-73_8-5.gif");
	    gift.setWithWord("我要用这把吉他给你唱我的故事，你愿听吗？");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(2);
	    gift.setName("戒指");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M04/59/23/wKhzLFHRTmuoUxNkAAAVOP2lJuc304_73-73_8-5.gif");
	    gift.setWithWord("看你的样子很可爱，这枚戒指很衬你~");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(3);
	    gift.setName("套娃");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M03/41/DF/wKhzKlGxxxqvhZBVAAAzsIA56-o969_73-73_8-5.gif");
	    gift.setWithWord("送你一只胖胖的套娃，希望你天天都有好心情！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(4);
	    gift.setName("盆栽");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs02/M00/C2/80/wKhzRlGxxwGfS70-AAAsJYFXs4k658_73-73_8-5.gif");
	    gift.setWithWord("这株盆栽送给你，记得来找我拿花肥！");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(5);
	    gift.setName("钻石表");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs02/M01/C2/96/wKhzRlGxyOjuiTGhAAAhqw6pTc8783_73-73_8-5.gif");
	    gift.setWithWord("送一只粉色的腕表，希望我们的故事能从这一刻开始。");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(6);
	    gift.setName("甲壳虫");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs02/M03/93/C5/wKhzR1GxyQvhLJW1AAAQpHM87oM231_73-73_8-5.gif");
	    gift.setWithWord("我想开着这辆小甲壳虫，带你去乡间兜风！你愿意吗女孩？");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(7);
	    gift.setName("草莓蛋糕");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs04/M03/41/AC/wKhzK1GxxmWj8fNUAAAgRyRz6Vk937_73-73_8-5.gif");
	    gift.setWithWord("香甜蛋糕送给你，让它带去我对你所有的祝福。");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(8);
	    gift.setName("泰迪熊");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs03/M01/7D/FA/wKhzGVHRTG72GTHZAAAU0rhMmJ8354_73-73_8-5.gif");
	    gift.setWithWord("我是一只坐在窗边的小熊，虽然不言不语，但是每天等你... ");
	    add(gift);

	    gift = new Gift();
	    gift.setGiftId(9);
	    gift.setName("冰淇凌");
	    gift.setImageUrl("http://image.ganjistatic1.com/gjfs03/M01/DA/A3/wKhzGVGxxrzQEWzNAAAfpAOKUWI041_73-73_8-5.gif");
	    gift.setWithWord("大口大口的吃冰淇淋，心中就能开出快活的花来！");
	    add(gift);
	}
    };

    private void init() {
	// TODO Auto-generated method stub
	com.huewu.pla.lib.MultiColumnPullToRefreshListView gridView = (com.huewu.pla.lib.MultiColumnPullToRefreshListView) findViewById(R.id.multiColumPullToRefreshListView_gift);

	final TextView tvTargetTips = (TextView) findViewById(R.id.tv_select_gift_tips);
	final EditText etWord = (EditText) findViewById(R.id.et_send_word);
	// 加载可选礼物

	giftChooseAdapter = new GiftChooseAdapter(this, gifts);
	gridView.setAdapter(giftChooseAdapter);
	giftChooseAdapter.setGiftOnClickListener(new GiftOnClickListener() {

	    @Override
	    public void onClick(Gift gift) {
		// TODO Auto-generated method stub
		etWord.setText(gift.getWithWord());
	    }
	});
    }

}
