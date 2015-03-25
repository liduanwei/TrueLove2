package me.himi.love.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * @ClassName:Downloader
 * @author sparklee liduanwei_911@163.com
 * @date Nov 14, 2014 10:18:59 PM
 */
public class Downloader {
    public static interface AsyncResponseHandler {
	void onFailure(String errorMsg);

	void onSuccess(byte[] response);

	void onProgress(int currentLength, int contentLength);
    }

    private static ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public static void download(final String imageUrl, final AsyncResponseHandler responseHandler) {
	final Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case -1:
		    responseHandler.onFailure(msg.obj.toString());
		    break;
		case 0:
		    responseHandler.onProgress(msg.arg1, msg.arg2);
		    break;
		case 1:
		    responseHandler.onSuccess((byte[]) msg.obj);
		    break;
		}
		super.handleMessage(msg);
	    }
	};
	threadPool.execute(new Runnable() {

	    @Override
	    public void run() {
		// TODO Auto-generated method stub
		try {
		    HttpURLConnection conn = (HttpURLConnection) new URL(imageUrl).openConnection();
		    InputStream in = conn.getInputStream();
		    int contentLength = conn.getContentLength();
		    int currentLength = 0;

		    int len = 0;
		    byte[] buffer = new byte[1024 << 1];

		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while (-1 != (len = in.read(buffer))) {
			baos.write(buffer, 0, len);
			currentLength += len;
			// 公布进度
			handler.obtainMessage(0, currentLength, contentLength).sendToTarget();
		    }
		    baos.flush();
		    baos.close();
		    in.close();

		    byte[] data = baos.toByteArray();
		    handler.obtainMessage(1, data).sendToTarget();
		    return;

		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		handler.obtainMessage(-1, "下载失败").sendToTarget();
	    }
	});

    }
}
