package me.himi.love.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.himi.love.entity.MyQuestion;
import me.himi.love.entity.PrivateMessage;
import me.himi.love.entity.PrivateMessage.MessageType;
import me.himi.love.util.ActivityUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName:DBHelper
 * @author sparklee liduanwei_911@163.com
 * @date Nov 7, 2014 7:06:36 PM
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static DBHelper getInstance(Context context, String name, CursorFactory factory, int version) {
	if (null == instance) {
	    instance = new DBHelper(context, name, factory, version);
	}
	return instance;
    }

    public static final String DB_NAME = "db_wei_love6.db";
    // 本地已注册用户ID,密码,昵称,IM账户用户名,IM账户密码,头像url
    static final String CREATE_TABLE_USER = "create table tb_wei_user(id integer primary key autoincrement,user_id int not null unique,user_pwd char(20) not null,user_nickname char(100),last_logintime int(10),face_url varchar(300),im_username varchar(20),im_password varchar(20))";
    // 首页推荐用户的简单资料 缓存
    static final String CREATE_TABLE_RECOMMEND_USER = "create table tb_wei_recommend_user(id integer primary key autoincrement,user_id int,gender int,nickname char(20),age int,height int,weight int,monologue varchar(500),homeplace varchar(10),address varchar(10))";

    // 私信消息表,service 从服务器获取到的消息添加到本地数据库中, 所属用户ID,类型,图标,标题,内容,是否已读,时间
    static final String CREATE_TABLE_MESSAGE = "create table tb_wei_message(id integer primary key autoincrement,user_id int,type int(1),count int,icon varchar(100),title varchar(50),content varchar(100),is_readed int(1),msg_time int(10) unique)";

    // 用户创建的问题
    static final String CREATE_TABLE_MYQUESTION = "create table tb_wei_myquestion(id integer primary key autoincrement,title varchar(100),option1 varchar(20) not null,option2 varchar(20) not null,option3 varchar(20),option4 varchar(20),create_time int(10) default 0)";

    // 关注表
    static final String CREATE_TABLE_FOLLOW = "create table tb_wei_follow(id integer primary key autoincrement,user_id int comment '所属用户id',follow_user_id int comment '被关注的用户id',create_time int(10) default 0)";

    private DBHelper(Context context, String name, CursorFactory factory, int version) {
	super(context, name, factory, version);
	// TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	db.execSQL(CREATE_TABLE_USER);
	db.execSQL(CREATE_TABLE_RECOMMEND_USER);
	db.execSQL(CREATE_TABLE_MESSAGE);
	db.execSQL(CREATE_TABLE_MYQUESTION);
	db.execSQL(CREATE_TABLE_FOLLOW);

	//	db.execSQL(CREATE_TABLE_EDITOR_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Auto-generated method stub

	String sql = "drop table tb_wei_user_info";
	getWritableDatabase().execSQL(sql);
	//	String sql2 = "drop table tb_wei_user_editor_new";
	//	getWritableDatabase().execSQL(sql2);

    }

    /**
     * 清空所有数据
     */
    public void deleteAllTables() {
	// 不支持 truncate?
	String sql2 = "delete from tb_wei_user";
	String sql3 = "delete from tb_wei_recommend_user";
	String sql4 = "delete from tb_wei_message";
	String sql5 = "delete from tb_wei_myquestion";
	String sql6 = "delete from tb_wei_follow";
	getWritableDatabase().execSQL(sql2);
	getWritableDatabase().execSQL(sql3);
	getWritableDatabase().execSQL(sql4);
	getWritableDatabase().execSQL(sql5);
	getWritableDatabase().execSQL(sql6);
    }

    /**
     * 清空消息表(每次新注册完后清空)
     */
    public void deleteMessageTable() {
	String sql2 = "delete from tb_wei_message";
	getWritableDatabase().execSQL(sql2);
    }

    /**
     * 添加注册成功的用户
     * @param userId
     * @param password
     * @param nickname
     */
    public void insertUser(int userId, String password, String nickname) {
	String sql = "insert into tb_wei_user(user_id,user_pwd,user_nickname,last_logintime)values({USER_ID},'{USER_PWD}','{USER_NICKNAME}',{LAST_LOGINTIME})".replace("{USER_ID}", userId + "").replace("{USER_PWD}", password).replace("{USER_NICKNAME}", nickname).replace("{LAST_LOGINTIME}", new Date().getTime() / 1000 + "");
	getWritableDatabase().execSQL(sql);
    }

    /**
     * 修改已有的账户
     * @param userId
     * @param password
     * @param nickname
     */
    public void updateUser(int userId, String password, String nickname, int last_logintime, String faceUrl, String imUserName, String imPassword) {
	String sql = String.format("update tb_wei_user set user_pwd='%s',user_nickname='%s',last_logintime=%s,face_url='%s',im_username='%s',im_password='%s' where user_id=%s", password, nickname, last_logintime, faceUrl, imUserName, imPassword, userId);
	getWritableDatabase().execSQL(sql);
    }

    /**
     * 查询最后登录的用户
     * @return
     */
    public User queryLastLoginUser() {
	//	String sql = String.format("select user.user_id,user.user_pwd,user.user_nickname from tb_wei_user ");
	Cursor cursor = getReadableDatabase().query("tb_wei_user", new String[] { "user_id", "user_pwd", "user_nickname", "im_username", "im_password", "face_url" }, null, null, null, null, "last_logintime DESC");

	if (cursor == null) {
	    return null;

	}

	if (cursor.moveToFirst()) {

	    int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
	    String pwd = cursor.getString(cursor.getColumnIndex("user_pwd"));
	    String nickname = cursor.getString(cursor.getColumnIndex("user_nickname"));

	    String imUsername = cursor.getString(cursor.getColumnIndex("im_username"));
	    String imPassword = cursor.getString(cursor.getColumnIndex("im_password"));

	    User user = new User();
	    user.userId = userId;
	    user.pwd = pwd;
	    user.nickname = nickname;

	    user.imUserName = imUsername;
	    user.imPassword = imPassword;

	    user.faceUrl = cursor.getString(cursor.getColumnIndex("face_url"));
	    return user;
	}
	cursor.close();
	close();
	return null;
    }

    /**
     * 查询指定用户是否存在
     * @param userId
     * @return
     */
    public boolean findUser(int userId) {
	Cursor cursor = getReadableDatabase().query("tb_wei_user", null, "user_id=?", new String[] { userId + "" }, null, null, "last_logintime DESC");
	if (cursor != null && cursor.moveToFirst()) {
	    return true;
	}
	return false;
    }

    /**
     * 查询所有注册登录过的账户含明文密码
     * @return
     */
    public List<User> queryAllUsers() {

	Cursor cursor = getReadableDatabase().query("tb_wei_user", new String[] { "user_id", "user_pwd", "user_nickname", "im_username", "im_password", "face_url" }, null, null, null, null, "last_logintime DESC");

	if (cursor == null) {
	    return null;
	}
	List<User> users = new ArrayList<User>();
	while (cursor.moveToNext()) {
	    int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
	    String pwd = cursor.getString(cursor.getColumnIndex("user_pwd"));
	    String nickname = cursor.getString(cursor.getColumnIndex("user_nickname"));

	    String imUsername = cursor.getString(cursor.getColumnIndex("im_username"));
	    String imPassword = cursor.getString(cursor.getColumnIndex("im_password"));

	    // faceUrl
	    String faceUrl = cursor.getString(cursor.getColumnIndex("face_url"));
	    User user = new User();
	    user.userId = userId;
	    user.pwd = pwd;
	    user.nickname = nickname;
	    user.imUserName = imUsername;
	    user.imPassword = imPassword;

	    user.faceUrl = faceUrl;

	    users.add(user);
	}
	cursor.close();

	return users;
    }

    /**
     * 插入推荐用户(缓存)
     */
    public void insertRecommendUser(RecommendUser user) {
	String sql = String.format("insert into tb_wei_recommend_user(user_id,gender,age,nickname,height,weight,monologue,homeplace,address) values(%s,%s,%s,'%s',%s,%s,'%s','%s','%s')", user.userId, user.gender, user.age, user.nickname, user.height, user.weight, user.monologue, user.homeplace, user.address);
	getWritableDatabase().execSQL(sql);
    }

    public static class RecommendUser {
	public int userId;
	public int gender;
	public int age;
	public int height, weight;
	public String nickname;
	public String monologue;
	public String homeplace, address;
    }

    /**
     * 插入我的问题
     * @param myquestion
     */
    public void insertMyQuestion(MyQuestion myquestion) {

	String sql = String.format("insert into tb_wei_myquestion(title,option1,option2,option3,option4,create_time) values('%s','%s','%s','%s','%s',%s)", myquestion.getTitle(), myquestion.getOptions().get(0), myquestion.getOptions().get(1), myquestion.getOptions().get(2), myquestion.getOptions().get(3), myquestion.getCreateTime());

	getWritableDatabase().execSQL(sql);

    }

    /**
     * 更新我的问题
     * @param myQuestion
     * @return
     */
    public int updateMyQuestion(MyQuestion myQuestion) {
	ContentValues values = new ContentValues();

	values.put("title", myQuestion.getTitle());
	values.put("option1", myQuestion.getOptions().get(0));
	values.put("option2", myQuestion.getOptions().get(1));
	values.put("option3", myQuestion.getOptions().get(2));
	values.put("option4", myQuestion.getOptions().get(3));

	int result = getWritableDatabase().update("tb_wei_myquestion", values, "id=?", new String[] { myQuestion.getId() + "" });
	close();
	return result;
    }

    /**
     * 查询我的所有问题
     * @param limitStart
     * @param limitSize
     * @return
     */
    public List<MyQuestion> queryMyQuestions(int limitStart, int limitSize) {

	// limit start
	limitStart = limitStart * limitSize;

	Cursor cursor = getReadableDatabase().query("tb_wei_myquestion", null, null, null, null, null, "create_time DESC", limitStart + "," + limitSize);

	if (cursor == null) {
	    System.out.println("cursor is null or no data");
	    return null;
	}

	//	if (!cursor.moveToFirst()) { // cursor移动到起始处
	//	    return null;
	//	}

	List<MyQuestion> myQuestions = new ArrayList<MyQuestion>();

	while (cursor.moveToNext()) {

	    List<String> options = new ArrayList<String>();

	    int id = cursor.getInt(cursor.getColumnIndex("id"));

	    String title = cursor.getString(cursor.getColumnIndex("title"));

	    String option1 = cursor.getString(cursor.getColumnIndex("option1"));
	    String option2 = cursor.getString(cursor.getColumnIndex("option2"));
	    String option3 = cursor.getString(cursor.getColumnIndex("option3"));
	    String option4 = cursor.getString(cursor.getColumnIndex("option4"));
	    int createTime = cursor.getInt(cursor.getColumnIndex("create_time"));

	    // 装填数据
	    MyQuestion myquestion = new MyQuestion();
	    myquestion.setId(id);

	    myquestion.setTitle(title);

	    myquestion.setCreateTime(createTime);
	    myquestion.setCreateTimeStr(ActivityUtil.convertTimeToString(createTime * 1000L));
	    options.add(option1);
	    options.add(option2);
	    options.add(option3);
	    options.add(option4);
	    myquestion.setOptions(options);

	    myQuestions.add(myquestion);
	}
	cursor.close();
	close();

	return myQuestions;
    }

    /**
     * 删除问题
     * @param questionId
     */
    public int deleteMyQuestion(int questionId) {
	int r = getWritableDatabase().delete("tb_wei_myquestion", "id=?", new String[] { questionId + "" });
	close();
	return r;
    }

    /**
     * 用户关注表
     */
    public void insertFollow(UserFollow userFollow) {
	String sql = String.format("insert into tb_wei_follow(user_id,follow_user_id,create_time) values(%s,%s,%s)", userFollow.userId, userFollow.followUserId, userFollow.createTime);
	getWritableDatabase().execSQL(sql);
    }

    /**
     * 查询指定userId和followUserId的记录
     * @param userId
     * @param followUserId
     * @return
     */
    public UserFollow queryFollow(int userId, int followUserId) {
	Cursor cursor = getReadableDatabase().query("tb_wei_follow", null, "user_id=? AND follow_user_id=?", new String[] { userId + "", followUserId + "" }, null, null, null);
	if (cursor == null) {
	    return null;
	}

	if (cursor.moveToFirst()) {
	    UserFollow follow = new UserFollow();
	    int id = cursor.getInt(cursor.getColumnIndex("id"));
	    int createTime = cursor.getInt(cursor.getColumnIndex("create_time"));
	    follow.id = id;
	    follow.userId = userId;
	    follow.followUserId = followUserId;
	    follow.createTime = createTime;
	    cursor.close();
	    return follow;
	}
	cursor.close();
	return null;
    }

    /**
     * 删除关注
     * @param userId
     * @param followUserId
     */
    public void deleteFollow(int userId, int followUserId) {
	String sql = String.format("delete from tb_wei_follow where user_id=%d AND follow_user_id=%d", userId, followUserId);
	getWritableDatabase().execSQL(sql);
    }

    public void updateFollow(UserFollow follow) {
    }

    public static class UserFollow {
	public int id;
	public int userId;
	public int followUserId;
	public int createTime;
    }

    /**
     * 插入消息
     * @param msg
     */
    public void insertMessage(PrivateMessage msg) {

	String sql = "select * from tb_wei_message where user_id=? AND type=?";
	Cursor cursor = getReadableDatabase().rawQuery(sql, new String[] { msg.getUserId() + "", msg.getType().ordinal() + "" });
	if (cursor == null || !cursor.moveToFirst()) { // 不存在该条消息就添加
	    sql = "INSERT INTO tb_wei_message(user_id,type,count,icon,title,content,is_readed,msg_time) VALUES(?,?,?,?,?,?,?,?)";
	    try {
		getWritableDatabase().execSQL(sql, new String[] { msg.getUserId() + "", msg.getType().ordinal() + "", msg.getCount() + "", msg.getIcon(), msg.getTitle(), msg.getContent(), msg.isReaded() ? 1 + "" : 0 + "", msg.getLastMsgTime() + "" });
	    } catch (Throwable th) {
		System.out.println(th);
	    }
	    return;
	}
	// 原未读数量
	int oldCount = cursor.getInt(cursor.getColumnIndex("count"));
	// 更新
	ContentValues values = new ContentValues();
	values.put("count", msg.getCount() + oldCount); // 增加上去
	values.put("title", msg.getTitle());
	values.put("icon", msg.getIcon());
	values.put("content", msg.getContent());
	values.put("msg_time", msg.getLastMsgTime());
	values.put("is_readed", msg.isReaded() ? 1 : 0);
	getWritableDatabase().update("tb_wei_message", values, "user_id=? AND type=?", new String[] { msg.getUserId() + "", msg.getType().ordinal() + "" });
    }

    /**
     * 查询指定用户的未读的消息
     * @return
     */
    public List<PrivateMessage> selectUnreadMessages(int userId) {
	//	int unreadHiCount = selectUnreadMessageCountByType(MessageType.SAYHI.ordinal());
	//	int unreadFollowCount = selectUnreadMessageCountByType(MessageType.FOLLOW.ordinal());
	//	int unreadQuestionCount = selectUnreadMessageCountByType(MessageType.QUESTION.ordinal());
	//	int unreadVisitorCount = selectUnreadMessageCountByType(MessageType.VISITOR.ordinal());

	String sql = "select * from tb_wei_message where is_readed=0 AND user_id=" + userId;
	Cursor cursor = getReadableDatabase().rawQuery(sql, null);
	if (cursor == null) {
	    return null;
	}

	List<PrivateMessage> messages = new ArrayList<PrivateMessage>();

	//	System.out.println("unreadHiCount:" + unreadHiCount);
	//	System.out.println("unreadFollowCount:" + unreadFollowCount);
	//	System.out.println("unreadQuestionCount:" + unreadQuestionCount);
	//	System.out.println("unreadVisitorCount:" + unreadVisitorCount);
	while (cursor.moveToNext()) {
	    PrivateMessage msg = new PrivateMessage();
	    String title = cursor.getString(cursor.getColumnIndex("title"));
	    String icon = cursor.getString(cursor.getColumnIndex("icon"));
	    String content = cursor.getString(cursor.getColumnIndex("content"));
	    int type = cursor.getInt(cursor.getColumnIndex("type"));
	    int lastMsgTime = cursor.getInt(cursor.getColumnIndex("msg_time"));
	    int isReaded = cursor.getInt(cursor.getColumnIndex("is_readed"));
	    int id = cursor.getInt(cursor.getColumnIndex("id"));
	    int count = cursor.getInt(cursor.getColumnIndex("count"));

	    msg.setIcon(icon);
	    msg.setTitle(title);
	    if (type == MessageType.SAYHI.ordinal()) {
		msg.setType(MessageType.SAYHI);
		msg.setCount(count);
	    } else if (type == MessageType.QUESTION.ordinal()) {
		msg.setType(MessageType.QUESTION);
		msg.setCount(count);
	    } else if (type == MessageType.FOLLOW.ordinal()) {
		msg.setType(MessageType.FOLLOW);
		msg.setCount(count);
	    } else if (type == MessageType.VISITOR.ordinal()) {
		msg.setType(MessageType.VISITOR);
		msg.setCount(count);
	    } else if (type == MessageType.SYSTEM.ordinal()) {
		msg.setType(MessageType.SYSTEM);
		msg.setCount(count);
	    } else if (type == MessageType.CHAT.ordinal()) {
		msg.setType(MessageType.CHAT);
		msg.setCount(count);
	    } else if (type == MessageType.GIFTS.ordinal()) {
		msg.setType(MessageType.GIFTS);
		msg.setCount(count);
	    }
	    msg.setContent(content);
	    msg.setReaded(false);
	    msg.setLastMsgTime(lastMsgTime);
	    msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
	    msg.setId(id);
	    msg.setUserId(userId);

	    messages.add(msg);
	}
	cursor.close();

	return messages;
    }

    /**
     * 查询指定用户的所有(已读或未读)的消息
     * @return
     */
    public List<PrivateMessage> selectAllMessages(int userId) {
	//	int unreadHiCount = selectUnreadMessageCountByType(MessageType.SAYHI.ordinal());
	//	int unreadFollowCount = selectUnreadMessageCountByType(MessageType.FOLLOW.ordinal());
	//	int unreadQuestionCount = selectUnreadMessageCountByType(MessageType.QUESTION.ordinal());
	//	int unreadVisitorCount = selectUnreadMessageCountByType(MessageType.VISITOR.ordinal());

	String sql = "select * from tb_wei_message where user_id=" + userId + " AND is_readed=0";
	Cursor cursor = getReadableDatabase().rawQuery(sql, null);
	if (cursor == null) {
	    return null;
	}

	List<PrivateMessage> messages = new ArrayList<PrivateMessage>();

	//	System.out.println("unreadHiCount:" + unreadHiCount);
	//	System.out.println("unreadFollowCount:" + unreadFollowCount);
	//	System.out.println("unreadQuestionCount:" + unreadQuestionCount);
	//	System.out.println("unreadVisitorCount:" + unreadVisitorCount);
	while (cursor.moveToNext()) {
	    PrivateMessage msg = new PrivateMessage();
	    String title = cursor.getString(cursor.getColumnIndex("title"));
	    String icon = cursor.getString(cursor.getColumnIndex("icon"));
	    String content = cursor.getString(cursor.getColumnIndex("content"));
	    int type = cursor.getInt(cursor.getColumnIndex("type"));
	    int lastMsgTime = cursor.getInt(cursor.getColumnIndex("msg_time"));
	    int isReaded = cursor.getInt(cursor.getColumnIndex("is_readed"));
	    int id = cursor.getInt(cursor.getColumnIndex("id"));
	    int count = cursor.getInt(cursor.getColumnIndex("count"));

	    msg.setIcon(icon);
	    msg.setTitle(title);
	    if (type == MessageType.SAYHI.ordinal()) {
		msg.setType(MessageType.SAYHI);
		msg.setCount(count);
	    } else if (type == MessageType.QUESTION.ordinal()) {
		msg.setType(MessageType.QUESTION);
		msg.setCount(count);
	    } else if (type == MessageType.FOLLOW.ordinal()) {
		msg.setType(MessageType.FOLLOW);
		msg.setCount(count);
	    } else if (type == MessageType.VISITOR.ordinal()) {
		msg.setType(MessageType.VISITOR);
		msg.setCount(count);
	    } else if (type == MessageType.SYSTEM.ordinal()) {
		msg.setType(MessageType.SYSTEM);
		msg.setCount(count);
	    } else if (type == MessageType.CHAT.ordinal()) {
		msg.setType(MessageType.CHAT);
		msg.setCount(count);
	    } else if (type == MessageType.GIFTS.ordinal()) {
		msg.setType(MessageType.GIFTS);
		msg.setCount(count);
	    }
	    msg.setContent(content);
	    msg.setReaded(isReaded == 1);
	    msg.setLastMsgTime(lastMsgTime);
	    msg.setTime(ActivityUtil.convertTimeToString(lastMsgTime * 1000L));
	    msg.setId(id);
	    msg.setUserId(userId);

	    messages.add(msg);
	}
	cursor.close();

	return messages;
    }

    /**
     * 查询指定类型未读消息的数量
     * @param type
     * @return
     */
    public int selectUnreadMessageCountByType(int type) {
	String sql = "select count(*) from tb_wei_message where is_readed=0 AND type=" + type;
	Cursor cursor = getReadableDatabase().rawQuery(sql, null);
	if (cursor == null) {
	    return 0;
	}

	long count = 0;
	if (cursor.moveToNext()) {
	    count = cursor.getLong(0); // getLong
	}
	cursor.close();
	return (int) count;
    }

    /**
     * 更新指定类型消息为已读
     * @param userId
     * @param type
     * @return
     */
    public int updateMessageToReaded(int userId, int type) {
	ContentValues values = new ContentValues();
	values.put("is_readed", 1);
	values.put("count", 0);
	return getWritableDatabase().update("tb_wei_message", values, "is_readed=? AND user_id=? AND type=?", new String[] { 0 + "", userId + "", type + "" });
    }

    /**
     * 更新指定ID消息为已读
     * @param userId
     * @param type
     * @return
     */
    public int updateMessageToReadedById(int id) {
	ContentValues values = new ContentValues();
	values.put("is_readed", 1);
	values.put("count", 0);
	return getWritableDatabase().update("tb_wei_message", values, "id=?", new String[] { id + "" });
    }

    /**
     * 查询数据库表中是否存在未读消息
     * @return
     */
    public boolean hasUnreadMessage() {
	return false;
    }

    public void close() {
	getWritableDatabase().close();
	getReadableDatabase().close();
    }

    public static class User {
	public int userId;
	public String pwd;
	public String nickname;

	public String imUserName, imPassword;

	public String faceUrl;
    }

}
