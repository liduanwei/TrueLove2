package me.himi.love.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName:StringUtils
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:14:59 PM
 */
public class StringUtils {
    public static String md5(String plainText) {
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(plainText.getBytes());
	    byte b[] = md.digest();

	    int i;

	    StringBuffer buf = new StringBuffer("");
	    for (int offset = 0; offset < b.length; offset++) {
		i = b[offset];
		if (i < 0)
		    i += 256;
		if (i < 16)
		    buf.append("0");
		buf.append(Integer.toHexString(i));
	    }
	    return buf.toString();
	} catch (Exception ex) {
	}
	return null;
    }

    /**
     * 取得文件的md5值
     * @param file
     * @return
     */
    public static String md5(File file) {
	InputStream fis;
	byte[] buffer = new byte[1024];
	int numRead = 0;
	MessageDigest md5;
	try {
	    fis = new FileInputStream(file);
	    md5 = MessageDigest.getInstance("MD5");
	    while ((numRead = fis.read(buffer)) > 0) {
		md5.update(buffer, 0, numRead);
	    }
	    fis.close();
	    return toHexString(md5.digest());
	} catch (Exception e) {
	    return null;
	}
    }

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static String toHexString(byte[] b) {
	StringBuilder sb = new StringBuilder(b.length * 2);
	for (int i = 0; i < b.length; i++) {
	    sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
	    sb.append(HEX_DIGITS[b[i] & 0x0f]);
	}
	return sb.toString();
    }

    /**
     * 检查是否为email
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
	String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

	Pattern p = Pattern.compile(strPattern);
	Matcher m = p.matcher(strEmail);
	return m.matches();
    }

    /**
     * 检查指定内容是否为中国大陆手机号码
     * @param strNumber
     * @return
     */
    public static boolean isPhoneNumber(String strNumber) {
	if (strNumber.length() < 11) {
	    return false;
	}
	String strPattern = "^1[3|4|5|8][0-9]\\d{4,8}$";
	Pattern p = Pattern.compile(strPattern);
	Matcher m = p.matcher(strNumber);
	return m.matches(); // 中国手机号11位
    }

    /**
     * 查找手机号
     * @param input
     * @return
     */
    public static String findPhoneNumber(String input) {
	Pattern pat = Pattern.compile("[0-9]{11}");
	Matcher matcher = pat.matcher(input);
	if (matcher.find()) {
	    String number = matcher.group();
//	    String number = input.substring(matcher.start(), matcher.end()); // 可以,正确的
	    return number;
	}
	return null;
    }

    public static String sha1(String s) {
	try {
	    MessageDigest md = MessageDigest.getInstance("SHA1");
	    md.update(s.getBytes());
	    byte b[] = md.digest();

	    int i;

	    StringBuffer buf = new StringBuffer("");
	    for (int offset = 0; offset < b.length; offset++) {
		i = b[offset];
		if (i < 0)
		    i += 256;
		if (i < 16)
		    buf.append("0");
		buf.append(Integer.toHexString(i));
	    }
	    return buf.toString();
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return "";
    }
}
