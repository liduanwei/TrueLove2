package me.himi.love.ui.fragment;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.Conversation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import me.himi.love.R;
import me.himi.love.adapter.ConversationsAdapter;
import me.himi.love.ui.fragment.base.BaseFragment;
import me.himi.love.view.list.XListView;
import me.himi.love.view.list.XListView.IXListViewListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @ClassName:UserRecommentFragment
 * @author sparklee liduanwei_911@163.com
 * @date Nov 2, 2014 10:52:08 PM
 */
public class ConversationFragment extends BaseFragment implements OnItemClickListener {

    View mContainerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	mContainerView = inflater.inflate(R.layout.fragment_conversation, container, false);
	init();
	return mContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
    }

    private XListView mListView;
    private ConversationsAdapter mAdapter;

    private int pageNumber = 0;

    private void init() {
	mListView = (me.himi.love.view.list.XListView) mContainerView.findViewById(R.id.listview_conversations);

	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mAdapter = new ConversationsAdapter(getActivity(), data);

	mListView.setAdapter(mAdapter);

	mListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position -= 1;
	    }

	});

	mListView.setPullRefreshEnable(true);
	mListView.setPullLoadEnable(true);

	mListView.setXListViewListener(new IXListViewListener() {

	    @Override
	    public void onRefresh() {
		// TODO Auto-generated method stub
		pageNumber = 1;
		//		try {
		loadConversations();
		//		} catch (Throwable th) {
		//		    th.printStackTrace();
		//		}
	    }

	    @Override
	    public void onLoadMore() {
		// TODO Auto-generated method stub
		loadConversations();
	    }
	});

	// 加载会话
	loadConversations();
	mListView.setOnItemClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

	super.onHiddenChanged(hidden);
    }

    private List<Conversation> data = new ArrayList<Conversation>();

    private Executor threadPool = Executors.newFixedThreadPool(3);

    // 加载到的数据
    private List<Conversation> conversations;

    /**
     * 
     */
    private void loadConversations() {

	final Handler handler = new Handler() {
	    @Override
	    public void dispatchMessage(Message msg) {
		switch (msg.what) {
		case 0:

		    if (pageNumber == 1) {
			mAdapter.getList().clear();
		    }
		    mAdapter.addAll(conversations);

		    mListView.stopRefresh();
		    mListView.stopLoadMore();

		    pageNumber++;
		    break;
		case 1:
		    mListView.stopRefresh();
		    mListView.stopLoadMore();
		    break;
		}
	    }
	};

	threadPool.execute(new Runnable() {

	    @Override
	    public void run() {
		// TODO Auto-generated method stub
		conversations = RongIM.getInstance().getConversationList();
		if (conversations != null && conversations.size() != 0) {
		    handler.sendEmptyMessage(0);
		} else {
		    handler.sendEmptyMessage(1);
		}
	    }
	});

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub

	Conversation conversation = mAdapter.getList().get(position - 1);

	RongIM.getInstance().startPrivateChat(getActivity(), conversation.getTargetId(), conversation.getSenderUserName());

    }

}
