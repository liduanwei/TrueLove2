package me.himi.love.view;

import me.himi.love.R;
import me.himi.love.view.city.CityPicker;
import me.himi.love.view.city.CityPicker.OnSelectingListener;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupWindow;

public class SelectProvinceCityPopupWindow extends PopupWindow implements OnClickListener {

    CityPicker cityPicker;
    private OnSubmitListener onSubmitListener;

    public SelectProvinceCityPopupWindow(Activity context, String date) {
	super(context);

	View v = LayoutInflater.from(context).inflate(R.layout.select_province_city, null);
	setContentView(v);

	final EditText etInput = (EditText) v.findViewById(R.id.et_input_city);

	cityPicker = (CityPicker) v.findViewById(R.id.citypicker);

	cityPicker.setOnSelectingListener(new OnSelectingListener() {

	    @Override
	    public void selected(boolean selected) {
		// TODO Auto-generated method stub
		etInput.setText(cityPicker.getSelectedCity());
	    }
	});

	v.findViewById(R.id.tv_submit).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		dismiss();
		if (null != onSubmitListener) {
		    String city = "";
		    if (!TextUtils.isEmpty(etInput.getText())) {
			city = etInput.getText().toString();
		    } else {
			city = cityPicker.getSelectedCity();
		    }
		    onSubmitListener.onSubmit(city);
		}
	    }
	});

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
	super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void onClick(View v) {
	this.dismiss();

    }

    public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
	this.onSubmitListener = onSubmitListener;
    }

    public interface OnSubmitListener {
	void onSubmit(String selectedCity);
    }

}
