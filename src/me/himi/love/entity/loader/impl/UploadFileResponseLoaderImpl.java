package me.himi.love.entity.loader.impl;

import me.himi.love.IAppService.UploadFaceResponse;
import me.himi.love.entity.loader.IUploadFileResponseLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:LoginedUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class UploadFileResponseLoaderImpl implements IUploadFileResponseLoader {

    @Override
    public UploadFaceResponse load(String response) {

	System.out.println("upload" + response);
	try {
	    JSONObject fileJsonObj = new JSONObject(response);

	    int status = fileJsonObj.getInt("status");

	    if (0 == status) {// 上传失败
		return null;
	    }
	    UploadFaceResponse uploadFileResponse = new UploadFaceResponse();
	    uploadFileResponse.imageUrl = fileJsonObj.getString("image_url");
	    uploadFileResponse.review = fileJsonObj.getInt("review");
	    uploadFileResponse.faceId = fileJsonObj.getInt("face_id");

	    return uploadFileResponse;

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return null;
    }

}
