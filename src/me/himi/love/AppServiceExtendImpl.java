package me.himi.love;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.himi.love.entity.Article;
import me.himi.love.entity.ArticleComment;
import me.himi.love.entity.BSImageUrl;
import me.himi.love.entity.FansUser;
import me.himi.love.entity.FollowUser;
import me.himi.love.entity.FriendUser;
import me.himi.love.entity.Gift;
import me.himi.love.entity.NearbyUser;
import me.himi.love.entity.ReceivedFans;
import me.himi.love.entity.ReceivedQuestion;
import me.himi.love.entity.ReceivedSayHi;
import me.himi.love.entity.RecommendUser;
import me.himi.love.entity.StrangeNews;
import me.himi.love.entity.UserGift;
import me.himi.love.entity.UserNews;
import me.himi.love.entity.VisitedUser;
import me.himi.love.entity.loader.impl.NearbyUserLoaderImpl;
import me.himi.love.entity.loader.impl.ReceivedFansLoaderImpl;
import me.himi.love.entity.loader.impl.SayHiResponseLoaderImpl;
import me.himi.love.entity.loader.impl.UserNewsLoaderImpl;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:AppServiceExtend
 * @author sparklee liduanwei_911@163.com
 * @date Nov 20, 2014 4:13:19 PM
 */
public class AppServiceExtendImpl implements IAppServiceExtend {

    private static IAppServiceExtend instance;

    private AppServiceExtendImpl() {

    }

    public synchronized static IAppServiceExtend getInstance() {
	if (null == instance) {
	    instance = new AppServiceExtendImpl();
	}
	return instance;
    }

