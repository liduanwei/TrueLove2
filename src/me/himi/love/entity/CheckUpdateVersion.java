package me.himi.love.entity;

import java.io.Serializable;

/**
 * @ClassName:CheckUpdateVersion
 * @author sparklee liduanwei_911@163.com
 * @date Nov 11, 2014 8:45:50 AM
 */
public class CheckUpdateVersion implements Serializable {

    private static final long serialVersionUID = -5156469397493301668L;
    private int newestVersion; // 版本号
    private String versionName;// // 版本名称
    private String updateInstruction; // 新版本说明
    private String downloadUrl; /// 新版本下载地址
    private String date;// 文件更新日期
    private String md5;// 文件md5
    private int size;// 文件大小

    public int getNewestVersion() {
	return newestVersion;
    }

    public void setNewestVersion(int newestVersion) {
	this.newestVersion = newestVersion;
    }

    public String getUpdateInstruction() {
	return updateInstruction;
    }

    public void setUpdateInstruction(String updateInstruction) {
	this.updateInstruction = updateInstruction;
    }

    public String getDownloadUrl() {
	return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
	this.downloadUrl = downloadUrl;
    }

    public String getVersionName() {
	return versionName;
    }

    public void setVersionName(String versionName) {
	this.versionName = versionName;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public String getMd5() {
	return md5;
    }

    public void setMd5(String md5) {
	this.md5 = md5;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }


}
