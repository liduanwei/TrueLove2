package me.himi.love.dao;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.entity.ChatMessage;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName:ChatMsgDBHelper
 * @author sparklee liduanwei_911@163.com
 * @date Nov 7, 2014 7:06:36 PM
 */
public class ChatMsgDBHelper extends SQLiteOpenHelper {

    private static ChatMsgDBHelper instance;

    public static ChatMsgDBHelper getInstance(Context context) {
	if (null == instance) {
	    instance = new ChatMsgDBHelper(context);
	}
	return instance;
    }

    public static final String DB_NAME = "db_wei_love_chatmsg.db";
    // 聊天消息表,从服务器获取到的消息添加到本地数据库中, 所属用户ID,发送状态,图标,内容,是否已读,消息时间,本地添加时间
    static final String CREATE_TABLE_CHAT_MESSAGE = "create table tb_wei_chat_message(id integer primary key autoincrement,chat_msg_id int,user_id int comment '本地所属用户ID',from_user_id int,to_user_id int,status int(1),icon varchar(100),content varchar(100),image_url varchar(300),voice_url varchar(300),is_readed int(1),msg_time int(10),create_time int(10))";

    private ChatMsgDBHelper(Context context) {
	this(context, DB_NAME, null, 1);
    }

    private ChatMsgDBHelper(Context context, String name, CursorFactory factory, int version) {
	super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	db.execSQL(CREATE_TABLE_CHAT_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Auto-generated method stub

	String sql = "drop table tb_wei_user_info";
	getWritableDatabase().execSQL(sql);
	//	String sql2 = "drop table tb_wei_user_editor_new";
	//	getWritableDatabase().execSQL(sql2);
    }

    public void insertMessage(ChatMessage msg) {
	String sql = "INSERT INTO tb_wei_chat_message(user_id,chat_msg_id,from_user_id,to_user_id,status,icon,content,image_url,voice_url,is_readed,msg_time,create_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
	int uid = MyApplication.getInstance().getCurrentLoginedUser().getUserId();
	String[] bindArgs = new String[] { uid + "", msg.getId() + "", msg.getFromUserId() + "", msg.getTargetUserId() + "", msg.getMsgStatus() + "", msg.getAvatarUrl(), msg.getContent(), msg.getImageUrl(), msg.getVoiceUrl(), 0 + "", msg.getTime() + "", System.currentTimeMillis() / 1000L + "" };

	getWritableDatabase().execSQL(sql, bindArgs);
    }

    public List<ChatMessage> selectChatMessagesByUserId(String userId, String otherUserId) {
	List<ChatMessage> msges = new ArrayList<ChatMessage>();

	String groupBy = null;
	String having = null;
	String orderBy = "msg_time ASC";

	Cursor cursor = getReadableDatabase().query("tb_wei_chat_message", null, "(from_user_id=? AND to_user_id=?)OR(to_user_id=? AND from_user_id=?) ", new String[] { userId, otherUserId, otherUserId, userId }, groupBy, having, orderBy);

	while (cursor.moveToNext()) {
	    int id = cursor.getInt(cursor.getColumnIndex("chat_msg_id"));
	    String fromUserId = cursor.getString(cursor.getColumnIndex("from_user_id"));
	    String toUserId = cursor.getString(cursor.getColumnIndex("to_user_id"));

	    int status = cursor.getInt(cursor.getColumnIndex("status"));
	    String icon = cursor.getString(cursor.getColumnIndex("icon"));
	    String content = cursor.getString(cursor.getColumnIndex("content"));
	    String imageUrl = cursor.getString(cursor.getColumnIndex("image_url"));
	    String voiceUrl = cursor.getString(cursor.getColumnIndex("voice_url"));

	    int time = cursor.getInt(cursor.getColumnIndex("msg_time"));

	    ChatMessage msg = new ChatMessage();
	    msg.setId(id);
	    msg.setMsgStatus(status);
	    msg.setAvatarUrl(icon);
	    msg.setFromUserId(fromUserId);
	    msg.setTargetUserId(toUserId);
	    msg.setContent(content);
	    msg.setImageUrl(imageUrl);
	    msg.setVoiceUrl(voiceUrl);

	    msg.setTime(time);

	    msges.add(msg);
	}

	cursor.close();

	return msges;

    }
}
