package me.himi.love;

/**
 * @ClassName:IAppServiceExtend
 * @author sparklee liduanwei_911@163.com
 * @date Nov 20, 2014 4:11:34 PM
 */
public interface IAppServiceBuy {

    /**
     * 购买恋恋币
     * @param postParams
     * @param listener
     */
    void buyedLoveMoney(OnBuyedLoveMoneyPostParams postParams, OnBuyedLoveMoneyResponseListener listener);

    public static class OnBuyedLoveMoneyPostParams {
	public int moneyType; // 购买的恋恋币数量类型,0:10,1:50,...

	public String orderId; // 万普支付 订单id
	public String payType;// 万普支付 支付类型

	public double amount;// 支付金额

    }

    public interface OnBuyedLoveMoneyResponseListener {
	void onSuccess(String result);

	void onFailure(String errorMsg);
    }

    /**
     * 购买vip
     * @param postParams
     * @param listener
     */
    void buyedVip(OnBuyedVipPostParams postParams, OnBuyedVipResponseListener listener);

    public static class OnBuyedVipPostParams {
	public int vipType; // 购买的会员时长类型,0:1个月,1:3个月,2:6个月,3:12个月,...

	public String orderId; // 万普支付 订单id
	public String payType;// 万普支付 支付类型

	public double amount;// 支付金额

    }

    public interface OnBuyedVipResponseListener {
	void onSuccess(String result);

	void onFailure(String errorMsg);
    }
}
