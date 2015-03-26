package me.himi.love;

import java.util.List;

import me.himi.love.entity.Article;
import me.himi.love.entity.ArticleComment;
import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.FansUser;
import me.himi.love.entity.FollowUser;
import me.himi.love.entity.FriendUser;
import me.himi.love.entity.NearbyUser;
import me.himi.love.entity.ReceivedFans;
import me.himi.love.entity.ReceivedQuestion;
import me.himi.love.entity.ReceivedSayHi;
import me.himi.love.entity.RecommendUser;
import me.himi.love.entity.StrangeNews;
import me.himi.love.entity.UserGift;
import me.himi.love.entity.UserNews;
import me.himi.love.entity.VisitedUser;

/**
 * @ClassName:IAppServiceExtend
 * @author sparklee liduanwei_911@163.com
 * @date Nov 20, 2014 4:11:34 PM
 */
public interface IAppServiceExtend {

    void sayhi(SayHiParams params, OnSayHiResponseListener listener);

    public static class SayHiParams {
	public int fromUserId;
	public int toUserId;
	public String content;
    }

    /**
     * 加载打招呼
     * @param postParams
     * @param listener
     */
    void loadSayhi(LoadSayHiParams postParams, OnLoadSayHiResponseListener listener);

    /**
     * 获取3类未读消息的数量
     * @param postParams
     * @param listener
     */
    void loadUnreadMessagesCount(LoadUnreadMessagesCountParams postParams, OnLoadUnreadMessagesCountResponseListener listener);

    public static class LoadUnreadMessagesCountParams {
	public int currentUserId;
    }

    public static class UnreadMessagesCountResponse {
	public int hiCount, fansCount, questionsCount; // 
    }

    public interface OnLoadUnreadMessagesCountResponseListener {
	void onSuccess(UnreadMessagesCountResponse countResponse);

	void onFailure(String errorMsg);
    }

    public static class LoadSayHiParams {
	public int page;
	public int currentUserId;
	public int pageSize;
    }

    public interface OnLoadSayHiResponseListener {
	void onSuccess(List<ReceivedSayHi> sayHiResponses);

	void onFailure(String errorMsg);
    }

    public interface OnSayHiResponseListener {
	void onSuccess(int loveMoney, String msg); // 剩余爱心币

	void onFailure(String msg);
    }

    /**
     * 关注指定用户
     * @param postParams
     * @param listener
     */
    void follow(FollowParams postParams, OnFollowResponseListener listener);

    public static class FollowParams {
	public int followUserId;
    }

    public interface OnFollowResponseListener {
	void onSuccess(int status);

	void onFailure(String errorMsg);
    }

    /**
     * 解除关注
     * @param postParams
     * @param listener
     */
    void unfollow(FollowParams postParams, OnFollowResponseListener listener);

    /**
     * 查看当前用户的所有关注用户
     * @param postParams
     * @param listener
     */
    void loadFollows(LoadFollowParams postParams, OnLoadFollowResponseListener listener);

    public static class LoadFollowParams {
	public int page;
	public int pageSize;
    }

    public interface OnLoadFollowResponseListener {
	void onSuccess(List<NearbyUser> users);

	void onFailure(String errorMsg);
    }

    /**
     * 查看当前用户的所有粉丝用户
     * @param postParams
     * @param listener
     */
    void loadFans(LoadFansParams postParams, OnLoadFansResponseListener listener);

    public static class LoadFansParams {
	public int page;
	public int pageSize;
    }

    public interface OnLoadFansResponseListener {
	void onSuccess(List<NearbyUser> users);

	void onFailure(String errorMsg);
    }

    /**
     * 
     * @param postParams
     * @param listener
     */
    void loadUnreadFans(LoadUnreadFansParams postParams, OnLoadUnreadFansResponseListener listener);

    public static class LoadUnreadFansParams {
	public int page;
	public int pageSize;
    }

    public interface OnLoadUnreadFansResponseListener {
	void onSuccess(List<ReceivedFans> users);

	void onFailure(String errorMsg);
    }

