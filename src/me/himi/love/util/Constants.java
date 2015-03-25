package me.himi.love.util;

/**
 * @ClassName:Constants
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:14:59 PM
 */
public interface Constants {
    //            String HOST = "http://192.168.1.101/RedLove2";

    String HOST = "http://love5.leavtechintl.com";

    //    String URL_DESTINY_USER = HOST + "/index.php/index/user/index";
    String URL_DESTINY_USER = HOST + "/index.php/index/user/nearby";

    // 推荐用户
    String URL_RECOMMEND_USER = HOST + "/index.php/index/user/recommend";

    // 附近用户
    String URL_NEARBY_USER = HOST + "/index.php/index/user/nearby";

    // 用户基本资料
    String URL_USER_SIMPLE_INFO = HOST + "/index.php/index/user/userSimpleInfo";

    // 好友用户
    String URL_FRIENDS_USER = HOST + "/index.php/index/user/friends";

    // 注册
    String URL_REGISTER = HOST + "/index.php/index/register/register";

    // 完善资料
    String URL_REGISTER_PERFECT_COMPLETE = HOST + "/index.php/index/register/updatePerfectComplete";

    // 登录
    String URL_LOGIN = HOST + "/index.php/index/login/login";

    // 退出登录, 清除session
    String URL_LOGOUT = HOST + "/index.php/index/login/logout";

    // 查看用户资料
    String URL_DETAIL = HOST + "/index.php/index/user/lookover";

    // 修改资料
    String URL_UPDATE_INFO = HOST + "/index.php/index/user/update";
    //    String URL_UPDATE_INFO = HOST + "/index.php/update/update";

    // 发布留言
    String URL_PUBLISH_NEWS = HOST + "/index.php/index/news/publish";

    // 查看指定用户留言
    String URL_USER_NEWS = HOST + "/index.php/index/news/lookover";

    // 查看当前登录用户的所有关注用户的留言
    String URL_FOLLOWS_NEWS = HOST + "/index.php/index/news/followsNews";

    // 查看当前登录用户留言
    String URL_CURRENT_LOGEDIN_USER_NEWS = HOST + "/index.php/index/news/lookoverme";

    // 修改当前登录用户留言
    String URL_UPDATE_USER_NEWS = HOST + "/index.php/index/news/update";

    // 删除当前登录用户留言
    String URL_DELETE_USER_NEWS = HOST + "/index.php/index/news/delete";

    //查看指定用户的最近留言
    String URL_USER_RECENT_NEWS_INFOS = HOST + "/index.php/index/news/recent";

    // 查看指定留言的评论
    String URL_USER_NEWS_COMMENT = HOST + "/index.php/index/comments/lookover";

    // 发表留言评论
    String URL_USER_NEWS_COMMENT_PUBLISH = HOST + "/index.php/index/comments/publish";

    // 用户访客
    String URL_USER_VISITORS = HOST + "/index.php/index/visitor/index";
    // 看过谁
    String URL_VISITED_USERS = HOST + "/index.php/index/visitor/selectVisitedUsers";

    // 获取指定用户的关注用户(简单资料)
    String URL_USER_FOLLOWS = HOST + "/index.php/index/follow/follows";

    // 获取指定用户的粉丝用户(简单资料)
    String URL_USER_FANS = HOST + "/index.php/index/follow/fans";

    // 获取用户最近几位访客
    String URL_RECENT_USER_VISITORS = HOST + "/index.php/index/visitor/selectRecentVisitors";

    // 获取用户最近几位粉丝
    String URL_RECENT_USER_FANS = HOST + "/index.php/index/follow/lookoverRecentFans";

    // 获取用户最近几位关注
    String URL_RECENT_USER_FOLLOWS = HOST + "/index.php/index/follow/lookoverRecentFollows";

    // 用户上传头像
    String URL_USER_UPLOAD_FACE = HOST + "/index.php/index/upload/uploadFace";

    // 用户上传头像
    String URL_USER_UPLOAD_PHOTO = HOST + "/index.php/index/upload/uploadPhoto";

    // 打招呼
    String URL_USER_SAYHI = HOST + "/index.php/index/hi/sayhi";

    // 查看当前登录用户收到的招呼
    String URL_USER_SAYHI_LIST = HOST + "/index.php/index/hi/lookover";

    // 获取当前登录用户收到的打招呼消息
    String URL_USER_MESSAGE_COUNT = HOST + "/index.php/index/message/index";

    // 获取当前登录用户收到的打招呼消息
    String URL_USER_MESSAGE_HI = HOST + "/index.php/index/message/hi";
    // 获取当前登录用户收到的粉丝消息
    String URL_USER_MESSAGE_FANS = HOST + "/index.php/index/message/fans";
    // 获取当前登录用户收到的问题消息
    String URL_USER_MESSAGE_QUESTION = HOST + "/index.php/index/message/question";
    // 获取当前登录用户收到的问题消息
    String URL_USER_MESSAGE_VISITOR = HOST + "/index.php/index/message/visitor";
    // 获取当前登录用户收到的系统消息
    String URL_USER_MESSAGE_SYSTEM = HOST + "/index.php/index/message/system";
    // 获取当前登录用户最后收到的聊天消息
    String URL_USER_MESSAGE_CHAT = HOST + "/index.php/index/message/chat";

