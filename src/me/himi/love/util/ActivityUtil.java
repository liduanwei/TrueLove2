package me.himi.love.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import me.himi.love.MyApplication;
import me.himi.love.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

/**
 * @author adison Activity帮助器类
 */
public final class ActivityUtil {

    /**
     * 
     * @param context
     * @param text
     */
    public static void copy(Context context, String text) {
	android.text.ClipboardManager cmb = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
	cmb.setText(text);
    }

    /**
     * 获取屏幕宽高
     * @param activity
     * @return
     */
    public static int[] getScreenSize() {
	int[] screens;
	// if (Constants.screenWidth > 0) {
	// return screens;
	// }
	DisplayMetrics dm = new DisplayMetrics();
	dm = MyApplication.getInstance().getResources().getDisplayMetrics();
	screens = new int[] { dm.widthPixels, dm.heightPixels };
	return screens;
    }

    public static float[] getBitmapConfiguration(Bitmap bitmap, ImageView imageView, float screenRadio) {
	int screenWidth = ActivityUtil.getScreenSize()[0];
	float rawWidth = 0;
	float rawHeight = 0;
	float width = 0;
	float height = 0;
	if (bitmap == null) {
	    // rawWidth = sourceWidth;
	    // rawHeight = sourceHeigth;
	    width = (float) (screenWidth / screenRadio);
	    height = (float) width;
	    imageView.setScaleType(ScaleType.FIT_XY);
	} else {
	    rawWidth = bitmap.getWidth();
	    rawHeight = bitmap.getHeight();
	    if (rawHeight > 10 * rawWidth) {
		imageView.setScaleType(ScaleType.CENTER);
	    } else {
		imageView.setScaleType(ScaleType.FIT_XY);
	    }
	    float radio = rawHeight / rawWidth;

	    width = (float) (screenWidth / screenRadio);
	    height = (float) (radio * width);
	}
	return new float[] { width, height };
    }

    /**
     * 获取应用versionCode
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
	PackageManager pm = context.getPackageManager();
	try {
	    PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
	    return info.versionCode;
	} catch (NameNotFoundException e) {
	    e.printStackTrace();
	}
	return 1;
    }

    /**
     * 获取应用versionName
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
	PackageManager pm = context.getPackageManager();
	try {
	    PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
	    return info.versionName;
	} catch (NameNotFoundException e) {
	    e.printStackTrace();
	}
	return "";
    }

    //
    // /**
    // * @author adison 设置图片参数
    // * @param bitmap
    // * @param left
    // * @param top
    // * @param right
    // * @param bottom
    // * @param sourceWidth
    // * @param sourceHeigth
    // * @param screenRadio
    // * @return
    // */
    // public static LayoutParams setImageViewParams(Bitmap bitmap, ImageView imageView, int left, int top, int right, int bottom,
    // float sourceWidth, float sourceHeigth, float screenRadio, boolean fitSource) {
    // float[] cons=getBitmapConfiguration(bitmap, imageView, sourceWidth, sourceHeigth, screenRadio, fitSource);
    // LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams((int)cons[0], (int)cons[1]);
    // layoutParams.gravity=Gravity.CENTER;
    // layoutParams.setMargins(left, top, right, bottom);
    // return layoutParams;
    // }

