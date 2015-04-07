package me.himi.love.entity.loader.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;

import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.loader.IUserDetailLoader;
import me.himi.love.util.ActivityUtil;
import me.himi.love.util.Constants;
import me.himi.love.util.ConstellationUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName:RecommendUserLoaderImpl
 * @author sparklee liduanwei_911@163.com
 * @date Nov 4, 2014 9:02:06 PM
 */
public class UserDetailLoaderImpl implements IUserDetailLoader {

    @Override
    public DetailInfoUser load(String response) {

	System.out.println("detailUserInfo response:" + response);
	try {
	    JSONObject userJsonObj = new JSONObject(response);
	    int userId = userJsonObj.getInt("user_id"); // 用户id
	    int gender = userJsonObj.getInt("gender");// 性别
	    int birthday = userJsonObj.getInt("birthday");//出生日期
	    String nickname = userJsonObj.getString("nickname"); // 昵称
	    String monologue = userJsonObj.getString("monologue"); // 独白
	    int height = userJsonObj.getInt("height"); // 身高
	    int weight = userJsonObj.getInt("weight"); // 体重

	    int money = userJsonObj.getInt("money");

	    int isVip = userJsonObj.getInt("is_vip");

	    String distance = userJsonObj.getString("distance"); // 距离
	    String monthlySalary = userJsonObj.getString("monthly_salary"); // 月薪
	    String homeplace = userJsonObj.getString("homeplace"); // 家乡
	    String address = userJsonObj.getString("address"); // 现居地
	    String bloodType = userJsonObj.getString("blood"); /// 血型
	    String employment = userJsonObj.getString("employment"); // 职业
	    String charm_body = userJsonObj.getString("charm_body"); // 
	    String house = userJsonObj.getString("house");
	    String education = userJsonObj.getString("education");
	    String distance_love = userJsonObj.getString("distance_love");
	    String opposite_sex_type = userJsonObj.getString("opposite_sex_type");
	    String premartial_sex = userJsonObj.getString("premartial_sex");
	    String live_with_parents = userJsonObj.getString("live_with_parents");
	    String want_baby = userJsonObj.getString("want_baby");
	    String matrital_status = userJsonObj.getString("marital_status");
	    String interests = userJsonObj.getString("interests");
	    String personality = userJsonObj.getString("personality");

	    String imUserName = userJsonObj.getString("im_username");

	    String instruction = userJsonObj.getString("instr");
	    String oftenAddress = userJsonObj.getString("often_addr");

	    String lastAddr = userJsonObj.getString("last_addr");

	    // 征友设置
	    String wantHomeplace = userJsonObj.getString("want_homeplace");
	    String wantAddress = userJsonObj.getString("want_address");
	    String wantAge = userJsonObj.getString("want_age");
	    String wantHeight = userJsonObj.getString("want_height");
	    String wantEducation = userJsonObj.getString("want_education");
	    String wantMonthSalary = userJsonObj.getString("want_salary");
	    String wantAdditional = userJsonObj.getString("want_additional");

	    String faceUrl = !userJsonObj.isNull("face_url") ? userJsonObj.getString("face_url") : "http://love.leavtechintl.com/Public/USERS_UPLOAD_FILES/liduanwei_911@163.com/nophoto.gif";

	    DetailInfoUser user = new DetailInfoUser();

	    GregorianCalendar birthdayCalendar = ActivityUtil.newGregorianCalendar(birthday);

	    GregorianCalendar nowCalendar = ActivityUtil.newGregorianCalendar(System.currentTimeMillis());

	    int age = nowCalendar.get(Calendar.YEAR) - birthdayCalendar.get(Calendar.YEAR);
	    // 修正 出生月份是否达到
	    age -= nowCalendar.get(Calendar.MONTH) >= birthdayCalendar.get(Calendar.MONTH) ? 0 : 1;

	    int birthdayYear = birthdayCalendar.get(Calendar.YEAR);
	    int birthdayMonth = birthdayCalendar.get(Calendar.MONTH) + 1; //月份 0-11 
	    int birthdayDay = birthdayCalendar.get(Calendar.DAY_OF_MONTH);

	    user.setBirthdayYear(birthdayYear);
	    user.setBirthdayMonth(birthdayMonth);
	    user.setBirthdayDay(birthdayDay);

	    // 星座
	    user.setConstellation(ConstellationUtils.getName(ConstellationUtils.get(birthdayMonth, birthdayDay)));

	    user.setUserId(userId);
	    user.setGender(gender);
	    user.setAge(age);
	    user.setNickname(nickname);
	    user.setHeight(height);
	    user.setWeight(weight);

	    user.setLoveMoney(money); // 爱心币

	    user.setVip(isVip == 1);

	    user.setInstruction(instruction);
	    user.setOftenAddress(oftenAddress);

	    user.setDistance(distance);

	    user.setMonologue(monologue.length() == 0 ? "" : monologue);
	    user.setMonthlySalary(monthlySalary);
	    user.setHomeplace(homeplace);
	    user.setAddress(address);
	    user.setBlood(bloodType);
	    user.setEmployment(employment);
	    user.setCharmBody(charm_body);
	    user.setEducation(education);
	    user.setOppositeSexType(opposite_sex_type);
	    user.setPremartialSex(premartial_sex);
	    user.setLiveWithParents(live_with_parents);
	    user.setWantBaby(want_baby);
	    user.setMartialStatus(matrital_status);
	    user.setDistanceLove(distance_love);
	    user.setHouse(house);
	    user.setInterests(interests);
	    user.setPersonalities(personality);

	    user.setImUserName(imUserName);

	    user.setWantHomeplace(wantHomeplace);
	    user.setWantAddress(wantAddress);
	    user.setWantAge(wantAge);
	    user.setWantHeight(wantHeight);
	    user.setWantEducation(wantEducation);
	    user.setWantMonthSalary(wantMonthSalary);
	    user.setWantAddtional(wantAdditional);

	    if (faceUrl.startsWith(".")) { // 加上 http://主机地址/Public/...
		faceUrl = Constants.HOST + faceUrl.substring(1);
	    }
	    user.setFaceUrl(faceUrl);

	    // 
	    String sigupTime = userJsonObj.getString("register_time");
	    user.setRegisterTime(sigupTime);
	    String loginTime = userJsonObj.getString("lastlogin_time");
	    user.setLastLogintime(loginTime);
	    user.setLastAddress(lastAddr);

	    return user;

	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }
}
