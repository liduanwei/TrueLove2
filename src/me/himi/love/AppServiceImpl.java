package me.himi.love;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.LoadUserNewsComments;
import me.himi.love.entity.LoginedUser;
import me.himi.love.entity.ReceivedSayHi;
import me.himi.love.entity.RegisteredUser;
import me.himi.love.entity.UserNews;
import me.himi.love.entity.UserNewsComment;
import me.himi.love.entity.VisitorUser;
import me.himi.love.entity.loader.impl.CheckUpdateLoaderImpl;
import me.himi.love.entity.loader.impl.LoginedUserLoaderImpl;
import me.himi.love.entity.loader.impl.PublishCommentResultLoaderImpl;
import me.himi.love.entity.loader.impl.RegisteredUserLoaderImpl;
import me.himi.love.entity.loader.impl.SayHiResponseLoaderImpl;
import me.himi.love.entity.loader.impl.UploadFileResponseLoaderImpl;
import me.himi.love.entity.loader.impl.UserDetailLoaderImpl;
import me.himi.love.entity.loader.impl.UserNewsCommentLoaderImpl;
import me.himi.love.entity.loader.impl.UserNewsLoaderImpl;
import me.himi.love.entity.loader.impl.VisitorUserLoaderImpl;
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
 * @ClassName:AppServiceImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 6, 2014 6:59:54 PM
 */
public class AppServiceImpl implements IAppService {

    private static IAppService instance;

    private AppServiceImpl() {

    }

    public synchronized static IAppService getInstance() {
	if (null == instance) {
	    instance = new AppServiceImpl();
	}
	return instance;
    }

    @Override
    public void register(RegisterUser user, final OnRegisterListener onRegisterListener) {
	String url = Constants.URL_REGISTER;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("app_id", "aBaCcA232CDfaEf");
	nameAndValues.put("device_id", user.getImei());
	nameAndValues.put("phone_number", user.getPhoneNumber());
	nameAndValues.put("phone_imei", user.getImei());
	nameAndValues.put("longtitude", user.getLongtitude());
	nameAndValues.put("latitude", user.getLatitude());
	nameAndValues.put("username", user.getUsername());
	nameAndValues.put("password", user.getPassword());
	nameAndValues.put("model", user.getModel());

	RequestParams params = new RequestParams(nameAndValues);

	AsyncHttpResponseHandler resHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		handleRegisterJson(new String(arg2));
		//		System.out.println("注册:" + new String(arg2));
	    }

