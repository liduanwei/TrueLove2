package me.himi.love;

import java.util.List;

import me.himi.love.entity.CheckUpdateVersion;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.entity.LoadUserNewsComments;
import me.himi.love.entity.LoginedUser;
import me.himi.love.entity.RegisteredUser;
import me.himi.love.entity.UserNews;
import me.himi.love.entity.UserNewsComment;
import me.himi.love.entity.VisitorUser;

/**
 * 应用主业务接口
 * @ClassName:AppService
 * @author sparklee liduanwei_911@163.com
 * @date Nov 6, 2014 6:57:16 PM
 */
public interface IAppService {

    /**
     * 注册
     * @param user
     * @param resHandler
     */
    void register(RegisterUser user, OnRegisterListener onRegisterListener);

    /**
     * 注册 完善资料
     * @param user
     * @param resHandler
     */
    void registerPerfectComplete(PerfectCompleetePostParams postParams, OnPerfectCompleteResponseListener listener);

    public static class PerfectCompleetePostParams {
	public int brithday;
	public String nickname;
	public int gender;
    }

    public interface OnPerfectCompleteResponseListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * 登录
     * @param userId
     * @param password
     * @param longtitude
     * @param latitude
     * @param address
     * @param onLoginListener
     */
    void login(String userId, String password, String longtitude, String latitude, String address, OnLoginListener onLoginListener);

    /**
     * 更新资料
     * @param user
     * @param resHandler
     */
    void update(UserInfo user, OnSimpleListener onUpdateListener);

    /**
     * 获取指定用户的详细资料
     * @param userId
     * @param resHandler
     */
    void loadUserInfo(int userId, OnLoadDetailUserListener onLoadDetailUserListener);

    /**
     * 发布留言
     * @param listener
     */
    void publishNews(PublishNewsParams news, OnSimpleListener listener);

    /**
     * 获取指定用户的留言列表
     * @param userId
     */
    void loadNewsList(UserNewsParams params, OnNewsListener listener);

    /**
     * 发布评论
     * @param publishParams
     * @param listener
     */
    void publishNewsComment(PublishNewsCommentParams publishParams, OnPublishNewsCommentListener listener);

    /**
     *获取 指定 用户指定留言ID的 所有评论
     * @param params
     * @param listener
     */
    void loadNewsComments(UserNewsCommentParams params, OnNewsCommentListener listener);

    /**
     * 登出
     * @param resHandler
     */
    void logout(OnLogoutListener onLogoutListener);

    /**
     * 获取用户的访客
     * @param params
     */
    void loadUserVisitors(UserVisitorsParams params, OnLoadUserVisitorsListener listener);

    /**
     * 上传头像
     */
    void uploadFace(UploadFileParams params, OnUploadFaceListener listener);

    /**
     * 上传其他图片
     */
    void uploadPhoto(UploadFileParams params, OnUploadPhotosListener listener);

    /**
     * 
     * @param params
     * @param listener
     */
    void checkUpdate(CheckUpdateParams params, OnCheckUpdateListener listener);

    /**
     * 上传头像的监听器
     * @author sparklee
     *
     */
    public interface OnUploadFaceListener {
	void onSuccess(UploadFaceResponse uploadFileResponse);

	void onFailure(String errorMsg);
    }

    /**
     * 上传图片的监听器
     * @author sparklee
     *
     */
    public interface OnUploadPhotosListener {
	void onSuccess(UploadPhotosResponse uploadPhotosResponse);

	void onFailure(String errorMsg);
    }

    /**
     * 上传单个头像后服务器响应的数据 结构, 可以得到上传后头像的路径
     * @author sparklee
     *
     */
    public static class UploadFaceResponse {
	public String imageUrl; // 文件名
	public int faceId;
	public int review; //

    }

    /**
     * 上传多个图片后服务器响应的数据结构
     * @author sparklee
     *
     */
    public static class UploadPhotosResponse {
	public List<String> imageUrls; // 所有文件名

    }

    public interface OnLoadUserVisitorsListener {
	void onSuccess(List<VisitorUser> user);

	void onFailure(String errorMsg);
    }

    /**
     * 发布评论的监听器
     * @author sparklee
     *
     */
    public interface OnPublishNewsCommentListener {
	void onSuccess(UserNewsComment comment);

	void onFailure(String errorMsg);
    }

    /**
     * 
     * @author sparklee
     *
     */
    public interface OnCheckUpdateListener {
	void onSuccess(CheckUpdateVersion v);

	void onFailure(String errormsg);
    }

    /**
     * 获取用户留言监听器
     * @author sparklee
     *
     */
    public interface OnNewsListener {
	void onSuccess(List<UserNews> news);

	void onFailure(String errorMsg);
    }

    /**
     * 获取留言评论监听器
     * @author sparklee
     *
     */
    public interface OnNewsCommentListener {
	void onSuccess(LoadUserNewsComments comments);

	void onFailure(String errorMsg);
    }

