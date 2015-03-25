package me.himi.love.adapter;

import java.util.List;

import me.himi.love.AppServiceExtendImpl;
import me.himi.love.IAppServiceExtend.OnVoteArticleLoveResponseListener;
import me.himi.love.IAppServiceExtend.VoteArticleLovePostParams;
import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.Article;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.ui.ZoombleImageActivity;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.util.Share;
import me.himi.love.view.EmojiTextView;
import android.content.Context;
import android.content.Intent;
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
 * @ClassName:UserRecommendAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class ArticleAdapter extends BaseListAdapter<Article> {

    public ArticleAdapter(Context context, List<Article> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.article_item, null);
	}

	final Article article = this.list.get(position);

	int backColor = Color.rgb(255, 255, 255);
	if (position % 2 == 0) {
	    backColor = Color.rgb(250, 250, 250);
	} else {
	    backColor = Color.rgb(255, 255, 255);
	}

	convertView.setBackgroundColor(backColor);

	// 文章配图
	ImageView ivBackground = ViewHolder.get(convertView, R.id.iv_article_image);

	// 作者头像
	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);
	ivFace.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mContext, UserInfoTextActivity.class);
		intent.putExtra("user_id", article.getUserId());
		intent.putExtra("is_vip", article.getVip() == 1);
		intent.putExtra("user_nickname", article.getNickname());
		intent.putExtra("user_face_url", article.getFaceUrl().getSmallImageUrl());
		mContext.startActivity(intent);
	    }
	});
	// 作者昵称
	EmojiTextView tvNickname = ViewHolder.get(convertView, R.id.tv_user_nickname);
	tvNickname.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mContext, UserInfoTextActivity.class);
		intent.putExtra("user_id", article.getUserId());
		intent.putExtra("is_vip", article.getVip() == 1);
		intent.putExtra("user_nickname", article.getNickname());
		intent.putExtra("user_face_url", article.getFaceUrl().getSmallImageUrl());
		mContext.startActivity(intent);
	    }
	});

	TextView tvContent = ViewHolder.get(convertView, R.id.tv_secret_content); // 内容

	TextView tvTime = ViewHolder.get(convertView, R.id.tv_time); // 时间

	final ImageView ivLoves = ViewHolder.get(convertView, R.id.iv_likes); //赞图标
	final TextView tvLoves = ViewHolder.get(convertView, R.id.tv_likes); // 赞次数
	TextView tvComments = ViewHolder.get(convertView, R.id.tv_comments); // 评论数

	final ImageView ivMore = ViewHolder.get(convertView, R.id.iv_article_more); // 内容分享

	ImageView ivVip = ViewHolder.get(convertView, R.id.iv_vip);

	tvLoves.setText(article.getLove() + "");
	tvComments.setText(article.getComments() + "");

	tvTime.setText(ActivityUtil.convertTimeToString(article.getCreateTime() * 1000L));

	// vip
	ivVip.setImageResource(article.getVip() != 0 ? R.drawable.vip : R.drawable.vipnot);
	tvNickname.setTextColor(mContext.getResources().getColor(article.getVip() != 0 ? R.color.c_vip : R.color.c_not_vip));
	//	ivVip.setVisibility(user.getVip() != 0 ? View.VISIBLE : View.GONE);

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
	    ivBackground.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent(mContext, ZoombleImageActivity.class);
		    intent.putExtra("image_url", article.getContentImageUrl().getBigImageUrl());
		    intent.putExtra("default_small_image_url", article.getContentImageUrl().getSmallImageUrl());
		    mContext.startActivity(intent);
		}
	    });
	}

	// displayImage 会有刷新闪烁问题
	ImageLoader.getInstance().displayImage(article.getFaceUrl().getSmallImageUrl(), ivFace, ImageLoaderOptions.circleOptions(), new ImageLoadingListener() {

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

	tvNickname.setEmojiText(article.getNickname());

	tvContent.setText(article.getContent()); // 秘密的内容

	View love = ViewHolder.get(convertView, R.id.layout_likes);
	love.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		VoteArticleLovePostParams postParams = new VoteArticleLovePostParams();
		postParams.articleId = article.getId();
		AppServiceExtendImpl.getInstance().loveArticle(postParams, new OnVoteArticleLoveResponseListener() {

		    @Override
		    public void onSuccess() {
			// TODO Auto-generated method stub
			tvLoves.setText(Integer.parseInt(tvLoves.getText().toString().trim()) + 1 + "");
			ivLoves.setBackgroundResource(R.drawable.player_collection_pressed);
		    }

		    @Override
		    public void onFailure(String errorMsg) {
			// TODO Auto-generated method stub
			showToast(errorMsg);
		    }
		});
	    }
	});

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

    private void share(Article article) {
	Share.share(mContext, article);
    }

}