    /**
     * 添加用户的IM资料
     * @param postParams
     * @param listener
     */
    void insertIM(InsertIMParams postParams, OnInsertIMResponseListener listener);

    public static class InsertIMParams {
	// 当前用户的id(注册时还没有登录)
	public int userId;
	public String imUserName, imPassword;
    }

    public interface OnInsertIMResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * (向其他用户)发送问题
     * @param postParams
     * @param listener
     */
    void postQuestion(PostQuestionParams postParams, OnPostQuestionResponseListener listener);

    void loadQuestions(LoadQuestionsPostParams postParams, OnLoadQuestionsResponseListener listener);

    public static class LoadQuestionsPostParams {
	public int page, pageSize;
    }

    public interface OnLoadQuestionsResponseListener {
	void onSuccess(List<ReceivedQuestion> questions);

	void onFailure(String errorMsg);
    }

    public static class PostQuestionParams {
	public int fromUserId, toUserId;
	public String title, option1, option2, option3, option4;
    }

    public interface OnPostQuestionResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * 获取附近的人
     * @param postParams
     * @param listener
     */
    void loadNearbyUsers(PostNearbyUsersParams postParams, OnPostNearbyUsersResponseListener listener);

    public static class PostNearbyUsersParams {
	public int page, pageSize;
	public String longtitude, latitude;

    }

    public interface OnPostNearbyUsersResponseListener {
	void onSuccess(List<NearbyUser> users);

	void onFailure(String errorMsg);
    }

    /**
     * 获取推荐的用户
     * @param postParams
     * @param listener
     */
    void loadRecommendUsers(PostRecommendUsersParams postParams, OnPostRecommendUsersResponseListener listener);

    public static class PostRecommendUsersParams {
	public int page, pageSize;
	public String longtitude, latitude;
    }

    public interface OnPostRecommendUsersResponseListener {
	void onSuccess(List<RecommendUser> users);

	void onFailure(String errorMsg);
    }

    /**
     * 获取与当前登录用户相互关注的用户
     * @param postParams
     * @param listener
     */
    void loadFriends(LoadFriendsPostParams postParams, OnLoadFriendsResponseListener listener);

    public static class LoadFriendsPostParams {
	public int page, pageSize;
	public String longtitude, latitude;
    }

    public interface OnLoadFriendsResponseListener {
	void onSuccess(List<FriendUser> users);

	void onFailure(String errorMsg);
    }

    /**
     * 加载用户最近的留言
     * @param postParams
     * @param listener
     */
    void loadUserNewsCount(LoadUserNewsRecentPostParams postParams, OnLoadUserNewsResponseListener listener);

    public static class LoadUserNewsRecentPostParams {
	public int userId;

    }

    public static class UserNewsInfos {
	public int count;
	public List<BSImageUrl> imageUrls; // 所有发布心情时上传的图片(大图小图)
	public String content;//内容
    }

    public interface OnLoadUserNewsResponseListener {
	void onSuccess(UserNewsInfos infos);

	void onFailure(String errorMsg);
    }

    /**
     * 更新留言(设为不可见,不可评论)
     * @param postParams
     * @param listener
     */
    void updateNews(UpdateNewsPostParams postParams, OnUpdateNewsResponseListener listener);

    public static class UpdateNewsPostParams {
	public int newsId;
	public boolean isPrivate, isAllowComment;

    }

    public interface OnUpdateNewsResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * 删除留言
     * @param postParams
     * @param listener
     */
    void deleteNews(DeleteNewsPostParams postParams, OnDeleteNewsResponseListener listener);

    public static class DeleteNewsPostParams {
	public int newsId;

    }

    public interface OnDeleteNewsResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * 加载最近几位访客
     * @param postParams
     * @param listener
     */
    void loadRecentVisitors(LoadVisitorsPostParams postParams, OnLoadVisitorsResponseListener listener);

    public static class LoadVisitorsPostParams {
	public int userId;
    }

    public interface OnLoadVisitorsResponseListener {
	void onSuccess(UserIdAndFaceInfos infos);