    // 通知更新当前登录用户最后收到的打招呼消息
    String URL_UPDATE_USER_MESSAGE_HI_TIME = HOST + "/index.php/index/message/updateHiLastTime";
    // 通知更新当前登录用户最后收到的粉丝消息
    String URL_UPDATE_USER_MESSAGE_FANS_TIME = HOST + "/index.php/index/message/updateFansLastTime";
    // 通知更新当前登录用户最后收到的问题消息
    String URL_UPDATE_USER_MESSAGE_QUESTION_TIME = HOST + "/index.php/index/message/updateQuestionLastTime";
    // 通知更新当前登录用户最后收到的访客消息
    String URL_UPDATE_USER_MESSAGE_VISITOR_TIME = HOST + "/index.php/index/message/updateVisitorLastTime";
    // 通知更新当前登录用户最后收到的系统消息
    String URL_UPDATE_USER_MESSAGE_SYSTEM_TIME = HOST + "/index.php/index/message/updateSystemLastTime";
    // 通知更新当前登录用户最后收到的聊天消息
    String URL_UPDATE_USER_MESSAGE_CHAT_TIME = HOST + "/index.php/index/message/updateChatLastTime";

    // 添加关注
    String URL_USER_FOLLOW_ADD = HOST + "/index.php/index/follow/addfollow";

    // 删除关注
    String URL_USER_FOLLOW_DELETE = HOST + "/index.php/index/follow/unfollow";

    // 查看当前登录用户的所有关注用户
    String URL_CURRENT_USER_FOLLOWS = HOST + "/index.php/index/follow/lookoverFollow";

    // 查看当前登录用户的所有粉丝用户
    String URL_CURRENT_USER_FANS = HOST + "/index.php/index/follow/lookoverFans";

    // 查看当前登录用户的所有粉丝用户
    String URL_USER_UNREAD_FANS = HOST + "/index.php/index/follow/lookoverFans";

    // 同步添加用户注册成功的im账户
    String URL_INSERT_IM = HOST + "/index.php/index/im/register";

    // 提问题
    String URL_POST_QUESTION = HOST + "/index.php/index/question/question";

    // 查看所有问题
    String URL_LOAD_QUESTIONS = HOST + "/index.php/index/question/lookoverQuestions";

    // 签到
    String URL_SIGNIN = HOST + "/index.php/index/user/signin";

    // 检测更新当前APP版本
    String URL_CHECKUPDATE = HOST + "/index.php/update";

    // (通过注册邮箱)重置密码
    String URL_RESET_PWD = HOST + "/index.php/password/checkAndSendEmail";
    // (通过注册邮箱)重置密码 验证码
    String URL_RESET_PWD_VERIFY_CODE = HOST + "/index.php/password/verify";

    // 困住小猫flash小游戏
    String URL_CAT_GAME = HOST + "/index.php/index/game";

    // 发表文章
    String URL_ARTICLE_PUBLISH = HOST + "/index.php/index/article/publish";
    // 加载文章
    String URL_ARTICLES = HOST + "/index.php/index/article/selectSecrets";
    // 发表文章评论
    String URL_ARTICLE_COMMENT_PUBLISH = HOST + "/index.php/index/articleComment/publish";
    // 加载文章评论
    String URL_ARTICLE_COMMENTS = HOST + "/index.php/index/articleComment/selectComments";

    // 顶文章
    String URL_ARTICLE_LOVE = HOST + "/index.php/index/article/love";

    // 发布糗事
    String URL_EMBARRASSMENT_PUBLISH = HOST + "/index.php/index/embarrassment/publish";
    // 糗事
    String URL_EMBARRASSMENTS = HOST + "/index.php/index/embarrassment/selectEmbarrassments";

    // 发送聊天消息
    String URL_SEND_CHATMSG = HOST + "/index.php/index/chatmessage/add";
    // 收到的所有不同用户的聊天消息
    String URL_LAST_CHATMSGES = HOST + "/index.php/index/chatmessage/selectLastMessages";
    // 当前用户与指定用户的所有聊天消息
    String URL_CHATMSGES = HOST + "/index.php/index/chatmessage/selectMessages";
    // 删除当前用户与指定用户指定时间之前的所有聊天消息
    String URL_DELETE_CHATMSGES = HOST + "/index.php/index/chatmessage/notifyDeleteHistories";

    // 获取融云token
    String URL_RONGCLOUD_IM_TOKEN = HOST + "/index.php/index/rong/getToken";
    // 刷新融云用户资料
    String URL_RONGCLOUD_IM_REFRESH_USERINFO = HOST + "/index.php/index/rong/refresh";

    // 首页数据 (好友关注粉丝用户总数)
    String URL_HOME_INFO = HOST + "/index.php/index/login/info";

    // 万普支付 购买vip通知
    String URL_VIP_NOTIFY = HOST + "/index.php/index/vip/buyedNotify";
    // 万普支付 购买恋恋币通知
    String URL_LOVE_MONEY_NOTIFY = HOST + "/index.php/index/money/buyedNotify";
    // 成功购买vip
    String URL_VIP_BUYED = HOST + "/index.php/index/vip/buyed";
    // 成功购买恋恋币
    String URL_LOVE_MONEY_BUYED = HOST + "/index.php/index/money/buyed";
    
    // 奇闻异事 
    String URL_STRANGENEWS = HOST + "/index.php/index/strange/select";
    // 奇闻异事 
    String URL_STRANGENEWS_DETAIL = HOST + "/index.php/index/strange/detail";

}
