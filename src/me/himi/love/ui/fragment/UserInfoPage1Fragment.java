package me.himi.love.ui.fragment;

import me.himi.love.R;
import me.himi.love.entity.DetailInfoUser;
import me.himi.love.ui.fragment.base.BaseFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class UserInfoPage1Fragment extends BaseFragment implements OnClickListener {

    View mContainerView;

    TextView tvUserId, tvAge, tvHeight, tvWeight, tvDistance, tvLoveMoney;
    TextView tvMaritalStatus, tvHomeplace, tvAddress, tvBirthday;

    TextView tvCharmBody, tvHouse, tvDistanceLove, tvOppositeSexType, tvPremartialSex, tvLiveWithParents, tvWantBaby, tvInterests, tvPersonlity;

    TextView tvEmployment;// 职业
    TextView tvUserEdu;// 学历
    TextView tvMonthSalary;//月薪
    TextView tvConstellation;

    TextView tvOftenAddress, tvInstruction;// 常出没地,个人说明

    TextView tvMonologue;
    ImageView ivUserGender;

    TextView tvWantedHomeplace, tvWantedAddress, tvWantedAge, tvWantedHeight, tvWantedEducation, tvWantedSalary, tvWantedExtends;

    TextView tvUserVip;// 用户VI状态P
    ImageView ivVipTop;// vip皇冠

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	mContainerView = inflater.inflate(R.layout.userinfo_page1, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
    }

    private void init() {

	// 独白
	tvMonologue = (TextView) mContainerView.findViewById(R.id.tv_monologue);

	// 征友要求
	tvWantedHomeplace = (TextView) mContainerView.findViewById(R.id.tv_wanted_homeplace);
	tvWantedAddress = (TextView) mContainerView.findViewById(R.id.tv_wanted_address);
	tvWantedAge = (TextView) mContainerView.findViewById(R.id.tv_wanted_age);
	tvWantedHeight = (TextView) mContainerView.findViewById(R.id.tv_wanted_height);
	tvWantedEducation = (TextView) mContainerView.findViewById(R.id.tv_wanted_education);
	tvWantedSalary = (TextView) mContainerView.findViewById(R.id.tv_wanted_monthly_salary);
	tvWantedExtends = (TextView) mContainerView.findViewById(R.id.tv_wanted_extends);

	// 用户vip
	tvUserVip = (TextView) mContainerView.findViewById(R.id.tv_user_vip);
	// vip
	ivVipTop = (ImageView) getActivity().findViewById(R.id.iv_user_face_top);

	tvWantedHomeplace.setText("湖北");
	tvWantedAddress.setText("不限");
	tvWantedAge.setText("20-26岁");
	tvWantedHeight.setText("不限");
	tvWantedEducation.setText("不限");
	tvWantedSalary.setText("不限");

	//tvWantedExtends.setText("只要真心相爱,能相濡以沫,待华发满头之时仍能陪我看每一个日出日落这就够了");

	// 基本资料
	// 用户ID
	tvUserId = (TextView) mContainerView.findViewById(R.id.tv_user_id);

	// 年龄
	tvAge = (TextView) mContainerView.findViewById(R.id.tv_user_age_gender);

	// 虚拟币
	tvLoveMoney = (TextView) mContainerView.findViewById(R.id.tv_user_love_money);
	tvLoveMoney.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		net.youmi.android.offers.OffersManager.getInstance(getActivity()).showOffersWallDialog(getActivity());
	    }
	});

	// 身高
	tvHeight = (TextView) mContainerView.findViewById(R.id.tv_user_height);

	// 体重
	tvWeight = (TextView) mContainerView.findViewById(R.id.tv_user_weight);

	// 距离
	tvDistance = (TextView) mContainerView.findViewById(R.id.tv_user_distance);

	// 生日
	tvBirthday = (TextView) mContainerView.findViewById(R.id.tv_user_birthday);

	// 职业
	tvEmployment = (TextView) mContainerView.findViewById(R.id.tv_user_employment);

	// 学历
	tvUserEdu = (TextView) mContainerView.findViewById(R.id.tv_user_edu);

	//月薪
	tvMonthSalary = (TextView) mContainerView.findViewById(R.id.tv_monthly_salary);

	// 常出没地
	tvOftenAddress = (TextView) mContainerView.findViewById(R.id.tv_often_address);

	// 个人说明
	tvInstruction = (TextView) mContainerView.findViewById(R.id.tv_personal_instruction);

	// 情感状况
	tvMaritalStatus = (TextView) mContainerView.findViewById(R.id.tv_user_marital_status);

	// 家乡
	tvHomeplace = (TextView) mContainerView.findViewById(R.id.tv_user_homeplace);

	// 现居地
	tvAddress = (TextView) mContainerView.findViewById(R.id.tv_user_address);

	// 星座
	tvConstellation = (TextView) mContainerView.findViewById(R.id.tv_user_constellation);

	tvCharmBody = (TextView) mContainerView.findViewById(R.id.tv_user_charm_body);
	tvHouse = (TextView) mContainerView.findViewById(R.id.tv_user_house);
	tvDistanceLove = (TextView) mContainerView.findViewById(R.id.tv_user_distance_love);
	tvOppositeSexType = (TextView) mContainerView.findViewById(R.id.tv_user_opposite_sex_type);
	tvPremartialSex = (TextView) mContainerView.findViewById(R.id.tv_user_premartial_sex);
	tvLiveWithParents = (TextView) mContainerView.findViewById(R.id.tv_user_live_with_parents);
	tvWantBaby = (TextView) mContainerView.findViewById(R.id.tv_user_want_baby);
	tvInterests = (TextView) mContainerView.findViewById(R.id.tv_user_interests);
	tvPersonlity = (TextView) mContainerView.findViewById(R.id.tv_user_personality);

	// 动态设置 颜色
	SharedPreferences sp = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
	int color = sp.getInt("back_color", getResources().getColor(R.color.bar));
	//line left 
	mContainerView.findViewById(R.id.view_monologue_left).setBackgroundColor(color);
	mContainerView.findViewById(R.id.view_line2_left).setBackgroundColor(color);
	mContainerView.findViewById(R.id.view_line3_left).setBackgroundColor(color);
    }

    public void setInfos(DetailInfoUser user) {
	// vip v图标
	tvUserVip.setBackgroundResource(user.isVip() ? R.drawable.vip_sign : R.drawable.vip_sign_not);
	// 皇冠
	ivVipTop.setVisibility(user.isVip() ? View.VISIBLE : View.GONE);

	int backRes = 0;
	Drawable genderDrawable = null;
	if (user.getGender() == 1) {
	    backRes = R.drawable.shape_gender_age_male;
	    genderDrawable = getResources().getDrawable(R.drawable.ic_user_male2);
	} else {
	    backRes = R.drawable.shape_gender_age_female;
	    genderDrawable = getResources().getDrawable(R.drawable.ic_user_famale2);
	}
	tvAge.setBackgroundResource(backRes);
	// 必须设置 bounds, 否则不显示
	genderDrawable.setBounds(0, 0, genderDrawable.getMinimumWidth(), genderDrawable.getMinimumHeight());
	tvAge.setCompoundDrawables(genderDrawable, null, null, null);
	tvAge.setText(user.getAge() + "");

	//	Spanned ss = Html.fromHtml("<u style='color:red'>" + user.getLoveMoney() + "</u>币");
	tvLoveMoney.setText(user.getLoveMoney() + "币");

	tvUserId.setText(user.getUserId() + "");

	tvHeight.setText(user.getHeight() + "cm");
	tvWeight.setText(user.getWeight() + "kg");

	tvDistance.setText(user.getDistance() + "km");

	tvHomeplace.setText(user.getHomeplace());
	tvAddress.setText(user.getAddress());
	tvMaritalStatus.setText(user.getMartialStatus());

	tvMonologue.setText(user.getMonologue());

	String year = user.getBirthdayYear() + "";
	String month = user.getBirthdayMonth() <= 9 ? "0" + user.getBirthdayMonth() : user.getBirthdayMonth() + "";
	String day = user.getBirthdayDay() <= 9 ? "0" + user.getBirthdayDay() : user.getBirthdayDay() + "";
	tvBirthday.setText(year + "-" + month + "-" + day);

	tvMonthSalary.setText(user.getMonthlySalary());
	tvCharmBody.setText(user.getCharmBody());
	tvEmployment.setText(user.getEmployment());
	tvUserEdu.setText(user.getEducation());// 学历
	tvHouse.setText(user.getHouse());
	tvDistanceLove.setText(user.getDistanceLove());
	tvOppositeSexType.setText(user.getOppositeSexType());
	tvPremartialSex.setText(user.getPremartialSex());
	tvLiveWithParents.setText(user.getLiveWithParents());
	tvWantBaby.setText(user.getWantBaby());
	tvInterests.setText(user.getInterests());
	tvPersonlity.setText(user.getPersonalities());

	tvConstellation.setText(user.getConstellation() + "座");

	tvInstruction.setText(user.getInstruction());

	tvOftenAddress.setText(user.getOftenAddress());

	tvWantedHomeplace.setText(user.getWantHomeplace());
	tvWantedAddress.setText(user.getWantAddress());
	tvWantedAge.setText(user.getWantAge());
	tvWantedHeight.setText(user.getWantHeight());
	tvWantedEducation.setText(user.getWantEducation());
	tvWantedSalary.setText(user.getWantMonthSalary());
	tvWantedExtends.setText(user.getWantAddtional());

    }

    public int getViewHeight() {
	return this.mContainerView.getHeight();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

	super.onHiddenChanged(hidden);
    }

    @Override
    public void onClick(View v) {

    }
}