    /**
     * 注册结果监听器
     * @author sparklee
     *
     */
    public interface OnRegisterListener {
	void onSuccess(RegisteredUser registeredUser);

	void onFailure(String errorMsg);
    }

    /**
     * 登录结果监听器
     * @author sparklee
     *
     */
    public interface OnLoginListener {
	void onSuccess(LoginedUser loginedUser);

	void onFailure(String errorMsg);
    }

    /**
     * 更新监听器
     * @author sparklee
     *
     */
    public interface OnSimpleListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    /**
     * 获取详细用户资料回调监听器
     * @author sparklee
     *
     */
    public interface OnLoadDetailUserListener {
	void onSuccess(DetailInfoUser detailInfoUser);

	void onFailure(String errorMsg);
    }

    /**
     * 登出回调监听器
     * @author sparklee
     *
     */
    public interface OnLogoutListener {
	void onSuccess();

	void onFailure(String errorMsg);
    }

    public static class UploadFileParams {
	public List<String> filePathes;
	public int userId;
    }

    /**
     * 检测更新需要的参数
     * @author sparklee
     *
     */
    public static class CheckUpdateParams {
	public int currentVersion;
    }

    /**
     * 发布评论所需的参数
     * @author sparklee
     *
     */
    public static class PublishNewsCommentParams {
	public int newsId; // 留言的 ID
	public int newsUserId;// 留言的作者的ID, 用来查询该条留言是否存在
	public String content;// 评论的内容
	public int replyCommentId; // 回复的评论ID

    }

    /**
     * 获取用户留言的评论需要的参数
     * @author sparklee
     *
     */
    public static class UserNewsCommentParams {
	public int userId; // 留言作者的ID
	public int newsId; // 目标留言的ID
	public int page;
	public int pageSize;

    }

    /**
     * 获取用户访客需要的参数
     * @author sparklee
     *
     */
    public static class UserVisitorsParams {
	public int userId;
	public int page;
	public int pageSize;
    }

    /**
     * 获取用户留言需要的参数字段
     * @author sparklee
     *
     */
    public static class UserNewsParams {
	public int userId;
	public int page;
	public int pageSize;
    }

    /**
     * 发布留言需要的参数(字段)
     * @author sparklee
     *
     */
    public static class PublishNewsParams {
	public String title;
	public String content;
	public String longitude;
	public String latitude;
	public String address;
	public int isPrivate; // 是否公开
	public int isAllowComment;// 是否允许评论
	public List<String> imageUrls; // 上传的所有图片地址 

    }

    /**
     * 注册用户所需的字段
     * @author sparklee
     *
     */
    public static class RegisterUser {
	private String username, password;

	private String imei;
	private String phoneNumber;
	
	
	private String model;// 设备型号 

	private String longtitude, latitude;//经纬度

	public String getImei() {
	    return imei;
	}

	public void setImei(String imei) {
	    this.imei = imei;
	}

	public String getPhoneNumber() {
	    return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
	    this.phoneNumber = phoneNumber;
	}

	public String getLongtitude() {
	    return longtitude;
	}

	public void setLongtitude(String longtitude) {
	    this.longtitude = longtitude;
	}

	public String getLatitude() {
	    return latitude;
	}

	public void setLatitude(String latitude) {
	    this.latitude = latitude;
	}

	public String getUsername() {
	    return username;
	}

	public void setUsername(String username) {
	    this.username = username;
	}

	public String getPassword() {
	    return password;
	}

	public void setPassword(String password) {
	    this.password = password;
	}

	public String getModel() {
	    return model;
	}

	public void setModel(String model) {
	    this.model = model;
	}

    }

    /**
     * 更新用户资料 所需的 字段
     * @author sparklee
     *
     */
    public static class UserInfo {
	private int userId;
	private int gender;
	private int birthday;
	private String monologue;
	private String nickname;
	private String education;
	private String homeplace;
	private String address;
	private int height;
	private int weight;
	private String blood;

	private String monthlySalary;
	private String employment;
	private String charmBody;
	private String house;
	private String distanceLove;
	private String oppositeSexType;
	private String premartialSex;
	private String liveWithParents;
	private String wantBaby;
	private String martialStatus;
	private String faceUrl;
	private String interests;
	private String personalities;

	private String instruction, oftenAddress;

	private String deviceId, appId; // 设备ID,appId

	private int faceId;// 头像id ,上传到服务器后的文件路径

	public int getUserId() {
	    return userId;
	}

	public void setUserId(int userId) {
	    this.userId = userId;
	}

	public int getGender() {
	    return gender;
	}

	public void setGender(int gender) {
	    this.gender = gender;
	}

	public String getMonologue() {
	    return monologue;
	}

	public void setMonologue(String monologue) {
	    this.monologue = monologue;
	}

	public String getNickname() {
	    return nickname;
	}

	public void setNickname(String nickname) {
	    this.nickname = nickname;
	}

