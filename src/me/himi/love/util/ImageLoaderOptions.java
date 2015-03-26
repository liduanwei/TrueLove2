package me.himi.love.util;

import me.himi.love.R;
import me.himi.love.imageloader.CircleBitmapDisplayer;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageLoaderOptions {

    public static DisplayImageOptions rounderOptions() {
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	// // 设置图片在下载期间显示的图片
		.showStubImage(R.drawable.loadingpic)
		// // 设置图片Uri为空或是错误的时候显示的图片
		.showImageForEmptyUri(R.drawable.errorpic)
		// // 设置图片加载/解码过程中错误时候显示的图片
		.showImageOnFail(R.drawable.errorpic).cacheInMemory(true)
		// 设置下载的图片是否缓存在内存中
		//		.cacheOnDisc(true)
		.cacheOnDisk(true)
		// 设置下载的图片是否缓存在SD卡中
		//.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
		// .decodingOptions(android.graphics.BitmapFactory.Options
		// decodingOptions)//设置图片的解码配置
		//.considerExifParams(true)
		// 设置图片下载前的延迟
		// .delayBeforeLoading(int delayInMillis)//int
		// delayInMillis为你设置的延迟时间
		// 设置图片加入缓存前，对bitmap进行设置
		// 。preProcessor(BitmapProcessor preProcessor)
		.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
		//.displayer(new RoundedBitmapDisplayer(25))//是否设置为圆角，弧度为多少
		.displayer(new FadeInBitmapDisplayer(500))// 淡入
		//		.displayer(new RoundedBitmapDisplayer(25))
		.displayer(new CircleBitmapDisplayer()).build();

	return options;
    }

    public static DisplayImageOptions normalOptions() {
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	// // 设置图片在下载期间显示的图片
		.showStubImage(R.drawable.loadingpic)
		// // 设置图片Uri为空或是错误的时候显示的图片
		.showImageForEmptyUri(R.drawable.errorpic)
		// // 设置图片加载/解码过程中错误时候显示的图片
		.showImageOnFail(R.drawable.errorpic).cacheInMemory(true)
		// 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true)
		// 设置下载的图片是否缓存在SD卡中
		//.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
		// .decodingOptions(android.graphics.BitmapFactory.Options
		// decodingOptions)//设置图片的解码配置
		//.considerExifParams(true)
		// 设置图片下载前的延迟
		// .delayBeforeLoading(int delayInMillis)//int
		// delayInMillis为你设置的延迟时间
		// 设置图片加入缓存前，对bitmap进行设置
		// 。preProcessor(BitmapProcessor preProcessor)
		.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
		//.displayer(new RoundedBitmapDisplayer(25))//是否设置为圆角，弧度为多少
		.displayer(new FadeInBitmapDisplayer(200))// 淡入
		//.displayer(new RoundedBitmapDisplayer(25))
		.build();

	return options;
    }

    public static DisplayImageOptions circleOptions() {
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	// // 设置图片在下载期间显示的图片
		.showStubImage(R.drawable.loadingpic)
		// // 设置图片Uri为空或是错误的时候显示的图片
		.showImageForEmptyUri(R.drawable.loadingpic)
		// // 设置图片加载/解码过程中错误时候显示的图片
		.showImageOnFail(R.drawable.errorpic).cacheInMemory(true)
		// 设置下载的图片是否缓存在内存中
		.cacheOnDisk(true)
		// 设置下载的图片是否缓存在SD卡中
		//.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
		// .decodingOptions(android.graphics.BitmapFactory.Options
		// decodingOptions)//设置图片的解码配置
		//.considerExifParams(true)
		// 设置图片下载前的延迟
		// .delayBeforeLoading(int delayInMillis)//int
		// delayInMillis为你设置的延迟时间
		// 设置图片加入缓存前，对bitmap进行设置
		// 。preProcessor(BitmapProcessor preProcessor)
		.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
		//.displayer(new RoundedBitmapDisplayer(25))//是否设置为圆角，弧度为多少
		.displayer(new FadeInBitmapDisplayer(500))// 淡入
		.displayer(new CircleBitmapDisplayer(0)).build();

	return options;
    }

}
