package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.BSImageUrl;
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
 * @ClassName:UserRecommendAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class UserNewsAdapter extends BaseListAdapter<UserNews> {

    public UserNewsAdapter(Context context, List<UserNews> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.user_news_item, null);
	}

	final UserNews news = this.list.get(position);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);
	final EmojiTextView tvNickname = ViewHolder.get(convertView, R.id.tv_news_user_nickname);
	final TextView tvAge = ViewHolder.get(convertView, R.id.tv_news_user_age);

	final View mainContentView = ViewHolder.get(convertView, R.id.layout_news_main_content);

	final TextView tvContent = ViewHolder.get(convertView, R.id.tv_news_content);
	final TextView tvPosttime = ViewHolder.get(convertView, R.id.tv_news_post_time);
	final TextView tvComments = ViewHolder.get(convertView, R.id.tv_news_comments);
	final TextView tvPostLocation = ViewHolder.get(convertView, R.id.tv_news_post_location);

	// 是否禁止了评论
	final TextView tvAllowComment = ViewHolder.get(convertView, R.id.tv_news_comments_allow);
	// 是否隐藏
	final TextView tvIsPrivate = ViewHolder.get(convertView, R.id.tv_news_private);

	// 测试图片内容
	final ImageView ivImageRow1Content01 = ViewHolder.get(convertView, R.id.iv_news_row1_image1_content);
	final ImageView ivImageRow1Content02 = ViewHolder.get(convertView, R.id.iv_news_row1_image2_content);
	final ImageView ivImageRow1Content03 = ViewHolder.get(convertView, R.id.iv_news_row1_image3_content);

	final ImageView ivImageRow2Content01 = ViewHolder.get(convertView, R.id.iv_news_row2_image1_content);
	final ImageView ivImageRow2Content02 = ViewHolder.get(convertView, R.id.iv_news_row2_image2_content);
	final ImageView ivImageRow2Content03 = ViewHolder.get(convertView, R.id.iv_news_row2_image3_content);

	int backRes = 0;
	Drawable genderDrawable = null;
	if (news.getGender() == 1) {
	    backRes = R.drawable.shape_gender_age_male;
	    genderDrawable = mContext.getResources().getDrawable(R.drawable.ic_user_male2);
	} else {
	    backRes = R.drawable.shape_gender_age_female;
	    genderDrawable = mContext.getResources().getDrawable(R.drawable.ic_user_famale2);
	}
	tvAge.setText(news.getAge() + "");
	tvAge.setBackgroundResource(backRes);
	genderDrawable.setBounds(0, 0, genderDrawable.getMinimumWidth(), genderDrawable.getMinimumHeight());
	tvAge.setCompoundDrawables(genderDrawable, null, null, null);

	tvNickname.setEmojiText(news.getNickname());
	tvContent.setText(news.getContent());
	tvPosttime.setText(news.getPostTime());
	tvComments.setText(news.getCommentsCount() + "");

	OnClickListener commentOnClickListener = new OnClickListener() { // 点击查看评论

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext, UserNewsCommentActivity.class);
		Bundle extras = new Bundle();
		extras.putSerializable("news", news);
		intent.putExtras(extras);
		mContext.startActivity(intent);
	    }
	};

	tvComments.setOnClickListener(commentOnClickListener);
	mainContentView.setOnClickListener(commentOnClickListener);

	// 隐藏
	tvIsPrivate.setSelected(news.isPrivate());
	// 禁止评论
	tvAllowComment.setVisibility(!news.isAllowComment() ? View.VISIBLE : View.GONE);

	// 需要计算经纬度对应的位置与当前用户的位置的距离
	tvPostLocation.setText(/*news.getPostLongitude() + "," + news.getPostLatitude() + "," +*/news.getAddress());

	// display 会有刷新闪烁问题

	int size = news.getImageUrls().size();

	ImageView[] ivs = { ivImageRow1Content01, ivImageRow1Content02, ivImageRow1Content03, ivImageRow2Content01, ivImageRow2Content02, ivImageRow2Content03 };

	int t = Math.min(ivs.length, size);//size >= 3 ? 3 : size;

	if (size != 0) {
	    int i = 0;
	    for (; i < t; ++i) {
		// 图片错位问题, 每次都需要设置 可视与不可视
		ivs[i].setVisibility(View.VISIBLE);
		// 默认使用小图
		final BSImageUrl imgUrl = news.getImageUrls().get(i);
		ImageLoader.getInstance().displayImage(imgUrl.getSmallImageUrl(), ivs[i], ImageLoaderOptions.normalOptions());
		ivs[i].setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
			// TODO Auto-generated method stub
			//  链接查看大图
			Intent intent = new Intent(mContext, ZoombleImageActivity.class);
			intent.putExtra("image_url", imgUrl.getBigImageUrl());
			intent.putExtra("default_small_image_url", imgUrl.getSmallImageUrl());
			mContext.startActivity(intent);

		    }
		});
	    }
	    for (; i < ivs.length; ++i) {
		ivs[i].setVisibility(View.GONE);
	    }
	} else {
	    for (int i = 0; i < ivs.length; ++i) {
		ivs[i].setVisibility(View.GONE);
	    }
	}
	ImageLoader.getInstance().displayImage(news.getFaceUrl(), ivFace, ImageLoaderOptions.rounderOptions());

	ivFace.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(mContext, UserInfoTextActivity.class);
		System.out.println("newsUserID" + news.getUserId());
		intent.putExtra("user_id", news.getUserId());
		intent.putExtra("user_nickname", news.getNickname());
		intent.putExtra("user_face_url", news.getFaceUrl());
		mContext.startActivity(intent);
	    }
	});

	return convertView;
    }
}
