package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.UserGift;
import me.himi.love.entity.UserNews;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.UserNewsCommentActivity;
import me.himi.love.ui.ZoombleImageActivity;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.EmojiTextView;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @ClassName:UserGiftsAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class UserGiftsAdapter extends BaseListAdapter<UserGift> {

    public UserGiftsAdapter(Context context, List<UserGift> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.user_gift_item, null);
	}

	final UserGift userGift = this.list.get(position);

	ImageView ivUserFace = ViewHolder.get(convertView, R.id.iv_user_face); // 用户头像
	ImageView ivGift = ViewHolder.get(convertView, R.id.iv_gift); // 礼物图像
	TextView tvUserName = ViewHolder.get(convertView, R.id.tv_user_nickname); // 用户昵称
	TextView tvUserWord = ViewHolder.get(convertView, R.id.tv_user_word); // 用户赠语

	ImageLoader.getInstance().displayImage(userGift.getFromUserAvatar(), ivUserFace, ImageLoaderOptions.normalOptions());

	ImageLoader.getInstance().displayImage(userGift.getGiftImageUrl(), ivGift, ImageLoaderOptions.normalOptions());

	tvUserName.setText(userGift.getFromUserNickname());

	tvUserWord.setText(userGift.getWord());

	return convertView;
    }
}
