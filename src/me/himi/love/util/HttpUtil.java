package me.himi.love.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:H
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:11:10 PM
 */
public class HttpUtil {

    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

    static {

	client.setTimeout(20000); // 设置链接超时，如果不设置，默认为10s
    }

    /**
     * 为 aysncHttpClient 设置cookie 存储目录
     * @param context
     */
    public static void setCookieStore(Context context) {
	PersistentCookieStore cookieStore = new PersistentCookieStore(context);
	client.setCookieStore(cookieStore);
    }

    // 用一个完整url获取一个string对象
    public static void get(String urlString, AsyncHttpResponseHandler res) {

	client.get(urlString, res);

    }

    // url里面带参数
    public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res) {

	client.get(urlString, params, res);

    }

    // 不带参数，获取json对象或者数组
    public static void get(String urlString, JsonHttpResponseHandler res) {
	client.get(urlString, res);

    }

    // 带参数，获取json对象或者数组
    public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) {

	client.get(urlString, params, res);

    }

    // 下载数据使用，会返回byte数据
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {

	client.get(uString, bHandler);

    }

    public static void post(String url, AsyncHttpResponseHandler resHandler) {
	// 发起新请求前取消掉所有其他请求
//	client.cancelAllRequests(true);

	client.post(url, resHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responesHandler) {
	// 发起新请求前取消掉所有其他请求
//	client.cancelAllRequests(true);

	client.post(url, params, responesHandler);
    }

    public static AsyncHttpClient getClient() {
	return client;
    }

}