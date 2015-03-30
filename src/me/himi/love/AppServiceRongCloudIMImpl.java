package me.himi.love;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.ConnectCallback;
import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:AppServiceRongCloudIMImpl
 * @author sparklee liduanwei_911@163.com
 * @date Jan 10, 2015 12:04:11 PM
 */
public class AppServiceRongCloudIMImpl implements IAppServiceRongCloudIM {

    private static IAppServiceRongCloudIM instance;

    public synchronized static IAppServiceRongCloudIM getInstance() {
	if (null == instance) {
	    instance = new AppServiceRongCloudIMImpl();
	}
	return instance;
    }

    @Override
    public void getTokenFromServer(String idOrUsernameOrPhonenumber, final OnTokenResponseListener listener) {
	// TODO Auto-generated method stub
	String url = Constants.URL_RONGCLOUD_IM_TOKEN;
	RequestParams params = new RequestParams();
	params.put("username", idOrUsernameOrPhonenumber);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);

		System.out.println("获取融云IM token:" + res);

		try {
		    JSONObject jsonObj = new JSONObject(res);
		    int code = jsonObj.getInt("code");
		    if (200 != code) {
			listener.onFailure("获取Token失败," + code);
			return;
		    }

		    String token = jsonObj.getString("token");
		    listener.onSuccess(token);
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
    public void connect(Context context, String token, ConnectCallback callback) {
	// TODO Auto-generated method stub
	// appKey
//	RongIM.init(context);//, "6tnym1brn8ys7", R.drawable.ic_launcher);

	try {
	    // 连接 登录
	    RongIM.connect(token, callback);

	    //	    Class<RongIM> c = (Class<RongIM>) Class.forName("io.rong.imkit.RongIM");
	    //	    for (Field f : c.getDeclaredFields()) {
	    //		f.setAccessible(true);
	    //		System.out.println(f.getName() + "," + f.get(rongIm));
	    //	    }

	    //	    Field f = rongIm.getClass().getField("mRrongIMClient");
	    //	    f.setAccessible(true);
	    //	    RongIMClient ric = (RongIMClient) f.get(rongIm);
	    //	    System.out.println("rongIMClient:" + ric.toString());

	    //	  

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public void refreshUserInfo() {
	// TODO Auto-generated method stub
	String url = Constants.URL_RONGCLOUD_IM_REFRESH_USERINFO;
	RequestParams params = new RequestParams();
	//	params.put("user_id", userId);

	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		String res = new String(arg2);
		System.out.println("RC: refresh userInfo: " + res);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		System.out.println("refresh RongCloud userInfo:error");
	    }
	};
	HttpUtil.post(url, params, responseHandler);
    }

}
