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
		//����activityʱ���Զ����������
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        
        //��ʼ�����棬���������������Ӱ�ť����
        initView();
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		
		//��ʼ�����ݣ�Ϊ�Ժ����������¼ ��չ����
		initData();
		
		//������Ϣ������
		initSocketHandler();
	};
	
	
	//��ʼ������
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
			//���Լ����½���
			ChatMsgEntity entityToMe = new ChatMsgEntity();
			entityToMe.setDate(getDate());
			entityToMe.setName("��");
			entityToMe.setMsgType(false);
			entityToMe.setText(contString);	
			mEditTextContent.setText("");
			pushMessage(entityToMe);
			
			//����chatManager���ⲿ������Ϣ
			ChatMsgEntity entityToYou  = new ChatMsgEntity();
			entityToYou.setDate(getDate());
			entityToYou.setName("��");
			entityToYou.setMsgType(true);
			entityToYou.setText(contString);
			if(chatManager != null){
				chatManager.write(entityToYou);	
			}
		}
	}

	
	//�����Լ���Ϣ�ķ���
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
	 * ���������ķָ���******************************************************************************************************
	 */
	private void initData() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * ���������ķָ���******************************************************************************************************
	 */
	
	private void initSocketHandler() {
		// TODO Auto-generated method stub
		Thread handlerSocket = null;
		//��Intent�����ȡOwner��Ϣ
		Intent intent = getIntent();
		String host = intent.getExtras().getString(GROUP_OWNER_ADDRESS);
		int port = intent.getExtras().getInt(GROUP_OWNER_PORT);
		Boolean isOwner = intent.getExtras().getBoolean(IS_GROUP_OWNER);
		
		//�ж��Ƿ�ΪOwner������ǣ�����owner handler;���ǣ�����client handler
		if (isOwner) {
			 try {
				Log.d(TAG, "Connected as group owner");
				    handlerSocket = new OwnerSocketHandler(
//					        ((MessageTarget) this).getHandler());
				    		handler);
				    Log.d(TAG, "���߳�handler: " + handler);

				    Log.d(TAG, "����һ");
					handlerSocket.start();
					Log.d(TAG, "�ɹ������������߳�");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			 Log.d(TAG, "Connected as peer");
	            handlerSocket = new ClientSocketHandler(
	            		((MessageTarget) this).getHandler(),host,port );
	            Log.d(TAG, "���߳�: ((MessageTarget) this).getHandler()" + handler);
//	                    handler,host,port );
	            handlerSocket.start();
	         Log.d(TAG, "�ɹ������ͻ����߳�");
		}
	}

	/*
	 * ���������ķָ���******************************************************************************************************
	 * ��Ϣ��������յ�handlerMessage�����ж�
	 * �ʼ���chatManager��ֵ��Ȼ����Ϣ����
	 * 
	 * �����潫UI�̵߳�handler�����������߳�������Ϣ���������
	 * �ɼ�testHandler������̡�
	 */
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case MESSAGE_READ:
			Log.d(TAG,"���ð�");
			ChatMsgEntity entity = (ChatMsgEntity)msg.obj;
            Log.d(TAG, entity.getText());
            pushMessage(entity);
			break;
		case MY_HANDLE:
			Object obj = msg.obj;
			setChatManager((ChatManager)obj);
		case 100:
			Log.d(TAG, "���ܴ�ӡ�����");
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
	 * ���������ķָ���******************************************************************************************************
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