	void onFailure(String errorMsg);
    }

    public static class UserIdAndFaceInfos {
	public int count;
	public List<UserIdAndFace> userFaces;
    }

    public static class UserIdAndFace {
	public int userId;
	public String nickname;
	public BSImageUrl face;
    }

    /**
     * 加载最近的几位粉丝
     * @param postParams
     * @param listener
     */
    void loadRecentFans(LoadRecentFansPostParams postParams, OnLoadRecentFansResponseListener listener);

    public static class LoadRecentFansPostParams {
	public int userId; // 查看目标用户ID
    }

    public interface OnLoadRecentFansResponseListener {
	void onSuccess(UserIdAndFaceInfos infos);

	void onFailure(String errorMsg);
    }

    /**
     * 加载指定用户最近的几位关注用户
     * @param postParams
     * @param listener
     */
    void loadRecentFollows(LoadRecentFollowsPostParams postParams, OnLoadRecentFollowsResponseListener listener);

    public static class LoadRecentFollowsPostParams {
	public int userId; // 查看目标用户ID
    }

    public interface OnLoadRecentFollowsResponseListener {
	void onSuccess(UserIdAndFaceInfos infos);

	void onFailure(String errorMsg);
    }

    /**
     * 签到
     * @param postParams
     * @param listener
     */
    void sigin(SigninPostParams postParams, OnSigninResponseListener listener);

    public static class SigninPostParams {

    }

    public interface OnSigninResponseListener {
	void onSuccess(int rewardMoney, int nowMoney);

	void onFailure(String errorMsg);
    }

    /**
     * 加载当前登录用户的所有好友的留言
     * @param postParams
     * @param listener
     */
    void loadFriendsNews(LoadFriendsNewsPostParams postParams, OnLoadFriendsNewsResponseListener listener);

    public static class LoadFriendsNewsPostParams {
	public int page, pageSize;
	public int userId; // 当前登录用户的ID
    }

    public interface OnLoadFriendsNewsResponseListener {
	void onSuccess(List<UserNews> news);

	void onFailure(String errorMsg);
    }

    /**
     * 加载用户的关注
     * @param postParams
     * @param listener
     */
    void loadUserFollows(LoadFollowsPostParams postParams, OnLoadFollowsResponseListener listener);

    public static class LoadFollowsPostParams {
	public int userId;
	public int page, pageSize;
    }

    public interface OnLoadFollowsResponseListener {
	void onSuccess(List<FollowUser> followUser);

	void onFailure(String errorMsg);
    }

    /**
     * 加载用户的粉丝
     * @param postParams
     * @param listener
     */
    void loadUserFans(LoadFansPostParams postParams, OnLoadUserFansResponseListener listener);

    public static class LoadFansPostParams {
	public int userId;
	public int page, pageSize;
    }

    public interface OnLoadUserFansResponseListener {
	void onSuccess(List<FansUser> fansUser);

	void onFailure(String errorMsg);
    }

    void loadFollowsNews(LoadFollowsNewsPostParams postParams, OnLoadFollowsNewsResponseListener listener);

    /**
     * 加载当前登录用户关注的所有用户的所有留言
     * @author sparklee
     *
     */
    public static class LoadFollowsNewsPostParams {
	public int userId;
	public int page;
	public int pageSize;
    }

    public interface OnLoadFollowsNewsResponseListener {
	void onSuccess(List<UserNews> news);

	void onFailure(String errorMsg);
    }

    /**
     * 发布帖子
     * @param postParams
     * @param listener
     */
    void publishArticle(PublishArticlePostParams postParams, OnLoadPublishArticleResponseListener listener);

    public static class PublishArticlePostParams {
	public boolean isPublic;
	public String content;
	public String backgroundImageUrl;
	public String tag;
	public String longitude, latitude;
	public String address;
    }

    public interface OnLoadPublishArticleResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * 发布帖子评论
     * @param postParams
     * @param listener
     */
    void publishArticleComment(PublishArticleCommentPostParams postParams, OnPublishArticleCommentResponseListener listener);

