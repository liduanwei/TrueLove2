package me.himi.love.entity;

import java.util.List;

/**
 * 解析器需要用到的, 对应服务器响应的数据内容结构
 * 获取用户留言评论服务器会响应的内容:当前留言的所有评论数 + 指定页面的所有评论
 * @ClassName:LoadUserNewsComments
 * @author sparklee liduanwei_911@163.com
 * @date Nov 11, 2014 10:42:17 PM
 */
public class LoadUserNewsComments implements java.io.Serializable {

    private List<UserNewsComment> comments;
    private int totalCommentCount;
    
    private String newsPostTime;

    public int getTotalCommentCount() {
	return totalCommentCount;
    }

    public void setTotalCommentCount(int totalCommentCount) {
	this.totalCommentCount = totalCommentCount;
    }

    public List<UserNewsComment> getComments() {
	return comments;
    }

    public void setComments(List<UserNewsComment> comments) {
	this.comments = comments;
    }

    public String getNewsPostTime() {
	return newsPostTime;
    }

    public void setNewsPostTime(String newsPostTime) {
	this.newsPostTime = newsPostTime;
    }

}
