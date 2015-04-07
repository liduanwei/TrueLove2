package me.himi.love.adapter;

import java.util.List;

import me.himi.love.AppServiceChatImpl;
import me.himi.love.IAppServiceChat.OnSendMessageResponseListener;
import me.himi.love.IAppServiceChat.SendMessagePostParams;
import me.himi.love.MyApplication;
import me.himi.love.R;
import me.himi.love.adapter.base.BaseListAdapter;
import me.himi.love.adapter.base.ViewHolder;
import me.himi.love.entity.ChatMessage;
import me.himi.love.ui.ZoombleImageActivity;
import android.content.Context;
import android.content.Intent;
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

public class ChatMessagesAdapter extends BaseListAdapter<ChatMessage> {

    public ChatMessagesAdapter(Context context, List<ChatMessage> list) {
	super(context, list);
    }

    // 重写getItemViewType,getViewTypeCount这两个方法,防止错位
    @Override
    public int getItemViewType(int position) {
	// TODO Auto-generated method stub
	ChatMessage msg = this.list.get(position);
	return Integer.parseInt(msg.getFromUserId()) == MyApplication.getInstance().getCurrentLoginedUser().getUserId() ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
	// TODO Auto-generated method stub
	return 2;
    }

    /**
     * 根据消息的发送或接收来创建view
     * @param msg
     * @return
     */
    private View createViewBySender(ChatMessage msg) {
	View v = null;
	if (Integer.parseInt(msg.getFromUserId()) == MyApplication.getInstance().getCurrentLoginedUser().getUserId()) {
	    v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sent, null);
	} else {
	    v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_receive, null);
	}
	return v;
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
	final ChatMessage msg = this.list.get(position);
	if (null == convertView) {
	    convertView = createViewBySender(msg);
	}

	ImageView ivAvatar = ViewHolder.get(convertView, R.id.iv_avatar);
	if (null != msg.getAvatarUrl()) {
	    ImageLoader.getInstance().displayImage(msg.getAvatarUrl(), ivAvatar);
	}

	ImageView ivChatImage = ViewHolder.get(convertView, R.id.iv_chat_image);

	if (msg.getImageUrl() == null || msg.getImageUrl().trim().length() == 0) {
	    ivChatImage.setVisibility(View.GONE);
	} else {
	    ivChatImage.setVisibility(View.VISIBLE);
	    ImageLoader.getInstance().displayImage(msg.getImageUrl(), ivChatImage);
	    ivChatImage.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    Intent intent = new Intent(mContext, ZoombleImageActivity.class);
		    intent.putExtra("image_url", msg.getImageUrl());
		    mContext.startActivity(intent);
		}
	    });
	}

	TextView tvSendStatus = ViewHolder.get(convertView, R.id.tv_send_status);

	if (Integer.parseInt(msg.getFromUserId()) != MyApplication.getInstance().getCurrentLoginedUser().getUserId()) {
	    // 是接收的消息则不显示发送状态
	    tvSendStatus.setVisibility(View.GONE);
	} else {
	    // 是当前用户发送的消息
	    tvSendStatus.setVisibility(View.VISIBLE);
	    if (msg.getMsgStatus() == ChatMessage.SENDING) {
		tvSendStatus.setText("发送中...");
		tvSendStatus.setOnClickListener(null);
	    } else if (msg.getMsgStatus() == ChatMessage.SEND_FAILED) {
		tvSendStatus.setText("点击重发");
		tvSendStatus.setOnClickListener(new OnClickListener() {

		    @Override
		    public void onClick(View v) {
			// TODO Auto-generated method stub
			sendMessage(msg);
		    }
		});
	    } else if (msg.getMsgStatus() == ChatMessage.SEND_SUCCESSED) {
		tvSendStatus.setText("已发送");
		tvSendStatus.setOnClickListener(null);
	    } else {
		tvSendStatus.setText("");
	    }
	}

	TextView tvTime = ViewHolder.get(convertView, R.id.tv_time);
	tvTime.setText(msg.getTimeStr());

	TextView tvContent = ViewHolder.get(convertView, R.id.tv_chat_content);
	tvContent.setText(msg.getContent());

	return convertView;
    }

    private void sendMessage(final ChatMessage msg) {
	String content = msg.getContent().trim();

	msg.setMsgStatus(ChatMessage.SENDING);
	msg.setFromUserId(MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "");
	msg.setContent(content);
	msg.setImageUrl("http://www.12306.cn/mormhweb/images/pic013.gif");
	msg.setVoiceUrl("");

	SendMessagePostParams postParams = new SendMessagePostParams();
	postParams.fromUserId = MyApplication.getInstance().getCurrentLoginedUser().getUserId() + "";
	postParams.toUserId = msg.getTargetUserId();
	postParams.content = content;
	postParams.imageUrl = "";
	postParams.voiceUrl = "";

	AppServiceChatImpl.getInstance().sendMessage(postParams, new OnSendMessageResponseListener() {

	    @Override
	    public void onSuccess(ChatMessage chatMessage) {
		// TODO Auto-generated method stub
		msg.setContent(chatMessage.getContent());
		msg.setMsgStatus(ChatMessage.SEND_SUCCESSED);
		notifyDataSetChanged();
	    }

	    @Override
	    public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		showToast(errorMsg);
		msg.setMsgStatus(ChatMessage.SEND_FAILED);
		notifyDataSetChanged();
	    }
	});
    }

}
