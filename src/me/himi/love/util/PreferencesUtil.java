package me.himi.love.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName:PreferencesUtil
 * @author sparklee liduanwei_911@163.com
 * @date Nov 19, 2014 7:02:50 PM
 */
public class PreferencesUtil {

    public static void save(Context context, String key, String value) {
	SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
	pref.edit().putString(key, value).commit();
    }

    public static String read(Context context, String key) {
	SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
	return pref.getString(key, null);
    }

}
