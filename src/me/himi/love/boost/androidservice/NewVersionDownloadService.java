package me.himi.love.boost.androidservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @ClassName:CheckUpdateService
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 3:03:07 PM
 */
public class NewVersionDownloadService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void onCreate() {
	super.onCreate();

	System.out.println("newversiondownload service oncreate");

    }

}
