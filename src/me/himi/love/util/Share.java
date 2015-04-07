package me.himi.love.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import me.himi.love.R;
import me.himi.love.entity.Article;
import me.himi.love.entity.StrangeNews;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * @ClassName:Share
 * @author sparklee liduanwei_911@163.com
 * @date Feb 5, 2015 9:30:14 PM
 */
public class Share {
    static {

    }

    private static boolean isShowing;// 展示中...

    public static void share(final Activity act, String content) {
	ShareSDK.initSDK(act);
	final OnekeyShare oks = new OnekeyShare();
	//关闭sso授权
	oks.disableSSOWhenAuthorize();

	// 分享时Notification的图标和文字
	oks.setNotification(R.drawable.icon, act.getString(R.string.app_name));
	// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	oks.setTitle(act.getString(R.string.app_name));
	// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	oks.setTitleUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// text是分享文本，所有平台都需要这个字段
	oks.setText(content + "#APP下载地址 http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数

	final Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case 1:
		    String path = msg.obj.toString();
		    // 本地图片(路径)
		    oks.setImagePath(path);//确保SDcard下面存在此张图片
		    //		    // 启动分享GUI
		    //		    oks.show(act);
		    break;
		}
		super.handleMessage(msg);
	    }
	};

	final Bitmap bmp = ActivityUtil.capture(act);

	new Thread() {
	    @Override
	    public void run() {
		try {
		    String pngPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.truelove/" + "screenshot.png";
		    File f = new File(pngPath);

		    if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		    }
		    bmp.compress(CompressFormat.PNG, 100, new FileOutputStream(pngPath));
		    handler.obtainMessage(1, pngPath).sendToTarget();

		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}.start();

	// 网络图片
	//		oks.setImageUrl("http://ali.xinshipu.cn/20100811/original/1281479630468.jpg@288w_216h_99q_1e_1c.jpg");//确保SDcard下面存在此张图片
	// url仅在微信（包括好友和朋友圈）中使用
	oks.setUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// comment是我对这条分享的评论，仅在人人网和QQ空间使用
	// 助依然守候的你在茫茫人海中找到相守一生对的那个Ta
	oks.setComment(act.getResources().getString(R.string.app_name) + ",帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\"");
	// site是分享此内容的网站名称，仅在QQ空间使用
	oks.setSite(act.getString(R.string.app_name));
	// siteUrl是分享此内容的网站地址，仅在QQ空间使用
	oks.setSiteUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");

	//	// 启动分享GUI
	oks.setDialogMode();
	//	oks.setTheme(OnekeyShareTheme.SKYBLUE);
	oks.show(act);

	//	oks.setOnShareButtonClickListener(new OnShareButtonClickListener() {
	//	    
	//	    @Override
	//	    public void onClick(View v, List<Object> checkPlatforms) {
	//		// TODO Auto-generated method stub
	//		
	//	    }
	//	});

    }

    public static void share(Context context, Article article) {
	ShareSDK.initSDK(context);
	final OnekeyShare oks = new OnekeyShare();
	//关闭sso授权
	oks.disableSSOWhenAuthorize();

	// 分享时Notification的图标和文字
	oks.setNotification(R.drawable.icon, context.getString(R.string.app_name));
	// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	oks.setTitle(context.getString(R.string.app_name));
	// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	oks.setTitleUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// text是分享文本，所有平台都需要这个字段
	oks.setText("#恋恋# " + article.getContent() + "#分享自" + context.getResources().getString(R.string.app_name) + " APP下载地址 http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	oks.setImageUrl(article.getContentImageUrl().getSmallImageUrl());
	// 网络图片
	//		oks.setImageUrl("http://ali.xinshipu.cn/20100811/original/1281479630468.jpg@288w_216h_99q_1e_1c.jpg");//确保SDcard下面存在此张图片
	// url仅在微信（包括好友和朋友圈）中使用
	oks.setUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// comment是我对这条分享的评论，仅在人人网和QQ空间使用
	// 助依然守候的你在茫茫人海中找到相守一生对的那个Ta
	oks.setComment(context.getResources().getString(R.string.app_name) + ",帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\"");
	// site是分享此内容的网站名称，仅在QQ空间使用
	oks.setSite(context.getString(R.string.app_name));
	// siteUrl是分享此内容的网站地址，仅在QQ空间使用
	oks.setSiteUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");

	//	// 启动分享GUI
	//	oks.setTheme(OnekeyShareTheme.SKYBLUE);
	oks.show(context);

    }

    public static void share(Context context, StrangeNews article) {
	ShareSDK.initSDK(context);
	final OnekeyShare oks = new OnekeyShare();
	//关闭sso授权
	oks.disableSSOWhenAuthorize();

	// 分享时Notification的图标和文字
	oks.setNotification(R.drawable.icon, context.getString(R.string.app_name));
	// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	oks.setTitle(context.getString(R.string.app_name));
	// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	oks.setTitleUrl("http://love5.leavtechintl.com/Public/TrueLove2.apk");
	// text是分享文本，所有平台都需要这个字段
	oks.setText(article.getTitle() + "--" + article.getSummary() + " #分享自" + context.getResources().getString(R.string.app_name) + " APP下载地址 http://love5.leavtechintl.com/Public/TrueLove2.apk, ");
	// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	oks.setImageUrl(article.getContentImageUrl().getSmallImageUrl());
	// 网络图片
	//		oks.setImageUrl("http://ali.xinshipu.cn/20100811/original/1281479630468.jpg@288w_216h_99q_1e_1c.jpg");//确保SDcard下面存在此张图片
	// url仅在微信（包括好友和朋友圈）中使用
	oks.setUrl("http://love5.leavtechintl.com/index.php/index/strange/detail?id=" + article.getId());
	// comment是我对这条分享的评论，仅在人人网和QQ空间使用
	// 助依然守候的你在茫茫人海中找到相守一生对的那个Ta
	oks.setComment(context.getResources().getString(R.string.app_name) + ",帮你发现身边的陌生朋友,寻找即将与自己相守一生的另一半,\"等你发现,真爱就在这里\"");
	// site是分享此内容的网站名称，仅在QQ空间使用
	oks.setSite(context.getString(R.string.app_name));
	// siteUrl是分享此内容的网站地址，仅在QQ空间使用
	oks.setSiteUrl("http://love5.leavtechintl.com/index.php/index/strange/detail?id=" + article.getId());

	//	// 启动分享GUI
	//	oks.setTheme(OnekeyShareTheme.SKYBLUE);
	oks.show(context);
    }

}
