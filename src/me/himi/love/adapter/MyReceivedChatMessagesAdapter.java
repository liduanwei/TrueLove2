package me.himi.love.adapter;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.ChatMessage;
import me.himi.love.entity.ReceivedChat;
import me.himi.love.util.ImageLoaderOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @ClassName:MyReceivedChatMessagesAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */
public class MyReceivedChatMessagesAdapter extends BaseListAdapter<ReceivedChat> {

    public MyReceivedChatMessagesAdapter(Context context, List<ReceivedChat> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_msg_item, null);
	}

	ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_message_icon);
	TextView tvMessageTitle = ViewHolder.get(convertView, R.id.tv_message_title);
	TextView tvMessageContent = ViewHolder.get(convertView, R.id.tv_message_content_simple);
	TextView tvMessageTime = ViewHolder.get(convertView, R.id.tv_message_time);
	TextView tvMessageCount = ViewHolder.get(convertView, R.id.tv_msg_count);

	ReceivedChat msg = list.get(position);

	ImageLoader.getInstance().displayImage(msg.getFaceUrl().getSmallImageUrl(), ivIcon, ImageLoaderOptions.rounderOptions(), new ImageLoadingListener() {

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
		notifyDataSetChanged(); // 更新 图片 圆角

	    }

	    @Override
	    public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	    }
	});
	tvMessageTitle.setText(msg.getNickname());
	tvMessageContent.setText(msg.getContent());
	tvMessageTime.setText(msg.getTimeStr());
	tvMessageCount.setText(msg.getCount() + "");

	return convertView;
    }

}