    @Override
    public void follow(FollowParams postParams, final OnFollowResponseListener listener) {
	// TODO Auto-generated method stub

	String url = Constants.URL_USER_FOLLOW_ADD;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("follow_user_id", postParams.followUserId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess(status);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void loadFollows(LoadFollowParams postParams, final OnLoadFollowResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_CURRENT_USER_FOLLOWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("关注用户: " + res);
		List<NearbyUser> users = new NearbyUserLoaderImpl().load(res);
		listener.onSuccess(users);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadFans(LoadFansParams postParams, final OnLoadFansResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_CURRENT_USER_FANS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		List<NearbyUser> users = new NearbyUserLoaderImpl().load(res);
		listener.onSuccess(users);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadSayhi(LoadSayHiParams postParams, final OnLoadSayHiResponseListener listener) {
	// TODO Auto-generated method stub

	String url = Constants.URL_USER_SAYHI_LIST;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.currentUserId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		List<ReceivedSayHi> users = new SayHiResponseLoaderImpl().load(res);
		listener.onSuccess(users);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadUnreadMessagesCount(LoadUnreadMessagesCountParams postParams, final OnLoadUnreadMessagesCountResponseListener listener) {
	// TODO Auto-generated method stub

	String url = Constants.URL_USER_MESSAGE_COUNT;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.currentUserId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int hiCount = jsonObj.getInt("hi");
		    int fansCount = jsonObj.getInt("fans");
		    int questionsCount = jsonObj.getInt("questions");
		    UnreadMessagesCountResponse countResponse = new UnreadMessagesCountResponse();
		    countResponse.hiCount = hiCount;
		    countResponse.fansCount = fansCount;
		    countResponse.questionsCount = questionsCount;
		    listener.onSuccess(countResponse);
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void sayhi(SayHiParams postParams, final OnSayHiResponseListener listener) {
	// TODO Auto-generated method stub

	String url = Constants.URL_USER_SAYHI;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("from_user_id", postParams.fromUserId + "");
	nameAndValues.put("to_user_id", postParams.toUserId + "");
	nameAndValues.put("content", postParams.content);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		try {
		    System.out.println("打招呼: " + res);
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    String msg = jsonObj.getString("msg");
		    int money = jsonObj.getInt("money");
		    listener.onSuccess(money, msg);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("打招呼失败");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void loadUnreadFans(LoadUnreadFansParams postParams, final OnLoadUnreadFansResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_UNREAD_FANS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		List<ReceivedFans> users = new ReceivedFansLoaderImpl().load(res);
		listener.onSuccess(users);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void unfollow(FollowParams postParams, final OnFollowResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_FOLLOW_DELETE;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("follow_user_id", postParams.followUserId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess(status);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void insertIM(InsertIMParams postParams, final OnInsertIMResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_INSERT_IM;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");
	nameAndValues.put("im_username", postParams.imUserName + "");
	nameAndValues.put("im_password", postParams.imPassword + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess();
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void postQuestion(PostQuestionParams postParams, final OnPostQuestionResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_POST_QUESTION;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("from_user_id", postParams.fromUserId + "");
	nameAndValues.put("to_user_id", postParams.toUserId + "");
	nameAndValues.put("title", postParams.title);
	nameAndValues.put("option1", postParams.option1);
	nameAndValues.put("option2", postParams.option2);
	nameAndValues.put("option3", postParams.option3);
	nameAndValues.put("option4", postParams.option4);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess();
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadQuestions(LoadQuestionsPostParams postParams, final OnLoadQuestionsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_LOAD_QUESTIONS;

	RequestParams params = new RequestParams();
	params.put("page", postParams.page);
	params.put("pageSize", postParams.pageSize);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONArray jsonArr = new JSONArray(res);

		    List<ReceivedQuestion> receivedQuestions = new ArrayList<ReceivedQuestion>();

		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);

			String title = jsonObj.getString("title");
			String option1 = jsonObj.getString("option1");
			String option2 = jsonObj.getString("option2");
			String option3 = jsonObj.getString("option3");
			String option4 = jsonObj.getString("option4");

			int isRead = jsonObj.getInt("is_read");

			int addtime = jsonObj.getInt("addtime");
			int userId = jsonObj.getInt("user_id");
			String nickname = jsonObj.getString("nickname");
			int gender = jsonObj.getInt("gender");
			String faceUrl = jsonObj.getString("face_url");
			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			ReceivedQuestion question = new ReceivedQuestion();
			question.setUserId(userId);
			question.setTitle(title);
			question.setFaceUrl(faceUrl);
			question.setGender(gender);
			question.setNickname(nickname);
			question.setRead(isRead == 1);

			List<String> options = new ArrayList<String>();
			options.add(option1);
			options.add(option2);
			options.add(option3);
			options.add(option4);

			question.setTime(addtime);
			question.setTimeStr(ActivityUtil.convertTimeToString(addtime * 1000L));
			question.setOptions(options);

			receivedQuestions.add(question);
		    }

		    listener.onSuccess(receivedQuestions);
		    return;

		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		listener.onFailure("参数错误");
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadNearbyUsers(PostNearbyUsersParams postParams, final OnPostNearbyUsersResponseListener listener) {
	// TODO Auto-generated method stub

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");
	nameAndValues.put("longtitude", postParams.longtitude);
	nameAndValues.put("latitude", postParams.latitude);

	RequestParams params = new RequestParams(nameAndValues);

	HttpUtil.post(Constants.URL_NEARBY_USER, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String response = new String(arg2);
		System.out.println("附近的人:" + response);
		List<NearbyUser> users = new ArrayList<NearbyUser>();

		try {
		    JSONArray jsonArr = new JSONArray(response);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");
			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			// vip类型, 主要两大类: 包月(1个月,3个月,6个月),包年
			int isVip = userJsonObj.getInt("is_vip");

			String nickname = userJsonObj.getString("nickname");
			int height = userJsonObj.getInt("height");
			int weight = userJsonObj.getInt("weight");
			String monthlySalary = userJsonObj.getString("monthly_salary");

			String homeplace = userJsonObj.getString("homeplace");
			String address = userJsonObj.getString("address");

			// 兴趣,个性
			String interests = userJsonObj.getString("interests");
			String peronality = userJsonObj.getString("personality");

			// 距离
			String distance = userJsonObj.getString("distance");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

			NearbyUser user = new NearbyUser();
			users.add(user);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			user.setUserId(userId);
			user.setGender(gender);
			user.setAge(age);

			user.setVip(isVip);

			user.setNickname(nickname);
			user.setHeight(height);
			user.setWeight(weight);
			user.setMonthlySalary(monthlySalary);
			user.setHomeplace(homeplace);
			user.setAddress(address);

			user.setInterests(interests);
			user.setPersonality(peronality);

			user.setDistance(distance);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}

			BSImageUrl bsImageUrl = new BSImageUrl();
			bsImageUrl.setBigImageUrl(faceUrl);
			bsImageUrl.setSmallImageUrl(getSmallImageUrl(faceUrl));
			user.setFaceUrl(bsImageUrl);
		    }

		} catch (JSONException e) {
		    e.printStackTrace();
		}

		if (users == null) {
		    listener.onFailure("参数错误");
		    return;
		}

		listener.onSuccess(users);
	    }

	});

    }

    @Override
    public void loadFriends(LoadFriendsPostParams postParams, final OnLoadFriendsResponseListener listener) {
	// TODO Auto-generated method stub
	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");
	nameAndValues.put("longtitude", postParams.longtitude);
	nameAndValues.put("latitude", postParams.latitude);

	RequestParams params = new RequestParams(nameAndValues);
	HttpUtil.post(Constants.URL_FRIENDS_USER, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String response = new String(arg2);

		System.out.println("好友:" + response);
		List<FriendUser> users = new ArrayList<FriendUser>();

		try {
		    JSONArray jsonArr = new JSONArray(response);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");
			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			// vip类型, 主要两大类: 包月(1个月,3个月,6个月),包年(放弃采用,改为统一vip加时)
			int isVip = userJsonObj.getInt("is_vip");

			String nickname = userJsonObj.getString("nickname");
			int height = userJsonObj.getInt("height");
			int weight = userJsonObj.getInt("weight");
			String monthlySalary = userJsonObj.getString("monthly_salary");

			String homeplace = userJsonObj.getString("homeplace");
			String address = userJsonObj.getString("address");

			// 兴趣,个性
			String interests = userJsonObj.getString("interests");
			String peronality = userJsonObj.getString("personality");

			// 距离
			String distance = userJsonObj.getString("distance");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

			FriendUser user = new FriendUser();
			users.add(user);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			user.setUserId(userId);
			user.setGender(gender);
			user.setAge(age);

			user.setVip(isVip);

			user.setNickname(nickname);
			user.setHeight(height);
			user.setWeight(weight);
			user.setMonthlySalary(monthlySalary);
			user.setHomeplace(homeplace);
			user.setAddress(address);

			user.setInterests(interests);
			user.setPersonality(peronality);

			user.setDistance(distance);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}

			BSImageUrl bsImageUrl = new BSImageUrl();
			bsImageUrl.setBigImageUrl(faceUrl);
			bsImageUrl.setSmallImageUrl(getSmallImageUrl(faceUrl));
			user.setFaceUrl(bsImageUrl);
		    }

		} catch (JSONException e) {
		    e.printStackTrace();
		}

		if (users == null) {
		    listener.onFailure("参数错误");
		    return;
		}

		listener.onSuccess(users);
	    }

	});

    }

    @Override
    public void loadUserNewsCount(LoadUserNewsRecentPostParams postParams, final OnLoadUserNewsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_RECENT_NEWS_INFOS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("留言:" + res);

		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    int count = jsonObj.getInt("count");

		    JSONObject newsJsonObj = jsonObj.getJSONObject("news");
		    String content = newsJsonObj.getString("content");

		    List<BSImageUrl> urls = new ArrayList<BSImageUrl>();

		    for (int i = 0; i < 10; ++i) {
			String name = "p" + (i + 1);
			if (newsJsonObj.isNull(name)) {
			    continue;
			}
			String bigImageUrl = newsJsonObj.getString(name);
			if (bigImageUrl.startsWith(".")) {
			    bigImageUrl = Constants.HOST + bigImageUrl.substring(1);
			}

			String smallImageUrl = getSmallImageUrl(bigImageUrl);
			BSImageUrl bsImageUrl = new BSImageUrl();
			bsImageUrl.setBigImageUrl(bigImageUrl);
			bsImageUrl.setSmallImageUrl(smallImageUrl);
			urls.add(bsImageUrl);
		    }

		    UserNewsInfos newsInfos = new UserNewsInfos();
		    newsInfos.count = count;
		    newsInfos.content = content;
		    newsInfos.imageUrls = urls;

		    listener.onSuccess(newsInfos);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    /**
     * 根据大图地址获取php服务器上的小图地址s_
     * @param bigImageUrl
     * @return
     */
    public static String getSmallImageUrl(String bigImageUrl) {
	// TODO Auto-generated method stub
	int nameSplitIndex = bigImageUrl.lastIndexOf("/");
	String path = bigImageUrl.substring(0, nameSplitIndex + 1);
	String name = bigImageUrl.substring(nameSplitIndex + 1);
	name = "s_" + name;
	return path + name;
    }

    @Override
    public void updateNews(UpdateNewsPostParams postParams, final OnUpdateNewsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_UPDATE_USER_NEWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("news_id", postParams.newsId + "");
	nameAndValues.put("is_private", (postParams.isPrivate ? 1 : 0) + "");
	nameAndValues.put("is_allow_comment", (postParams.isAllowComment ? 1 : 0) + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("修改留言:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess();
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure(arg0 + new String(arg2) + "连接超时" + arg3.getMessage());
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void deleteNews(DeleteNewsPostParams postParams, final OnDeleteNewsResponseListener listener) {
	String url = Constants.URL_DELETE_USER_NEWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();

	nameAndValues.put("news_id", postParams.newsId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("删除留言:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess();
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure(arg0 + new String(arg2) + "连接超时" + arg3.getMessage());
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadRecentVisitors(LoadVisitorsPostParams postParams, final OnLoadVisitorsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_RECENT_USER_VISITORS;

	Map<String, String> nameAndValues = new HashMap<String, String>();

	nameAndValues.put("user_id", postParams.userId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("最近访客:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    UserIdAndFaceInfos infos = new UserIdAndFaceInfos();
		    infos.count = jsonObj.getInt("count");
		    JSONArray jsonArray = jsonObj.getJSONArray("visitors");
		    infos.userFaces = new ArrayList<UserIdAndFace>();

		    System.out.println("len:" + jsonArray.length());
		    for (int i = 0, n = jsonArray.length(); i < n; ++i) {
			JSONObject visitorJson = jsonArray.getJSONObject(i);
			UserIdAndFace visitorFace = new UserIdAndFace();
			visitorFace.userId = visitorJson.getInt("visitor_id");
			visitorFace.nickname = visitorJson.getString("nickname");
			String faceUrl = visitorJson.getString("face_url");
			visitorFace.face = new BSImageUrl();
			if (faceUrl.startsWith(".")) {
			    faceUrl = Constants.HOST + faceUrl.substring(1); // 去掉.
			}
			visitorFace.face.setBigImageUrl(faceUrl);
			visitorFace.face.setSmallImageUrl(getSmallImageUrl(faceUrl));
			infos.userFaces.add(visitorFace);
		    }

		    listener.onSuccess(infos);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		//		listener.onFailure(arg0 + new String(arg2) + "连接超时" + arg3.getMessage());
		listener.onFailure("加载访客失败,连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void sigin(SigninPostParams postParams, final OnSigninResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_SIGNIN;

	Map<String, String> nameAndValues = new HashMap<String, String>();

	//	nameAndValues.put("user_id", postParams.userId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("签到:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    int reward = jsonObj.getInt("reward");
		    int money = jsonObj.getInt("money");
		    listener.onSuccess(reward, money);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
		//		listener.onFailure(arg0 + new String(arg2) + "连接超时" + arg3.getMessage());
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadRecentFans(LoadRecentFansPostParams postParams, final OnLoadRecentFansResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_RECENT_USER_FANS;

	Map<String, String> nameAndValues = new HashMap<String, String>();

	nameAndValues.put("user_id", postParams.userId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("最近粉丝:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    UserIdAndFaceInfos infos = new UserIdAndFaceInfos();
		    infos.count = jsonObj.getInt("count");
		    JSONArray jsonArray = jsonObj.getJSONArray("fans");
		    infos.userFaces = new ArrayList<UserIdAndFace>();

		    System.out.println("len:" + jsonArray.length());
		    for (int i = 0, n = jsonArray.length(); i < n; ++i) {
			JSONObject visitorJson = jsonArray.getJSONObject(i);
			UserIdAndFace fansFace = new UserIdAndFace();
			fansFace.userId = visitorJson.getInt("fans_id");
			fansFace.nickname = visitorJson.getString("nickname");
			String faceUrl = visitorJson.getString("face_url");
			fansFace.face = new BSImageUrl();
			if (faceUrl.startsWith(".")) {
			    faceUrl = Constants.HOST + faceUrl.substring(1); // 去掉.
			}
			fansFace.face.setBigImageUrl(faceUrl);
			fansFace.face.setSmallImageUrl(getSmallImageUrl(faceUrl));
			infos.userFaces.add(fansFace);
		    }

		    listener.onSuccess(infos);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		//		listener.onFailure(arg0 + new String(arg2) + "连接超时" + arg3.getMessage());
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void loadRecentFollows(LoadRecentFollowsPostParams postParams, final OnLoadRecentFollowsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_RECENT_USER_FOLLOWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();

	nameAndValues.put("user_id", postParams.userId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("最近关注:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    UserIdAndFaceInfos infos = new UserIdAndFaceInfos();
		    infos.count = jsonObj.getInt("count");
		    JSONArray jsonArray = jsonObj.getJSONArray("follows");
		    infos.userFaces = new ArrayList<UserIdAndFace>();

		    System.out.println("len:" + jsonArray.length());
		    for (int i = 0, n = jsonArray.length(); i < n; ++i) {
			JSONObject visitorJson = jsonArray.getJSONObject(i);
			UserIdAndFace fansFace = new UserIdAndFace();
			fansFace.userId = visitorJson.getInt("follow_id");
			fansFace.nickname = visitorJson.getString("nickname");
			String faceUrl = visitorJson.getString("face_url");
			fansFace.face = new BSImageUrl();
			if (faceUrl.startsWith(".")) {
			    faceUrl = Constants.HOST + faceUrl.substring(1); // 去掉.
			}
			fansFace.face.setBigImageUrl(faceUrl);
			fansFace.face.setSmallImageUrl(getSmallImageUrl(faceUrl));
			infos.userFaces.add(fansFace);
		    }

		    listener.onSuccess(infos);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadFriendsNews(LoadFriendsNewsPostParams postParams, final OnLoadFriendsNewsResponseListener listener) {
	// TODO Auto-generated method stub

	String url = Constants.URL_FOLLOWS_NEWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		List<UserNews> userNewsList = new UserNewsLoaderImpl().load(res);
		listener.onSuccess(userNewsList);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadUserFollows(LoadFollowsPostParams postParams, final OnLoadFollowsResponseListener listener) {
	String url = Constants.URL_USER_FOLLOWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("加载关注:" + res);
		List<FollowUser> users = new ArrayList<FollowUser>();
		try {
		    JSONArray jsonArr = new JSONArray(res);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");

			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			String nickname = userJsonObj.getString("nickname");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "";

			FollowUser user = new FollowUser();
			users.add(user);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			user.setUserId(userId);
			user.setGender(gender);
			user.setAge(age);
			user.setNickname(nickname);
			user.setFaceUrl(faceUrl);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			user.setFaceUrl(faceUrl);
			int addTime = userJsonObj.getInt("addtime");
			String addTimeStr = ActivityUtil.convertTimeToString(addTime * 1000L);
			user.setAddTime(addTimeStr);
		    }
		    listener.onSuccess(users);

		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadUserFans(LoadFansPostParams postParams, final OnLoadUserFansResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_FANS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("加载粉丝:" + res);
		List<FansUser> users = new ArrayList<FansUser>();
		try {
		    JSONArray jsonArr = new JSONArray(res);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");

			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			String nickname = userJsonObj.getString("nickname");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "";

			FansUser user = new FansUser();
			users.add(user);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			user.setUserId(userId);
			user.setGender(gender);
			user.setAge(age);
			user.setNickname(nickname);
			user.setFaceUrl(faceUrl);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			user.setFaceUrl(faceUrl);
			int addTime = userJsonObj.getInt("addtime");
			String addTimeStr = ActivityUtil.convertTimeToString(addTime * 1000L);
			user.setAddTime(addTimeStr);
		    }
		    listener.onSuccess(users);

		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadFollowsNews(LoadFollowsNewsPostParams postParams, final OnLoadFollowsNewsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_FOLLOWS_NEWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		List<UserNews> newsList = new ArrayList<UserNews>();

		System.out.println("follows news:" + res);

		try {

		    JSONArray newsArray = new JSONArray(res);

		    int len = newsArray.length();
		    for (int i = 0; i < len; ++i) {
			JSONObject news = (JSONObject) newsArray.get(i);

			int userId = news.getInt("user_id");
			int gender = news.getInt("gender");
			String nickname = news.getString("nickname");
			String faceUrl = news.getString("face_url");
			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			int birthday = news.getInt("birthday");

			int newsId = news.getInt("id");
			String title = news.getString("title");
			String content = news.getString("content");
			int postTime = news.getInt("post_time");

			int isPrivate = news.getInt("is_private");
			int isAllowComment = news.getInt("is_allow_comment");

			String post_longitude = news.getString("post_longitude");
			String post_latitude = news.getString("post_latitude");

			String address = news.getString("address");

			int commentsCount = news.getInt("comments");

			UserNews un = new UserNews();
			newsList.add(un);
			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int nowYear = nowCalendar.get(Calendar.YEAR);

			int age = nowYear - birthdayCalendar.get(Calendar.YEAR);
			// 修正 出生月份是否达到
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			// 必须加 L, 否则会被强转成 int 造成变负数
			String postTimeStr = ActivityUtil.convertTimeToString(postTime * 1000L);

			un.setNewsId(newsId);
			un.setAge(age);
			un.setGender(gender);
			un.setNickname(nickname);
			un.setFaceUrl(faceUrl);
			un.setUserId(userId);
			un.setTitle(title);
			un.setContent(content);
			un.setPostTime(postTimeStr);
			un.setPostLongitude(post_longitude);
			un.setPostLatitude(post_latitude);
			un.setAddress(address);

			un.setPrivate(isPrivate == 1);
			un.setAllowComment(isAllowComment == 1);

			un.setCommentsCount(commentsCount);

			un.setImageUrls(new ArrayList<BSImageUrl>());

			for (int t = 1; t <= 10; ++t) {
			    String name = "img" + (t <= 9 ? "0" + t : t + "");
			    if (!news.isNull(name)) {
				String bigImageUrl = news.getString(name);
				if (bigImageUrl.startsWith(".")) {
				    bigImageUrl = Constants.HOST + bigImageUrl.substring(1);
				}
				String smallImageUrl = getSmallImageUrl(bigImageUrl);

				//			Strng smallImageUrl = bigImageUrl.substring(bigImageUrl.lastIndexOf("/"))
				BSImageUrl imageUrl = new BSImageUrl();
				imageUrl.setBigImageUrl(bigImageUrl);
				imageUrl.setSmallImageUrl(smallImageUrl);
				un.getImageUrls().add(imageUrl);
				System.out.println("小图:" + smallImageUrl);

			    }
			}
		    }

		    listener.onSuccess(newsList);

		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void publishArticle(PublishArticlePostParams postParams, final OnLoadPublishArticleResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_ARTICLE_PUBLISH;
	Map<String, String> nameAndValues = new HashMap<String, String>();

	nameAndValues.put("is_public", (postParams.isPublic ? 1 : 0) + "");
	nameAndValues.put("content", postParams.content);
	nameAndValues.put("back", postParams.backgroundImageUrl);
	nameAndValues.put("tag", postParams.tag);
	nameAndValues.put("lng", postParams.longitude);
	nameAndValues.put("lat", postParams.latitude);
	nameAndValues.put("addr", postParams.address);

	RequestParams params = new RequestParams(nameAndValues);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("发布秘密:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);

		    int status = jsonObj.getInt("status");
		    if (status == 0) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess();

		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("发布失败,参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadArticles(LoadArticlesPostParams postParams, final OnLoadArticlesResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_ARTICLES;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");
	nameAndValues.put("order", postParams.order);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("文章: " + res);
		List<Article> secrets = new ArrayList<Article>();
		try {
		    JSONArray jsonArr = new JSONArray(res);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject rowObj = jsonArr.getJSONObject(i);

			JSONObject secretJsonObj = rowObj.getJSONObject("data");

			int id = secretJsonObj.getInt("id");
			int love = secretJsonObj.getInt("love");
			int hate = secretJsonObj.getInt("hate");
			int comments = secretJsonObj.getInt("comments");

			boolean isPublic = secretJsonObj.getInt("is_public") == 1;
			String content = secretJsonObj.getString("content");

			// 内容 大小配图
			String contentImageUrl = null;
			String contentSmallImageUrl = null;
			if (!secretJsonObj.isNull("back")) {
			    contentImageUrl = secretJsonObj.getString("back");
			    if (contentImageUrl.startsWith(".")) { // 本服务器上的图片
				contentImageUrl = Constants.HOST + contentImageUrl.substring(1);
				contentSmallImageUrl = UserNewsLoaderImpl.getSmallImageUrl(contentImageUrl);
			    } else { // 图片url,其他网络服务器上的图片
				contentSmallImageUrl = contentImageUrl; // 小图和大图一样
			    }
			}

			String tag = secretJsonObj.getString("tag");
			int createTime = secretJsonObj.getInt("time");

			JSONObject userJsonObj = rowObj.getJSONObject("user");

			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");
			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			// vip类型, 主要两大类: 包月(1个月,3个月,6个月),包年
			int isVip = userJsonObj.getInt("is_vip");

			String nickname = userJsonObj.getString("nickname");
			int height = userJsonObj.getInt("height");
			int weight = userJsonObj.getInt("weight");
			String monthlySalary = userJsonObj.getString("monthly_salary");

			String homeplace = userJsonObj.getString("homeplace");
			String address = userJsonObj.getString("address");

			// 兴趣,个性
			String interests = userJsonObj.getString("interests");
			String peronality = userJsonObj.getString("personality");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

			Article secret = new Article();
			secrets.add(secret);

			if (contentImageUrl.startsWith(".")) {
			    contentImageUrl = Constants.HOST + contentImageUrl.substring(1);
			}

			secret.setId(id);
			secret.setLove(love);
			secret.setHate(hate);
			secret.setComments(comments);

			secret.setContentImageUrl(new BSImageUrl(contentImageUrl, contentSmallImageUrl));
			secret.setContent(content);
			secret.setCreateTime(createTime);
			secret.setPublic(isPublic);
			secret.setTag(tag);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			secret.setUserId(userId);
			secret.setGender(gender);
			secret.setAge(age);

			secret.setVip(isVip);

			secret.setNickname(nickname);
			secret.setHeight(height);
			secret.setWeight(weight);
			secret.setMonthlySalary(monthlySalary);
			secret.setHomeplace(homeplace);
			secret.setAddress(address);

			secret.setInterests(interests);
			secret.setPersonality(peronality);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}

			BSImageUrl bsImageUrl = new BSImageUrl();
			bsImageUrl.setBigImageUrl(faceUrl);
			bsImageUrl.setSmallImageUrl(getSmallImageUrl(faceUrl));
			secret.setFaceUrl(bsImageUrl);
		    }
		    listener.onSuccess(secrets);

		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadVisitedUsers(LoadVisitedUsersPostParams postParams, final OnLoadVisitedUsersResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_VISITED_USERS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", postParams.userId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("看过谁: " + res);
		try {
		    JSONArray jsonArr = new JSONArray(res);

		    List<VisitedUser> users = new ArrayList<VisitedUser>();

		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");

			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			String nickname = userJsonObj.getString("nickname");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "";

			VisitedUser user = new VisitedUser();
			users.add(user);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			user.setUserId(userId);
			user.setGender(gender);
			user.setAge(age);
			user.setNickname(nickname);
			user.setFaceUrl(faceUrl);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			user.setFaceUrl(faceUrl);
			int visitTime = userJsonObj.getInt("time");
			String visitTimeStr = ActivityUtil.convertTimeToString(visitTime * 1000L);
			user.setVisitTime(visitTimeStr);
		    }

		    listener.onSuccess(users);

		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void publishArticleComment(PublishArticleCommentPostParams postParams, final OnPublishArticleCommentResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_ARTICLE_COMMENT_PUBLISH;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("article_id", postParams.articleId + "");
	nameAndValues.put("pid", postParams.parentCommentId + "");
	nameAndValues.put("content", postParams.content);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("发布文章评论:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    if (jsonObj.getInt("status") == 0) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    int id = jsonObj.getInt("id");
		    int userId = jsonObj.getInt("user_id");
		    String faceUrl = jsonObj.getString("face_url");
		    String nickname = jsonObj.getString("nickname");
		    String content = jsonObj.getString("content");
		    int time = jsonObj.getInt("time");

		    String replyUser = null;
		    String replyUserContent = null;
		    if (!jsonObj.isNull("reply_user")) {
			replyUser = jsonObj.getString("reply_user");
			replyUserContent = jsonObj.getString("reply_content");
		    }

		    ArticleComment comment = new ArticleComment(); // 返回已发布成功的评论
		    comment.setId(id);
		    comment.setUserId(userId);
		    if (faceUrl.startsWith(".")) {
			faceUrl = Constants.HOST + faceUrl.substring(1);
		    }
		    comment.setFaceUrl(faceUrl);
		    comment.setNickname(nickname);
		    comment.setContent(content);
		    comment.setPostTime(ActivityUtil.convertTimeToString(time * 1000L));
		    comment.setReplyUser(replyUser);
		    comment.setReplyUserCommentContent(replyUserContent);
		    listener.onSuccess(comment);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadArticleComments(LoadArticleCommentsPostParams postParams, final OnLoadArticleCommentsResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_ARTICLE_COMMENTS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("article_id", postParams.articleId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("文章评论:" + res);
		try {
		    JSONArray jsonArr = new JSONArray(res);

		    List<ArticleComment> comments = new ArrayList<ArticleComment>();
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			int id = jsonObj.getInt("id");
			int userId = jsonObj.getInt("user_id");
			String faceUrl = jsonObj.getString("face_url");
			String nickname = jsonObj.getString("nickname");
			String content = jsonObj.getString("content");
			int time = jsonObj.getInt("time");

			String replyUser = null;
			String replyUserContent = null;
			if (!jsonObj.isNull("reply_user")) {
			    replyUser = jsonObj.getString("reply_user");
			    replyUserContent = jsonObj.getString("reply_content");
			}

			ArticleComment comment = new ArticleComment(); // 返回已发布成功的评论
			comments.add(comment);

			comment.setId(id);
			comment.setUserId(userId);
			if (faceUrl.startsWith(".")) {
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			comment.setFaceUrl(faceUrl);
			comment.setNickname(nickname);
			comment.setContent(content);
			comment.setPostTime(ActivityUtil.convertTimeToString(time * 1000L));
			comment.setReplyUser(replyUser);
			comment.setReplyUserCommentContent(replyUserContent);

		    }

		    listener.onSuccess(comments);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loveArticle(VoteArticleLovePostParams postParams, final OnVoteArticleLoveResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_ARTICLE_LOVE;
	RequestParams params = new RequestParams();
	params.put("article_id", postParams.articleId);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("顶文章:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);

		    int status = jsonObj.getInt("status");
		    if (status == 0) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }

		    listener.onSuccess();

		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("顶失败,参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadHomeInfo(LoadHomeInfoPostParams postParams, final OnLoadHomeInfoResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_HOME_INFO;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	//	nameAndValues.put("follow_user_id", postParams.followUserId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("loadHomeInfo:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);

		    boolean signed = jsonObj.getInt("signed") == 1;
		    boolean isVip = jsonObj.getInt("is_vip") == 1;
		    int vipExpireTime = jsonObj.getInt("vip_expire_time");
		    int loveMoney = jsonObj.getInt("love_money");
		    boolean isLock = jsonObj.getInt("is_lock") == 1;
		    int unlockTime = jsonObj.getInt("unlock_time");
		    int lastLoginTime = jsonObj.getInt("lastlogin_time");

		    int friendsCount = jsonObj.getInt("friends_count");
		    int followsCount = jsonObj.getInt("follows_count");
		    int fansCount = jsonObj.getInt("fans_count");

		    String nickname = jsonObj.getString("nickname");
		    String faceUrl = jsonObj.getString("face_url");
		    int gender = jsonObj.getInt("gender");

		    HomeInfo homeInfo = new HomeInfo();

		    homeInfo.signed = signed;
		    homeInfo.isVip = isVip;
		    homeInfo.vipExpireTime = vipExpireTime;
		    homeInfo.isLock = isLock;
		    homeInfo.unlockTime = unlockTime;
		    homeInfo.loveMoney = loveMoney;
		    homeInfo.lastLoginTime = lastLoginTime;

		    homeInfo.fans = fansCount;
		    homeInfo.friends = friendsCount;
		    homeInfo.follows = followsCount;

		    homeInfo.nickname = nickname;
		    homeInfo.faceUrl = faceUrl;
		    homeInfo.gender = gender;
		    listener.onSuccess(homeInfo);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadUserSimpleInfo(LoadUserInfoPostParams postParams, final OnLoadUserInfoResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_SIMPLE_INFO;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("target_user_id", postParams.userId);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("loadSimpleUserInfo:" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);

		    SimpleUserInfo userInfo = new SimpleUserInfo();

		    listener.onSuccess(userInfo);
		    return;
		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void loadRecommendUsers(PostRecommendUsersParams postParams, final OnPostRecommendUsersResponseListener listener) {
	// TODO Auto-generated method stub
	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");
	nameAndValues.put("longtitude", postParams.longtitude);
	nameAndValues.put("latitude", postParams.latitude);

	RequestParams params = new RequestParams(nameAndValues);

	HttpUtil.post(Constants.URL_RECOMMEND_USER, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String response = new String(arg2);
		System.out.println("推荐用户:" + response);
		List<RecommendUser> users = new ArrayList<RecommendUser>();

		try {
		    JSONArray jsonArr = new JSONArray(response);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject userJsonObj = (JSONObject) jsonArr.get(i);
			int userId = userJsonObj.getInt("user_id");
			int gender = userJsonObj.getInt("gender");
			int birthday = userJsonObj.getInt("birthday"); // 秒时间

			// vip类型, 主要两大类: 包月(1个月,3个月,6个月),包年
			int isVip = userJsonObj.getInt("is_vip");

			String nickname = userJsonObj.getString("nickname");
			int height = userJsonObj.getInt("height");
			int weight = userJsonObj.getInt("weight");
			String monthlySalary = userJsonObj.getString("monthly_salary");

			String homeplace = userJsonObj.getString("homeplace");
			String address = userJsonObj.getString("address");

			// 兴趣,个性
			String interests = userJsonObj.getString("interests");
			String peronality = userJsonObj.getString("personality");

			// 距离
			String distance = userJsonObj.getString("distance");

			String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

			RecommendUser user = new RecommendUser();
			users.add(user);

			GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

			GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

			int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);

			// 当前月份小于出生月份则表示未满,再-1,不考虑天
			age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

			user.setUserId(userId);
			user.setGender(gender);
			user.setAge(age);

			user.setVip(isVip);

			user.setNickname(nickname);
			user.setHeight(height);
			user.setWeight(weight);
			user.setMonthlySalary(monthlySalary);
			user.setHomeplace(homeplace);
			user.setAddress(address);

			user.setInterests(interests);
			user.setPersonality(peronality);

			user.setDistance(distance);

			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}

			BSImageUrl bsImageUrl = new BSImageUrl();
			bsImageUrl.setBigImageUrl(faceUrl);
			bsImageUrl.setSmallImageUrl(getSmallImageUrl(faceUrl));
			user.setFaceUrl(bsImageUrl);
		    }

		} catch (JSONException e) {
		    e.printStackTrace();
		}

		if (users == null) {
		    listener.onFailure("参数错误");
		    return;
		}

		listener.onSuccess(users);
	    }

	});
    }

    @Override
    public void loadStrangeNews(LoadStrangeNewsPostParams postParams, final OnLoadStrangeNewsResponseListener listener) {
	// TODO Auto-generated method stub
	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");
	nameAndValues.put("order", postParams.order);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");

	    }

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String response = new String(arg2);
		System.out.println("奇闻:" + response);
		List<StrangeNews> newsList = new ArrayList<StrangeNews>();

		try {
		    JSONArray jsonArr = new JSONArray(response);
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
			int id = jsonObj.getInt("id");
			String title = jsonObj.getString("title");
			String summary = jsonObj.getString("summary");
			String back = jsonObj.getString("back");
			String tag = jsonObj.getString("tag");
			int addTime = jsonObj.getInt("addtime");

			StrangeNews news = new StrangeNews();
			newsList.add(news);

			news.setId(id);
			news.setTitle(title);
			news.setSummary(summary);
			news.setTag(tag);
			news.setCreateTime(addTime);

			BSImageUrl bsImageUrl = new BSImageUrl();

			if (back.startsWith(".")) { // 加上 http://主机地址/Public/...
			    back = Constants.HOST + back.substring(1);
			    bsImageUrl.setBigImageUrl(back);
			    bsImageUrl.setSmallImageUrl(getSmallImageUrl(back));
			} else {
			    bsImageUrl.setBigImageUrl(back);
			    bsImageUrl.setSmallImageUrl(back);
			}

			news.setContentImageUrl(bsImageUrl);
		    }

		    listener.onSuccess(newsList);

		} catch (JSONException e) {
		    e.printStackTrace();
		    listener.onFailure("参数错误");
		}
	    }

	};
	String url = Constants.URL_STRANGENEWS;
	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void giveGift(final GiveGiftPostParams postParams, final OnGiveGiftResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_GIVE_GIFT;

	RequestParams params = new RequestParams();
	params.put("gift_id", postParams.giftId);
	params.put("to_user_id", postParams.toUserId);
	params.put("word", postParams.word);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String data = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(data);
		    int status = jsonObj.getInt("status");
		    if (0 == status) { // 失败
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    UserGift userGift = new UserGift();
		    userGift.setWord(postParams.word);
		    userGift.setFromUserId(""); // 当前用户ID
		    userGift.setFromUserNickname("");// 当前用户昵称
		    userGift.setWord(postParams.word);
		    listener.onSuccess(userGift);

		} catch (Throwable th) {
		    listener.onFailure("参数错误");
		}
	    }

	};

	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void loadGift(final LoadGiftPostParams postParams, final OnLoadGiftResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_GIFT_LIST;

	RequestParams params = new RequestParams();
	params.put("page", postParams.page);
	params.put("page_size", postParams.pageSize);
	params.put("gender", postParams.targetUserGender);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String data = new String(arg2);

		System.out.println("可选礼物:" + data);
		try {
		    JSONArray jsonArr = new JSONArray(data);

		    List<Gift> gifts = new ArrayList<Gift>();
		    for (int i = 0, n = jsonArr.length(); i < n; ++i) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			Gift gift = new Gift();
			gifts.add(gift);

			String id = jsonObj.getString("id");
			String name = jsonObj.getString("name");
			int price = jsonObj.getInt("price");
			int glamour = jsonObj.getInt("glamour");
			String imageUrl = jsonObj.getString("image_url");
			String withWord = jsonObj.getString("with_word");

			gift.setGiftId(id);
			gift.setGlamour(glamour);
			gift.setPrice(price);
			gift.setName(name);
			gift.setImageUrl(imageUrl);
			gift.setWithWord(withWord);

		    }
		    listener.onSuccess(gifts);
		} catch (Throwable th) {
		    listener.onFailure("参数错误");
		}
	    }

	};

	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void findGiftByUserId(FindGiftByUserIdPostParams postParams, OnFindGiftByUserIdResponseListener listener) {
	// TODO Auto-generated method stub

    }
}
