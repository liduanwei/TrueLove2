package me.himi.love.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import me.himi.love.R;
import me.himi.love.adapter.SystemMessageAdapter;
import me.himi.love.entity.SystemMessage;
import me.himi.love.entity.SystemMessage.MessageType;
import me.himi.love.ui.fragment.base.BaseFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SystemMessagesFragment extends BaseFragment {
    private static final String TAG = "TestFragment";

    private me.himi.love.view.list.XListView mListView;

    private SystemMessageAdapter mAdapter;

    private List<SystemMessage> msges = new ArrayList<SystemMessage>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.d(TAG, "TestFragment-----onCreate");
//	Bundle args = getArguments();
	
	
	

	// 放在只创建一次的地方避免重复添加数据
	//	for (int i = 0; i < 1; ++i) {
	//	    data.add("暂无系统消息");
	//	}

	SystemMessage msg = new SystemMessage();
	msg.setTime("");
	msg.setIcon("");
	msg.setTitle("会员通知");
	msg.setContent("你已经是会员了,可以尽情享受本应用的所有尊贵特权");
	msg.setLinkUrl("http://himi.me");
	msg.setReaded(false);
	msg.setType(MessageType.NOTIFICATION); // 通知消息
	msges.add(msg);

	msg = new SystemMessage();
	msg.setTime("");
	msg.setIcon("");
	msg.setTitle("会员通知");
	msg.setContent("你已经是会员了,可以尽情享受本应用的所有尊贵特权");
	msg.setLinkUrl("http://love.leavtechintl.com/index.php/index/login/index");
	msg.setReaded(false);
	msg.setType(MessageType.NOTIFICATION); // 通知消息
	msges.add(msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "TestFragment-----onCreateView");
	View view = inflater.inflate(R.layout.fragment_sytem_message, container, false);
	init(view);
	return view;

    }

    private void init(View v) {
	mListView = (me.himi.love.view.list.XListView) v.findViewById(R.id.listview_system);
	mListView.setPullRefreshEnable(false);
	mListView.setPullLoadEnable(false);

	mAdapter = new SystemMessageAdapter(this.getActivity(), msges);

	mListView.setAdapter(mAdapter);

	mListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position -= 1;
		Intent intent = new Intent(getActivity(), me.himi.love.ui.MyReceivedSystemMessageActivity.class);

		SystemMessage msg = msges.get(position);
		Bundle extras = new Bundle();
		extras.putSerializable("msg", msg);
		intent.putExtras(extras);
		getActivity().startActivity(intent);
	    }

	});
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.d(TAG, "TestFragment-----onDestroy");
    }

}
