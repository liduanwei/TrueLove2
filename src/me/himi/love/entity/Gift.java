package me.himi.love.entity;

/**
 * 
 * @ClassName:Gift
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 8:56:37 PM
 */
public class Gift implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String giftId;

    private String name;

    private int price;// 售价
    private int glamour;// 魅力值

    private String imageUrl;

    private String withWord; // 自身附带的赠言

    public String getGiftId() {
	return giftId;
    }

    public void setGiftId(String giftId) {
	this.giftId = giftId;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getWithWord() {
	return withWord;
    }

    public void setWithWord(String withWord) {
	this.withWord = withWord;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getPrice() {
	return price;
    }

    public void setPrice(int price) {
	this.price = price;
    }

    public int getGlamour() {
	return glamour;
    }

    public void setGlamour(int glamour) {
	this.glamour = glamour;
    }
}
