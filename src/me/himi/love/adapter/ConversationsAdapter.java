package me.himi.love.adapter;

import io.rong.imlib.RongIMClient.Conversation;
import io.rong.imlib.RongIMClient.MessageContent;
import io.rong.message.TextMessage;

import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName:UserNearbyAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 2:52:14 PM
 */

public class ConversationsAdapter extends BaseListAdapter<Conversation> {

    public ConversationsAdapter(Context context, List<Conversation> list) {
	super(context, list);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	if (null == convertView) {
	    convertView = LayoutInflater.from(mContext).inflate(R.layout.conversation_item, null);
	}

	final Conversation conversation = this.list.get(position);

	final ImageView ivFace = ViewHolder.get(convertView, R.id.iv_user_face);

	TextView tvNickname = ViewHolder.get(convertView, R.id.tv_user_nickname);
	TextView tvContent = ViewHolder.get(convertView, R.id.tv_content);

	ImageView ivVip = ViewHolder.get(convertView, R.id.iv_vip);

	// 距离
	TextView tvDistance = ViewHolder.get(convertView, R.id.tv_user_distance);

	// vip
	//	ivVip.setVisibility(user.getVip() != 0 ? View.VISIBLE : View.GONE);

	// displayImage 会有刷新闪烁问题

	//	ImageLoader.getInstance().displayImage(conversation.get.getFaceUrl().getSmallImageUrl(), ivFace, ImageLoaderOptions.rounderOptions(), new ImageLoadingListener() {
	//
	//	    @Override
	//	    public void onLoadingStarted(String arg0, View arg1) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//
	//	    @Override
	//	    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//
	//	    @Override
	//	    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
	//		// TODO Auto-generated method stub
	//		notifyDataSetChanged();
	//	    }
	//
	//	    @Override
	//	    public void onLoadingCancelled(String arg0, View arg1) {
	//		// TODO Auto-generated method stub
	//
	//	    }
	//	});

	tvNickname.setText(conversation.getConversationTitle());

	MessageContent msg = conversation.getLatestMessage();
	if (msg instanceof TextMessage) {
	    TextMessage txtMsg = (TextMessage) msg;
	    tvContent.setText(txtMsg.getContent());
	}
	//	tvAge.setText(user.getAge() + "");

	//	tvDistance.setText(user.getDistance() + "km");

	return convertView;
    }

}
