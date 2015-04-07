package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.StrangeNews;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.Share;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName:StrangeNewsAndIntriguingStoryAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class StrangeNewsAndIntriguingStoryAdapter extends BaseListAdapter<StrangeNews> {

    public StrangeNewsAndIntriguingStoryAdapter(Context context, List<StrangeNews> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.strangenews_item, null);
	}

	final StrangeNews article = this.list.get(position);

	int backColor = Color.rgb(255, 255, 255);
	if (position % 2 == 0) {
	    backColor = Color.rgb(250, 250, 250);
	} else {
	    backColor = Color.rgb(255, 255, 255);
	}

	convertView.setBackgroundColor(backColor);

	TextView tvTitle = ViewHolder.get(convertView, R.id.tv_title);

	// 文章配图
	ImageView ivBackground = ViewHolder.get(convertView, R.id.iv_strangenews_image);

	TextView tvSummary = ViewHolder.get(convertView, R.id.tv_summary); // 摘要

	TextView tvTime = ViewHolder.get(convertView, R.id.tv_time); // 时间
	TextView tvTag = ViewHolder.get(convertView, R.id.tv_tag); // 分类

	final ImageView ivMore = ViewHolder.get(convertView, R.id.iv_strangenews_more); // 内容分享

	tvTitle.setText(article.getTitle());
	tvSummary.setText(article.getSummary());
	tvTag.setText(article.getTag());

	tvTime.setText(ActivityUtil.convertTimeToString(article.getCreateTime() * 1000L));
	// 分享
	ivMore.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Share.share(mContext, article);
	    }
	});

	if (article.getContentImageUrl().getSmallImageUrl() == null || article.getContentImageUrl().getSmallImageUrl().trim().length() == 0) {
	    ivBackground.setVisibility(View.GONE);
	} else {
	    ivBackground.setVisibility(View.VISIBLE);
	    String imageUrl = article.getContentImageUrl().getSmallImageUrl();
	    // 文章配图
	    ImageLoader.getInstance().displayImage(imageUrl, ivBackground, ImageLoaderOptions.normalOptions(), new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
		    // TODO Auto-generated method stub

		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		    // TODO Auto-generated method stub

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		    // TODO Auto-generated method stub
		    notifyDataSetChanged();
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
		    // TODO Auto-generated method stub

		}
	    });

	    //	    // 点击图片进入大图浏览
	    //	    ivBackground.setOnClickListener(new View.OnClickListener() {
	    //
	    //		@Override
	    //		public void onClick(View v) {
	    //		    // TODO Auto-generated method stub
	    //		    Intent intent = new Intent(mContext, ZoombleImageActivity.class);
	    //		    intent.putExtra("image_url", article.getContentImageUrl().getBigImageUrl());
	    //		    intent.putExtra("default_small_image_url", article.getContentImageUrl().getSmallImageUrl());
	    //		    mContext.startActivity(intent);
	    //		}
	    //	    });
	}

	// 分享
	ivMore.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		share(article);
	    }
	});

	return convertView;
    }

    private void share(StrangeNews article) {
	Share.share(mContext, article);
    }
}
