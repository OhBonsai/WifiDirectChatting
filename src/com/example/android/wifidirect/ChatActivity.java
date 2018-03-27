package com.example.android.wifidirect;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity implements OnClickListener,
         Handler.Callback,MessageTarget{

	
	public static final String TAG = "wifidirect";
	
	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatManager chatManager;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	
	
	public static final String GROUP_OWNER_ADDRESS  = "host_address";
	public static final String GROUP_OWNER_PORT = "host_port";
	public static final String IS_GROUP_OWNER = "is_owner";
	public static final int MESSAGE_READ = 0x400 + 1;
	public static final int MY_HANDLE = 0x400 + 2;
	

	private Handler handler = new Handler(this);
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_xiaohei);
		//启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        
        //初始化界面，设置适配器，增加按钮监听
        initView();
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		
		//初始化数据，为以后增加聊天记录 扩展方法
		initData();
		
		//启动消息处理器
		initSocketHandler();
	};
	
	
	//初始化界面
	private void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.listview);
    	mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	mBtnSend.setOnClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;
		}
	}
	
	private void send() {
		// TODO Auto-generated method stub
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			//给自己更新界面
			ChatMsgEntity entityToMe = new ChatMsgEntity();
			entityToMe.setDate(getDate());
			entityToMe.setName("我");
			entityToMe.setMsgType(false);
			entityToMe.setText(contString);	
			mEditTextContent.setText("");
			pushMessage(entityToMe);
			
			//利用chatManager给外部发送消息
			ChatMsgEntity entityToYou  = new ChatMsgEntity();
			entityToYou.setDate(getDate());
			entityToYou.setName("他");
			entityToYou.setMsgType(true);
			entityToYou.setText(contString);
			if(chatManager != null){
				chatManager.write(entityToYou);	
			}
		}
	}

	
	//跟新自己消息的方法
	public void pushMessage(ChatMsgEntity readMessage) {
		mDataArrays.add(readMessage);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
	}
	
	
	private String getDate() {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));      
        
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins); 
        								
        return sbBuffer.toString();
	}

	/*
	 * 我是淫荡的分割线******************************************************************************************************
	 */
	private void initData() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * 我是淫荡的分割线******************************************************************************************************
	 */
	
	private void initSocketHandler() {
		// TODO Auto-generated method stub
		Thread handlerSocket = null;
		//从Intent里面获取Owner信息
		Intent intent = getIntent();
		String host = intent.getExtras().getString(GROUP_OWNER_ADDRESS);
		int port = intent.getExtras().getInt(GROUP_OWNER_PORT);
		Boolean isOwner = intent.getExtras().getBoolean(IS_GROUP_OWNER);
		
		//判断是否为Owner，如果是，启动owner handler;不是，启动client handler
		if (isOwner) {
			 try {
				Log.d(TAG, "Connected as group owner");
				    handlerSocket = new OwnerSocketHandler(
//					        ((MessageTarget) this).getHandler());
				    		handler);
				    Log.d(TAG, "主线程handler: " + handler);

				    Log.d(TAG, "步骤一");
					handlerSocket.start();
					Log.d(TAG, "成功启动服务器线程");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			 Log.d(TAG, "Connected as peer");
	            handlerSocket = new ClientSocketHandler(
	            		((MessageTarget) this).getHandler(),host,port );
	            Log.d(TAG, "主线程: ((MessageTarget) this).getHandler()" + handler);
//	                    handler,host,port );
	            handlerSocket.start();
	         Log.d(TAG, "成功启动客户端线程");
		}
	}

	/*
	 * 我是淫荡的分割线******************************************************************************************************
	 * 消息处理，如果收到handlerMessage进行判断
	 * 最开始会给chatManager赋值，然后将消息更新
	 * 
	 * 这里面将UI线程的handler，交给其他线程来发消息。具体机制
	 * 可见testHandler这个工程。
	 */
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MESSAGE_READ:
			Log.d(TAG,"你妹啊");
			ChatMsgEntity entity = (ChatMsgEntity)msg.obj;
            Log.d(TAG, entity.getText());
            pushMessage(entity);
			break;
		case MY_HANDLE:
			Object obj = msg.obj;
			setChatManager((ChatManager)obj);
		case 100:
			Log.d(TAG, "我能打印你妈逼");
		default:
			break;
		}
		return true;
	}

	@Override
	public Handler getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}

	/*
	 * 我是淫荡的分割线******************************************************************************************************
	 */
		
	public void setHandler(Handler handler) {
		this.handler = handler;
	}	
	
	public void setChatManager(ChatManager obj) {
		 chatManager = obj;
	}

}

 interface MessageTarget{
	public Handler getHandler();
}
