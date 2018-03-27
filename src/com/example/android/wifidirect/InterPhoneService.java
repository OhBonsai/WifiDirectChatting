package com.example.android.wifidirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class InterPhoneService {

	
	private final String TAG= "interphoneservice";
	
	//传输数据的线程
	private DataTransferThread mDataTransferThread = null;
	//传输数据的端口
	private final int InterPhonePort = 1024;
	
	//Activity传输过来的handler
	private Handler mMsgHandler = null;
	
	//Activity传过来的Socket
	private Socket socket = null;
	
	//Activity传过来是不是owner
	private boolean isOwner = false;
	
	//返回去的Message类型
	public static final int MSG_CONTENT_READED = 0;
	public static final int MSG_CONTENT_WRITED = 1;
	
	private static InterPhoneService gInterPhoneService = null;
	
	public  static InterPhoneService getInterPhoneService() {
		if(null == gInterPhoneService)
		{
			gInterPhoneService = new InterPhoneService();
		}
		return gInterPhoneService;
	}
	
	public Handler getMsgHandler(){
		return mMsgHandler;
	}
	
	public void setMsgHandler(Handler mMsgHandler) {
		this.mMsgHandler = mMsgHandler;
		Log.d(TAG, "设置了HANDLER");
	}
	
	public void setSocket(Socket fuckSocket){
		this.socket = fuckSocket;
		if(this.socket!= null){
			Log.d(TAG, "socket传进来了");
		}else{
			Log.d(TAG, "干你娘，socket都是空的");
		}
		
	}
	
	public void setIsOwner(boolean isOwner){
		this.isOwner = isOwner;
		Log.d(TAG, "设置了owner");
		
	}
	
	public void beginTransferData(){
		
		if(null != socket){
			mDataTransferThread = new DataTransferThread(socket);
			mDataTransferThread.start();
			Log.d(TAG, "发送线程启动啦");
		}else{
			Log.d(TAG, "玩个妈蛋，socket都没传过来");
		}
	}
	
	public void stopTransferData(){
		if(null != mDataTransferThread)
		{
			mDataTransferThread.cancel();
		}
	}
	
	public void sendNotifyMessage(int messageType, Object obj)
	{
		Message msg = new Message();
		msg.what = messageType;
		msg.obj = obj;					
		mMsgHandler.sendMessage(msg);
	}
	
	public void sendMessageToRemote(String message)
	{
		if (null == mDataTransferThread || null == message || message.isEmpty())
		{
			return;
		}
		mDataTransferThread.write(message.getBytes());
	}
	
	public void sendDataToRemote(byte[] buffer, int offset, int count)
	{
		if (null == mDataTransferThread)
		{
			Log.d(TAG, "发送线程都没启动，玩个蛋啊");
			return;
		}
		mDataTransferThread.write(buffer, offset, count);
		Log.d(TAG, "线程启动，并进入write函数");
	}
	
	//数据传送的线程类
	private class DataTransferThread extends Thread{

		private Socket transferSocket = null;
		private OutputStream os = null;
		private InputStream is =null;	
		
		public DataTransferThread(Socket socket) {
			// TODO Auto-generated constructor stub
			this.transferSocket = socket;
			OutputStream ops = null;
			InputStream ips = null;
			
			try {
				ops = transferSocket.getOutputStream();
				ips = transferSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.os  = ops;
			this.is = ips;
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] buffer = new byte[4096];
			int readBytes = 0;
			
			while(true){
				
					try {
						readBytes = is.read(buffer);
						byte[] readBuffer = new byte[readBytes];
						System.arraycopy(buffer, 0, readBuffer, 0, readBytes);
						mMsgHandler.obtainMessage(MSG_CONTENT_READED,readBytes
								,-1,readBuffer).sendToTarget();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		}
		
		public void write(byte[] buffer, int offset, int count) {
			// TODO Auto-generated method stub
			try {
				os.write(buffer, offset, count);
				Log.d(TAG,"输出流写了buffer内容了");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void write(final byte[] buffer) {
			// TODO Auto-generated method stub
			try {
				os.write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendNotifyMessage(MSG_CONTENT_WRITED, null);
		}

		public void cancel() {
			// TODO Auto-generated method stub
			try {
				transferSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
