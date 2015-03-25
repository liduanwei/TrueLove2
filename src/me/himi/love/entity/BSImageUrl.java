package me.himi.love.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 含大小两张图的 url
 * @ClassName:BSImageUrl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 10, 2014 2:37:08 PM
 */
public class BSImageUrl implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 128480388786953321L;
    private String bigImageUrl, smallImageUrl;

    public String getBigImageUrl() {
	return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
	this.bigImageUrl = bigImageUrl;
    }

    public String getSmallImageUrl() {
	return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
	this.smallImageUrl = smallImageUrl;
    }

    public BSImageUrl() {
	// TODO Auto-generated constructor stub
    }

    public BSImageUrl(String big, String small) {
	this.bigImageUrl = big;
	this.smallImageUrl = small;
    }

}