    public static class PublishArticleCommentPostParams {
	public int articleId;
	public int parentCommentId;
	public String content;
    }

    public interface OnPublishArticleCommentResponseListener {
	void onSuccess(ArticleComment insertedComment);

	void onFailure(String errorMsg);
    }

    /**
     * 加载帖子
     */

    void loadArticles(LoadArticlesPostParams postParams, OnLoadArticlesResponseListener listener);

    public static class LoadArticlesPostParams {
	public int page, pageSize;
	public String order;
    }

    public interface OnLoadArticlesResponseListener {
	void onSuccess(List<Article> secrets);

	void onFailure(String errorMsg);
    }

    /**
     * 加载帖子评论
     */

    void loadArticleComments(LoadArticleCommentsPostParams postParams, OnLoadArticleCommentsResponseListener listener);

    public static class LoadArticleCommentsPostParams {
	public int articleId;
	public int page, pageSize;
    }

    public interface OnLoadArticleCommentsResponseListener {
	void onSuccess(List<ArticleComment> comments);

	void onFailure(String errorMsg);
    }

    /**
     * 赞帖子
     * @param postParams
     * @param listener
     */
    void loveArticle(VoteArticleLovePostParams postParams, OnVoteArticleLoveResponseListener listener);

    public static class VoteArticleLovePostParams {
	public int articleId;
    }

    public interface OnVoteArticleLoveResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /***
     * 加载指定用户访问过的用户
     * @param postParams
     * @param listener
     */
    void loadVisitedUsers(LoadVisitedUsersPostParams postParams, OnLoadVisitedUsersResponseListener listener);

    public static class LoadVisitedUsersPostParams {
	public int userId;
	public int page, pageSize;
    }

    public interface OnLoadVisitedUsersResponseListener {
	void onSuccess(List<VisitedUser> users);

	void onFailure(String errorMsg);
    }

    /**
     * 加载当前登录用户的首页资料(好友,粉丝,关注各用户数)
     * @param postParams
     * @param listener
     */
    void loadHomeInfo(LoadHomeInfoPostParams postParams, OnLoadHomeInfoResponseListener listener);

    public static class LoadHomeInfoPostParams {

    }

    public interface OnLoadHomeInfoResponseListener {
	void onSuccess(HomeInfo counts);

	void onFailure(String errorMsg);
    }

    public static class HomeInfo {
	public boolean isVip, isLock, signed;
	public int vipExpireTime, unlockTime, lastLoginTime;
	public int gender;

	public int loveMoney;

	public String nickname, faceUrl;
	public int friends, follows, fans;
    }

    /**
     * 获取指定用户的简单资料
     * @param postParams
     * @param listener
     */
    void loadUserSimpleInfo(LoadUserInfoPostParams postParams, OnLoadUserInfoResponseListener listener);

    public static class LoadUserInfoPostParams {
	public String userId;
    }

    public interface OnLoadUserInfoResponseListener {
	void onSuccess(SimpleUserInfo userInfo);

	void onFailure(String errorMsg);
    }

    public static class SimpleUserInfo {

    }

    /**
     * 加载奇闻帖子
     */

    void loadStrangeNews(LoadStrangeNewsPostParams postParams, OnLoadStrangeNewsResponseListener listener);

    public static class LoadStrangeNewsPostParams {
	public int page, pageSize;
	public String order = "addtime DESC";

    }

    public static interface OnLoadStrangeNewsResponseListener {
	void onSuccess(List<StrangeNews> news);

	void onFailure(String errorMsg);
    }

    /**
     * 赠送礼物
     * @param postParams
     * @param listener
     */
    void giveGift(GiveGiftPostParams postParams, OnGiveGiftResponseListener listener);

    public static class GiveGiftPostParams {
	public String giftId;
	public String toUserId;
	public String word;
    }

    public static interface OnGiveGiftResponseListener {
	void onSuccess(UserGift userGift);

	void onFailure(String errorMsg);
    }
}
