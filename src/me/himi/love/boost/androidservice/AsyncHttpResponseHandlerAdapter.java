package me.himi.love.boost.androidservice;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.loopj.android.http.ResponseHandlerInterface;

/**
 * @ClassName:AsyncHttpResponseHandlerAdapter
 * @author sparklee liduanwei_911@163.com
 * @date Nov 21, 2014 6:12:14 PM
 */
public class AsyncHttpResponseHandlerAdapter implements ResponseHandlerInterface {

    @Override
    public Header[] getRequestHeaders() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public URI getRequestURI() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean getUseSynchronousMode() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void onPostProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface arg0, HttpResponse arg1) {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendCancelMessage() {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendFailureMessage(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendFinishMessage() {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendProgressMessage(int arg0, int arg1) {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendResponseMessage(HttpResponse arg0) throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendRetryMessage(int arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendStartMessage() {
	// TODO Auto-generated method stub

    }

    @Override
    public void sendSuccessMessage(int arg0, Header[] arg1, byte[] arg2) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setRequestHeaders(Header[] arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setRequestURI(URI arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void setUseSynchronousMode(boolean arg0) {
	// TODO Auto-generated method stub

    }

}
