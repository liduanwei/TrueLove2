package me.himi.love;

import java.util.List;

import me.himi.love.entity.ChatMessage;
import me.himi.love.entity.ReceivedChat;

/**
 * @ClassName:IAppServiceChat
 * @author sparklee liduanwei_911@163.com
 * @date Dec 16, 2014 4:17:02 PM
 */
public interface IAppServiceChat {
    void sendMessage(SendMessagePostParams postParams, OnSendMessageResponseListener listener);

    public static class SendMessagePostParams {
	public String toUserId;
	public String fromUserId;
	public String content;
	public String imageUrl;
	public String voiceUrl;
    }

    public interface OnSendMessageResponseListener {
	void onSuccess(ChatMessage msg);

	void onFailure(String errorMsg);
    }

    /**
     * 加载所有来自不同用户的聊天消息
     * @param postParams
     * @param listener
     */
    void loadAllChatMessages(LoadAllChatMessagesPostParams postParams, OnLoadAllChatMessagesResponseListener listener);

    public static class LoadAllChatMessagesPostParams {
	public int page, pageSize;
    }

    public interface OnLoadAllChatMessagesResponseListener {
	void onSuccess(List<ReceivedChat> msges);

	void onFailure(String errorMsg);
    }

    void loadAllChatMsgesByOtherUserId(LoadAllChatMsgesByOtherUserIdPostParams postParams, OnLoadAllChatMsgesByOtherUserIdResponseListener listener);

    public static class LoadAllChatMsgesByOtherUserIdPostParams {
	public int otherUserId;
	public int page, pageSize;

	public int lastMsgTime;
    }

    public interface OnLoadAllChatMsgesByOtherUserIdResponseListener {
	void onSuccess(List<ChatMessage> msges);

	void onFailure(String errorMsg);
    }

    //    void loadChatMessages(LoadChatMessagesPostParams postParams,OnLoad)

    /**
     * 删除指定时间的聊天消息
     * @param postParams
     * @param listener
     */
    void deleteMessagesByTime(DeleteChatMessagesPostParams postParams, OnDeleteChatMessagesResponseListener listener);

    public static class DeleteChatMessagesPostParams {
	public int userId, otherUserId;
	public int lastMsgTime;
    }

    public interface OnDeleteChatMessagesResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }
}