	    private void handleRegisterJson(String responseJson) {
		RegisteredUser registeredUser = new RegisteredUserLoaderImpl().load(responseJson);
		onRegisterListener.onSuccess(registeredUser);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		onRegisterListener.onFailure("注册失败:" + new String(arg2));
		System.out.println("注册失败:" + new String(arg2));
	    }
	};

	HttpUtil.post(url, params, resHandler);
    }

    @Override
    public void login(String userId, String password, String longtitude, String latitude, String address, final OnLoginListener onLoginListener) {
	String url = Constants.URL_LOGIN;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("app_id", "abaxca232zdfasf");
	nameAndValues.put("device_id", "9sdf8a7fandsfj23");
	nameAndValues.put("user_id", userId + "");
	nameAndValues.put("password", password);
	nameAndValues.put("longtitude", longtitude);
	nameAndValues.put("latitude", latitude);
	nameAndValues.put("addr", address);

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler resHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] headers, byte[] arg2) {
		String response = new String(arg2);

		System.out.println("logined:" + response);
		handleResponseJson(new String(arg2));

	    }

	    private void handleResponseJson(String responseJson) {
		// TODO Auto-generated method stub
		// 查看登录是否成功
		// 成功则跳转到 主页
		// 失败 停留

		// 查询登录后的会员信息
		LoginedUser loginedUser = new LoginedUserLoaderImpl().load(responseJson);
		onLoginListener.onSuccess(loginedUser);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		onLoginListener.onFailure("连接超时");
	    }
	};

	HttpUtil.post(url, params, resHandler);
    }

    @Override
    public void update(UserInfo user, final OnSimpleListener onUpdateListener) {
	String url = Constants.URL_UPDATE_INFO;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("app_id", user.getAppId());
	nameAndValues.put("device_id", user.getDeviceId());

	nameAndValues.put("monologue", user.getMonologue());
	nameAndValues.put("nickname", user.getNickname());
	nameAndValues.put("birthday", user.getBirthday() + "");

	nameAndValues.put("education", user.getEducation());
	nameAndValues.put("homeplace", user.getHomeplace());
	nameAndValues.put("address", user.getAddress());
	nameAndValues.put("blood", user.getBlood());
	nameAndValues.put("monthly_salary", user.getMonthlySalary());
	nameAndValues.put("employment", user.getEmployment());
	nameAndValues.put("charm_body", user.getCharmBody());
	nameAndValues.put("house", user.getHouse());
	nameAndValues.put("distance_love", user.getDistanceLove());
	nameAndValues.put("opposite_sex_type", user.getOppositeSexType());
	nameAndValues.put("premartial_sex", user.getPremartialSex());
	nameAndValues.put("live_with_parents", user.getLiveWithParents());
	nameAndValues.put("want_baby", user.getWantBaby());
	nameAndValues.put("marital_status", user.getMartialStatus());
	nameAndValues.put("interests", user.getInterests());
	nameAndValues.put("personality", user.getPersonalities());
	nameAndValues.put("height", user.getHeight() + "");
	nameAndValues.put("weight", user.getWeight() + "");

	nameAndValues.put("personal_instruction", user.getInstruction());
	nameAndValues.put("often_haunt_address", user.getOftenAddress());

	nameAndValues.put("want_homeplace", user.getWantHomeplace());
	nameAndValues.put("want_address", user.getWantAddress());
	nameAndValues.put("want_age", user.getWantAge());
	nameAndValues.put("want_height", user.getWantHeight());
	nameAndValues.put("want_education", user.getWantEducation());
	nameAndValues.put("want_monthly_salary", user.getWantMonthSalary());
	nameAndValues.put("want_additional", user.getWantAddtional());

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler resHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		System.out.println("updaet:" + new String(arg2));
		if (new String(arg2).length() == 1) {
		    onUpdateListener.onSuccess();
		} else {
		    onUpdateListener.onFailure("更新失败了:" + new String(arg2));
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		onUpdateListener.onFailure("连接超时");
	    }
	};

	HttpUtil.post(url, params, resHandler);
    }

    @Override
    public void logout(final OnLogoutListener onLogoutListener) {
	String logoutUrl = Constants.URL_LOGOUT;
	AsyncHttpResponseHandler resHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

		String response = new String(arg2);
		if (response.length() == 1) {
		    onLogoutListener.onSuccess();
		} else {
		    onLogoutListener.onFailure("数据出错了");
		}
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// 网络错误
		onLogoutListener.onFailure("连接超时");
	    }
	};
	HttpUtil.get(logoutUrl, resHandler);
    }

    @Override
    public void loadUserInfo(int userId, final OnLoadDetailUserListener onLoadDetailUserListener) {
	String url = Constants.URL_DETAIL;

	RequestParams params = new RequestParams();
	params.put("user_id", userId + "");
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		DetailInfoUser user = new UserDetailLoaderImpl().load(res);
		onLoadDetailUserListener.onSuccess(user);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		onLoadDetailUserListener.onFailure("连接超时");

	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

    //    @Override
    //    public void loadCurrentLoginedUserInfo(final OnLoadDetailUserListener onLoadUserListner) {
    //	String url = Constants.URL_DETAIL_CURRENT_LOGINED_USER;
    //	Map<String, String> nameAndValues = new HashMap<String, String>();
    //	//	nameAndValues.put("", "");
    //
    //	RequestParams params = new RequestParams(nameAndValues);
    //	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
    //
    //	    @Override
    //	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
    //		String res = new String(arg2);
    //		DetailInfoUser user = new UserDetailLoaderImpl().load(res);
    //		onLoadUserListner.onSuccess(user);
    //	    }
    //
    //	    @Override
    //	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
    //		// TODO Auto-generated method stub
    //		onLoadUserListner.onFailure("连接超时");
    //	    }
    //	};
    //	HttpUtil.post(url, params, responseHandler);
    //    }

    @Override
    public void publishNews(PublishNewsParams news, final OnSimpleListener listener) {
	String url = Constants.URL_PUBLISH_NEWS;
	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("title", news.title);
	nameAndValues.put("content", news.content);
	nameAndValues.put("post_longitude", news.longitude);
	nameAndValues.put("post_latitude", news.latitude);
	nameAndValues.put("is_private", news.isPrivate + "");
	nameAndValues.put("is_allow_comment", news.isAllowComment + "");
	nameAndValues.put("address", news.address);

	for (int i = 0, n = news.imageUrls.size(); i < n; ++i) {
	    System.out.println("publishNews:" + news.imageUrls.get(i));
	    nameAndValues.put("image_url" + (i + 1 <= 9 ? "0" + (i + 1) : i + 1), news.imageUrls.get(i));
	}

	RequestParams params = new RequestParams(nameAndValues);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    if (jsonObj.getInt("status") == 1) {
			listener.onSuccess();
		    } else {
			listener.onFailure("参数错误");
		    }
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    listener.onFailure("响应数据错误");
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
    public void loadNewsList(UserNewsParams news, final OnNewsListener listener) {
	// TODO Auto-generated method stub

	String url = Constants.URL_USER_NEWS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", news.userId + "");
	nameAndValues.put("page", news.page + "");
	nameAndValues.put("page_size", news.pageSize + "");

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
    public void loadNewsComments(UserNewsCommentParams comment, final OnNewsCommentListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_NEWS_COMMENT;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("user_id", comment.userId + "");
	nameAndValues.put("page", comment.page + "");
	nameAndValues.put("page_size", comment.pageSize + "");
	nameAndValues.put("news_id", comment.newsId + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		LoadUserNewsComments loadUserNewsComments = new UserNewsCommentLoaderImpl().load(res);
		if (loadUserNewsComments != null) {
		    listener.onSuccess(loadUserNewsComments);
		} else {
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
    public void publishNewsComment(PublishNewsCommentParams publishParams, final OnPublishNewsCommentListener listener) {
	String url = Constants.URL_USER_NEWS_COMMENT_PUBLISH;
	Map<String, String> nameAndValues = new HashMap<String, String>();

	nameAndValues.put("news_id", publishParams.newsId + "");
	nameAndValues.put("news_user_id", publishParams.newsUserId + "");
	nameAndValues.put("content", publishParams.content);
	nameAndValues.put("parent_comment_id", publishParams.replyCommentId + "");

	RequestParams params = new RequestParams(nameAndValues);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		try {
		    JSONObject jsonObj = new JSONObject(res);

		    int status = jsonObj.getInt("status");
		    if (status == 0) {
			String msg = jsonObj.getString("msg");
			listener.onFailure(msg);
			return;
		    }
		    if (status == 1) {

			JSONObject commentObj = jsonObj.getJSONObject("comment");

			int id = commentObj.getInt("id");
			int userId = commentObj.getInt("user_id");
			String nickname = commentObj.getString("nickname");
			String faceUrl = commentObj.getString("face_url");
			String content = commentObj.getString("content");
			int postTime = commentObj.getInt("post_time");

			UserNewsComment comment = new UserNewsComment();

			String postTimeStr = ActivityUtil.convertTimeToString(postTime * 1000L);

			comment.setNickname(nickname);
			if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
			    faceUrl = Constants.HOST + faceUrl.substring(1);
			}
			comment.setFaceUrl(faceUrl);
			comment.setUserId(userId);
			comment.setContent(content);
			comment.setPostTime(postTimeStr);
			comment.setId(id);

			if (commentObj.has("reply_user")) {
			    String replyUserNickname = commentObj.getString("reply_user");
			    comment.setReplyUser(replyUserNickname);

			    String replyUserCommentContent = commentObj.getString("reply_user_content");
			    comment.setReplyUserCommentContent(replyUserCommentContent);
			}

			listener.onSuccess(comment);
			return;
		    }

		} catch (JSONException e) {
		    e.printStackTrace();
		}

		listener.onFailure("评论失败,参数错误");

	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void checkUpdate(CheckUpdateParams update, final OnCheckUpdateListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_CHECKUPDATE;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("version_id", update.currentVersion + "");

	RequestParams params = new RequestParams(nameAndValues);
	HttpUtil.post(url, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String response = new String(arg2);

		System.out.println("checkUpdate:" + response);
		CheckUpdateVersion version = new CheckUpdateLoaderImpl().load(response);
		listener.onSuccess(version);

	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }
	});
    }

    @Override
    public void loadUserVisitors(UserVisitorsParams postParams, final OnLoadUserVisitorsListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_USER_VISITORS;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("target_user_id", postParams.userId + "");
	nameAndValues.put("page", postParams.page + "");
	nameAndValues.put("page_size", postParams.pageSize + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("谁看过:" + res);
		List<VisitorUser> users = new VisitorUserLoaderImpl().load(res);
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
    public void uploadFace(UploadFileParams fileParams, final OnUploadFaceListener listener) {

	System.out.println("upload,,,,,,,,,");
	String url = Constants.URL_USER_UPLOAD_FACE;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("target_user_id", fileParams.userId + "");
	nameAndValues.put("file_path", fileParams.filePathes.get(0) + "");

	RequestParams params = new RequestParams(nameAndValues);
	try {

	    // 如给定的是 File 则 会在http头中加上 Content-Type
	    // 如果是 InputStream 则不会加 Content-Type
	    // 所以会对上传文件类型进行 检查的服务器端这里就得使用 File, 不能用 InputStream
	    params.put("filexxxx", new File(fileParams.filePathes.get(0)));

	} catch (FileNotFoundException e) {
	    listener.onFailure("上传文件不存在");
	    return;
	}

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		UploadFaceResponse uploadFileResponse = new UploadFileResponseLoaderImpl().load(res);
		if (uploadFileResponse != null) {
		    listener.onSuccess(uploadFileResponse);
		} else {
		    listener.onFailure("上传失败");
		}

	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onRetry(int retryNo) {
		// TODO Auto-generated method stub
		super.onRetry(retryNo);
	    }
	};

	HttpUtil.post(url, params, responseHandler);

    }

    @Override
    public void uploadPhoto(UploadFileParams fileParams, final OnUploadPhotosListener listener) {
	String url = Constants.URL_USER_UPLOAD_PHOTO;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("target_user_id", fileParams.userId + "");

	RequestParams params = new RequestParams(nameAndValues);
	try {

	    // 如给定的是 File 则 会在http头中加上 Content-Type
	    // 如果是 InputStream 则不会加 Content-Type
	    // 所以会对上传文件类型进行 检查的服务器这里就得使用 File, 不能用 InputStream
	    for (int i = 0, n = fileParams.filePathes.size(); i < n; ++i) {
		params.put("file" + i, new File(fileParams.filePathes.get(i)));
	    }
	} catch (FileNotFoundException e) {
	    listener.onFailure("上传文件不存在");
	    return;
	}

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("uploadP" + res);
		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int status = jsonObj.getInt("status");
		    if (0 == status) {
			listener.onFailure(jsonObj.getString("msg"));
			return;
		    }
		    JSONArray imagesArr = jsonObj.getJSONArray("images");
		    UploadPhotosResponse uploadPhotoResponse = new UploadPhotosResponse();
		    uploadPhotoResponse.imageUrls = new ArrayList<String>();
		    for (int i = 0, n = imagesArr.length(); i < n; ++i) {
			String imageUrl = imagesArr.getString(i);
			uploadPhotoResponse.imageUrls.add(imageUrl);
		    }
		    listener.onSuccess(uploadPhotoResponse);
		    return;
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		listener.onFailure("参数错误xxxxx");
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }

	    @Override
	    public void onRetry(int retryNo) {
		// TODO Auto-generated method stub
		super.onRetry(retryNo);
	    }
	};

	HttpUtil.post(url, params, responseHandler);
    }

    @Override
    public void registerPerfectComplete(PerfectCompleetePostParams postParams, final OnPerfectCompleteResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_REGISTER_PERFECT_COMPLETE;

	Map<String, String> nameAndValues = new HashMap<String, String>();
	nameAndValues.put("nickname", postParams.nickname);
	nameAndValues.put("birthday", postParams.brithday + "");
	nameAndValues.put("gender", postParams.gender + "");

	RequestParams params = new RequestParams(nameAndValues);
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		listener.onSuccess();
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		listener.onFailure("连接超时");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

}
