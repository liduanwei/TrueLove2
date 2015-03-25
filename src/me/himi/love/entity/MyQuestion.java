package me.himi.love.entity;

import java.util.List;

/**
 * @ClassName:MyQuestion
 * @author sparklee liduanwei_911@163.com
 * @date Nov 23, 2014 9:24:09 PM
 */
public class MyQuestion implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3678244884383126940L;

    private int id;

    private String title;
    private List<String> options;
    private int createTime;
    private String createTimeStr;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public List<String> getOptions() {
	return options;
    }

    public void setOptions(List<String> options) {
	this.options = options;
    }

    public int getCreateTime() {
	return createTime;
    }

    public void setCreateTime(int createTime) {
	this.createTime = createTime;
    }

    public String getCreateTimeStr() {
	return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
	this.createTimeStr = createTimeStr;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    @Override
    public String toString() {
	return this.title;
    }

}
