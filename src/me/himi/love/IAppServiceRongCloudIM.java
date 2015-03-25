package me.himi.love;

import io.rong.imlib.RongIMClient;
import android.content.Context;

/**
 * @ClassName:IAppServiceChat
 * @author sparklee liduanwei_911@163.com
 * @date Dec 16, 2014 4:17:02 PM
 */
public interface IAppServiceRongCloudIM {
    // 获取 token
    void getTokenFromServer(String userId, OnTokenResponseListener listener);

    public interface OnTokenResponseListener {
	void onSuccess(String token);

	void onFailure(String errorMsg);
    }

    // 登录
    void connect(Context context, String token, RongIMClient.ConnectCallback callback);

    // 刷新(当前已登录)用户资料
    void refreshUserInfo();

}