	public void setFaceUrl(String faceUrl) {
	    this.faceUrl = faceUrl;
	}

	public String getEducation() {
	    return education;
	}

	public void setEducation(String education) {
	    this.education = education;
	}

	public int getHeight() {
	    return height;
	}

	public void setHeight(int height) {
	    this.height = height;
	}

	public int getWeight() {
	    return weight;
	}

	public void setWeight(int weight) {
	    this.weight = weight;
	}

	public String getHomeplace() {
	    return homeplace;
	}

	public void setHomeplace(String homeplace) {
	    this.homeplace = homeplace;
	}

	public String getAddress() {
	    return address;
	}

	public void setAddress(String address) {
	    this.address = address;
	}

	public String getBlood() {
	    return blood;
	}

	public void setBlood(String blood) {
	    this.blood = blood;
	}

	public String getMonthlySalary() {
	    return monthlySalary;
	}

	public void setMonthlySalary(String monthlySalary) {
	    this.monthlySalary = monthlySalary;
	}

	public String getEmployment() {
	    return employment;
	}

	public void setEmployment(String employment) {
	    this.employment = employment;
	}

	public String getCharmBody() {
	    return charmBody;
	}

	public void setCharmBody(String charmBody) {
	    this.charmBody = charmBody;
	}

	public String getHouse() {
	    return house;
	}

	public void setHouse(String house) {
	    this.house = house;
	}

	public String getDistanceLove() {
	    return distanceLove;
	}

	public void setDistanceLove(String distanceLove) {
	    this.distanceLove = distanceLove;
	}

	public String getOppositeSexType() {
	    return oppositeSexType;
	}

	public void setOppositeSexType(String oppositeSexType) {
	    this.oppositeSexType = oppositeSexType;
	}

	public String getPremartialSex() {
	    return premartialSex;
	}

	public void setPremartialSex(String premartialSex) {
	    this.premartialSex = premartialSex;
	}

	public String getLiveWithParents() {
	    return liveWithParents;
	}

	public void setLiveWithParents(String liveWithParents) {
	    this.liveWithParents = liveWithParents;
	}

	public String getWantBaby() {
	    return wantBaby;
	}

	public void setWantBaby(String wantBaby) {
	    this.wantBaby = wantBaby;
	}

	public String getMartialStatus() {
	    return martialStatus;
	}

	public void setMartialStatus(String martialStatus) {
	    this.martialStatus = martialStatus;
	}

	public String getFaceUrl() {
	    return faceUrl;
	}

	public String getInterests() {
	    return interests;
	}

	public void setInterests(String interests) {
	    this.interests = interests;
	}

	public String getPersonalities() {
	    return personalities;
	}

	public void setPersonalities(String personalities) {
	    this.personalities = personalities;
	}

	public int getBirthday() {
	    return birthday;
	}

	public void setBirthday(int birthday) {
	    this.birthday = birthday;
	}

	public int getFaceId() {
	    return faceId;
	}

	public void setFaceId(int faceId) {
	    this.faceId = faceId;
	}

	public String getDeviceId() {
	    return deviceId;
	}

	public void setDeviceId(String deviceId) {
	    this.deviceId = deviceId;
	}

	public String getAppId() {
	    return appId;
	}

	public void setAppId(String appId) {
	    this.appId = appId;
	}

	public String getWantHomeplace() {
	    return wantHomeplace;
	}

	public void setWantHomeplace(String wantHomeplace) {
	    this.wantHomeplace = wantHomeplace;
	}

	public String getWantAddress() {
	    return wantAddress;
	}

	public void setWantAddress(String wantAddress) {
	    this.wantAddress = wantAddress;
	}

	public String getWantEducation() {
	    return wantEducation;
	}

	public void setWantEducation(String wantEducation) {
	    this.wantEducation = wantEducation;
	}

	public String getWantMonthSalary() {
	    return wantMonthSalary;
	}

	public void setWantMonthSalary(String wantMonthSalary) {
	    this.wantMonthSalary = wantMonthSalary;
	}

	public String getWantAddtional() {
	    return wantAddtional;
	}

	public void setWantAddtional(String wantAddtional) {
	    this.wantAddtional = wantAddtional;
	}

	public String getWantAge() {
	    return wantAge;
	}

	public void setWantAge(String wantAge) {
	    this.wantAge = wantAge;
	}

	public String getWantHeight() {
	    return wantHeight;
	}

	public void setWantHeight(String wantHeight) {
	    this.wantHeight = wantHeight;
	}

	public String getOftenAddress() {
	    return oftenAddress;
	}

	public void setOftenAddress(String oftenAddress) {
	    this.oftenAddress = oftenAddress;
	}

	public String getInstruction() {
	    return instruction;
	}

	public void setInstruction(String instruction) {
	    this.instruction = instruction;
	}

	private String wantHomeplace, wantAddress, wantAge, wantHeight, wantEducation, wantMonthSalary, wantAddtional;

    }

}
