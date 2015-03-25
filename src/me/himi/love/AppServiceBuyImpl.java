package me.himi.love;

import me.himi.love.util.Constants;
import me.himi.love.util.HttpUtil;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @ClassName:IAppServiceExtend
 * @author sparklee liduanwei_911@163.com
 * @date Nov 20, 2014 4:11:34 PM
 */
public class AppServiceBuyImpl implements IAppServiceBuy {

    private static IAppServiceBuy instance;

    public synchronized static IAppServiceBuy getInstance() {
	if (instance == null) {
	    instance = new AppServiceBuyImpl();
	}
	return instance;
    }

    @Override
    public void buyedLoveMoney(OnBuyedLoveMoneyPostParams postParams, final OnBuyedLoveMoneyResponseListener listener) {
	// TODO Auto-generated method stub
	RequestParams params = new RequestParams();
	params.put("money_type", postParams.moneyType);
	params.put("order_id", postParams.orderId);
	params.put("pay_type", postParams.payType);
	params.put("amount", postParams.amount);

	HttpUtil.post(Constants.URL_LOVE_MONEY_BUYED, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String response = new String(arg2);
		System.out.println("buyLoveMoney:" + response);
		listener.onSuccess(response);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }
	});
    }

    @Override
    public void buyedVip(OnBuyedVipPostParams postParams, final OnBuyedVipResponseListener listener) {
	// TODO Auto-generated method stub
	RequestParams params = new RequestParams();
	params.put("vip_type", postParams.vipType);
	params.put("order_id", postParams.orderId);
	params.put("pay_type", postParams.payType);
	params.put("amount", postParams.amount);

	HttpUtil.post(Constants.URL_VIP_BUYED, params, new AsyncHttpResponseHandler() {

	    @Override
	    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub
		String response = new String(arg2);
		System.out.println("buyVip:" + response);
		listener.onSuccess(response);
	    }

	    @Override
	    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		// TODO Auto-generated method stub
		listener.onFailure("连接超时");
	    }
	});
    }

}
