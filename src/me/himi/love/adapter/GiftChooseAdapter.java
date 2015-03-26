package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.Gift;
import me.himi.love.entity.RecommendUser;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.util.ImageLoaderOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserRecommendAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class GiftChooseAdapter extends BaseListAdapter<Gift> {

    private GiftOnClickListener giftOnClickListener;

    public GiftChooseAdapter(Context context, List<Gift> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.gift_item, null);
	}

	final Gift gift = this.list.get(position);
	ImageView ivGift = ViewHolder.get(convertView, R.id.iv_gift);
	// 加载礼物图片
	ImageLoader.getInstance().displayImage(gift.getImageUrl(), ivGift, ImageLoaderOptions.normalOptions());
	// 
	ivGift.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		if (null != giftOnClickListener) {
		    giftOnClickListener.onClick(gift);
		}
	    }
	});

	// 礼物名称
	TextView tvName = ViewHolder.get(convertView, R.id.tv_gift_name);
	tvName.setText(gift.getName());

	return convertView;
    }

    public void setGiftOnClickListener(GiftOnClickListener giftOnClickListener) {
	this.giftOnClickListener = giftOnClickListener;
    }

    public static interface GiftOnClickListener {
	void onClick(Gift gift);
    }
}
