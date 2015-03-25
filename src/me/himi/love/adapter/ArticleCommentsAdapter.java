package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.ArticleComment;
import me.himi.love.ui.UserInfoTextActivity;
import me.himi.love.util.ImageLoaderOptions;
import me.himi.love.view.EmojiTextView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 文章评论
 * @ClassName:UserNewsCommentAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class ArticleCommentsAdapter extends BaseListAdapter<ArticleComment> {

    public ArticleCommentsAdapter(Context context, List<ArticleComment> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.article_comment_item, null);
	}

	final ArticleComment comment = this.list.get(position);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);
	final EmojiTextView tvNickname = ViewHolder.get(convertView, R.id.tv_news_comment_user_nickname);
	final TextView tvReplyUserContent = ViewHolder.get(convertView, R.id.tv_news_comment_reply_user_content);
	final me.himi.love.im.ui.customview.EmoticonsTextView tvContent = ViewHolder.get(convertView, R.id.tv_news_comment_content);
	final TextView tvPosttime = ViewHolder.get(convertView, R.id.tv_news_comment_post_time);

	tvNickname.setEmojiText(comment.getNickname());

	if (comment.getReplyUser() != null) {
	    tvReplyUserContent.setVisibility(View.VISIBLE);
	    tvReplyUserContent.setText(comment.getReplyUser() + "\n   " + comment.getReplyUserCommentContent() + ":");
	} else {
	    tvReplyUserContent.setVisibility(View.GONE);
	    tvReplyUserContent.setText("");
	}

	tvContent.setText(comment.getContent());//  + "\\ue056"

	tvPosttime.setText(comment.getPostTime());

	ImageLoader.getInstance().displayImage(comment.getFaceUrl(), ivFace, ImageLoaderOptions.rounderOptions());
	ivFace.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(mContext, UserInfoTextActivity.class);
		intent.putExtra("user_id", comment.getUserId());
		intent.putExtra("user_nickname", comment.getNickname());
		intent.putExtra("user_face_url", comment.getFaceUrl());
		mContext.startActivity(intent);
	    }
	});

	// 点击评论内容 可以选择 回复,复制
	//	tvContent.setOnClickListener(new View.OnClickListener() {
	//
	//	    @Override
	//	    public void onClick(View v) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//
	//	});
	return convertView;
    }
}
