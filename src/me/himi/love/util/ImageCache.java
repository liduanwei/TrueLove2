package me.himi.love.util;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * @ClassName:ImageCache
 * @author sparklee liduanwei_911@163.com
 * @date Nov 16, 2014 12:57:39 PM
 */
public class ImageCache {
    public static final Map<String, Bitmap> cachedBitmaps = new HashMap<String, Bitmap>();
    private static ImageCache instance;

    public static ImageCache getInstance() {
	if (null == instance) {
	    instance = new ImageCache();
	}
	return instance;
    }

    public void addBitmap(String key, Bitmap bitmap) {
	if (!cachedBitmaps.containsKey(key)) {
	    cachedBitmaps.put(key, bitmap);
	}
    }
}
