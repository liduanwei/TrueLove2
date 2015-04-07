package me.himi.love.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * @ClassName:CacheUtils
 * @author sparklee liduanwei_911@163.com
 * @date Mar 29, 2015 9:24:47 PM
 */
public class CacheUtils {

    /**
     * 写入本地缓存文件
     * @param list
     * @param path
     */
    public static void cacheToLocal(List list, String path) {
	// TODO Auto-generated method stub
	File f = new File(path);
	if (!f.getParentFile().exists()) {
	    f.getParentFile().mkdirs();
	}
	try {
	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
	    oos.writeObject(list);
	    oos.close();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static <T> List<T> loadFromCache(String cachedPath) {
	// TODO Auto-generated method stub
	File f = new File(cachedPath);

	if (f.exists()) {

	    try {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));

		Object obj = ois.readObject();

		List<T> list = (List<T>) obj;

		ois.close();
		return list;

	    } catch (StreamCorruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    return null;
	} else {
	    // 不存在则从网络获取
	    return null;
	}

    }
}