    /**
     * 通过外部浏览器打开页面
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String urlText) {
	Intent intent = new Intent();
	intent.setAction("android.intent.action.VIEW");
	Uri url = Uri.parse(urlText);
	intent.setData(url);
	context.startActivity(intent);
    }

    /**
     * 切换全屏状态。
     * @param activity Activity
     * @param isFull 设置为true则全屏，否则非全屏
     */
    public static void toggleFullScreen(Activity activity, boolean isFull) {
	hideTitleBar(activity);
	Window window = activity.getWindow();
	WindowManager.LayoutParams params = window.getAttributes();
	if (isFull) {
	    params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
	    window.setAttributes(params);
	    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	} else {
	    params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    window.setAttributes(params);
	    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
    }

    /**
     * 设置为全屏
     * @param activity Activity
     */
    public static void setFullScreen(Activity activity) {
	toggleFullScreen(activity, true);
    }

    /**
     * 获取系统状态栏高度
     * @param activity Activity
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
	try {
	    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
	    Object object = clazz.newInstance();
	    Field field = clazz.getField("status_bar_height");
	    int dpHeight = Integer.parseInt(field.get(object).toString());
	    return activity.getResources().getDimensionPixelSize(dpHeight);
	} catch (Exception e1) {
	    e1.printStackTrace();
	    return 0;
	}
    }

    /**
     * 隐藏Activity的系统默认标题栏
     * @param activity Activity
     */
    public static void hideTitleBar(Activity activity) {
	activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * 强制设置Actiity的显示方向为垂直方向。
     * @param activity Activity
     */
    public static void setScreenVertical(Activity activity) {
	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 强制设置Activity的显示方向为横向。
     * @param activity Activity
     */
    public static void setScreenHorizontal(Activity activity) {
	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 隐藏软件输入法
     * @param activity Activity
     */
    public static void hideSoftInput(Activity activity) {
	activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showSoftInputView(Context context, View focusingView) {
	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.showSoftInput(focusingView, 1);

    }

    /**
     * 关闭已经显示的输入法窗口。
     * @param context 上下文对象，一般为Activity
     * @param focusingView 输入法所在焦点的View
     */
    public static void closeSoftInput(Context context, View focusingView) {
	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(focusingView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 使UI适配输入法
     * @param activity Activity
     */
    public static void adjustSoftInput(Activity activity) {
	activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public interface MessageFilter {

	String filter(String msg);
    }

    public static MessageFilter msgFilter;

    /**
     * 短时间显示Toast消息，并保证运行在UI线程中
     * @param activity Activity
     * @param message 消息内容
     */
    public static void show(final Activity activity, final String message) {
	if (activity == null) { // 防止 java.lang.NullPointerException
	    return;
	}
	final String msg = msgFilter != null ? msgFilter.filter(message) : message;
	activity.runOnUiThread(new Runnable() {

	    public void run() {
		// Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		Toast toast = ToastFactory.getToast(activity, message);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	    }
	});
    }

    /**
     * 短时间显示Toast消息，并保证运行在UI线程中
     * @param activity Activity
     * @param message 消息内容
     */
    public static void show(final Context context, final String message) {
	final String msg = msgFilter != null ? msgFilter.filter(message) : message;
	MyApplication.getInstance().getTopActivity().runOnUiThread(new Runnable() {
	    @Override
	    public void run() {
		// Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		Toast toast = ToastFactory.getToast(MyApplication.getInstance().getTopActivity(), message);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	    }
	});
    }

    /**
     * 长时间显示Toast消息，并保证运行在UI线程中
     * @param activity Activity
     * @param message 消息内容
     */
    public static void showL(final Activity activity, final String message) {
	final String msg = msgFilter != null ? msgFilter.filter(message) : message;
	activity.runOnUiThread(new Runnable() {

	    public void run() {
		// Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
		Toast toast = ToastFactory.getToast(activity, message);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	    }
	});
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     * @param context 上下文，一般为Activity
     * @param dpValue dp数据值
     * @return px像素值
     */
    public static int dip2px(Context context, float dpValue) {
	final float scale = context.getResources().getDisplayMetrics().density;
	return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     * @param context 上下文，一般为Activity
     * @param pxValue px像素值
     * @return dp数据值
     */
    public static int px2dip(Context context, float pxValue) {
	final float scale = context.getResources().getDisplayMetrics().density;
	return (int) (pxValue / scale + 0.5f);
    }

    /**
     * activity切换动画
     * @param m
     * @param isEnd
     */
    @SuppressLint("NewApi")
    public static void runActivityAnim(Activity m, boolean isEnd) {
	if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
	    if (isEnd) {
		m.overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
	    } else {
		m.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
	    }
	}
    }

    /**
     * 快捷方式是否存在
     * @return
     */
    public static boolean isAddedShortCut(Context context) {
	boolean isInstallShortCut = false;
	ContentResolver cr = context.getContentResolver();
	String authority = "com.android.launcher2.settings";
	Uri uri = Uri.parse("content://" + authority + "/favorites?notify=true");
	Cursor c = cr.query(uri, new String[] { "title", "iconResource" }, "title=?", new String[] { context.getString(R.string.app_name) }, null);
	if (null != c && c.getCount() > 0) {
	    isInstallShortCut = true;
	}
	return isInstallShortCut;
    }

    /**
     * 创建快捷方式
     */
    public static void addShortCut(Context context, Class<? extends Activity> launcher) {
	Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
	// 设置属性
	shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
	ShortcutIconResource resource = Intent.ShortcutIconResource.fromContext(context, R.drawable.icon);
	shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, resource);
	// 是否允许重复创建
	shortcut.putExtra("duplicate", false);
	Intent intent = new Intent(Intent.ACTION_MAIN);// 标识Activity为一个程序的开始
	intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
	intent.addCategory(Intent.CATEGORY_LAUNCHER);
	intent.setClass(context, launcher);
	shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
	context.sendBroadcast(shortcut);
    }

    /**
     * 得到view高度
     * @param w
     * @param bmw
     * @param bmh
     * @return
     */
    public static int getViewHeight(int w, int bmw, int bmh) {
	return w * bmh / bmw;
    }

    /**
     * 获取屏幕宽高
     * @param activity
     * @return
     */
    public static int[] getScreenSize(Activity activity) {
	int[] screens;
	// if (Constants.screenWidth > 0) {
	// return screens;
	// }
	DisplayMetrics dm = new DisplayMetrics();
	activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	screens = new int[] { dm.widthPixels, dm.heightPixels };
	return screens;
    }

    // MD5変換
    public static String Md5(String str) {
	if (str != null && !str.equals("")) {
	    try {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < md5Byte.length; i++) {
		    sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
		    sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
		}
		str = sb.toString();
	    } catch (NoSuchAlgorithmException e) {

	    } catch (Exception e) {
	    }
	}
	return str;
    }

    /**
     * 得到手机IMEI
     * @param context
     * @return
     */
    public static String getImei(Context context) {
	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	return tm.getDeviceId();
    }

    /**
     * 获取AndroidId
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
	String androidId = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.ANDROID_ID);
	return androidId;
    }

    /**
     * 获取手机的号码(可能获取不到
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	return tm.getLine1Number();
    }

    /**
     * 获取mac地址
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
	WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	WifiInfo info = wm.getConnectionInfo();
	return info.getMacAddress();

    }

    /**
     * 设备型号
     * @return
     */
    public static String getModel() {
	return Build.MODEL;
    }

    /**
     * 判断网络连接是否可用
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
	// 获取手机所有连接管理对象（包括wifi，net等连接的管理）
	ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (connectivity != null) {
	    // 获取网络连接管理的对象
	    NetworkInfo info = connectivity.getActiveNetworkInfo();
	    if (info != null && info.isConnected()) {
		// 判断当前网络是否已经连接
		if (info.getState() == NetworkInfo.State.CONNECTED) {
		    return true;
		}
	    }
	}
	return false;

    }

    /**
     * 安装一个APK文件
     * @param file
     */
    public static void installApk(Context context, File file) {
	Intent intent = new Intent();
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(android.content.Intent.ACTION_VIEW);
	intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
	context.startActivity(intent);
    }

    /**
     * 将指定byte数组转换成16进制大写字符串
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
	StringBuffer hexString = new StringBuffer();
	for (int i = 0; i < b.length; i++) {
	    String hex = Integer.toHexString(b[i] & 0xFF);
	    if (hex.length() == 1) {
		hex = '0' + hex;
	    }
	    hexString.append(hex.toUpperCase());
	}
	return hexString.toString();
    }

    /**
     * 展示toast
     * @param context
     * @param str
     */
    public static void showToast(Context context, String str) {
	// Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);

	Toast toast = ToastFactory.getToast(context, str);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();
    }

    /**
     * 将输入流中的数据全部读取出来, 一次性返回
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] load(InputStream is) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	byte[] buffer = new byte[1024];
	int len;
	while ((len = is.read(buffer)) != -1)
	    baos.write(buffer, 0, len);
	baos.close();
	is.close();
	return baos.toByteArray();
    }

    /**
     * 根据文件得到字节流
     * @param file
     * @return
     */
    public static byte[] getFileByte(File file) {
	if (!file.exists()) {
	    return null;
	}
	try {
	    FileInputStream fis = new FileInputStream(file);
	    int len = fis.available();
	    byte[] bytes = new byte[len];
	    fis.read(bytes);
	    fis.close();
	    return bytes;
	} catch (Exception e) {

	}

	return null;
    }

    /**
     * 根据图片大小得到合适的缩放率
     * @param value
     * @return
     */
    public static int getSimpleNumber(int value) {
	if (value > 30) {
	    return 1 + getSimpleNumber(value / 4);
	} else {
	    return 1;
	}
    }

    /**
     * 大数组（String）获取相同元素 大致思路是:1.首先将两个数组A、B排序(递增)<br>
     * 2.分别从A和B中各取出一元素a,b，对a和b进行比 较：<br>
     * 1) 如果a与b相等，则将a或b存入一指定集合中<br>
     * 2)如果a小于b，则继续取A的下一元素，再与b比 较<br>
     * 3) 如果a大于b，则取B的下一个元素，与a进行比较<br>
     * 3.反复进行步骤2，知道A或B的元素都比较完<br>
     * 4.返回集合(存了相同的元素)<br>
     * @param strArr1
     * @param strArr2
     * @return
     */
    public static List<String> getAllSameElement2(String[] strArr1, String[] strArr2) {
	if (strArr1 == null || strArr2 == null) {
	    return null;
	}
	Arrays.sort(strArr1);
	Arrays.sort(strArr2);
	List<String> list = new ArrayList<String>();
	int k = 0;
	int j = 0;
	while (k < strArr1.length && j < strArr2.length) {
	    if (strArr1[k].compareTo(strArr2[j]) == 0) {
		if (strArr1[k].equals(strArr2[j])) {
		    list.add(strArr1[k]);
		    k++;
		    j++;
		}
		continue;
	    } else if (strArr1[k].compareTo(strArr2[j]) < 0) {
		k++;
	    } else {
		j++;
	    }
	}
	return list;
    }

    /**
     * 获取当前的年、月、日 对应的时间
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static long getTime() {
	Date d = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String dateNowStr = sdf.format(d);
	// System.out.println("格式化后的日期：" + dateNowStr);
	Date d2 = null;
	try {
	    d2 = sdf.parse(dateNowStr);
	    // System.out.println(d2);
	    // System.out.println(d2.getTime());
	    return d2.getTime();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return 0;
    }

    /**
     * 
     * @param pattern
     * @param seconds
     * @return
     */
    public static String getTimeStr(String pattern, int seconds) {
	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	return sdf.format(new Date(seconds * 1000L));
    }

    /**
     * 创建 以秒单位的时间的 GregorianCalendar
     * @param second
     * @return
     */
    public static GregorianCalendar newGregorianCalendar(int second) {
	// 转换成毫秒,必须加L表示长整型,否则会自动使用int造成变负数
	return newGregorianCalendar(second * 1000L);
    }

    /**
     * 创建 以毫秒单位的时间的 GregorianCalendar
     * @param milli
     * @return
     */
    public static GregorianCalendar newGregorianCalendar(long milli) {
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(new Date(milli)); // 转换成毫秒,必须加L表示长整型,否则会自动使用int造成变负数
	return gc;
    }

    /**
     * 转换秒时间到指定模式的字符串
     * @param second
     * @param pattern
     * @return
     */
    public static String getDateStr(int second, String pattern) {
	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
	return sdf.format(new Date(second * 1000L));
    }

    /**
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static long getTimemillis(int year, int month, int day) {
	Calendar cal = Calendar.getInstance();
	cal.set(year, month, day);
	return cal.getTimeInMillis();
    }

    /**
     * 转换指定的毫秒时间与系统时间之间的时间差,以"1分钟前,1小时前,1天前来表示"
     * @param time1
     * @return
     */
    public static String convertTimeToString(long time1) {

	// 手机系统当前时间 - 指定的时间 
	long passTime = (System.currentTimeMillis() - time1) / 1000L;
	if (passTime < 0 || passTime > 3600 * 24 * 30 * 12) { // 一年以上或手机时间错误则直接显示完整日期时间
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(time1);
	    int year = calendar.get(Calendar.YEAR);
	    int month = calendar.get(Calendar.MONTH) + 1;
	    int day = calendar.get(Calendar.DAY_OF_MONTH);
	    int hour = calendar.get(Calendar.HOUR_OF_DAY);
	    int minute = calendar.get(Calendar.MINUTE);
	    //	int second = calendar.get(Calendar.SECOND);

	    String dateStr = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (day <= 9 ? "0" + day : day) + " " + (hour <= 9 ? "0" + hour : hour) + ":" + (minute <= 9 ? "0" + minute : minute);//+ ":" + (second <= 9 ? "0" + second : second);
	    return dateStr;
	} else if (passTime == 0) {
	    return "刚刚";
	} else if (passTime < 60) {
	    return passTime + "秒前";
	} else if (passTime >= 60 && passTime < 3600) {
	    return passTime / 60 + "分钟前";
	} else if (passTime >= 3600 && passTime < 3600 * 24) {
	    return passTime / 3600 + "小时前";
	} else if (passTime >= 3600 * 24 && passTime < 3600 * 24 * 30) { // 这里判断不超过30天为一个月
	    int days = (int) (passTime / 3600 / 24);
	    if (days == 1) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time1);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String h = hour <= 9 ? "0" + hour : hour + "";
		String m = minute <= 9 ? "0" + minute : minute + "";
		return "昨天 " + h + ":" + m;
	    }
	    if (days < 10) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time1);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		String M = month <= 9 ? "0" + month : month + "";
		String D = day <= 9 ? "0" + day : day + "";
		String h = hour <= 9 ? "0" + hour : hour + "";
		String m = minute <= 9 ? "0" + minute : minute + "";
		return M + "-" + D + " " + h + ":" + m;
	    }
	    return days + "天前";
	} else if (passTime >= 3600 * 24 * 30 && passTime < 3600 * 24 * 30 * 12) {// 以每月30天计算,
	    int month = (int) (passTime / 3600 / 24 / 30);
	    return "大约" + month + " 个月前";
	}

	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(time1);
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH) + 1;
	int day = calendar.get(Calendar.DAY_OF_MONTH);
	int hour = calendar.get(Calendar.HOUR_OF_DAY);
	int minute = calendar.get(Calendar.MINUTE);
	//	int second = calendar.get(Calendar.SECOND);

	String dateStr = year + "-" + (month <= 9 ? "0" + month : month) + "-" + (day <= 9 ? "0" + day : day) + " " + (hour <= 9 ? "0" + hour : hour) + ":" + (minute <= 9 ? "0" + minute : minute);//+ ":" + (second <= 9 ? "0" + second : second);
	return dateStr;

    }

    /**
     * 从  Uri 中获取文件的绝对路径
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
	if (null == uri)
	    return null;
	final String scheme = uri.getScheme();
	String data = null;
	if (scheme == null)
	    data = uri.getPath();
	else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
	    data = uri.getPath();
	} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
	    Cursor cursor = context.getContentResolver().query(uri, new String[] { ImageColumns.DATA }, null, null, null);
	    if (null != cursor) {
		if (cursor.moveToFirst()) {
		    int index = cursor.getColumnIndex(ImageColumns.DATA);
		    if (index > -1) {
			data = cursor.getString(index);
		    }
		}
		cursor.close();
	    }
	}
	return data;
    }

    /**
     * 
     * @param act
     * @param uri
     * @param outputX 图片宽
     * @param outputY 图片高
     * @param requestCode
     * @param isCrop
     */
    public static void startImageAction(Activity act, Uri uri, int outputX, int outputY, int requestCode, boolean isCrop) {
	Intent intent = null;
	if (isCrop) {
	    intent = new Intent("com.android.camera.action.CROP");
	} else {
	    intent = new Intent(Intent.ACTION_GET_CONTENT, null);
	}
	intent.setDataAndType(uri, "image/*");
	intent.putExtra("crop", "true");
	intent.putExtra("aspectX", 1);
	intent.putExtra("aspectY", 1);
	intent.putExtra("outputX", outputX);
	intent.putExtra("outputY", outputY);
	intent.putExtra("scale", true);
	intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	intent.putExtra("return-data", true);
	intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	intent.putExtra("noFaceDetection", false); // no face detection
	act.startActivityForResult(intent, requestCode);
    }

    // I added a generic return type to reduce the casting noise in client code
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
	SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
	if (viewHolder == null) {
	    viewHolder = new SparseArray<View>();
	    view.setTag(viewHolder);
	}
	View childView = viewHolder.get(id);
	if (childView == null) {
	    childView = view.findViewById(id);
	    viewHolder.put(id, childView);
	}
	return (T) childView;
    }

    /**
     * activity截图
     * @param act
     * @return
     */
    public static Bitmap capture(Activity act) {
	act.getWindow().getDecorView().setDrawingCacheEnabled(true);
	Bitmap bmp = act.getWindow().getDecorView().getDrawingCache();
	return bmp;
    }

    /**
    * 
    * @param context
    * @return
    */
    public static boolean isBackground(Context context) {

	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	for (RunningAppProcessInfo appProcess : appProcesses) {
	    if (appProcess.processName.equals(context.getPackageName())) {
		if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
		    //		    Log.i(String.format("Background App:", appProcess.processName));
		    return true;
		} else {
		    //		    Log.i(String.format("Foreground App:", appProcess.processName));
		    return false;
		}
	    }
	}
	return false;
    }
}
