package me.himi.love.entity.loader.impl;

import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.entity.loader.ICheckUpdateLoader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:CheckUpdateLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class CheckUpdateLoaderImpl implements ICheckUpdateLoader {

    @Override
    public CheckUpdateVersion load(String response) {

	try {
	    JSONObject jsonObj = new JSONObject(response);
	    int status = jsonObj.getInt("has_new_version");
	    if (1 != status) {
		return null;
	    }

	    int version = jsonObj.getInt("version");
	    String versionName = jsonObj.getString("version_name");
	    String downloadUrl = jsonObj.getString("down_url");
	    String instruction = jsonObj.getString("update_instruction");
	    String date = jsonObj.getString("date");
	    String md5 = jsonObj.getString("md5");
	    int size = jsonObj.getInt("size");

	    CheckUpdateVersion newVersion = new CheckUpdateVersion();
	    newVersion.setDownloadUrl(downloadUrl);
	    newVersion.setUpdateInstruction(instruction);
	    newVersion.setNewestVersion(version);
	    newVersion.setVersionName(versionName);
	    newVersion.setDate(date);
	    newVersion.setMd5(md5);
	    newVersion.setSize(size);

	    return newVersion;

	} catch (JSONException e) {
	    e.printStackTrace();
	}

	return null;
    }
}
